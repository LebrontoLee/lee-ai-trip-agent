<template>
  <div
    class="chat-message"
    :class="[role === 'user' ? 'chat-message--user' : 'chat-message--ai']"
  >
    <div class="chat-message__avatar">
      <div v-if="role === 'ai'" class="avatar avatar--ai">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z" fill="currentColor" opacity="0.2"/>
          <path d="M12 15c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3z" fill="currentColor"/>
          <path d="M12 6c-3.31 0-6 2.69-6 6s2.69 6 6 6 6-2.69 6-6-2.69-6-6-6zm0 2c2.21 0 4 1.79 4 4s-1.79 4-4 4-4-1.79-4-4 1.79-4 4-4z" fill="currentColor"/>
        </svg>
      </div>
      <div v-else class="avatar avatar--user">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" fill="currentColor"/>
        </svg>
      </div>
    </div>
    <div class="chat-message__body">
      <div class="chat-message__header">
        <span class="chat-message__sender">
          {{ role === 'ai' ? 'AI 助手' : '我' }}
        </span>
        <span class="chat-message__time">{{ formattedTime }}</span>
      </div>
      <div class="chat-message__content">
        <div v-if="role === 'ai'" class="message-text markdown-body" v-html="renderedContent"></div>
        <div v-else class="message-text">{{ content }}</div>
        <div v-if="isStreaming" class="typing-indicator">
          <span></span><span></span><span></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  role: {
    type: String,
    required: true,
    validator: (v) => ['user', 'ai'].includes(v)
  },
  content: {
    type: String,
    default: ''
  },
  timestamp: {
    type: [Number, String],
    default: Date.now
  },
  isStreaming: {
    type: Boolean,
    default: false
  }
})

const formattedTime = computed(() => {
  const date = new Date(props.timestamp)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
})

// 简单的 Markdown 渲染（支持粗体、斜体、代码块、链接）
const renderedContent = computed(() => {
  let text = props.content
  if (!text) return ''

  // 转义 HTML
  text = text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')

  // 代码块 ```
  text = text.replace(/```(\w*)\n?([\s\S]*?)```/g, '<pre><code class="language-$1">$2</code></pre>')

  // 行内代码 `code`
  text = text.replace(/`([^`]+)`/g, '<code>$1</code>')

  // 粗体 **text**
  text = text.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')

  // 斜体 *text*
  text = text.replace(/\*([^*]+)\*/g, '<em>$1</em>')

  // 链接 [text](url)
  text = text.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank" rel="noopener">$1</a>')

  // 换行
  text = text.replace(/\n/g, '<br>')

  return text
})
</script>

<style scoped>
.chat-message {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  animation: messageSlideIn 0.35s ease-out;
}

.chat-message--ai {
  flex-direction: row;
}

.chat-message--user {
  flex-direction: row-reverse;
}

.chat-message__avatar {
  flex-shrink: 0;
  width: 38px;
  height: 38px;
}

.avatar {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

.avatar--ai {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.avatar--user {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.avatar svg {
  width: 22px;
  height: 22px;
  color: #fff;
}

.chat-message__body {
  max-width: 72%;
  min-width: 120px;
}

.chat-message--user .chat-message__body {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.chat-message__header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.chat-message__sender {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
}

.chat-message__time {
  font-size: 11px;
  color: #94a3b8;
}

.chat-message__content {
  position: relative;
}

.message-text {
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.65;
  word-break: break-word;
}

.chat-message--ai .message-text {
  background: #ffffff;
  color: #1e293b;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.chat-message--user .message-text {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #ffffff;
  border-top-right-radius: 4px;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

/* Markdown 内容样式 */
.message-text :deep(pre) {
  background: #1e293b;
  color: #e2e8f0;
  padding: 12px 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 8px 0;
  font-size: 13px;
  line-height: 1.5;
}

.message-text :deep(code) {
  background: #f1f5f9;
  color: #e11d48;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
  font-family: 'Fira Code', 'Consolas', monospace;
}

.message-text :deep(pre code) {
  background: transparent;
  color: #e2e8f0;
  padding: 0;
}

.message-text :deep(a) {
  color: #3b82f6;
  text-decoration: underline;
}

.message-text :deep(strong) {
  font-weight: 600;
}

.chat-message--user .message-text :deep(code) {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.chat-message--user .message-text :deep(a) {
  color: #bfdbfe;
}

/* 打字指示器 */
.typing-indicator {
  display: inline-flex;
  gap: 4px;
  padding: 8px 12px;
  margin-top: 4px;
}

.typing-indicator span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #94a3b8;
  animation: typingBounce 1.4s infinite ease-in-out both;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }
.typing-indicator span:nth-child(3) { animation-delay: 0s; }

@keyframes typingBounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
