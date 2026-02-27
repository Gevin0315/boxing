import request from '@/utils/request'

/** 上课签到记录 */
export interface TrainingRecord {
  id?: number
  scheduleId: number
  scheduleDate?: string
  courseId?: number
  courseName?: string
  coachId?: number
  coachName?: string
  classroom?: string
  memberId: number
  memberNo?: string
  memberName?: string
  checkInTime?: string
  checkOutTime?: string
  status: '0' | '1' | '2' // 0-已签到 1-缺勤 2-请假
  remark?: string
  createTime?: string
}

/** 查询签到记录列表 */
export function listTrainingRecord(query: any) {
  return request.get('/training-record/list', { params: query })
}

/** 查询签到记录详情 */
export function getTrainingRecord(id: number) {
  return request.get(`/training-record/${id}`)
}

/** 新增签到记录 */
export function addTrainingRecord(data: TrainingRecord) {
  return request.post('/training-record', data)
}

/** 修改签到记录 */
export function updateTrainingRecord(data: TrainingRecord) {
  return request.put('/training-record', data)
}

/** 删除签到记录 */
export function delTrainingRecord(ids: number[]) {
  return request.delete(`/training-record/${ids.join(',')}`)
}

/** 签到 */
export function checkIn(scheduleId: number, memberId: number) {
  return request.post('/training-record/check-in', { scheduleId, memberId })
}

/** 签退 */
export function checkOut(id: number) {
  return request.post('/training-record/check-out', { id })
}

/** 获取排课的签到记录 */
export function getScheduleCheckIns(scheduleId: number) {
  return request.get(`/training-record/schedule/${scheduleId}`)
}

/** 获取会员签到记录 */
export function getMemberCheckIns(memberId: number, params: any) {
  return request.get(`/training-record/member/${memberId}`, { params })
}
