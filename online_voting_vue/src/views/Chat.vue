<template>
  <div class="chat-container">
    <!-- 顶部栏 -->
    <div class="chat-header">
      <el-button icon="ArrowLeft" @click="goBack" />
      <span class="chat-title">{{ friendName || `好友 ${friendId}` }}</span>
      <span class="chat-status" :class="isOnline ? 'online' : 'offline'">
        {{ isOnline ? '在线' : '离线' }}
      </span>
    </div>

    <!-- 消息列表 -->
    <el-scrollbar ref="scrollbarRef" class="chat-messages" @scroll="handleScroll">
      <div v-for="msg in messages" :key="msg.id" class="message-item" :class="{ own: msg.fromUserId === currentUserId }">
        <div class="message-bubble">{{ msg.content }}</div>
        <span class="message-time">{{ formatTime(msg.createTime) }}</span>
      </div>
      <div v-if="loading && pageNum === 1" class="loading-tip">加载中...</div>
      <div v-if="!loading && messages.length === 0" class="empty-tip">暂无消息</div>
    </el-scrollbar>

    <!-- 输入区 -->
    <div class="chat-input">
      <el-input
        v-model="inputText"
        placeholder="输入消息..."
        @keyup.enter="sendMessage"
        :disabled="!isConnected"
      />
      <el-button type="primary" @click="sendMessage" :disabled="!isConnected || !inputText.trim()">
        发送
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getChatHistory, markMessagesRead } from '@/api/chat'
import { getFriendList } from '@/api/friends'

const route = useRoute()
const router = useRouter()
const friendId = Number(route.params.friendId)
const currentUserId = Number(localStorage.getItem('userId') || 0)

// 状态
const friendName = ref('')
const messages = ref<any[]>([])
const inputText = ref('')
const loading = ref(false)
const pageNum = ref(1)
const pageSize = 50
const hasMore = ref(true)
const scrollbarRef = ref()
const isConnected = ref(false)
const ws = ref<WebSocket | null>(null)

const isOnline = computed(() => isConnected.value)

const formatTime = (iso: string) => {
  if (!iso) return ''
  return new Date(iso).toLocaleString('zh-CN', { hour12: false })
}

const loadHistory = async (append = false) => {
  if (loading.value || !hasMore.value) return
  loading.value = true
  try {
    const data = await getChatHistory(friendId, pageNum.value, pageSize)
    const list = data || []
    if (append) {
      messages.value = [...list, ...messages.value]
    } else {
      messages.value = list
    }
    if (list.length < pageSize) hasMore.value = false
    await nextTick()
    if (!append) {
      scrollToBottom()
    }
  } catch (e: any) {
    ElMessage.error(e.message || '加载历史消息失败')
  } finally {
    loading.value = false
  }
}

const scrollToBottom = () => {
  const wrap = scrollbarRef.value?.wrapRef
  if (wrap) {
    scrollbarRef.value.setScrollTop(wrap.scrollHeight)
  }
}

const handleScroll = ({ scrollTop }: { scrollTop: number }) => {
  if (scrollTop === 0 && hasMore.value && !loading.value) {
    pageNum.value++
    loadHistory(true)
  }
}

const sendMessage = () => {
  const text = inputText.value.trim();
  if (!text || !isConnected.value) return;
  // 发送格式：{"toUserId": friendId, "content": text}
  ws.value?.send(JSON.stringify({ toUserId: friendId, content: text }));
  // 乐观更新
  const tempMsg = {
    id: Date.now(),
    fromUserId: currentUserId,
    content: text,
    createTime: new Date().toISOString(),
    isRead: true
  };
  messages.value.push(tempMsg);
  inputText.value = '';
  nextTick(scrollToBottom);
};

const connectWebSocket = () => {
  const token = localStorage.getItem('token');
  if (!token) {
    ElMessage.warning('请先登录');
    return;
  }
  const wsUrl = `ws://localhost:8080/ws/chat?token=${token}`;
  ws.value = new WebSocket(wsUrl);

  ws.value.onopen = () => {
    isConnected.value = true;
    console.log('WebSocket 已连接');
  };

  ws.value.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data);
      // 处理错误消息
      if (data.type === 'error') {
        ElMessage.error(data.message || '消息发送失败');
        return;
      }
      // 处理 ack 确认（可选）
      if (data.type === 'ack') {
        console.log('消息已送达:', data.messageId);
        return;
      }
      // 普通消息（对方发来的）
      if (data.fromUserId && data.fromUserId === friendId) {
        messages.value.push({
          id: Date.now() + Math.random(),
          fromUserId: data.fromUserId,
          content: data.content,
          createTime: data.createTime || new Date().toISOString(),
          isRead: false
        });
        nextTick(scrollToBottom);
        // 自动标记已读
        markMessagesRead(friendId);
      }
    } catch (e) {
      console.error('解析消息失败', e);
    }
  };

  ws.value.onerror = (error) => {
    console.error('WebSocket 错误:', error);
    ElMessage.error('连接异常，请刷新重试');
  };

  ws.value.onclose = () => {
    isConnected.value = false;
    console.log('WebSocket 已断开');
    // 可尝试重连
  };
};

const fetchFriendInfo = async () => {
  try {
    const list = await getFriendList()
    const friend = list.find((f: any) => f.friendId === friendId)
    if (friend) friendName.value = friend.username || friend.remark || `好友${friendId}`
  } catch (e) {
    console.warn('获取好友信息失败')
  }
}

const markRead = async () => {
  try {
    await markMessagesRead(friendId)
  } catch (e) {
    console.warn('标记已读失败')
  }
}

const goBack = () => router.back()

onMounted(async () => {
  await fetchFriendInfo()
  await loadHistory()
  markRead()
  connectWebSocket()
})

onBeforeUnmount(() => {
  if (ws.value) {
    ws.value.close()
    ws.value = null
  }
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 80px);
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  overflow: hidden;
}
.chat-header {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}
.chat-title {
  flex: 1;
  font-weight: 600;
  font-size: 16px;
  margin-left: 8px;
}
.chat-status {
  font-size: 13px;
  padding: 2px 10px;
  border-radius: 12px;
}
.online {
  color: #67c23a;
  background: #e1f3d8;
}
.offline {
  color: #909399;
  background: #f0f0f0;
}
.chat-messages {
  flex: 1;
  padding: 16px 20px;
  background: #f7f8fa;
}
.message-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
  max-width: 75%;
}
.message-item.own {
  align-self: flex-end;
  align-items: flex-end;
}
.message-bubble {
  padding: 10px 16px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  word-break: break-word;
  line-height: 1.5;
}
.message-item.own .message-bubble {
  background: #409eff;
  color: #fff;
}
.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
.loading-tip, .empty-tip {
  text-align: center;
  color: #999;
  padding: 20px 0;
}
.chat-input {
  display: flex;
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
  gap: 10px;
  align-items: center;
}
.chat-input .el-input {
  flex: 1;
}
</style>