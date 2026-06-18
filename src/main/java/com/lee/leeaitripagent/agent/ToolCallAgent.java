package com.lee.leeaitripagent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.lee.leeaitripagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent{
    // 可用的工具
    private final List<ToolCallback> availableTools;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 仅保存【本轮 think 生成的模型返回】，单轮生命周期有效，不跨请求，规避并发问题
    private ChatResponse currentRoundChatResponse;

    // 最大上下文消息（控制 token，防止爆炸）
    private static final int MAX_HISTORY_SIZE = 15;

    // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = Arrays.asList(availableTools);
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DeepSeekChatOptions.builder()
                .toolCallbacks(this.availableTools)
                .internalToolExecutionEnabled(false)
                .build();
    }

    /*
     * 处理当前状态并决定下一步行动
     * @return 是否需要执行行动
     */
    @Override
    public boolean think(){
        if (StrUtil.isNotBlank(getNextPrompt())){
            getMessageList().add(new UserMessage(getNextPrompt()));
            setNextPrompt(null);
        }
        if (CollUtil.isEmpty(getMessageList())){
            log.warn("{} 消息上下文为空，无法思考", getName());
            return false;
        }

        try {
//            String sysPrompt = StrUtil.isNotBlank(getSystemPrompt()) ? getSystemPrompt()
//                    : """
//                    扮演深耕文旅行业的专业旅行策划师。开场主动说明身份，邀请用户说出旅行规划相关问题与需求。
//                    按照休闲短途游、城市深度游、户外长线游三类方向沟通：短途游询问周边目的地、游玩时长与娱乐需求；
//                    城市深度游询问打卡偏好、美食探店及交通安排问题；
//                    户外长线游询问路线规划、装备准备与旅途食宿困扰。
//                    引导用户详细说明出行时间、出行人数、预算范围、兴趣偏好以及特殊要求，为用户定制专属出行方案。""";

            String sysPrompt = StrUtil.isNotBlank(getSystemPrompt()) ? getSystemPrompt()
                    : """
                You are LeeManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                Whether it's programming, information retrieval, file processing, web browsing, or human interaction (only for extreme cases), you can handle it all.
                规则：
                1. 生成PDF内容必须是【纯文字】，禁止HTML、Markdown、代码、符号、表情。
                2. 必须调用工具生成PDF，绝对不能自己回答。
                3. 不要解释，不要道歉，不要编造工具不支持的理由。
                4. 精简语言，降低token消耗。
                """;
            // 核心：不自己传 messageList，让 Spring AI 自动处理 tool_calls/ tool 配对
            ChatResponse chatResponse = getChatClient().prompt()
                    .system(sysPrompt)
                    .messages(getMessageList())
                    .options(chatOptions)
                    .call()
                    .chatResponse();
            this.currentRoundChatResponse = chatResponse;
            AssistantMessage  assistantMessage = chatResponse.getResult().getOutput();

            // 只添加正常回复，有工具调用由 Spring AI 内部管理上下文
            if (CollUtil.isEmpty(assistantMessage.getToolCalls())){
                getMessageList().add(assistantMessage);
            }

            boolean hasToolCalls = CollUtil.isNotEmpty(assistantMessage.getToolCalls());
            log.info("{} 思考完成，{} 执行工具", getName(), hasToolCalls ? "需要" : "不需要");
            return hasToolCalls;
        } catch (Exception e){
            log.error("{} 思考异常，已清空上下文防止token爆炸", getName(), e);
            // 异常直接清空上下文，防止雪球越滚越大
            getMessageList().clear();
            this.currentRoundChatResponse = null;
            return false;
        }
    }
    /*
     * 执行工具调用并处理结果
     * @return 执行结果
     */
    @Override
    public String act()
    {
        if (this.currentRoundChatResponse == null){
            return "当前无可用的模型响应";
        }
        if(!this.currentRoundChatResponse.hasToolCalls()){
            return "无工具需要调用";
        }
        try{
            Prompt prompt = new Prompt(getMessageList(), chatOptions);
            ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, currentRoundChatResponse);
            List<Message> toolResponseMessages = toolExecutionResult.conversationHistory();
            if(CollUtil.isEmpty(toolResponseMessages)){
                return "工具执行完成，无返回结果";
            }
            getMessageList().addAll(toolResponseMessages);

            // 日志输出工具结果
            Message lastMsg = CollUtil.getLast(toolResponseMessages);
            // 判断是否是工具消息
            if(!(lastMsg instanceof ToolResponseMessage toolRespMsg)){
                return "工具返回消息格式不正确";
            }
            // 判断是否调用了终止工具
            boolean terminateToolCalled = toolRespMsg.getResponses().stream()
                    .anyMatch(r -> r.name().equals("doTerminate"));
            if(terminateToolCalled){
                // 任务结束，更改状态
                setState(AgentState.FINISHED);
                return "任务已完成，Agent 已终止（通过 doTerminate 工具结束）";
            }
            String resultStr = toolRespMsg.getResponses().stream()
                    .map(r -> "【工具结果】" + r.name() + " => " + r.responseData())
                    .collect(Collectors.joining("\n"));
            log.info("{} 工具执行完成：\n{}", getName(), resultStr);
            return resultStr;
        } catch (Exception e) {
            log.error("{} 工具执行阶段异常", getName(), e);
            return "工具执行失败：" + e.getMessage();
        } finally {
            this.currentRoundChatResponse = null;
        }
    }

}
