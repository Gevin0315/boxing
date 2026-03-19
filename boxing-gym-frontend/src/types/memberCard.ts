/**
 * 会员持卡相关类型
 */

/** 会员卡状态 */
export enum MemberCardStatus {
  /** 未激活 */
  INACTIVE = 0,
  /** 生效中 */
  ACTIVE = 1,
  /** 已过期 */
  EXPIRED = 2,
  /** 已作废 */
  VOIDED = 3,
}

/** 会员卡状态选项 */
export const MEMBER_CARD_STATUS_OPTIONS = [
  { label: '未激活', value: MemberCardStatus.INACTIVE },
  { label: '生效中', value: MemberCardStatus.ACTIVE },
  { label: '已过期', value: MemberCardStatus.EXPIRED },
  { label: '已作废', value: MemberCardStatus.VOIDED },
]

/** 会员卡状态映射 */
export const MEMBER_CARD_STATUS_MAP: Record<number, string> = {
  [MemberCardStatus.INACTIVE]: '未激活',
  [MemberCardStatus.ACTIVE]: '生效中',
  [MemberCardStatus.EXPIRED]: '已过期',
  [MemberCardStatus.VOIDED]: '已作废',
}

/** 会员卡状态标签类型 */
export const MEMBER_CARD_STATUS_TAG_TYPE: Record<number, string> = {
  [MemberCardStatus.INACTIVE]: 'warning',
  [MemberCardStatus.ACTIVE]: 'success',
  [MemberCardStatus.EXPIRED]: 'info',
  [MemberCardStatus.VOIDED]: 'danger',
}

/** 使用类型 */
export enum CardUsageType {
  /** 激活 */
  ACTIVATE = 1,
  /** 签到 */
  CHECKIN = 2,
  /** 过期 */
  EXPIRE = 3,
  /** 作废 */
  VOID = 4,
}

/** 使用类型映射 */
export const CARD_USAGE_TYPE_MAP: Record<number, string> = {
  [CardUsageType.ACTIVATE]: '激活',
  [CardUsageType.CHECKIN]: '签到',
  [CardUsageType.EXPIRE]: '过期',
  [CardUsageType.VOID]: '作废',
}

/** 卡分类（与后端保持一致） */
export enum CardCategory {
  /** 团课期限卡 */
  GROUP_TIME = 1,
  /** 团课次卡 */
  GROUP_SESSION = 2,
  /** 私教次卡 */
  PRIVATE_SESSION = 3,
}

/** 卡分类映射 */
export const CARD_CATEGORY_MAP: Record<number, string> = {
  [CardCategory.GROUP_TIME]: '团课期限卡',
  [CardCategory.GROUP_SESSION]: '团课次卡',
  [CardCategory.PRIVATE_SESSION]: '私教次卡',
}

/** 会员持卡记录 */
export interface MemberCard {
  id: number
  cardNo: string
  memberId: number
  memberName: string
  cardId: number
  cardName: string
  cardCategory: CardCategory
  cardCategoryDesc: string
  cardType: string
  cardTypeDesc: string
  status: MemberCardStatus
  statusDesc: string
  totalSessions?: number
  remainingSessions?: number
  purchaseTime: string
  activationDeadline: string
  activationTime?: string
  startDate?: string
  expireDate?: string
  lastCheckinTime?: string
  orderId?: number
  remark?: string
  createTime: string
  canBeActivated: boolean
  canBeUsedForCheckin: boolean
  remainingDays?: number
}

/** 购卡请求 */
export interface PurchaseCardDTO {
  memberId: number
  cardId: number
  payMethod: number
  paidAmount?: number
  remark?: string
}

/** 激活卡请求 */
export interface ActivateCardDTO {
  memberCardId: number
  remark?: string
}

/** 作废卡请求 */
export interface VoidCardDTO {
  memberCardId: number
  reason: string
}

/** 卡片使用记录 */
export interface CardUsageRecord {
  id: number
  recordNo: string
  memberCardId: number
  cardNo?: string
  memberId: number
  memberName?: string
  usageType: CardUsageType
  usageTypeDesc: string
  scheduleId?: number
  courseName?: string
  trainingRecordId?: number
  sessionsBefore?: number
  sessionsAfter?: number
  remark?: string
  createTime: string
  createBy?: number
  operatorName?: string
}


/** 使用记录查询参数 */
export interface CardUsageRecordQuery {
  current: number
  size: number
  memberCardId?: number
  memberId?: number
}
