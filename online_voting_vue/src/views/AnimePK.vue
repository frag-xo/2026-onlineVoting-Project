<template>
  <div class="pk-root">
    <div class="pk-topbar">
      <div class="topbar-left">
        <button class="topbar-back" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
            <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
          </svg> 返回
        </button>
      </div>
      <div class="topbar-title">🎯 动漫二选一</div>
      <div class="topbar-right">
        <span class="round-badge" v-if="!showResult">{{ currentRound }} / {{ maxRounds }}</span>
      </div>
    </div>

    <div class="progress-track" v-if="!showResult">
      <div class="progress-fill" :style="{ width: (currentRound / maxRounds) * 100 + '%' }"></div>
    </div>

    <div class="pk-body">
      <!-- VS 对战 -->
      <template v-if="!showResult">
        <div class="vs-hint">选择你更喜欢的作品</div>

        <div class="vs-arena">
          <!-- 左卡 -->
          <div class="vs-card-wrap">
            <Transition name="card-swap" mode="out-in">
              <div
                v-if="fighterLeft"
                :key="fighterLeft.id"
                class="vs-card"
                :class="{ 'is-chosen': chosenId === fighterLeft.id, 'is-eliminated': eliminatedId === fighterLeft.id }"
                @click="choose(fighterLeft)"
              >
                <div class="card-glow"></div>
                <div class="card-img-wrap">
                  <img class="card-image" :src="BASE_URL + fighterLeft.imageUrl" :alt="fighterLeft.name" loading="lazy" @error="onImgError" />
                  <div class="card-shine"></div>
                </div>
                <div class="card-name">{{ fighterLeft.name }}</div>
              </div>
            </Transition>
          </div>

          <!-- VS 标志 -->
          <div class="vs-badge">VS</div>

          <!-- 右卡 -->
          <div class="vs-card-wrap">
            <Transition name="card-swap" mode="out-in">
              <div
                v-if="fighterRight"
                :key="fighterRight.id"
                class="vs-card"
                :class="{ 'is-chosen': chosenId === fighterRight.id, 'is-eliminated': eliminatedId === fighterRight.id }"
                @click="choose(fighterRight)"
              >
                <div class="card-glow"></div>
                <div class="card-img-wrap">
                  <img class="card-image" :src="BASE_URL + fighterRight.imageUrl" :alt="fighterRight.name" loading="lazy" @error="onImgError" />
                  <div class="card-shine"></div>
                </div>
                <div class="card-name">{{ fighterRight.name }}</div>
              </div>
            </Transition>
          </div>
        </div>

        <div v-if="loading" class="load-state">
          <div class="loader" /><p>加载中...</p>
        </div>
      </template>

      <!-- 🎬 终选动画 -->
      <template v-if="showFinale && finalChosen">
        <div class="vs-arena" style="max-width:700px;">
          <div class="finale-card">
            <div class="finale-glow"></div>
            <div class="card-img-wrap" style="max-width:260px;">
              <img class="card-image" :src="BASE_URL + finalChosen.imageUrl" :alt="finalChosen.name" />
            </div>
            <div class="card-name" style="font-size:18px;">{{ finalChosen.name }}</div>
          </div>
          <div class="vs-badge" style="opacity:0;">VS</div>
          <div class="finale-text">
            <span class="finale-line1">原来你是</span>
            <span class="finale-line2">真爱粉</span>
            <button class="finale-btn" @click="showRanking">查看排行 →</button>
          </div>
        </div>
      </template>

      <!-- 结果页面 -->
      <div v-if="showResult" class="result-stage">
        <div class="result-header">
          <h2>🏆 你的动漫排行</h2>
          <p class="result-sub">已完成 {{ maxRounds }} 轮对战</p>
        </div>
        <div class="result-list" v-loading="rankLoading">
          <div v-for="(item, i) in ranking" :key="item.id" class="rank-item" :class="{ 'rank-top': i < 3 }" :style="{ animationDelay: i * 0.04 + 's' }">
            <div class="rank-num">
              <span v-if="i === 0" class="medal">🥇</span>
              <span v-else-if="i === 1" class="medal">🥈</span>
              <span v-else-if="i === 2" class="medal">🥉</span>
              <span v-else class="rank-idx">{{ i + 1 }}</span>
            </div>
            <img class="rank-img" :src="BASE_URL + item.imageUrl" :alt="item.name" />
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

    <div class="pk-footer" v-if="!showResult">每轮选择你更喜欢的作品，{{ maxRounds }}轮后生成专属排行</div>

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
import { submitAnimeBattle, getAnimeRanking } from '@/api/vote'
import request from '@/api'

