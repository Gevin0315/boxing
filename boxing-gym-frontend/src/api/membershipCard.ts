/**
 * 会员卡定义 API
 */
import request from '@/utils/request'
import type {
  MembershipCard,
  MembershipCardForm,
  MembershipCardQuery,
} from '@/types/membershipCard'
import type { ApiResponse, PageResult } from '@/types/common'

const BASE_URL = '/membership-card'

/** 获取在售卡片列表 */
export function getAvailableCards() {
  return request.get<ApiResponse<MembershipCard[]>>(`${BASE_URL}/available`)
}

/** 按分类获取卡片列表 */
export function getCardsByCategory(category: number) {
  return request.get<ApiResponse<MembershipCard[]>>(`${BASE_URL}/category/${category}`)
}

/** 获取卡片详情 */
export function getCardDetail(id: number) {
  return request.get<ApiResponse<MembershipCard>>(`${BASE_URL}/${id}`)
}

/** 分页查询卡片列表 */
export function getCardPage(params: MembershipCardQuery) {
  return request.get<ApiResponse<PageResult<MembershipCard>>>(`${BASE_URL}/page`, { params })
}

/** 创建卡片 */
export function createCard(data: MembershipCardForm) {
  return request.post<ApiResponse<number>>(BASE_URL, data)
}

/** 更新卡片 */
export function updateCard(id: number, data: MembershipCardForm) {
  return request.put<ApiResponse<void>>(`${BASE_URL}/${id}`, data)
}

/** 更新卡片状态 */
export function updateCardStatus(id: number, status: number) {
  return request.put<ApiResponse<void>>(`${BASE_URL}/${id}/status`, null, {
    params: { status },
  })
}

/** 删除卡片 */
export function deleteCard(id: number) {
  return request.delete<ApiResponse<void>>(`${BASE_URL}/${id}`)
}
