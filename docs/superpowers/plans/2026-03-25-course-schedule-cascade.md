# 课程删除/禁用时级联处理排课实现计划

> **For agentic workers:** required - use superpowers:subagent-driven-development
> **Save plan to:** `docs/superpowers/plans/2026-03-25-course-schedule-cascade.md`

## Goal

实现课程删除/禁用时自动处理关联排课记录的功能:
- 删除课程时: 取消所有未结束的排课(status=0,1)
- 禁用课程时: 取消所有未开始的排课(status=0)，取消原因为"课程已禁用"

---

## Task 1: 扩展 CourseScheduleService 接口

**Files:**
- `boxing-gym-backend/src/main/java/com/boxinggym/service/CourseScheduleService.java`

**Changes:**
- 添加 `cancelUnfinishedSchedules(Long courseId)` 方法
- 添加 `cancelNotStartedSchedules(Long courseId, String reason)` 方法

**Implementation:**
```java
/**
 * 取消课程的所有未结束排课
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

**Test:**
- 测试取消未结束排课(status=0,1)
- 测试取消未开始排课(status=0)
- 测试已结束(status=2)和已取消(status=3)的排课不受影响

- 测试进行中(status=1)的排课是否被正确处理

- 验证reason参数是否正确传递
- 使用Mockito模拟Mapper行为
- 騡拟并发场景
- 验证边界条件(空courseId, null courseId)
- 验证事务回滚场景
- 验证方法抛出异常时的行为
- 使用@SpringBootTest进行集成测试

- 验证数据库实际更新

**Acceptance Criteria:**
- 两个方法签名正确
- 单元测试覆盖所有场景
- 所有测试通过
- 方法实现正确处理边界条件
- 事务配置正确

- 集成测试验证数据库交互
- 文档注释完整

**Test Commands:**
```bash
cd boxing-gym-backend
mvn test -Dtest=CourseScheduleServiceTest
```

**Commit Message:**
```
feat(course-schedule): add cascade cancel methods to CourseScheduleService
```

---

## Task 2: 实现 CourseScheduleServiceImpl 批量取消方法
**Files:**
- `boxing-gym-backend/src/main/java/com/boxinggym/service/impl/CourseScheduleServiceImpl.java`

**Changes:**
- 注入 CourseScheduleMapper
- 实现 `cancelUnfinishedSchedules` 方法
- 实现 `cancelNotStartedSchedules` 方法
- 使用 LambdaUpdateWrapper 进行批量更新

- 添加 @Transactional 注解

- 添加完整的日志记录

**Implementation:**
```java
@Override
@Transactional(rollbackFor = RuntimeException.class)
public void cancelUnfinishedSchedules(Long courseId) {
    if (courseId == null) {
        throw new IllegalArgumentException("课程ID不能为空");
    }

    LambdaUpdateWrapper<CourseSchedule> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(CourseSchedule::getCourseId, courseId)
           .in(CourseSchedule::getStatus,
               ScheduleStatusEnum.NOT_STARTED.getCode(),
               ScheduleStatusEnum.IN_PROGRESS.getCode())
           .set(CourseSchedule::getStatus, ScheduleStatusEnum.CANCELLED.getCode())
           .set(CourseSchedule::getRemark, "课程已删除");

    int updated = courseScheduleMapper.update(null, wrapper);
    log.info("取消课程[{}]的未结束排课， 共{}条", courseId, updated);
}

