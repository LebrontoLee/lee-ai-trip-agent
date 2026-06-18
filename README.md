# 🌍 AI 旅行规划大师 (AI Travel Planning Master)

基于 **Spring AI + DeepSeek + Vue 3** 的全栈 AI 智能旅行助手平台。提供旅行规划聊天与超级智能体两大 AI 服务，支持 SSE 流式实时响应、MCP 工具扩展、ReAct 自主推理与执行。

---

## 🏗️ 项目架构

```
┌─────────────────────────────────────────────────────────────────┐
│                    用户浏览器 (Vue 3 SPA)                         │
│                  http://localhost:5173                           │
│                                                                  │
│  ┌──────────┐  ┌───────────┐  ┌───────────┐                    │
│  │  Home    │  │ AiTripChat │  │ ManusChat │                    │
│  │  主页     │  │ 旅行规划    │  │ 超级智能体  │                    │
│  └──────────┘  └─────┬─────┘  └─────┬─────┘                    │
│                       │              │                           │
│              SSE 流式  │              │ SSE 流式                   │
└───────────────────────┼──────────────┼──────────────────────────┘
                        │              │
                  Vite Proxy (/:api → localhost:8123)
                        │              │
┌───────────────────────┼──────────────┼──────────────────────────┐
│                  Spring Boot 后端 (端口 8123)                     │
│                                                                  │
│  ┌──────────────────┐  ┌──────────────────────────────────┐    │
│  │    AiTripApp      │  │         LeeManus (超级智能体)      │    │
│  │  (旅行规划助手)     │  │  ┌──────────────────────────┐   │    │
│  │  · ChatClient     │  │  │  ReAct 推理-行动循环       │   │    │
│  │  · ChatMemory     │  │  │  · think() → act()        │   │    │
│  │  · System Prompt  │  │  │  · 最多 8 步              │   │    │
│  └──────────────────┘  │  │  · 7 个注册工具            │   │    │
│                         │  └──────────────────────────┘   │    │
│                         └──────────────────────────────────┘    │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    工具集 (ToolCallbacks)                   │  │
│  │  🔍 WebSearch  📄 FileOperation  🌐 WebScraping           │  │
│  │  📥 Download   💻 Terminal        📝 PDFGeneration         │  │
│  │  ⏹️  Terminate                                            │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
│  ┌─────────────────┐  ┌────────────────────────────────────┐   │
│  │  MCP Client     │  │  RAG (可选，默认禁用)                │   │
│  │  · Amap Maps    │  │  · pgVector 向量存储               │   │
│  │  · Image Search │  │  · Markdown 文档加载               │   │
│  └─────────────────┘  └────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
                          │
                          │ MCP 协议 (SSE / STDIO)
                          ▼
┌──────────────────────────────────────────────────────────────────┐
│          lee-image-search-mcp (MCP Server, 端口 8127)             │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  searchImage — Pexels API 图片搜索工具                     │  │
│  │  支持 SSE / STDIO 双传输模式                                │  │
│  └──────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 📦 项目组成

本项目包含三个子项目：

| 子项目 | 目录 | 技术栈 | 说明 |
|--------|------|--------|------|
| 🔧 **后端服务** | `./` (根目录) | Spring Boot 3.5.15 + Spring AI 1.1.2 + Java 21 | 核心 AI 服务，提供旅行规划与超级智能体 API |
| 🎨 **前端应用** | `./lee-ai-agent-frontend/` | Vue 3.4 + Vite 5.3 + Axios 1.7 | SPA 聊天界面，SSE 流式响应，暗色主题 |
| 🖼️ **MCP 图片搜索** | `./lee-image-search-mcp/` | Spring Boot 3.5.15 + Spring AI MCP Server | MCP 图片搜索服务，为 AI Agent 提供 Pexels 图片检索 |

---

## ✨ 功能概览

### 🗺️ AI Trip 旅行规划 (`/ai-trip`)

专业旅行规划助手，专注三类旅行场景：
- **短期休闲游** — 周边目的地推荐、行程天数、娱乐安排
- **城市深度游** — 地标打卡、美食探索、交通攻略
- **户外长途游** — 路线规划、装备建议、住宿安排

特性：会话记忆（文件持久化）、SSE 流式输出、快捷提示词、Markdown 渲染

### 🧠 AI 超级智能体 (`/manus`)

基于 **ReAct（Reasoning + Acting）** 模式的自主 AI Agent，可调用多种工具完成复杂任务：
- 🔍 **网络搜索** — 通过 SearchAPI.io 搜索百度
- 🌐 **网页抓取** — 使用 Jsoup 抓取网页内容
- 📄 **文件操作** — 读写本地 UTF-8 文件
- 📥 **资源下载** — 从 URL 下载文件
- 💻 **终端命令** — 执行 Windows 终端指令
- 📝 **PDF 生成** — 生成含中文的 PDF 文档
- ⏹️ **任务终止** — 自动判断任务完成并退出

特性：自主推理循环（最多 8 步）、SSE 流式输出、工具调用可视化

---

## 🛠️ 技术栈

### 后端（lee-ai-trip-agent）

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 21 |
| 框架 | Spring Boot | 3.5.15 |
| AI 框架 | Spring AI + Spring AI Alibaba Agent Framework | 1.1.2 |
| LLM | DeepSeek (deepseek-v4-flash) | — |
| MCP Client | Spring AI MCP Client WebFlux | 1.1.2 |
| API 文档 | Knife4j (OpenAPI 3) | 4.4.0 |
| 工具库 | Hutool | 5.8.40 |
| 序列化 | Kryo | 5.6.2 |
| HTML 解析 | Jsoup | 1.22.2 |
| PDF 生成 | iText Core + font-asian | 9.6.0 |
| JSON Schema | victools jsonschema-generator | 4.38.0 |
| 简化代码 | Lombok | 1.18.36 |

### 前端（lee-ai-agent-frontend）

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 (Composition API) | ^3.4 |
| 构建工具 | Vite | ^5.3 |
| 路由 | Vue Router | ^4.3 |
| HTTP | Axios | ^1.7 |
| SSE | Fetch API + ReadableStream | 原生 |
| 字体 | Inter + Noto Sans SC (Google Fonts) | — |
| 图片 | Unsplash | — |

### MCP 服务（lee-image-search-mcp）

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 21 |
| 框架 | Spring Boot | 3.5.15 |
| MCP Server | Spring AI MCP Server WebMVC | 1.1.2 |
| 工具库 | Hutool | 5.8.40 |
| 图片 API | Pexels API | — |

---

## 🚀 快速开始

### 环境要求

- **JDK** >= 21
- **Node.js** >= 18
- **Maven** (或使用项目自带的 Maven Wrapper)
- **API Keys**：
  - [DeepSeek API Key](https://platform.deepseek.com/) — 后端 LLM 调用
  - [SearchAPI.io Key](https://www.searchapi.io/) — 网络搜索工具
  - [Pexels API Key](https://www.pexels.com/api/) — 图片搜索（可选）
  - [高德地图 API Key](https://lbs.amap.com/) — 地图 MCP 服务（可选）

### 1. 配置后端

编辑 `src/main/resources/application-local.yml`：

```yaml
spring:
  ai:
    deepseek:
      api-key: your_deepseek_api_key
      base-url: https://api.deepseek.com
      chat:
        options:
          model: deepseek-v4-flash
