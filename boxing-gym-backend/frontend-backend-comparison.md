# 前后端数据字段对比测试报告

## 报告信息

- **生成日期**: 2026-03-05
- **测试范围**: 拳馆管理系统前端与后端接口数据字段对比
- **前端路径**: `/Users/gevinlee/code/boxing-gym/boxing-gym-fronted`
- **后端路径**: `/Users/gevinlee/code/boxing-gym/src`

---

## 一、课程管理模块

### 1.1 课程类型对比

| 字段 | 前端类型 | 前端字段名 | 后端类型 | 后端字段名 | 状态 |
|------|----------|------------|----------|------------|------|
| 主键ID | number | id | Long | id | 匹配 |
| 课程名称 | string | courseName | String | name | **字段名不匹配** |
| 课程代码 | - | - | String | code | 后端有，前端无 |
| 课程类型 | string | courseType | Integer | type | **类型不匹配** |
| 课程分类 | string | category | - | - | **后端缺失** |
| 课程难度 | string | level | - | - | **后端缺失** |
| 课程描述 | string | description | String | description | 匹配 |
| 封面图片 | string | imageUrl | String | coverImg | **字段名不匹配** |
| 课程时长 | number | duration | Integer | duration | 匹配 |
| 最大人数 | number | maxCapacity | - | - | **后端缺失** |
| 课程价格 | number | price | - | - | **后端缺失** |
| 教练ID | number | coachId | - | - | **后端缺失** |
| 教练名称 | string | coachName | - | - | **后端缺失** |
| 状态 | string | status | Integer | status | **类型不匹配** |
| 排序 | - | - | Integer | sortOrder | 后端有，前端无 |
| 创建时间 | string | createTime | LocalDateTime | createTime | 匹配 |
| 更新时间 | string | updateTime | LocalDateTime | updateTime | 匹配 |

#### 课程分类与课程难度 - 严重问题
- **前端期望**: `category` (课程分类) 和 `level` (课程难度)
- **后端提供**: 无对应字段
- **影响**: 课程列表无法按分类和难度筛选、显示

#### 前端课程分类字典
```typescript
// boxing-gym-fronted/src/constants/dict.ts
export const COURSE_CATEGORY = [
  { value: 'boxing', label: '拳击基础' },
  { value: 'muay_thai', label: '泰拳' },
  { value: 'kickboxing', label: '自由搏击' },
  { value: 'fitness', label: '体能训练' },
  { value: 'self_defense', label: '防身术' },
  { value: 'kids', label: '青少年拳击' }
]
```

#### 前端课程难度字典
```typescript
// boxing-gym-fronted/src/constants/dict.ts
export const COURSE_LEVEL = [
  { value: 'beginner', label: '初级' },
  { value: 'intermediate', label: '中级' },
  { value: 'advanced', label: '高级' }
]
```

### 1.2 课程类型值映射对比

| 前端值 | 后端值 | 映射状态 |
|---------|--------|---------|
| 'group' (团课) | 1 | 映射正确 |
| 'private' (私教) | 2 | 映射正确 |

### 1.3 课程状态值映射对比

| 前端值 | 前端描述 | 后端值 | 后端描述 | 映射状态 |
|---------|----------|--------|----------|---------|
| '0' | 启用 | 1 | 启用 | **值不匹配** |
| '1' | 禁用 | 0 | 停用 | **值不匹配** |

**注意**: 前后端状态值语义相反

---

## 二、教练管理模块

### 2.1 教练信息对比

