package com.lee.leeaitripagent.app;


import com.lee.leeaitripagent.advisor.MyLoggerAdvisor;
import com.lee.leeaitripagent.advisor.ReReadingAdvisor;
import com.lee.leeaitripagent.chatmemory.FileBasedChatMemory;
//import com.lee.leeaitripagent.rag.QueryRewritten;
//import com.lee.leeaitripagent.rag.TripAppRagCustomAdvisorFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Slf4j
public class AiTripApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕文旅行业的专业旅行策划师。开场主动说明身份，邀请用户说出旅行规划相关问题与需求。" +
            "按照休闲短途游、城市深度游、户外长线游三类方向沟通：短途游询问周边目的地、游玩时长与娱乐需求；城市深度游询问打卡偏好、美食探店及交通安排问题；户外长线游询问路线规划、装备准备与旅途食宿困扰。" +
            "引导用户详细说明出行时间、出行人数、预算范围、兴趣偏好以及特殊要求，为用户定制专属出行方案。";

    public AiTripApp(ChatModel deepSeekChatModel) {
        // 初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);

        // 初始化基于内存的对话记忆
        //MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                //.chatMemoryRepository(new InMemoryChatMemoryRepository())
                //.maxMessages(10)
                //.build();
        this.chatClient = ChatClient.builder(deepSeekChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        // 自定义日志 Advisor，可按需开启
                        new MyLoggerAdvisor()
                        // 自定义推理增强 Advisor，可按需开启
                        //, new ReReadingAdvisor()
                )
                .build();
    }
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec-> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = null;
        if (chatResponse != null) {
            content = chatResponse.getResult().getOutput().getText();
        }
        log.info("Chat Response: {}", content);
        return content;
    }

    // 定义旅行规划报告类，使用 Java 14 引入的 record 特性快速定义
    record TripReport(String title, List<String> suggestions) {

    }
    public TripReport doChatWithReport(String message, String chatId) {
        TripReport tripReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成旅行规划结果，标题为{用户名}的旅行规划报告，内容为建议列表")
                .user(message)
                .advisors(spec-> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(TripReport.class);
        log.info("Trip Report: {}", tripReport);
        return tripReport;
    }

//    // AI Trip 知识库问答功能
//    @Resource
//    private VectorStore tripAppVectorStore;
//
//    @Resource
//    private Advisor tripAppRagCloudAdvisor;
//
//    @Resource
//    private VectorStore pgVectorVectorStore;
//
//    @Resource
//    private QueryRewritten queryRewritten;
//
//    public String doChatWithRag(String message, String chatId) {
//        String rewrittenMessage = queryRewritten.doQueryRewrite(message);
//        ChatResponse chatResponse = chatClient
//                .prompt()
//                // 使用 QueryRewritten 改写后的查询
//                .user(rewrittenMessage)
//                //.user(message)
//                .advisors(spec-> spec.param(ChatMemory.CONVERSATION_ID, chatId))
//                .advisors(new MyLoggerAdvisor())
//                // 应用 RAG 知识库问答
//                //.advisors(QuestionAnswerAdvisor.builder(tripAppVectorStore).build())
//                // 应用 RAG 检索增强服务（基于云知识库服务）
//                //.advisors(tripAppRagCloudAdvisor)
//                //.advisors(TripAppRagCustomAdvisorFactory.createTripAppRagCustomAdvisor(tripAppVectorStore, "休闲短途游"))
//                // 应用 RAG 检索增强服务（基于 PgVector 向量存储）
//                //.advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build())
//                .call()
//                .chatResponse();
//        String content = null;
//        if (chatResponse != null) {
//            content = chatResponse.getResult().getOutput().getText();
//        }
//        log.info("Chat Response: {}", content);
//        return content;
//    }

    // AI Trip 知识库调用工具能力
    @Resource
    private ToolCallback[] allTools;

    public String doChatWithTools(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec-> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        String content = null;
        if (chatResponse != null) {
            content = chatResponse.getResult().getOutput().getText();
        }
        log.info("Chat Response: {}", content);
        return content;

    }

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    public String doChatWithMcp(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec-> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = null;
        if (chatResponse != null) {
            content = chatResponse.getResult().getOutput().getText();
        }
        log.info("Chat Response: {}", content);
        return content;
    }

    /**
     * AI 基础对话（支持多轮对话记忆，SSE 流式传输）
     * @param message 用户消息
     * @param chatId 聊天室 Id
     * @return AI 回复内容
     */
    public Flux<String> doChatWithStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }
}
