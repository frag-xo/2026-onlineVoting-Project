<template>
  <div class="pk-root">
    <!-- 顶部信息栏 -->
    <div class="pk-topbar">
      <div class="topbar-left">
        <button class="topbar-back" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
            <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
          </svg>
          返回
        </button>
      </div>
      <div class="topbar-title">🎯 动漫二选一</div>
      <div class="topbar-right">
        <span class="round-badge" v-if="!showResult">{{ currentRound }} / {{ maxRounds }}</span>
      </div>
    </div>

    <!-- 进度条 -->
    <div class="progress-track" v-if="!showResult">
      <div class="progress-fill" :style="{ width: (currentRound / maxRounds) * 100 + '%' }"></div>
    </div>

    <!-- 主内容 -->
    <div class="pk-body">
      <!-- VS 对战模式 -->
      <template v-if="!showResult">
        <div class="vs-stage" :key="battleKey">
          <div class="vs-hint">选择你更喜欢的作品</div>
          <div class="vs-cards">
            <div
              v-for="fighter in currentPair"
              :key="fighter.id"
              class="vs-card"
              :class="{ 'is-chosen': chosenId === fighter.id }"
              :style="{ animationDelay: (fighter.id % 2) * 0.1 + 's' }"
              @click="choose(fighter)"
            >
              <div class="card-glow"></div>
              <div class="card-img-wrap">
                <img :src="fighter.imageUrl" :alt="fighter.name" loading="lazy" />
                <div class="card-shine"></div>
              </div>
              <div class="card-name">{{ fighter.name }}</div>
            </div>
          </div>
          <div class="vs-divider">
            <span class="vs-logo">VS</span>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && currentPair.length === 0 && !showResult" class="empty-state">
          <p>暂无更多对战数据</p>
        </div>

        <!-- 加载 -->
        <div v-if="loading" class="load-state">
          <div class="loader" />
          <p>加载中...</p>
        </div>
      </template>

      <!-- 结果页面 -->
      <div v-else class="result-stage">
        <div class="result-header">
          <h2>🏆 你的动漫排行</h2>
          <p class="result-sub">已完成 {{ maxRounds }} 轮对战</p>
        </div>
        <div class="result-list" v-loading="rankLoading">
          <div
            v-for="(item, i) in ranking"
            :key="item.id"
            class="rank-item"
            :class="{ 'rank-top': i < 3 }"
            :style="{ animationDelay: i * 0.04 + 's' }"
          >
            <div class="rank-num">
              <span v-if="i === 0" class="medal">🥇</span>
              <span v-else-if="i === 1" class="medal">🥈</span>
              <span v-else-if="i === 2" class="medal">🥉</span>
              <span v-else class="rank-idx">{{ i + 1 }}</span>
            </div>
            <img class="rank-img" :src="item.imageUrl" :alt="item.name" />
            <div class="rank-info">
              <span class="rank-name">{{ item.name }}</span>
              <span class="rank-stat">胜率 {{ item.winRate }}% / ELO {{ item.eloRating }}</span>
            </div>
            <div class="rank-bar-wrap">
              <div class="rank-bar" :style="{ width: item.winRate + '%' }"></div>
            </div>
          </div>
          <el-empty v-if="!rankLoading && ranking.length === 0" description="还没有对战记录" />
        </div>
        <div class="result-actions">
          <button class="action-btn primary" @click="restart">🔄 再来一轮</button>
          <button class="action-btn ghost" @click="goBack">返回首页</button>
        </div>
      </div>
    </div>

    <!-- 底部 -->
    <div class="pk-footer" v-if="!showResult">
      <span>每轮选择你更喜欢的作品，{{ maxRounds }}轮后生成你的专属排行</span>
    </div>

    <!-- 入场动画覆盖层 -->
    <transition name="fade">
      <div v-if="entryAnim" class="entry-overlay" @animationend="entryAnim = false">
        <div class="entry-text">动漫二选一</div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAnimePair, submitAnimeBattle, getAnimeRanking } from '@/api/vote'

const router = useRouter()
const maxRounds = 30
const currentRound = ref(0)
const battleKey = ref(0)
const loading = ref(false)
const showResult = ref(false)
const rankLoading = ref(false)
const entryAnim = ref(true)
const currentPair = ref<any[]>([])
const chosenId = ref<number | null>(null)
const ranking = ref<any[]>([])