| 字段 | 前端类型 | 前端字段名 | 后端类型 | 后端字段名 | 来源表 | 状态 |
|------|----------|------------|----------|------------|--------|------|
| 主键ID | number | id | Long | id | sys_coach_profile | 匹配 |
| 教练号 | string | coachNo | - | - | - | **前端生成** |
| 用户ID | - | - | Long | userId | sys_coach_profile | **前端未使用** |
| 姓名 | string | name | String | realName | sys_user | **字段名不匹配** |
| 性别 | string | gender | - | - | - | **后端缺失** |
| 手机号 | string | phone | - | - | - | **后端缺失** |
| 邮箱 | string | email | - | - | - | **后端缺失** |
| 身份证 | string | idCard | - | - | - | **后端缺失** |
| 生日 | string | birthday | - | - | - | **后端缺失** |
| 地址 | string | address | - | - | - | **后端缺失** |
| 专长 | string | specialties | String | specialty | sys_coach_profile | **字段名不匹配** |
| 等级 | string | level | - | - | - | **后端缺失** |
| 状态 | string | status | Integer | status | sys_user | **类型不匹配** |
| 入职日期 | string | hireDate | - | - | - | **后端缺失** |
| 头像 | string | imageUrl | String | imgUrl | sys_coach_profile | **字段名不匹配** |
| 简介 | string | description | String | intro | sys_coach_profile | **字段名不匹配** |
| 用户名 | - | - | String | username | sys_user | 后端有，前端无 |
| 密码 | - | - | String | password | sys_user | 后端有，前端无 |
| 角色 | - | - | String | role | sys_user | 后端有，前端无 |

### 2.2 教练状态值对比

| 前端值 | 前端描述 | 后端值 | 后端描述 | 映射状态 |
|---------|----------|--------|----------|---------|
| '0' | 在职 | 1 | 启用 | **值不匹配** |
| '1' | 休假 | 0 | 禁用 | **值不匹配** |
| '2' | 离职 | - | - | 后端无此状态 |

### 2.3 教练等级对比

**前端期望**: `level` 字段，值为 '1'/'2'/'3'/'4' (初级/中级/高级/资深教练)
**后端提供**: 无 `level` 字段

---

## 三、会员管理模块

### 3.1 会员信息对比

| 字段 | 前端类型 | 前端字段名 | 后端类型 | 后端字段名 | 状态 |
|------|----------|------------|----------|------------|------|
| 主键ID | number | id | Long | id | 匹配 |
| 会员编号 | string | memberNo | String | memberNo | 匹配 |
| 姓名 | string | name | String | name | 匹配 |
| 性别 | string | gender | Integer | gender | **类型不匹配** |
| 手机号 | string | phone | String | phone | 匹配 |
| 生日 | string | birthday | - | - | **后端缺失** |
| 身份证 | string | idCard | - | - | **后端缺失** |
| 地址 | string | address | - | - | **后端缺失** |
| 紧急联系人 | string | emergencyContact | - | - | **后端缺失** |
| 紧急联系电话 | string | emergencyPhone | - | - | **后端缺失** |
| 会员等级 | string | membershipLevel | - | - | **后端缺失** |
| 余额 | number | remainingBalance | BigDecimal | balance | **字段名不匹配** |
| 剩余课时数 | - | - | Integer | remainingCourseCount | 后端有，前端无 |
| 会员卡有效期 | string | expiryDate | LocalDate | cardExpireDate | **字段名不匹配** |
| 头像 | - | - | String | avatarUrl | 后端有，前端未在表单中使用 |
| 状态 | string | status | Integer | status | **类型不匹配** |
| 备注 | string | remark | - | - | **后端缺失** |

### 3.2 会员状态值对比

| 前端值 | 前端描述 | 后端值 | 后端描述 | 映射状态 |
|---------|----------|--------|----------|---------|
| '0' | 正常 | 1 | 正常 | **值不匹配** |
| '1' | 暂停 | 0 | 禁用 | **值不匹配** |
| '2' | 注销 | - | - | 后端无此状态 |

### 3.3 会员等级对比

**前端期望**: `membershipLevel` 字段，值为 '1'/'2'/'3'/'4' (普通/银卡/金卡/钻石会员)
**后端提供**: 无 `membershipLevel` 字段

**前端会员等级字典**:
```typescript
export const MEMBERSHIP_LEVEL = [
  { value: '1', label: '普通会员', price: 299, duration: 1 },
  { value: '2', label: '银卡会员', price: 599, duration: 3 },
  { value: '3', label: '金卡会员', price: 999, duration: 6 },
  { value: '4', label: '钻石会员', price: 1999, duration: 12 }
]
```

---

## 四、排课管理模块

### 4.1 排课信息对比

