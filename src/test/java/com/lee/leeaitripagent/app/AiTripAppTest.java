package com.lee.leeaitripagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class AiTripAppTest {

    @Resource
    AiTripApp aiTripApp;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是Lee，我想要在上海周边短途旅行";
        String answer = aiTripApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我想要在上海及周边深度旅行";
        answer = aiTripApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我想要在上海户外长线游玩";
        answer = aiTripApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是Lee，我想要在上海周边短途旅行几天，但我不知道该怎么规划";
        AiTripApp.TripReport tripReport = aiTripApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(tripReport);
    }

//    @Test
//    void doChatWithRag() {
//            String chatId = UUID.randomUUID().toString();
//            String message = "你好，我是Lee，我想要在上海周边短途旅行几天，但我不知道该怎么规划";
//            String answer =  aiTripApp.doChatWithRag(message, chatId);
//            Assertions.assertNotNull(answer);
//    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想在上海周边旅游，推荐几个小众打卡地？");

        // 测试网页抓取
        testMessage("看看去哪儿网上有哪些值得玩的旅游路线");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的西藏旅行图片为文件");

        // 测试文件操作：保存用户档案
        testMessage("保存我的出行计划为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘出行计划’PDF，包含出行交通工具选择、景区门票预订以及行程时间规划");

    }
    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = aiTripApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }


    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        String message = "周末想在上海静安区游玩，帮我找到 10 公里内合适的游玩地点";
        String answer = aiTripApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }
}