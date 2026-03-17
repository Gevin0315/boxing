import type { FormItemRule } from 'element-plus'

/** 手机号校验 */
export function validatePhone(rule: any, value: string, callback: any) {
  const reg = /^1[3-9]\d{9}$/
  if (!value) {
    callback()
  } else if (!reg.test(value)) {
    callback(new Error('请输入正确的手机号码'))
  } else {
    callback()
  }
}

/** 邮箱校验 */
export function validateEmail(rule: any, value: string, callback: any) {
  const reg = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
  if (!value) {
    callback()
  } else if (!reg.test(value)) {
    callback(new Error('请输入正确的邮箱地址'))
  } else {
    callback()
  }
}

/** 身份证号校验 */
export function validateIdCard(rule: any, value: string, callback: any) {
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  if (!value) {
    callback()
  } else if (!reg.test(value)) {
    callback(new Error('请输入正确的身份证号码'))
  } else {
    callback()
  }
}

/** 用户名校验 */
export function validateUsername(rule: any, value: string, callback: any) {
  const reg = /^[a-zA-Z0-9_]{4,20}$/
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (!reg.test(value)) {
    callback(new Error('用户名必须是4-20位的字母、数字或下划线'))
  } else {
    callback()
  }
}

/** 密码校验 */
export function validatePassword(rule: any, value: string, callback: any) {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else {
    callback()
  }
}

/** 必填校验 */
export function required(message = '该项不能为空'): FormItemRule {
  return { required: true, message, trigger: 'blur' }
}

/** 手机号规则 */
export const phoneRule = {
  validator: validatePhone,
  trigger: 'blur'
}

/** 邮箱规则 */
export const emailRule = {
  validator: validateEmail,
  trigger: 'blur'
}

/** 身份证规则 */
export const idCardRule = {
  validator: validateIdCard,
  trigger: 'blur'
}

/** 用户名规则 */
export const usernameRule = {
  validator: validateUsername,
  trigger: 'blur'
}

/** 密码规则 */
export const passwordRule = {
  validator: validatePassword,
  trigger: 'blur'
}

/** 数字规则 */
export const numberRule = {
  type: 'number' as const,
  message: '请输入数字',
  trigger: 'blur'
}