const BASE_URL = 'http://localhost:8080'
const router = useRouter()
const loading = ref(false)
const showResult = ref(false)
const showFinale = ref(false)
const finalChosen = ref<any>(null)
const rankLoading = ref(false)
const entryAnim = ref(true)
const fighterLeft = ref<any>(null)
const fighterRight = ref<any>(null)
const chosenId = ref<number | null>(null)
const eliminatedId = ref<number | null>(null)
const ranking = ref<any[]>([])
const currentRound = ref(0)
const maxRounds = ref(0)

// 全部动漫池子
let allFighters: any[] = []
let usedChallengerIds = new Set<number>()
let currentWinner: any = null

/** 初始化 */
const initPool = async () => {
  loading.value = true
  try {
    const data = await request.get('/anime/list')
    allFighters = (data || []).sort(() => Math.random() - 0.5)
    usedChallengerIds.clear()
    currentWinner = null
    currentRound.value = 0
    maxRounds.value = 30
    // 第一轮：取前两作为初始对战
    fighterLeft.value = { ...allFighters[0] }
    fighterRight.value = { ...allFighters[1] }
    usedChallengerIds.add(allFighters[0].id)
    usedChallengerIds.add(allFighters[1].id)
    chosenId.value = null
    eliminatedId.value = null
  } catch {
    ElMessage.error('加载动漫数据失败')
  } finally {
    loading.value = false
  }
}

/** 取一个未出场过的挑战者 */
const getFreshChallenger = () => {
  // 排除所有已出场 + 当前在场上的
  const exclude = new Set(usedChallengerIds)
  if (fighterLeft.value) exclude.add(fighterLeft.value.id)
  if (fighterRight.value) exclude.add(fighterRight.value.id)
  const available = allFighters.filter(f => !exclude.has(f.id))
  if (available.length === 0) return null
  return available[Math.floor(Math.random() * available.length)]
}

const choose = async (fighter: any) => {
  if (chosenId.value !== null || loading.value) return

  const loser = fighter.id === fighterLeft.value?.id ? fighterRight.value : fighterLeft.value
  if (!loser) return

  chosenId.value = fighter.id
  eliminatedId.value = loser.id
  currentWinner = fighter

  try {
    await submitAnimeBattle(fighter.id, loser.id)
  } catch { /* ignore */ }

  setTimeout(async () => {
    currentRound.value++
    if (currentRound.value >= maxRounds.value) {
      await finishBattle()
      return
    }

    // 新挑战者（未出场过的）
    const challenger = getFreshChallenger()
    if (!challenger) {
      await finishBattle()
      return
    }
    usedChallengerIds.add(challenger.id)

    // 胜者保留原位，挑战者去另一边
    if (fighterLeft.value?.id === fighter.id) {
      fighterLeft.value = { ...fighter }
      fighterRight.value = { ...challenger }
    } else {
      fighterRight.value = { ...fighter }
      fighterLeft.value = { ...challenger }
    }
    chosenId.value = null
    eliminatedId.value = null
  }, 500)
}

const finishBattle = async () => {
  // 记录最后一选
  const last = fighterLeft.value?.id === chosenId.value ? fighterLeft.value : fighterRight.value
  if (last) finalChosen.value = { ...last }
  fighterLeft.value = null
  fighterRight.value = null
  chosenId.value = null
  eliminatedId.value = null

  // 显示终选画面
  showFinale.value = true
}

const showRanking = async () => {
  showFinale.value = false
  showResult.value = true
  rankLoading.value = true
  try { ranking.value = await getAnimeRanking() }
  catch { ElMessage.error('加载排行失败') }
  finally { rankLoading.value = false }
}

const loadRanking = async () => {
  rankLoading.value = true
  try { ranking.value = await getAnimeRanking() }
  catch { ElMessage.error('加载排行失败') }
  finally { rankLoading.value = false }
}

const restart = () => {
  showResult.value = false
  showFinale.value = false
  finalChosen.value = null
  ranking.value = []
  initPool()
}

const onImgError = (e: any) => { e.target.style.display = 'none' }
const goBack = () => router.push('/')

onMounted(() => initPool())
</script>

<style scoped>
.pk-root {
  min-height: 100dvh;
  background: #0a0a0f;
  color: #e8e8ed;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  display: flex;
  flex-direction: column;
}

.pk-topbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 24px;
}
.topbar-back {
  display: flex; align-items: center; gap: 4px;
  background: none; border: 1px solid rgba(255,255,255,0.1); color: #a0a0b0;
  padding: 6px 14px; border-radius: 10px; font-size: 13px; cursor: pointer;
  transition: all 0.2s;
}
.topbar-back:hover { color: #fff; border-color: rgba(255,255,255,0.25); background: rgba(255,255,255,0.05); }
.topbar-title { font-size: 16px; font-weight: 600; }
.round-badge {
  background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1);
  padding: 4px 14px; border-radius: 20px; font-size: 13px; color: #a0a0b0; font-weight: 500;
}

