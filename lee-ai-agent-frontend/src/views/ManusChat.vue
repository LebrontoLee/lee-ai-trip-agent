<template>
  <div class="chat-page chat-page--manus">
    <!-- 背景 -->
    <div class="chat-page__bg">
      <div class="chat-page__bg-image" :style="{ backgroundImage: `url(${bgImage})` }"></div>
      <div class="chat-page__bg-overlay"></div>
    </div>

    <!-- 顶部导航栏 -->
    <header class="chat-header">
      <button class="chat-header__back" @click="$router.push('/')" title="返回首页">
        <svg viewBox="0 0 24 24" fill="none">
          <path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <div class="chat-header__info">
        <div class="chat-header__icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 2a4 4 0 0 1 4 4c0 1.1-.44 2.1-1.17 2.83l1.41 1.42A5.98 5.98 0 0 1 18 6a6 6 0 0 0-12 0c0 1.62.64 3.09 1.76 4.18l1.41-1.42A3.98 3.98 0 0 1 8 6a4 4 0 0 1 4-4z" fill="currentColor" opacity="0.6"/>
            <path d="M12 8a4 4 0 0 0-4 4c0 1.1.44 2.1 1.17 2.83l-1.41 1.42A5.98 5.98 0 0 1 6 12a6 6 0 0 1 12 0c0 1.62-.64 3.09-1.76 4.18l-1.41-1.42A3.98 3.98 0 0 0 16 12a4 4 0 0 0-4-4z" fill="currentColor" opacity="0.8"/>
            <circle cx="12" cy="16" r="3" fill="currentColor"/>
          </svg>
        </div>
        <div>
          <h2 class="chat-header__title">AI 超级智能体</h2>
          <p class="chat-header__status">
            <span class="status-dot status-dot--online"></span>
            在线 · 深度推理与工具调用
          </p>
        </div>
      </div>
      <button class="chat-header__new-chat" @click="startNewChat" title="新建会话">
        <svg viewBox="0 0 24 24" fill="none">
          <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <span>新会话</span>
      </button>
    </header>

    <!-- 聊天记录区 -->
    <div class="chat-messages" ref="messagesContainer">
      <!-- 欢迎消息 -->
      <div v-if="messages.length === 0" class="chat-welcome">
        <div class="chat-welcome__icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M9.663 17h4.674M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707M12 13a1 1 0 1 0 0-2 1 1 0 0 0 0 2z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" fill="none"/>
            <circle cx="12" cy="12" r="2" fill="currentColor" opacity="0.4"/>
            <path d="M12 4C7.58 4 4 7.58 4 12s3.58 8 8 8 8-3.58 8-8-3.58-8-8-8z" stroke="currentColor" stroke-width="1.5" opacity="0.6"/>
            <path d="M12 1C5.92 1 1 5.92 1 12s4.92 11 11 11 11-4.92 11-11S18.08 1 12 1z" stroke="currentColor" stroke-width="1" opacity="0.3"/>
          </svg>
        </div>
        <h3 class="chat-welcome__title">您好，探索者！🧠</h3>
        <p class="chat-welcome__desc">
          我是 AI 超级智能体，集成了多种 AI 工具和深度推理能力。<br>
          无论是复杂分析、工具调用还是知识问答，我都能为您服务。
        </p>
        <div class="chat-welcome__prompts">
          <button
            v-for="prompt in quickPrompts"
            :key="prompt"
            class="prompt-chip"
            @click="handleQuickPrompt(prompt)"
            :disabled="isLoading"
          >
            {{ prompt }}
          </button>
        </div>
      </div>

      <!-- 消息列表 -->
      <ChatMessage
        v-for="msg in messages"
        :key="msg.id"
        :role="msg.role"
        :content="msg.content"
        :timestamp="msg.timestamp"
        :isStreaming="msg.isStreaming"
      />

      <!-- 加载指示器 -->
      <div v-if="isLoading && currentAiContent === ''" class="chat-loading">
        <div class="loading-dots">
          <span></span><span></span><span></span>
        </div>
        <span>AI 正在深度思考...</span>
      </div>

      <div ref="messagesEnd" class="messages-end"></div>
    </div>

    <!-- 输入区 -->
    <ChatInput
      :disabled="isLoading"
      placeholder="告诉我您需要什么帮助，我会调用合适的工具来完成任务..."
      @send="handleSend"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import ChatMessage from '../components/ChatMessage.vue'
