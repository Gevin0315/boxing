import { PageQuery, PageResult } from './common'

/** 财务订单信息 */
export interface FinanceOrder {
  id?: number
  orderNo: string
  orderType: '0' | '1' | '2' | '3' // 0-会员卡 1-私教课 2-团课 3-商品
  memberId?: number
  memberName?: string
  memberNo?: string
  courseId?: number
  courseName?: string
  amount: number
  paidAmount: number
  paymentMethod: '0' | '1' | '2' | '3' // 0-微信 1-支付宝 2-现金 3-银行卡
  paymentStatus: '0' | '1' | '2' // 0-未支付 1-已支付 2-部分支付
  remark?: string
  createTime?: string
  updateTime?: string
  operatorId?: number
}

/** 财务订单查询参数 */
export interface FinanceOrderQuery extends PageQuery {
  orderNo?: string
  orderType?: string
  memberNo?: string
  memberName?: string
  paymentStatus?: string
  startTime?: string
  endTime?: string
}

/** 财务订单表单 */
export interface FinanceOrderForm {
  id?: number
  orderNo?: string
  orderType: '0' | '1' | '2' | '3'
  memberId: number
  courseId?: number
  amount: number
  paidAmount: number
  paymentMethod: '0' | '1' | '2' | '3'
  paymentStatus: '0' | '1' | '2'
  remark?: string
}

/** 支付记录 */
export interface PaymentRecord {
  id: number
  orderId: number
  amount: number
  paymentMethod: string
  paymentTime: string
  operator: string
}
