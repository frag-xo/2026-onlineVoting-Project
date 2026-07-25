<template>
  <div class="ai-chat-container">
    <!-- 悬浮按钮 -->
    <el-button
      class="ai-float-btn"
      :class="{ 'is-active': visible }"
      @click="toggleChat"
      circle
    >
      <span class="btn-icon">{{ visible ? '✕' : '🤖' }}</span>
    </el-button>

    <!-- 聊天窗口 -->
    <transition name="chat-slide">
      <div v-if="visible" class="ai-chat-window">
        <div class="chat-header">
          <div class="header-left">
            <span class="header-icon">🤖</span>
            <span class="header-title">AI 小助手</span>
          </div>
          <el-button text size="small" @click="clearMessages" :disabled="messages.length <= 1">
            清空
          </el-button>
        </div>

        <div class="chat-body" ref="chatBodyRef">
          <div
            v-for="(msg, i) in messages"
            :key="i"
            class="msg-row"
            :class="msg.role === 'user' ? 'msg-user' : 'msg-ai'"
          >
            <div class="msg-bubble">{{ msg.content }}</div>
          </div>
          <div v-if="loading" class="msg-row msg-ai">
            <div class="msg-bubble msg-thinking">
              <span class="dot-pulse"></span>
            </div>
          </div>
        </div>

        <div class="chat-footer">
          <el-input
            v-model="inputText"
            placeholder="输入你的问题..."
            size="small"
            class="chat-input"
            @keyup.enter="sendMessage"
            :disabled="loading"
          />
          <el-button type="primary" size="small" @click="sendMessage" :loading="loading" class="send-btn">
            发送
          </el-button>
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
const chatBodyRef = ref<HTMLDivElement | null>(null)

const messages = ref<{ role: string; content: string }[]>([
  { role: 'ai', content: '你好呀！我是 Online_Voting 小助手，有什么可以帮你的吗？😊' }
])

const toggleChat = () => {
  visible.value = !visible.value
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const data = await request.post('/ai/chat', null, {
      params: { message: text }
    })
    const reply = typeof data === 'string' ? data : (data.reply || data.t?.reply || data)
    messages.value.push({ role: 'ai', content: reply })
  } catch {
    messages.value.push({ role: 'ai', content: '小助手暂时开小差了，稍后再试试吧 😅' })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const clearMessages = () => {
  messages.value = [
    { role: 'ai', content: '你好呀！我是 Online_Voting 小助手，有什么可以帮你的吗？😊' }
  ]
}

const scrollToBottom = async () => {
  await nextTick()
  if (chatBodyRef.value) {
    chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
  }
}
</script>

<style scoped>
.ai-chat-container {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 9999;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

.ai-float-btn {
  width: 52px !important;
  height: 52px !important;
  background: linear-gradient(135deg, #4361ee, #725bff) !important;
  border: none !important;
  box-shadow: 0 6px 24px rgba(67, 97, 238, 0.35) !important;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
  position: relative;
}

.ai-float-btn:hover {
  transform: scale(1.08);
  box-shadow: 0 8px 32px rgba(67, 97, 238, 0.5) !important;
}

.ai-float-btn.is-active {
  background: linear-gradient(135deg, #e74c3c, #c0392b) !important;
  box-shadow: 0 6px 24px rgba(231, 76, 60, 0.35) !important;
}

.btn-icon {
  font-size: 20px;
  line-height: 1;
}

/* 聊天窗口 */
.ai-chat-window {
  position: absolute;
  bottom: 64px;
  right: 0;
  width: 360px;
  height: 520px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid #f0f0f0;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: linear-gradient(135deg, #4361ee, #725bff);
  color: #fff;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  font-size: 18px;
}

.header-title {
  font-weight: 600;
  font-size: 15px;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
  background: #f8f9fc;
}

.msg-row {
  margin-bottom: 12px;
  display: flex;
}

.msg-user {
  justify-content: flex-end;
}

.msg-bubble {
  max-width: 80%;
  padding: 10px 14px;
  border-radius: 14px;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
}

.msg-user .msg-bubble {
  background: #4361ee;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.msg-ai .msg-bubble {
  background: #fff;
  color: #303133;
  border: 1px solid #ebeef5;
  border-bottom-left-radius: 4px;
}

.msg-thinking {
  padding: 14px 20px !important;
}

.dot-pulse {
  display: inline-block;
  width: 8px;
  height: 8px;
  background: #4361ee;
  border-radius: 50%;
  animation: pulse 1.2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; transform: scale(0.8); }
  50% { opacity: 1; transform: scale(1.2); }
}

.chat-footer {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
}

.chat-input {
  flex: 1;
}

.chat-input :deep(.el-input__inner) {
  border-radius: 10px !important;
  font-size: 13px;
}

.send-btn {
  border-radius: 10px !important;
  font-weight: 500 !important;
}

/* 动画 */
.chat-slide-enter-active,
.chat-slide-leave-active {
  transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.chat-slide-enter-from,
.chat-slide-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}
</style>
