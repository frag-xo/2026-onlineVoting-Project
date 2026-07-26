<template>
  <div class="detail-container">
    <el-card v-loading="loading" class="detail-card">
      <template #header>
        <div class="detail-header">
          <h2>{{ vote.title }}</h2>
          <div>
            <el-tag :type="vote.status === 0 ? 'warning' : vote.status === 1 ? 'success' : 'info'">
              {{ vote.status === 0 ? '未开始' : vote.status === 1 ? '进行中' : '已结束' }}
            </el-tag>
            <el-button
                :type="isFav ? 'danger' : 'default'"
                @click="toggleFav"
                style="margin-left:10px;"
            >
              {{ isFav ? '已收藏' : '收藏' }}
            </el-button>
            <el-button
                :type="isLiked ? 'primary' : 'default'"
                @click="toggleLike"
                style="margin-left:10px;"
            >
              👍 {{ likeCount }}
            </el-button>
          </div>
        </div>
      </template>

      <!-- 倒计时 -->
      <div v-if="!vote.isExpired" class="countdown">
        ⏰ 剩余时间：<span class="countdown-num">{{ countdown }}</span>
      </div>

      <div class="vote-meta">
        <span>⏰ 截止时间：{{ vote.deadline }}</span>
        <span>👥 参与人数：{{ vote.totalVotes || 0 }}</span>
      </div>

      <el-divider />

      <div class="options-area">
        <h3>请选择：</h3>
        <el-radio-group v-model="selectedOption">
          <el-radio
              v-for="opt in vote.options"
              :key="opt.optionId"
              :label="opt.optionId"
              :disabled="vote.isExpired"
              class="option-radio"
          >
            {{ opt.text }}
          </el-radio>
        </el-radio-group>
      </div>

      <el-divider />

      <!-- 验证码 -->
      <div class="captcha-area">
        <el-form :model="captchaForm" label-width="80px">
          <el-form-item label="验证码">
            <el-input
                v-model="captchaForm.code"
                placeholder="请输入验证码"
                style="width: 200px; margin-right: 10px;"
            />
            <img
                :src="captchaImage"
                alt="验证码"
                class="captcha-img"
                @click="refreshCaptcha"
            />
            <span style="font-size:12px; color:#909399; margin-left:10px;">点击图片刷新</span>
          </el-form-item>
        </el-form>
      </div>

      <div class="submit-area">
        <el-button
            type="primary"
            :disabled="vote.status !== 1 || !selectedOption"
            :loading="submitting"
            @click="handleSubmit"
        >
          {{ vote.status === 0 ? '未开始' : vote.status === 2 ? '已结束' : '提交投票' }}
        </el-button>
        <el-button @click="goBack">返回列表</el-button>
      </div>
    </el-card>

    <!-- 评论区 -->
    <el-card style="margin-top:20px;" class="comment-card">
      <template #header>
        <span><strong>💬 评论 ({{ comments.length }})</strong></span>
      </template>
      <div class="comment-input">
        <el-input
            v-model="newComment"
            placeholder="发表你的看法..."
            style="width: 80%; margin-right: 10px;"
            maxlength="200"
            show-word-limit
        />
        <el-button type="primary" @click="submitComment" :loading="commentLoading">发表</el-button>
      </div>
      <div class="comment-list" v-loading="commentLoading">
        <div v-for="item in comments" :key="item.id" class="comment-item">
          <div class="comment-user">{{ item.userName || item.user || '匿名' }}</div>
          <div class="comment-content">{{ item.content }}</div>
          <div class="comment-time">{{ item.createTime || item.time || '' }}</div>
        </div>
        <el-empty v-if="!comments.length" description="暂无评论，快来发表你的看法吧！" />
      </div>
    </el-card>

    <!-- 满意度调查弹窗 -->
    <el-dialog v-model="showSurvey" title="投票满意度调查" width="400px">
      <p>您对本次投票体验满意吗？</p>
      <el-rate v-model="surveyScore" :texts="['很差', '较差', '一般', '满意', '很满意']" show-text />
      <template #footer>
        <el-button @click="showSurvey = false">稍后评价</el-button>
        <el-button type="primary" @click="submitSurvey">提交评价</el-button>
      </template>
    </el-dialog>

    <!-- ===== 转盘抽奖弹窗 ===== -->
    <el-dialog
        v-model="showWheel"
        title="🎡 幸运大转盘"
        width="520px"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        @close="onWheelClose"
    >
      <div class="wheel-container">
        <div class="wheel-wrapper">
          <canvas ref="wheelCanvas" width="400" height="400"></canvas>
          <div class="pointer" @click="startDraw">🎯</div>
          <div v-if="isSpinning" class="spinning-mask">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>抽奖中...</span>
          </div>
        </div>

        <!-- 中奖结果 -->
        <div v-if="wheelResult" class="result-box">
          <el-alert
              :title="wheelResult.message"
              :type="wheelResult.points > 0 ? 'success' : 'info'"
              :closable="false"
              show-icon
          >
            <template v-if="wheelResult.points > 0">
              <span>🎉 恭喜获得 <strong>{{ wheelResult.points }}</strong> 积分！</span>
            </template>
            <template v-else>
              <span>{{ wheelResult.message || '😅 谢谢参与，下次好运！' }}</span>
            </template>
          </el-alert>
        </div>

        <!-- 底部按钮 -->
        <div class="wheel-actions">
          <el-button
              v-if="!wheelResult && !isSpinning"
              type="primary"
              @click="startDraw"
          >
            🎰 开始抽奖
          </el-button>
          <el-button v-if="wheelResult" type="success" @click="onWheelClose">
            太好了！
          </el-button>
          <el-button v-if="wheelResult" type="info" plain @click="skipDraw">
            下次再玩
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import {
  getVoteDetail,
  submitVote,
  getCaptcha,
  checkFavorited,
  favoriteVote,
  unfavoriteVote,
  getComments,
  addComment,
  getLikeCount,
  wheelDraw
} from '@/api/vote'

