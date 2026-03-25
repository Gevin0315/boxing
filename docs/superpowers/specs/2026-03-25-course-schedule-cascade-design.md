# 课程删除/禁用时级联处理排课设计文档

## 需求概述

当课程被删除或禁用时，需要自动处理关联的排课记录，避免产生孤立的排课数据。

## 业务规则

### 删除课程时
- 取消所有**未结束**的排课（status=0未开始, status=1进行中）
- **已结束**(status=2)和**已取消**(status=3)的排课不处理

### 禁用课程时
- 取消所有**未开始**的排课(status=0)
- 取消原因自动写入："课程已禁用"
- **进行中**(status=1)、**已结束**(status=2)、**已取消**(status=3)的排课不处理

## 技术设计

### 后端修改

#### 1. CourseScheduleService接口新增方法

```java
/**
 * 取消课程的所有未结束排课（未开始+进行中）
 * @param courseId 课程ID
 */
void cancelUnfinishedSchedules(Long courseId);

/**
 * 取消课程的所有未开始排课
 * @param courseId 课程ID
 * @param reason 取消原因
 */
void cancelNotStartedSchedules(Long courseId, String reason);
```

#### 2. CourseScheduleServiceImpl实现

**cancelUnfinishedSchedules**:
```java
@Override
public void cancelUnfinishedSchedules(Long courseId) {
    LambdaUpdateWrapper<CourseSchedule> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(CourseSchedule::getCourseId, courseId)
           .in(CourseSchedule::getStatus,
               ScheduleStatusEnum.NOT_STARTED.getCode(),
               ScheduleStatusEnum.IN_PROGRESS.getCode())
           .set(CourseSchedule::getStatus, ScheduleStatusEnum.CANCELLED.getCode());
    update(wrapper);
}
```

**cancelNotStartedSchedules**:
```java
@Override
public void cancelNotStartedSchedules(Long courseId, String reason) {
    LambdaUpdateWrapper<CourseSchedule> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(CourseSchedule::getCourseId, courseId)
           .eq(CourseSchedule::getStatus, ScheduleStatusEnum.NOT_STARTED.getCode())
           .set(CourseSchedule::getStatus, ScheduleStatusEnum.CANCELLED.getCode())
           .set(CourseSchedule::getRemark, reason);
    update(wrapper);
}
```

#### 3. CourseServiceImpl修改

**removeById**:
- 删除前调用 `courseScheduleService.cancelUnfinishedSchedules(courseId)`
- 添加 `@Transactional` 注解

**removeByIds**:
- 批量删除前循环调用取消逻辑

**updateStatus**:
- 当 status=0(禁用) 时，调用 `courseScheduleService.cancelNotStartedSchedules(courseId, "课程已禁用")`
- 添加 `@Transactional` 注解

### 事务控制

所有操作使用 `@Transactional` 保证原子性：
- 删除/禁用课程 + 取消排课 在同一事务中
- 任一失败则全部回滚

## 涉及文件

| 文件 | 修改类型 |
|------|----------|
| CourseScheduleService.java | 新增2个接口方法 |
| CourseScheduleServiceImpl.java | 实现2个批量取消方法 |
| CourseServiceImpl.java | 修改删除和状态更新方法，添加事务注解 |

## 状态说明

### 排课状态 (ScheduleStatusEnum)
- 0: 未开始 (NOT_STARTED)
- 1: 进行中 (IN_PROGRESS)
- 2: 已结束 (FINISHED)
- 3: 已取消 (CANCELLED)

### 课程状态
- 0: 禁用
- 1: 启用
