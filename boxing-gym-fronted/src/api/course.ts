import request from '@/utils/request'
import type { Course, CourseQuery, CourseForm } from '@/types/course'

const toFrontendStatus = (status?: number | string) => (Number(status) === 1 ? '0' : '1')
const toBackendStatus = (status?: string | number) => (String(status) === '0' ? 1 : 0)
const toBackendType = (courseType?: string) => (courseType === 'private' ? 2 : 1)
const toFrontendType = (type?: number | string) => (Number(type) === 2 ? 'private' : 'group')

const mapCourse = (item: any): Course => ({
  id: item.id,
  courseName: item.name || '',
  courseType: toFrontendType(item.type),
  category: '',
  level: '',
  maxCapacity: 20,
  duration: Number(item.duration || 60),
  price: 0,
  description: item.description || '',
  coachId: undefined,
  coachName: '',
  imageUrl: item.coverImg || '',
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
  sortOrder: 0
})

/** 查询课程列表 */
export async function listCourse(query: CourseQuery) {
  const list = await request.get<any[]>('/course/list')
  let rows = (list || []).map(mapCourse)

  if (query.courseName) {
    rows = rows.filter((c) => c.courseName.includes(query.courseName!))
  }
  if (query.courseType) {
    rows = rows.filter((c) => c.courseType === query.courseType)
  }
  if (query.status) {
    rows = rows.filter((c) => c.status === query.status)
  }

  const total = rows.length
  const pageNum = query.pageNum || 1
  const pageSize = query.pageSize || 10
  const start = (pageNum - 1) * pageSize
  rows = rows.slice(start, start + pageSize)
  return { rows, total }
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