| 字段 | 前端类型 | 前端字段名 | 后端类型 | 后端字段名 | 状态 |
|------|----------|------------|----------|------------|------|
| 主键ID | number | id | Long | id | 匹配 |
| 课程ID | number | courseId | Long | courseId | 匹配 |
| 课程名称 | string | courseName | - | - | **后端缺失** |
| 课程类型 | string | courseType | - | - | **后端缺失** |
| 教练ID | number | coachId | Long | coachId | 匹配 |
| 教练名称 | string | coachName | - | - | **后端缺失** |
| 排课日期 | string | scheduleDate | LocalDateTime | startTime | **字段结构不匹配** |
| 开始时间 | string | startTime | LocalDateTime | startTime | **字段结构不匹配** |
| 结束时间 | string | endTime | LocalDateTime | endTime | **字段结构不匹配** |
| 教室 | string | classroom | - | - | **后端缺失** |
| 最大人数 | number | maxCapacity | Integer | maxPeople | **字段名不匹配** |
| 已报名数 | number | currentCount | Integer | currentPeople | **字段名不匹配** |
| 剩余名额 | - | - | - | - | 前端期望，后端未提供 |
| 状态 | string | status | Integer | status | **类型不匹配** |
| 备注 | string | remark | - | - | **后端缺失** |

### 4.2 排课状态值对比

| 前端值 | 前端描述 | 后端值 | 后端描述 | 映射状态 |
|---------|----------|--------|----------|---------|
| '0' | 正常 | 0 | 未开始 | 匹配 |
| '1' | 已取消 | 3 | 已取消 | **值不匹配** |
| '2' | 已完成 | 2 | 已结束 | 描述略有差异 |

---

## 五、签到管理模块

### 5.1 签到记录对比

| 字段 | 前端类型 | 前端字段名 | 后端类型 | 后端字段名 | 状态 |
|------|----------|------------|----------|------------|------|
| 主键ID | number | id | Long | id | 匹配 |
| 记录编号 | - | - | String | recordNo | 后端有，前端无 |
| 会员ID | number | memberId | Long | memberId | 匹配 |
| 会员编号 | string | memberNo | - | - | **后端缺失** |
| 会员姓名 | string | memberName | - | - | **后端缺失** |
| 排课ID | number | scheduleId | Long | scheduleId | 匹配 |
| 排课日期 | string | scheduleDate | - | - | **后端缺失** |
| 课程ID | - | - | - | - | 前端期望，后端未提供 |
| 课程名称 | string | courseName | - | - | **后端缺失** |
| 教练ID | number | coachId | Long | coachId | 匹配 |
| 教练名称 | string | coachName | - | - | **后端缺失** |
| 教室 | string | classroom | - | - | **后端缺失** |
| 签到时间 | string | checkInTime | LocalDateTime | checkinTime | **字段名不匹配** |
| 签退时间 | string | checkOutTime | - | - | **后端缺失** |
| 状态 | string | status | Integer | status | **类型不匹配** |
| 备注 | string | remark | String | remark | 匹配 |

### 5.2 签到状态值对比

| 前端值 | 前端描述 | 后端值 | 后端描述 | 映射状态 |
|---------|----------|--------|----------|---------|
| '0' | 已签到 | 0 | 已预约 | 描述不匹配 |
| '1' | 缺勤 | 1 | 已签到 | **值/描述不匹配** |
| '2' | 请假 | 2 | 已取消 | 描述不匹配 |
| - | - | 3 | 旷课 | 前端无此状态 |

**注意**: 签到状态值映射存在严重混乱

---

## 六、财务订单模块

### 6.1 订单信息对比

| 字段 | 前端类型 | 前端字段名 | 后端类型 | 后端字段名 | 状态 |
|------|----------|------------|----------|------------|------|
| 主键ID | number | id | Long | id | 匹配 |
| 订单号 | string | orderNo | String | orderNo | 匹配 |
| 订单类型 | string | orderType | Integer | type | **字段名/值不匹配** |
| 会员ID | number | memberId | Long | memberId | 匹配 |
| 会员编号 | string | memberNo | - | - | **后端缺失** |
| 会员姓名 | string | memberName | - | - | **后端缺失** |
| 教练ID | number | coachId | - | - | **后端缺失** |
| 教练名称 | string | coachName | - | - | **后端缺失** |
| 课程ID | number | courseId | - | - | **后端缺失** |
| 课程名称 | string | courseName | - | - | **后端缺失** |
| 金额 | number | amount | BigDecimal | amount | 匹配 |
| 已付金额 | number | paidAmount | - | - | **后端缺失** |
| 支付方式 | string | paymentMethod | Integer | payMethod | **字段名/值不匹配** |
| 支付状态 | string | paymentStatus | - | - | **后端缺失** |
| 备注 | string | remark | String | remark | 匹配 |
| 创建时间 | string | createTime | LocalDateTime | createTime | 匹配 |
| 更新时间 | - | - | - | - | 后端无此字段 |
| 操作人ID | number | operatorId | Long | createBy | **字段名不匹配** |

