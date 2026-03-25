/**
 * UI 相关常量
 */

/** 弹窗宽度配置 */
export const DIALOG_WIDTH = {
  SMALL: '400px',
  MEDIUM: '500px',
  LARGE: '600px',
  XLARGE: '700px',
} as const

/** 延迟时间配置（毫秒） */
export const DELAY = {
  DIALOG_CLOSE: 100,
  DEBOUNCE: 300,
} as const

/** 支付方式 */
export const PAYMENT_METHOD = {
  WECHAT: 1,
  ALIPAY: 2,
  CASH: 3,
  BANK_CARD: 4,
} as const
