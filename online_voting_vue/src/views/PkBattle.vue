<template>
  <div class="pk-root">
    <div class="pk-topbar">
      <button class="topbar-back" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg> 返回
      </button>
      <span class="topbar-title">{{ categoryName }}</span>
      <span class="round-badge" v-if="!showResult">{{ currentRound }} / {{ maxRounds }}</span>
    </div>

    <div class="progress-track" v-if="!showResult">
      <div class="progress-fill" :style="{ width: (currentRound / maxRounds) * 100 + '%' }"></div>
    </div>

    <div class="pk-body">
      <!-- 选择页 -->
      <template v-if="!showResult">
        <div class="vs-hint">你会选哪个？</div>
        <div class="vs-arena" :key="battleKey">
          <div class="vs-card" @click="choose('A')">
            <div class="card-bg" :style="{ background: 'linear-gradient(135deg, #6366f1, #4f46e5)' }"></div>
            <span class="card-text">{{ currentPair?.optionA }}</span>
          </div>
          <div class="vs-badge">OR</div>
          <div class="vs-card" @click="choose('B')">
            <div class="card-bg" :style="{ background: 'linear-gradient(135deg, #a855f7, #9333ea)' }"></div>
            <span class="card-text">{{ currentPair?.optionB }}</span>
          </div>
        </div>
        <div class="round-hint">第 {{ currentRound + 1 }} 轮 · 共 {{ maxRounds }} 轮</div>
      </template>

      <!-- 结果页 -->
      <div v-else class="result-stage">
        <div class="result-icon">{{ categoryIcon }}</div>
        <div class="result-title">{{ categoryName }}</div>

        <div class="result-card" v-if="result">
          <div class="result-badge">🏅 {{ result.title }}</div>
          <div class="result-analysis">{{ result.analysis }}</div>
          <div class="result-stat">共 {{ result.totalRounds }} 轮对战</div>
        </div>

        <div class="result-actions">
          <button class="action-btn primary" @click="restart">🔄 再来一轮</button>
          <button class="action-btn ghost" @click="goBack">返回分类</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPkPair, submitPkBattle, getPkResult } from '@/api/vote'

const router = useRouter()
const route = useRoute()
const categoryId = Number(route.params.id)
const categoryName = route.query.name as string || 'PK'
const categoryIcon = route.query.icon as string || '🔥'

const maxRounds = 10
const currentRound = ref(0)
const battleKey = ref(0)
const loading = ref(false)
const showResult = ref(false)
const currentPair = ref<any>(null)
const result = ref<any>(null)
// 胜者保留模式（仅奶茶=categoryId=6）
let allPairs: any[] = []
let challengerPool: string[] = []
let currentWinner: string | null = null
const isChampionMode = categoryId === 6  // 只有奶茶用冠军模式

const loadPairs = async () => {
  loading.value = true
  try {
    const { getPkPairs } = await import('@/api/vote')
    allPairs = await getPkPairs ? await getPkPairs(categoryId) : []
    if (allPairs.length === 0) {
      try { currentPair.value = await getPkPair(categoryId) } catch {}
      return
    }

    if (isChampionMode) {
      // 冠军模式：第一对初始对战，剩余打散为挑战者池
      const first = allPairs.shift()
      currentPair.value = { optionA: first.optionA, optionB: first.optionB }
      challengerPool = allPairs.flatMap((p: any) => [p.optionA, p.optionB])
        .sort(() => Math.random() - 0.5)
      currentWinner = null
    } else {
      // 普通模式：所有话题按顺序出
      challengerPool = allPairs.map((p: any) => p)
      nextRound()
    }
  } catch {
    try { currentPair.value = await getPkPair(categoryId) } catch {}
  }
  finally { loading.value = false }
}

const nextRound = () => {
  if (challengerPool.length > 0) {
    const pair = challengerPool.shift()
    currentPair.value = { optionA: pair.optionA, optionB: pair.optionB }
  } else {
    currentPair.value = null
  }
}

const choose = async (side: string) => {
  if (!currentPair.value || loading.value) return
  const chosen = side === 'A' ? currentPair.value.optionA : currentPair.value.optionB

  try {
    await submitPkBattle(categoryId, currentPair.value.id || categoryId * 100 + currentRound.value, chosen)
  } catch { /* ignore */ }

  currentRound.value++

  if (isChampionMode) {
    // 冠军模式：胜者留守
    if (challengerPool.length === 0 || currentRound.value >= maxRounds) {
      try { result.value = await getPkResult(categoryId) } catch {}
      showResult.value = true
    } else {
      const next = challengerPool.shift()
      battleKey.value++
      if (side === 'A') {
        currentPair.value = { optionA: chosen, optionB: next }
      } else {
        currentPair.value = { optionA: next, optionB: chosen }
      }
    }
  } else {
    // 普通模式：换下一对
    if (currentRound.value >= maxRounds || challengerPool.length === 0) {
      try { result.value = await getPkResult(categoryId) } catch {}
      showResult.value = true
    } else {
      battleKey.value++
      nextRound()
    }
  }
}

