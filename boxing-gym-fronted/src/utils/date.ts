import dayjs, { Dayjs } from 'dayjs'

/** 格式化日期 */
export function formatDate(date: string | Date | Dayjs | number | null | undefined, format = 'YYYY-MM-DD'): string {
  if (!date) return ''
  return dayjs(date).format(format)
}

/** 格式化日期时间 */
export function formatDateTime(date: string | Date | Dayjs | number | null | undefined): string {
  return formatDate(date, 'YYYY-MM-DD HH:mm:ss')
}

/** 格式化时间 */
export function formatTime(date: string | Date | Dayjs | number | null | undefined): string {
  return formatDate(date, 'HH:mm:ss')
}

/** 获取当前日期 */
export function getCurrentDate(format = 'YYYY-MM-DD'): string {
  return dayjs().format(format)
}

/** 获取当前日期时间 */
export function getCurrentDateTime(): string {
  return dayjs().format('YYYY-MM-DD HH:mm:ss')
}

/** 获取相对时间 */
export function getRelativeTime(date: string | Date | Dayjs | number, unit: dayjs.ManipulateType, value = 1): string {
  return dayjs().add(value, unit).format('YYYY-MM-DD')
}

/** 获取日期范围 */
export function getDateRange(type: 'today' | 'yesterday' | 'week' | 'month' | 'year'): [string, string] {
  const now = dayjs()
  switch (type) {
    case 'today':
      return [now.format('YYYY-MM-DD'), now.format('YYYY-MM-DD')]
    case 'yesterday':
      return [now.subtract(1, 'day').format('YYYY-MM-DD'), now.subtract(1, 'day').format('YYYY-MM-DD')]
    case 'week':
      return [now.startOf('week').format('YYYY-MM-DD'), now.endOf('week').format('YYYY-MM-DD')]
    case 'month':
      return [now.startOf('month').format('YYYY-MM-DD'), now.endOf('month').format('YYYY-MM-DD')]
    case 'year':
      return [now.startOf('year').format('YYYY-MM-DD'), now.endOf('year').format('YYYY-MM-DD')]
    default:
      return ['', '']
  }
}

/** 计算日期差 */
export function dateDiff(date1: string | Date, date2: string | Date, unit: dayjs.OpUnitType = 'day'): number {
  return dayjs(date1).diff(dayjs(date2), unit)
}

/** 判断是否过期 */
export function isExpired(date: string | Date | Dayjs): boolean {
  return dayjs(date).isBefore(dayjs(), 'day')
}

/** 计算剩余天数 */
export function remainingDays(date: string | Date): number {
  return dayjs(date).diff(dayjs(), 'day')
}
