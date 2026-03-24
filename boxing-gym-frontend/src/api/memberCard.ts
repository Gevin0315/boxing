/**
 * 会员持卡 API
 */
import request from '@/utils/request'
import type {
  MemberCard,
  PurchaseCardDTO,
  PurchaseCardResult,
  PaymentResult,
  PendingOrder,
  ActivateCardDTO,
  VoidCardDTO,
  CardUsageRecord,
  CardUsageRecordQuery,
} from '@/types/memberCard'
import type { ApiResponse, PageResult } from '@/types/common'

const BASE_URL = '/member-card'

/** 购买会员卡 */
export function purchaseCard(data: PurchaseCardDTO) {
  return request.post<ApiResponse<PurchaseCardResult>>(`${BASE_URL}/purchase`, data)
}

/** 激活会员卡 */
export function activateCard(data: ActivateCardDTO) {
  return request.post<ApiResponse<void>>(`${BASE_URL}/activate`, data)
}

/** 作废会员卡 */
export function voidCard(data: VoidCardDTO) {
  return request.post<ApiResponse<void>>(`${BASE_URL}/void`, data)
}

/** 获取会员所有卡片 */
export function getMemberCards(memberId: number) {
  return request.get<ApiResponse<MemberCard[]>>(`${BASE_URL}/member/${memberId}`)
}

/** 获取会员可用的团课卡 */
export function getAvailableGroupCards(memberId: number) {
  return request.get<ApiResponse<MemberCard[]>>(`${BASE_URL}/member/${memberId}/available-group`)
}

/** 获取会员可用的私教卡 */
export function getAvailablePrivateCards(memberId: number) {
  return request.get<ApiResponse<MemberCard[]>>(`${BASE_URL}/member/${memberId}/available-private`)
}

/** 获取卡片详情 */
export function getMemberCardDetail(id: number) {
  return request.get<ApiResponse<MemberCard>>(`${BASE_URL}/${id}`)
}

/** 获取卡片使用记录 */
export function getCardUsageRecords(memberCardId: number) {
  return request.get<ApiResponse<CardUsageRecord[]>>(`${BASE_URL}/${memberCardId}/usage-records`)
}

/** 分页获取卡片使用记录 */
export function getCardUsageRecordPage(params: CardUsageRecordQuery) {
  return request.get<ApiResponse<PageResult<CardUsageRecord>>>(`${BASE_URL}/usage-records/page`, { params })
}

/** 验证卡片是否可用于签到 */
export function validateCardForCheckin(memberCardId: number, memberId: number, isPrivateClass: boolean) {
  return request.get<ApiResponse<boolean>>(`${BASE_URL}/${memberCardId}/validate-checkin`, {
    params: { memberId, isPrivateClass },
  })
}

/** 确认支付 */
export function confirmPayment(orderId: number) {
  return request.post<ApiResponse<PaymentResult>>(`${BASE_URL}/payment/confirm`, { orderId })
}

/** 取消订单 */
export function cancelOrder(orderId: number, reason?: string) {
  return request.post<ApiResponse<PaymentResult>>(`${BASE_URL}/payment/cancel`, { orderId, reason })
}

/** 获取会员待支付订单 */
export function getPendingOrders(memberId: number) {
  return request.get<ApiResponse<PendingOrder[]>>(`${BASE_URL}/payment/pending`, { params: { memberId } })
}

