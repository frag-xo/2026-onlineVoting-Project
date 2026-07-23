<template>
  <div class="vote-container">
    <div class="page-header">
      <h1>在线投票</h1>
      <p class="page-subtitle">参与投票，表达你的观点</p>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-area">
      <div class="filter-group">
        <el-select v-model="statusFilter" placeholder="投票状态" clearable @change="loadVotes" class="filter-select">
          <el-option label="全部投票" value="" />
          <el-option label="进行中" :value="1" />
          <el-option label="未开始" :value="0" />
          <el-option label="已结束" :value="2" />
        </el-select>
        <el-select v-model="sortBy" placeholder="排序方式" @change="loadVotes" class="filter-select">
          <el-option label="按截止时间（早→晚）" value="endTime-asc" />
          <el-option label="按截止时间（晚→早）" value="endTime-desc" />
          <el-option label="按创建时间（新→旧）" value="createTime-desc" />
          <el-option label="按创建时间（旧→新）" value="createTime-asc" />
        </el-select>
      </div>
    </div>

    <div v-loading="loading" class="vote-grid">
      <template v-if="voteList.length > 0">
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
      </template>
      <el-empty v-else description="暂无投票" />
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
const isAdmin = ref(false)
const statusFilter = ref<number | ''>('')
const sortBy = ref('endTime-asc')

const loadVotes = async () => {
  loading.value = true
  try {
    // 解析排序参数
    const [orderBy, orderDirection] = sortBy.value.split('-')

    // 构建查询参数
    const params: any = {
      pageNum: 1,
      pageSize: 100,
      orderBy,
      orderDirection
    }

    // 添加状态筛选
    if (statusFilter.value !== '') {
      params.status = statusFilter.value
    }

    const data = await getVoteList(params)
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
  const utype = localStorage.getItem('utype')
  isAdmin.value = utype === 'ROLE_1'
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
.page-header {
  text-align: center;
  margin-bottom: 28px;
}

.page-header h1 {
  font-size: 30px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 6px;
  font-family: 'Outfit', sans-serif;
  letter-spacing: -0.02em;
}

.page-subtitle {
  color: #909399;
  font-size: 15px;
}

.filter-area {
  margin-bottom: 24px;
}

.filter-group {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.filter-select {
  width: 200px;
}
.vote-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.vote-card {
  border-radius: 14px !important;
  border: 1px solid #f0f0f0 !important;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1) !important;
  cursor: default;
  background: #fff !important;
}

.vote-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 32px -8px rgba(0, 0, 0, 0.08), 0 4px 8px -2px rgba(0, 0, 0, 0.02) !important;
  border-color: #d4d4d4 !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding-right: 10px;
}

.vote-meta {
  display: flex;
  justify-content: space-between;
  color: #909399;
  font-size: 13px;
  margin-bottom: 18px;
}

.card-actions {
  display: flex;
  gap: 10px;
  padding-top: 4px;
  border-top: 1px solid #f0f2f5;
}
</style>