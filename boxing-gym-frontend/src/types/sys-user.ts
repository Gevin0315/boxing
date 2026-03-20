import { PageQuery, PageResult } from './common'

/** 系统用户信息 */
export interface SysUser {
  id?: number
  username: string
  nickname: string
  password?: string
  phone?: string
  email?: string
  status: number // 0-禁用 1-启用
  role: string
  roleDescription?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 系统用户查询参数 */
export interface SysUserQuery extends PageQuery {
  username?: string
  realName?: string
  phone?: string
  status?: number
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
  status: number
  role: string
  remark?: string
}

/** 角色选项 */
export interface RoleOption {
  value: string
  label: string
}
