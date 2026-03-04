/**
 * 统一 API 响应类型
 */
export interface ApiResponse<T = any> {
  code: number
  msg?: string
  message?: string
  data: T
}

export interface PageResult<T> {
  rows: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface ApiError {
  code: number
  message: string
  stack?: string
}

/**
 * 错误码枚举
 */
export enum ErrorCode {
  SUCCESS = 200,
  BAD_REQUEST = 400,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  INTERNAL_ERROR = 500
}

/**
 * 判断 API 响应是否成功
 */
export function isSuccess<T>(response: ApiResponse<T>): response is ApiResponse<T> & { code: typeof ErrorCode.SUCCESS } {
  return response.code === ErrorCode.SUCCESS
}

/**
 * 获取错误信息
 */
export function getErrorMessage(error: any): string {
  if (error?.response?.data?.msg) {
    return error.response.data.msg
  }
  if (error?.message) {
    return error.message
  }
  return '请求失败，请稍后重试'
}
