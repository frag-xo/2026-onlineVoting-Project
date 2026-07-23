<template>
  <div class="vote-container">
    <div class="page-header">
      <h1>📊 在线投票系统</h1>
      <p>参与投票，表达你的观点</p>
      <el-button type="primary" @click="goAdmin" style="margin-top: 10px;">⚙️ 后台管理</el-button>
    </div>

    <div v-loading="loading" class="vote-grid">
      <el-card 
        v-for="item in voteList" 
        :key="item.id" 
        class="vote-card"
        shadow="hover"
      >
        <div class="card-header">
          <h3>{{ item.title }}</h3>
          <el-tag :type="item.status === '进行中' ? 'success' : item.status === '已结束' ? 'info' : 'warning'">
            {{ item.status }}
          </el-tag>
        </div>
        <div class="card-body">
          <div class="vote-meta">
            <span>👥 参与人数：{{ item.totalVotes }}</span>
            <span>⏰ 截止：{{ item.deadline }}</span>
          </div>
          <div class="card-actions">
            <el-button type="primary" size="small" @click="goDetail(item.id)">查看详情</el-button>
            <el-button type="warning" size="small" plain @click="goResult(item.id)">查看结果</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVoteList } from '@/api/vote'

const router = useRouter()
const voteList = ref<any[]>([])
const loading = ref(false)

const loadVotes = async () => {
  loading.value = true
  try {
    const data = await getVoteList(1, 100)
    const records = data.records || data || []
    voteList.value = records.map((item: any) => ({
      id: item.id,
      title: item.title,
      status: item.statusText || (item.status === 1 ? '进行中' : item.status === 2 ? '已结束' : '未开始'),
      totalVotes: item.totalVoters || 0,
      deadline: item.endTime,
      options: item.options || []
    }))
  } catch (error: any) {
    ElMessage.error(error.message || '加载投票列表失败')
  } finally {
    loading.value = false
  }
}

const goDetail = (id: number) => {
  router.push(`/detail/${id}`)
}

const goResult = (id: number) => {
  router.push(`/result/${id}`)
}

const goAdmin = () => {
  router.push('/admin')
}

onMounted(() => {
  loadVotes()
})
</script>

<style scoped>
.vote-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.page-header {
  text-align: center;
  margin-bottom: 30px;
}
.page-header h1 {
  font-size: 32px;
  color: #303133;
}
.page-header p {
  color: #909399;
  font-size: 16px;
}
.vote-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}
.vote-card:hover {
  transform: translateY(-4px);
  transition: transform 0.2s;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.card-header h3 {
  margin: 0;
  font-size: 18px;
}
.vote-meta {
  display: flex;
  justify-content: space-between;
  color: #606266;
  font-size: 14px;
  margin-bottom: 16px;
}
.card-actions {
  display: flex;
  gap: 10px;
}
</style>