```

如需启用网络搜索，在同一个文件中配置 SearchAPI.io Key：

```yaml
searchapi:
  key: your_searchapi_key
```

### 2. 启动后端

```bash
# macOS / Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

后端服务运行在 **`http://localhost:8123`**，API 文档地址 **`http://localhost:8123/api/doc.html`**。

### 3. 配置并启动前端

```bash
cd lee-ai-agent-frontend
npm install
npm run dev
```

前端开发服务器运行在 **`http://localhost:5173`**，自动代理 `/api` 请求到后端。

### 4. 启动 MCP 图片搜索服务（可选）

```bash
cd lee-image-search-mcp

# 1. 先配置 Pexels API Key
# 编辑 src/main/java/com/lee/leeimagesearchmcp/tools/ImageSearchTool.java
# 将 API_KEY 替换为你的 Pexels API Key

# 2. 启动服务
./mvnw spring-boot:run
```

MCP 服务运行在 **`http://localhost:8127`**。

---

## 📡 API 端点

### AiController（后端核心接口）

| 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|
| GET | `/api/ai/ai_trip_app/chat/sync` | `message`, `chatId` | 同步旅行规划对话 |
| GET | `/api/ai/ai_trip_app/chat/sse` | `message`, `chatId` | SSE 流式旅行规划对话 |
| GET | `/api/ai/ai_trip_app/chat/server_sent_event` | `message`, `chatId` | SSE 流式（ServerSentEvent 封装） |
| GET | `/api/ai/ai_trip_app/chat/sse_emitter` | `message`, `chatId` | SSE 流式（SseEmitter） |
| GET | `/api/ai/manus/chat` | `message` | SSE 流式超级智能体对话 |

