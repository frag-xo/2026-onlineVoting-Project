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
          <el-menu-item index="/rankings">
            <el-icon><TrendCharts /></el-icon>
            <template #title>投票排行</template>
          </el-menu-item>
          <el-menu-item index="/create">
            <el-icon><EditPen /></el-icon>
            <template #title>发布投票</template>
          </el-menu-item>
          <el-menu-item index="/profile">
            <el-icon><UserFilled /></el-icon>
            <template #title>个人中心</template>
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
            <el-button
              type="primary"
              link
              @click="goAdmin"
              v-if="isLoggedIn && isAdmin"
              class="header-btn"
            >
              <el-icon><Setting /></el-icon> 后台
            </el-button>

            <template v-if="isLoggedIn">
              <span class="username">👤 {{ username }}</span>
              <span class="role-tag">{{ isAdmin ? '管理员' : '用户' }}</span>
              <el-button type="danger" link @click="handleLogout" class="header-btn">
                退出
              </el-button>
            </template>
            <template v-else>
              <el-button type="primary" link @click="goLogin" class="header-btn">登录</el-button>
              <el-button type="primary" link @click="goRegister" class="header-btn">注册</el-button>
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
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  House,
  Setting,
  DataAnalysis,
  TrendCharts,
  EditPen,
  UserFilled,
  DArrowLeft,
  DArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const isCollapse = ref(localStorage.getItem('sidebarCollapse') === 'true')
const isLoggedIn = ref(false)
const username = ref('')
const isAdmin = ref(false)
const currentPageTitle = ref('投票列表')

const pageTitles: Record<string, string> = {
  '/': '投票列表',
  '/login': '登录',
  '/register': '注册',
  '/admin': '后台管理',
  '/dashboard': '数据看板',
  '/profile': '个人中心',
  '/rankings': '投票排行',
  '/create': '发布投票'
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

const checkLoginStatus = () => {
  const token = localStorage.getItem('token')
  if (token) {
    isLoggedIn.value = true
    username.value = localStorage.getItem('username') || '用户'
    const utype = localStorage.getItem('utype')
    isAdmin.value = utype === 'ROLE_1'
  } else {
    isLoggedIn.value = false
    username.value = ''
    isAdmin.value = false
  }
}

onMounted(() => {
  checkLoginStatus()
  updatePageTitle()
})

watch(() => route.path, () => {
  updatePageTitle()
  checkLoginStatus()
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
  localStorage.setItem('sidebarCollapse', String(isCollapse.value))
}

const goLogin = () => router.push('/login')
const goRegister = () => router.push('/register')
const goAdmin = () => router.push('/admin')

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
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Helvetica Neue', Arial, sans-serif; background-color: #f0f2f5; height: 100%; }
html, #app { height: 100%; }
.app-container { height: 100%; }

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
  height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 18px;
  font-weight: bold; background-color: #2b3a4a; white-space: nowrap; overflow: hidden;
}
.app-menu { border-right: none; flex: 1; overflow-y: auto; }
.app-menu .el-menu-item { height: 50px; line-height: 50px; }
.collapse-btn {
  height: 50px; line-height: 50px; text-align: center; color: #bfcbd9;
  cursor: pointer; background-color: #2b3a4a;
}
.collapse-btn:hover { background-color: #1f2d3d; }
.collapse-btn .el-icon { font-size: 20px; }

.app-header {
  background-color: #fff; box-shadow: 0 1px 4px rgba(0,21,41,0.08);
  display: flex; justify-content: space-between; align-items: center;
  padding: 0 24px; height: 60px;
}
.header-left .welcome { font-size: 16px; color: #303133; }
.header-right { display: flex; align-items: center; gap: 16px; }
.header-btn { font-size: 14px; }
.username { color: #303133; font-size: 14px; }
.role-tag { font-size: 12px; color: #909399; background-color: #f4f4f5; padding: 2px 12px; border-radius: 12px; }
.app-main { background-color: #f0f2f5; padding: 20px; overflow-y: auto; height: calc(100vh - 60px); }
</style>