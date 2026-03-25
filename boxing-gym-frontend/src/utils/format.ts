/** 格式化金额（分转元） */
export function formatMoney(cents: number): string {
  return (cents / 100).toFixed(2)
}

/** 格式化金额（元） */
export function formatCurrency(amount: number): string {
  return `¥${amount.toFixed(2)}`
}

/** 格式化百分比 */
export function formatPercent(value: number, decimals = 2): string {
  return `${(value * 100).toFixed(decimals)}%`
}

/** 隐藏手机号中间四位 */
export function maskPhone(phone: string): string {
  if (!phone || phone.length !== 11) return phone
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/** 隐藏身份证号 */
export function maskIdCard(idCard: string): string {
  if (!idCard) return idCard
  const len = idCard.length
  if (len === 15) {
    return idCard.replace(/(\d{6})\d{6}(\d{3})/, '$1******$2')
  } else if (len === 18) {
    return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
  }
  return idCard
}

/** 格式化文件大小 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return `${(bytes / Math.pow(k, i)).toFixed(2)} ${sizes[i]}`
}

/** 格式化时长（秒转分钟） */
export function formatDuration(seconds: number): string {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

/** 格式化时长（分钟） */
export function formatMinutes(minutes: number): string {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours > 0) {
    return `${hours}小时${mins}分钟`
  }
  return `${mins}分钟`
}

/** 数字千分位格式化 */
export function formatNumber(num: number): string {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

/** 获取字典标签 */
export function getDictLabel(options: Array<{ value: string | number; label: string }>, value: string | number): string {
  const item = options.find(item => item.value === value)
  return item?.label || String(value)
}

/**
 * 防抖函数
 * @param func 要防抖的函数
 * @param delay 延迟时间（毫秒）
 * @returns 防抖后的函数
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timeoutId: ReturnType<typeof setTimeout> | null = null
  return function (this: any, ...args: Parameters<T>) {
    if (timeoutId) {
      clearTimeout(timeoutId)
    }
    timeoutId = setTimeout(() => {
      func.apply(this, args)
    }, delay)
  }
}
