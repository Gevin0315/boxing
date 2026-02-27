import request from '@/utils/request'
import type { SysUser, SysUserQuery, SysUserForm } from '@/types/sys-user'

/** 查询系统用户列表 */
export function listSysUser(query: SysUserQuery) {
  return request.get('/sys-user/list', { params: query })
}

/** 查询系统用户详情 */
export function getSysUser(id: number) {
  return request.get(`/sys-user/${id}`)
}

/** 新增系统用户 */
export function addSysUser(data: SysUserForm) {
  return request.post('/sys-user', data)
}

/** 修改系统用户 */
export function updateSysUser(data: SysUserForm) {
  return request.put('/sys-user', data)
}

/** 删除系统用户 */
export function delSysUser(ids: number[]) {
  return request.delete(`/sys-user/${ids.join(',')}`)
}

/** 重置用户密码 */
export function resetUserPassword(id: number, password: string) {
  return request.put(`/sys-user/password`, { id, password })
}

/** 修改用户状态 */
export function updateUserStatus(id: number, status: string) {
  return request.put(`/sys-user/status`, { id, status })
}
