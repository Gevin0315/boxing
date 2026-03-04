import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/api/auth'
import { getToken, setToken, removeToken, type UserInfo } from '@/types/auth'
import router from '@/router'

interface UserState {
  token: string | null
  userInfo: UserInfo | null
  permissions: string[]
  roles: string[]
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: getToken(),
    userInfo: null,
    permissions: [],
    roles: []
  }),

  getters: {
    /** 是否已登录 */
    isLoggedIn: (state) => !!state.token,

    /** 用户头像 */
    avatar: (state) => state.userInfo?.avatar || '',

    /** 用户名 */
    username: (state) => state.userInfo?.username || '',

    /** 昵称 */
    nickname: (state) => state.userInfo?.nickname || ''
  },

  actions: {
    /** 登录 */
    async login(loginForm: { username: string; password: string }) {
      try {
        const res = await login(loginForm)
        this.token = res.token
        setToken(res.token)
        return res
      } catch (error) {
        throw error
      }
    },

    /** 获取用户信息 */
    async getUserInfo() {
      try {
        const res = await getUserInfo() as any
        const user: UserInfo = {
          id: res.id,
          username: res.username,
          nickname: res.nickname || res.realName || res.username,
          avatar: res.avatar || '',
          roles: res.roles || (res.role ? [res.role] : []),
          permissions: res.permissions || ['*:*:*']
        }
        this.userInfo = user
        this.permissions = user.permissions || []
        this.roles = user.roles || []
        return user
      } catch (error) {
        throw error
      }
    },

    /** 登出 */
    async logout() {
      try {
        await logout()
      } catch (error) {
        console.error('Logout error:', error)
      } finally {
        this.token = null
        this.userInfo = null
        this.permissions = []
        this.roles = []
        removeToken()
        router.push('/login')
      }
    },

    /** 重置token */
    resetToken() {
      this.token = null
      this.userInfo = null
      this.permissions = []
      this.roles = []
      removeToken()
    },

    /** 检查权限 */
    hasPermission(permission: string): boolean {
      if (!this.permissions || this.permissions.length === 0) {
        return false
      }
      return this.permissions.includes(permission) || this.permissions.includes('*:*:*')
    },

    /** 检查角色 */
    hasRole(role: string): boolean {
      if (!this.roles || this.roles.length === 0) {
        return false
      }
      return this.roles.includes(role)
    }
  }
})
