

/** 登录表单 */
export interface LoginForm {
  username: string
  password: string
  code?: string
  uuid?: string
}

/** 登录响应 */
export interface LoginResult {
  token: string
  [key: string]: any
}

/** 用户信息 */
export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar?: string
  roles: string[]
  permissions: string[]
}

/** Token存储键 */
export const TOKEN_KEY = 'Admin-Token'

/** 存储token */
export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

/** 设置token */
export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

/** 移除token */
export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}
