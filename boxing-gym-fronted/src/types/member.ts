import { PageQuery, PageResult } from './common'

/** 会员信息 */
export interface Member {
  id?: number
  memberNo: string
  name: string
  gender: '0' | '1' // 0-男 1-女
  phone: string
  birthday?: string
  idCard?: string
  address?: string
  emergencyContact?: string
  emergencyPhone?: string
  status: '0' | '1' | '2' // 0-正常 1-暂停 2-注销
  membershipLevel: string
  expiryDate?: string
  remainingBalance?: number
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 会员查询参数 */
export interface MemberQuery extends PageQuery {
  memberNo?: string
  name?: string
  phone?: string
  status?: string
  membershipLevel?: string
}

/** 会员表单 */
export interface MemberForm {
  id?: number
  memberNo?: string
  name: string
  gender: '0' | '1'
  phone: string
  birthday?: string
  idCard?: string
  address?: string
  emergencyContact?: string
  emergencyPhone?: string
  status: '0' | '1' | '2'
  membershipLevel: string
  expiryDate?: string
  remainingBalance?: number
  remark?: string
}

/** 会员等级 */
export interface MembershipLevel {
  value: string
  label: string
  price: number
  duration: number // 月数
}
