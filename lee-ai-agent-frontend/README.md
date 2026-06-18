# 🌍 AI 旅行规划大师 — 前端项目

基于 **Vue 3 + Vite** 构建的 AI 智能旅行助手前端应用，提供旅行规划聊天与超级智能体两大 AI 对话服务，支持 SSE 流式实时响应。

---

## ✨ 功能概览

| 页面 | 功能 | 接口 |
|------|------|------|
| 🏠 **主页** | 应用入口，切换 AI Trip 与 AI 超级智能体 | — |
| 🗺️ **AI Trip 旅行规划** | 聊天式旅行规划，智能推荐行程、景点、美食、住宿 | `GET /api/ai/ai_trip_app/chat/sse` |
| 🧠 **AI 超级智能体** | 多工具智能代理，深度推理、工具调用、复杂任务执行 | `GET /api/ai/manus/chat` |

### 交互细节

- **SSE 流式响应** — AI 回复逐字实时展示，提升交互体验
- **自动会话管理** — 进入 AI Trip 页面自动生成唯一 Chat ID，区分不同会话
- **一键新会话** — 顶部按钮快速清空对话并重新生成 Chat ID
- **快捷提示** — 欢迎页预设常用提问，点击即可发送
- **Markdown 渲染** — AI 回复支持代码块、粗体、斜体、链接等格式
- **输入法兼容** — 正确处理中文输入法 composition 事件
- **流式中断** — 发送新消息或离开页面时自动取消当前 SSE 连接

---

## 🛠️ 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 (Composition API) | ^3.4 |
| 构建工具 | Vite | ^5.3 |
| 路由 | Vue Router | ^4.3 |
| HTTP 请求 | Axios | ^1.7 |
| SSE 通信 | Fetch API + ReadableStream | — |
| 字体 | Inter + Noto Sans SC (Google Fonts) | — |
| 图片 | Unsplash 自然风光 | — |

---

## 📁 项目结构

```
lee-ai-agent-frontend/
├── index.html                    # 入口 HTML
├── package.json                  # 依赖与脚本
├── vite.config.js                # Vite 配置 + API 代理
├── README.md                     # 本文件
├── public/
│   └── vite.svg                  # Favicon 图标
└── src/
    ├── main.js                   # 应用入口
    ├── App.vue                   # 根组件（路由视图 + 过渡动画）
    ├── router/
    │   └── index.js              # 路由配置（Home / AiTrip / Manus）
    ├── api/
    │   └── index.js              # Axios 实例 + 拦截器
    ├── utils/
    │   └── sse.js                # SSE 连接工具（fetch ReadableStream）
    ├── components/
    │   ├── ChatMessage.vue       # 聊天消息气泡（用户/AI 双端样式）
    │   └── ChatInput.vue         # 聊天输入框（自动伸缩 + Enter 发送）
    ├── views/
    │   ├── Home.vue              # 主页 — 应用选择卡片
    │   ├── AiTripChat.vue        # AI Trip 旅行规划聊天页
    │   └── ManusChat.vue         # AI 超级智能体聊天页
    └── styles/
        └── global.css            # 全局样式 + CSS 变量 + 主题配置
```

---

## 🚀 快速开始

### 环境要求

- **Node.js** >= 18
- **npm** >= 9

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

开发服务器默认运行在 `http://localhost:5173`。

### 生产构建

```bash
npm run build      # 构建到 dist/
npm run preview    # 预览构建结果
```

---

## ⚙️ 后端接口代理

开发环境下，所有 `/api` 前缀的请求自动代理到后端服务。

默认后端地址：**`http://localhost:8123`**

如需修改，编辑 `vite.config.js`：

```js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8123',  // ← 修改此处
      changeOrigin: true
    }
  }
}
```

### 后端接口说明

| 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|
| GET | `/api/ai/ai_trip_app/chat/sse` | `message`, `chatId` | AI Trip 流式对话（SSE） |
| GET | `/api/ai/manus/chat` | `message` | AI 超级智能体流式对话（SSE） |

---

## 🎨 设计系统

### 配色方案

| 角色 | 色值 | 用途 |
|------|------|------|
| 主色调 | `#3b82f6` | 按钮、链接、用户消息气泡 |
| 天空蓝 | `#38bdf8` | AI Trip 主题强调色 |
| 紫罗兰 | `#a78bfa` | Manus 主题强调色 |
| 深色背景 | `#0f172a` | 全局背景 |
| 文字主色 | `#f1f5f9` | 主要文字 |
| 文字辅色 | `#94a3b8` | 次要文字、描述 |

### 设计特点

- **毛玻璃效果** — 导航栏、卡片使用 `backdrop-filter: blur()` 实现半透明磨砂质感
- **渐变动画** — 主页标题文字渐变色流动、背景光斑浮动
- **微交互** — 卡片 hover 上浮、按钮缩放、消息滑入动画、打字指示器
- **暗色主题** — 全局暗色基调，搭配低饱和度自然风光背景，沉浸式体验
- **响应式** — 适配桌面端与移动端，移动端隐藏辅助文字、缩小间距

---

## 🖥️ 页面路由

| 路径 | 页面 | 说明 |
|------|------|------|
| `/` | Home | 应用入口主页 |
| `/ai-trip` | AiTripChat | AI Trip 旅行规划聊天 |
| `/manus` | ManusChat | AI 超级智能体聊天 |

---

## 📄 License

MIT
