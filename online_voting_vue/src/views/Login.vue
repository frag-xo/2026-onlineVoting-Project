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
          <el-button type="primary" size="large" @click="handleLogin" :loading="loading" style="width: 100%;">
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
import { useRouter } from 'vue-router'
import { login } from '@/api/vote'

const router = useRouter()
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
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
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
        window.location.href = '/'
        console.log('6. 跳转命令已执行')
      } catch (error: any) {
        console.log('7. catch 捕获到错误:', error)
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
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
  min-height: 100vh;
  background: linear-gradient(135deg, #409eff 0%, #79bbff 100%);
}

.login-box {
  width: 400px;
  padding: 40px 35px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.login-title {
  text-align: center;
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
}

.login-subtitle {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-bottom: 30px;
}

.login-footer {
  text-align: center;
  font-size: 14px;
  color: #909399;
}
</style>