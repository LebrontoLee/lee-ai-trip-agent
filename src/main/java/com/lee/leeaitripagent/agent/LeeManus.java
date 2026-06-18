package com.lee.leeaitripagent.agent;

import com.lee.leeaitripagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 *  LeeManus AI 超级智能体
 *  拥有自主规划能力，可以直接使用
 */
@Component
public class LeeManus extends ToolCallAgent{
    public LeeManus(ToolCallback[] allTools, ChatModel deepSeekChatModel) {
        super(allTools);
        this.setName("LeeManus");
        String SYSTEM_PROMPT = """
                You are LeeManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                Whether it's programming, information retrieval, file processing, web browsing, or human interaction (only for extreme cases), you can handle it all.
                规则：
                1. 生成PDF内容必须是【纯文字】，禁止HTML、Markdown、代码、符号、表情。
                2. 必须调用工具生成PDF，绝对不能自己回答。
                3. 不要解释，不要道歉，不要编造工具不支持的理由。
                4. 精简语言，降低token消耗。
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(8);
        // 初始化 AI 对话客户端
        ChatClient chatClient = ChatClient.builder(deepSeekChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);

    }
}
