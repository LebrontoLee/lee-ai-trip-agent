package com.lee.leeaitripagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "啦啦啦.pdf";
        String content = "https://baidu.com";
        String result = tool.generatePDF(fileName, content);
        Assertions.assertNotNull(result);
    }
}