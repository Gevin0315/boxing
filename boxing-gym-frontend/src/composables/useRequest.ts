import { ref, type Ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types/api'

/**
 * 统一请求处理 Hook
 * 自动管理 loading 状态、错误处理和请求取消
 */
export function useRequest<T = any>() {
  const loading = ref(false)
  const error = Ref<string | null>(ref(null))

  let abortController: AbortController | null = null

  const execute = async (
    requestFn: () => Promise<ApiResponse<T>>,
    options?: {
      onSuccess?: (data: T) => void
      onError?: (error: string) => void
      showSuccessMessage?: boolean
      showErrorMessage?: boolean
    }
  ): Promise<T | null> => {
    // 取消上一次请求
    if (abortController) {
      abortController.abort()
    }

    abortController = new AbortController()
    loading.value = true
    error.value = null

    try {
      const response = await requestFn()

      if (response.code === 200) {
        if (options?.showSuccessMessage !== false) {
          ElMessage.success(response.msg || '操作成功')
        }
        options?.onSuccess?.(response.data)
        return response.data
      } else {
        error.value = response.msg || '请求失败'
        if (options?.showErrorMessage !== false) {
          ElMessage.error(response.msg || '请求失败')
        }
        options?.onError?.(response.msg || '请求失败')
        return null
      }
    } catch (err: any) {
      loading.value = false

      // 不处理取消的请求
      if (err.name === 'AbortError') {
        return null
      }

      const errorMsg = getErrorMessage(err)
      error.value = errorMsg

      if (options?.showErrorMessage !== false) {
        ElMessage.error(errorMsg)
      }
      options?.onError?.(errorMsg)
      return null
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    execute
  }
}

function getErrorMessage(error: any): string {
  if (error?.response?.data?.msg) {
    return error.response.data.msg
  }
  if (error?.message) {
    return error.message
  }
  return '请求失败，请稍后重试'
}
