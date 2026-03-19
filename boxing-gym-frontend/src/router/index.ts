import { createRouter, createWebHistory } from 'vue-router'
import type { Router, RouteRecordRaw } from 'vue-router'
import { getToken } from '@/types/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      }
    ]
  },
  {
    path: '/sys-user',
    component: () => import('@/views/layout/index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'SysUser',
        component: () => import('@/views/sys-user/index.vue'),
        meta: { title: '系统用户', icon: 'User' }
      }
    ]
  },
  {
    path: '/member',
    component: () => import('@/views/layout/index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Member',
        component: () => import('@/views/member/index.vue'),
        meta: { title: '会员管理', icon: 'UserFilled' }
      }
    ]
  },
  {
    path: '/course',
    component: () => import('@/views/layout/index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Course',
        component: () => import('@/views/course/index.vue'),
        meta: { title: '课程管理', icon: 'Reading' }
      }
    ]
  },
  {
    path: '/schedule',
    component: () => import('@/views/layout/index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Schedule',
        component: () => import('@/views/schedule/index.vue'),
        meta: { title: '排课管理', icon: 'Calendar' }
      }
    ]
  },
  {
    path: '/coach',
    component: () => import('@/views/layout/index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Coach',
        component: () => import('@/views/coach/index.vue'),
        meta: { title: '教练管理', icon: 'Avatar' }
      }
    ]
  },
  {
    path: '/training',
    component: () => import('@/views/layout/index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Training',
        component: () => import('@/views/training/index.vue'),
        meta: { title: '签到管理', icon: 'CircleCheck' }
      }
    ]
  },
  {
    path: '/finance',
    component: () => import('@/views/layout/index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Finance',
        component: () => import('@/views/finance/index.vue'),
        meta: { title: '财务管理', icon: 'Wallet' }
      }
    ]
  },
  {
    path: '/membership-card',
    component: () => import('@/views/layout/index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'MembershipCard',
        component: () => import('@/views/membership-card/index.vue'),
        meta: { title: '会员卡管理', icon: 'Postcard' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', requiresAuth: false }
  }
]

const router: Router = createRouter({
  history: createWebHistory(),
  routes
})

/** 路由守卫 */
router.beforeEach((to, _from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title || '拳馆管理系统'} - 拳馆管理系统`

  const requiresAuth = to.meta.requiresAuth !== false
  const hasToken = getToken() !== null

  if (requiresAuth) {
    if (hasToken) {
      next()
    } else {
      next({ name: 'Login', query: { redirect: to.fullPath } })
    }
  } else {
    // 已登录用户访问登录页，跳转到首页
    if (to.name === 'Login' && hasToken) {
      next({ name: 'Dashboard' })
    } else {
      next()
    }
  }
})

export default router