const loadPair = async () => {
  loading.value = true
  try {
    const data = await getAnimePair()
    currentPair.value = data || []
  } catch {
    ElMessage.error('加载对战数据失败')
  } finally {
    loading.value = false
  }
}

const choose = async (fighter: any) => {
  if (chosenId.value !== null || loading.value) return
  chosenId.value = fighter.id

  const loser = currentPair.value.find((f: any) => f.id !== fighter.id)
  if (!loser) return

  try {
    await submitAnimeBattle(fighter.id, loser.id)
  } catch { /* ignore */ }

  // 下一轮
  setTimeout(async () => {
    chosenId.value = null
    currentRound.value++
    if (currentRound.value >= maxRounds) {
      await loadRanking()
      showResult.value = true
    } else {
      battleKey.value++
      await loadPair()
    }
  }, 400)
}

const loadRanking = async () => {
  rankLoading.value = true
  try {
    ranking.value = await getAnimeRanking()
  } catch {
    ElMessage.error('加载排行失败')
  } finally {
    rankLoading.value = false
  }
}

const restart = () => {
  showResult.value = false
  currentRound.value = 0
  battleKey.value++
  ranking.value = []
  loadPair()
}

const goBack = () => router.push('/')

onMounted(() => {
  loadPair()
})
</script>

<style scoped>
.pk-root {
  min-height: 100dvh;
  background: #0a0a0f;
  color: #e8e8ed;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

/* ===== 顶部栏 ===== */
.pk-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  position: relative;
  z-index: 10;
}

.topbar-back {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: 1px solid rgba(255,255,255,0.1);
  color: #a0a0b0;
  padding: 6px 14px;
  border-radius: 10px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}
.topbar-back:hover {
  color: #fff;
  border-color: rgba(255,255,255,0.25);
  background: rgba(255,255,255,0.05);
}

.topbar-title {
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.round-badge {
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.1);
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 13px;
  color: #a0a0b0;
  font-weight: 500;
}

/* ===== 进度条 ===== */
.progress-track {
  height: 2px;
  background: rgba(255,255,255,0.06);
  margin: 0 24px 0;
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #6366f1, #a855f7);
  border-radius: 2px;
  transition: width 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

/* ===== 主体 ===== */
.pk-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
}

/* ===== VS对战模式 ===== */
.vs-stage {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  width: 100%;
  max-width: 720px;
  animation: fadeUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.vs-hint {
  font-size: 14px;
  color: #6b6b80;
  letter-spacing: 0.05em;
}

.vs-cards {
  display: flex;
  gap: 40px;
  width: 100%;
  justify-content: center;
  align-items: stretch;
}

.vs-card {
  flex: 1;
  max-width: 280px;
  cursor: pointer;
  position: relative;
  animation: cardIn 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.vs-card:hover {
  transform: translateY(-8px) scale(1.02);
}

.vs-card:hover .card-shine {
  opacity: 0.15;
}

.vs-card.is-chosen {
  transform: scale(1.05);
  z-index: 2;
}

.vs-card.is-chosen .card-glow {
  opacity: 0.4;
}

/* 卡片光晕 */
.card-glow {
  position: absolute;
  inset: -3px;
  border-radius: 18px;
  background: linear-gradient(135deg, #6366f1, #a855f7);
  opacity: 0;
  transition: opacity 0.4s;
  z-index: 0;
}

.vs-card:hover .card-glow {
  opacity: 0.2;
}

/* 图片区域 */
.card-img-wrap {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  aspect-ratio: 400 / 560;
  background: #14141f;
  z-index: 1;
}

.card-img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
}

.vs-card:hover .card-img-wrap img {
  transform: scale(1.06);
}

.card-shine {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0) 40%, rgba(255,255,255,0.08) 50%, rgba(255,255,255,0) 60%);
  opacity: 0;
  transition: opacity 0.4s;
  pointer-events: none;
}

/* 动漫名称 */
.card-name {
  text-align: center;
  margin-top: 12px;
  font-size: 15px;
  font-weight: 600;
  color: #e0e0e8;
  letter-spacing: 0.02em;
  position: relative;
  z-index: 1;
}