### 6.2 订单类型值映射对比

| 前端值 | 前端描述 | 后端值 | 后端描述 | 映射状态 |
|---------|----------|--------|----------|---------|
| '0' | 会员卡 | 1 | 充值 | **值不匹配** |
| '1' | 私教课 | 3 | 课程消费 | **值不匹配** |
| '2' | 团课 | - | - | 后端无此类型 |
| '3' | 商品 | - | - | 后端无此类型 |
| - | - | 2 | 退款 | 前端无此类型 |

### 6.3 支付方式值映射对比

| 前端值 | 前端描述 | 后端值 | 后端描述 | 映射状态 |
|---------|----------|--------|----------|---------|
| '0' | 微信 | 1 | 微信 | **值不匹配** |
| '1' | 支付宝 | 2 | 支付宝 | **值不匹配** |
| '2' | 现金 | 3 | 现金 | **值不匹配** |
| '3' | 银行卡 | 4 | 刷卡 | **值不匹配** |
| - | - | 5 | 系统扣减 | 前端无此方式 |

### 6.4 支付状态对比

**前端期望**: `paymentStatus` 字段 ('0':未支付/'1':已支付/'2':部分支付)
**后端提供**: 无独立支付状态字段，通过 `payMethod` 是否为空判断

---

## 七、系统用户模块

### 7.1 用户信息对比

| 字段 | 前端类型 | 前端字段名 | 后端类型 | 后端字段名 | 状态 |
|------|----------|------------|----------|------------|------|
| 主键ID | number | id | Long | id | 匹配 |
| 用户名 | string | username | String | username | 匹配 |
| 昵称 | string | nickname | String | realName | **字段名不匹配** |
| 密码 | string | password | String | password | 匹配 |
| 手机号 | string | phone | - | - | **后端缺失** |
| 邮箱 | string | email | - | - | **后端缺失** |
| 角色 | string | role | String | role | 匹配 |
| 状态 | string | status | Integer | status | **类型不匹配** |
| 备注 | string | remark | - | - | **后端缺失** |

### 7.2 用户状态值对比

| 前端值 | 前端描述 | 后端值 | 后端描述 | 映射状态 |
|---------|----------|--------|----------|---------|
| '0' | 正常 | 1 | 启用 | **值不匹配** |
| '1' | 禁用 | 0 | 禁用 | **值不匹配** |
| '2' | 锁定 | - | - | 后端无此状态 |

### 7.3 用户角色对比

| 前端角色 | 后端角色 | 匹配状态 |
|---------|----------|---------|
| 系统管理员 | ROLE_ADMIN | 匹配 |
| 前台操作员 | ROLE_RECEPTION | 匹配 |
| - | ROLE_COACH | 后端有，前端无 |

---

## 八、缺失字段汇总

### 8.1 后端缺失字段（前端需要但后端未提供）

#### 课程模块
1. `category` - 课程分类
2. `level` - 课程难度
3. `maxCapacity` - 最大人数
4. `price` - 课程价格
5. `coachId` - 教练ID
6. `coachName` - 教练名称（关联字段）

#### 教练模块
1. `gender` - 性别
2. `phone` - 手机号
3. `email` - 邮箱
4. `idCard` - 身份证号
5. `birthday` - 生日
6. `address` - 地址
7. `level` - 教练等级
8. `hireDate` - 入职日期
9. `status` - 状态值不匹配（前端: 在职/休假/离职）

