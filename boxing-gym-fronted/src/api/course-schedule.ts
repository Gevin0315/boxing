import request from '@/utils/request'

/** 课程排课信息 */
export interface CourseSchedule {
  id?: number
  courseId: number
  courseName?: string
  courseType?: string
  coachId: number
  coachName?: string
  scheduleDate: string
  startTime: string
  endTime: string
  classroom: string
  maxCapacity: number
  currentCount?: number
  status: '0' | '1' | '2' // 0-正常 1-已取消 2-已完成
  remark?: string
  createTime?: string
}

const toIsoDate = (value?: string) => (value ? value.slice(0, 10) : '')
const toTime = (value?: string) => (value ? value.slice(11, 16) : '')

const mapSchedule = (item: any): CourseSchedule => ({
  id: item.id,
  courseId: item.courseId,
  coachId: item.coachId,
  scheduleDate: toIsoDate(item.startTime),
  startTime: toTime(item.startTime),
  endTime: toTime(item.endTime),
  classroom: '',
  maxCapacity: Number(item.maxPeople || 0),
  currentCount: Number(item.currentPeople || 0),
  status: toFrontendStatus(item.status),
  createTime: item.createTime
})

/** 统一状态映射规则：'0'正常、'1'已取消、'2'已完成 */
const toFrontendStatus = (status?: number | string): '0' | '1' | '2' => {
  const num = Number(status)
  if (num === 0) return '0'
  if (num === 1) return '1'
  if (num === 2) return '2'
  return '0' // 默认正常
}

const toBackendStatus = (status?: string | number): number => {
  const str = String(status)
  if (str === '0') return 0
  if (str === '1') return 1
  if (str === '2') return 2
  return 0 // 默认正常
}

const toBackendPayload = (data: CourseSchedule) => ({
  id: data.id,
  courseId: data.courseId,
  coachId: data.coachId,
  startTime: `${data.scheduleDate} ${data.startTime}:00`,
  endTime: `${data.scheduleDate} ${data.endTime}:00`,
  maxPeople: Number(data.maxCapacity || 0),
  currentPeople: Number(data.currentCount || 0),
  status: toBackendStatus(data.status)
})

/** 查询排课列表 */
export async function listSchedule(query: any) {
  const list = await request.get<any[]>('/course-schedule/list')
  let rows = (list || []).map(mapSchedule)

  if (query.courseId) {
    rows = rows.filter((s) => s.courseId === query.courseId)
  }
  if (query.coachId) {
    rows = rows.filter((s) => s.coachId === query.coachId)
  }
  if (query.status) {
    rows = rows.filter((s) => String(s.status) === String(query.status))
  }
  if (query.startDate) {
    rows = rows.filter((s) => s.scheduleDate >= query.startDate)
  }
  if (query.endDate) {
    rows = rows.filter((s) => s.scheduleDate <= query.endDate)
  }

  const total = rows.length
  const pageNum = query.pageNum || 1
  const pageSize = query.pageSize || 10
  const start = (pageNum - 1) * pageSize
  rows = rows.slice(start, start + pageSize)
  return { rows, total }
}

/** 查询排课详情 */
export async function getSchedule(id: number) {
  const data = await request.get<any>(`/course-schedule/${id}`)
  return mapSchedule(data)
}

/** 新增排课 */
export function addSchedule(data: CourseSchedule) {
  return request.post('/course-schedule', toBackendPayload(data))
}

/** 修改排课 */
export function updateSchedule(data: CourseSchedule) {
  return request.put('/course-schedule', toBackendPayload(data))
}

/** 删除排课 */
export function delSchedule(ids: number[]) {
  return Promise.all(ids.map((id) => request.delete(`/course-schedule/${id}`)))
}

/** 取消排课 */
export function cancelSchedule(id: number, reason: string) {
  return request.put('/course-schedule/cancel', null, { params: { id, reason } })
}

/** 完成排课 */
export function completeSchedule(id: number) {
  return request.put('/course-schedule/complete', null, { params: { id } })
}

/** 获取排课日历数据 */
export function getScheduleCalendar(params: { startDate: string; endDate: string }) {
  return request.get('/course-schedule/calendar', { params })
}
