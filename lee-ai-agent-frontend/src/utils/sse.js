/**
 * SSE (Server-Sent Events) 连接工具
 * 用于实时接收后端流式响应
 */

/**
 * 创建 SSE 连接到 AiTrip 聊天接口
 * @param {string} message - 用户消息
 * @param {string} chatId - 聊天室 ID
 * @param {object} callbacks - 回调函数集合
 * @param {function} callbacks.onMessage - 收到消息回调
 * @param {function} callbacks.onError - 错误回调
 * @param {function} callbacks.onComplete - 完成回调
 * @returns {AbortController} 用于取消连接
 */
export function connectAiTripChat(message, chatId, callbacks) {
  const { onMessage, onError, onComplete } = callbacks
  const controller = new AbortController()

  const params = new URLSearchParams({
    message: message,
    chatId: chatId
  })

  const url = `/api/ai/ai_trip_app/chat/sse?${params.toString()}`

  fetch(url, {
    method: 'GET',
    headers: {
      'Accept': 'text/event-stream',
      'Cache-Control': 'no-cache'
    },
    signal: controller.signal
  })
    .then(async (response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      while (true) {
        const { done, value } = await reader.read()
        if (done) {
          if (onComplete) onComplete()
          break
        }

        buffer += decoder.decode(value, { stream: true })
        const lines = buffer.split('\n')
        buffer = lines.pop() || ''

        for (const line of lines) {
          if (line.startsWith('data:')) {
            let data = line.slice(5).trim()

            // 处理 SSE data 可能带空格前缀的情况
            if (data.startsWith('data:')) {
              data = data.slice(5).trim()
            }

            if (data && data !== '[DONE]') {
              if (onMessage) onMessage(data)
            } else if (data === '[DONE]') {
              if (onComplete) onComplete()
              return
            }
          }
        }
      }
    })
    .catch((error) => {
      if (error.name === 'AbortError') {
        console.log('SSE 连接已取消')
        return
      }
      console.error('SSE 连接错误:', error)
      if (onError) onError(error)
    })

  return controller
}

/**
 * 创建 SSE 连接到 Manus 聊天接口
 * @param {string} message - 用户消息
 * @param {object} callbacks - 回调函数集合
 * @param {function} callbacks.onMessage - 收到消息回调
 * @param {function} callbacks.onError - 错误回调
 * @param {function} callbacks.onComplete - 完成回调
 * @returns {AbortController} 用于取消连接
 */
export function connectManusChat(message, callbacks) {
  const { onMessage, onError, onComplete } = callbacks
  const controller = new AbortController()

  const params = new URLSearchParams({
    message: message
  })

  const url = `/api/ai/manus/chat?${params.toString()}`

  fetch(url, {
    method: 'GET',
    headers: {
      'Accept': 'text/event-stream',
      'Cache-Control': 'no-cache'
    },
    signal: controller.signal
  })
    .then(async (response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      while (true) {
        const { done, value } = await reader.read()
        if (done) {
          if (onComplete) onComplete()
          break
        }

        buffer += decoder.decode(value, { stream: true })
        const lines = buffer.split('\n')
        buffer = lines.pop() || ''

        for (const line of lines) {
          if (line.startsWith('data:')) {
            let data = line.slice(5).trim()

            if (data.startsWith('data:')) {
              data = data.slice(5).trim()
            }

            if (data && data !== '[DONE]') {
              if (onMessage) onMessage(data)
            } else if (data === '[DONE]') {
              if (onComplete) onComplete()
              return
            }
          }
        }
      }
    })
    .catch((error) => {
      if (error.name === 'AbortError') {
        console.log('SSE 连接已取消')
        return
      }
      console.error('SSE 连接错误:', error)
      if (onError) onError(error)
    })

  return controller
}

/**
 * 生成唯一聊天室 ID
 * @returns {string} UUID v4
 */
export function generateChatId() {
  return 'xxxxxx-xxxx-4xxx-yxxx-xxxxxx'.replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0
    const v = c === 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}