#### 会员模块
1. `birthday` - 生日
2. `idCard` - 身份证号
3. `address` - 地址
4. `emergencyContact` - 紧急联系人
5. `emergencyPhone` - 紧急联系电话
6. `membershipLevel` - 会员等级
7. `remark` - 备注
8. `status` - 状态值不匹配（前端: 正常/暂停/注销）

#### 排课模块
1. `courseName` - 课程名称（关联字段）
2. `courseType` - 课程类型（关联字段）
3. `coachName` - 教练名称（关联字段）
4. `classroom` - 教室
5. `remainingPeople` - 剩余名额
6. `remark` - 备注

#### 签到模块
1. `memberNo` - 会员编号（关联字段）
2. `memberName` - 会员姓名（关联字段）
3. `scheduleDate` - 排课日期（关联字段）
4. `courseId` - 课程ID（关联字段）
5. `courseName` - 课程名称（关联字段）
6. `coachName` - 教练名称（关联字段）
7. `classroom` - 教室（关联字段）
8. `checkOutTime` - 签退时间

#### 财务订单模块
1. `memberNo` - 会员编号（关联字段）
2. `memberName` - 会员姓名（关联字段）
3. `coachId` - 教练ID
4. `coachName` - 教练名称（关联字段）
5. `courseId` - 课程ID
6. `courseName` - 课程名称（关联字段）
7. `paidAmount` - 已付金额
8. `paymentStatus` - 支付状态

#### 系统用户模块
1. `phone` - 手机号
2. `email` - 邮箱
3. `remark` - 备注

### 8.2 前端缺失字段（后端提供但前端未使用）

#### 课程模块
1. `code` - 课程代码
2. `sortOrder` - 排序

#### 教练模块
1. `userId` - 关联用户ID
2. `username` - 用户名
3. `password` - 密码

#### 会员模块
1. `remainingCourseCount` - 剩余课时数
2. `avatarUrl` - 头像URL

#### 签到模块
1. `recordNo` - 记录编号

#### 财务订单模块
1. `updateTime` - 更新时间

---

## 九、数据类型不匹配问题

### 9.1 状态字段类型不匹配

| 模块 | 字段 | 前端类型 | 前端值 | 后端类型 | 后端值 | 问题 |
|------|------|----------|---------|----------|--------|------|
| 课程 | status | string | '0'启用/'1'禁用 | Integer | 1启用/0停用 | 类型和值都不匹配 |
| 教练 | status | string | '0'在职/'1'休假/'2'离职 | Integer | 1启用/0禁用 | 类型和值都不匹配 |
| 会员 | status | string | '0'正常/'1'暂停/'2'注销 | Integer | 1正常/0禁用 | 类型和值都不匹配 |
| 排课 | status | string | '0'正常/'1'取消/'2'完成 | Integer | 0未开始/1进行中/2已结束/3已取消 | 部分不匹配 |
| 签到 | status | string | '0'已签到/'1'缺勤/'2'请假 | Integer | 0已预约/1已签到/2已取消/3旷课 | 值描述混乱 |
| 用户 | status | string | '0'正常/'1'禁用/'2'锁定 | Integer | 1启用/0禁用 | 类型和值都不匹配 |

### 9.2 枚举值映射问题

#### 订单类型
```
前端: '0'(会员卡) '1'(私教课) '2'(团课) '3'(商品)
后端: 1(充值) 2(退款) 3(课程消费)
问题: 后端没有对应"团课"和"商品"的类型
```

#### 支付方式
```
前端: '0'(微信) '1'(支付宝) '2'(现金) '3'(银行卡)
后端: 1(微信) 2(支付宝) 3(现金) 4(刷卡) 5(系统扣减)
问题: 值不匹配
```

---

## 十、接口完整性评估

### 10.1 课程管理接口

| 接口 | 前端调用 | 后端提供 | 评估 |
|------|----------|----------|------|
| 列表查询 | 支持 | 支持 | 完整 |
| 详情查询 | 支持 | 支持 | 完整 |
| 新增 | 支持 | 支持 | 完整 |
| 修改 | 支持 | 支持 | 完整 |
| 删除 | 支持 | 支持 | 完整 |
| 修改状态 | 支持 | 支持 | 完整 |
| 获取选项 | 支持 | 支持 | 完整 |

**问题**: 前端期望按分类(category)和难度(level)筛选，但后端未支持

