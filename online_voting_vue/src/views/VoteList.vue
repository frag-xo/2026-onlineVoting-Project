<template>
  <div class="vote-container">
    <div class="hero-section">
      <h1 class="hero-title">在线投票</h1>
      <p class="hero-subtitle">参与投票，表达你的观点</p>
    </div>

    <!-- 统计横幅 -->
    <div class="stats-banner">
      <div class="stat-card stat-active">
        <span class="stat-num">{{ activeCount }}</span>
        <span class="stat-label">进行中</span>
      </div>
      <div class="stat-card stat-ended">
        <span class="stat-num">{{ endedCount }}</span>
        <span class="stat-label">已结束</span>
      </div>
      <div class="stat-card stat-total">
        <span class="stat-num">{{ voteList.length }}</span>
        <span class="stat-label">共加载</span>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-area">
      <div class="filter-left">
        <el-input
            v-model="searchKeyword"
            placeholder="输入投票标题搜索..."
            clearable
            size="default"
            style="width: 240px; margin-right: 12px;"
            @keyup.enter="loadVotes"
            @clear="loadVotes"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <div class="filter-group">
          <button
              v-for="tab in filterTabs"
              :key="tab.value"
              :class="['filter-tag', { active: statusFilter === tab.value }]"
              @click="statusFilter = tab.value; loadVotes()"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <div class="sort-group">
        <el-select v-model="sortBy" placeholder="排序方式" @change="loadVotes" class="filter-select">
          <el-option label="截止时间 ↑" value="endTime-asc" />
          <el-option label="截止时间 ↓" value="endTime-desc" />
          <el-option label="最新发布" value="createTime-desc" />
          <el-option label="最早发布" value="createTime-asc" />
        </el-select>
      </div>
    </div>

    <div v-loading="loading" class="vote-grid">
      <template v-if="voteList.length > 0">
        <el-card
            v-for="(item, index) in voteList"
            :key="item.id"
            class="vote-card"
            shadow="hover"
            :style="{ animationDelay: (index * 0.05) + 's' }"
        >
          <div class="card-header">
            <h3>{{ item.title }}</h3>
            <el-tag :type="item.status === '进行中' ? 'success' : item.status === '已结束' ? 'info' : 'warning'">
              {{ item.status }}
            </el-tag>
          </div>
          <div class="card-body">
            <div class="vote-meta">
              <span class="meta-item meta-users">{{ item.totalVotes || 0 }} 人参与</span>
              <span class="meta-item meta-time">{{ item.deadline ? item.deadline.slice(0, 10) : '长期有效' }}</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: Math.min(100, (item.totalVotes || 0) * 5) + '%' }"></div>
            </div>
            <div class="card-actions">
              <el-button class="action-btn primary" size="small" @click="goDetail(item.id)">投票</el-button>
              <el-button class="action-btn ghost" size="small" @click="goResult(item.id)">结果</el-button>
              <el-dropdown trigger="click" @command="(cmd: string) => handleShare(item.id, cmd)">
                <el-button class="action-btn share-btn" size="small">
                  <span style="font-size:15px;line-height:1;">⋯</span>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="link">🔗 复制链接</el-dropdown-item>
                    <el-dropdown-item command="qrcode">📱 二维码</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-card>
      </template>
      <el-empty v-else description="暂无投票" />
    </div>

    <!-- 分享二维码弹窗 -->
    <el-dialog v-model="qrDialogVisible" title="扫二维码参与投票" width="360px" :close-on-click-modal="true">
      <div class="qr-container">
        <img v-if="qrImageUrl" :src="qrImageUrl" class="qr-image" alt="投票二维码" />
        <p class="qr-hint">扫码或长按识别二维码参与投票</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getVoteList } from '@/api/vote'
import request from '@/api/index'
import { getToken } from '@/utils/auth'

const router = useRouter()

// ============================================================
// 数据定义
// ============================================================

interface VoteItem {
  id: number
  title: string
  status: string
  totalVotes: number
  deadline: string
  options: any[]
}

