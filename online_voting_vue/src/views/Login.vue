<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="login-title">在线投票系统</h1>
      <p class="login-subtitle">登录以参与投票</p>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="uname">
          <el-input
            v-model="form.uname"
            placeholder="请输入用户名"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="pwd">
          <el-input
            v-model="form.pwd"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" @click="handleLogin" :loading="loading" class="login-btn">
            登 录
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="goRegister">立即注册</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { login } from '@/api/vote'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  uname: '',
  pwd: ''
})

const rules: FormRules = {
  uname: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4-20位', trigger: 'blur' }
  ],
  pwd: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  console.log('1. handleLogin 开始执行')
  if (!formRef.value) {
    console.log('2. formRef 为空，退出')
    return
  }
  await formRef.value.validate(async (valid) => {
    console.log('3. 表单验证结果:', valid)
    if (valid) {
      loading.value = true
      console.log('4. 开始调用 login 接口')
      try {
        console.log('1. 开始调用 login 接口')
        const res: any = await login(form.uname, form.pwd)
        console.log('2. login 返回:', res)

        // 注意：响应拦截器可能已经返回了 t 对象
        // 如果 res 是 { uname, utype, id, token, realname }，直接用
        // 如果 res 是 { code, msg, t, ... }，需要用 res.t
        const user = res
        const token = user.token
        console.log('3. 提取的 token:', token)

        if (!token) {
          console.error('token 为空！响应结构可能是:', res)
          ElMessage.error('登录响应中没有 token')
          return
        }

        localStorage.setItem('token', token)
        localStorage.setItem('userId', String(user.id))
        localStorage.setItem('username', user.uname)
        localStorage.setItem('utype', user.utype || 'ROLE_3')
        console.log('4. localStorage 存储完成')

        ElMessage.success('登录成功！')
        console.log('5. 准备跳转')
        const redirect = (route.query.redirect as string) || '/'
        router.push(redirect)
        console.log('6. 跳转命令已执行，目标:', redirect)
      } catch (error: any) {
        console.log('7. catch 捕获到错误:', error)
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
        console.log('10. finally 执行完毕')
      }
    }
  })
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 56px);
  padding: 20px;
}

.login-box {
  width: 400px;
  padding: 44px 36px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.login-title {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 6px;
  font-family: 'Outfit', sans-serif;
  letter-spacing: -0.02em;
}

.login-subtitle {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-bottom: 32px;
}

.login-footer {
  text-align: center;
  font-size: 14px;
  color: #909399;
  margin-top: 24px;
}

.login-btn {
  width: 100%;
  height: 44px !important;
  border-radius: 12px !important;
  font-size: 15px !important;
  font-weight: 600 !important;
  background: #4361ee !important;
  border-color: #4361ee !important;
  transition: all 0.2s !important;
}

.login-btn:hover {
  background: #3651d4 !important;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(67, 97, 238, 0.25) !important;
}
</style>