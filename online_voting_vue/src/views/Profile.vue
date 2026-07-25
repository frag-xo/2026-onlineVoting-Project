<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 左侧：用户信息 -->
      <el-col :span="6">
        <el-card class="profile-card">
          <div class="user-info">
            <!-- 头像 -->
            <div class="avatar-wrapper">
              <el-avatar
                :size="80"
                :src="avatarUrl"
                class="user-avatar"
                @click="triggerUpload"
              >
                {{ !avatarUrl ? '👤' : '' }}
              </el-avatar>
              <div class="avatar-hint" @click="triggerUpload">
                <el-icon><Camera /></el-icon>
                <span>换头像</span>
              </div>
              <el-upload
                ref="uploadRef"
                class="avatar-upload"
                :show-file-list="false"
                :auto-upload="false"
                :on-change="handleAvatarChange"
                accept="image/*"
              >
                <input type="file" style="display:none" />
              </el-upload>
            </div>

            <div class="username-wrapper">
              <h3>{{ displayName }}</h3>
              <el-button type="primary" link size="small" @click="showEditNameDialog" class="edit-name-btn">
                <el-icon><Edit /></el-icon> 修改
              </el-button>
            </div>
            <p class="role">{{ isAdmin ? '管理员' : '普通用户' }}</p>
            <p class="level-tag">{{ levelInfo.icon }} {{ levelInfo.name }}</p>

            <div class="stats">
              <div>
                <span class="num">{{ stats.totalVotes }}</span>
                <span class="label">参与投票</span>
              </div>
              <div>
                <span class="num">{{ stats.totalFav }}</span>
                <span class="label">收藏</span>
              </div>
              <div>
                <span class="num">{{ stats.points }}</span>
                <span class="label">积分</span>
              </div>
            </div>

            <div style="margin-top: 16px;">
              <el-button size="small" type="warning" plain @click="showRulesDialog = true" style="width: 100%;">
                📖 积分规则
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧 -->
      <el-col :span="18">
        <el-card class="profile-card">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="我的投票记录" name="records">
              <el-table :data="myVotes" stripe v-loading="loading">
                <el-table-column prop="title" label="投票标题" />
                <el-table-column prop="choice" label="我的选择" />
                <el-table-column prop="result" label="最终结果" />
                <el-table-column prop="createTime" label="参与时间" width="180" />
                <el-table-column label="操作" width="120">
                  <template #default="{ row }">
                    <el-button size="small" @click="viewResult(row.id)">查看结果</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="我的收藏" name="favorites">
              <el-table :data="favorites" stripe v-loading="favLoading">
                <el-table-column prop="title" label="投票标题" />
                <el-table-column prop="statusText" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.statusText === '进行中' ? 'success' : 'info'">{{ row.statusText }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="endTime" label="截止时间" width="180" />
                <el-table-column label="操作" width="160">
                  <template #default="{ row }">
                    <el-button size="small" @click="viewDetail(row.id)">查看</el-button>
                    <el-button size="small" type="danger" @click="unfav(row.id)">取消</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="我发布的投票" name="myPublish">
              <el-table :data="myPublish" stripe v-loading="publishLoading">
                <el-table-column prop="title" label="投票标题" />
                <el-table-column prop="auditStatus" label="审核状态" width="120">
                  <template #default="{ row }">
                    <el-tag :type="row.auditStatus === '待审核' ? 'warning' : row.auditStatus === '已通过' ? 'success' : 'danger'">
                      {{ row.auditStatus }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="statusText" label="发布状态" width="120">
                  <template #default="{ row }">
                    <el-tag :type="row.statusText === '进行中' ? 'success' : 'info'">{{ row.statusText }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="提交时间" width="180" />
                <el-table-column label="操作" width="120">
                  <template #default="{ row }">
                    <el-button size="small" @click="viewDetail(row.id)" v-if="row.auditStatus === '已通过'">查看</el-button>
                    <el-button size="small" disabled v-else-if="row.auditStatus === '待审核'">待审核</el-button>
                    <el-button size="small" disabled v-else>已拒绝</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="审核投票" name="audit" v-if="isAdmin">
              <div class="audit-header">
                <span class="audit-info">待审核投票数：{{ pendingAuditList.length }}</span>
                <el-button size="small" @click="loadPendingAudits" :loading="auditLoading">刷新</el-button>
              </div>
              <el-table :data="pendingAuditList" stripe v-loading="auditLoading">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="title" label="投票标题" />
                <el-table-column prop="creatorName" label="发布人" width="120" />
                <el-table-column prop="createTime" label="提交时间" width="180" />
                <el-table-column prop="optionsCount" label="选项数" width="80">
                  <template #default="{ row }">{{ row.optionsCount || 0 }}</template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                  <template #default="{ row }">
                    <el-button size="small" type="success" @click="approveVote(row.id)">通过</el-button>
                    <el-button size="small" type="danger" @click="rejectVote(row.id)">拒绝</el-button>
                    <el-button size="small" @click="viewDetail(row.id)">预览</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="pendingAuditList.length === 0" description="暂无待审核投票 🎉" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改昵称对话框 -->
    <el-dialog v-model="editNameDialogVisible" title="修改昵称" width="400px">
      <el-form>
        <el-form-item label="新昵称">
          <el-input v-model="newName" placeholder="请输入新昵称" maxlength="20" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editNameDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmEditName" :loading="nameSaving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 头像预览对话框 -->
    <el-dialog v-model="avatarPreviewVisible" title="更换头像" width="400px">
      <div class="avatar-preview-area">
        <el-avatar :size="120" :src="tempAvatarUrl" class="preview-avatar" />
        <p style="color:#909399;font-size:14px;margin-top:12px;">点击下方按钮上传新头像</p>
        <el-upload
          class="avatar-upload-btn"
          :show-file-list="false"
          :auto-upload="false"
          :on-change="handleAvatarConfirm"
          accept="image/*"
        >
          <el-button type="primary">选择图片</el-button>
        </el-upload>
        <el-button
          v-if="tempAvatarUrl && tempAvatarUrl !== avatarUrl"
          type="success"
          @click="saveAvatar"
          style="margin-top:12px;"
          :loading="avatarSaving"
        >
          确认保存
        </el-button>
      </div>
    </el-dialog>

    <!-- 积分规则弹窗 -->
    <el-dialog v-model="showRulesDialog" title="📖 积分规则" width="580px">
      <div class="rules-container">
        <h4>🎯 获取积分</h4>
        <el-table :data="earnRules" border size="small" style="margin-bottom:16px;">
          <el-table-column prop="action" label="行为" />
          <el-table-column prop="points" label="积分" width="80" align="center" />
          <el-table-column prop="limit" label="每日上限" width="120" align="center" />
        </el-table>
        <h4>💎 消耗积分</h4>
        <el-table :data="spendRules" border size="small" style="margin-bottom:16px;">
          <el-table-column prop="action" label="行为" />
          <el-table-column prop="points" label="积分" width="80" align="center" />
          <el-table-column prop="note" label="说明" />
        </el-table>
        <h4>🏅 等级体系</h4>
        <el-table :data="levelRules" border size="small">
          <el-table-column prop="level" label="等级" width="80" align="center" />
          <el-table-column prop="icon" label="图标" width="60" align="center" />
          <el-table-column prop="name" label="称号" />
          <el-table-column prop="required" label="所需积分" width="120" align="center" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Camera, Edit } from '@element-plus/icons-vue'
import {
  getVoteHistory,
  getFavorites,
  getMyPoints,
  getPendingAudits,
  auditVote,
  updateUsername,
  uploadAvatar,
  unfavoriteVote,
  getVoteList,
  getVoteDetail
} from '@/api/vote'

const router = useRouter()
const loading = ref(false)
const favLoading = ref(false)
const publishLoading = ref(false)
const auditLoading = ref(false)
const activeTab = ref('records')

const userId = ref(Number(localStorage.getItem('userId') || 0))
const username = ref(localStorage.getItem('username') || '用户')
const isAdmin = ref(localStorage.getItem('utype') === 'ROLE_1')
const avatarUrl = ref(localStorage.getItem('avatar') || '')

const displayName = computed(() => username.value)

// 积分规则
const showRulesDialog = ref(false)

const levelInfo = computed(() => {
  const points = stats.value.points || 0
  const levels = [
    { level: 1, icon: '🍃', name: '初来乍到', required: 0 },
    { level: 2, icon: '🌱', name: '热心市民', required: 50 },
    { level: 3, icon: '🌿', name: '活跃分子', required: 100 },
    { level: 4, icon: '🌳', name: '投票达人', required: 200 },
    { level: 5, icon: '🌲', name: '意见领袖', required: 500 },
    { level: 6, icon: '👑', name: '投票之王', required: 1000 }
  ]
  let current = levels[0]
  for (const l of levels) {
    if (points >= l.required) current = l
  }
  return current
})

// 获取积分规则（与截图一致）
const earnRules = [
  { action: '注册', points: '+20', limit: '一次性' },
  { action: '每日登录', points: '+10', limit: '每天1次' },
  { action: '参与投票', points: '+5', limit: '每天最多20次' },
  { action: '评论', points: '+2', limit: '每天最多10次' },
  { action: '评论被点赞', points: '+1', limit: '无限制' },
  { action: '收藏投票', points: '+3', limit: '最多10次' },
  { action: '发布投票被参与', points: '+1', limit: '无限制' },
  { action: '投票被推荐', points: '+50', limit: '无限制' }
]

// 消费积分规则（与截图一致）
const spendRules = [
  { action: '发布投票', points: '-10', note: '普通用户发布消耗' },
  { action: '置顶投票', points: '-50', note: '投票置顶展示' },
  { action: '匿名投票', points: '-5', note: '匿名发布' }
]

const levelRules = [
  { level: 'Lv.1', icon: '🍃', name: '初来乍到', required: 0 },
  { level: 'Lv.2', icon: '🌱', name: '热心市民', required: 50 },
  { level: 'Lv.3', icon: '🌿', name: '活跃分子', required: 100 },
  { level: 'Lv.4', icon: '🌳', name: '投票达人', required: 200 },
  { level: 'Lv.5', icon: '🌲', name: '意见领袖', required: 500 },
  { level: 'Lv.6', icon: '👑', name: '投票之王', required: 1000 }
]

const stats = ref({
  totalVotes: 0,
  totalFav: 0,
  points: 0
})

const myVotes = ref<any[]>([])
const favorites = ref<any[]>([])
const myPublish = ref<any[]>([])
const pendingAuditList = ref<any[]>([])

// -------------------- 加载数据 --------------------
const loadHistory = async () => {
  try {
    const data = await getVoteHistory()
    // 后端返回: [{ voteId, voteTitle, optionId, voteTime }]
    myVotes.value = data.map((item: any) => ({
      id: item.voteId,
      title: item.voteTitle || '未知投票',
      choice: `选项 ${item.optionId}`,
      result: '已参与',
      createTime: item.voteTime
    }))
    stats.value.totalVotes = myVotes.value.length
  } catch (error: any) {
    console.warn('加载投票历史失败', error.message)
  }
}

const loadFavorites = async () => {
  favLoading.value = true
  try {
    const ids = await getFavorites() // 返回 [1,2,3]
    if (!ids || ids.length === 0) {
      favorites.value = []
      stats.value.totalFav = 0
      return
    }
    // 批量获取投票详情
    const promises = ids.map((id: number) => getVoteDetail(id))
    const results = await Promise.all(promises)
    favorites.value = results.map((item: any) => ({
      id: item.id,
      title: item.title,
      statusText: item.statusText || (item.status === 1 ? '进行中' : '已结束'),
      endTime: item.endTime
    }))
    stats.value.totalFav = favorites.value.length
  } catch (error: any) {
    ElMessage.error(error.message || '加载收藏失败')
  } finally {
    favLoading.value = false
  }
}

const loadPoints = async () => {
  try {
    const data = await getMyPoints()
    stats.value.points = data.points || 0
  } catch (error: any) {
    console.warn('加载积分失败', error.message)
  }
}

const loadMyPublish = async () => {
  publishLoading.value = true
  try {
    // 1. 先获取投票列表（只获取 ID 和标题）
    const data = await getVoteList({
      pageNum: 1,
      pageSize: 100,
      creatorId: userId.value
    })
    const records = data.records || data || []
    
    // 2. 对每个投票调用 getVoteDetail 获取完整信息（含 auditStatus）
    const detailPromises = records.map((item: any) => getVoteDetail(item.id))
    const details = await Promise.all(detailPromises)
    
    myPublish.value = details.map((item: any) => ({
      id: item.id,
      title: item.title,
      auditStatus: item.auditStatus === 0 ? '待审核' : item.auditStatus === 1 ? '已通过' : '已拒绝',
      statusText: item.statusText || (item.status === 1 ? '进行中' : item.status === 2 ? '已结束' : '未开始'),
      createTime: item.createTime
    }))
  } catch (error: any) {
    ElMessage.error(error.message || '加载我发布的投票失败')
  } finally {
    publishLoading.value = false
  }
}

const loadPendingAudits = async () => {
  if (!isAdmin.value) return
  auditLoading.value = true
  try {
    const data = await getPendingAudits()
    pendingAuditList.value = data || []
  } catch (error: any) {
    ElMessage.error(error.message || '加载待审核列表失败')
  } finally {
    auditLoading.value = false
  }
}

// -------------------- 修改昵称 --------------------
const editNameDialogVisible = ref(false)
const nameSaving = ref(false)
const newName = ref('')

const showEditNameDialog = () => {
  newName.value = username.value
  editNameDialogVisible.value = true
}

const confirmEditName = async () => {
  if (!newName.value.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  nameSaving.value = true
  try {
    await updateUsername(newName.value.trim())
    username.value = newName.value.trim()
    localStorage.setItem('username', username.value)
    window.dispatchEvent(new Event('storage'))
    editNameDialogVisible.value = false
    ElMessage.success('昵称修改成功！')
  } catch (error: any) {
    ElMessage.error(error.message || '修改失败')
  } finally {
    nameSaving.value = false
  }
}

// -------------------- 换头像 --------------------
const avatarPreviewVisible = ref(false)
const tempAvatarUrl = ref('')
const avatarSaving = ref(false)
const uploadRef = ref()

const triggerUpload = () => {
  const input = document.querySelector('.avatar-upload input[type="file"]') as HTMLInputElement
  if (input) input.click()
}

const handleAvatarChange = (file: any) => {
  const reader = new FileReader()
  reader.onload = (e: any) => {
    tempAvatarUrl.value = e.target.result
    avatarPreviewVisible.value = true
  }
  reader.readAsDataURL(file.raw)
}

const handleAvatarConfirm = (file: any) => {
  const reader = new FileReader()
  reader.onload = (e: any) => {
    tempAvatarUrl.value = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

const saveAvatar = async () => {
  avatarSaving.value = true
  try {
    const input = document.querySelector('.avatar-upload input[type="file"]') as HTMLInputElement
    const file = input?.files?.[0]
    if (!file) {
      ElMessage.warning('请先选择图片')
      avatarSaving.value = false
      return
    }
    const data = await uploadAvatar(file)
    const avatarUrlStr = data.avatar || data.url || data
    avatarUrl.value = avatarUrlStr
    localStorage.setItem('avatar', avatarUrlStr)
    avatarPreviewVisible.value = false
    ElMessage.success('头像更换成功！')
  } catch (error: any) {
    ElMessage.error(error.message || '上传失败')
  } finally {
    avatarSaving.value = false
  }
}

// -------------------- 取消收藏 --------------------
const unfav = async (id: number) => {
  try {
    await unfavoriteVote(id)
    favorites.value = favorites.value.filter(item => item.id !== id)
    stats.value.totalFav = favorites.value.length
    ElMessage.success('已取消收藏')
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// -------------------- 审核操作 --------------------
const approveVote = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认通过该投票吗？', '审核确认', {
      confirmButtonText: '确认通过',
      cancelButtonText: '取消',
      type: 'info'
    })
    await auditVote(id, 1)
    ElMessage.success('审核通过，投票已发布')
    await loadPendingAudits()
    await loadMyPublish()
  } catch {}
}

const rejectVote = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认拒绝该投票吗？', '审核确认', {
      confirmButtonText: '确认拒绝',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await auditVote(id, 2)
    ElMessage.warning('已拒绝该投票')
    await loadPendingAudits()
    await loadMyPublish()
  } catch {}
}

