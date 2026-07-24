<template>
  <div class="create-container">
    <h2>📝 发布投票</h2>
    <el-card>
      <!-- 提示：审核状态 -->
      <el-alert
        :title="isAdmin ? '管理员发布，无需审核直接发布' : '普通用户发布，需等待管理员审核'"
        :type="isAdmin ? 'success' : 'warning'"
        :closable="false"
        show-icon
        style="margin-bottom: 16px;"
      />

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="投票标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入投票标题（不超过100字）" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="投票描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入投票描述（可选）" />
        </el-form-item>

        <el-form-item label="选项列表" prop="options">
          <div v-for="(item, index) in form.options" :key="index" class="option-item">
            <el-input
              v-model="form.options[index]"
              :placeholder="`选项 ${index + 1}`"
              style="width: 300px; margin-right: 10px;"
            />
            <el-button type="danger" size="small" :disabled="form.options.length <= 2" @click="removeOption(index)">
              删除
            </el-button>
          </div>
          <el-button type="primary" size="small" @click="addOption">+ 添加选项</el-button>
        </el-form-item>

        <el-form-item label="截止时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择截止时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="定时发布" prop="scheduledTime">
          <el-date-picker
            v-model="form.scheduledTime"
            type="datetime"
            placeholder="选择发布时间（可选）"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
          <span style="font-size:12px;color:#909399;margin-left:10px;">留空则立即发布</span>
        </el-form-item>

        <el-form-item label="分组管理" prop="group">
          <el-select v-model="form.group" placeholder="选择分组" clearable>
            <el-option label="技术讨论" value="tech" />
            <el-option label="团队建设" value="team" />
            <el-option label="产品反馈" value="product" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isAdmin ? '发布投票' : '提交审核' }}
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>

        <el-alert
          v-if="!isAdmin"
          title="提交后需等待管理员审核，审核通过后投票才会发布到列表"
          type="info"
          :closable="false"
          show-icon
        />
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { createVote, submitVoteForAudit } from '@/api/vote'

const formRef = ref<FormInstance>()
const submitting = ref(false)

const isAdmin = ref(localStorage.getItem('utype') === 'ROLE_1')

const form = reactive({
  title: '',
  description: '',
  options: ['', ''],
  endTime: '',
  scheduledTime: '',
  group: ''
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入投票标题', trigger: 'blur' }],
  options: [{
    validator: (_rule: any, value: string[], callback: any) => {
      const filtered = value.filter(item => item.trim() !== '')
      if (filtered.length < 2) callback(new Error('至少需要 2 个有效选项'))
      else callback()
    },
    trigger: 'blur'
  }],
  endTime: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
}

const addOption = () => form.options.push('')
const removeOption = (index: number) => form.options.splice(index, 1)

const resetForm = () => {
  form.title = ''
  form.description = ''
  form.options = ['', '']
  form.endTime = ''
  form.scheduledTime = ''
  form.group = ''
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const filteredOptions = form.options.filter(item => item.trim() !== '')
        const userId = Number(localStorage.getItem('userId') || 0)

        if (isAdmin.value) {
          // 管理员直接发布
          await createVote({
            title: form.title,
            description: form.description || '',
            status: 1,
            endTime: form.endTime,
            creatorId: userId,
            options: filteredOptions
          })
          ElMessage.success('投票发布成功！')
        } else {
          // 普通用户提交审核
          await submitVoteForAudit({
            title: form.title,
            description: form.description || '',
            endTime: form.endTime,
            options: filteredOptions
          })
          ElMessage.success('投票已提交审核，请等待管理员审核')
        }
        resetForm()
      } catch (error: any) {
        ElMessage.error(error.message || '提交失败')
      } finally {
        submitting.value = false
      }
    }
  })
}
</script>

<style scoped>
.create-container { padding: 20px; max-width: 800px; margin: 0 auto; }
.option-item { display: flex; align-items: center; margin-bottom: 10px; }
</style>