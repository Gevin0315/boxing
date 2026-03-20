import request from '@/utils/request'
import type { Member, MemberQuery, MemberForm } from '@/types/member'

/** 状态转换：后端 0-禁用, 1-正常，前端保持一致 */
const toFrontendStatus = (status?: number | string): '0' | '1' => {
  const str = String(status)
  if (str === '1') return '1'
  if (str === '0') return '0'
  return '0' // 默认禁用
}

const toBackendStatus = (status?: string | number): number => {
  const str = String(status)
  if (str === '1') return 1
  if (str === '0') return 0
  return 0 // 默认禁用
}

const mapMember = (item: any): Member => ({
  id: item.id,
  name: item.name || '',
  gender: String(item.gender ?? 0) as '0' | '1',
  phone: item.phone || '',
  status: toFrontendStatus(item.status),
  membershipLevel: item.membershipLevel || '1',
  expiryDate: item.cardExpireDate || '',
  remainingBalance: Number(item.balance || 0),
  birthday: item.birthday || '',
  idCard: item.idCard || '',
  address: item.address || '',
  emergencyContact: item.emergencyContact || '',
  emergencyPhone: item.emergencyPhone || '',
  remark: item.remark || '',
  createTime: item.createTime,
  updateTime: item.updateTime
})

const toBackendMember = (data: MemberForm) => ({
  id: data.id,
  name: data.name,
  gender: Number(data.gender),
  phone: data.phone,
  balance: data.remainingBalance ?? 0,
  cardExpireDate: data.expiryDate || null,
  status: toBackendStatus(data.status),
  birthday: data.birthday || null,
  idCard: data.idCard || null,
  address: data.address || null,
  emergencyContact: data.emergencyContact || null,
  emergencyPhone: data.emergencyPhone || null,
  membershipLevel: data.membershipLevel || '1',
  remark: data.remark || null
})

/** 查询会员列表 */
export async function listMember(query: MemberQuery) {
  const data = await request.get<any>('/member/page', {
    params: {
      current: query.pageNum || 1,
      size: query.pageSize || 10,
      name: query.name || undefined,
      phone: query.phone || undefined,
      status: query.status !== undefined && query.status !== null && query.status !== ''
        ? toBackendStatus(query.status)
        : undefined
    }
  })
  return {
    rows: (data.records || []).map(mapMember),
    total: data.total || 0
  }
}

/** 查询会员详情 */
export async function getMember(id: number) {
  const data = await request.get<any>(`/member/${id}`)
  return mapMember(data)
}

/** 新增会员 */
export function addMember(data: MemberForm) {
  return request.post('/member', toBackendMember(data))
}

/** 修改会员 */
export function updateMember(data: MemberForm) {
  return request.put('/member', toBackendMember(data))
}

/** 删除会员 */
export function delMember(ids: number[]) {
  return Promise.all(ids.map((id) => request.delete(`/member/${id}`)))
}

/** 会员充值 */
export function memberRecharge(id: number, amount: number) {
  return request.post('/member/recharge', null, { params: { id, amount, payMethod: 3 } })
}

/** 会员扣费 */
export function memberDeduct(id: number, amount: number) {
  return request.post('/member/deduct', null, { params: { id, amount, remark: '前台扣费' } })
}

/** 修改会员状态 */
export function updateMemberStatus(id: number, status: string) {
  return request.put('/member/status', null, {
    params: { id, status: toBackendStatus(status) }
  })
}