### 10.2 教练管理接口

| 接口 | 前端调用 | 后端提供 | 评估 |
|------|----------|----------|------|
| 列表查询 | 支持 | 支持 | 完整 |
| 详情查询 | 支持 | 支持 | 完整 |
| 新增 | 支持 | 支持 | 完整 |
| 修改 | 支持 | 支持 | 完整 |
| 删除 | 支持 | 支持 | 完整 |
| 修改状态 | 支持 | 支持 | 完整 |
| 获取选项 | 支持 | 支持 | 完整 |
| 生成教练号 | 支持 | 支持 | 完整 |

**问题**: 前端期望完整的教练个人信息（性别、手机号、邮箱等），后端仅在sys_user表中有部分字段

### 10.3 会员管理接口

| 接口 | 前端调用 | 后端提供 | 评估 |
|------|----------|----------|------|
| 分页查询 | 支持 | 支持 | 完整 |
| 详情查询 | 支持 | 支持 | 完整 |
| 新增 | 支持 | 支持 | 完整 |
| 修改 | 支持 | 支持 | 完整 |
| 删除 | 支持 | 支持 | 完整 |
| 充值 | 支持 | 支持 | 完整 |
| 扣费 | 支持 | 支持 | 完整 |
| 修改状态 | 支持 | 支持 | 完整 |
| 生成会员号 | 支持 | 支持 | 完整 |

**问题**: 前端期望的会员等级、生日、身份证等字段后端未提供

### 10.4 排课管理接口

| 接口 | 前端调用 | 后端提供 | 评估 |
|------|----------|----------|------|
| 列表查询 | 支持 | 支持 | 完整 |
| 详情查询 | 支持 | 支持 | 完整 |
| 新增 | 支持 | 支持 | 完整 |
| 修改 | 支持 | 支持 | 完整 |
| 删除 | 支持 | 支持 | 完整 |
| 取消 | 支持 | 支持 | 完整 |
| 完成 | 支持 | 支持 | 完整 |
| 日历查询 | 支持 | 支持 | 完整 |
| 根据教练查询 | 支持 | 支持 | 完整 |

**问题**: 前端期望教室、课程名称、教练名称等关联字段，后端未提供

### 10.5 签到管理接口

| 接口 | 前端调用 | 后端提供 | 评估 |
|------|----------|----------|------|
| 列表查询 | 支持 | 支持 | 完整 |
| 详情查询 | 支持 | 支持 | 完整 |
| 新增 | 支持 | 支持 | 完整 |
| 修改 | 支持 | 支持 | 完整 |
| 删除 | 支持 | 支持 | 完整 |
| 签到 | 支持 | 支持 | 完整 |
| 签退 | 支持 | 支持 | 完整 |
| 根据会员查询 | 支持 | 支持 | 完整 |
| 根据教练查询 | 支持 | 支持 | 完整 |
| 根据排课查询 | 支持 | 支持 | 完整 |

**问题**: 前端期望的会员信息、课程信息、签退时间等字段后端未提供

### 10.6 财务订单接口

| 接口 | 前端调用 | 后端提供 | 评估 |
|------|----------|----------|------|
| 列表查询 | 支持 | 支持 | 完整 |
| 详情查询 | 支持 | 支持 | 完整 |
| 新增 | 支持 | 支持 | 完整 |
| 删除 | 支持 | 支持 | 完整 |
| 支付 | 支持 | 支持 | 完整 |
| 退款 | 支持 | 支持 | 完整 |
| 财务统计 | 支持 | 支持 | 完整 |
| 生成订单号 | 支持 | 支持 | 完整 |

**问题**: 前端期望的订单类型（团课、商品）、已付金额、支付状态等后端未提供

### 10.7 系统用户接口

| 接口 | 前端调用 | 后端提供 | 评估 |
|------|----------|----------|------|
| 列表查询 | 支持 | 支持 | 完整 |
| 详情查询 | 支持 | 支持 | 完整 |
| 新增 | 支持 | 支持 | 完整 |
| 修改 | 支持 | 支持 | 完整 |
| 删除 | 支持 | 支持 | 完整 |
| 修改状态 | 支持 | 支持 | 完整 |
| 重置密码 | 支持 | 支持 | 完整 |

