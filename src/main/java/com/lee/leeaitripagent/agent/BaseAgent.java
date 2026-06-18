package com.lee.leeaitripagent.agent;


import cn.hutool.core.util.StrUtil;
import com.lee.leeaitripagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程
 * 提供状态转换、内容管理和基于步骤的执行循环的基本功能
 * 子类必须实现 step 方法（执行单步操作）
 */
@Data
@Slf4j
public abstract class BaseAgent {
    // 核心属性
    private String name;

    // 提示词
    private String systemPrompt;
    private String nextPrompt;

    // 代理状态
    private volatile AgentState state = AgentState.IDLE;

    // 执行步骤控制
    private volatile int currentStep = 0;
    private int maxSteps = 10;

    // LLM 大模型
    private ChatClient chatClient;

    // Memory 记忆（需要自主维护会话上下文）
    private List<Message> messageList = new CopyOnWriteArrayList<>();

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt){
        // 1、基础校验
        if (state != AgentState.IDLE) {
            throw new RuntimeException("Agent 正在运行中，当前状态："+ state);
        }
        if (StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("用户提示词不能为空");
        }
        // 统一交给 nextPrompt，不直接 add，避免重复添加
        setNextPrompt(userPrompt);

        // 2、执行，更改状态
        state = AgentState.RUNNING;
        // 保存结果列表
        StringBuilder finalResult = new StringBuilder();
        try{
            for (int i = 0; i < maxSteps && state == AgentState.RUNNING; i++){
                currentStep = i + 1;
                log.info("===== 执行步骤 {}/{} =====", currentStep, maxSteps);
                String stepResult = step();
                finalResult.append("Step").append(currentStep).append(":").append(stepResult).append("\n");
            }
            if(currentStep >= maxSteps){
                finalResult.append("已达到最大步骤数，自动终止");
            }
            state = AgentState.FINISHED;
            return finalResult.toString();
        } catch (Exception e){
            state = AgentState.ERROR;
            log.error("Agent 执行异常", e);
            return "执行错误" + e.getMessage();
        } finally{
            // 3、清理资源
            this.cleanup();
        }
    }
    /**
     * 运行代理（流式输出）
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public SseEmitter runStream(String userPrompt){
        // 主线程先做校验，拦截并发调用
        if (state != AgentState.IDLE){
            SseEmitter emitter = new SseEmitter(300000L);
            try{
                emitter.send("错误：无法从状态运行代理："+this.state);
            } catch (Exception e){
                log.error("SSE 发送错误提示失败", e);
            }
            emitter.complete();
            return emitter;
        }
        if (StrUtil.isBlank(userPrompt)) {
            SseEmitter emitter = new SseEmitter(300000L);
            try{
                emitter.send("错误，不能使用空提示词运行代理");
            } catch (Exception e){
                log.error("SSE 发送错误提示失败", e);
            }
            emitter.complete();
            return emitter;
        }

        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(300000L);
        // 标记是否已完成清理，防止重复 cleanup
        final AtomicBoolean cleaned = new AtomicBoolean(false);
        // 增加 SSE 生命周期回调，释放资源
        emitter.onTimeout( ()-> {
            log.warn("SSE 连接超时");
            state = AgentState.ERROR;
            emitter.complete();
            cleanup();
        });
        emitter.onCompletion(()->{
            log.info("SSE 连接已关闭");
            // 兜底重置状态
            if (state == AgentState.RUNNING){
                state = AgentState.FINISHED;
            }
            // 只执行一次清理
            if(cleaned.compareAndSet(false,true)){
                cleanup();
            }
        });

        // 使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(()->{
            // 统一交给 nextPrompt，不直接 add，避免重复添加
            setNextPrompt(nextPrompt);

            // 2、执行，更改状态
            state = AgentState.RUNNING;
            // 保存结果列表
            StringBuilder finalResult = new StringBuilder();
            try{
                for (int i = 0; i < maxSteps && state == AgentState.RUNNING; i++){
                    currentStep = i + 1;
                    log.info("===== 执行步骤 {}/{} =====", currentStep, maxSteps);
                    String stepResult = step();
                    String currentStepResult = "Step" + currentStep + ": " + stepResult;
                    finalResult.append("Step").append(currentStep).append(":").append(currentStepResult).append("\n");
                    // send 单独捕获 IO 异常，处理客户端主动断连
                    try{
                        emitter.send(currentStepResult);
                    } catch (IOException e){
                        log.warn("客户端断开 SSE 连接，终止执行", e);
                        state = AgentState.FINISHED;
                        break;
                    }
                }
                if(currentStep >= maxSteps){
                    String tip = "已到达最大步骤数，自动终止";
                    try{
                        emitter.send(tip);
                    }catch (IOException e){
                        log.warn("发送终止提示失败", e);
                    }
                }
                emitter.complete();
                state = AgentState.FINISHED;
            } catch (Exception e){
                state = AgentState.ERROR;
                log.error("Agent 执行异常", e);
                try{
                    emitter.send("执行错误："+ e.getMessage());
                    emitter.complete();
                }catch (IOException e1){
                    emitter.completeWithError(e1);
                }
            }
            finally{
                // 兜底清理，保证只执行一次
                if(cleaned.compareAndSet(false,true)){
                    cleanup();
                }
            }
        });
        return emitter;
    }
    /**
     * 定义单个步骤
     *
     * @return 子类实现
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup(){
        log.info("Agent 清理资源，清空上下文");
        messageList.clear();
        state = AgentState.IDLE;
        currentStep = 0;
        nextPrompt = null;
    }
    /**
     * 手动清空上下文（外部可用）
     */
    public void clearContext() {
        messageList.clear();
    }

}
