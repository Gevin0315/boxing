/** 字典常量 */

/** 用户状态 */
export const USER_STATUS = [
  { value: 1, label: '正常' },
  { value: 0, label: '禁用' }
]

/** 系统用户角色 */
export const USER_ROLE = [
  { value: 'ROLE_ADMIN', label: '超级管理员' },
  { value: 'ROLE_RECEPTION', label: '前台' },
  { value: 'ROLE_COACH', label: '教练' }
]

/** 性别 */
export const GENDER = [
  { value: '0', label: '男' },
  { value: '1', label: '女' }
]

/** 会员状态 */
export const MEMBER_STATUS = [
  { value: '1', label: '正常' },
  { value: '0', label: '禁用' }
]

/** 会员等级 */
export const MEMBERSHIP_LEVEL = [
  { value: '1', label: '普通会员', price: 299, duration: 1 },
  { value: '2', label: '银卡会员', price: 599, duration: 3 },
  { value: '3', label: '金卡会员', price: 999, duration: 6 },
  { value: '4', label: '钻石会员', price: 1999, duration: 12 }
]

/** 课程类型 */
export const COURSE_TYPE = [
  { value: 'group', label: '团课' },
  { value: 'private', label: '私教课' }
]

/** 课程分类 */
export const COURSE_CATEGORY = [
  { value: 'boxing', label: '拳击基础' },
  { value: 'muay_thai', label: '泰拳' },
  { value: 'kickboxing', label: '自由搏击' },
  { value: 'fitness', label: '体能训练' },
  { value: 'self_defense', label: '防身术' },
  { value: 'kids', label: '青少年拳击' }
]

/** 课程难度 */
export const COURSE_LEVEL = [
  { value: 'beginner', label: '初级' },
  { value: 'intermediate', label: '中级' },
  { value: 'advanced', label: '高级' }
]

/** 课程状态 */
export const COURSE_STATUS = [
  { value: '0', label: '启用' },
  { value: '1', label: '禁用' }
]

/** 教练状态 */
export const COACH_STATUS = [
  { value: '0', label: '在职' },
  { value: '1', label: '休假' },
  { value: '2', label: '离职' }
]

/** 教练等级 */
export const COACH_LEVEL = [
  { value: '1', label: '初级教练' },
  { value: '2', label: '中级教练' },
  { value: '3', label: '高级教练' },
  { value: '4', label: '资深教练' }
]

/** 订单类型 */
export const ORDER_TYPE = [
  { value: '0', label: '会员卡' },
  { value: '1', label: '私教课' },
  { value: '2', label: '团课' },
  { value: '3', label: '商品' }
]

/** 支付方式 */
export const PAYMENT_METHOD = [
  { value: '0', label: '微信' },
  { value: '1', label: '支付宝' },
  { value: '2', label: '现金' },
  { value: '3', label: '银行卡' }
]

/** 支付状态 */
export const PAYMENT_STATUS = [
  { value: '0', label: '未支付' },
  { value: '1', label: '已支付' },
  { value: '2', label: '部分支付' }
]

/** 签到状态 */
export const CHECKIN_STATUS = [
  { value: '0', label: '已预约' },
  { value: '1', label: '已签到' },
  { value: '2', label: '已取消' },
  { value: '3', label: '旷课' }
]

/** 排课状态 */
export const SCHEDULE_STATUS = [
  { value: '0', label: '正常' },
  { value: '1', label: '已取消' },
  { value: '2', label: '已完成' }
]

/** 周期类型 */
export const REPEAT_TYPE = [
  { value: 'once', label: '单次' },
  { value: 'daily', label: '每天' },
  { value: 'weekly', label: '每周' },
  { value: 'monthly', label: '每月' }
]

/** 星期 */
export const WEEKDAYS = [
  { value: 1, label: '周一' },
  { value: 2, label: '周二' },
  { value: 3, label: '周三' },
  { value: 4, label: '周四' },
  { value: 5, label: '周五' },
  { value: 6, label: '周六' },
  { value: 7, label: '周日' }
]
