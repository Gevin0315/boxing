/** 枚举常量 */

/** 用户状态枚举 */
export enum UserStatus {
  NORMAL = '0',
  DISABLED = '1',
  LOCKED = '2'
}

/** 性别枚举 */
export enum Gender {
  MALE = '0',
  FEMALE = '1'
}

/** 会员状态枚举 */
export enum MemberStatus {
  NORMAL = '0',
  SUSPENDED = '1',
  CANCELLED = '2'
}

/** 会员等级枚举 */
export enum MembershipLevel {
  NORMAL = '1',
  SILVER = '2',
  GOLD = '3',
  DIAMOND = '4'
}

/** 课程类型枚举 */
export enum CourseType {
  GROUP = 'group',
  PRIVATE = 'private'
}

/** 课程分类枚举 */
export enum CourseCategory {
  BOXING = 'boxing',
  MUAY_THAI = 'muay_thai',
  KICKBOXING = 'kickboxing',
  FITNESS = 'fitness',
  SELF_DEFENSE = 'self_defense',
  KIDS = 'kids'
}

/** 课程难度枚举 */
export enum CourseLevel {
  BEGINNER = 'beginner',
  INTERMEDIATE = 'intermediate',
  ADVANCED = 'advanced'
}

/** 教练状态枚举 */
export enum CoachStatus {
  ACTIVE = '0',
  ON_LEAVE = '1',
  RESIGNED = '2'
}

/** 订单类型枚举 */
export enum OrderType {
  MEMBERSHIP = '0',
  PRIVATE_COURSE = '1',
  GROUP_COURSE = '2',
  PRODUCT = '3'
}

/** 支付方式枚举 */
export enum PaymentMethod {
  WECHAT = '0',
  ALIPAY = '1',
  CASH = '2',
  BANK_CARD = '3'
}

/** 支付状态枚举 */
export enum PaymentStatus {
  UNPAID = '0',
  PAID = '1',
  PARTIAL = '2'
}

/** 签到状态枚举 */
export enum CheckinStatus {
  CHECKED = '0',
  ABSENT = '1',
  LEAVE = '2'
}

/** 路由名称枚举 */
export enum RouteName {
  DASHBOARD = 'Dashboard',
  SYS_USER = 'SysUser',
  MEMBER = 'Member',
  COURSE = 'Course',
  SCHEDULE = 'Schedule',
  COACH = 'Coach',
  TRAINING = 'Training',
  FINANCE = 'Finance',
  LOGIN = 'Login'
}
