package com.lee.leeaitripagent.agent;

import com.lee.leeaitripagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * ReAct (Reasoning and Acting) 模式的代理抽象类
 * 实现了思考--行动的循环模式
 */
@EqualsAndHashCode(callSuper=true)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent {
    /*
     * 处理当前状态并决定下一步行动
     * @return 是否需要执行行动，true 表示需要执行，false 表示不需要执行
     */
    public abstract boolean think();

    /*
     * 执行决定的行动
     * @return 行动执行结果
     */
    public abstract String act();

    /*
     * 执行单个步骤，思考和行动
     * @return 步骤执行结果
     */
    @Override
    public String step()
    {
        try{
            // 先思考
            boolean shouldAct = think();
            if(!shouldAct){
                log.info("{} 思考完成，无需执行工具", getName());
                this.setState(AgentState.FINISHED);
                return getFinalAnswer();
            }
            // 再行动
            String actResult = act();
            log.info("{} 工具执行完成：{}", getName(), actResult);
            return "工具执行成功：" + actResult;
        }
        catch(Exception e){
            // 记录异常日志
            log.error("{} ReAct 单步执行异常", getName(), e);
            this.setState(AgentState.ERROR);
            return "步骤执行失败：" + e.getMessage();
        }
    }
    /**
     * 获取最终答案
     */
    public String getFinalAnswer()
    {
        List<Message> messageList = getMessageList();
        if(messageList == null || messageList.isEmpty()){
            return "思考完成 - 暂无回复";
        }

        Message lastMsg = messageList.get(messageList.size()-1);
        if(lastMsg instanceof AssistantMessage assistantMessage){
            return "思考完成 - " + assistantMessage.getText();
        }
        return "思考完成 - 无法解析回复";
    }
}
