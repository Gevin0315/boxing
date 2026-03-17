import { PageQuery, PageResult } from './common'

/** 系统用户信息 */
export interface SysUser {
  id?: number
  username: string
  nickname: string
  password?: string
  phone?: string
  email?: string
  status: '0' | '1' | '2' // 0-正常 1-禁用 2-锁定
  role: string
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 系统用户查询参数 */
export interface SysUserQuery extends PageQuery {
  username?: string
  nickname?: string
  phone?: string
  status?: string
  role?: string
}

/** 系统用户表单 */
export interface SysUserForm {
  id?: number
  username: string
  nickname: string
  password?: string
  phone?: string
  email?: string
  status: '0' | '1' | '2'
  role: string
  remark?: string
}