// -------------------- 跳转 --------------------
const viewDetail = (id: number) => router.push(`/detail/${id}`)
const viewResult = (id: number) => router.push(`/result/${id}`)

// -------------------- 生命周期 --------------------
onMounted(async () => {
  await Promise.all([
    loadHistory(),
    loadFavorites(),
    loadPoints(),
    loadMyPublish(),
    loadPendingAudits()
  ])

  window.addEventListener('storage', () => {
    username.value = localStorage.getItem('username') || '用户'
    avatarUrl.value = localStorage.getItem('avatar') || ''
    isAdmin.value = localStorage.getItem('utype') === 'ROLE_1'
  })
})
</script>

<style scoped>
.profile-container { padding: 20px; }
.profile-card { background: #fff !important; }
.user-info { text-align: center; }

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 12px;
}
.user-avatar {
  display: block;
  margin: 0 auto;
  font-size: 40px;
  background: #f0f2f5;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.user-avatar:hover {
  transform: scale(1.08);
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
}
.avatar-hint {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0,0,0,0.6);
  color: #fff;
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s;
  white-space: nowrap;
}
.avatar-wrapper:hover .avatar-hint { opacity: 1; }
.avatar-upload { display: none; }

.username-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 4px;
}
.username-wrapper h3 { margin: 0; font-size: 20px; color: #303133; }
.edit-name-btn { font-size: 13px; padding: 0 4px; }

.role { color: #909399; font-size: 14px; margin: 2px 0 0; }
.level-tag { color: #e6a23c; font-size: 15px; margin: 2px 0 12px; font-weight: 500; }

.stats {
  display: flex;
  justify-content: space-around;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}
.stats .num { display: block; font-size: 24px; font-weight: bold; color: #409eff; }
.stats .label { font-size: 12px; color: #909399; }

.audit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.audit-info { font-size: 14px; color: #606266; }

.avatar-preview-area { text-align: center; padding: 20px 0; }
.preview-avatar { display: block; margin: 0 auto; }
.avatar-upload-btn { display: inline-block; margin-top: 16px; }

/* 积分规则弹窗样式 */
.rules-container h4 {
  margin: 16px 0 8px;
  color: #303133;
}
.rules-container h4:first-child {
  margin-top: 0;
}
</style>