import ChatInput from '../components/ChatInput.vue'
import { connectManusChat } from '../utils/sse'

// 背景图（科技感自然风光）
const bgImages = [
  'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1920&q=80',
  'https://images.unsplash.com/photo-1518818414301-1b72d883d3d0?w=1920&q=80',
  'https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=1920&q=80',
  'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=1920&q=80',
  'https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1920&q=80'
]
const bgImage = ref(bgImages[Math.floor(Math.random() * bgImages.length)])

const messages = ref([])
const isLoading = ref(false)
const currentAiContent = ref('')
const currentAiMessageId = ref(null)
const sseController = ref(null)
const messagesContainer = ref(null)
const messagesEnd = ref(null)

const quickPrompts = [
  '🔍 帮我分析最近 AI 领域的发展趋势',
  '📊 对比分析 Python 和 Go 在微服务架构中的优劣',
  '💡 设计一个电商推荐系统的技术方案',
  '🌐 用中文和英文各写一首关于人工智能的诗'
]

// 新建会话
function startNewChat() {
  if (isLoading.value) {
    cancelSSE()
  }
  messages.value = []
  currentAiContent.value = ''
  currentAiMessageId.value = null
}

// 快速提示
function handleQuickPrompt(prompt) {
  handleSend(prompt)
}

// 发送消息
function handleSend(text) {
  if (!text || isLoading.value) return

  // 添加用户消息
  const userMsg = {
    id: 'user-' + Date.now(),
    role: 'user',
    content: text,
    timestamp: Date.now(),
    isStreaming: false
  }
  messages.value.push(userMsg)

  // 创建 AI 消息占位
  const aiMsgId = 'ai-' + Date.now()
  currentAiMessageId.value = aiMsgId
  currentAiContent.value = ''

  const aiMsg = {
    id: aiMsgId,
    role: 'ai',
    content: '',
    timestamp: Date.now(),
    isStreaming: true
  }
  messages.value.push(aiMsg)

  isLoading.value = true
  scrollToBottom()

  // 建立 SSE 连接
  sseController.value = connectManusChat(text, {
    onMessage: (data) => {
      currentAiContent.value += data
      updateLastAiMessage()
      scrollToBottom()
    },
    onError: (error) => {
      console.error('SSE Error:', error)
      if (currentAiContent.value === '') {
        currentAiContent.value = '抱歉，连接出现了问题，请稍后重试。'
      }
      finishStreaming()
    },
    onComplete: () => {
      finishStreaming()
    }
  })
}

// 更新最后一条 AI 消息的内容
function updateLastAiMessage() {
  const lastMsg = messages.value[messages.value.length - 1]
  if (lastMsg && lastMsg.id === currentAiMessageId.value) {
    lastMsg.content = currentAiContent.value
  }
}

// 完成流式传输
function finishStreaming() {
  const lastMsg = messages.value[messages.value.length - 1]
  if (lastMsg && lastMsg.id === currentAiMessageId.value) {
    lastMsg.isStreaming = false
  }
  isLoading.value = false
  currentAiContent.value = ''
  currentAiMessageId.value = null
  sseController.value = null
}

// 取消 SSE
function cancelSSE() {
  if (sseController.value) {
    sseController.value.abort()
    sseController.value = null
  }
  finishStreaming()
}

// 滚动到底部
function scrollToBottom() {
  nextTick(() => {
    messagesEnd.value?.scrollIntoView({ behavior: 'smooth' })
  })
}