**问题**: 前端期望的手机号、邮箱等字段后端未提供

---

## 十一、补全建议

### 11.1 数据库层补全建议

#### 1. 课程表(course)需要新增字段
```sql
ALTER TABLE course ADD COLUMN category VARCHAR(50) COMMENT '课程分类';
ALTER TABLE course ADD COLUMN level VARCHAR(50) COMMENT '课程难度';
ALTER TABLE course ADD COLUMN max_capacity INT COMMENT '最大人数';
ALTER TABLE course ADD COLUMN price DECIMAL(10,2) COMMENT '课程价格';
ALTER TABLE course ADD COLUMN coach_id BIGINT COMMENT '教练ID';
```

#### 2. 教练表需要新增字段
```sql
-- 在sys_coach_profile表中新增
ALTER TABLE sys_coach_profile ADD COLUMN gender INT COMMENT '性别';
ALTER TABLE sys_coach_profile ADD COLUMN phone VARCHAR(20) COMMENT '手机号';
ALTER TABLE sys_coach_profile ADD COLUMN email VARCHAR(100) COMMENT '邮箱';
ALTER TABLE sys_coach_profile ADD COLUMN id_card VARCHAR(18) COMMENT '身份证号';
ALTER TABLE sys_coach_profile ADD COLUMN birthday DATE COMMENT '生日';
ALTER TABLE sys_coach_profile ADD COLUMN address VARCHAR(255) COMMENT '地址';
ALTER TABLE sys_coach_profile ADD COLUMN level INT COMMENT '等级';
ALTER TABLE sys_coach_profile ADD COLUMN hire_date DATE COMMENT '入职日期';

-- 或者将sys_user表扩充分为教练使用
```

#### 3. 会员表(member)需要新增字段
```sql
ALTER TABLE member ADD COLUMN birthday DATE COMMENT '生日';
ALTER TABLE member ADD COLUMN id_card VARCHAR(18) COMMENT '身份证号';
ALTER TABLE member ADD COLUMN address VARCHAR(255) COMMENT '地址';
ALTER TABLE member ADD COLUMN emergency_contact VARCHAR(50) COMMENT '紧急联系人';
ALTER TABLE member ADD COLUMN emergency_phone VARCHAR(20) COMMENT '紧急联系电话';
ALTER TABLE member ADD COLUMN membership_level VARCHAR(10) COMMENT '会员等级';
ALTER TABLE member ADD COLUMN remark VARCHAR(500) COMMENT '备注';
```

#### 4. 排课表(course_schedule)需要新增字段
```sql
ALTER TABLE course_schedule ADD COLUMN classroom VARCHAR(50) COMMENT '教室';
ALTER TABLE course_schedule ADD COLUMN remark VARCHAR(500) COMMENT '备注';
```

#### 5. 签到表(training_record)需要新增字段
```sql
ALTER TABLE training_record ADD COLUMN checkout_time DATETIME COMMENT '签退时间';
```

#### 6. 财务订单表(finance_order)需要新增字段
```sql
ALTER TABLE finance_order ADD COLUMN paid_amount DECIMAL(10,2) COMMENT '已付金额';
ALTER TABLE finance_order ADD COLUMN payment_status INT COMMENT '支付状态';
```

### 11.2 后端Entity补全建议

#### Course.java
```java
private String category;      // 课程分类
private String level;         // 课程难度
private Integer maxCapacity;  // 最大人数
private BigDecimal price;     // 课程价格
private Long coachId;        // 教练ID
```

#### SysCoachProfile.java
```java
private Integer gender;           // 性别
private String phone;             // 手机号
private String email;             // 邮箱
private String idCard;            // 身份证号
private LocalDate birthday;        // 生日
private String address;           // 地址
private Integer level;            // 等级
private LocalDate hireDate;        // 入职日期
```

#### Member.java
```java
private LocalDate birthday;            // 生日
private String idCard;                // 身份证号
private String address;               // 地址
private String emergencyContact;      // 紧急联系人
private String emergencyPhone;        // 紧急联系电话
private String membershipLevel;       // 会员等级
private String remark;                // 备注
```

#### CourseSchedule.java
```java
private String classroom;  // 教室
private String remark;      // 备注
```

