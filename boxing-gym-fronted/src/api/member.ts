import request from '@/utils/request'
import type { Member, MemberQuery, MemberForm } from '@/types/member'

/** 状态转换：支持三种状态：'0'正常、'1'禁用、'2'锁定 */
const toFrontendStatus = (status?: number | string): '0' | '1' | '2' => {
  const num = Number(status)
  if (num === 0) return '0'
  if (num === 1) return '1'
  if (num === 2) return '2'
  return '1' // 默认禁用
}

const toBackendStatus = (status?: string | number): number => {
  const str = String(status)
  if (str === '0') return 0
  if (str === '1') return 1
  if (str === '2') return 2
  return 1 // 默认禁用
}

const mapMember = (item: any): Member => ({
  id: item.id,
  memberNo: item.memberNo || '',
  name: item.name || '',
  gender: String(item.gender ?? '0') as '0' | '1',
  phone: item.phone || '',
  status: toFrontendStatus(item.status),
  membershipLevel: '1',
  expiryDate: item.cardExpireDate || '',
  remainingBalance: Number(item.balance || 0),
  createTime: item.createTime,
  updateTime: item.updateTime
})

const toBackendMember = (data: MemberForm) => ({
  id: data.id,
  memberNo: data.memberNo,
  name: data.name,
  gender: Number(data.gender),
  phone: data.phone,
  balance: data.remainingBalance ?? 0,
  cardExpireDate: data.expiryDate || null,
  status: toBackendStatus(data.status)
})

/** 查询会员列表 */
export async function listMember(query: MemberQuery) {
  const list = await request.get<any[]>('/member/list')
  let rows = (list || []).map(mapMember)

  if (query.memberNo) {
    rows = rows.filter((m) => m.memberNo.includes(query.memberNo!))
  }
  if (query.name) {
    rows = rows.filter((m) => m.name.includes(query.name!))
  }
  if (query.phone) {
    rows = rows.filter((m) => m.phone.includes(query.phone!))
  }
  if (query.status) {
    rows = rows.filter((m) => m.status === query.status)
  }

  const total = rows.length
  const pageNum = query.pageNum || 1
  const pageSize = query.pageSize || 10
  const start = (pageNum - 1) * pageSize
  rows = rows.slice(start, start + pageSize)
  return { rows, total }
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

/** 生成会员号 */
export function generateMemberNo() {
  return request.get('/member/generate-no')
}
