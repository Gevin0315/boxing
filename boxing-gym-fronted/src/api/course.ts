import request from '@/utils/request'
import type { Course, CourseQuery, CourseForm } from '@/types/course'

/** 查询课程列表 */
export function listCourse(query: CourseQuery) {
  return request.get('/course/list', { params: query })
}

/** 查询课程详情 */
export function getCourse(id: number) {
  return request.get(`/course/${id}`)
}

/** 新增课程 */
export function addCourse(data: CourseForm) {
  return request.post('/course', data)
}

/** 修改课程 */
export function updateCourse(data: CourseForm) {
  return request.put('/course', data)
}

/** 删除课程 */
export function delCourse(ids: number[]) {
  return request.delete(`/course/${ids.join(',')}`)
}

/** 修改课程状态 */
export function updateCourseStatus(id: number, status: string) {
  return request.put(`/course/status`, { id, status })
}

/** 获取课程下拉选项 */
export function getCourseOptions() {
  return request.get('/course/options')
}
