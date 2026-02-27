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

/** 查询排课列表 */
export function listSchedule(query: any) {
  return request.get('/course-schedule/list', { params: query })
}

/** 查询排课详情 */
export function getSchedule(id: number) {
  return request.get(`/course-schedule/${id}`)
}

/** 新增排课 */
export function addSchedule(data: CourseSchedule) {
  return request.post('/course-schedule', data)
}

/** 修改排课 */
export function updateSchedule(data: CourseSchedule) {
  return request.put('/course-schedule', data)
}

/** 删除排课 */
export function delSchedule(ids: number[]) {
  return request.delete(`/course-schedule/${ids.join(',')}`)
}

/** 取消排课 */
export function cancelSchedule(id: number, reason: string) {
  return request.put(`/course-schedule/cancel`, { id, reason })
}

/** 完成排课 */
export function completeSchedule(id: number) {
  return request.put(`/course-schedule/complete`, { id })
}

/** 获取排课日历数据 */
export function getScheduleCalendar(params: { startDate: string; endDate: string }) {
  return request.get('/course-schedule/calendar', { params })
}