const restart = () => {
  showResult.value = false
  currentRound.value = 0
  result.value = null
  currentWinner = null
  loadPairs()
}

const goBack = () => router.push('/pk')

onMounted(() => loadPairs())
</script>

<style scoped>
.pk-root {
  min-height: 100dvh;
  background: #0a0a0f;
  color: #e8e8ed;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  display: flex; flex-direction: column;
}

.pk-topbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 20px;
}
.topbar-back {
  display: flex; align-items: center; gap: 4px;
  background: none; border: 1px solid rgba(255,255,255,0.1); color: #a0a0b0;
  padding: 6px 12px; border-radius: 10px; font-size: 13px; cursor: pointer;
}
.topbar-back:hover { color: #fff; }
.topbar-title { font-size: 16px; font-weight: 600; }
.round-badge {
  background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1);
  padding: 4px 14px; border-radius: 20px; font-size: 13px; color: #a0a0b0;
}

.progress-track { height: 2px; background: rgba(255,255,255,0.06); margin: 0 20px; border-radius: 2px; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #6366f1, #a855f7); transition: width 0.4s ease; }

.pk-body { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 20px; }

.vs-hint { font-size: 14px; color: #6b6b80; margin-bottom: 28px; }

.vs-arena {
  display: flex; align-items: center; gap: 20px;
  width: 100%; max-width: 600px;
}
.vs-card {
  flex: 1; height: 120px;
  border-radius: 16px;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden;
  transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
  animation: cardIn 0.4s ease both;
}
.vs-card:hover {
  transform: scale(1.06);
  box-shadow: 0 12px 40px rgba(99,102,241,0.2);
}
.vs-card:active {
  transform: scale(0.95);
}

.card-bg {
  position: absolute; inset: 0;
}
.card-text {
  position: relative; z-index: 1;
  font-size: 20px; font-weight: 700;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
  text-align: center;
  padding: 0 12px;
}

.vs-badge {
  width: 44px; height: 44px; border-radius: 50%;
  background: #1a1a2e; border: 2px solid rgba(255,255,255,0.1);
  color: #a0a0b0; font-size: 12px; font-weight: 800;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.round-hint {
  margin-top: 24px;
  font-size: 13px; color: #4a4a5a;
}

/* 结果 */
.result-stage {
  width: 100%; max-width: 480px;
  display: flex; flex-direction: column; align-items: center;
  animation: fadeUp 0.5s ease;
}
.result-icon { font-size: 48px; margin-bottom: 4px; }
.result-title { font-size: 20px; font-weight: 700; margin-bottom: 20px; }

.result-card {
  width: 100%;
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 16px;
  padding: 24px;
  text-align: center;
}
.result-badge {
  font-size: 22px; font-weight: 800;
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 6px;
}
.result-analysis {
  font-size: 18px;
  font-weight: 700;
  color: #e0e0e8;
  margin-bottom: 8px;
  animation: textIn 0.6s ease;
}
.result-stat { font-size: 13px; color: #6b6b80; margin-bottom: 16px; }
.choice-row {
  display: flex; align-items: center; gap: 10px;
  padding: 6px 0;
}
.choice-name { width: 80px; font-size: 13px; color: #a0a0b0; flex-shrink: 0; overflow: hidden; text-overflow: ellipsis; }
.choice-bar-wrap { flex: 1; height: 6px; background: rgba(255,255,255,0.06); border-radius: 4px; overflow: hidden; }
.choice-bar { height: 100%; background: linear-gradient(90deg, #6366f1, #a855f7); border-radius: 4px; transition: width 0.6s ease; }
.choice-count { width: 36px; font-size: 12px; color: #6b6b80; text-align: right; }

.result-actions { display: flex; gap: 10px; margin-top: 20px; }
.action-btn {
  padding: 10px 22px; border-radius: 12px; font-size: 14px; font-weight: 600;
  cursor: pointer; border: none; transition: all 0.2s;
}
.action-btn.primary { background: linear-gradient(135deg, #6366f1, #a855f7); color: #fff; }
.action-btn.primary:hover { transform: scale(1.03); }
.action-btn.ghost { background: rgba(255,255,255,0.06); color: #a0a0b0; border: 1px solid rgba(255,255,255,0.1); }
.action-btn.ghost:hover { color: #e0e0e8; }

@keyframes cardIn { from { opacity: 0; transform: translateY(20px) scale(0.95); } to { opacity: 1; transform: translateY(0) scale(1); } }
@keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes textIn { from { opacity: 0; transform: translateX(10px); } to { opacity: 1; transform: translateX(0); } }
</style>
