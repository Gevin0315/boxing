import request from '@/utils/request'
import type { Coach, CoachQuery, CoachForm } from '@/types/coach'

/** 查询教练列表 */
export function listCoach(query: CoachQuery) {
  return request.get('/coach-profile/list', { params: query })
}

/** 查询教练详情 */
export function getCoach(id: number) {
  return request.get(`/coach-profile/${id}`)
}

/** 新增教练 */
export function addCoach(data: CoachForm) {
  return request.post('/coach-profile', data)
}

/** 修改教练 */
export function updateCoach(data: CoachForm) {
  return request.put('/coach-profile', data)
}

/** 删除教练 */
export function delCoach(ids: number[]) {
  return request.delete(`/coach-profile/${ids.join(',')}`)
}

/** 修改教练状态 */
export function updateCoachStatus(id: number, status: string) {
  return request.put(`/coach-profile/status`, { id, status })
}

/** 获取教练下拉选项 */
export function getCoachOptions() {
  return request.get('/coach-profile/options')
}

/** 生成教练号 */
export function generateCoachNo() {
  return request.get('/coach-profile/generate-no')
}
