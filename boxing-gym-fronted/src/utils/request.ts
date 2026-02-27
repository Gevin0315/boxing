import axios, {
  type AxiosInstance,
  type AxiosError,
  type InternalAxiosRequestConfig,
  type AxiosResponse
} from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/types/auth'
import type { ApiResponse } from '@/types/common'

/** 创建axios实例 */
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000
})

/** 请求拦截器 */
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加token
    const token = getToken()
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

/** 响应拦截器 */
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data

    // 判断响应状态码
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')

      // 401: 未授权，跳转登录
      if (res.code === 401) {
        removeToken()
        window.location.href = '/login'
      }

      return Promise.reject(new Error(res.msg || 'Error'))
    }

    // 返回数据
    return res.data
  },
  (error: AxiosError) => {
    console.error('Response error:', error)

    let message = '请求失败'
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请登录'
          removeToken()
          window.location.href = '/login'
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        case 502:
          message = '网关错误'
          break
        case 503:
          message = '服务不可用'
          break
        case 504:
          message = '网关超时'
          break
        default:
          message = error.message
      }
    } else if (error.message.includes('timeout')) {
      message = '请求超时'
    } else if (error.message.includes('Network')) {
      message = '网络连接失败'
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
