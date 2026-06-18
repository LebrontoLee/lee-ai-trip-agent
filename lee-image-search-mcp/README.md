# Lee Image Search MCP Server

基于 Spring AI MCP Server 的图片搜索服务，为 AI Agent 提供网页图片搜索能力。

## 功能

- 通过 Pexels API 搜索网络图片，返回中等尺寸图片 URL 列表
- 支持 **SSE**（Server-Sent Events）和 **STDIO** 两种传输模式
- 可作为 MCP Tool 集成到任何支持 MCP 协议的 AI Agent 中

## 技术栈

- Java 21
- Spring Boot 3.5.15
- Spring AI MCP Server WebMVC
- Hutool 5.8.40
- Pexels API

## 快速开始

### 1. 获取 Pexels API Key

前往 [Pexels API](https://www.pexels.com/api/) 注册并获取 API Key。

### 2. 配置 API Key

修改 `src/main/java/com/lee/leeimagesearchmcp/tools/ImageSearchTool.java` 中的 `API_KEY` 常量：

```java
private static final String API_KEY = "your_pexels_api_key";
```

### 3. 启动服务

**SSE 模式**（默认，适合远程调用）：

```bash
cd lee-image-search-mcp
./mvnw spring-boot:run
```

服务默认运行在 `http://localhost:8127`

**STDIO 模式**（适合本地进程通信）：

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=stdio
```

### 4. 在 AI Agent 中配置

在 `mcp-servers.json` 中添加该 MCP Server：

```json
{
  "mcpServers": {
    "image-search": {
      "type": "sse",
      "url": "http://localhost:8127"
    }
  }
}
```

STDIO 模式配置：

```json
{
  "mcpServers": {
    "image-search": {
      "type": "stdio",
      "command": "java",
      "args": ["-jar", "lee-image-search-mcp/target/lee-image-search-mcp-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=stdio"]
    }
  }
}
```

## 工具说明

### searchImage

根据关键词搜索网络图片。

| 参数  | 类型   | 必填 | 说明         |
|-------|--------|------|--------------|
| query | String | 是   | 搜索关键词   |

**返回值**：多行图片 URL（每行一个），可直接渲染多张图片。

## 项目结构

```
lee-image-search-mcp/
├── src/main/java/com/lee/leeimagesearchmcp/
│   ├── LeeImageSearchMcpApplication.java   # 主应用入口，注册 MCP Tools
│   └── tools/
│       └── ImageSearchTool.java            # 图片搜索工具实现
├── src/main/resources/
│   ├── application.yaml                    # 默认配置（端口 8127）
│   ├── application-sse.yaml                # SSE 模式配置
│   └── application-stdio.yaml              # STDIO 模式配置
├── src/test/java/                          # 测试代码
├── pom.xml                                 # Maven 依赖
├── mvnw / mvnw.cmd                         # Maven Wrapper
└── README.md
```
