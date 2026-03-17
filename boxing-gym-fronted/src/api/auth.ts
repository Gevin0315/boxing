import request from '@/utils/request'
import type { LoginForm, LoginResult } from '@/types/auth'

/** 登录 */
export function login(data: LoginForm) {
  return request.post<any, LoginResult>('/auth/login', data)
}

/** 登出 */
export function logout() {
  return request.post('/auth/logout')
}

/** 获取用户信息 */
export function getUserInfo() {
  return request.get('/auth/info')
}

/** 获取验证码 */
export function getCaptcha() {
  return request.get('/auth/captcha')
}
