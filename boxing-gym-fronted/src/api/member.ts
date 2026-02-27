import request from '@/utils/request'
import type { Member, MemberQuery, MemberForm } from '@/types/member'

/** 查询会员列表 */
export function listMember(query: MemberQuery) {
  return request.get('/member/list', { params: query })
}

/** 查询会员详情 */
export function getMember(id: number) {
  return request.get(`/member/${id}`)
}

/** 新增会员 */
export function addMember(data: MemberForm) {
  return request.post('/member', data)
}

/** 修改会员 */
export function updateMember(data: MemberForm) {
  return request.put('/member', data)
}

/** 删除会员 */
export function delMember(ids: number[]) {
  return request.delete(`/member/${ids.join(',')}`)
}

/** 会员充值 */
export function memberRecharge(id: number, amount: number) {
  return request.post('/member/recharge', { id, amount })
}

/** 会员扣费 */
export function memberDeduct(id: number, amount: number) {
  return request.post('/member/deduct', { id, amount })
}

/** 修改会员状态 */
export function updateMemberStatus(id: number, status: string) {
  return request.put(`/member/status`, { id, status })
}

/** 生成会员号 */
export function generateMemberNo() {
  return request.get('/member/generate-no')
}
