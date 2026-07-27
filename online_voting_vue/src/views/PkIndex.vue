<template>
  <div class="pk-root">
    <div class="pk-topbar">
      <div class="topbar-title">🔥 话题PK</div>
      <span class="topbar-sub">选择你感兴趣的话题分类</span>
    </div>

    <div class="pk-body">
      <div class="cat-grid">
        <!-- 动漫PK（特殊卡片） -->
        <div class="cat-card cat-anime" @click="goAnimePk">
          <div class="cat-icon">🎯</div>
          <div class="cat-name">动漫二选一</div>
          <div class="cat-desc">32部动漫两两对决，选出你的本命</div>
          <div class="cat-meta">暗黑风格 · ELO评分</div>
          <div class="cat-arrow">开始PK →</div>
        </div>

        <div
          v-for="cat in categories"
          :key="cat.id"
          class="cat-card"
          @click="enterCategory(cat)"
        >
          <div class="cat-icon">{{ cat.icon }}</div>
          <div class="cat-name">{{ cat.name }}</div>
          <div class="cat-desc">{{ cat.description }}</div>
          <div class="cat-meta">{{ cat.pairCount }} 个对战话题</div>
          <div class="cat-arrow">开始PK →</div>
        </div>
      </div>
      <el-empty v-if="!loading && categories.length === 0" description="暂无分类" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPkCategories } from '@/api/vote'

const router = useRouter()
const loading = ref(false)
const categories = ref<any[]>([])

const loadCategories = async () => {
  loading.value = true
  try {
    categories.value = await getPkCategories()
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const enterCategory = (cat: any) => {
  router.push(`/pk/battle/${cat.id}?name=${encodeURIComponent(cat.name)}&icon=${encodeURIComponent(cat.icon)}`)
}

const goAnimePk = () => {
  router.push('/anime-pk')
}

onMounted(() => loadCategories())
</script>

<style scoped>
.pk-root {
  min-height: 100dvh;
  background: #0a0a0f;
  color: #e8e8ed;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.pk-topbar {
  text-align: center;
  padding: 40px 20px 20px;
}
.topbar-title {
  font-size: 28px;
  font-weight: 800;
  background: linear-gradient(135deg, #6366f1, #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.topbar-sub {
  font-size: 14px;
  color: #6b6b80;
  margin-top: 6px;
  display: block;
}

.pk-body {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.cat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.cat-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 16px;
  padding: 24px 20px;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
  animation: fadeUp 0.5s ease both;
}

.cat-card:hover {
  background: rgba(255,255,255,0.06);
  border-color: rgba(99,102,241,0.3);
  transform: translateY(-6px);
  box-shadow: 0 12px 40px rgba(99,102,241,0.1);
}

.cat-icon {
  font-size: 36px;
  margin-bottom: 10px;
}

.cat-name {
  font-size: 18px;
  font-weight: 700;
  color: #e0e0e8;
  margin-bottom: 6px;
}

.cat-desc {
  font-size: 13px;
  color: #6b6b80;
  line-height: 1.5;
  margin-bottom: 12px;
}

.cat-meta {
  font-size: 12px;
  color: #4a4a5a;
}

.cat-arrow {
  margin-top: 12px;
  font-size: 13px;
  font-weight: 600;
  color: #6366f1;
  opacity: 0;
  transition: opacity 0.3s;
}

.cat-card:hover .cat-arrow {
  opacity: 1;
}

.cat-anime {
  background: linear-gradient(135deg, rgba(99,102,241,0.08), rgba(168,85,247,0.08)) !important;
  border-color: rgba(99,102,241,0.2) !important;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
