<template>
  <div class="chat-input">
    <div class="chat-input__wrapper">
      <textarea
        ref="textareaRef"
        v-model="inputText"
        class="chat-input__textarea"
        :placeholder="placeholder"
        :disabled="disabled"
        rows="1"
        @keydown.enter.exact="handleSend"
        @input="autoResize"
        @compositionstart="isComposing = true"
        @compositionend="isComposing = false"
      ></textarea>
      <button
        class="chat-input__send-btn"
        :class="{ 'chat-input__send-btn--active': inputText.trim() && !disabled }"
        :disabled="!inputText.trim() || disabled"
        @click="handleSend"
        :title="disabled ? 'AI 正在回复中...' : '发送消息'"
      >
        <svg v-if="!disabled" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z" fill="currentColor"/>
        </svg>
        <svg v-else class="spinner" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" opacity="0.25"/>
          <path d="M12 2a10 10 0 0 1 10 10" stroke="currentColor" stroke-width="3" stroke-linecap="round"/>
        </svg>
      </button>
    </div>
    <div class="chat-input__hint">
      按 Enter 发送消息 · Shift + Enter 换行
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'

const props = defineProps({
  placeholder: {
    type: String,
    default: '输入您的问题...'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['send'])

const inputText = ref('')
const isComposing = ref(false)
const textareaRef = ref(null)

function autoResize() {
  nextTick(() => {
    const el = textareaRef.value
    if (el) {
      el.style.height = 'auto'
      el.style.height = Math.min(el.scrollHeight, 150) + 'px'
    }
  })
}

function handleSend(e) {
  // 处理中文输入法 composition 状态
  if (isComposing.value) return
  // Shift + Enter 换行
  if (e.shiftKey) return

  e.preventDefault()
  const text = inputText.value.trim()
  if (!text || props.disabled) return

  emit('send', text)
  inputText.value = ''

  nextTick(() => {
    const el = textareaRef.value
    if (el) {
      el.style.height = 'auto'
    }
  })
}
</script>

<style scoped>
.chat-input {
  padding: 16px 20px 20px;
  background: #ffffff;
  border-top: 1px solid #e2e8f0;
}

.chat-input__wrapper {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  background: #f8fafc;
  border: 1.5px solid #e2e8f0;
  border-radius: 20px;
  padding: 8px 8px 8px 18px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.chat-input__wrapper:focus-within {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  background: #ffffff;
}

.chat-input__textarea {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  line-height: 1.5;
  color: #1e293b;
  resize: none;
  max-height: 150px;
  font-family: inherit;
  padding: 4px 0;
}

.chat-input__textarea::placeholder {
  color: #94a3b8;
}

.chat-input__textarea:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.chat-input__send-btn {
  flex-shrink: 0;
  width: 38px;
  height: 38px;
  border-radius: 50%;
  border: none;
  background: #e2e8f0;
  color: #94a3b8;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
}

.chat-input__send-btn svg {
  width: 20px;
  height: 20px;
}

.chat-input__send-btn--active {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.chat-input__send-btn--active:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.4);
}

.chat-input__send-btn:disabled {
  cursor: not-allowed;
}

.spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.chat-input__hint {
  text-align: center;
  font-size: 11px;
  color: #94a3b8;
  margin-top: 8px;
}
</style>
