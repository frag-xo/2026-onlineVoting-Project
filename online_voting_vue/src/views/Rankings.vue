<template>
  <div class="rankings-container">
    <h2>🏆 投票排行</h2>
    <el-tabs v-model="rankType">
      <!-- 热门投票 -->
      <el-tab-pane label="热门投票" name="hot">
        <el-table :data="hotList" stripe v-loading="loading">
          <el-table-column label="排名" width="80">
            <template #default="{ $index }">
              <el-tag :type="$index < 3 ? 'danger' : 'info'">{{ $index + 1 }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="投票标题" />
          <el-table-column prop="totalVotes" label="参与人数" width="120" sortable />
          <el-table-column prop="statusText" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.statusText === '进行中' ? 'success' : row.statusText === '已结束' ? 'info' : 'warning'">
                {{ row.statusText }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" @click="goDetail(row.voteId || row.id)">参与</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 最新发布 -->
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

      <!-- 积分排行 -->
      <el-tab-pane label="积分排行" name="points">
        <el-table :data="pointsList" stripe v-loading="loading">
          <el-table-column label="排名" width="80">
            <template #default="{ $index }">
              <el-tag :type="$index < 3 ? 'warning' : 'info'">{{ $index + 1 }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="用户" width="160">
            <template #default="{ row }">
              <span>{{ row.username || row.realname || '未知' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="level" label="等级" width="80">
            <template #default="{ row }">
              <el-tag size="small">Lv.{{ row.level || 1 }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="currentPoints" label="当前积分" width="120" sortable />
          <el-table-column prop="totalPoints" label="累计获得" width="120" sortable />
        </el-table>
        <el-empty v-if="!loading && pointsList.length === 0" description="暂无积分排行数据" />
      </el-tab-pane>

      <!-- 推荐投票 -->
      <el-tab-pane label="推荐投票" name="recommended">
        <el-table :data="recommendList" stripe v-loading="loading">
          <el-table-column label="排名" width="80">
            <template #default="{ $index }">
              <el-tag :type="$index < 3 ? 'danger' : 'info'">{{ $index + 1 }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="投票标题" />
          <el-table-column prop="totalVotes" label="参与人数" width="120" />
          <el-table-column prop="statusText" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.statusText === '进行中' ? 'success' : row.statusText === '已结束' ? 'info' : 'warning'">
                {{ row.statusText }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" @click="goDetail(row.voteId || row.id)">参与</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loading && recommendList.length === 0" description="暂无推荐投票，管理员可推荐优质投票" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVoteRanking, getVoteList, getRecommendedVotes, getPointsRanking } from '@/api/vote'

const router = useRouter()
const rankType = ref('hot')
const loading = ref(false)

const hotList = ref<any[]>([])
const newestList = ref<any[]>([])
const recommendList = ref<any[]>([])
const pointsList = ref<any[]>([])

// 加载热门排行
const loadHotRanking = async () => {
  try {
    const data = await getVoteRanking(10)
    hotList.value = (data || []).map((item: any) => ({
      voteId: item.voteId || item.id,
      id: item.voteId || item.id,
      title: item.title,
      totalVotes: item.totalVotes || 0,
      status: item.status,
      statusText: item.status === 1 ? '进行中' : item.status === 2 ? '已结束' : '未开始',
      endTime: item.endTime
    }))
  } catch (error: any) {
    ElMessage.error(error.message || '加载排行失败')
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

// 加载推荐投票（调用后端推荐接口）
const loadRecommended = async () => {
  try {
    const data = await getRecommendedVotes()
    recommendList.value = data.map((item: any) => ({
      voteId: item.id,
      id: item.id,
      title: item.title,
      totalVotes: item.totalVoters || 0,
      status: item.status,
      statusText: item.status === 1 ? '进行中' : item.status === 2 ? '已结束' : '未开始',
      endTime: item.endTime
    }))
  } catch (error: any) {
    console.warn('加载推荐投票失败', error.message)
    // 如果后端推荐接口未实现，可降级复用热门数据（取消下面注释）
    // try {
    //   const fallback = await getVoteRanking(10)
    //   recommendList.value = fallback.map(...)
    // } catch (e) {}
  }
}

// 加载积分排行
const loadPointsRanking = async () => {
  try {
    const data = await getPointsRanking(20)
    pointsList.value = (data || []).map((item: any) => ({
      username: item.username,
      realname: item.realname,
      level: item.level,
      currentPoints: item.currentPoints,
      totalPoints: item.totalPoints
    }))
  } catch (error: any) {
    console.warn('加载积分排行失败', error.message)
  }
}

const goDetail = (id: number) => {
  if (!id) {
    ElMessage.warning('投票ID无效')
    return
  }
  router.push(`/detail/${id}`)
}

onMounted(() => {
  loading.value = true
  Promise.all([loadHotRanking(), loadNewest(), loadRecommended(), loadPointsRanking()]).finally(() => {
    loading.value = false
  })
})
</script>

<style scoped>
.rankings-container {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}
</style>