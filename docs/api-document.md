# 拳馆管理系统 API 文档

## 项目概述

拳馆管理系统是一个基于 Spring Boot 3 + Vue 3 的前后端分离管理系统，提供会员管理、课程管理、教练管理、排课管理、财务管理等功能。

## 基础信息

- **后端服务地址**: `http://localhost:8080`
- **前端服务地址**: `http://localhost:3000`
- **API 基础路径**: `/api`
- **统一响应格式**:
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {}
  }
  ```

## 接口目录

### 1. 认证模块 `/auth`
### 2. 会员管理 `/member`
### 3. 教练管理 `/coach-profile`、`/sys-user`
### 4. 课程管理 `/course`
### 5. 排课管理 `/course-schedule`
### 6. 训练记录 `/training-record`
### 7. 财务管理 `/finance-order`
### 8. 会员卡定义 `/membership-card`
### 9. 会员持卡管理 `/api/member-card`

---

## 1. 认证模块 `/auth`

### 1.1 用户登录

**接口地址**: `POST /auth/login`

**请求参数**:
```typescript
{
  username: string    // 用户名
  password: string    // 密码
  code?: string      // 验证码（可选）
  uuid?: string      // 验证码UUID（可选）
}
```

**响应数据**:
```typescript
{
  token: string      // JWT令牌
  // 其他用户信息...
}
```

**接口说明**: 用户登录接口，登录成功后返回JWT令牌

---

### 1.2 用户登出

**接口地址**: `POST /auth/logout`

**请求参数**: 无

**响应数据**: 无

**接口说明**: 用户退出登录

---

### 1.3 获取用户信息

**接口地址**: `GET /auth/info`

**请求参数**: 无

**响应数据**:
```typescript
{
  id: number
  username: string
  nickname: string
  avatar?: string
  roles: string[]
  permissions: string[]
}
```

**接口说明**: 获取当前登录用户的详细信息

---

### 1.4 获取验证码

**接口地址**: `GET /auth/captcha`

**请求参数**: 无

**响应数据**:
```typescript
{
  uuid: string      // 验证码UUID
  img: string        // 验证码图片Base64
}
```

**接口说明**: 获取登录验证码

---

## 2. 会员管理 `/member`

### 2.1 查询会员列表

**接口地址**: `GET /member/list`

**请求参数**:
```typescript
{
  pageNum?: number      // 页码，默认1
  pageSize?: number     // 每页数量，默认10
  memberNo?: string     // 会员编号（模糊匹配）
  name?: string         // 姓名（模糊匹配）
  phone?: string        // 手机号（模糊匹配）
  status?: string       // 状态：0-正常，1-暂停，2-注销
  membershipLevel?: string // 会员等级
}
```

**响应数据**:
```typescript
{
  rows: Array<{
    id?: number
    memberNo: string         // 会员编号
    name: string             // 姓名
    gender: '0' | '1'        // 0-男，1-女
    phone: string            // 手机号
    birthday?: string        // 生日
    idCard?: string          // 身份证号
    address?: string         // 地址
    emergencyContact?: string // 紧急联系人
    emergencyPhone?: string   // 紧急联系人电话
    status: '0' | '1' | '2'  // 0-正常，1-暂停，2-注销
    membershipLevel: string  // 会员等级
    expiryDate?: string      // 到期日期
    remainingBalance?: number // 剩余余额
    remark?: string          // 备注
    createTime?: string      // 创建时间
    updateTime?: string      // 更新时间
  }>
  total: number             // 总记录数
}
```

**接口说明**: 分页查询会员列表，支持多条件查询

---

### 2.2 查询会员详情

**接口地址**: `GET /member/{id}`

**请求参数**:
```typescript
id: number  // 会员ID
```

**响应数据**: 同 2.1 中的 rows 项

**接口说明**: 根据会员ID获取会员详细信息

---

### 2.3 新增会员

**接口地址**: `POST /member`

**请求参数**:
```typescript
{
  memberNo?: string        // 会员编号（可选）
  name: string              // 姓名
  gender: '0' | '1'        // 性别：0-男，1-女
  phone: string             // 手机号
  birthday?: string         // 生日
  idCard?: string           // 身份证号
  address?: string          // 地址
  emergencyContact?: string  // 紧急联系人
  emergencyPhone?: string    // 紧急联系人电话
  status: '0' | '1' | '2'   // 状态：0-正常，1-暂停，2-注销
  membershipLevel: string   // 会员等级
  expiryDate?: string       // 到期日期
  remainingBalance?: number  // 剩余余额
  remark?: string           // 备注
}
```

**响应数据**: 成功返回

**接口说明**: 添加新会员

---

### 2.4 修改会员

**接口地址**: `PUT /member`

**请求参数**: 同 2.3

**响应数据**: 成功返回

**接口说明**: 修改会员信息

---

### 2.5 删除会员

**接口地址**: `DELETE /member/{id}`

**请求参数**:
```typescript
id: number  // 会员ID
```

**响应数据**: 成功返回

**接口说明**: 删除会员（单个）

---

### 2.6 批量删除会员

**接口地址**: `DELETE /member`（批量）

**请求参数**:
```typescript
ids: number[]  // 会员ID数组
```

**响应数据**: 成功返回

**接口说明**: 批量删除会员

---

### 2.7 会员充值

**接口地址**: `POST /member/recharge`

**请求参数**:
```typescript
{
  id: number         // 会员ID
  amount: number     // 充值金额
  payMethod: 3      // 支付方式：3-现金
}
```

**响应数据**: 成功返回

**接口说明**: 为会员账户充值

---

### 2.8 会员扣费

**接口地址**: `POST /member/deduct`

**请求参数**:
```typescript
{
  id: number   // 会员ID
  amount: number // 扣费金额
  remark: string // 备注（默认"前台扣费"）
}
```

**响应数据**: 成功返回

**接口说明**: 扣减会员余额

---

### 2.9 修改会员状态

**接口地址**: `PUT /member/status`

**请求参数**:
```typescript
{
  id: number   // 会员ID
  status: '0' | '1' | '2' // 新状态：0-正常，1-暂停，2-注销
}
```

**响应数据**: 成功返回

**接口说明**: 修改会员状态

---

## 3. 教练管理

### 3.1 教练档案管理 `/coach-profile`

#### 3.1.1 查询教练列表

**接口地址**: `GET /coach-profile/list` + `GET /sys-user/list`

**请求参数**:
```typescript
{
  pageNum?: number    // 页码，默认1
  pageSize?: number   // 每页数量，默认10
  name?: string       // 教练姓名（模糊匹配）
  status?: string      // 状态：0-在职，1-休假，2-离职
}
```

**响应数据**:
```typescript
{
  rows: Array<{
    id?: number
    coachNo: string         // 教练编号
    name: string            // 姓名
    gender: '0' | '1'      // 性别：0-男，1-女
    phone: string           // 手机号
    email?: string          // 邮箱
    specialties: string     // 专长
    level: '1' | '2' | '3' | '4' // 级别
    status: '0' | '1' | '2' // 0-在职，1-休假，2-离职
    imageUrl?: string       // 头像URL
    description?: string    // 简介
    birthday?: string        // 生日
    address?: string         // 地址
    hireDate?: string       // 入职日期
    createTime?: string      // 创建时间
    updateTime?: string      // 更新时间
  }>
  total: number             // 总记录数
}
```

**接口说明**: 分页查询教练列表，同时关联用户表获取状态信息

---

#### 3.1.2 查询教练详情

**接口地址**: `GET /coach-profile/{id}` + `GET /sys-user/{userId}`

**请求参数**:
```typescript
id: number  // 教练档案ID
```

**响应数据**: 同 3.1.1 中的 rows 项

**接口说明**: 根据教练档案ID获取教练详细信息

---

#### 3.1.3 新增教练

**接口地址**: `POST /sys-user` + `POST /coach-profile`

**请求参数**:
```typescript
{
  coachNo: string         // 教练编号（自动生成）
  name: string            // 姓名
  gender: '0' | '1'       // 性别：0-男，1-女
  phone: string           // 手机号
  email?: string          // 邮箱
  specialties: string      // 专长
  level: '1' | '2' | '3' | '4' // 级别
  status: '0' | '1' | '2' // 0-在职，1-休假，2-离职
  imageUrl?: string       // 头像URL
  description?: string     // 简介
  birthday?: string        // 生日
  address?: string         // 地址
  hireDate?: string       // 入职日期
  remark?: string         // 备注
}
```

**响应数据**: 成功返回

**接口说明**: 添加新教练，同时创建系统用户和教练档案

---

#### 3.1.4 修改教练

**接口地址**: `PUT /sys-user` + `PUT /coach-profile`

**请求参数**: 同 3.1.3

**响应数据**: 成功返回

**接口说明**: 修改教练信息，同时更新系统用户和教练档案

---

#### 3.1.5 删除教练

**接口地址**: `DELETE /coach-profile/{id}`

**请求参数**:
```typescript
id: number  // 教练档案ID
```

**响应数据**: 成功返回

**接口说明**: 删除教练档案

---

#### 3.1.6 修改教练状态

**接口地址**: `PUT /sys-user/status`

**请求参数**:
```typescript
{
  id: number      // 教练关联的用户ID
  status: string   // 状态：0-在职，1-休假，2-离职
}
```

**响应数据**: 成功返回

**接口说明**: 修改教练状态（通过关联的用户ID）

---

#### 3.1.7 获取教练下拉选项

**接口地址**: `GET /coach-profile/options`

**请求参数**: 无

**响应数据**:
```typescript
Array<{
  id: number
  name: string
  coachNo: string
  status: string
}>
```

**接口说明**: 获取教练下拉列表，用于选择

---

### 3.2 系统用户管理 `/sys-user`

#### 3.2.1 查询系统用户列表

**接口地址**: `GET /sys-user/list`

**请求参数**:
```typescript
{
  pageNum?: number  // 页码，默认1
  pageSize?: number // 每页数量，默认10
  username?: string  // 用户名（模糊匹配）
  nickname?: string  // 真实姓名（模糊匹配）
  role?: string      // 角色（模糊匹配）
  status?: string    // 状态：0-正常，1-禁用，2-锁定
}
```

**响应数据**:
```typescript
{
  rows: Array<{
    id: number
    username: string      // 用户名
    nickname: string      // 真实姓名
    status: '0' | '1' | '2' // 0-正常，1-禁用，2-锁定
    role: string          // 角色
    phone?: string        // 手机号
    email?: string        // 邮箱
    remark?: string       // 备注
    createTime?: string   // 创建时间
    updateTime?: string   // 更新时间
  }>
  total: number          // 总记录数
}
```

**接口说明**: 分页查询系统用户列表

---

#### 3.2.2 新增系统用户

**接口地址**: `POST /sys-user`

**请求参数**:
```typescript
{
  username: string      // 用户名
  password: string      // 密码
  realName: string      // 真实姓名
  role: string          // 角色
  status: '0' | '1' | '2' // 状态
  phone?: string        // 手机号
  email?: string        // 邮箱
  remark?: string       // 备注
}
```

**响应数据**: 成功返回

**接口说明**: 添加系统用户

---

#### 3.2.3 重置用户密码

**接口地址**: `PUT /sys-user/password`

**请求参数**:
```typescript
{
  id: number      // 用户ID
  password: string // 新密码
}
```

**响应数据**: 成功返回

**接口说明**: 重置用户密码

---

## 4. 课程管理 `/course`

### 4.1 查询课程列表

**接口地址**: `GET /course/list`

**请求参数**:
```typescript
{
  pageNum?: number     // 页码，默认1
  pageSize?: number    // 每页数量，默认10
  courseName?: string   // 课程名称（模糊匹配）
  courseType?: string  // 课程类型：group-团课，private-私教
  category?: string     // 课程分类
  level?: string        // 课程级别
  coachId?: number      // 教练ID
  status?: string       // 状态：0-启用，1-禁用
}
```

**响应数据**:
```typescript
{
  rows: Array<{
    id?: number
    courseName: string      // 课程名称
    courseType: string      // 课程类型：group-团课，private-私教
    category: string         // 课程分类
    level: string            // 课程级别
    maxCapacity: number      // 最大容量
    duration: number         // 时长（分钟）
    price: number            // 价格
    description?: string     // 课程描述
    coachId?: number         // 教练ID
    coachName?: string       // 教练姓名
    imageUrl?: string        // 课程图片URL
    status: '0' | '1'       // 0-启用，1-禁用
    createTime?: string      // 创建时间
    updateTime?: string      // 更新时间
  }>
  total: number             // 总记录数
}
```

**接口说明**: 分页查询课程列表，支持多条件查询

---

### 4.2 查询课程详情

**接口地址**: `GET /course/{id}`

**请求参数**:
```typescript
id: number  // 课程ID
```

**响应数据**: 同 4.1 中的 rows 项

**接口说明**: 根据课程ID获取课程详细信息

---

### 4.3 新增课程

**接口地址**: `POST /course`

**请求参数**:
```typescript
{
  courseName: string      // 课程名称
  courseType: string      // 课程类型：group-团课，private-私教
  category: string        // 课程分类
  level: string           // 课程级别
  maxCapacity: number     // 最大容量
  duration: number        // 时长（分钟）
  price: number           // 价格
  description?: string    // 课程描述
  coachId?: number        // 教练ID
  imageUrl?: string       // 课程图片URL
  status: '0' | '1'      // 0-启用，1-禁用
}
```

**响应数据**: 成功返回

**接口说明**: 添加新课程

---

### 4.4 修改课程

**接口地址**: `PUT /course`

**请求参数**: 同 4.3

**响应数据**: 成功返回

**接口说明**: 修改课程信息

---

### 4.5 删除课程

**接口地址**: `DELETE /course/{id}`

**请求参数**:
```typescript
id: number  // 课程ID
```

**响应数据**: 成功返回

**接口说明**: 删除课程（单个）

---

### 4.6 批量删除课程

**接口地址**: `DELETE /course`（批量）

**请求参数**:
```typescript
ids: number[]  // 课程ID数组
```

**响应数据**: 成功返回

**接口说明**: 批量删除课程

---

### 4.7 修改课程状态

**接口地址**: `PUT /course/status`

**请求参数**:
```typescript
{
  id: number   // 课程ID
  status: '0' | '1' // 新状态：0-启用，1-禁用
}
```

**响应数据**: 成功返回

**接口说明**: 修改课程状态

---

### 4.8 获取课程下拉选项

**接口地址**: `GET /course/options`

**请求参数**: 无

**响应数据**:
```typescript
Array<{
  id: number
  courseName: string
  courseType: string
  status: string
}>
```

**接口说明**: 获取课程下拉列表，用于选择

---

## 5. 排课管理 `/course-schedule`

### 5.1 查询排课列表

**接口地址**: `GET /course-schedule/list`

**请求参数**:
```typescript
{
  pageNum?: number      // 页码，默认1
  pageSize?: number     // 每页数量，默认10
  courseId?: number      // 课程ID
  coachId?: number       // 教练ID
  status?: string        // 状态：0-正常，1-已取消，2-已完成
  startDate?: string     // 开始日期（过滤）
  endDate?: string       // 结束日期（过滤）
}
```

**响应数据**:
```typescript
{
  rows: Array<{
    id?: number
    courseId: number         // 课程ID
    courseName?: string       // 课程名称
    courseType?: string       // 课程类型
    coachId: number           // 教练ID
    coachName?: string        // 教练姓名
    scheduleDate: string      // 排课日期（YYYY-MM-DD）
    startTime: string         // 开始时间（HH:mm）
    endTime: string           // 结束时间（HH:mm）
    classroom: string         // 教室
    maxCapacity: number       // 最大容量
    currentCount?: number     // 当前报名人数
    status: '0' | '1' | '2'   // 0-正常，1-已取消，2-已完成
    remark?: string            // 备注
    createTime?: string       // 创建时间
  }>
  total: number              // 总记录数
}
```

**接口说明**: 分页查询课程排课列表

---

### 5.2 查询排课详情

**接口地址**: `GET /course-schedule/{id}`

**请求参数**:
```typescript
id: number  // 排课ID
```

**响应数据**: 同 5.1 中的 rows 项

**接口说明**: 根据排课ID获取排课详细信息

---

### 5.3 新增排课

**接口地址**: `POST /course-schedule`

**请求参数**:
```typescript
{
  courseId: number       // 课程ID
  coachId: number         // 教练ID
  scheduleDate: string    // 排课日期（YYYY-MM-DD）
  startTime: string       // 开始时间（HH:mm）
  endTime: string         // 结束时间（HH:mm）
  classroom: string       // 教室
  maxCapacity: number      // 最大容量
  status: '0' | '1' | '2' // 0-正常，1-已取消，2-已完成
  remark?: string         // 备注
}
```

**响应数据**: 成功返回

**接口说明**: 创建课程排课

---

### 5.4 修改排课

**接口地址**: `PUT /course-schedule`

**请求参数**: 同 5.3

**响应数据**: 成功返回

**接口说明**: 修改排课信息

---

### 5.5 删除排课

**接口地址**: `DELETE /course-schedule/{id}`

**请求参数**:
```typescript
id: number  // 排课ID
```

**响应数据**: 成功返回

**接口说明**: 删除排课（单个）

---

### 5.6 批量删除排课

**接口地址**: `DELETE /course-schedule`（批量）

**请求参数**:
```typescript
ids: number[]  // 排课ID数组
```

**响应数据**: 成功返回

**接口说明**: 批量删除排课

---

### 5.7 取消排课

**接口地址**: `PUT /course-schedule/cancel`

**请求参数**:
```typescript
{
  id: number   // 排课ID
  reason: string // 取消原因
}
```

**响应数据**: 成功返回

**接口说明**: 取消排课，并记录取消原因

---

### 5.8 完成排课

**接口地址**: `PUT /course-schedule/complete`

**请求参数**:
```typescript
{
  id: number  // 排课ID
}
```

**响应数据**: 成功返回

**接口说明**: 标记排课为已完成

---

### 5.9 获取排课日历数据

**接口地址**: `GET /course-schedule/calendar`

**请求参数**:
```typescript
{
  startDate: string  // 开始日期（YYYY-MM-DD）
  endDate: string    // 结束日期（YYYY-MM-DD）
}
```

**响应数据**:
```typescript
Array<{
  id: number
  courseId: number
  scheduleDate: string
  startTime: string
  endTime: string
  courseName: string
  coachName: string
  classroom: string
  status: string
}>
```

**接口说明**: 获取指定日期范围内的排课数据，用于日历展示

---

## 6. 训练记录 `/training-record`

### 6.1 查询签到记录列表

**接口地址**: `GET /training-record/list`

**请求参数**:
```typescript
{
  pageNum?: number   // 页码，默认1
  pageSize?: number  // 每页数量，默认10
  memberId?: number  // 会员ID
  scheduleId?: number // 排课ID
  status?: string    // 签到状态：0-已签到，1-缺勤，2-请假
}
```

**响应数据**:
```typescript
{
  rows: Array<{
    id?: number
    scheduleId: number       // 排课ID
    scheduleDate?: string    // 排课日期
    courseId?: number        // 课程ID
    courseName?: string      // 课程名称
    coachId?: number         // 教练ID
    coachName?: string       // 教练姓名
    classroom?: string       // 教室
    memberId: number         // 会员ID
    memberNo?: string        // 会员编号
    memberName?: string      // 会员姓名
    checkInTime?: string     // 签到时间
    checkOutTime?: string    // 签退时间
    status: '0' | '1' | '2'  // 0-已签到，1-缺勤，2-请假
    remark?: string          // 备注
    createTime?: string      // 创建时间
  }>
  total: number             // 总记录数
}
```

**接口说明**: 分页查询训练签到记录

---

### 6.2 查询签到记录详情

**接口地址**: `GET /training-record/{id}`

**请求参数**:
```typescript
id: number  // 训练记录ID
```

**响应数据**: 同 6.1 中的 rows 项

**接口说明**: 根据训练记录ID获取详细信息

---

### 6.3 新增签到记录

**接口地址**: `POST /training-record`

**请求参数**:
```typescript
{
  scheduleId: number       // 排课ID
  memberId: number         // 会员ID
  coachId?: number         // 教练ID（可选）
  checkInTime?: string     // 签到时间（可选）
  status: '0' | '1' | '2'  // 0-已签到，1-缺勤，2-请假
  remark?: string          // 备注（可选）
}
```

**响应数据**: 成功返回

**接口说明**: 创建新的签到记录

---

### 6.4 修改签到记录

**接口地址**: `PUT /training-record`

**请求参数**:
```typescript
{
  id?: number              // 训练记录ID（修改时需要）
  scheduleId: number       // 排课ID
  memberId: number         // 会员ID
  coachId?: number         // 教练ID（可选）
  checkInTime?: string     // 签到时间（可选）
  checkOutTime?: string    // 签退时间（可选）
  status: '0' | '1' | '2' // 0-已签到，1-缺勤，2-请假
  remark?: string          // 备注（可选）
}
```

**响应数据**: 成功返回

**接口说明**: 修改签到记录信息

---

### 6.5 删除签到记录

**接口地址**: `DELETE /training-record/{id}`

**请求参数**:
```typescript
id: number  // 训练记录ID
```

**响应数据**: 成功返回

**接口说明**: 删除签到记录（单个）

---

### 6.6 批量删除签到记录

**接口地址**: `DELETE /training-record`（批量）

**请求参数**:
```typescript
ids: number[]  // 训练记录ID数组
```

**响应数据**: 成功返回

**接口说明**: 批量删除签到记录

---

### 6.7 签到

**接口地址**: `POST /training-record/check-in`

**请求参数**:
```typescript
{
  scheduleId: number  // 排课ID
  memberId: number    // 会员ID
}
```

**响应数据**: 成功返回

**接口说明**: 会员签到，记录签到时间和状态

---

### 6.8 签退

**接口地址**: `PUT /training-record/check-out`

**请求参数**:
```typescript
{
  id: number  // 训练记录ID
}
```

**响应数据**: 成功返回

**接口说明**: 会员签退，记录签退时间

---

### 6.9 获取排课的签到记录

**接口地址**: `GET /training-record/schedule/{scheduleId}`

**请求参数**:
```typescript
scheduleId: number  // 排课ID
```

**响应数据**:
```typescript
Array<{
  id?: number
  memberId: number         // 会员ID
  memberNo?: string         // 会员编号
  memberName?: string       // 会员姓名
  status: '0' | '1' | '2'   // 签到状态
  checkInTime?: string      // 签到时间
  checkOutTime?: string     // 签退时间
  remark?: string           // 备注
}>
```

**接口说明**: 获取指定排课的所有签到记录

---

### 6.10 获取会员签到记录

**接口地址**: `GET /training-record/member/{memberId}`

**请求参数**:
```typescript
memberId: number  // 会员ID
{
  pageNum?: number  // 页码，可选
  pageSize?: number // 每页数量，可选
}
```

**响应数据**:
```typescript
Array<{
  id?: number
  scheduleId: number
  scheduleDate?: string
  courseId?: number
  courseName?: string
  coachId?: number
  coachName?: string
  classroom?: string
  status: '0' | '1' | '2'
  checkInTime?: string
  checkOutTime?: string
  remark?: string
  createTime?: string
}>
```

**接口说明**: 获取指定会员的所有签到记录

---

## 7. 财务管理 `/finance-order`

### 7.1 查询财务订单列表

**接口地址**: `GET /finance-order/list`

**请求参数**:
```typescript
{
  pageNum?: number     // 页码，默认1
  pageSize?: number    // 每页数量，默认10
  orderNo?: string     // 订单号（模糊匹配）
  orderType?: string   // 订单类型：0-会员卡，1-私教课，2-团课，3-商品
  memberNo?: string    // 会员编号（模糊匹配）
  memberName?: string  // 会员姓名（模糊匹配）
  paymentStatus?: string // 支付状态：0-未支付，1-已支付，2-已退款
}
```

**响应数据**:
```typescript
{
  rows: Array<{
    id?: number
    orderNo: string         // 订单号
    orderType: '0' | '1' | '2' | '3' // 0-会员卡，1-私教课，2-团课，3-商品
    memberId: number        // 会员ID
    memberNo?: string       // 会员编号
    memberName?: string     // 会员姓名
    courseName?: string     // 课程名称（订单类型为课程时）
    amount: number          // 订单金额
    paidAmount: number      // 实付金额
    paymentMethod: '0' | '1' | '2' | '3' // 0-微信，1-支付宝，2-现金，3-银行卡
    paymentStatus: '0' | '1' | '2' // 0-未支付，1-已支付，2-已退款
    remark?: string          // 备注
    createTime?: string     // 创建时间
  }>
  total: number            // 总记录数
}
```

**接口说明**: 分页查询财务订单列表

---

### 7.2 查询财务订单详情

**接口地址**: `GET /finance-order/{id}`

**请求参数**:
```typescript
id: number  // 订单ID
```

**响应数据**: 同 7.1 中的 rows 项

**接口说明**: 根据订单ID获取订单详细信息

---

### 7.3 新增财务订单

**接口地址**: `POST /finance-order`

**请求参数**:
```typescript
{
  orderNo: string              // 订单号
  memberId: number             // 会员ID
  amount: number               // 订单金额
  orderType: '0' | '1' | '2' | '3' // 0-会员卡，1-私教课，2-团课，3-商品
  paymentMethod?: '0' | '1' | '2' | '3' // 支付方式（支付时必需）
  paidAmount?: number           // 实付金额
  paymentStatus: '0' | '1' | '2' // 0-未支付，1-已支付，2-已退款
  remark?: string              // 备注（可选）
}
```

**响应数据**: 成功返回

**接口说明**: 创建新的财务订单

---

### 7.4 支付订单

**接口地址**: `POST /finance-order/pay`

**请求参数**:
```typescript
{
  id: number                  // 订单ID
  payMethod: '0' | '1' | '2' | '3' // 支付方式：0-微信，1-支付宝，2-现金，3-银行卡
  amount?: number              // 支付金额（可选）
}
```

**响应数据**: 成功返回

**接口说明**: 处理订单支付，更新支付状态和实付金额

---

### 7.5 退款订单

**接口地址**: `POST /finance-order/refund`

**请求参数**:
```typescript
{
  id: number     // 订单ID
  reason: string // 退款原因
}
```

**响应数据**: 成功返回

**接口说明**: 处理订单退款，更新支付状态为已退款

---

### 7.6 获取财务统计

**接口地址**: `GET /finance-order/stats`

**请求参数**:
```typescript
{
  startDate?: string  // 开始日期（可选）
  endDate?: string    // 结束日期（可选）
}
```

**响应数据**:
```typescript
{
  totalAmount: number      // 总金额
  paidAmount: number       // 已付金额
  refundAmount: number     // 退款金额
  orderCount: number       // 订单数量
}
```

**接口说明**: 获取财务统计数据，可选日期范围

---

### 7.7 生成订单号

**接口地址**: `GET /finance-order/generate-no`

**请求参数**: 无

**响应数据**:
```typescript
{
  orderNo: string  // 生成的订单号
}
```

**接口说明**: 生成新的订单号

---

## 8. 会员卡定义 `/membership-card`

### 8.1 获取在售卡片列表

**接口地址**: `GET /membership-card/available`

**请求参数**: 无

**响应数据**:
```typescript
Array<{
  id: number
  cardCode: string              // 卡片代码
  cardName: string              // 卡片名称
  cardCategory: number          // 卡分类：1-团课期限卡，2-团课次卡，3-私教次卡
  cardCategoryDesc: string      // 卡分类描述
  cardType: string              // 卡类型
  cardTypeDesc: string          // 卡类型描述
  durationDays?: number         // 有效期天数（期限卡）
  sessionCount?: number         // 课次数（次卡）
  price: number                 // 价格
  originalPrice?: number        // 原价（可选）
  activationDeadlineDays: number // 激活截止天数
  validityDaysAfterActivation?: number // 激活后有效天数（可选）
  description?: string          // 描述
  status: number                // 0-停售，1-在售
  statusDesc: string            // 状态描述
  sortOrder: number             // 排序序号
  createTime: string            // 创建时间
  updateTime: string            // 更新时间
}>
```

**接口说明**: 获取所有正在销售的会员卡定义

---

### 8.2 按分类获取卡片列表

**接口地址**: `GET /membership-card/category/{category}`

**请求参数**:
```typescript
category: number  // 卡分类：1-团课期限卡，2-团课次卡，3-私教次卡
```

**响应数据**: 同 8.1

**接口说明**: 根据卡片分类获取对应的会员卡定义

---

### 8.3 获取卡片详情

**接口地址**: `GET /membership-card/{id}`

**请求参数**:
```typescript
id: number  // 会员卡定义ID
```

**响应数据**: 同 8.1 的单个项

**接口说明**: 根据ID获取会员卡定义详情

---

### 8.4 分页查询卡片列表

**接口地址**: `GET /membership-card/page`

**请求参数**:
```typescript
{
  current: number      // 当前页码
  size: number         // 每页数量
  cardCategory?: number // 卡分类（可选）
  status?: number      // 状态：0-停售，1-在售（可选）
}
```

**响应数据**:
```typescript
{
  records: Array<会员卡定义对象>  // 卡片列表
  total: number                  // 总记录数
  size: number                  // 每页数量
  current: number               // 当前页码
  pages: number                 // 总页数
}
```

**接口说明**: 分页查询会员卡定义列表

---

### 8.5 创建卡片定义

**接口地址**: `POST /membership-card`

**请求参数**:
```typescript
{
  cardCode: string              // 卡片代码
  cardName: string              // 卡片名称
  cardCategory: number          // 卡分类
  cardType: string              // 卡类型
  durationDays?: number         // 有效期天数（期限卡）
  sessionCount?: number         // 课次数（次卡）
  price: number                 // 价格
  originalPrice?: number        // 原价（可选）
  activationDeadlineDays: number // 激活截止天数
  validityDaysAfterActivation?: number // 激活后有效天数（可选）
  description?: string          // 描述（可选）
  status: number                // 0-停售，1-在售
  sortOrder: number             // 排序序号
}
```

**响应数据**:
```typescript
{
  code: number
  msg: string
  data: number  // 创建的卡片ID
}
```

**接口说明**: 创建新的会员卡定义

---

### 8.6 更新卡片定义

**接口地址**: `PUT /membership-card/{id}`

**请求参数**:
```typescript
id: number // 卡片ID
// 请求参数同 8.5，但不包括 id
```

**响应数据**:
```typescript
{
  code: number
  msg: string
  data: null
}
```

**接口说明**: 更新会员卡定义信息

---

### 8.7 更新卡片状态

**接口地址**: `PUT /membership-card/{id}/status`

**请求参数**:
```typescript
{
  id: number      // 卡片ID
  status: number  // 0-停售，1-在售
}
```

**响应数据**:
```typescript
{
  code: number
  msg: string
  data: null
}
```

**接口说明**: 更新会员卡定义的销售状态

---

### 8.8 删除卡片定义

**接口地址**: `DELETE /membership-card/{id}`

**请求参数**:
```typescript
id: number  // 卡片ID
```

**响应数据**:
```typescript
{
  code: number
  msg: string
  data: null
}
```

**接口说明**: 删除会员卡定义

---

## 9. 会员持卡管理 `/api/member-card`

### 9.1 购买会员卡

**接口地址**: `POST /api/member-card/purchase`

**请求参数**:
```typescript
{
  memberId: number      // 会员ID
  cardId: number        // 会员卡定义ID
  payMethod: number     // 支付方式：1-微信，2-支付宝，3-现金，4-银行卡
  paidAmount?: number   // 实付金额（可选）
  remark?: string       // 备注（可选）
}
```

**响应数据**:
```typescript
{
  code: number
  msg: string
  data: number  // 会员持卡ID
}
```

**接口说明**: 会员购买会员卡，创建会员持卡记录

---

### 9.2 激活会员卡

**接口地址**: `POST /api/member-card/activate`

**请求参数**:
```typescript
{
  memberCardId: number  // 会员持卡ID
  remark?: string        // 备注（可选）
}
```

**响应数据**:
```typescript
{
  code: number
  msg: string
  data: null
}
```

**接口说明**: 激活会员卡，更新激活时间和状态

---

### 9.3 作废会员卡

**接口地址**: `POST /api/member-card/void`

**请求参数**:
```typescript
{
  memberCardId: number  // 会员持卡ID
  reason: string        // 作废原因
}
```

**响应数据**:
```typescript
{
  code: number
  msg: string
  data: null
}
```

**接口说明**: 作废会员卡，更新状态和作废原因

---

### 9.4 获取会员所有卡片

**接口地址**: `GET /api/member-card/member/{memberId}`

**请求参数**:
```typescript
memberId: number  // 会员ID
```

**响应数据**:
```typescript
Array<{
  id: number
  cardNo: string               // 持卡编号
  memberId: number             // 会员ID
  memberName: string            // 会员姓名
  cardId: number               // 卡片定义ID
  cardName: string              // 卡片名称
  cardCategory: number          // 卡分类：1-团课期限卡，2-团课次卡，3-私教次卡
  cardCategoryDesc: string      // 卡分类描述
  cardType: string              // 卡类型
  cardTypeDesc: string          // 卡类型描述
  status: number                // 0-未激活，1-生效中，2-已过期，3-已作废
  statusDesc: string            // 状态描述
  totalSessions?: number        // 总课次数（次卡）
  remainingSessions?: number    // 剩余课次数（次卡）
  purchaseTime: string          // 购买时间
  activationDeadline: string    // 激活截止时间
  activationTime?: string       // 激活时间（可选）
  startDate?: string            // 开始时间（可选）
  expireDate?: string           // 过期时间（可选）
  lastCheckinTime?: string      // 最后使用时间（可选）
  orderId?: number              // 关联订单ID（可选）
  remark?: string               // 备注（可选）
  createTime: string            // 创建时间
  canBeActivated: boolean       // 是否可激活
  canBeUsedForCheckin: boolean  // 是否可用于签到
  remainingDays?: number        // 剩余天数（可选）
}>
```

**接口说明**: 获取指定会员的所有持卡记录

---

### 9.5 获取会员可用的团课卡

**接口地址**: `GET /api/member-card/member/{memberId}/available-group`

**请求参数**:
```typescript
memberId: number  // 会员ID
```

**响应数据**: 同 9.4

**接口说明**: 获取会员可用的团课卡列表

---

### 9.6 获取会员可用的私教卡

**接口地址**: `GET /api/member-card/member/{memberId}/available-private`

**请求参数**:
```typescript
memberId: number  // 会员ID
```

**响应数据**: 同 9.4

**接口说明**: 获取会员可用的私教卡列表

---

### 9.7 获取卡片详情

**接口地址**: `GET /api/member-card/{id}`

**请求参数**:
```typescript
id: number  // 会员持卡ID
```

**响应数据**: 同 9.4 的单个项

**接口说明**: 获取会员持卡详情

---

### 9.8 获取卡片使用记录

**接口地址**: `GET /api/member-card/{memberCardId}/usage-records`

**请求参数**:
```typescript
memberCardId: number  // 会员持卡ID
```

**响应数据**:
```typescript
Array<{
  id: number
  recordNo: string            // 记录编号
  memberCardId: number        // 会员持卡ID
  cardNo?: string             // 卡片编号（可选）
  memberId: number            // 会员ID
  memberName?: string         // 会员姓名（可选）
  usageType: number           // 使用类型：1-激活，2-签到，3-过期，4-作废
  usageTypeDesc: string       // 使用类型描述
  scheduleId?: number          // 排课ID（签到时）
  courseName?: string         // 课程名称（签到时）
  trainingRecordId?: number   // 训练记录ID（签到时）
  sessionsBefore?: number     // 使用前课次数（次卡）
  sessionsAfter?: number      // 使用后课次数（次卡）
  remark?: string              // 备注（可选）
  createTime: string          // 创建时间
  createBy?: number           // 操作人ID（可选）
  operatorName?: string        // 操作人姓名（可选）
}>
```

**接口说明**: 获取指定会员持卡的所有使用记录

---

### 9.9 分页获取卡片使用记录

**接口地址**: `GET /api/member-card/usage-records/page`

**请求参数**:
```typescript
{
  current: number        // 当前页码
  size: number           // 每页数量
  memberCardId?: number  // 会员持卡ID（可选）
  memberId?: number     // 会员ID（可选）
}
```

**响应数据**:
```typescript
{
  records: Array<使用记录对象>  // 使用记录列表
  total: number                // 总记录数
  size: number                 // 每页数量
  current: number             // 当前页码
  pages: number                // 总页数
}
```

**接口说明**: 分页查询卡片使用记录

---

### 9.10 验证卡片是否可用于签到

**接口地址**: `GET /api/member-card/{memberCardId}/validate-checkin`

**请求参数**:
```typescript
{
  memberId: number      // 会员ID
  isPrivateClass: boolean // 是否是私教课程
}
```

**响应数据**:
```typescript
{
  code: number
  msg: string
  data: boolean  // 是否可用
}
```

**接口说明**: 验证会员卡是否可用于课程签到

---

## 数据字典

### 状态码说明

#### 会员状态
- `0`: 正常
- `1`: 暂停
- `2`: 注销

#### 教练状态
- `0`: 在职
- `1`: 休假
- `2`: 离职

#### 课程状态
- `0`: 启用
- `1`: 禁用

#### 排课状态
- `0`: 正常
- `1`: 已取消
- `2`: 已完成

#### 训练记录状态
- `0`: 已签到
- `1`: 缺勤
- `2`: 请假

#### 系统用户状态
- `0`: 正常
- `1`: 禁用
- `2`: 锁定

#### 支付状态
- `0`: 未支付
- `1`: 已支付
- `2`: 已退款

#### 支付方式
- `0`: 微信
- `1`: 支付宝
- `2`: 现金
- `3`: 银行卡

#### 订单类型
- `0`: 会员卡
- `1`: 私教课
- `2`: 团课
- `3`: 商品

#### 会员卡分类
- `1`: 团课期限卡
- `2`: 团课次卡
- `3`: 私教次卡

#### 会员卡状态
- `0`: 未激活
- `1`: 生效中
- `2`: 已过期
- `3`: 已作废

#### 使用类型
- `1`: 激活
- `2`: 签到
- `3`: 过期
- `4`: 作废

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200    | 操作成功 |
| 400    | 请求参数错误 |
| 401    | 未授权，需要登录 |
| 403    | 权限不足 |
| 404    | 资源不存在 |
| 500    | 服务器内部错误 |
| 1001   | 会员不存在 |
| 1002   | 教练不存在 |
| 1003   | 课程不存在 |
| 1004   | 排课不存在 |
| 2001   | 会员卡类型不支持 |
| 2002   | 会员卡已过期 |
| 2003   | 会员卡已作废 |
| 2004   | 会员卡未激活 |
| 2005   | 会员卡余额不足 |

## 版本信息

- **API版本**: v1.0
- **文档版本**: 1.0
- **最后更新**: 2026-03-19

---

*本文档基于拳馆管理系统的前端API接口实现生成，如有疑问请联系开发团队。*