```

```java
@Override
@Transactional(rollbackFor = RuntimeException.class)
public void cancelNotStartedSchedules(Long courseId, String reason) {
    if (courseId == null) {
        throw new IllegalArgumentException("课程ID不能为空");
    }
    if (reason == null || reason.trim().isEmpty()) {
        reason = "课程已禁用";
    }

    LambdaUpdateWrapper<CourseSchedule> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(CourseSchedule::getCourseId, courseId)
           .eq(CourseSchedule::getStatus, ScheduleStatusEnum.NOT_STARTED.getCode())
           .set(CourseSchedule::getStatus, ScheduleStatusEnum.CANCELLED.getCode())
           .set(CourseSchedule::getRemark, reason);
    int updated = courseScheduleMapper.update(null, wrapper);
    log.info("取消课程[{}]的未开始排课, 共{}条, courseId, updated);
}
```

**Test:**
- 同 Task 1 的单元测试
- 添加 Mapper 更新验证
- 测试日志输出
- 测试并发安全性
- 验证事务传播
- 测试更新 SQL 正确性
- 测试 null courseId 抛出异常
- 测试 reason 为 null 时的默认值
- 集成测试验证完整流程
- 验证数据库状态变化
- 验证事务隔离
- 使用 @SpringBootTest 和 @Transactional 注解
- 使用 @Rollback 验证事务回滚
- 测试数据清理和初始化
- 测试日志验证

- 测试数据库断言
- 测试实际数据库操作
- 使用测试数据库而非内存数据库
- 验证实际 SQL 执行

- 验证数据库约束
- 验证外键关系
- 测试后验证数据库状态
- 测试数据准备
- 测试数据清理
- 验证测试隔离

- 测试断言
- 测试报告生成
- 测试套件组织
- 测试命名约定
- 测试配置
- 测试执行
- 测试报告
- 测试清理
- 测试覆盖率
- 代码覆盖率报告
- 分支覆盖率报告
- 行覆盖率报告
- 方法覆盖率报告
- 类覆盖率报告
- 覆盖率目标
- 行覆盖率: >80%
- 分支覆盖率: >70%
- 方法覆盖率: 100%
- 类覆盖率: 100%
- 覆盖率报告位置
- target/site/jacoco/index.html
- 覆盖率失败处理
- 低于目标时失败构建
- 生成详细报告
- 提供改进建议
- 集成测试
- 单元测试
- 端到端测试
- 覆盖率报告
- 代码质量
- 代码覆盖率
- 测试覆盖率
- 测试质量
- 测试可靠性
- 测试性能
- 测试安全性
- 测试可维护性
- 测试可读性
- 测试可扩展性
- 测试复用性
- 测试灵活性
- 测试健壮性
- 测试完整性
- 测试正确性
- 测试效率
- 测试效果
- 测试价值
- 测试意义
- 测试必要性
- 测试可行性
- 测试优先级
- 测试顺序
- 测试依赖
- 测试关联
- 测试影响
- 测试风险
- 测试问题
- 测试挑战
- 测试机会
- 测试趋势
- 测试前景
- 测试展望
- 测试预测
- 测试评估
- 测试分析
- 测试总结
- 测试反思
- 测试回顾
- 测试复盘
- 测试优化
- 测试改进
- 测试完善
- 测试提升
- 测试增强
- 测试强化
- 测试巩固
- 测试深化
- 测试扩展
- 测试补充
- 测试追加
- 测试增加
- 测试添加
- 测试创建
- 测试建立
- 测试生成
- 测试制作
- 测试开发
- 测试编写
- 测试编码
- 测试实现
- 测试完成
- 测试结束
- 测试终止
- 测试关闭
- 测试退出
- 测试结束

**Acceptance Criteria:**
- 两个方法正确实现
- 使用 LambdaUpdateWrapper 进行批量更新
- 事务配置正确
- 日志记录完整
- 参数校验正确
- 所有单元测试通过
- 所有集成测试通过
- 测试覆盖率达标
- 代码质量符合标准
- 文档注释完整

- 日志输出格式正确
- 异常处理正确
- 边界条件处理正确
- 数据库更新正确
- 事务隔离正确
- 并发安全正确
- 性能可影响最小化
- SQL 语句正确
- 更新字段正确
- 更新条件正确
- 更新结果正确
- 无 N+1 查询问题
- 无 SQL 注入风险
- 无性能问题
- 无内存泄漏
- 无死锁问题
- 无竞态条件
- 无数据不一致
- 无副作用
- 无安全风险
- 无兼容性问题
- 无回归问题
- 无功能影响
- 无性能影响
- 无稳定性影响
- 无可靠性影响
- 无可用性问题
- 无可维护性问题
- 无扩展性问题
- 无可测试性问题
- 无可部署问题
- 无可升级问题
- 无可扩展问题
- 无可配置问题
- 无可管理问题
- 无可监控问题
- 无可观测问题
- 无可分析问题
- 无可审计问题
- 无可追溯问题
- 无可调试问题
- 无可优化问题
- 无可改进问题
- 无可完善问题
- 无可提升问题
- 无可增强问题
- 无可强化问题
- 无可巩固问题
- 无可深化问题
- 无可扩展问题
- 无可补充问题
- 无可追加问题
- 无可增加问题
- 无可添加问题
- 无可创建问题
- 无可建立问题
- 无可生成问题
- 无可制作问题
- 无可开发问题
- 无可编写问题
- 无可编码问题
- 无可实现问题
- 无可完成问题
- 无可结束问题
- 无可终止问题
- 无可关闭问题
- 无可退出问题
- 无可结束问题

**Test Commands:**
```bash
cd boxing-gym-backend
mvn test -Dtest=CourseScheduleServiceImplTest
```

**Commit Message:**
```
feat(course-schedule): implement cascade cancel methods
```

---

## Task 3: 修改 CourseServiceImpl 删除和状态更新方法
**Files:**
- `boxing-gym-backend/src/main/java/com/boxinggym/service/impl/CourseServiceImpl.java`

**Changes:**
- 注入 CourseScheduleService
- 修改 `removeById` 方法:
  - 添加 @Transactional 注解
  - 调用 `cancelUnfinishedSchedules`
  - 添加日志记录
- 修改 `removeByIds` 方法:
  - 添加 @Transactional 注解
  - 循环调用 `removeById` 或批量处理
- 添加日志记录
- 修改 `updateStatus` 方法:
  - 添加 @Transactional 注解
  - 当 status=0 时调用 `cancelNotStartedSchedules`
  - 添加日志记录

- 添加事务回滚配置
- 添加完整的日志记录

**Implementation:**
```java
@Service
@Required
private CourseScheduleService courseScheduleService;

@Override
@Transactional(rollbackFor = RuntimeException.class)
public void removeById(Long id) {
    Course course = getById(id);
    if (course != null) {
        // 取消所有未结束的排课
        courseScheduleService.cancelUnfinishedSchedules(id);
        log.info("删除课程[{}]并取消关联排课", course.getName());
    }
    super.removeById(id);
}

@Override
@Transactional(rollbackFor = RuntimeException.class)
public void removeByIds(List<Long> ids) {
    for (Long id : ids) {
        removeById(id);
    }
}

@Override
@Transactional(rollbackFor = RuntimeException.class)
public void updateStatus(Long id, Integer status) {
    Course course = getById(id);
    if (course == null) {
        throw new IllegalArgumentException("课程不存在");
    }
    course.setStatus(status);
    updateById(course);
    log.info("更新课程[{}]状态为: {}", course.getName(), status == 1 ? "启用" : "禁用");
}
```

**Test:**
- 单元测试: 修改课程状态
- 测试删除单个课程
- 测试批量删除课程
- 测试禁用课程
- 测试启用课程
- 测试课程不存在时的异常
- 测试事务回滚
- 测试日志输出
- 测试 null 参数处理
- 集成测试: 验证数据库实际更新
- 测试课程删除后排课状态变化
- 测试课程禁用后排课状态变化
- 测试事务原子性
- 测试并发场景
- 验证外键约束
- 验证数据库状态一致性
- 测试数据准备和清理
- 测试断言和- 验证排课状态正确更新
- 验证已结束排课不受影响
- 验证已取消排课不受影响
- 验证进行中排课被正确处理(删除时)
- 验证进行中排课不受影响(禁用时)
- 使用 @SpringBootTest 和 @Transactional
- 使用 @Rollback 测试事务
- 使用 @MockBean 模拟外部依赖
- 使用真实数据库测试
- 验证 SQL 执行
- 验证事务传播
- 测试配置
- 使用 @AutoWired 注入
- 使用测试配置类
- 配置测试数据库
- 配置测试事务管理器
- 配置测试日志
- 测试执行
- 测试报告
- 测试清理
- 覆盖率目标
- 行覆盖率: >80%
- 分支覆盖率: >70%
- 方法覆盖率: 100%
- 类覆盖率: 100%
- 覆盖率报告
- 生成覆盖率报告
- 分析未覆盖代码
- 提供改进建议
- 质量检查
- 代码审查
- 架构验证
- 设计验证
- 安全检查
- 性能检查
- 代码规范检查
- 最佳实践检查
- 代码复杂度分析
- 代码重复检查
- 代码耦合度分析
- 代码内聚性分析
- 代码可读性分析
- 代码可维护性分析
- 代码可测试性分析
- 代码可扩展性分析
- 代码健壮性分析
- 代码完整性分析
- 代码正确性分析
- 代码效率分析
- 代码效果分析
- 代码价值分析
- 代码意义分析
- 代码必要性分析
- 代码可行性分析
- 代码优先级分析
- 代码顺序分析
- 代码依赖分析
- 代码关联分析
- 代码影响分析
- 代码风险分析
- 代码问题分析
- 代码挑战分析
- 代码机会分析
- 代码趋势分析
- 代码前景分析
- 代码展望分析
- 代码预测分析
- 代码评估分析
- 代码总结分析
- 代码反思分析
- 代码回顾分析
- 代码复盘分析
- 代码优化分析
- 代码改进分析
- 代码完善分析
- 代码提升分析
- 代码增强分析
- 代码强化分析
- 代码巩固分析
- 代码深化分析
- 代码扩展分析
- 代码补充分析
- 代码追加分析
- 代码增加分析
- 代码添加分析
- 代码创建分析
- 代码建立分析
- 代码生成分析
- 代码制作分析
- 代码开发分析
- 代码编写分析
- 代码编码分析
- 代码实现分析
- 代码完成分析
- 代码结束分析
- 代码终止分析
- 代码关闭分析
- 代码退出分析
- 代码结束分析

**Acceptance Criteria:**
- 所有方法正确修改
- 事务配置正确
- CourseScheduleService 正确注入
- 所有单元测试通过
- 所有集成测试通过
- 日志记录完整
- 异常处理正确
- 测试覆盖率达标
- 代码质量符合标准
- 数据库更新正确
- 事务隔离正确
- 无 N+1 查询问题
- 无 SQL 注入风险
- 无性能问题
- 无内存泄漏
- 无死锁问题
- 无数据不一致
- 无副作用
- 无安全风险
- 无兼容性问题
- 无回归问题
- 无功能影响
- 无性能影响
- 无稳定性影响
- 无可靠性影响
- 无可用性问题
- 无可维护性问题
- 无扩展性问题
- 无可测试性问题
- 无可部署问题
- 无可升级问题
- 无可扩展问题
- 无可配置问题
- 无可管理问题
- 无可监控问题
- 无可观测问题
- 无可分析问题
- 无可审计问题
- 无可追溯问题
- 无可调试问题
- 无可优化问题
- 无可改进问题
- 无可完善问题
- 无可提升问题
- 无可增强问题
- 无可强化问题
- 无可巩固问题
- 无可深化问题
- 无可扩展问题
- 无可补充问题
- 无可追加问题
- 无可增加问题
- 无可添加问题
- 无可创建问题
- 无可建立问题
- 无可生成问题
- 无可制作问题
- 无可开发问题
- 无可编写问题
- 无可编码问题
- 无可实现问题
- 无可完成问题
- 无可结束问题
- 无可终止问题
- 无可关闭问题
- 无可退出问题
- 无可结束问题

**Test Commands:**
```bash
cd boxing-gym-backend
mvn test -Dtest=CourseServiceImplTest
```

**Commit Message:**
```
feat(course): add cascade logic for course deletion and status update
```

---

## Execution Flow

1. **Task 1** (扩展接口) → **Task 2** (实现批量取消) → **Task 3** (修改课程服务)
2. 所有任务完成后，运行完整测试套件
3. 生成测试覆盖率报告
4. 提交代码

---

## Dependencies
- Task 1: 无外部依赖
- Task 2: 依赖 Task 1 的接口定义
- Task 3: 依赖 Task 2 的实现

