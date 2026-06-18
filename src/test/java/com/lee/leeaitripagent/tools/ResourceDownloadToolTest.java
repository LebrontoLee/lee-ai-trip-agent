package com.lee.leeaitripagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceDownloadToolTest {

    @Test
    void downloadResource() {
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        String url = "https://www.codefather.cn/logo.png";
        String filename = "logo.png";
        String s = resourceDownloadTool.downloadResource(url, filename);
        Assertions.assertNotNull(s);
    }
}