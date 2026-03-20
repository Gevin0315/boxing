import { PageQuery, PageResult } from './common'

/** 教练信息 */
export interface Coach {
  id?: number
  name: string
  gender: '0' | '1' // 0-男 1-女
  phone: string
  email?: string
  idCard?: string
  birthday?: string
  address?: string
  specialties: string // 专长
  level: string
  status: '0' | '1' | '2' // 0-在职 1-休假 2-离职
  hireDate?: string
  imageUrl?: string
  description?: string
  createTime?: string
  updateTime?: string
}

/** 教练查询参数 */
export interface CoachQuery extends PageQuery {
  name?: string
  phone?: string
  specialties?: string
  level?: string
  status?: string
}

/** 教练表单 */
export interface CoachForm {
  id?: number
  name: string
  gender: '0' | '1'
  phone: string
  email?: string
  idCard?: string
  birthday?: string
  address?: string
  specialties: string
  level: string
  status: '0' | '1' | '2'
  hireDate?: string
  imageUrl?: string
  description?: string
}