### MCP 工具

| MCP Server | 工具名 | 参数 | 说明 |
|------------|--------|------|------|
| lee-image-search-mcp | `searchImage` | `query` (String) | Pexels 图片搜索，返回多行 URL |
| amap-maps-mcp-server | （高德地图服务） | — | 地图/地理相关工具 |

### 前端路由

| 路径 | 页面 | 说明 |
|------|------|------|
| `/` | Home | 应用入口主页 |
| `/ai-trip` | AiTripChat | AI Trip 旅行规划聊天 |
| `/manus` | ManusChat | AI 超级智能体聊天 |

---

## 📁 目录结构

```
lee-ai-trip-agent/                          # 🔧 后端项目根目录（Spring Boot Maven 项目）
├── pom.xml                                  # Maven 构建配置
├── mvnw / mvnw.cmd                          # Maven Wrapper
├── src/
│   ├── main/java/com/lee/leeaitripagent/
│   │   ├── LeeAiTripAgentApplication.java   # Spring Boot 入口
│   │   ├── controller/AiController.java     # REST API 控制器
│   │   ├── app/AiTripApp.java               # 旅行规划应用编排器
│   │   ├── agent/                           # AI Agent 框架
│   │   │   ├── BaseAgent.java               # 抽象基类（状态机 + 步骤循环）
│   │   │   ├── ReActAgent.java              # ReAct 模式（推理 + 行动）
│   │   │   ├── ToolCallAgent.java           # 工具调用 Agent
│   │   │   └── LeeManus.java               # 超级智能体（用户入口）
│   │   ├── advisor/                         # Chat Advisors
│   │   │   ├── MyLoggerAdvisor.java         # 日志记录 Advisor
│   │   │   └── ReReadingAdvisor.java        # Re2 重读增强 Advisor
│   │   ├── chatmemory/                      # 聊天记忆（Kryo 文件持久化）
│   │   ├── config/CorsConfig.java           # 跨域配置
│   │   ├── rag/                             # RAG 检索增强（可选，默认禁用）
│   │   └── tools/                           # 7 个注册工具
│   │       ├── FileOperationTool.java       # 文件读写工具
│   │       ├── WebSearchTool.java           # 网络搜索工具（百度）
│   │       ├── WebScrapingTool.java         # 网页抓取工具
│   │       ├── ResourceDownloadTool.java    # 资源下载工具
│   │       ├── TerminalOperationTool.java   # 终端命令工具
│   │       ├── PDFGenerationTool.java       # PDF 生成工具
│   │       ├── TerminateTool.java           # 任务终止工具
│   │       └── ToolsRegistration.java       # 工具注册配置
│   ├── main/resources/
│   │   ├── application.yml                  # 主配置（端口 8123）
│   │   ├── application-local.yml            # 本地环境配置
│   │   ├── mcp-servers.json                 # MCP 客户端配置
│   │   └── documents/                       # RAG 知识库文档
│   └── test/                                # 测试代码
│
├── lee-ai-agent-frontend/                   # 🎨 前端项目（Vue 3 + Vite）
│   ├── package.json
│   ├── vite.config.js                       # Vite 配置 + API 代理
│   ├── index.html
│   └── src/
│       ├── main.js                          # 应用入口
│       ├── App.vue                          # 根组件（路由 + 过渡动画）
│       ├── router/index.js                  # 路由（3 个页面）
│       ├── api/index.js                     # Axios 实例
│       ├── utils/sse.js                     # SSE 连接工具
│       ├── components/
│       │   ├── ChatMessage.vue              # 消息气泡组件
│       │   └── ChatInput.vue                # 输入框组件
│       ├── views/
│       │   ├── Home.vue                     # 主页
│       │   ├── AiTripChat.vue               # 旅行规划聊天页
│       │   └── ManusChat.vue                # 超级智能体聊天页
│       └── styles/global.css                # 全局样式 + 暗色主题
│
└── lee-image-search-mcp/                    # 🖼️ MCP 图片搜索服务
    ├── pom.xml
    └── src/main/java/com/lee/leeimagesearchmcp/
        ├── LeeImageSearchMcpApplication.java # MCP Server 入口
        └── tools/ImageSearchTool.java        # Pexels 图片搜索工具
```

