import request from '@/utils/request'
import type { Coach, CoachQuery, CoachForm } from '@/types/coach'

/** 状态转换：前端'0'在职/'1'休假/'2'离职，后端1启用/0禁用 */
const toFrontendStatus = (status?: number | string): '0' | '1' | '2' => {
  const num = Number(status)
  if (num === 1) return '0'  // 后端1启用 -> 前端'0'在职
  if (num === 0) return '1'  // 后端0禁用 -> 前端'1'休假
  return '2'               // 其他 -> 前端'2'离职
}

const toBackendStatus = (status?: string | number): number => {
  const str = String(status)
  if (str === '0') return 1  // 前端'0'在职 -> 后端1启用
  if (str === '1') return 0  // 前端'1'休假 -> 后端0禁用
  return 0                 // 前端'2'离职 -> 后端0禁用
}

const mapCoach = (item: any, userMap: Record<number, any>): Coach => {
  const user = userMap[item.userId] || {}
  return {
    id: item.id,
    coachNo: `C${String(item.userId || item.id || 0).padStart(6, '0')}`,
    name: user.realName || '',
    gender: String(item.gender ?? 0) as '0' | '1',
    phone: item.phone || user.phone || '',
    specialties: item.specialty || '',
    level: String(item.level ?? 1) as '1' | '2' | '3' | '4',
    status: toFrontendStatus(user.status) as '0' | '1' | '2',
    imageUrl: item.imgUrl || '',
    description: item.intro || '',
    email: item.email || user.email || '',
    birthday: item.birthday || '',
    address: item.address || '',
    hireDate: item.hireDate || '',
    createTime: item.createTime,
    updateTime: item.updateTime
  }
}

async function getUserMap() {
  const users = await request.get<any[]>('/sys-user/list')
  return (users || []).reduce((acc: Record<number, any>, user: any) => {
    acc[user.id] = user
    return acc
  }, {})
}

/** 查询教练列表 */
export async function listCoach(query: CoachQuery) {
  const [profiles, userMap] = await Promise.all([
    request.get<any[]>('/coach-profile/list'),
    getUserMap()
  ])

  let rows = (profiles || []).map((item) => mapCoach(item, userMap))

  if (query.name) {
    rows = rows.filter((c) => c.name.includes(query.name!))
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

/** 查询教练详情 */
export async function getCoach(id: number) {
  const [profile, userMap] = await Promise.all([
    request.get<any>(`/coach-profile/${id}`),
    getUserMap()
  ])
  return mapCoach(profile, userMap)
}

/** 新增教练 */
export async function addCoach(data: CoachForm) {
  await request.post('/sys-user', {
    username: data.coachNo,
    password: '123456',
    realName: data.name,
    role: 'ROLE_COACH',
    status: toBackendStatus(data.status),
    phone: data.phone || '',
    email: data.email || '',
    remark: data.remark || ''
  })

  const users = await request.get<any[]>('/sys-user/list')
  const created = (users || []).find((u) => u.username === data.coachNo)
  if (!created?.id) {
    throw new Error('创建教练账号失败')
  }

  return request.post('/coach-profile', {
    userId: created.id,
    specialty: data.specialties,
    intro: data.description || '',
    imgUrl: data.imageUrl || '',
    gender: Number(data.gender) || 0,
    phone: data.phone || '',
    email: data.email || '',
    idCard: data.idCard || '',
    birthday: data.birthday || '',
    address: data.address || '',
    level: Number(data.level) || 1,
    hireDate: data.hireDate || ''
  })
}

/** 修改教练 */
export async function updateCoach(data: CoachForm) {
  const profile = await request.get<any>(`/coach-profile/${data.id}`)
  if (!profile?.userId) {
    throw new Error('教练账号不存在')
  }

  // 更新用户信息
  await request.put('/sys-user', {
    id: profile.userId,
    realName: data.name,
    status: toBackendStatus(data.status),
    phone: data.phone || '',
    email: data.email || '',
    remark: data.remark || ''
  })

  return request.put('/coach-profile', {
    id: data.id,
    userId: profile.userId,
    specialty: data.specialties,
    intro: data.description || '',
    imgUrl: data.imageUrl || '',
    gender: Number(data.gender) || 0,
    phone: data.phone || '',
    email: data.email || '',
    idCard: data.idCard || '',
    birthday: data.birthday || '',
    address: data.address || '',
    level: Number(data.level) || 1,
    hireDate: data.hireDate || ''
  })
}

/** 删除教练 */
export function delCoach(ids: number[]) {
  return Promise.all(ids.map((id) => request.delete(`/coach-profile/${id}`)))
}

/** 修改教练状态 - 需要修改关联用户的状态 */
export async function updateCoachStatus(id: number, status: string) {
  // 先获取教练档案，找到关联的用户 ID
  const profile = await request.get<any>(`/coach-profile/${id}`)

  // 更新关联用户的状态
  return request.put('/sys-user/status', null, {
    params: { id: profile.userId, status: toBackendStatus(status) }
  })
}

/** 获取教练下拉选项 */
export function getCoachOptions() {
  return request.get('/coach-profile/options')
}

/** 生成教练号 */
export function generateCoachNo() {
  return request.get('/coach-profile/generate-no')
}
