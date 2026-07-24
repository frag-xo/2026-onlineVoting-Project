<template>
  <div class="rankings-container">
    <h2>🏆 投票排行</h2>
    <el-tabs v-model="rankType">
      <el-tab-pane label="热门投票" name="hot">
        <el-table :data="hotList" stripe v-loading="loading">
          <el-table-column label="排名" width="80">
            <template #default="{ $index }">
              <el-tag :type="$index < 3 ? 'danger' : 'info'">{{ $index + 1 }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="投票标题" />
          <el-table-column prop="totalVoters" label="参与人数" width="120" sortable />
          <el-table-column prop="statusText" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.statusText === '进行中' ? 'success' : 'info'">{{ row.statusText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" @click="goDetail(row.id)">参与</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="最新发布" name="newest">
        <el-table :data="newestList" stripe v-loading="loading">
          <el-table-column prop="title" label="投票标题" />
          <el-table-column prop="createTime" label="发布时间" width="180" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" @click="goDetail(row.id)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVoteRanking, getVoteList } from '@/api/vote'

const router = useRouter()
const rankType = ref('hot')
const loading = ref(false)

const hotList = ref<any[]>([])
const newestList = ref<any[]>([])

// 加载热门排行
const loadHotRanking = async () => {
  loading.value = true
  try {
    const data = await getVoteRanking(10)
    hotList.value = data || []
  } catch (error: any) {
    ElMessage.error(error.message || '加载排行失败')
  } finally {
    loading.value = false
  }
}

// 加载最新发布
const loadNewest = async () => {
  try {
    const data = await getVoteList({ pageNum: 1, pageSize: 10, orderBy: 'createTime', orderDirection: 'desc' })
    const records = data.records || data || []
    newestList.value = records.map((item: any) => ({
      id: item.id,
      title: item.title,
      createTime: item.createTime
    }))
  } catch (error: any) {
    console.warn('加载最新发布失败', error.message)
  }
}

const goDetail = (id: number) => {
  router.push(`/detail/${id}`)
}

onMounted(() => {
  loadHotRanking()
  loadNewest()
})
</script>

<style scoped>
.rankings-container {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}
</style>