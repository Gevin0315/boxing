import axios, {
  type AxiosInstance,
  type AxiosError,
  type InternalAxiosRequestConfig,
  type AxiosRequestConfig,
  type AxiosResponse
} from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/types/auth'
import type { ApiResponse, ErrorCode } from '@/types/api'
import router from '@/router'

/**
 * 请求配置
 */
const CONFIG = {
  timeout: 30000,
  retryCount: 2,
  retryDelay: 1000,
  debounceTime: 300
}

/**
 * 请求缓存 Map - 用于防止重复请求
 * key: 请求标识, value: AbortController
 */
const requestCache = new Map<string, AbortController>()

/**
 * 生成缓存 key
 */
const getCacheKey = (config: AxiosRequestConfig): string => {
  return `${config.method}-${config.url}-${JSON.stringify(config.params || config.data)}`
}

/**
 * 清除指定缓存
 */
export const clearRequestCache = (url?: string) => {
  if (url) {
    requestCache.forEach((controller, key) => {
      if (key.includes(url)) {
        controller.abort()
        requestCache.delete(key)
      }
    })
  } else {
    requestCache.forEach((controller) => controller.abort())
    requestCache.clear()
  }
}

/**
 * 重试请求
 */
const retryRequest = async (
  requestFn: () => Promise<any>,
  retries: number = CONFIG.retryCount
): Promise<any> => {
  try {
    return await requestFn()
  } catch (error) {
    if (retries <= 0) {
      throw error
    }

    // 等待后重试
    await new Promise(resolve => setTimeout(resolve, CONFIG.retryDelay))
    console.warn(`请求失败，${retries} 次重试中...`, error)

    return retryRequest(requestFn, retries - 1)
  }
}

/**
 * 创建 axios 实例
 */
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: CONFIG.timeout
})

/**
 * 请求拦截器
 */
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 生成缓存 key
    const cacheKey = getCacheKey(config)

    // 检查是否有相同请求正在进行
    if (requestCache.has(cacheKey)) {
      // 取消之前的请求
      const controller = requestCache.get(cacheKey)!
      controller.abort()
      requestCache.delete(cacheKey)
    }

    // 创建新的 AbortController
    const controller = new AbortController()
    requestCache.set(cacheKey, controller)

    // 添加 token
    const token = getToken()
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 添加 AbortController signal
    config.signal = controller.signal

    // 添加请求 ID 用于追踪
    config.metadata = {
      cacheKey,
      requestId: `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
    }

    return config
  },
  (error: AxiosError) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 */
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data

    // 请求完成后清除缓存
    const cacheKey = response.config.metadata?.cacheKey
    if (cacheKey && requestCache.has(cacheKey)) {
      requestCache.delete(cacheKey)
    }

    // 判断响应状态码
    if (res.code !== 200) {
      const errorMsg = res.msg || '请求失败'

      // 根据不同错误码做不同处理
      if (res.code === ErrorCode.UNAUTHORIZED) {
        removeToken()
        ElMessage.warning('登录已过期，请重新登录')
        router.push('/login')
        return Promise.reject(new Error(errorMsg))
      }

      if (res.code === ErrorCode.FORBIDDEN) {
        ElMessage.error('没有权限访问该资源')
        return Promise.reject(new Error(errorMsg))
      }

      ElMessage.error(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }

    // 返回数据
    return res.data
  },
  (error: AxiosError) => {
    // 响应错误，清除缓存
    const cacheKey = error.config?.metadata?.cacheKey
    if (cacheKey && requestCache.has(cacheKey)) {
      requestCache.delete(cacheKey)
    }

    console.error('Response error:', error)

    // 网络错误处理
    if (!error.response) {
      if (error.code === 'ECONNABORTED') {
        // 请求被取消，不提示错误
        return Promise.reject(error)
      }
      if (error.message.includes('timeout')) {
        ElMessage.error('请求超时，请检查网络连接')
      } else if (error.message.includes('Network')) {
        ElMessage.error('网络连接失败，请检查网络设置')
      } else {
        ElMessage.error('网络异常，请稍后重试')
      }
      return Promise.reject(error)
    }

    // HTTP 错误处理
    const status = error.response?.status
    let message = '请求失败'

    switch (status) {
      case 400:
        message = '请求参数错误'
        break
      case 401:
        message = '未授权，请登录'
        removeToken()
        router.push('/login')
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
        message = error.message || '请求失败'
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }
)

/**
 * 通用 GET 请求（带防抖和缓存）
 */
export const get = <T = any>(url: string, config?: AxiosRequestConfig) => {
  return service.get<T, ApiResponse<T>>(url, config)
}

/**
 * 通用 POST 请求（带防抖和缓存）
 */
export const post = <T = any>(url: string, data?: any, config?: AxiosRequestConfig) => {
  return service.post<T, ApiResponse<T>>(url, data, config)
}

/**
 * 通用 PUT 请求
 */
export const put = <T = any>(url: string, data?: any, config?: AxiosRequestConfig) => {
  return service.put<T, ApiResponse<T>>(url, data, config)
}

/**
 * 通用 DELETE 请求
 */
export const del = <T = any>(url: string, config?: AxiosRequestConfig) => {
  return service.delete<T, ApiResponse<T>>(url, config)
}

/**
 * 通用文件上传
 */
export const upload = <T = any>(url: string, file: File, config?: AxiosRequestConfig) => {
  const formData = new FormData()
  formData.append('file', file)

  return service.post<T, ApiResponse<T>>(url, formData, {
    ...config,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 下载文件
 */
export const download = (url: string, filename?: string, config?: AxiosRequestConfig) => {
  return service.get(url, {
    ...config,
    responseType: 'blob'
  }).then(response => {
    const blob = new Blob([response.data])
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = filename || `download_${Date.now()}`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(link.href)
  })
}

export default service