onUnmounted(() => {
  cancelSSE()
})
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

/* ===== 背景 ===== */
.chat-page__bg {
  position: fixed;
  inset: 0;
  z-index: 0;
}

.chat-page__bg-image {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  filter: brightness(0.3) saturate(0.5);
}

.chat-page__bg-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    rgba(15, 23, 42, 0.9) 0%,
    rgba(30, 20, 50, 0.65) 50%,
    rgba(15, 23, 42, 0.85) 100%
  );
}

/* ===== 顶部导航 ===== */
.chat-header {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.04);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.chat-header__back {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: rgba(255, 255, 255, 0.06);
  color: #e2e8f0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.chat-header__back:hover {
  background: rgba(255, 255, 255, 0.12);
}

.chat-header__back svg {
  width: 20px;
  height: 20px;
}

.chat-header__info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.chat-header__icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #a78bfa, #7c3aed);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.chat-header__icon svg {
  width: 22px;
  height: 22px;
}

.chat-header__title {
  font-size: 16px;
  font-weight: 600;
  color: #f1f5f9;
  margin: 0;
  letter-spacing: -0.01em;
}

.chat-header__status {
  font-size: 12px;
  color: #94a3b8;
  margin: 2px 0 0 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
}

.status-dot--online {
  background: #22c55e;
  box-shadow: 0 0 6px rgba(34, 197, 94, 0.5);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.chat-header__new-chat {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: rgba(255, 255, 255, 0.06);
  color: #e2e8f0;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
  font-family: inherit;
}

.chat-header__new-chat:hover {
  background: rgba(255, 255, 255, 0.12);
}

.chat-header__new-chat svg {
  width: 16px;
  height: 16px;
}

/* ===== 聊天记录区 ===== */
.chat-messages {
  position: relative;
  z-index: 10;
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
  scroll-behavior: smooth;
}

.chat-messages::-webkit-scrollbar {
  width: 5px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.12);
  border-radius: 10px;
}

.messages-end {
  height: 8px;
}

/* ===== 欢迎页 ===== */
.chat-welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.chat-welcome__icon {
  width: 80px;
  height: 80px;
  border-radius: 24px;
  background: rgba(168, 85, 247, 0.12);
  border: 1px solid rgba(168, 85, 247, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a78bfa;
  margin-bottom: 24px;
}

.chat-welcome__icon svg {
  width: 44px;
  height: 44px;
}

.chat-welcome__title {
  font-size: 28px;
  font-weight: 700;
  color: #f1f5f9;
  margin: 0 0 12px 0;
  letter-spacing: -0.02em;
}

.chat-welcome__desc {
  font-size: 15px;
  color: #94a3b8;
  line-height: 1.6;
  max-width: 480px;
  margin: 0 0 32px 0;
}

.chat-welcome__prompts {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  max-width: 520px;
}

.prompt-chip {
  padding: 10px 18px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  color: #cbd5e1;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.25s;
  font-family: inherit;
  text-align: left;
}

.prompt-chip:hover {
  background: rgba(168, 85, 247, 0.12);
  border-color: rgba(168, 85, 247, 0.3);
  color: #f1f5f9;
  transform: translateY(-2px);
}

.prompt-chip:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

/* ===== 加载状态 ===== */
.chat-loading {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 24px;
  color: #94a3b8;
  font-size: 13px;
}

.loading-dots {
  display: flex;
  gap: 4px;
}

.loading-dots span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #64748b;
  animation: loadingDot 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }
.loading-dots span:nth-child(3) { animation-delay: 0s; }

@keyframes loadingDot {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

/* ===== 响应式 ===== */
@media (max-width: 640px) {
  .chat-header {
    padding: 12px 16px;
  }

  .chat-header__new-chat span {
    display: none;
  }

  .chat-welcome__title {
    font-size: 24px;
  }
}
</style>
