<template>
  <div class="admin-container">
    <!-- ===== 发布新投票 ===== -->
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span><strong>📝 发布新投票</strong></span>
          <el-tag v-if="isAdmin" type="success" size="small">管理员</el-tag>
          <el-tag v-else type="warning" size="small">普通用户</el-tag>
        </div>
      </template>

      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="投票标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入投票标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="投票描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入投票描述（可选）" />
        </el-form-item>

        <el-form-item label="选项列表" prop="options">
          <div v-for="(item, index) in form.options" :key="index" class="option-item">
            <el-input
              v-model="form.options[index]"
              :placeholder="`选项 ${index + 1}`"
              style="width: 300px; margin-right: 10px;"
            />
            <el-button
              type="danger"
              size="small"
              :disabled="form.options.length <= 2"
              @click="removeOption(index)"
            >
              删除
            </el-button>
          </div>
          <el-button type="primary" size="small" @click="addOption">+ 添加选项</el-button>
        </el-form-item>

        <el-form-item label="截止时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择截止时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="定时发布" prop="scheduledTime">
          <el-date-picker
            v-model="form.scheduledTime"
            type="datetime"
            placeholder="选择发布时间（可选）"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
          <span style="font-size:12px;color:#909399;margin-left:10px;">留空则立即发布</span>
        </el-form-item>

        <el-form-item label="分组管理" prop="group">
          <el-select v-model="form.group" placeholder="选择分组" clearable>
            <el-option label="技术讨论" value="tech" />
            <el-option label="团队建设" value="team" />
            <el-option label="产品反馈" value="product" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handlePublish" :loading="publishing">
            {{ isAdmin ? '发布投票' : '提交审核' }}
          </el-button>
          <el-button @click="resetForm">重置</el-button>
          <span v-if="!isAdmin" style="font-size:12px;color:#909399;margin-left:16px;">
            ⚠️ 提交后需管理员审核
          </span>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- ===== 已发布投票列表 ===== -->
    <el-card class="box-card" style="margin-top: 30px;">
      <template #header>
        <div class="card-header">
          <span><strong>📋 已发布投票</strong></span>
          <el-button size="small" @click="loadVotes" :loading="tableLoading">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>
      </template>

      <el-table :data="voteList" stripe v-loading="tableLoading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '进行中' ? 'success' : row.status === '已暂停' ? 'warning' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- ✅ 审核状态列 -->
        <el-table-column prop="auditStatus" label="审核状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.auditStatus === '已通过' ? 'success' : row.auditStatus === '待审核' ? 'warning' : 'danger'">
              {{ row.auditStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止时间" width="180" />
        <el-table-column prop="totalVotes" label="参与人数" width="100" align="center" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editVote(row)">编辑</el-button>
            <el-button
              size="small"
              :type="row.status === '进行中' ? 'warning' : 'success'"
              @click="toggleVoteStatus(row)"
            >
              {{ row.status === '进行中' ? '暂停' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="deleteVote(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- ===== 数据统计看板 ===== -->
    <el-card class="box-card" style="margin-top: 30px;">
      <template #header>
        <div class="card-header">
          <span><strong>📊 数据统计看板</strong></span>
        </div>
      </template>

      <div class="stat-cards" v-loading="dashboardLoading">
        <div class="stat-item">
          <div class="stat-number">{{ statistics.totalVotes }}</div>
          <div class="stat-label">总投票数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ statistics.totalParticipants }}</div>
          <div class="stat-label">总参与人数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ statistics.activeVotes }}</div>
          <div class="stat-label">进行中投票</div>
        </div>
      </div>

      <div class="chart-row">
        <div class="chart-box">
          <h4>各投票参与人数</h4>
          <div ref="barChartRef" class="chart-container"></div>
        </div>
        <div class="chart-box">
          <h4>投票参与趋势（近7天）</h4>
          <div ref="lineChartRef" class="chart-container"></div>
        </div>
      </div>
    </el-card>

    <!-- ===== 编辑投票对话框 ===== -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑投票"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="投票标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入投票标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="选项列表" prop="options">
          <div v-for="(item, index) in editForm.options" :key="index" class="option-item">
            <el-input
              v-model="editForm.options[index]"
              :placeholder="`选项 ${index + 1}`"
              style="width: 300px; margin-right: 10px;"
            />
            <el-button
              type="danger"
              size="small"
              :disabled="editForm.options.length <= 2"
              @click="removeEditOption(index)"
            >
              删除
            </el-button>
          </div>
          <el-button type="primary" size="small" @click="addEditOption">+ 添加选项</el-button>
        </el-form-item>

        <el-form-item label="截止时间" prop="endTime">
          <el-date-picker
            v-model="editForm.endTime"
            type="datetime"
            placeholder="选择截止时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="editLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import * as echarts from 'echarts'
