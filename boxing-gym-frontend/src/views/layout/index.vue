<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { CaretBottom, Fold, Expand } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapse = computed(() => false)

const menuList = [
  { path: '/dashboard', title: '仪表盘', icon: 'Odometer' },
  { path: '/sys-user', title: '系统用户', icon: 'User' },
  { path: '/member', title: '会员管理', icon: 'UserFilled' },
  { path: '/membership-card', title: '会员卡管理', icon: 'Postcard' },
  { path: '/member-card', title: '会员持卡', icon: 'CreditCard' },
  { path: '/course', title: '课程管理', icon: 'Reading' },
  { path: '/schedule', title: '排课管理', icon: 'Calendar' },
  { path: '/coach', title: '教练管理', icon: 'Avatar' },
  { path: '/training', title: '签到管理', icon: 'CircleCheck' },
  { path: '/finance', title: '财务管理', icon: 'Wallet' }
]

const activeMenu = computed(() => route.path)

const handleMenuSelect = (index: string) => {
  router.push(index)
}

const handleLogout = () => {
  userStore.logout()
}
</script>

<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="layout-aside">
      <div class="logo">
        <span v-if="!isCollapse">拳馆管理</span>
        <span v-else>拳馆</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        router
        class="layout-menu"
      >
        <el-menu-item
          v-for="item in menuList"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="layout-main">
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon :size="20" class="collapse-icon">
            <Fold />
          </el-icon>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-dropdown">
              <el-avatar :size="32" :src="userStore.avatar">
                {{ userStore.nickname?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.nickname || userStore.username }}</span>
              <el-icon class="el-icon--right"><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  width: 100%;
  height: 100%;
}

.layout-aside {
  background: #304156;
  transition: width 0.3s;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}

.layout-menu {
  border-right: none;
  background: #304156;
}

.layout-menu :deep(.el-menu-item) {
  color: #bfcbd9;
}

.layout-menu :deep(.el-menu-item:hover) {
  background: #263445;
  color: #fff;
}

.layout-menu :deep(.el-menu-item.is-active) {
  background: #409eff;
  color: #fff;
}

.layout-main {
  background: #f0f2f5;
}

.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-icon {
  cursor: pointer;
  color: #606266;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 10px;
}

.user-name {
  margin: 0 8px;
  font-size: 14px;
  color: #303133;
}

.layout-content {
  padding: 20px;
  overflow-y: auto;
}
</style>
