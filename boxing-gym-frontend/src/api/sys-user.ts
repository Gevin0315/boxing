import request from '@/utils/request'
import { USER_ROLE } from '@/constants/dict'
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
  const data: any = await request.get('/sys-user/page', {
    params: {
      current: query.pageNum || 1,
      size: query.pageSize || 10,
      username: query.username || undefined,
      realName: query.realName || undefined,
      phone: query.phone || undefined,
      role: query.role || undefined,
      status: query.status !== undefined && query.status !== null
        ? query.status
        : undefined
    }
  })
  return {
    rows: (data?.records || []).map(mapSysUser),
    total: data?.total || 0
  }
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

/** 获取角色列表 */
export function getRoleList(): Promise<RoleOption[]> {
  return Promise.resolve(USER_ROLE.map(item => ({
    value: item.value as string,
    label: item.label
  })))
}
