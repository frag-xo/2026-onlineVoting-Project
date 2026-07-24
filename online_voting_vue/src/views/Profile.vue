<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 左侧：用户信息 -->
      <el-col :span="6">
        <el-card>
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
              <!-- 隐藏的文件上传 -->
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

            <!-- 统计 -->
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

      <!-- 右侧：Tab切换 -->
      <el-col :span="18">
        <el-card>
          <el-tabs v-model="activeTab">
            <!-- 我的投票记录 -->
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

            <!-- 我的收藏 -->
            <el-tab-pane label="我的收藏" name="favorites">
              <el-table :data="favorites" stripe v-loading="loading">
                <el-table-column prop="title" label="投票标题" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === '进行中' ? 'success' : 'info'">{{ row.status }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="deadline" label="截止时间" width="180" />
                <el-table-column label="操作" width="160">
                  <template #default="{ row }">
                    <el-button size="small" @click="viewDetail(row.id)">查看</el-button>
                    <el-button size="small" type="danger" @click="unfav(row.id)">取消</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <!-- 我发布的投票 -->
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
                <el-table-column prop="status" label="发布状态" width="120">
                  <template #default="{ row }">
                    <el-tag :type="row.status === '进行中' ? 'success' : 'info'">{{ row.status }}</el-tag>
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

            <!-- 审核投票（管理员） -->
            <el-tab-pane label="审核投票" name="audit" v-if="isAdmin">
              <div class="audit-header">
                <span class="audit-info">待审核投票数：{{ pendingAuditList.length }}</span>
              </div>
              <el-table :data="pendingAuditList" stripe v-loading="auditLoading">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="title" label="投票标题" />
                <el-table-column prop="creatorName" label="发布人" width="120" />
                <el-table-column prop="createTime" label="提交时间" width="180" />
                <el-table-column prop="options" label="选项数" width="80">
                  <template #default="{ row }">
                    {{ row.options?.length || 0 }}
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

    <!-- ✅ 修改昵称对话框 -->
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

    <!-- ✅ 头像预览对话框 -->
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
import { ref, computed,onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Camera, Edit } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const auditLoading = ref(false)
const activeTab = ref('records')

// ============================================================
// 用户信息（响应式）
// ============================================================
const userId = ref(Number(localStorage.getItem('userId') || 0))
const username = ref(localStorage.getItem('username') || '用户')
const isAdmin = ref(localStorage.getItem('utype') === 'ROLE_1')
const avatarUrl = ref(localStorage.getItem('avatar') || '')

const displayName = computed(() => username.value)

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

const confirmEditName = () => {
  if (!newName.value.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  nameSaving.value = true
  // 模拟保存（后续对接后端接口）
  setTimeout(() => {
    username.value = newName.value.trim()
    localStorage.setItem('username', username.value)
    // 同步更新 App.vue 中显示的用户名
    window.dispatchEvent(new Event('storage'))
    editNameDialogVisible.value = false
    ElMessage.success('昵称修改成功！')
    nameSaving.value = false
  }, 500)
}

// ============================================================
// 换头像
// ============================================================
const avatarPreviewVisible = ref(false)
const tempAvatarUrl = ref('')
const avatarSaving = ref(false)
const uploadRef = ref()

const triggerUpload = () => {
  // 触发文件选择
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

const saveAvatar = () => {
  avatarSaving.value = true
  // 模拟保存（后续对接后端接口）
  setTimeout(() => {
    avatarUrl.value = tempAvatarUrl.value
    localStorage.setItem('avatar', avatarUrl.value)
    avatarPreviewVisible.value = false
    ElMessage.success('头像更换成功！')
    avatarSaving.value = false
  }, 500)
}

// ============================================================
// 统计数据
// ============================================================
const stats = ref({
  totalVotes: 12,
  totalFav: 5,
  points: 168
})

// ============================================================
// 我的投票记录（模拟数据）
// ============================================================
const myVotes = ref([
  { id: 1, title: '年度最受欢迎编程语言', choice: 'Java', result: 'Java (39%)', createTime: '2026-07-23 14:30' },
  { id: 2, title: '最佳前端框架评选', choice: 'Vue', result: 'React (45%)', createTime: '2026-07-22 10:20' }
])

// ============================================================
// 我的收藏（模拟数据）
// ============================================================
const favorites = ref([
  { id: 3, title: '最喜欢的数据库', status: '进行中', deadline: '2026-08-10 23:59' },
  { id: 4, title: '年度最佳电影', status: '已结束', deadline: '2026-07-20 23:59' }
])

// ============================================================
// 我发布的投票（模拟数据）
// ============================================================
const myPublish = ref([
  {
    id: 5,
    title: '团队建设活动方案投票',
    auditStatus: '待审核',
    status: '未开始',
    createTime: '2026-07-24 09:00',
    options: ['方案A', '方案B', '方案C']
  },
  {
    id: 6,
    title: '下季度技术选型',
    auditStatus: '已通过',
    status: '进行中',
    createTime: '2026-07-22 16:30',
    options: ['React', 'Vue', 'Angular']
  },
  {
    id: 9,
    title: '年会节目征集',
    auditStatus: '已拒绝',
    status: '未开始',
    createTime: '2026-07-21 11:00',
    options: ['唱歌', '跳舞', '小品']
  }
])

// ============================================================
// 待审核投票（管理员）
// ============================================================
const pendingAuditList = ref([
  {
    id: 10,
    title: '团队午餐吃什么',
    creatorName: 'user1',
    createTime: '2026-07-24 08:30',
    options: ['火锅', '炒菜', '西餐']
  },
  {
    id: 11,
    title: '周末团建活动投票',
    creatorName: 'user2',
    createTime: '2026-07-23 20:00',
    options: ['爬山', '露营', '密室逃脱']
  }
])

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
    const index = pendingAuditList.value.findIndex(item => item.id === id)
    if (index !== -1) {
      const approved = pendingAuditList.value[index]
      pendingAuditList.value.splice(index, 1)
      myPublish.value.unshift({
        id: approved.id,
        title: approved.title,
        auditStatus: '已通过',
        status: '进行中',
        createTime: new Date().toLocaleString(),
        options: approved.options || []
      })
      ElMessage.success(`投票「${approved.title}」已审核通过并发布`)
    }
  } catch {}
}

