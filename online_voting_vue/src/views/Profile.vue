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

            <!-- 昵称 -->
            <div class="username-wrapper">
              <h3>{{ displayName }}</h3>
              <el-button
                type="primary"
                link
                size="small"
                @click="showEditNameDialog"
                class="edit-name-btn"
              >
                <el-icon><Edit /></el-icon> 修改
              </el-button>
            </div>
            <p class="role">{{ isAdmin ? '管理员' : '普通用户' }}</p>

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
              <el-table :data="myPublish" stripe v-loading="loading">
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
                  <template #default="{ row }">
                    {{ row.optionsCount || 0 }}
                  </template>
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
  favoriteVote
} from '@/api/vote'

const router = useRouter()
const loading = ref(false)
const favLoading = ref(false)
const auditLoading = ref(false)
const activeTab = ref('records')

const userId = ref(Number(localStorage.getItem('userId') || 0))
const username = ref(localStorage.getItem('username') || '用户')
const isAdmin = ref(localStorage.getItem('utype') === 'ROLE_1')
const avatarUrl = ref(localStorage.getItem('avatar') || '')

const displayName = computed(() => username.value)

// 统计数据
const stats = ref({
  totalVotes: 0,
  totalFav: 0,
  points: 0
})

// 我的投票记录
const myVotes = ref<any[]>([])

// 我的收藏
const favorites = ref<any[]>([])

// 我发布的投票
const myPublish = ref<any[]>([])

// 待审核投票
const pendingAuditList = ref<any[]>([])

// ============================================================
// 加载数据
// ============================================================
const loadHistory = async () => {
  try {
    const data = await getVoteHistory()
    myVotes.value = data || []
  } catch (error: any) {
    console.warn('加载投票历史失败', error.message)
  }
}

const loadFavorites = async () => {
  favLoading.value = true
  try {
    const data = await getFavorites()
    favorites.value = data || []
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

// ============================================================
// 修改昵称
// ============================================================
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

// ============================================================
// 换头像
// ============================================================
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
    // 获取上传的文件
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

// ============================================================
// 取消收藏
// ============================================================
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

// ============================================================
// 审核操作
// ============================================================
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
  } catch {}
}

// ============================================================
// 跳转方法
// ============================================================
const viewDetail = (id: number) => router.push(`/detail/${id}`)
const viewResult = (id: number) => router.push(`/result/${id}`)

// ============================================================
// 生命周期
// ============================================================
onMounted(async () => {
  // 并行加载所有数据
  await Promise.all([
    loadHistory(),
    loadFavorites(),
    loadPoints(),
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

.role { color: #909399; font-size: 14px; margin: 4px 0 16px 0; }

.stats {
  display: flex;
  justify-content: space-around;
  margin-top: 16px;
  padding-top: 16px;
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
</style>