const router = useRouter()
const route = useRoute()
const voteId = Number(route.params.id)
import { getUserId } from '@/utils/auth'
const userId = getUserId()

// ===== 投票数据 =====
const vote = ref({
  id: 0,
  title: '',
  deadline: '',
  status: 0,
  isExpired: false,
  totalVotes: 0,
  options: [] as { optionId: number; text: string }[]
})
const selectedOption = ref<number>()
const loading = ref(false)
const submitting = ref(false)
const captchaId = ref('')
const captchaImage = ref('')
const captchaForm = reactive({ code: '' })

// ===== 收藏 =====
const isFav = ref(false)
const favLoading = ref(false)

const toggleFav = async () => {
  if (!userId) {
    ElMessage.warning('请先登录')
    return
  }
  favLoading.value = true
  try {
    if (isFav.value) {
      await unfavoriteVote(voteId)
      isFav.value = false
      ElMessage.success('已取消收藏')
    } else {
      await favoriteVote(voteId)
      isFav.value = true
      ElMessage.success('收藏成功')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    favLoading.value = false
  }
}

const checkFavStatus = async () => {
  if (!userId) return
  try {
    const data: any = await checkFavorited(voteId)
    isFav.value = data === true || data === 1
  } catch (error: any) {
    console.warn('检查收藏状态失败', error.message)
  }
}

// ===== 点赞 =====
const isLiked = ref(false)
const likeCount = ref(0)

const loadLikeCount = async () => {
  try {
    const data: any = await getLikeCount(voteId)
    likeCount.value = data || 0
  } catch (error) {
    console.warn('加载点赞数失败', error)
    likeCount.value = 0
  }
}

const toggleLike = async () => {
  if (!userId) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    isLiked.value = !isLiked.value
    likeCount.value += isLiked.value ? 1 : -1
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// ===== 倒计时 =====
const countdown = ref('')
let timer: any = null

const updateCountdown = () => {
  if (!vote.value.deadline) return
  const now = Date.now()
  const end = new Date(vote.value.deadline).getTime()
  const diff = end - now
  if (diff <= 0) {
    countdown.value = '已截止'
    return
  }
  const days = Math.floor(diff / 86400000)
  const hours = Math.floor((diff % 86400000) / 3600000)
  const minutes = Math.floor((diff % 3600000) / 60000)
  countdown.value = `${days}天 ${hours}时 ${minutes}分`
}

// ===== 评论 =====
const commentLoading = ref(false)
const comments = ref<any[]>([])
const newComment = ref('')

const loadComments = async () => {
  commentLoading.value = true
  try {
    const data: any = await getComments(voteId)
    comments.value = data || []
  } catch (error: any) {
    console.warn('加载评论失败', error.message)
    comments.value = []
  } finally {
    commentLoading.value = false
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  if (!userId) {
    ElMessage.warning('请先登录')
    return
  }
  commentLoading.value = true
  try {
    await addComment(voteId, newComment.value)
    ElMessage.success('评论发表成功')
    newComment.value = ''
    await loadComments()
  } catch (error: any) {
    ElMessage.error(error.message || '发表评论失败')
  } finally {
    commentLoading.value = false
  }
}

// ===== 满意度调查 =====
const showSurvey = ref(false)
const surveyScore = ref(0)

const submitSurvey = () => {
  if (surveyScore.value === 0) {
    ElMessage.warning('请选择评分')
    return
  }
  ElMessage.success('感谢您的评价！')
  showSurvey.value = false
  surveyScore.value = 0
}

// ===== 加载详情 =====
const loadDetail = async () => {
  loading.value = true
  try {
    const data: any = await getVoteDetail(voteId)
    const voteInfo = data.vote || data
    const options = data.options || []
    vote.value = {
      id: voteInfo.id || 0,
      title: voteInfo.title || '',
      deadline: voteInfo.endTime || '',
      status: voteInfo.status !== undefined ? voteInfo.status : (voteInfo.isEnded ? 2 : 1),
      isExpired: voteInfo.status === 2 || voteInfo.isEnded || false,
      totalVotes: voteInfo.totalVoters || 0,
      options: options.map((opt: any) => ({
        optionId: opt.id,
        text: opt.optionText || opt.text
      }))
    }
    updateCountdown()
  } catch (error: any) {
    ElMessage.error(error.message || '加载投票详情失败')
  } finally {
    loading.value = false
  }
}

// ===== 验证码 =====
const refreshCaptcha = async () => {
  try {
    const data: any = await getCaptcha()
    captchaId.value = data.captchaId || ''
    captchaImage.value = data.image || data.img || ''
  } catch (error: any) {
    ElMessage.error(error.message || '获取验证码失败')
  }
}

// ============================================================
// ===== 转盘抽奖 =====
// ============================================================

const showWheel = ref(false)
const isSpinning = ref(false)
const wheelResult = ref<{ points: number; message: string } | null>(null)
const wheelCanvas = ref<HTMLCanvasElement | null>(null)
let currentAngle = 0

const prizes = [
  { label: '10积分', points: 10, color: '#FF6B6B' },
  { label: '5积分', points: 5, color: '#4ECDC4' },
  { label: '谢谢参与', points: 0, color: '#45B7D1' },
  { label: '20积分', points: 20, color: '#FFA07A' },
  { label: '2积分', points: 2, color: '#98D8C8' },
  { label: '50积分', points: 50, color: '#F7DC6F' },
  { label: '谢谢参与', points: 0, color: '#BB8FCE' },
  { label: '8积分', points: 8, color: '#85C1E9' }
]

const SEGMENT = (2 * Math.PI) / prizes.length

const drawWheel = (rotation = 0) => {
  const canvas = wheelCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  const centerX = canvas.width / 2
  const centerY = canvas.height / 2
  const radius = Math.min(centerX, centerY) - 10

  ctx.clearRect(0, 0, canvas.width, canvas.height)

  prizes.forEach((prize, index) => {
    const startAngle = index * SEGMENT + rotation
    const endAngle = startAngle + SEGMENT

    ctx.beginPath()
    ctx.moveTo(centerX, centerY)
    ctx.arc(centerX, centerY, radius, startAngle, endAngle)
    ctx.closePath()
    ctx.fillStyle = prize.color
    ctx.fill()
    ctx.strokeStyle = '#fff'
    ctx.lineWidth = 2
    ctx.stroke()

    ctx.save()
    ctx.translate(centerX, centerY)
    ctx.rotate(startAngle + SEGMENT / 2)
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    ctx.fillStyle = '#fff'
    ctx.font = 'bold 14px Arial'
    ctx.shadowColor = 'rgba(0,0,0,0.3)'
    ctx.shadowBlur = 4

    const textRadius = radius * 0.7
    const label = prize.label.length > 6 ? prize.label.slice(0, 6) : prize.label
    ctx.fillText(label, textRadius, 0)
    ctx.restore()
  })

  ctx.beginPath()
  ctx.arc(centerX, centerY, 30, 0, 2 * Math.PI)
  ctx.fillStyle = '#FFD700'
  ctx.fill()
  ctx.strokeStyle = '#DAA520'
  ctx.lineWidth = 3
  ctx.stroke()

  ctx.fillStyle = '#8B4513'
  ctx.font = 'bold 16px Arial'
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText('GO', centerX, centerY)
}

const startDraw = async () => {
  if (isSpinning.value) return

  isSpinning.value = true
  wheelResult.value = null

  try {
    const data: any = await wheelDraw(voteId)
    console.log('🎰 抽奖结果:', data)

    if (data.alreadyDrawn) {
      ElMessage.info('您已抽过奖')
      wheelResult.value = {
        points: 0,
        message: '😅 您已抽过奖，不能再抽了'
      }
      isSpinning.value = false
      return
    }

    const points = data.points || 0
    const prizeName = data.prizeName || '谢谢参与'

    let targetIndex = prizes.findIndex(p => p.points === points)
    if (targetIndex < 0) targetIndex = 0

    const targetAngle = targetIndex * SEGMENT + SEGMENT / 2
    const spins = 5 + Math.random() * 3
    const finalAngle = targetAngle + spins * 2 * Math.PI

    const duration = 3000
    const startTime = performance.now()
    const startAngle = currentAngle

    await new Promise<void>((resolve) => {
      const animate = (time: number) => {
        const progress = Math.min((time - startTime) / duration, 1)
        const ease = 1 - Math.pow(1 - progress, 3)
        const currentAngleNew = startAngle + (finalAngle - startAngle) * ease

        drawWheel(currentAngleNew)

        if (progress < 1) {
          requestAnimationFrame(animate)
        } else {
          currentAngle = currentAngleNew
          isSpinning.value = false

          const prize = prizes[targetIndex] || { points: 0, label: '谢谢参与' }
          const finalPoints = points || 0

          wheelResult.value = {
            points: finalPoints,
            message: finalPoints > 0 ? `🎉 恭喜获得 ${finalPoints} 积分！` : '😅 谢谢参与，下次好运！'
          }

          if (finalPoints > 0) {
            ElMessage.success(`🎉 获得 ${finalPoints} 积分！`)
          } else {
            ElMessage.info('谢谢参与，下次好运！')
          }

          resolve()
        }
      }

      requestAnimationFrame(animate)
    })
  } catch (error: any) {
    console.error('抽奖失败:', error)
    ElMessage.error(error.message || '抽奖失败')
    isSpinning.value = false
  }
}

const skipDraw = () => {
  showWheel.value = false
  router.push(`/result/${voteId}`)
}

const onWheelClose = () => {
  showWheel.value = false
  router.push(`/result/${voteId}`)
}

// ============================================================
// ===== 提交投票 =====
// ============================================================

const handleSubmit = async () => {
  if (!selectedOption.value) {
    ElMessage.warning('请选择一个选项')
    return
  }
  if (!captchaForm.code) {
    ElMessage.warning('请输入验证码')
    return
  }
  if (!userId) {
    ElMessage.warning('请先登录')
    return
  }

  submitting.value = true
  try {
    await submitVote({
      voteId: voteId,
      optionId: selectedOption.value,
      userId: userId,
      captchaId: captchaId.value,
      captchaCode: captchaForm.code
    })

    ElMessage.success('投票成功！')

    // 弹出转盘抽奖
    showWheel.value = true
    wheelResult.value = null
    isSpinning.value = false
    nextTick(() => {
      drawWheel(currentAngle)
    })

  } catch (error: any) {
    ElMessage.error(error.message || '投票失败，请重试')
    refreshCaptcha()
    captchaForm.code = ''
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.push('/')
}

// ===== 生命周期 =====
onMounted(() => {
  loadDetail()
  refreshCaptcha()
  checkFavStatus()
  loadComments()
  loadLikeCount()
  timer = setInterval(updateCountdown, 10000)
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.detail-card { background: #fff !important; }
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.countdown {
  text-align: center;
  padding: 10px;
  background: #fdf6ec;
  border-radius: 4px;
  margin-bottom: 16px;
}
.countdown-num { color: #e6a23c; font-weight: bold; font-size: 18px; }
.vote-meta {
  display: flex;
  gap: 30px;
  color: #606266;
  margin-bottom: 10px;
}
.options-area { margin: 20px 0; }
.options-area h3 { margin-bottom: 15px; }
.option-radio {
  display: block;
  margin-bottom: 12px;
  padding: 6px 12px;
  border-radius: 8px;
  transition: transform 0.2s ease, background 0.2s ease;
}
.option-radio:hover {
  transform: translateX(6px);
  background: #f5f7fa;
}
.option-radio.is-checked {
  background: var(--el-color-primary-light-9, #ecf5ff);
  border-radius: 8px;
}
.captcha-area { margin: 20px 0; }
.captcha-img {
  vertical-align: middle;
  cursor: pointer;
  height: 32px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.captcha-img:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.submit-area {
  margin-top: 30px;
  display: flex;
  gap: 15px;
}
.comment-card { background: #fff !important; }
.comment-input {
  display: flex;
  margin-bottom: 16px;
}
.comment-item {
  border-bottom: 1px solid #ebeef5;
  padding: 12px 0;
}
.comment-user { font-weight: bold; color: #303133; }
.comment-content { color: #606266; margin: 4px 0; }
.comment-time { font-size: 12px; color: #909399; }

/* 转盘样式 */
.wheel-container {
  text-align: center;
  padding: 10px 0;
}
.wheel-wrapper {
  position: relative;
  display: inline-block;
}
.wheel-wrapper canvas {
  display: block;
  margin: 0 auto;
  border-radius: 50%;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.2);
  max-width: 100%;
  height: auto;
}
.pointer {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 40px;
  cursor: pointer;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
  transition: transform 0.2s;
  z-index: 10;
}
.pointer:hover {
  transform: translateX(-50%) scale(1.1);
}
.pointer:active {
  transform: translateX(-50%) scale(0.9);
}
.spinning-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 50%;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
}
.spinning-mask .el-icon {
  font-size: 40px;
  margin-bottom: 8px;
}
.result-box {
  margin-top: 16px;
  padding: 0 10px;
}
.wheel-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}
</style>