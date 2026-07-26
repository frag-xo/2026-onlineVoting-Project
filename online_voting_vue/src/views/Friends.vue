<template>
  <div class="friends-container">
    <h2>👥 好友管理</h2>

    <!-- 搜索添加好友 -->
    <div class="search-add">
      <el-input
        v-model="searchUserId"
        placeholder="输入用户ID添加好友"
        style="width: 240px; margin-right: 12px;"
        clearable
      />
      <el-button type="primary" @click="sendFriendRequest">发送申请</el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="我的好友" name="friends">
        <el-table :data="friendList" stripe v-loading="loading">
          <el-table-column label="头像" width="80">
            <template #default="{ row }">
              <el-avatar :size="40" :src="row.avatar || ''">
                {{ row.username?.charAt(0) || '?' }}
              </el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="username" label="昵称" />
          <el-table-column prop="remark" label="备注" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="goChat(row.friendId)">发消息</el-button>
              <el-button size="small" type="danger" @click="removeFriend(row.friendId)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loading && friendList.length === 0" description="还没有好友，快去添加吧" />
      </el-tab-pane>

      <el-tab-pane label="好友申请" name="requests">
        <el-badge :value="pendingCount" class="badge" v-if="pendingCount > 0" />
        <el-table :data="requestList" stripe v-loading="loading">
          <el-table-column prop="fromUsername" label="申请人" />
          <el-table-column prop="createTime" label="申请时间" width="180" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="acceptRequest(row.id)">同意</el-button>
              <el-button size="small" type="danger" @click="rejectRequest(row.id)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loading && requestList.length === 0" description="暂无好友申请" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getFriendList,
  getFriendRequests,
  sendFriendRequest as apiSendRequest,
  acceptFriendRequest,
  rejectFriendRequest,
  deleteFriend
} from '@/api/friends'

const router = useRouter()
const activeTab = ref('friends')
const loading = ref(false)
const friendList = ref<any[]>([])
const requestList = ref<any[]>([])
const searchUserId = ref('')

const pendingCount = computed(() => requestList.value.length)

const loadFriends = async () => {
  try {
    const data = await getFriendList()
    friendList.value = data || []
  } catch (e: any) {
    ElMessage.error(e.message || '加载好友列表失败')
  }
}

const loadRequests = async () => {
  try {
    const data = await getFriendRequests()
    requestList.value = data || []
  } catch (e: any) {
    ElMessage.error(e.message || '加载申请列表失败')
  }
}

const loadData = () => {
  loading.value = true
  Promise.all([loadFriends(), loadRequests()]).finally(() => {
    loading.value = false
  })
}

// 发送好友申请
const sendFriendRequest = async () => {
  const id = Number(searchUserId.value.trim())
  if (!id) {
    ElMessage.warning('请输入有效的用户ID')
    return
  }
  try {
    await apiSendRequest(id)
    ElMessage.success('申请已发送')
    searchUserId.value = ''
  } catch (e: any) {
    ElMessage.error(e.message || '发送失败')
  }
}

// 同意申请
const acceptRequest = async (relationId: number) => {
  try {
    await acceptFriendRequest(relationId)
    ElMessage.success('已添加好友')
    await loadRequests()
    await loadFriends()
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  }
}

// 拒绝申请
const rejectRequest = async (relationId: number) => {
  try {
    await rejectFriendRequest(relationId)
    ElMessage.success('已拒绝')
    await loadRequests()
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  }
}

// 删除好友
const removeFriend = async (friendId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该好友吗？', '提示', { type: 'warning' })
    await deleteFriend(friendId)
    ElMessage.success('已删除')
    await loadFriends()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}

// 跳转聊天
const goChat = (friendId: number) => {
  router.push(`/chat/${friendId}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.friends-container {
  max-width: 900px;
  margin: 24px auto;
  padding: 0 20px;
}
.search-add {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.badge {
  margin-left: 12px;
}
</style>