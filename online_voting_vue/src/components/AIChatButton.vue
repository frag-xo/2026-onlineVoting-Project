<template>
  <div class="ai-chat-root">
    <!-- 悬浮触发按钮 -->
    <button
      class="ai-trigger"
      :class="{ 'is-open': visible }"
      @click="toggleChat"
      aria-label="AI 助手"
    >
      <svg v-if="!visible" class="trigger-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
        <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1.27A7 7 0 0 1 14 23h-4a7 7 0 0 1-5.73-3H3a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2z"/>
        <path d="M9 15v1"/>
        <path d="M15 15v1"/>
      </svg>
      <svg v-else class="trigger-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
        <line x1="18" y1="6" x2="6" y2="18"/>
        <line x1="6" y1="6" x2="18" y2="18"/>
      </svg>
    </button>

    <!-- 聊天面板 -->
    <transition name="panel">
      <div v-if="visible" class="ai-panel">
        <!-- 头部 -->
        <div class="panel-head">
          <div class="head-left">
            <div class="head-avatar">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1.27A7 7 0 0 1 14 23h-4a7 7 0 0 1-5.73-3H3a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2z"/>
              </svg>
            </div>
            <div class="head-meta">
              <span class="head-name">AI 助手</span>
              <span class="head-status">在线 · 即时回复</span>
            </div>
          </div>
          <button class="head-clear" @click="clearMessages" :disabled="messages.length <= 1" title="清空对话">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" width="16" height="16">
              <polyline points="3 6 5 6 21 6"/>
              <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              <line x1="10" y1="11" x2="10" y2="17"/>
              <line x1="14" y1="11" x2="14" y2="17"/>
            </svg>
          </button>
        </div>

        <!-- 消息区 -->
        <div class="panel-body" ref="bodyRef">
          <div
            v-for="(msg, i) in messages"
            :key="i"
            class="msg"
            :class="msg.role"
          >
            <div class="msg-bubble">{{ msg.content }}</div>
          </div>
          <div v-if="loading" class="msg ai">
            <div class="msg-bubble thinking">
              <span class="dot" />
              <span class="dot" />
              <span class="dot" />
            </div>
          </div>
        </div>

        <!-- 底栏 -->
        <div class="panel-foot">
          <div class="input-wrap">
            <input
              v-model="inputText"
              placeholder="输入消息..."
              @keydown.enter.prevent="sendMessage"
              :disabled="loading"
              class="msg-input"
            />
            <button class="send-btn" @click="sendMessage" :disabled="loading || !inputText.trim()">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="18" height="18">
                <line x1="22" y1="2" x2="11" y2="13"/>
                <polygon points="22 2 15 22 11 13 2 9 22 2"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api'

const visible = ref(false)
const loading = ref(false)
const inputText = ref('')
const bodyRef = ref<HTMLDivElement | null>(null)

const messages = ref<{ role: string; content: string }[]>([
  { role: 'ai', content: '你好，我是 AI 助手。有什么可以帮助你的？' }
])

const toggleChat = () => { visible.value = !visible.value }

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || loading.value) return
  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true
  scrollDown()
  try {
    const data = await request.post('/ai/chat', null, { params: { message: text } })
    const reply = typeof data === 'string' ? data : (data.reply || data.t?.reply || data)
    messages.value.push({ role: 'ai', content: reply })
  } catch {
    messages.value.push({ role: 'ai', content: '抱歉，我暂时遇到了一点问题，请稍后重试。' })
  } finally {
    loading.value = false
    scrollDown()
  }
}

const clearMessages = () => {
  messages.value = [
    { role: 'ai', content: '你好，我是 AI 助手。有什么可以帮助你的？' }
  ]
}

const scrollDown = async () => {
  await nextTick()
  if (bodyRef.value) bodyRef.value.scrollTop = bodyRef.value.scrollHeight
}
</script>

<style scoped>
/* ===== 容器 ===== */
.ai-chat-root {
  position: fixed;
  bottom: 28px;
  right: 28px;
  z-index: 9999;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* ===== 触发按钮 ===== */
.ai-trigger {
  position: relative;
  width: 52px;
  height: 52px;
  border: none;
  border-radius: 50%;
  background: #1a1a2e;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 4px 16px rgba(26, 26, 46, 0.25),
    0 0 0 1px rgba(255, 255, 255, 0.06);
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1),
              box-shadow 0.3s ease,
              background 0.3s ease;
  outline: none;
}