#### TrainingRecord.java
```java
private LocalDateTime checkoutTime;  // 签退时间
```

#### FinanceOrder.java
```java
private BigDecimal paidAmount;     // 已付金额
private Integer paymentStatus;     // 支付状态
```

#### SysUser.java
```java
private String phone;   // 手机号
private String email;   // 邮箱
private String remark;   // 备注
```

### 11.3 关联字段补全建议

#### 建议创建VO对象返回关联数据
```java
// CourseVO.java
public class CourseVO extends Course {
    private String coachName;        // 教练名称
    private String categoryName;     // 分类名称
    private String levelName;        // 难度名称
}

// CourseScheduleVO.java
public class CourseScheduleVO extends CourseSchedule {
    private String courseName;       // 课程名称
    private String courseType;       // 课程类型
    private String coachName;        // 教练名称
    private Integer remainingPeople;  // 剩余名额
}

// TrainingRecordVO.java
public class TrainingRecordVO extends TrainingRecord {
    private String memberNo;         // 会员编号
    private String memberName;        // 会员姓名
    private String courseName;        // 课程名称
    private String coachName;         // 教练名称
    private String classroom;         // 教室
}

// FinanceOrderVO.java
public class FinanceOrderVO extends FinanceOrder {
    private String memberNo;         // 会员编号
    private String memberName;        // 会员姓名
    private String coachName;        // 教练名称
    private String courseName;        // 课程名称
}
```

### 11.4 状态值统一建议

#### 建议统一前后端状态值定义

| 模块 | 前端 | 后端 | 建议值 |
|------|------|------|--------|
| 课程状态 | '0'启用/'1'禁用 | 1启用/0停用 | 建议统一为 0启用/1禁用 |
| 教练状态 | '0'在职/'1'休假/'2'离职 | 1启用/0禁用 | 建议后端增加状态值 |
| 会员状态 | '0'正常/'1'暂停/'2'注销 | 1正常/0禁用 | 建议后端增加状态值 |
| 排课状态 | '0'正常/'1'取消/'2'完成 | 0未开始/1进行中/2已结束/3已取消 | 建议后端调整 |
| 签到状态 | '0'已签到/'1'缺勤/'2'请假 | 0已预约/1已签到/2已取消/3旷课 | 建议重新定义 |

### 11.5 枚举值映射建议

#### 订单类型
```
前端期望: '0'会员卡, '1'私教课, '2'团课, '3'商品
建议后端: 1会员卡, 2私教课, 3团课, 4商品, 5充值, 6退款
```

#### 支付方式
```
前端期望: '0'微信, '1'支付宝, '2'现金, '3'银行卡
建议后端: 1微信, 2支付宝, 3现金, 4银行卡, 5系统扣减
```

---

## 十二、优先级排序

### 高优先级（影响核心功能）

1. **课程分类和难度字段** - 影响课程展示和筛选
2. **教练个人信息字段** - 影响教练管理
3. **会员等级字段** - 影响会员权益管理
4. **签到状态值映射** - 影响签到记录展示

### 中优先级（影响功能完整性）

5. **排课教室字段** - 影响排课管理
6. **订单支付状态字段** - 影响财务统计
7. **会员额外信息字段** - 影响会员档案完整性

### 低优先级（优化体验）

8. **关联字段返回** - 优化列表展示，减少前端请求
9. **状态值统一** - 提高代码可维护性
10. **备注字段** - 增加数据备注功能

---

## 十三、总结

### 主要问题汇总

1. **课程分类和难度字段缺失** - 前端完整定义，后端无对应字段
2. **状态值不统一** - 前后端使用不同的值表示相同概念
3. **关联字段未返回** - 前端需要多次请求获取关联数据
4. **枚举值映射不一致** - 订单类型、支付方式等映射存在问题
5. **教练/会员个人信息不完整** - 基础信息字段缺失

### 推荐行动计划

1. **第一阶段**: 补充课程表、教练表、会员表的缺失字段
2. **第二阶段**: 统一前后端状态值定义
3. **第三阶段**: 创建VO对象，优化关联数据返回
4. **第四阶段**: 完善枚举值映射关系

---

**报告生成时间**: 2026-03-05
**报告生成人**: API Designer Agent