const voteList = ref<VoteItem[]>([])
const loading = ref(false)
const statusFilter = ref<number | string>('')
const sortBy = ref('endTime-asc')
const searchKeyword = ref('')

const filterTabs = [
  { label: '全部', value: '' },
  { label: '进行中', value: 1 },
  { label: '未开始', value: 0 },
  { label: '已结束', value: 2 }
]

// ============================================================
// 计算属性
// ============================================================

const activeCount = computed(() => voteList.value.filter(v => v.status === '进行中').length)
const endedCount = computed(() => voteList.value.filter(v => v.status === '已结束').length)

// ============================================================
// 加载投票列表
// ============================================================

const loadVotes = async () => {
  loading.value = true
  try {
    const [orderBy, orderDirection] = sortBy.value.split('-')
    const params: any = {
      pageNum: 1,
      pageSize: 100,
      title: searchKeyword.value || undefined
    }
    if (statusFilter.value !== '') {
      params.status = statusFilter.value
    }
    if (orderBy) {
      params.orderBy = orderBy
    }
    if (orderDirection) {
      params.orderDirection = orderDirection
    }

    console.log('📤 请求参数:', params)

    const data: any = await getVoteList(params)

    // 兼容多种返回格式
    let records = data
    if (data && data.records) {
      records = data.records
    } else if (Array.isArray(data)) {
      records = data
    } else if (data && data.tList) {
      records = data.tList
    } else if (data && data.t) {
      records = data.t
    }

    if (!records || !Array.isArray(records)) {
      voteList.value = []
      return
    }

    const statusMap: Record<number, string> = {
      0: '未开始',
      1: '进行中',
      2: '已结束'
    }

    voteList.value = records.map((item: any) => ({
      id: item.id,
      title: item.title || '未命名投票',
      status: statusMap[item.status] || '未知',
      totalVotes: item.totalVoters || item.totalVotes || 0,
      deadline: item.endTime || item.deadline || '',
      options: item.options || []
    }))

    console.log('✅ 加载完成，共', voteList.value.length, '条')
  } catch (error: any) {
    console.error('❌ 加载投票列表失败:', error)
    // 不弹窗错误，静默失败
    voteList.value = []
  } finally {
    loading.value = false
  }
}

// ============================================================
// 分享功能
// ============================================================

const qrDialogVisible = ref(false)
const qrImageUrl = ref('')
const shareVoteId = ref(0)

