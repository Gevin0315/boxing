import { PageQuery, PageResult } from './common'

/** 课程信息 */
export interface Course {
  id?: number
  courseName: string
  courseType: string
  category: string
  level: string
  maxCapacity: number
  duration: number // 分钟
  price: number
  description?: string
  coachId?: number
  coachName?: string
  imageUrl?: string
  status: '0' | '1' // 0-启用 1-禁用
  createTime?: string
  updateTime?: string
}

/** 课程查询参数 */
export interface CourseQuery extends PageQuery {
  courseName?: string
  courseType?: string
  category?: string
  level?: string
  coachId?: number
  status?: string
}

/** 课程表单 */
export interface CourseForm {
  id?: number
  courseName: string
  courseType: string
  category: string
  level: string
  maxCapacity: number
  duration: number
  price: number
  description?: string
  coachId?: number
  imageUrl?: string
  status: '0' | '1'
}
