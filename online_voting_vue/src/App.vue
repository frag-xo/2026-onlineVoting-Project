<template>
  <div id="app">
    <el-container class="app-container">
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '220px'" class="app-aside">
        <div class="logo-area">
          <span v-if="!isCollapse">📊 投票系统</span>
          <span v-else>📊</span>
        </div>
        <el-menu
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          class="app-menu"
        >
          <el-menu-item index="/">
            <el-icon><House /></el-icon>
            <template #title>投票列表</template>
          </el-menu-item>
          <el-menu-item index="/admin" v-if="isLoggedIn && isAdmin">
            <el-icon><Setting /></el-icon>
            <template #title>后台管理</template>
          </el-menu-item>
          <el-menu-item index="/dashboard" v-if="isLoggedIn && isAdmin">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>数据看板</template>
          </el-menu-item>
        </el-menu>
        <!-- 折叠按钮 -->
        <div class="collapse-btn" @click="toggleCollapse">
          <el-icon>
            <DArrowLeft v-if="!isCollapse" />
            <DArrowRight v-else />
          </el-icon>
        </div>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <el-header class="app-header">
          <div class="header-left">
            <span class="welcome">📋 {{ currentPageTitle }}</span>
          </div>
          <div class="header-right">
            <!-- 后台快捷入口 -->
            <el-button
              type="primary"
              link
              @click="goAdmin"
              v-if="isLoggedIn && isAdmin"
              class="header-btn"
            >
              <el-icon><Setting /></el-icon> 后台
            </el-button>

            <!-- 已登录：显示用户信息 + 退出 -->
            <template v-if="isLoggedIn">
              <span class="username">👤 {{ username }}</span>
              <span class="role-tag">{{ isAdmin ? '管理员' : '用户' }}</span>
              <el-button type="danger" link @click="handleLogout" class="header-btn">
                退出
              </el-button>
            </template>

            <!-- 未登录：显示登录 + 注册 -->
            <template v-else>
              <el-button type="primary" link @click="goLogin" class="header-btn">
                登录
              </el-button>
              <el-button type="primary" link @click="goRegister" class="header-btn">
                注册
              </el-button>
            </template>
          </div>
        </el-header>
        <el-main class="app-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  House,
  Setting,
  DataAnalysis,
  DArrowLeft,
  DArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 侧边栏折叠状态
const isCollapse = ref(localStorage.getItem('sidebarCollapse') === 'true')

// 用户状态
const isLoggedIn = ref(false)
const username = ref('')
const isAdmin = ref(false)

// 当前页面标题
const currentPageTitle = ref('投票列表')

const pageTitles: Record<string, string> = {
  '/': '投票列表',
  '/login': '登录',
  '/register': '注册',
  '/admin': '后台管理',
  '/dashboard': '数据看板',
  '/detail': '投票详情',
  '/result': '投票结果'
}

const updatePageTitle = () => {
  const path = route.path
  if (path.startsWith('/detail')) {
    currentPageTitle.value = '投票详情'
  } else if (path.startsWith('/result')) {
    currentPageTitle.value = '投票结果'
  } else {
    currentPageTitle.value = pageTitles[path] || '投票系统'
  }
}

// ✅ 检查登录状态（每次路由变化时也检查）
const checkLoginStatus = () => {
  const token = localStorage.getItem('token')
  console.log('🔐 检查登录状态, token:', token ? '存在' : '无')
  if (token) {
    isLoggedIn.value = true
    username.value = localStorage.getItem('username') || '用户'
    const utype = localStorage.getItem('utype')
    isAdmin.value = utype === 'ROLE_1'
    console.log('✅ 已登录, 角色:', utype)
  } else {
    isLoggedIn.value = false
    username.value = ''
    isAdmin.value = false
    console.log('❌ 未登录')
  }
}

// 强制刷新登录状态（用于登录/退出后）
const refreshLoginStatus = () => {
  nextTick(() => {
    checkLoginStatus()
  })
}

onMounted(() => {
  checkLoginStatus()
  updatePageTitle()
})

// 路由变化时重新检查
watch(() => route.path, () => {
  updatePageTitle()
  checkLoginStatus()
})

// 切换侧边栏折叠
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
  localStorage.setItem('sidebarCollapse', String(isCollapse.value))
}

// 跳转方法
const goLogin = () => router.push('/login')
const goRegister = () => router.push('/register')
const goAdmin = () => router.push('/admin')

// ✅ 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('utype')
  isLoggedIn.value = false
  isAdmin.value = false
  ElMessage.success('已退出登录')
  router.push('/login')
}

// 暴露刷新方法给子组件（Login.vue 登录成功后调用）
// 在 Login.vue 中可以通过 window.__refreshLoginStatus 调用
if (typeof window !== 'undefined') {
  window.__refreshLoginStatus = refreshLoginStatus
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  background-color: #f0f2f5;
  height: 100%;
}

html,
#app {
  height: 100%;
}

.app-container {
  height: 100%;
}

/* 侧边栏 */
.app-aside {
  background-color: #304156;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
  height: 100vh;
  position: sticky;
  top: 0;
  overflow: hidden;
}

.logo-area {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background-color: #2b3a4a;
  white-space: nowrap;
  overflow: hidden;
  transition: all 0.3s;
}

.app-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
}
.app-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}

.collapse-btn {
  height: 50px;
  line-height: 50px;
  text-align: center;
  color: #bfcbd9;
  cursor: pointer;
  background-color: #2b3a4a;
  transition: background-color 0.2s;
}
.collapse-btn:hover {
  background-color: #1f2d3d;
}
.collapse-btn .el-icon {
  font-size: 20px;
}

/* 顶部栏 */
.app-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 60px;
}

.header-left .welcome {
  font-size: 16px;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-btn {
  font-size: 14px;
}

.username {
  color: #303133;
  font-size: 14px;
}

.role-tag {
  font-size: 12px;
  color: #909399;
  background-color: #f4f4f5;
  padding: 2px 12px;
  border-radius: 12px;
}

/* 主内容区 */
.app-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 60px);
}
</style>