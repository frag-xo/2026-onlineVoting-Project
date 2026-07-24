<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="login-title">在线投票系统</h1>
      <p class="login-subtitle">选择身份登录</p>

      <!-- Tab 切换 -->
      <div class="login-tabs">
        <div
            class="login-tab"
            :class="{ active: loginType === 'user' }"
            @click="loginType = 'user'"
        >
          👤 普通用户
        </div>
        <div
            class="login-tab"
            :class="{ active: loginType === 'admin' }"
            @click="loginType = 'admin'"
        >
          🔐 管理员
        </div>
      </div>

      <!-- 登录表单 -->
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="uname">
          <el-input
              v-model="form.uname"
              :placeholder="loginType === 'admin' ? '请输入管理员用户名' : '请输入用户名'"
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

        <!-- 普通用户：显示注册链接 -->
        <div v-if="loginType === 'user'" class="login-extra">
          <span>还没有账号？</span>
          <el-link type="primary" @click="goRegister">立即注册</el-link>
        </div>

        <!-- 管理员：显示不可注册提示 -->
        <div v-else class="login-extra admin-hint">
          <span>🔒 管理员账号由系统分配，不可注册</span>
        </div>

        <el-form-item>
          <el-button
              type="primary"
              size="large"
              @click="handleLogin"
              :loading="loading"
              class="login-btn"
          >
            {{ loginType === 'admin' ? '管理员登录' : '登 录' }}
          </el-button>
        </el-form-item>
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

// 登录类型
const loginType = ref<'user' | 'admin'>('user')

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
    if (!valid) return

    loading.value = true
    try {
      const userData: any = await login(form.uname, form.pwd)
      console.log('登录返回数据:', userData)

      const token = userData?.token
      const utype = userData?.utype || 'ROLE_3'

      if (!token) {
        ElMessage.error('登录失败：响应中没有 token')
        loading.value = false
        return
      }

      // 权限校验
      if (loginType.value === 'admin') {
        if (utype !== 'ROLE_1') {
          ElMessage.error('该账号不是管理员，请使用普通用户登录')
          loading.value = false
          return
        }
      } else {
        if (utype === 'ROLE_1') {
          ElMessage.error('管理员请使用管理员入口登录')
          loading.value = false
          return
        }
      }

      // 保存用户信息
      localStorage.setItem('token', token)
      localStorage.setItem('userId', String(userData.id || ''))
      localStorage.setItem('username', userData.uname || '')
      localStorage.setItem('utype', utype)

      ElMessage.success(loginType.value === 'admin' ? '管理员登录成功！' : '登录成功！')

      if (utype === 'ROLE_1') {
        router.push('/admin')
      } else {
        const redirect = (route.query.redirect as string) || '/'
        router.push(redirect)
      }
    } catch (error: any) {
      console.error('登录错误:', error)
    } finally {
      loading.value = false
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
  background: #f5f7fa;
}

.login-box {
  width: 420px;
  padding: 44px 36px 36px;
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
  margin-bottom: 4px;
}

.login-subtitle {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-bottom: 28px;
}

.login-tabs {
  display: flex;
  gap: 0;
  margin-bottom: 28px;
  border-radius: 12px;
  overflow: hidden;
  border: 1.5px solid #e8e8ec;
  background: #f5f7fa;
}

.login-tab {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  font-size: 15px;
  font-weight: 500;
  color: #8e8ea0;
  cursor: pointer;
  transition: all 0.3s ease;
  background: transparent;
  user-select: none;
}

.login-tab:hover {
  color: #4361ee;
}

.login-tab.active {
  background: #4361ee;
  color: #fff;
  box-shadow: 0 4px 14px rgba(67, 97, 238, 0.25);
}

.login-tab.active:hover {
  color: #fff;
}

.login-extra {
  text-align: center;
  font-size: 14px;
  color: #909399;
  margin-bottom: 20px;
}

.admin-hint {
  display: inline-block;
  padding: 8px 20px;
  background: #fdf6ec;
  border-radius: 8px;
  color: #e6a23c;
  font-size: 13px;
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
  margin-top: 4px;
}

.login-btn:hover {
  background: #3651d4 !important;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(67, 97, 238, 0.25) !important;
}

@media (max-width: 480px) {
  .login-box {
    padding: 32px 20px 28px;
    margin: 0 12px;
  }
  .login-title {
    font-size: 24px;
  }
  .login-tab {
    font-size: 14px;
    padding: 10px 0;
  }
}
</style>