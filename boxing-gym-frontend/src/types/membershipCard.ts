/**
 * 会员卡定义相关类型
 */

/** 卡分类枚举 */
export enum CardCategory {
  /** 团课期限卡 */
  GROUP_TIME = 1,
  /** 团课次卡 */
  GROUP_SESSION = 2,
  /** 私教次卡 */
  PRIVATE_SESSION = 3,
}

/** 卡分类选项 */
export const CARD_CATEGORY_OPTIONS = [
  { label: '团课期限卡', value: CardCategory.GROUP_TIME },
  { label: '团课次卡', value: CardCategory.GROUP_SESSION },
  { label: '私教次卡', value: CardCategory.PRIVATE_SESSION },
]

/** 卡分类映射 */
export const CARD_CATEGORY_MAP: Record<number, string> = {
  [CardCategory.GROUP_TIME]: '团课期限卡',
  [CardCategory.GROUP_SESSION]: '团课次卡',
  [CardCategory.PRIVATE_SESSION]: '私教次卡',
}

/** 卡类型 */
export type CardType =
  | 'MONTHLY'
  | 'QUARTERLY'
  | 'YEARLY'
  | 'SESSION_GROUP_1'
  | 'SESSION_GROUP_10'
  | 'SESSION_GROUP_20'
  | 'SESSION_PRIVATE_5'
  | 'SESSION_PRIVATE_10'
  | 'SESSION_PRIVATE_20'

/** 卡类型选项 */
export const CARD_TYPE_OPTIONS: { label: string; value: CardType; category: CardCategory }[] = [
  // 团课期限卡
  { label: '月卡', value: 'MONTHLY', category: CardCategory.GROUP_TIME },
  { label: '季卡', value: 'QUARTERLY', category: CardCategory.GROUP_TIME },
  { label: '年卡', value: 'YEARLY', category: CardCategory.GROUP_TIME },
  // 团课次卡
  { label: '单次日卡', value: 'SESSION_GROUP_1', category: CardCategory.GROUP_SESSION },
  { label: '10次团课卡', value: 'SESSION_GROUP_10', category: CardCategory.GROUP_SESSION },
  { label: '20次团课卡', value: 'SESSION_GROUP_20', category: CardCategory.GROUP_SESSION },
  // 私教次卡
  { label: '5次私教卡', value: 'SESSION_PRIVATE_5', category: CardCategory.PRIVATE_SESSION },
  { label: '10次私教卡', value: 'SESSION_PRIVATE_10', category: CardCategory.PRIVATE_SESSION },
  { label: '20次私教卡', value: 'SESSION_PRIVATE_20', category: CardCategory.PRIVATE_SESSION },
]

/** 卡类型映射 */
export const CARD_TYPE_MAP: Record<CardType, string> = {
  MONTHLY: '月卡',
  QUARTERLY: '季卡',
  YEARLY: '年卡',
  SESSION_GROUP_1: '单次日卡',
  SESSION_GROUP_10: '10次团课卡',
  SESSION_GROUP_20: '20次团课卡',
  SESSION_PRIVATE_5: '5次私教卡',
  SESSION_PRIVATE_10: '10次私教卡',
  SESSION_PRIVATE_20: '20次私教卡',
}

/** 卡片状态 */
export enum CardStatus {
  /** 停售 */
  DISABLED = 0,
  /** 在售 */
  ENABLED = 1,
}

/** 卡片状态选项 */
export const CARD_STATUS_OPTIONS = [
  { label: '停售', value: CardStatus.DISABLED },
  { label: '在售', value: CardStatus.ENABLED },
]

/** 卡片状态映射 */
export const CARD_STATUS_MAP: Record<number, string> = {
  [CardStatus.DISABLED]: '停售',
  [CardStatus.ENABLED]: '在售',
}

/** 会员卡定义接口 */
export interface MembershipCard {
  id: number
  cardName: string
  cardCategory: CardCategory
  cardCategoryDesc: string
  cardType: CardType
  cardTypeDesc: string
  durationDays?: number
  sessionCount?: number
  price: number
  originalPrice?: number
  activationDeadlineDays: number
  validityDaysAfterActivation?: number
  description?: string
  status: CardStatus
  statusDesc: string
  sortOrder: number
  createTime: string
  updateTime: string
}

/** 会员卡定义表单 */
export interface MembershipCardForm {
  id?: number
  cardName: string
  cardCategory: CardCategory
  cardType: CardType
  durationDays?: number
  sessionCount?: number
  price: number
  originalPrice?: number
  activationDeadlineDays: number
  validityDaysAfterActivation?: number
  description?: string
  status: CardStatus
  sortOrder: number
}

/** 会员卡定义查询参数 */
export interface MembershipCardQuery {
  current: number
  size: number
  cardCategory?: CardCategory
  status?: CardStatus
}