.progress-track { height: 2px; background: rgba(255,255,255,0.06); margin: 0 24px; border-radius: 2px; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #6366f1, #a855f7); transition: width 0.4s cubic-bezier(0.16, 1, 0.3, 1); }

.pk-body { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 20px; }

.vs-hint { font-size: 14px; color: #6b6b80; margin-bottom: 32px; letter-spacing: 0.05em; }

/* ===== 竞技场 ===== */
.vs-arena {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  width: 100%;
  max-width: 700px;
  position: relative;
}

.vs-card {
  cursor: pointer;
  position: relative;
  transition: transform 0.35s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.35s ease;
  animation: cardIn 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
}
.vs-card {
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.vs-card:hover {
  transform: translateY(-6px) scale(1.015);
}
.vs-card:hover .card-img-wrap {
  box-shadow:
    0 0 0 1.5px rgba(255,255,255,0.08),
    0 0 40px rgba(99,102,241,0.08);
}
.vs-card:hover .card-image {
  transform: scale(1.04);
}
.vs-card.is-chosen {
  transform: scale(1.03); z-index: 2;
}
.vs-card.is-chosen .card-glow {
  opacity: 0.4;
}
.vs-card.is-eliminated {
  opacity: 0.2; transform: scale(0.9); pointer-events: none;
}

.card-image {
  width: 100%; height: 100%; object-fit: cover; display: block;
  transition: transform 0.5s ease;
}

.card-glow {
  position: absolute; inset: -2px; border-radius: 18px;
  background: linear-gradient(135deg, #6366f1, #a855f7);
  opacity: 0;
  transition: opacity 0.5s ease;
  z-index: 0;
  filter: blur(4px);
}
.vs-card:hover .card-glow {
  opacity: 0.15;
}

.card-img-wrap {
  position: relative; border-radius: 16px; overflow: hidden;
  aspect-ratio: 400 / 560; background: #14141f; z-index: 1;
}
.card-img-wrap { transition: box-shadow 0.4s ease; }

.card-shine {
  position: absolute; inset: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0) 30%, rgba(255,255,255,0.04) 50%, rgba(255,255,255,0) 70%);
  opacity: 0;
  transition: opacity 0.6s ease;
  pointer-events: none;
}
.vs-card:hover .card-shine {
  opacity: 0.6;
}

.card-name {
  text-align: center; margin-top: 12px; font-size: 15px; font-weight: 600;
  color: #e0e0e8; letter-spacing: 0.02em; position: relative; z-index: 1;
}

/* ===== VS 标志 ===== */
.vs-badge {
  width: 48px; height: 48px;
  border-radius: 50%;
  background: #1a1a2e;
  border: 2px solid rgba(255,255,255,0.12);
  color: #a0a0b0;
  font-size: 13px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  letter-spacing: 0.04em;
  box-shadow: 0 0 30px rgba(99, 102, 241, 0.12);
  z-index: 3;
}

/* ===== 结果页 ===== */
.result-stage { width: 100%; max-width: 600px; animation: fadeUp 0.5s ease; }
.result-header { text-align: center; margin-bottom: 28px; }
.result-header h2 { font-size: 24px; font-weight: 700; margin: 0 0 6px; background: linear-gradient(135deg, #e0e0e8, #a0a0b0); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.result-sub { font-size: 13px; color: #6b6b80; margin: 0; }
.result-list { display: flex; flex-direction: column; gap: 8px; max-height: 440px; overflow-y: auto; }

.rank-item {
  display: flex; align-items: center; gap: 14px; padding: 12px 16px;
  background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06);
  border-radius: 14px; animation: fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) both;
}
.rank-item:hover { background: rgba(255,255,255,0.06); }
.rank-top { background: rgba(255,215,0,0.04); border-color: rgba(255,215,0,0.1); }
.rank-num { width: 36px; text-align: center; flex-shrink: 0; }
.medal { font-size: 22px; }
.rank-idx { font-size: 15px; font-weight: 700; color: #6b6b80; }
.rank-img { width: 44px; height: 62px; border-radius: 8px; object-fit: cover; background: #14141f; flex-shrink: 0; }
.rank-info { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 2px; }
.rank-name { font-size: 14px; font-weight: 600; color: #e0e0e8; }
.rank-stat { font-size: 11.5px; color: #6b6b80; }
.rank-bar-wrap { width: 80px; height: 4px; background: rgba(255,255,255,0.06); border-radius: 4px; overflow: hidden; flex-shrink: 0; }
.rank-bar { height: 100%; background: linear-gradient(90deg, #6366f1, #a855f7); border-radius: 4px; transition: width 0.6s ease; }

.result-actions { display: flex; justify-content: center; gap: 12px; margin-top: 28px; }
.action-btn { padding: 10px 24px; border-radius: 12px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; border: none; }
.action-btn.primary { background: linear-gradient(135deg, #6366f1, #a855f7); color: #fff; }
.action-btn.primary:hover { transform: scale(1.03); box-shadow: 0 8px 24px rgba(99,102,241,0.3); }
.action-btn.ghost { background: rgba(255,255,255,0.06); color: #a0a0b0; border: 1px solid rgba(255,255,255,0.1); }
.action-btn.ghost:hover { background: rgba(255,255,255,0.1); color: #e0e0e8; }

.pk-footer { text-align: center; padding: 16px; font-size: 12px; color: #4a4a5a; }
.load-state { text-align: center; color: #6b6b80; }
.loader { width: 28px; height: 28px; border: 2px solid rgba(255,255,255,0.08); border-top-color: #6366f1; border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto 12px; }

/* 入场动画 */
.entry-overlay { position: fixed; inset: 0; background: #0a0a0f; display: flex; align-items: center; justify-content: center; z-index: 100; animation: entryFade 1.8s ease forwards; }
.entry-text { font-size: 36px; font-weight: 800; letter-spacing: 0.08em; background: linear-gradient(135deg, #6366f1, #a855f7, #ec4899); -webkit-background-clip: text; -webkit-text-fill-color: transparent; animation: entryScale 1.8s ease forwards; }

@keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes cardIn { from { opacity: 0; transform: translateY(30px) scale(0.95); } to { opacity: 1; transform: translateY(0) scale(1); } }
@keyframes spin { to { transform: rotate(360deg); } }
@keyframes entryFade { 0% { opacity: 1; } 60% { opacity: 1; } 100% { opacity: 0; pointer-events: none; } }
@keyframes entryScale { 0% { transform: scale(0.6); opacity: 0; } 30% { transform: scale(1.05); opacity: 1; } 60% { transform: scale(1); opacity: 1; } 100% { transform: scale(0.9); opacity: 0; } }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.result-list::-webkit-scrollbar { width: 4px; }
.result-list::-webkit-scrollbar-track { background: transparent; }
.result-list::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 4px; }

/* ===== 卡片切换动画 ===== */
.card-swap-enter-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}
.card-swap-leave-active {
  transition: all 0.25s ease;
}

.vs-card-wrap {
  flex: 1;
  max-width: 260px;
  position: relative;
}
.card-swap-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}
.card-swap-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.92);
}

/* ===== 🎬 终选动画 ===== */
.finale-card {
  flex: 1;
  max-width: 260px;
  position: relative;
  animation: finaleCardIn 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
}

.finale-glow {
  position: absolute; inset: -4px; border-radius: 18px;
  background: linear-gradient(135deg, #6366f1, #a855f7, #ec4899);
  opacity: 0.4; animation: glowPulse 2.5s ease-in-out infinite;
  z-index: 0;
  filter: blur(3px);
}

.finale-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}

.finale-line1 {
  font-size: 28px;
  font-weight: 300;
  color: rgba(255,255,255,0.5);
  letter-spacing: 0.12em;
  animation: textIn 0.8s 0.4s cubic-bezier(0.16, 1, 0.3, 1) both;
}

.finale-line2 {
  font-size: 52px;
  font-weight: 800;
  letter-spacing: 0.08em;
  background: linear-gradient(135deg, #6366f1, #a855f7, #ec4899);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: textIn 0.8s 0.7s cubic-bezier(0.16, 1, 0.3, 1) both;
}

@keyframes finaleCardIn {
  from { opacity: 0; transform: scale(0.92); }
  to { opacity: 1; transform: scale(1); }
}

@keyframes glowPulse {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.04); }
}

.finale-btn {
  margin-top: 20px;
  padding: 10px 28px;
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.12);
  background: rgba(255,255,255,0.04);
  color: #a0a0b0;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  letter-spacing: 0.03em;
  animation: textIn 0.8s 1.1s cubic-bezier(0.16, 1, 0.3, 1) both;
}
.finale-btn:hover {
  background: linear-gradient(135deg, #6366f1, #a855f7);
  border-color: transparent;
  color: #fff;
  transform: scale(1.03);
  box-shadow: 0 8px 24px rgba(99,102,241,0.25);
}

@keyframes textIn {
  from { opacity: 0; transform: translateX(30px); }
  to { opacity: 1; transform: translateX(0); }
}
</style>