.ai-trigger:hover {
  transform: scale(1.06);
  box-shadow:
    0 8px 28px rgba(26, 26, 46, 0.32),
    0 0 0 1px rgba(255, 255, 255, 0.08);
  background: #2a2a4e;
}

.ai-trigger:active {
  transform: scale(0.94);
}

.ai-trigger.is-open {
  background: #dc2626;
  box-shadow: 0 4px 16px rgba(220, 38, 38, 0.25);
}

.ai-trigger.is-open:hover {
  background: #b91c1c;
}

.trigger-icon {
  width: 22px;
  height: 22px;
}

/* ===== 聊天面板 ===== */
.ai-panel {
  position: absolute;
  bottom: 64px;
  right: 0;
  width: 368px;
  height: 540px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow:
    0 24px 64px rgba(0, 0, 0, 0.12),
    0 0 0 1px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ===== 面板头部 ===== */
.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 20px 14px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.head-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.head-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: #1a1a2e;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.head-avatar svg {
  width: 18px;
  height: 18px;
}

.head-meta {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.head-name {
  font-size: 14px;
  font-weight: 600;
  color: #111;
  letter-spacing: -0.01em;
}

.head-status {
  font-size: 11.5px;
  color: #22c55e;
  font-weight: 500;
  letter-spacing: 0.02em;
}

.head-clear {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: none;
  background: transparent;
  color: #9ca3af;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.head-clear:hover {
  background: #f3f4f6;
  color: #6b7280;
}

.head-clear:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

/* ===== 消息区 ===== */
.panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
  background: #fafafa;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.msg {
  display: flex;
  max-width: 82%;
}

.msg.user {
  align-self: flex-end;
}

.msg.ai {
  align-self: flex-start;
}

.msg-bubble {
  padding: 10px 16px;
  font-size: 13.5px;
  line-height: 1.6;
  word-break: break-word;
  border-radius: 18px;
  letter-spacing: -0.005em;
}

.msg.user .msg-bubble {
  background: #1a1a2e;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.msg.ai .msg-bubble {
  background: #fff;
  color: #1f2937;
  border: 1px solid #f0f0f0;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
}

/* 思考动画 */
.thinking {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 14px 22px !important;
}

.dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #d1d5db;
  animation: bounce 1.4s infinite both;
}

.dot:nth-child(2) { animation-delay: 0.16s; }
.dot:nth-child(3) { animation-delay: 0.32s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 0.9; }
}

/* ===== 底栏 ===== */
.panel-foot {
  padding: 12px 16px 16px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
  flex-shrink: 0;
}

.input-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f5f5f5;
  border-radius: 14px;
  padding: 4px 4px 4px 16px;
  transition: background 0.2s ease, box-shadow 0.2s ease;
}

.input-wrap:focus-within {
  background: #fff;
  box-shadow: 0 0 0 1.5px #1a1a2e;
}

.msg-input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 13.5px;
  color: #1f2937;
  padding: 8px 0;
  font-family: inherit;
}

.msg-input::placeholder {
  color: #9ca3af;
}

.send-btn {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  border: none;
  background: #1a1a2e;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.send-btn:hover {
  background: #2a2a4e;
}

.send-btn:active {
  transform: scale(0.92);
}

.send-btn:disabled {
  background: #e5e7eb;
  color: #9ca3af;
  cursor: not-allowed;
}

/* ===== 面板进出动画 ===== */
.panel-enter-active {
  animation: panel-in 0.35s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.panel-leave-active {
  animation: panel-out 0.25s cubic-bezier(0.4, 0, 1, 1) forwards;
}

@keyframes panel-in {
  from {
    opacity: 0;
    transform: translateY(12px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes panel-out {
  from {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translateY(8px) scale(0.96);
  }
}

/* ===== 滚动条 ===== */
.panel-body::-webkit-scrollbar {
  width: 4px;
}

.panel-body::-webkit-scrollbar-track {
  background: transparent;
}

.panel-body::-webkit-scrollbar-thumb {
  background: #e5e7eb;
  border-radius: 4px;
}

.panel-body::-webkit-scrollbar-thumb:hover {
  background: #d1d5db;
}
</style>