const rejectVote = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认拒绝该投票吗？', '审核确认', {
      confirmButtonText: '确认拒绝',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const index = pendingAuditList.value.findIndex(item => item.id === id)
    if (index !== -1) {
      const rejected = pendingAuditList.value[index]
      pendingAuditList.value.splice(index, 1)
      myPublish.value.unshift({
        id: rejected.id,
        title: rejected.title,
        auditStatus: '已拒绝',
        status: '未开始',
        createTime: new Date().toLocaleString(),
        options: rejected.options || []
      })
      ElMessage.warning(`已拒绝投票「${rejected.title}」`)
    }
  } catch {}
}

// ============================================================
// 跳转方法
// ============================================================
const viewDetail = (id: number) => router.push(`/detail/${id}`)
const viewResult = (id: number) => router.push(`/result/${id}`)

const unfav = (id: number) => {
  favorites.value = favorites.value.filter(item => item.id !== id)
}

// ============================================================
// 生命周期
// ============================================================
onMounted(() => {
  // 监听 storage 变化，同步其他标签页的修改
  window.addEventListener('storage', () => {
    username.value = localStorage.getItem('username') || '用户'
    avatarUrl.value = localStorage.getItem('avatar') || ''
    isAdmin.value = localStorage.getItem('utype') === 'ROLE_1'
  })
})
</script>

<style scoped>
.profile-container { padding: 20px; }

/* ===== 用户信息卡片 ===== */
.user-info { text-align: center; }

/* 头像 */
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
  transition: opacity 0.3s;
}
.user-avatar:hover {
  opacity: 0.8;
}
.avatar-hint {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.6);
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
.avatar-wrapper:hover .avatar-hint {
  opacity: 1;
}
.avatar-upload { display: none; }

/* 昵称 */
.username-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 4px;
}
.username-wrapper h3 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}
.edit-name-btn {
  font-size: 13px;
  padding: 0 4px;
}

.role {
  color: #909399;
  font-size: 14px;
  margin: 4px 0 16px 0;
}

/* 统计 */
.stats {
  display: flex;
  justify-content: space-around;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
.stats .num {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}
.stats .label {
  font-size: 12px;
  color: #909399;
}

/* 审核 */
.audit-header { margin-bottom: 16px; }
.audit-info { font-size: 14px; color: #606266; }

/* 头像预览 */
.avatar-preview-area {
  text-align: center;
  padding: 20px 0;
}
.preview-avatar {
  display: block;
  margin: 0 auto;
}
.avatar-upload-btn {
  display: inline-block;
  margin-top: 16px;
}
</style>