---

## 🔧 工具详解

### 后端注册的 7 个工具

| 工具 | 类 | 功能 |
|------|-----|------|
| `fileOperation` | `FileOperationTool.java` | 读写 `tmp/file/` 下的 UTF-8 文件 |
| `webSearch` | `WebSearchTool.java` | 通过 SearchAPI.io 搜索百度，返回 Top 5 JSON 结果 |
| `webScraping` | `WebScrapingTool.java` | 使用 Jsoup 抓取指定 URL 的完整 HTML |
| `resourceDownload` | `ResourceDownloadTool.java` | 从 URL 下载任意文件到 `tmp/download/` |
| `terminalOperation` | `TerminalOperationTool.java` | 通过 `cmd.exe /c` 执行 Windows 终端命令 |
| `pdfGeneration` | `PDFGenerationTool.java` | 生成含中文字体（STSongStd-Light）的 PDF，存储到 `tmp/pdf/` |
| `terminate` | `TerminateTool.java` | 智能体完成任务后主动退出循环 |

### MCP 工具

| 工具 | 来源 | 功能 |
|------|------|------|
| `searchImage` | lee-image-search-mcp (端口 8127) | Pexels API 图片搜索，返回中尺寸图片 URL |
| 高德地图工具集 | amap-maps-mcp-server | 地理编码、POI 搜索、路径规划等 |

---

## 🎨 设计理念

### 后端

- **分层 Agent 架构**：`BaseAgent → ReActAgent → ToolCallAgent → LeeManus` 四层继承，职责清晰
- **ReAct 推理循环**：每个步骤先思考（think）再行动（act），最大步数可控
- **工具自动注册**：通过 `ToolCallback` 机制自动发现和注册工具
- **MCP 扩展**：通过 Model Context Protocol 集成外部工具服务，支持 SSE/STDIO 双模式
- **会话持久化**：基于 Kryo 序列化的文件聊天记忆，支持滑动窗口裁剪
- **RAG 可选增强**：预留 pgVector 向量检索能力，通过配置文件灵活开启/关闭

### 前端

- **暗色主题**：全局暗色基调（`#0f172a`），配合自然风光背景，沉浸式体验
- **毛玻璃效果**：导航栏和卡片使用 `backdrop-filter: blur()` 实现磨砂质感
- **SSE 流式展示**：AI 回复逐字实时渲染，打字指示器动画
- **中文输入兼容**：正确处理 IME composition 事件
- **响应式布局**：适配桌面和移动端

---

## 📄 License

MIT

---

## 🔗 相关链接

- [Spring AI 文档](https://docs.spring.io/spring-ai/reference/)
- [DeepSeek 平台](https://platform.deepseek.com/)
- [Pexels API](https://www.pexels.com/api/)
- [高德开放平台](https://lbs.amap.com/)
- [Model Context Protocol (MCP)](https://modelcontextprotocol.io/)
- [Vue 3 文档](https://vuejs.org/)
- [Vite 文档](https://vitejs.dev/)
