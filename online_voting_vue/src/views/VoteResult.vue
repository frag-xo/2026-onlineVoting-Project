<template>
  <div class="result-container">
    <div class="result-header">
      <h2>{{ voteTitle }}</h2>
      <p>总票数：{{ totalVotes }}</p>
      <el-button type="primary" size="small" @click="goBack">返回列表</el-button>
    </div>

    <div ref="chartRef" class="chart-box"></div>

    <div class="result-table">
      <el-table :data="optionsData" stripe>
        <el-table-column prop="text" label="选项" />
        <el-table-column prop="count" label="票数" />
        <el-table-column prop="percentage" label="占比 (%)">
          <template #default="{ row }">
            {{ row.percentage.toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'
import { getVoteResult } from '@/api/vote'

const router = useRouter()
const route = useRoute()
const voteId = Number(route.params.id)

const chartRef = ref<HTMLDivElement | null>(null)
let chartInstance: ECharts | null = null
let timer: any = null

const voteTitle = ref('')
const totalVotes = ref(0)
const optionsData = ref<{ text: string; count: number; percentage: number }[]>([])
const loading = ref(false)

const loadResult = async () => {
  loading.value = true
  try {
    const data = await getVoteResult(voteId)
    // 后端返回 { title, totalVoters, options: [{ optionText, count }] }
    voteTitle.value = data.title || ''
    totalVotes.value = data.totalCount || 0
    const total = totalVotes.value
    optionsData.value = (data.options || []).map((item: any) => ({
      text: item.optionText || item.text,
      count: item.count || 0,
      percentage: total > 0 ? (item.count / total) * 100 : 0
    }))
    updateChart()
  } catch (error: any) {
    ElMessage.error(error.message || '加载结果失败')
  } finally {
    loading.value = false
  }
}

const updateChart = () => {
  if (!chartInstance) return
  chartInstance.setOption({
    series: [{
      data: optionsData.value.map(item => ({
        name: item.text,
        value: item.count
      }))
    }]
  })
}

const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center'
    },
    series: [
      {
        name: '投票结果',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['45%', '50%'],
        data: optionsData.value.map(item => ({
          name: item.text,
          value: item.count
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  chartInstance.setOption(option)
  window.addEventListener('resize', handleResize)
}

const handleResize = () => {
  chartInstance?.resize()
}

const startPolling = () => {
  timer = setInterval(loadResult, 30000)
}

const goBack = () => {
  router.push('/')
}

onMounted(() => {
  loadResult()
  setTimeout(initChart, 300)
  startPolling()
})

onBeforeUnmount(() => {
  clearInterval(timer)
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
  chartInstance = null
})
</script>

<style scoped>
.result-container {
  max-width: 800px;
  margin: 24px auto;
  padding: 0 20px;
}
.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.result-header h2 {
  font-size: 22px;
  font-weight: 600;
  color: #1a1a2e;
}
.chart-box {
  width: 100%;
  height: 400px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #f0f2f5;
  padding: 16px;
}
.result-table {
  margin-top: 24px;
}
</style>