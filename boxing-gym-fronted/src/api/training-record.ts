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

const mapTraining = (item: any): TrainingRecord => ({
  id: item.id,
  scheduleId: item.scheduleId,
  memberId: item.memberId,
  coachId: item.coachId,
  checkInTime: item.checkinTime || item.checkInTime || '',
  checkOutTime: item.checkoutTime || '',
  status: String(item.status ?? 0) as '0' | '1' | '2',
  remark: item.remark || '',
  createTime: item.createTime
})

/** 查询签到记录列表 */
export async function listTrainingRecord(query: any) {
  const list = await request.get<any[]>('/training-record/list')
  let rows = (list || []).map(mapTraining)

  if (query.memberId) {
    rows = rows.filter((r) => r.memberId === query.memberId)
  }
  if (query.scheduleId) {
    rows = rows.filter((r) => r.scheduleId === query.scheduleId)
  }
  if (query.status) {
    rows = rows.filter((r) => String(r.status) === String(query.status))
  }

  const total = rows.length
  const pageNum = query.pageNum || 1
  const pageSize = query.pageSize || 10
  const start = (pageNum - 1) * pageSize
  rows = rows.slice(start, start + pageSize)
  return { rows, total }
}

/** 查询签到记录详情 */
export async function getTrainingRecord(id: number) {
  const data = await request.get<any>(`/training-record/${id}`)
  return mapTraining(data)
}

/** 新增签到记录 */
export function addTrainingRecord(data: TrainingRecord) {
  return request.post('/training-record', {
    id: data.id,
    scheduleId: data.scheduleId,
    memberId: data.memberId,
    coachId: data.coachId,
    checkinTime: data.checkInTime || null,
    status: Number(data.status || 0),
    remark: data.remark || ''
  })
}

/** 修改签到记录 */
export function updateTrainingRecord(data: TrainingRecord) {
  return request.put('/training-record', {
    id: data.id,
    scheduleId: data.scheduleId,
    memberId: data.memberId,
    coachId: data.coachId,
    checkinTime: data.checkInTime || null,
    status: Number(data.status || 0),
    remark: data.remark || ''
  })
}

/** 删除签到记录 */
export function delTrainingRecord(ids: number[]) {
  return Promise.all(ids.map((id) => request.delete(`/training-record/${id}`)))
}

/** 签到 */
export function checkIn(scheduleId: number, memberId: number) {
  return request.post('/training-record/check-in', null, { params: { scheduleId, memberId } })
}

/** 签退 */
export function checkOut(id: number) {
  return request.put('/training-record/check-out', null, { params: { id } })
}

/** 获取排课的签到记录 */
export function getScheduleCheckIns(scheduleId: number) {
  return request.get(`/training-record/schedule/${scheduleId}`)
}

/** 获取会员签到记录 */
export function getMemberCheckIns(memberId: number, params: any) {
  return request.get(`/training-record/member/${memberId}`, { params })
}
