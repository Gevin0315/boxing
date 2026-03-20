import request from '@/utils/request'
import type { Course, CourseQuery, CourseForm } from '@/types/course'

const toFrontendStatus = (status?: number | string) => (Number(status) === 1 ? '0' : '1')
const toBackendStatus = (status?: string | number) => (String(status) === '0' ? 1 : 0)
const toBackendType = (courseType?: string) => (courseType === 'private' ? 2 : 1)
const toFrontendType = (type?: number | string) => (Number(type) === 2 ? 'private' : 'group')

const mapCourse = (item: any): Course => ({
  id: item.id,
  courseName: item.courseName || item.name || '',
  courseType: toFrontendType(item.courseType ?? item.type),
  category: item.category || '',
  level: item.level || '',
  maxCapacity: item.maxCapacity || 20,
  duration: Number(item.duration || 60),
  price: Number(item.price || 0),
  description: item.description || '',
  coachId: item.coachId,
  coachName: item.coachName || '',
  imageUrl: item.imageUrl || item.coverImg || '',
  status: toFrontendStatus(item.status) as '0' | '1',
  createTime: item.createTime,
  updateTime: item.updateTime
})

const buildCoursePayload = (data: CourseForm) => ({
  id: data.id,
  name: data.courseName,
  code: data.id ? undefined : `C${Date.now()}`,
  description: data.description || '',
  coverImg: data.imageUrl || '',
  type: toBackendType(data.courseType),
  duration: Number(data.duration || 60),
  status: toBackendStatus(data.status),
  sortOrder: 0,
  category: data.category || '',
  level: data.level || '',
  maxCapacity: data.maxCapacity,
  price: data.price,
  coachId: data.coachId
})

/** 查询课程分页列表 */
export async function pageCourse(query: CourseQuery) {
  const params: Record<string, any> = {
    current: query.pageNum || 1,
    size: query.pageSize || 10
  }
  if (query.courseName) params.name = query.courseName
  if (query.courseType) params.type = query.courseType === 'private' ? 2 : 1
  if (query.category) params.category = query.category
  if (query.level) params.level = query.level
  if (query.coachId) params.coachId = query.coachId
  if (query.status) params.status = query.status === '0' ? 1 : 0

  const data = await request.get<{ records: any[]; total: number; current: number; size: number }>('/course/page', { params })
  return {
    rows: data.records.map(mapCourse),
    total: data.total
  }
}

/** 查询课程列表（改为调用分页接口）*/
export async function listCourse(query: CourseQuery) {
  return pageCourse(query)
}

/** 查询课程详情 */
export async function getCourse(id: number) {
  const data = await request.get<any>(`/course/${id}`)
  return mapCourse(data)
}

/** 新增课程 */
export function addCourse(data: CourseForm) {
  return request.post('/course', buildCoursePayload(data))
}

/** 修改课程 */
export function updateCourse(data: CourseForm) {
  return request.put('/course', buildCoursePayload(data))
}

/** 删除课程 */
export function delCourse(ids: number[]) {
  return Promise.all(ids.map((id) => request.delete(`/course/${id}`)))
}

/** 修改课程状态 */
export function updateCourseStatus(id: number, status: string) {
  return request.put('/course/status', null, {
    params: { id, status: toBackendStatus(status) }
  })
}

/** 获取课程下拉选项 */
export function getCourseOptions() {
  return request.get('/course/options')
}
