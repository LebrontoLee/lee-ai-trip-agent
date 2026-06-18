package com.lee.leeaitripagent.controller;

import com.lee.leeaitripagent.agent.LeeManus;
import com.lee.leeaitripagent.app.AiTripApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Resource
    private AiTripApp aiTripApp;

    @Resource
    private ToolCallback[] alltools;

    @Resource
    private ChatModel deepSeekChatModel;


    /*
     * 同步调用 Ai Trip 应用
     */
    @GetMapping(value = "/ai_trip_app/chat/sync")
    public String doChatWithAiTripAppSync(String message, String chatId)
    {
        return aiTripApp.doChat(message, chatId);
    }

    /*
     * SSE 流式调用 Ai Trip 应用
     */
    @GetMapping(value = "/ai_trip_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithAiTripAppSse(String message, String chatId)
    {
        return aiTripApp.doChatWithStream(message, chatId);
    }

    /*
     * SSE 流式调用 Ai Trip 应用
     */
    @GetMapping(value = "/ai_trip_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithAiTripAppServerSentEvent(String message, String chatId)
    {
        return aiTripApp.doChatWithStream(message, chatId)
                .map(chunk-> ServerSentEvent.<String>builder().data(chunk).build());
    }
    /*
     * SSE 流式调用 Ai Trip 应用
     */
    @GetMapping(value = "/ai_trip_app/chat/sse_emitter")
    public SseEmitter dochatWithAiTripAppSseEmitter(String message, String chatId)
    {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(180000L);
        // 获取 Flux 响应式数据流并且直接通过订阅推送给 SseEmitter
        aiTripApp.doChatWithStream(message, chatId)
                .subscribe( chunk-> {
                    try{
                        sseEmitter.send(chunk);
                    } catch (Exception e){
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        return sseEmitter;
    }

    /**
     * 流式调用 LeeManus 超级智能体
     * @param message
     * @return AI 回复内容
     */
    @GetMapping(value = "/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        LeeManus leeManus = new LeeManus(alltools, deepSeekChatModel);
        return leeManus.runStream(message);
    }
}
