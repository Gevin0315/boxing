import request from '@/utils/request'
import type { FinanceOrder, FinanceOrderQuery, FinanceOrderForm } from '@/types/finance'

/** 查询财务订单列表 */
export function listFinanceOrder(query: FinanceOrderQuery) {
  return request.get('/finance-order/list', { params: query })
}

/** 查询财务订单详情 */
export function getFinanceOrder(id: number) {
  return request.get(`/finance-order/${id}`)
}

/** 新增财务订单 */
export function addFinanceOrder(data: FinanceOrderForm) {
  return request.post('/finance-order', data)
}

/** 修改财务订单 */
export function updateFinanceOrder(data: FinanceOrderForm) {
  return request.put('/finance-order', data)
}

/** 删除财务订单 */
export function delFinanceOrder(ids: number[]) {
  return request.delete(`/finance-order/${ids.join(',')}`)
}

/** 支付订单 */
export function payOrder(id: number, paymentMethod: string, amount?: number) {
  return request.post('/finance-order/pay', { id, paymentMethod, amount })
}

/** 退款订单 */
export function refundOrder(id: number, reason: string) {
  return request.post('/finance-order/refund', { id, reason })
}

/** 获取财务统计 */
export function getFinanceStats(params: { startDate?: string; endDate?: string }) {
  return request.get('/finance-order/stats', { params })
}

/** 生成订单号 */
export function generateOrderNo() {
  return request.get('/finance-order/generate-no')
}
