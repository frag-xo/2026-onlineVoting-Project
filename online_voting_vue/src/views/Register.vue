<template>
  <div class="register-container">
    <div class="register-box">
      <h1 class="register-title">在线投票系统</h1>
      <p class="register-subtitle">注册新账号</p>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="uname">
          <el-input
            v-model="form.uname"
            placeholder="请输入用户名（4-20位字母或数字）"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="pwd">
          <el-input
            v-model="form.pwd"
            type="password"
            placeholder="请输入密码（6-20位）"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPwd">
          <el-input
            v-model="form.confirmPwd"
            type="password"
            placeholder="请再次输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" @click="handleRegister" :loading="loading" style="width: 100%;">
            注 册
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="goLogin">立即登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { register } from '@/api/vote'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  uname: '',
  pwd: '',
  confirmPwd: ''
})

const validateConfirmPwd = (_rule: any, value: string, callback: any) => {
  if (value !== form.pwd) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  uname: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4-20位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]+$/, message: '用户名只能包含字母和数字', trigger: 'blur' }
  ],
  pwd: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPwd: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await register({
          uname: form.uname,
          pwd: form.pwd
        })
        ElMessage.success('注册成功！请登录')
        router.push('/login')
      } catch (error: any) {
        ElMessage.error(error.message || '注册失败，用户名可能已被占用')
      } finally {
        loading.value = false
      }
    }
  })
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
}

.register-box {
  width: 400px;
  padding: 40px 35px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.register-title {
  text-align: center;
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
}

.register-subtitle {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-bottom: 30px;
}

.register-footer {
  text-align: center;
  font-size: 14px;
  color: #909399;
}
</style>