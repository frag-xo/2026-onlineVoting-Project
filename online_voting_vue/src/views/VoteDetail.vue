<template>
  <div class="detail-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="detail-header">
          <h2>{{ vote.title }}</h2>
          <el-tag :type="vote.isExpired ? 'info' : 'success'">
            {{ vote.isExpired ? '已结束' : '进行中' }}
          </el-tag>
        </div>
      </template>

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
            <span class="option-text">{{ opt.text }}</span>
          </el-radio>
        </el-radio-group>
      </div>

      <el-divider />

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
              style="vertical-align: middle; cursor: pointer; height: 32px; border: 1px solid #dcdfe6; border-radius: 4px;"
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVoteDetail, submitVote, getCaptcha } from '@/api/vote'

const router = useRouter()
const route = useRoute()
const voteId = Number(route.params.id)
const userId = Number(localStorage.getItem('userId') || 0)

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

const loadDetail = async () => {
  loading.value = true
  try {
    const data = await getVoteDetail(voteId)
    // 后端返回：{ id, title, description, options: [{id, optionText}], endTime, status, totalVoters }
    vote.value = {
      id: data.id,
      title: data.title,
      deadline: data.endTime,
      isExpired: data.status === 2 || data.isEnded || false,
      totalVotes: data.totalVoters || 0,
      options: (data.options || []).map((opt: any) => ({
        optionId: opt.id,
        text: opt.optionText
      }))
    }
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
    captchaImage.value = data.image
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
})
</script>

<style scoped>
.detail-container {
  max-width: 700px;
  margin: 24px auto;
  padding: 0 20px;
}
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.detail-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
  font-family: 'Outfit', sans-serif;
  letter-spacing: -0.02em;
}
.vote-meta {
  display: flex;
  gap: 28px;
  color: #8e8ea0;
  font-size: 14px;
  margin-bottom: 10px;
}
.options-area {
  margin: 28px 0;
}
.options-area h3 {
  margin-bottom: 16px;
  color: #1a1a2e;
  font-size: 16px;
  font-weight: 500;
}
.el-radio-group .el-radio {
  display: block;
  margin-bottom: 10px;
  padding: 14px 18px;
  border: 1.5px solid #e8e8ec;
  border-radius: 10px;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  cursor: pointer;
}
.option-radio {
  display: flex !important;
  align-items: center;
  margin-bottom: 10px;
  padding: 14px 18px;
  border: 1.5px solid #e8e8ec;
  border-radius: 10px;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  cursor: pointer;
}
.option-radio:hover {
  border-color: #a0a0b8;
  background: #f8f8fc;
  transform: translateX(3px);
}
.option-radio.is-checked {
  border-color: #4361ee !important;
  background: #f0f2ff !important;
}
.option-text {
  font-weight: 500;
  color: #1a1a2e;
}
.captcha-area {
  margin: 24px 0;
}
.submit-area {
  margin-top: 28px;
  display: flex;
  gap: 12px;
}
</style>