import { createVote, updateVote, deleteVote, endVote, getVoteList, getDashboard } from '@/api/vote'

// ============================================================
// 用户身份
// ============================================================
const isAdmin = computed(() => localStorage.getItem('utype') === 'ROLE_1')

// ============================================================
// 发布投票表单
// ============================================================
const formRef = ref<FormInstance>()
const publishing = ref(false)

const form = reactive({
  title: '',
  description: '',
  options: ['', ''],
  endTime: '',
  scheduledTime: '',
  group: ''
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入投票标题', trigger: 'blur' }],
  options: [{
    validator: (_rule: any, value: string[], callback: any) => {
      const filtered = value.filter(item => item.trim() !== '')
      if (filtered.length < 2) callback(new Error('至少需要 2 个有效选项'))
      else callback()
    },
    trigger: 'blur'
  }],
  endTime: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
}

const addOption = () => form.options.push('')
const removeOption = (index: number) => form.options.splice(index, 1)

const resetForm = () => {
  form.title = ''
  form.description = ''
  form.options = ['', '']
  form.endTime = ''
  form.scheduledTime = ''
  form.group = ''
  formRef.value?.resetFields()
}

const handlePublish = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      publishing.value = true
      try {
        const filteredOptions = form.options.filter(item => item.trim() !== '')
        const userId = Number(localStorage.getItem('userId') || 0)
        // 根据角色决定审核状态：管理员直接通过，普通用户待审核
        const status = isAdmin.value ? 1 : 0
        await createVote({
          title: form.title,
          description: form.description || '',
          status: status,
          endTime: form.endTime,
          options: filteredOptions,
          creatorId: userId,
          scheduledTime: form.scheduledTime || undefined
        })
        ElMessage.success(isAdmin.value ? '投票发布成功！' : '投票已提交审核，请等待管理员审核')
        resetForm()
        await loadVotes()
        await loadDashboard()
      } catch (error: any) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        publishing.value = false
      }
    }
  })
}

// ============================================================
// 已发布投票列表
// ============================================================
const voteList = ref<any[]>([])
const tableLoading = ref(false)

const loadVotes = async () => {
  tableLoading.value = true
  try {
    const data = await getVoteList(1, 100)
    const records = data.records || data || []
    voteList.value = records.map((item: any) => ({
      id: item.id,
      title: item.title,
      status: item.statusText || (item.status === 1 ? '进行中' : item.status === 2 ? '已结束' : '未开始'),
      auditStatus: item.auditStatus || (item.status === 0 ? '待审核' : '已通过'),
      deadline: item.endTime,
      totalVotes: item.totalVoters || 0,
      options: item.options || []
    }))
    refreshCharts()
  } catch (error: any) {
    ElMessage.error(error.message || '加载投票列表失败')
  } finally {
    tableLoading.value = false
  }
}

// ============================================================
// 统计数据
// ============================================================
const dashboardLoading = ref(false)
const statistics = reactive({
  totalVotes: 0,
  totalParticipants: 0,
  activeVotes: 0
})

const loadDashboard = async () => {
  dashboardLoading.value = true
  try {
    const data = await getDashboard()
    statistics.totalVotes = data.totalVotes || 0
    statistics.totalParticipants = data.totalParticipants || 0
    statistics.activeVotes = data.activeVotes || 0
  } catch (error: any) {
    console.warn('加载看板数据失败', error.message)
  } finally {
    dashboardLoading.value = false
  }
}

// ============================================================
// 图表
// ============================================================
const barChartRef = ref<HTMLDivElement | null>(null)
const lineChartRef = ref<HTMLDivElement | null>(null)
let barChart: echarts.ECharts | null = null
let lineChart: echarts.ECharts | null = null

const initBarChart = () => {
  if (!barChartRef.value) return
  barChart = echarts.init(barChartRef.value)
  const barData = voteList.value.map(item => ({
    name: item.title.length > 6 ? item.title.slice(0, 6) + '...' : item.title,
    value: item.totalVotes || 0
  }))
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: barData.map(d => d.name) },
    yAxis: { type: 'value', name: '参与人数' },
    series: [{
      type: 'bar',
      data: barData.map(d => d.value),
      itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] }
    }]
  })
  barChart.resize()
}

