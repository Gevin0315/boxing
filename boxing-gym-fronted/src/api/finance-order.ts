import request from '@/utils/request'
import type { FinanceOrder, FinanceOrderQuery, FinanceOrderForm } from '@/types/finance'

/** 支付方式映射表 */
const PAYMENT_METHOD_MAP: Record<number, '0' | '1' | '2' | '3'> = {
  1: '0',  // 微信
  2: '1',  // 支付宝
  3: '2',  // 现金
  4: '3'   // 银行卡
}

const REVERSE_PAYMENT_METHOD_MAP: Record<'0' | '1' | '2' | '3', number> = {
  '0': 1,
  '1': 2,
  '2': 3,
  '3': 4
}

const toFrontendPaymentMethod = (method?: number | string): '0' | '1' | '2' | '3' => {
  const val = Number(method)
  return PAYMENT_METHOD_MAP[val] || '0'
}

const toBackendPayMethod = (method?: string | number): number => {
  const str = String(method)
  return REVERSE_PAYMENT_METHOD_MAP[str as '0' | '1' | '2' | '3'] || 1
}

/** 订单类型映射表 */
const ORDER_TYPE_MAP: Record<number, '0' | '1' | '2' | '3'> = {
  1: '0',  // 会员卡
  2: '1',  // 私教课
  3: '2',  // 团课
  4: '3'   // 商品
}

const REVERSE_ORDER_TYPE_MAP: Record<'0' | '1' | '2' | '3', number> = {
  '0': 1,
  '1': 2,
  '2': 3,
  '3': 4
}

const toFrontendOrderType = (type?: number | string): '0' | '1' | '2' | '3' => {
  const val = Number(type)
  return ORDER_TYPE_MAP[val] || '0'
}

const toBackendOrderType = (orderType?: string | number): number => {
  const str = String(orderType)
  return REVERSE_ORDER_TYPE_MAP[str as '0' | '1' | '2' | '3'] || 1
}

const mapFinance = (item: any): FinanceOrder => ({
  id: item.id,
  orderNo: item.orderNo || '',
  orderType: toFrontendOrderType(item.type),
  memberId: item.memberId,
  memberName: '',
  memberNo: '',
  amount: Number(item.amount || 0),
  paidAmount: item.payMethod ? Number(item.amount || 0) : 0,
  paymentMethod: toFrontendPaymentMethod(item.payMethod),
  paymentStatus: item.payMethod ? '1' : '0',
  remark: item.remark || '',
  createTime: item.createTime
})

/** 查询财务订单列表 */
export async function listFinanceOrder(query: FinanceOrderQuery) {
  const list = await request.get<any[]>('/finance-order/list')
  let rows = (list || []).map(mapFinance)

  if (query.orderNo) {
    rows = rows.filter((o) => o.orderNo.includes(query.orderNo!))
  }
  if (query.orderType) {
    rows = rows.filter((o) => o.orderType === query.orderType)
  }
  if (query.paymentStatus) {
    rows = rows.filter((o) => o.paymentStatus === query.paymentStatus)
  }

  const total = rows.length
  const pageNum = query.pageNum || 1
  const pageSize = query.pageSize || 10
  const start = (pageNum - 1) * pageSize
  rows = rows.slice(start, start + pageSize)
  return { rows, total }
}

/** 查询财务订单详情 */
export async function getFinanceOrder(id: number) {
  const data = await request.get<any>(`/finance-order/${id}`)
  return mapFinance(data)
}

/** 新增财务订单 */
export function addFinanceOrder(data: FinanceOrderForm) {
  return request.post('/finance-order', {
    orderNo: data.orderNo,
    memberId: data.memberId,
    amount: data.amount,
    type: toBackendOrderType(data.orderType),
    payMethod: data.paymentStatus === '1' ? toBackendPayMethod(data.paymentMethod) : undefined,
    remark: data.remark || ''
  })
}

/** 修改财务订单 */
export function updateFinanceOrder(data: FinanceOrderForm) {
  return addFinanceOrder(data)
}

/** 删除财务订单 */
export function delFinanceOrder(ids: number[]) {
  return Promise.all(ids.map((id) => request.delete(`/finance-order/${id}`)))
}

/** 支付订单 */
export function payOrder(id: number, paymentMethod: string, amount?: number) {
  return request.post('/finance-order/pay', null, {
    params: { id, payMethod: toBackendPayMethod(paymentMethod), amount }
  })
}

/** 退款订单 */
export function refundOrder(id: number, reason: string) {
  return request.post('/finance-order/refund', null, { params: { id, reason } })
}

/** 获取财务统计 */
export function getFinanceStats(params: { startDate?: string; endDate?: string }) {
  return request.get('/finance-order/stats', { params })
}

/** 生成订单号 */
export function generateOrderNo() {
  return request.get('/finance-order/generate-no')
}
