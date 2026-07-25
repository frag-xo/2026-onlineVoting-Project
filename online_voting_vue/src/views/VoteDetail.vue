<template>
  <div class="detail-container">
    <el-card v-loading="loading" class="detail-card">
      <template #header>
        <div class="detail-header">
          <h2>{{ vote.title }}</h2>
          <div>
            <el-tag :type="vote.isExpired ? 'info' : 'success'">
              {{ vote.isExpired ? '已结束' : '进行中' }}
            </el-tag>
            <el-button
              :type="isFav ? 'danger' : 'default'"
              :icon="isFav ? 'Star' : 'Star'"
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
            :value="opt.optionId"
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
          :disabled="vote.isExpired || !selectedOption"
          :loading="submitting"
          @click="handleSubmit"
        >
          提交投票
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
          <div class="comment-user">{{ item.userName || item.user }}</div>
          <div class="comment-content">{{ item.content }}</div>
          <div class="comment-time">{{ item.createTime || item.time }}</div>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getVoteDetail,
  submitVote,
  getCaptcha,
  checkFavorited,
  favoriteVote,
  unfavoriteVote,
  getComments,
  addComment,
  likeVote,
  getLikeCount
} from '@/api/vote'

const router = useRouter()
const route = useRoute()
const voteId = Number(route.params.id)
const userId = Number(localStorage.getItem('userId') || 0)

// 投票数据
const vote = ref({
  id: 0,
  title: '',
  deadline: '',
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

// 收藏
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

// 检查收藏状态
const checkFavStatus = async () => {
  if (!userId) return
  try {
    const data = await checkFavorited(voteId)
    isFav.value = data === true || data === 1
  } catch (error: any) {
    console.warn('检查收藏状态失败', error.message)
  }
}

// 点赞
const isLiked = ref(false)
const likeCount = ref(0)

const loadLikeCount = async () => {
  try {
    likeCount.value = await getLikeCount(voteId)
  } catch (error) {
    console.warn('加载点赞数失败', error)
  }
}

const toggleLike = async () => {
  if (!userId) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await likeVote(voteId)
    isLiked.value = !isLiked.value
    likeCount.value += isLiked.value ? 1 : -1
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 倒计时
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

// 评论
const commentLoading = ref(false)
const comments = ref<any[]>([])
const newComment = ref('')

const loadComments = async () => {
  commentLoading.value = true
  try {
    const data = await getComments(voteId)
    comments.value = data || []
  } catch (error: any) {
    console.warn('加载评论失败', error.message)
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

// 满意度调查
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

const loadDetail = async () => {
  loading.value = true
  try {
    const data = await getVoteDetail(voteId)
    const voteInfo = data.vote || data
    const options = data.options || []
    vote.value = {
      id: voteInfo.id || 0,
      title: voteInfo.title || '',
      deadline: voteInfo.endTime || '',
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

const refreshCaptcha = async () => {
  try {
    const data = await getCaptcha()
    captchaId.value = data.captchaId
    captchaImage.value = data.image || data.img
  } catch (error: any) {
    ElMessage.error(error.message || '获取验证码失败')
  }
}

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
    showSurvey.value = true
    router.push(`/result/${voteId}`)
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
</style>