const initLineChart = () => {
  if (!lineChartRef.value) return
  lineChart = echarts.init(lineChartRef.value)
  const days = []
  const values = []
  for (let i = 6; i >= 0; i--) {
    const d = new Date()
    d.setDate(d.getDate() - i)
    days.push(`${d.getMonth()+1}/${d.getDate()}`)
    values.push(Math.floor(Math.random() * 30) + 5)
  }
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: days },
    yAxis: { type: 'value', name: '参与人数' },
    series: [{
      type: 'line',
      data: values,
      smooth: true,
      lineStyle: { color: '#67c23a', width: 3 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(103,194,58,0.3)' },
          { offset: 1, color: 'rgba(103,194,58,0.05)' }
        ])
      },
      symbol: 'circle',
      symbolSize: 8,
      itemStyle: { color: '#67c23a' }
    }]
  })
  lineChart.resize()
}

const handleResize = () => {
  barChart?.resize()
  lineChart?.resize()
}

const refreshCharts = () => {
  setTimeout(() => {
    initBarChart()
    initLineChart()
  }, 200)
}

// ============================================================
// 编辑投票
// ============================================================
const editDialogVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref<FormInstance>()
const editId = ref<number | null>(null)

const editForm = reactive({
  title: '',
  options: ['', ''],
  endTime: ''
})

const editRules: FormRules = {
  title: [{ required: true, message: '请输入投票标题', trigger: 'blur' }],
  options: [{
    validator: (_rule: any, value: string[], callback: any) => {
      const filtered = value.filter(item => item.trim() !== '')
      if (filtered.length < 2) callback(new Error('至少需要 2 个有效选项'))
      else callback()
    },
    trigger: 'blur'
  }],
  endTime: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
}

const editVote = (row: any) => {
  editId.value = row.id
  editForm.title = row.title
  editForm.options = row.options || ['', '']
  editForm.endTime = row.deadline
  editDialogVisible.value = true
}

const addEditOption = () => editForm.options.push('')
const removeEditOption = (index: number) => editForm.options.splice(index, 1)

const handleEditSubmit = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      editLoading.value = true
      try {
        const filteredOptions = editForm.options.filter(item => item.trim() !== '')
        await updateVote({
          id: editId.value!,
          title: editForm.title,
          endTime: editForm.endTime,
          options: filteredOptions
        })
        ElMessage.success('编辑成功！')
        editDialogVisible.value = false
        await loadVotes()
        await loadDashboard()
      } catch (error: any) {
        ElMessage.error(error.message || '编辑失败')
      } finally {
        editLoading.value = false
      }
    }
  })
}

// ============================================================
// 启停投票
// ============================================================
const toggleVoteStatus = async (row: any) => {
  const action = row.status === '进行中' ? '暂停' : '启用'
  try {
    if (row.status === '进行中') {
      await endVote(row.id)
      row.status = '已暂停'
    } else {
      await updateVote({ id: row.id, status: 1 })
      row.status = '进行中'
    }
    ElMessage.success(`投票已${action}`)
    await loadVotes()
    await loadDashboard()
  } catch (error: any) {
    ElMessage.error(error.message || `${action}失败`)
  }
}

// ============================================================
// 删除投票
// ============================================================
const deleteVote = (id: number) => {
  ElMessageBox.confirm('确认删除该投票吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteVote(id)
      ElMessage.success('删除成功')
      await loadVotes()
      await loadDashboard()
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

// ============================================================
// 生命周期
// ============================================================
onMounted(() => {
  loadVotes()
  loadDashboard()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  barChart?.dispose()
  lineChart?.dispose()
})
</script>

<style scoped>
.admin-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.stat-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.stat-item {
  flex: 1;
  min-width: 120px;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  color: #909399;
  font-size: 14px;
  margin-top: 8px;
}

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-box {
  background: #fafafa;
  border-radius: 8px;
  padding: 15px;
}

.chart-box h4 {
  text-align: center;
  margin-bottom: 10px;
  color: #303133;
}

.chart-container {
  width: 100%;
  height: 250px;
}

@media (max-width: 768px) {
  .chart-row {
    grid-template-columns: 1fr;
  }
}
</style>