/* VS 标志 */
.vs-divider {
  position: absolute;
  z-index: 5;
  pointer-events: none;
}

.vs-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: #1a1a2e;
  border: 2px solid rgba(255,255,255,0.12);
  color: #a0a0b0;
  font-size: 14px;
  font-weight: 800;
  letter-spacing: 0.05em;
  box-shadow: 0 0 30px rgba(99, 102, 241, 0.15);
}

/* ===== 结果页 ===== */
.result-stage {
  width: 100%;
  max-width: 600px;
  animation: fadeUp 0.5s ease;
}

.result-header {
  text-align: center;
  margin-bottom: 28px;
}

.result-header h2 {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 6px;
  background: linear-gradient(135deg, #e0e0e8, #a0a0b0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.result-sub {
  font-size: 13px;
  color: #6b6b80;
  margin: 0;
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 16px;
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 14px;
  animation: fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) both;
  transition: all 0.2s;
}

.rank-item:hover {
  background: rgba(255,255,255,0.06);
  border-color: rgba(255,255,255,0.1);
}

.rank-top {
  background: rgba(255,215,0,0.04);
  border-color: rgba(255,215,0,0.1);
}

.rank-num {
  width: 36px;
  text-align: center;
  flex-shrink: 0;
}

.medal {
  font-size: 22px;
}

.rank-idx {
  font-size: 15px;
  font-weight: 700;
  color: #6b6b80;
}

.rank-img {
  width: 44px;
  height: 62px;
  border-radius: 8px;
  object-fit: cover;
  background: #14141f;
  flex-shrink: 0;
}

.rank-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.rank-name {
  font-size: 14px;
  font-weight: 600;
  color: #e0e0e8;
}

.rank-stat {
  font-size: 11.5px;
  color: #6b6b80;
}

.rank-bar-wrap {
  width: 80px;
  height: 4px;
  background: rgba(255,255,255,0.06);
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.rank-bar {
  height: 100%;
  background: linear-gradient(90deg, #6366f1, #a855f7);
  border-radius: 4px;
  transition: width 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

.result-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 28px;
}

.action-btn {
  padding: 10px 24px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.action-btn.primary {
  background: linear-gradient(135deg, #6366f1, #a855f7);
  color: #fff;
}
.action-btn.primary:hover {
  transform: scale(1.03);
  box-shadow: 0 8px 24px rgba(99,102,241,0.3);
}

.action-btn.ghost {
  background: rgba(255,255,255,0.06);
  color: #a0a0b0;
  border: 1px solid rgba(255,255,255,0.1);
}
.action-btn.ghost:hover {
  background: rgba(255,255,255,0.1);
  color: #e0e0e8;
}

/* ===== 底部 ===== */
.pk-footer {
  text-align: center;
  padding: 16px;
  font-size: 12px;
  color: #4a4a5a;
}

/* ===== 加载 ===== */
.load-state {
  text-align: center;
  color: #6b6b80;
}

.loader {
  width: 28px;
  height: 28px;
  border: 2px solid rgba(255,255,255,0.08);
  border-top-color: #6366f1;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 12px;
}

.empty-state {
  text-align: center;
  color: #6b6b80;
  font-size: 14px;
}

/* ===== 入场覆盖层 ===== */
.entry-overlay {
  position: fixed;
  inset: 0;
  background: #0a0a0f;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  animation: entryFade 1.8s ease forwards;
}

.entry-text {
  font-size: 36px;
  font-weight: 800;
  letter-spacing: 0.08em;
  background: linear-gradient(135deg, #6366f1, #a855f7, #ec4899);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: entryScale 1.8s ease forwards;
}

/* ===== 动画 ===== */
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes cardIn {
  from { opacity: 0; transform: translateY(30px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes entryFade {
  0% { opacity: 1; }
  60% { opacity: 1; }
  100% { opacity: 0; pointer-events: none; }
}

@keyframes entryScale {
  0% { transform: scale(0.6); opacity: 0; }
  30% { transform: scale(1.05); opacity: 1; }
  60% { transform: scale(1); opacity: 1; }
  100% { transform: scale(0.9); opacity: 0; }
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* ===== 滚动条 ===== */
.result-list::-webkit-scrollbar { width: 4px; }
.result-list::-webkit-scrollbar-track { background: transparent; }
.result-list::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 4px; }
</style>
