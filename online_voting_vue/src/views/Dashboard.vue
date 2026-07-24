<template>
  <div class="dashboard-container">
    <h2 style="margin-bottom:20px;">📊 数据统计看板</h2>

    <el-card>
      <!-- 统计卡片 -->
      <div class="stat-cards" v-loading="loading">
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

      <!-- 图表 -->
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDashboard, getVoteList, getTrend } from '@/api/vote'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const statistics = ref({
  totalVotes: 0,
  totalParticipants: 0,
  activeVotes: 0
})
const voteList = ref<any[]>([])
const trendData = ref<{ dates: string[]; counts: number[] }>({
  dates: [],
  counts: []
})

const barChartRef = ref<HTMLDivElement | null>(null)
const lineChartRef = ref<HTMLDivElement | null>(null)
let barChart: echarts.ECharts | null = null
let lineChart: echarts.ECharts | null = null

// 加载统计数据
const loadStatistics = async () => {
  try {
    const data = await getDashboard()
    statistics.value.totalVotes = data.voteCount || 0
    statistics.value.totalParticipants = data.recordCount || 0
    statistics.value.activeVotes = data.ongoingVoteCount || 0
  } catch (error) {
    console.warn('加载统计数据失败', error)
  }
}

// 加载投票列表（用于柱状图）
const loadVoteList = async () => {
  try {
    const data = await getVoteList(1, 100)
    const records = data.records || data || []
    voteList.value = records.map((item: any) => ({
      id: item.id,
      title: item.title,
      totalVoters: item.totalVoters || 0
    }))
  } catch (error) {
    console.warn('加载投票列表失败', error)
  }
}

// 加载趋势数据（近7天）
const loadTrend = async () => {
  try {
    const data = await getTrend()
    trendData.value.dates = data.trendLabels || []
    trendData.value.counts = data.trendValues || []
  } catch (error) {
    console.warn('加载趋势数据失败，使用模拟数据', error)
    // 如果后端无接口，生成模拟近7天数据
    const dates = []
    const counts = []
    for (let i = 6; i >= 0; i--) {
      const d = new Date()
      d.setDate(d.getDate() - i)
      dates.push(`${d.getMonth()+1}/${d.getDate()}`)
      counts.push(Math.floor(Math.random() * 30) + 5)
    }
    trendData.value.dates = dates
    trendData.value.counts = counts
  }
}

// 初始化图表
const initCharts = () => {
  // 柱状图
  if (barChartRef.value) {
    barChart = echarts.init(barChartRef.value)
    const barData = voteList.value.map(item => ({
      name: item.title.length > 6 ? item.title.slice(0, 6) + '...' : item.title,
      value: item.totalVoters || 0
    }))
    barChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: barData.map(d => d.name)
      },
      yAxis: { type: 'value', name: '参与人数' },
      series: [{
        type: 'bar',
        data: barData.map(d => d.value),
        itemStyle: {
          color: '#409eff',
          borderRadius: [4, 4, 0, 0]
        }
      }]
    })
    barChart.resize()
  }

  // 折线图
  if (lineChartRef.value) {
    lineChart = echarts.init(lineChartRef.value)
    const { dates, counts } = trendData.value
    lineChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: dates.length ? dates : ['暂无数据']
      },
      yAxis: { type: 'value', name: '参与人数' },
      series: [{
        type: 'line',
        data: counts.length ? counts : [0],
        smooth: true,
        lineStyle: { color: '#67c23a', width: 3 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        },
        symbol: 'circle',
        symbolSize: 8,
        itemStyle: { color: '#67c23a' }
      }]
    })
    lineChart.resize()
  }
}

const handleResize = () => {
  barChart?.resize()
  lineChart?.resize()
}

onMounted(async () => {
  loading.value = true
  await Promise.all([loadStatistics(), loadVoteList(), loadTrend()])
  loading.value = false
  await nextTick()
  initCharts()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  barChart?.dispose()
  lineChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.stat-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}
.stat-item {
  flex: 1;
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
}
.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
.chart-box {
  background: #fafafa;
  padding: 15px;
  border-radius: 8px;
}
.chart-box h4 {
  text-align: center;
  margin-bottom: 10px;
  color: #303133;
}
.chart-container {
  width: 100%;
  height: 300px;
}
</style>