# 分页接口改造设计文档

## 概述

将5个管理模块从前端内存分页改为后端服务端分页，提升性能和用户体验。

## 改造范围

| 模块 | 当前接口 | 新增接口 | 新增VO | 新增QueryDTO |
|------|---------|---------|--------|--------------|
| 课程管理 | `/course/list` | `/course/page` | CourseVO | CourseQueryDTO |
| 排课管理 | `/course-schedule/list` | `/course-schedule/page` | CourseScheduleVO | CourseScheduleQueryDTO |
| 教练管理 | `/coach-profile/list` | `/coach-profile/page` | CoachVO | CoachQueryDTO |
| 签到管理 | `/training-record/list` | `/training-record/page` | TrainingRecordVO | TrainingRecordQueryDTO |
| 财务管理 | `/finance-order/list` | `/finance-order/page` | FinanceOrderVO | FinanceOrderQueryDTO |

## 筛选条件

保持前端现有筛选条件不变：

### 课程管理
- `name` - 课程名称（模糊匹配）
- `type` - 课程类型（1-团课, 2-私教）
- `category` - 课程分类
- `level` - 课程难度
- `coachId` - 教练ID
- `status` - 状态（0-停用, 1-启用）

### 排课管理
- `courseId` - 课程ID
- `coachId` - 教练ID
- `status` - 状态（0-正常, 1-已取消, 2-已完成）
- `startDate` - 开始日期
- `endDate` - 结束日期

### 教练管理
- `name` - 教练姓名（模糊匹配）
- `status` - 状态（通过关联用户表查询）

### 签到管理
- `memberId` - 会员ID
- `scheduleId` - 排课ID
- `status` - 状态（0-已预约, 1-已签到, 2-已取消, 3-旷课）

### 财务管理
- `orderNo` - 订单号（模糊匹配）
- `type` - 订单类型（1-充值, 2-退款, 3-课程消费）
- `memberNo` - 会员号（模糊匹配）
- `memberName` - 会员姓名（模糊匹配）
- `paymentStatus` - 支付状态（0-未支付, 1-已支付, 2-部分支付）

## 后端实现规范

### QueryDTO 基类

```java
@Data
public class PageQueryDTO {
    private Integer current = 1;
    private Integer size = 10;
}
```

### 分页接口返回格式

使用 MyBatis-Plus 的 `Page<VO>` 对象，包含：
- `records` - 数据列表
- `total` - 总记录数
- `current` - 当前页码
- `size` - 每页大小

### Controller 示例

```java
@GetMapping("/page")
public Result<Page<CourseVO>> page(CourseQueryDTO query) {
    return Result.success(courseService.page(query));
}
```

## VO 字段设计

### CourseVO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键ID |
| courseName | String | 课程名称 |
| courseType | Integer | 课程类型（1-团课, 2-私教） |
| category | String | 课程分类 |
| level | String | 课程难度 |
| duration | Integer | 时长（分钟） |
| maxCapacity | Integer | 最大人数 |
| price | BigDecimal | 价格 |
| coachId | Long | 教练ID |
| coachName | String | 教练姓名 |
| imageUrl | String | 封面图片 |
| status | Integer | 状态 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

### CourseScheduleVO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键ID |
| courseId | Long | 课程ID |
| courseName | String | 课程名称 |
| courseType | Integer | 课程类型 |
| coachId | Long | 教练ID |
| coachName | String | 教练姓名 |
| startTime | LocalDateTime | 开始时间 |
| endTime | LocalDateTime | 结束时间 |
| classroom | String | 教室 |
| maxCapacity | Integer | 最大人数 |
| currentCount | Integer | 已约人数 |
| status | Integer | 状态 |
| remark | String | 备注 |
| createTime | LocalDateTime | 创建时间 |

### CoachVO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键ID |
| coachNo | String | 教练编号 |
| name | String | 姓名 |
| gender | Integer | 性别 |
| phone | String | 手机号 |
| email | String | 邮箱 |
| specialties | String | 擅长流派 |
| level | Integer | 等级 |
| status | Integer | 状态 |
| imageUrl | String | 头像 |
| description | String | 简介 |
| hireDate | LocalDate | 入职日期 |
| createTime | LocalDateTime | 创建时间 |

### TrainingRecordVO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键ID |
| recordNo | String | 记录编号 |
| scheduleId | Long | 排课ID |
| courseId | Long | 课程ID |
| courseName | String | 课程名称 |
| coachId | Long | 教练ID |
| coachName | String | 教练姓名 |
| memberId | Long | 会员ID |
| memberNo | String | 会员号 |
| memberName | String | 会员姓名 |
| checkinTime | LocalDateTime | 签到时间 |
| checkoutTime | LocalDateTime | 签退时间 |
| status | Integer | 状态 |
| remark | String | 备注 |
| createTime | LocalDateTime | 创建时间 |

### FinanceOrderVO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键ID |
| orderNo | String | 订单号 |
| memberId | Long | 会员ID |
| memberNo | String | 会员号 |
| memberName | String | 会员姓名 |
| type | Integer | 订单类型 |
| amount | BigDecimal | 金额 |
| paidAmount | BigDecimal | 已付金额 |
| payMethod | Integer | 支付方式 |
| paymentStatus | Integer | 支付状态 |
| remark | String | 备注 |
| createTime | LocalDateTime | 创建时间 |

## 前端改造

### API 调用变更

每个模块的 API 文件新增 `pageXxx` 函数：

```typescript
// 示例：course.ts
export async function pageCourse(query: CourseQuery) {
  const params = new URLSearchParams()
  params.append('current', String(query.pageNum || 1))
  params.append('size', String(query.pageSize || 10))
  if (query.courseName) params.append('name', query.courseName)
  if (query.courseType) params.append('type', toBackendType(query.courseType))
  // ... 其他参数

  const data = await request.get<Page<CourseVO>>('/course/page', { params })
  return {
    rows: data.records.map(mapCourse),
    total: data.total
  }
}
```

### 前端页面适配

页面组件基本不变，只需将 `listXxx` 调用改为 `pageXxx`。

## 保留接口

所有模块保留现有的 `/list` 接口，用于：
- 下拉选项数据源
- 导出功能
- 其他需要全量数据的场景

## 实现顺序

1. 课程管理
2. 排课管理
3. 教练管理
4. 签到管理
5. 财务管理

每个模块的实现步骤：
1. 创建 QueryDTO
2. 创建 VO
3. Service 层添加 page 方法
4. Controller 层添加 page 接口
5. 前端 API 添加 pageXxx 函数
6. 前端页面调用新 API
