import request from '@/utils/request'
import type { SysUser, SysUserQuery, SysUserForm, RoleOption } from '@/types/sys-user'

const mapSysUser = (item: any): SysUser => ({
  id: item.id,
  username: item.username || '',
  nickname: item.realName || '',
  status: item.status,
  role: item.role || '',
  roleDescription: item.roleDescription || '',
  phone: item.phone || '',
  email: item.email || '',
  remark: item.remark || '',
  createTime: item.createTime,
  updateTime: item.updateTime
})

/** 查询系统用户列表 */
export async function listSysUser(query: SysUserQuery) {
  const list = await request.get<any[]>('/sys-user/list')
  let rows = (list || []).map(mapSysUser)

  if (query.username) {
    rows = rows.filter((u) => u.username.includes(query.username!))
  }
  if (query.nickname) {
    rows = rows.filter((u) => u.nickname.includes(query.nickname!))
  }
  if (query.role) {
    rows = rows.filter((u) => (u.role || '').includes(query.role!))
  }
  if (query.status) {
    rows = rows.filter((u) => u.status === query.status)
  }

  const total = rows.length
  const pageNum = query.pageNum || 1
  const pageSize = query.pageSize || 10
  const start = (pageNum - 1) * pageSize
  rows = rows.slice(start, start + pageSize)

  return { rows, total }
}

/** 查询系统用户详情 */
export async function getSysUser(id: number) {
  const data = await request.get<any>(`/sys-user/${id}`)
  return mapSysUser(data)
}

/** 新增系统用户 */
export function addSysUser(data: SysUserForm) {
  return request.post('/sys-user', {
    username: data.username,
    password: data.password,
    realName: data.nickname,
    role: data.role,
    status: data.status,
    phone: data.phone || '',
    email: data.email || '',
    remark: data.remark || ''
  })
}

/** 修改系统用户 */
export function updateSysUser(data: SysUserForm) {
  return request.put('/sys-user', {
    id: data.id,
    password: data.password,
    realName: data.nickname,
    role: data.role,
    status: data.status,
    phone: data.phone || '',
    email: data.email || '',
    remark: data.remark || ''
  })
}

/** 删除系统用户 */
export function delSysUser(ids: number[]) {
  return Promise.all(ids.map((id) => request.delete(`/sys-user/${id}`)))
}

/** 重置用户密码 */
export function resetUserPassword(id: number, password: string) {
  return request.put('/sys-user/password', null, { params: { id, password } })
}

/** 修改用户状态 */
export function updateUserStatus(id: number, status: number) {
  return request.put('/sys-user/status', null, {
    params: { id, status }
  })
}