const handleShare = async (id: number, cmd: string) => {
  const baseUrl = window.location.origin
  if (cmd === 'link') {
    try {
      const res: any = await request.get(`/vote/share/link/${id}`, { params: { baseUrl } })
      const link = typeof res === 'string' ? res : JSON.stringify(res)
      await navigator.clipboard.writeText(link)
      ElMessage.success('链接已复制到剪贴板')
    } catch {
      ElMessage.error('生成分享链接失败')
    }
  } else if (cmd === 'qrcode') {
    try {
      const token = getToken()
      const resp = await fetch(`http://localhost:8080/api/vote/share/qrcode/${id}?baseUrl=${encodeURIComponent(baseUrl)}&width=300&height=300`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      if (!resp.ok) throw new Error('请求失败')
      const blob = await resp.blob()
      qrImageUrl.value = URL.createObjectURL(blob)
      shareVoteId.value = id
      qrDialogVisible.value = true
    } catch {
      ElMessage.error('生成二维码失败')
    }
  }
}

// ============================================================
// 跳转
// ============================================================

const goDetail = (id: number) => {
  router.push(`/detail/${id}`)
}

const goResult = (id: number) => {
  router.push(`/result/${id}`)
}

// ============================================================
// 生命周期
// ============================================================

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

.hero-section {
  text-align: center;
  margin-bottom: 28px;
  padding: 32px 0 16px;
}

.hero-title {
  font-family: 'Outfit', sans-serif;
  font-size: 36px;
  font-weight: 700;
  color: #1a1a2e;
  letter-spacing: -0.03em;
  margin-bottom: 8px;
}

.hero-subtitle {
  color: #8e8ea0;
  font-size: 16px;
}

.stats-banner {
  display: flex;
  gap: 14px;
  margin-bottom: 28px;
  justify-content: center;
}

.stat-card {
  flex: 1;
  max-width: 180px;
  padding: 18px 24px;
  border-radius: 14px;
  text-align: center;
  border: 1px solid;
}

.stat-active {
  background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
  border-color: #bae6fd;
}

.stat-ended {
  background: linear-gradient(135deg, #f5f5f5, #fafafa);
  border-color: #e5e5e5;
}

.stat-total {
  background: linear-gradient(135deg, #f0f0ff, #e8e8ff);
  border-color: #d4d4ff;
}

.stat-num {
  display: block;
  font-family: 'Outfit', sans-serif;
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #8e8ea0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.filter-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-left {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  gap: 6px;
  background: #f0f0f3;
  padding: 4px;
  border-radius: 10px;
}

.filter-tag {
  padding: 8px 18px;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  background: transparent;
  color: #6c6c80;
  transition: all 0.2s;
  font-family: 'Inter', sans-serif;
}

.filter-tag:hover {
  color: #1a1a2e;
}

.filter-tag.active {
  background: #fff;
  color: #4361ee;
  box-shadow: 0 2px 8px rgba(67, 97, 238, 0.12);
}

.filter-select {
  width: 150px;
}

:deep(.el-input__wrapper) {
  border-radius: 10px !important;
  border: 1px solid #e8e8ed !important;
  box-shadow: none !important;
}

:deep(.el-input__wrapper:hover) {
  border-color: #4361ee !important;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #4361ee !important;
  box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.1) !important;
}

.vote-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 18px;
}

.vote-card {
  border-radius: 14px !important;
  border: 1px solid #f0f0f0 !important;
  transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1) !important;
  cursor: default;
  background: #fff !important;
  opacity: 0;
  animation: fadeUp 0.6s cubic-bezier(.34,1.56,.64,1) forwards;
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.vote-card:hover {
  transform: translateY(-4px) !important;
  box-shadow: 0 20px 40px -12px rgba(0, 0, 0, 0.1) !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
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
  margin-bottom: 14px;
}

.meta-item {
  font-size: 13px;
  color: #8e8ea0;
}

.meta-users {
  font-weight: 500;
  color: #6c6c80;
}

.progress-bar {
  height: 4px;
  background: #f0f0f3;
  border-radius: 2px;
  margin-bottom: 14px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #4361ee, #725bff);
  border-radius: 2px;
  transition: width 0.65s cubic-bezier(0.16, 1, 0.3, 1);
}

.card-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  flex: 1;
  border-radius: 10px !important;
  font-weight: 500 !important;
  font-size: 13px !important;
  padding: 8px 0 !important;
}

.action-btn.primary {
  background: #4361ee !important;
  border-color: #4361ee !important;
  color: #fff !important;
}

.action-btn.primary:hover {
  background: #3651d4 !important;
  transform: scale(1.02);
}

.action-btn.ghost {
  border: 1.5px solid #e0e0e5 !important;
  color: #6c6c80 !important;
  background: transparent !important;
}

.action-btn.ghost:hover {
  border-color: #4361ee !important;
  color: #4361ee !important;
}

.share-btn {
  width: 36px !important;
  min-width: 36px !important;
  padding: 0 !important;
  border: 1.5px solid #e0e0e5 !important;
  color: #8e8ea0 !important;
  background: transparent !important;
  border-radius: 10px !important;
}

.share-btn:hover {
  border-color: #4361ee !important;
  color: #4361ee !important;
  background: #f0f2ff !important;
}

.qr-container {
  text-align: center;
  padding: 16px;
}

.qr-image {
  width: 240px;
  height: 240px;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.qr-hint {
  margin-top: 16px;
  color: #8e8ea0;
  font-size: 14px;
}
</style>