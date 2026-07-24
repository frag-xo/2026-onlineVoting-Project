<template>
  <div class="rankings-container">
    <h2>🏆 投票排行</h2>
    <el-tabs v-model="rankType">
      <el-tab-pane label="热门投票" name="hot">
        <el-table :data="hotList" stripe>
          <el-table-column label="排名" width="80">
            <template #default="{ $index }">
              <el-tag :type="$index < 3 ? 'danger' : 'info'">{{ $index + 1 }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="投票标题" />
          <el-table-column prop="participants" label="参与人数" width="120" sortable />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === '进行中' ? 'success' : 'info'">{{ row.status }}</el-tag>
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
        <el-table :data="newestList" stripe>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const rankType = ref('hot')

const hotList = ref([
  { id: 1, title: '年度最受欢迎编程语言', participants: 1024, status: '进行中' },
  { id: 2, title: '最佳前端框架评选', participants: 512, status: '已结束' },
  { id: 3, title: '最喜欢的数据库', participants: 256, status: '进行中' }
])

const newestList = ref([
  { id: 7, title: '下季度技术选型投票', createTime: '2026-07-24 10:00' },
  { id: 8, title: '团队建设活动方案', createTime: '2026-07-23 16:30' }
])

const goDetail = (id: number) => {
  router.push(`/detail/${id}`)
}
</script>

<style scoped>
.rankings-container {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}
</style>