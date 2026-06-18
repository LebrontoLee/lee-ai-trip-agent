package com.lee.leeaitripagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LeeManusTest {
    @Resource
    private LeeManus leemanus;
    @Test
    public void run(){
        String userPrompt = """
            我想在上海静安区游玩，请帮我找到 5 公里内合适的游玩地点，
            并结合一些网络图片，制定一份详细的旅游计划，
            并以 PDF 格式输出""";
        String answer = leemanus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}
