# 分页接口改造实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将5个管理模块从前端内存分页改为后端服务端分页

**Architecture:** 为每个模块创建 QueryDTO 和 VO，在 Service 层实现分页查询逻辑，Controller 暴露 /page 接口，前端 API 调用新接口

**Tech Stack:** Spring Boot 3.2, MyBatis-Plus, Vue 3, TypeScript, Axios

**Spec:** `docs/superpowers/specs/2026-03-20-pagination-api-design.md`

---

## 文件结构

### 后端新增文件

```
boxing-gym-backend/src/main/java/com/boxinggym/
├── dto/
│   ├── CourseQueryDTO.java
│   ├── CourseScheduleQueryDTO.java
│   ├── CoachQueryDTO.java
│   ├── TrainingRecordQueryDTO.java
│   └── FinanceOrderQueryDTO.java
└── vo/
    ├── CourseVO.java
    ├── CourseScheduleVO.java
    ├── CoachVO.java
    ├── TrainingRecordVO.java
    └── FinanceOrderVO.java
```

### 后端修改文件

```
boxing-gym-backend/src/main/java/com/boxinggym/
├── controller/
│   ├── CourseController.java
│   ├── CourseScheduleController.java
│   ├── SysCoachProfileController.java
│   ├── TrainingRecordController.java
│   └── FinanceOrderController.java
└── service/
    ├── CourseService.java
    ├── impl/CourseServiceImpl.java
    ├── CourseScheduleService.java
    ├── impl/CourseScheduleServiceImpl.java
    ├── SysCoachProfileService.java
    ├── impl/SysCoachProfileServiceImpl.java
    ├── TrainingRecordService.java
    ├── impl/TrainingRecordServiceImpl.java
    ├── FinanceOrderService.java
    └── impl/FinanceOrderServiceImpl.java
```

### 前端修改文件

```
boxing-gym-frontend/src/
├── api/
│   ├── course.ts
│   ├── course-schedule.ts
│   ├── coach-profile.ts
│   ├── training-record.ts
│   └── finance-order.ts
└── types/
    ├── course.ts
    ├── coach.ts
    └── finance.ts
```

---

## Task 1: 课程管理模块

**Files:**
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/dto/CourseQueryDTO.java`
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/vo/CourseVO.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/service/CourseService.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/service/impl/CourseServiceImpl.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/controller/CourseController.java`
- Modify: `boxing-gym-frontend/src/api/course.ts`

### Step 1: 创建 CourseQueryDTO

- [ ] **创建 CourseQueryDTO.java**

```java
package com.boxinggym.dto;

import lombok.Data;

/**
 * 课程查询条件
 */
@Data
public class CourseQueryDTO {
    private Integer current = 1;
    private Integer size = 10;
    private String name;
    private Integer type;
    private String category;
    private String level;
    private Long coachId;
    private Integer status;
}
```

### Step 2: 创建 CourseVO

- [ ] **创建 CourseVO.java**

```java
package com.boxinggym.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程视图对象
 */
@Data
public class CourseVO {
    private Long id;
    private String courseName;
    private Integer courseType;
    private String category;
    private String level;
    private Integer duration;
    private Integer maxCapacity;
    private BigDecimal price;
    private Long coachId;
    private String coachName;
    private String imageUrl;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### Step 3: 修改 CourseService 接口

- [ ] **在 CourseService.java 添加 page 方法**

```java
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.dto.CourseQueryDTO;
import com.boxinggym.vo.CourseVO;

/**
 * 分页查询课程
 */
Page<CourseVO> page(CourseQueryDTO query);
```

### Step 4: 实现 CourseServiceImpl

- [ ] **在 CourseServiceImpl.java 实现 page 方法**

```java
@Override
public Page<CourseVO> page(CourseQueryDTO query) {
    Page<Course> page = new Page<>(query.getCurrent(), query.getSize());
    LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(query.getName())) {
        wrapper.like(Course::getName, query.getName());
    }
    if (query.getType() != null) {
        wrapper.eq(Course::getType, query.getType());
    }
    if (StringUtils.hasText(query.getCategory())) {
        wrapper.eq(Course::getCategory, query.getCategory());
    }
    if (StringUtils.hasText(query.getLevel())) {
        wrapper.eq(Course::getLevel, query.getLevel());
    }
    if (query.getCoachId() != null) {
        wrapper.eq(Course::getCoachId, query.getCoachId());
    }
    if (query.getStatus() != null) {
        wrapper.eq(Course::getStatus, query.getStatus());
    }
    wrapper.orderByAsc(Course::getSortOrder);

    this.page(page, wrapper);

    // 获取教练名称映射
    Map<Long, String> coachMap = new HashMap<>();
    if (query.getCoachId() != null) {
        SysUser coach = sysUserService.getById(query.getCoachId());
        if (coach != null) {
            coachMap.put(coach.getId(), coach.getRealName());
        }
    } else {
        List<Long> coachIds = page.getRecords().stream()
                .map(Course::getCoachId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (!coachIds.isEmpty()) {
            List<SysUser> coaches = sysUserService.listByIds(coachIds);
            coachMap = coaches.stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));
        }
    }

    // 转换为 VO
    Page<CourseVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
    List<CourseVO> voList = page.getRecords().stream().map(course -> {
        CourseVO vo = new CourseVO();
        vo.setId(course.getId());
        vo.setCourseName(course.getName());
        vo.setCourseType(course.getType());
        vo.setCategory(course.getCategory());
        vo.setLevel(course.getLevel());
        vo.setDuration(course.getDuration());
        vo.setMaxCapacity(course.getMaxCapacity());
        vo.setPrice(course.getPrice());
        vo.setCoachId(course.getCoachId());
        vo.setCoachName(coachMap.get(course.getCoachId()));
        vo.setImageUrl(course.getCoverImg());
        vo.setStatus(course.getStatus());
        vo.setCreateTime(course.getCreateTime());
        vo.setUpdateTime(course.getUpdateTime());
        return vo;
    }).collect(Collectors.toList());
    voPage.setRecords(voList);

    return voPage;
}
```

### Step 5: 修改 CourseController

- [ ] **在 CourseController.java 添加 page 接口**

```java
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.dto.CourseQueryDTO;
import com.boxinggym.vo.CourseVO;

/**
 * 分页查询课程
 */
@Operation(summary = "分页查询课程")
@GetMapping("/page")
public Result<Page<CourseVO>> page(CourseQueryDTO query) {
    return Result.success(courseService.page(query));
}
```

### Step 6: 修改前端 API

- [ ] **在 course.ts 添加 pageCourse 函数并修改 listCourse**

```typescript
/** 查询课程分页列表 */
export async function pageCourse(query: CourseQuery) {
  const params: Record<string, any> = {
    current: query.pageNum || 1,
    size: query.pageSize || 10
  }
  if (query.courseName) params.name = query.courseName
  if (query.courseType) params.type = query.courseType === 'private' ? 2 : 1
  if (query.category) params.category = query.category
  if (query.level) params.level = query.level
  if (query.coachId) params.coachId = query.coachId
  if (query.status) params.status = query.status === '0' ? 1 : 0

  const data = await request.get<{ records: any[]; total: number; current: number; size: number }>('/course/page', { params })
  return {
    rows: data.records.map(mapCourse),
    total: data.total
  }
}

/** 查询课程列表（改为调用分页接口）*/
export async function listCourse(query: CourseQuery) {
  return pageCourse(query)
}
```

### Step 7: 提交

- [ ] **提交代码**

```bash
git add boxing-gym-backend/src/main/java/com/boxinggym/dto/CourseQueryDTO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/vo/CourseVO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/service/CourseService.java \
        boxing-gym-backend/src/main/java/com/boxinggym/service/impl/CourseServiceImpl.java \
        boxing-gym-backend/src/main/java/com/boxinggym/controller/CourseController.java \
        boxing-gym-frontend/src/api/course.ts
git commit -m "feat(course): 添加课程管理服务端分页接口"
```

---

## Task 2: 排课管理模块

**Files:**
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/dto/CourseScheduleQueryDTO.java`
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/vo/CourseScheduleVO.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/service/CourseScheduleService.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/service/impl/CourseScheduleServiceImpl.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/controller/CourseScheduleController.java`
- Modify: `boxing-gym-frontend/src/api/course-schedule.ts`

### Step 1: 创建 CourseScheduleQueryDTO

- [ ] **创建 CourseScheduleQueryDTO.java**

```java
package com.boxinggym.dto;

import lombok.Data;

/**
 * 排课查询条件
 */
@Data
public class CourseScheduleQueryDTO {
    private Integer current = 1;
    private Integer size = 10;
    private Long courseId;
    private Long coachId;
    private Integer status;
    private String startDate;
    private String endDate;
}
```

### Step 2: 创建 CourseScheduleVO

- [ ] **创建 CourseScheduleVO.java**

```java
package com.boxinggym.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 排课视图对象
 */
@Data
public class CourseScheduleVO {
    private Long id;
    private Long courseId;
    private String courseName;
    private Integer courseType;
    private Long coachId;
    private String coachName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String classroom;
    private Integer maxCapacity;
    private Integer currentCount;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
```

### Step 3: 修改 CourseScheduleService 接口

- [ ] **在 CourseScheduleService.java 添加 page 方法**

```java
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.dto.CourseScheduleQueryDTO;
import com.boxinggym.vo.CourseScheduleVO;

/**
 * 分页查询排课
 */
Page<CourseScheduleVO> page(CourseScheduleQueryDTO query);
```

### Step 4: 实现 CourseScheduleServiceImpl

- [ ] **在 CourseScheduleServiceImpl.java 实现 page 方法**

```java
@Override
public Page<CourseScheduleVO> page(CourseScheduleQueryDTO query) {
    Page<CourseSchedule> page = new Page<>(query.getCurrent(), query.getSize());
    LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();

    if (query.getCourseId() != null) {
        wrapper.eq(CourseSchedule::getCourseId, query.getCourseId());
    }
    if (query.getCoachId() != null) {
        wrapper.eq(CourseSchedule::getCoachId, query.getCoachId());
    }
    if (query.getStatus() != null) {
        wrapper.eq(CourseSchedule::getStatus, query.getStatus());
    }
    if (StringUtils.hasText(query.getStartDate())) {
        wrapper.ge(CourseSchedule::getStartTime, query.getStartDate() + " 00:00:00");
    }
    if (StringUtils.hasText(query.getEndDate())) {
        wrapper.le(CourseSchedule::getStartTime, query.getEndDate() + " 23:59:59");
    }
    wrapper.orderByAsc(CourseSchedule::getStartTime);

    this.page(page, wrapper);

    // 获取关联数据
    Map<Long, Course> courseMap = new HashMap<>();
    Map<Long, SysUser> coachMap = new HashMap<>();

    List<Long> courseIds = page.getRecords().stream()
            .map(CourseSchedule::getCourseId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    List<Long> coachIds = page.getRecords().stream()
            .map(CourseSchedule::getCoachId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

    if (!courseIds.isEmpty()) {
        courseMap = courseService.listByIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, c -> c));
    }
    if (!coachIds.isEmpty()) {
        coachMap = sysUserService.listByIds(coachIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));
    }

    // 转换为 VO
    Map<Long, Course> finalCourseMap = courseMap;
    Map<Long, SysUser> finalCoachMap = coachMap;
    Page<CourseScheduleVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
    List<CourseScheduleVO> voList = page.getRecords().stream().map(schedule -> {
        CourseScheduleVO vo = new CourseScheduleVO();
        vo.setId(schedule.getId());
        vo.setCourseId(schedule.getCourseId());
        vo.setCoachId(schedule.getCoachId());
        vo.setStartTime(schedule.getStartTime());
        vo.setEndTime(schedule.getEndTime());
        vo.setClassroom(schedule.getClassroom());
        vo.setMaxCapacity(schedule.getMaxPeople());
        vo.setCurrentCount(schedule.getCurrentPeople());
        vo.setStatus(schedule.getStatus());
        vo.setRemark(schedule.getRemark());
        vo.setCreateTime(schedule.getCreateTime());

        Course course = finalCourseMap.get(schedule.getCourseId());
        if (course != null) {
            vo.setCourseName(course.getName());
            vo.setCourseType(course.getType());
        }

        SysUser coach = finalCoachMap.get(schedule.getCoachId());
        if (coach != null) {
            vo.setCoachName(coach.getRealName());
        }

        return vo;
    }).collect(Collectors.toList());
    voPage.setRecords(voList);

    return voPage;
}
```

### Step 5: 修改 CourseScheduleController

- [ ] **在 CourseScheduleController.java 添加 page 接口**

```java
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.dto.CourseScheduleQueryDTO;
import com.boxinggym.vo.CourseScheduleVO;

/**
 * 分页查询排课
 */
@Operation(summary = "分页查询排课")
@GetMapping("/page")
public Result<Page<CourseScheduleVO>> page(CourseScheduleQueryDTO query) {
    return Result.success(courseScheduleService.page(query));
}
```

### Step 6: 修改前端 API

- [ ] **在 course-schedule.ts 添加 pageSchedule 函数并修改 listSchedule**

```typescript
/** 查询排课分页列表 */
export async function pageSchedule(query: any) {
  const params: Record<string, any> = {
    current: query.pageNum || 1,
    size: query.pageSize || 10
  }
  if (query.courseId) params.courseId = query.courseId
  if (query.coachId) params.coachId = query.coachId
  if (query.status !== undefined && query.status !== '') params.status = query.status
  if (query.startDate) params.startDate = query.startDate
  if (query.endDate) params.endDate = query.endDate

  const data = await request.get<{ records: any[]; total: number }>('/course-schedule/page', { params })
  return {
    rows: data.records.map(mapSchedule),
    total: data.total
  }
}

/** 查询排课列表（改为调用分页接口）*/
export async function listSchedule(query: any) {
  return pageSchedule(query)
}
```

### Step 7: 提交

- [ ] **提交代码**

```bash
git add boxing-gym-backend/src/main/java/com/boxinggym/dto/CourseScheduleQueryDTO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/vo/CourseScheduleVO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/service/CourseScheduleService.java \
        boxing-gym-backend/src/main/java/com/boxinggym/service/impl/CourseScheduleServiceImpl.java \
        boxing-gym-backend/src/main/java/com/boxinggym/controller/CourseScheduleController.java \
        boxing-gym-frontend/src/api/course-schedule.ts
git commit -m "feat(schedule): 添加排课管理服务端分页接口"
```

---

## Task 3: 教练管理模块

**Files:**
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/dto/CoachQueryDTO.java`
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/vo/CoachVO.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/service/SysCoachProfileService.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/service/impl/SysCoachProfileServiceImpl.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/controller/SysCoachProfileController.java`
- Modify: `boxing-gym-frontend/src/api/coach-profile.ts`

### Step 1: 创建 CoachQueryDTO

- [ ] **创建 CoachQueryDTO.java**

```java
package com.boxinggym.dto;

import lombok.Data;

/**
 * 教练查询条件
 */
@Data
public class CoachQueryDTO {
    private Integer current = 1;
    private Integer size = 10;
    private String name;
    private Integer status;
}
```

### Step 2: 创建 CoachVO

- [ ] **创建 CoachVO.java**

```java
package com.boxinggym.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教练视图对象
 */
@Data
public class CoachVO {
    private Long id;
    private Long userId;
    private String coachNo;
    private String name;
    private Integer gender;
    private String phone;
    private String email;
    private String specialties;
    private Integer level;
    private Integer status;
    private String imageUrl;
    private String description;
    private LocalDate hireDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### Step 3: 修改 SysCoachProfileService 接口

- [ ] **在 SysCoachProfileService.java 添加 page 方法**

```java
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.dto.CoachQueryDTO;
import com.boxinggym.vo.CoachVO;

/**
 * 分页查询教练
 */
Page<CoachVO> page(CoachQueryDTO query);
```

### Step 4: 实现 SysCoachProfileServiceImpl

- [ ] **在 SysCoachProfileServiceImpl.java 实现 page 方法**

```java
@Override
public Page<CoachVO> page(CoachQueryDTO query) {
    Page<SysCoachProfile> page = new Page<>(query.getCurrent(), query.getSize());

    // 先查询所有教练档案
    LambdaQueryWrapper<SysCoachProfile> wrapper = new LambdaQueryWrapper<>();
    wrapper.isNotNull(SysCoachProfile::getUserId);
    this.page(page, wrapper);

    // 获取关联用户
    List<Long> userIds = page.getRecords().stream()
            .map(SysCoachProfile::getUserId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

    Map<Long, SysUser> userMap = new HashMap<>();
    if (!userIds.isEmpty()) {
        userMap = sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));
    }

    // 转换为 VO 并过滤
    Map<Long, SysUser> finalUserMap = userMap;
    List<CoachVO> voList = page.getRecords().stream()
            .map(profile -> {
                SysUser user = finalUserMap.get(profile.getUserId());
                if (user == null) return null;

                // 按姓名过滤
                if (StringUtils.hasText(query.getName()) && !user.getRealName().contains(query.getName())) {
                    return null;
                }
                // 按状态过滤
                if (query.getStatus() != null && !query.getStatus().equals(user.getStatus())) {
                    return null;
                }

                CoachVO vo = new CoachVO();
                vo.setId(profile.getId());
                vo.setUserId(profile.getUserId());
                vo.setCoachNo("C" + String.format("%06d", profile.getUserId()));
                vo.setName(user.getRealName());
                vo.setGender(profile.getGender());
                vo.setPhone(profile.getPhone() != null ? profile.getPhone() : user.getPhone());
                vo.setEmail(profile.getEmail() != null ? profile.getEmail() : user.getEmail());
                vo.setSpecialties(profile.getSpecialty());
                vo.setLevel(profile.getLevel());
                vo.setStatus(user.getStatus());
                vo.setImageUrl(profile.getImgUrl());
                vo.setDescription(profile.getIntro());
                vo.setHireDate(profile.getHireDate());
                vo.setCreateTime(profile.getCreateTime());
                vo.setUpdateTime(profile.getUpdateTime());
                return vo;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    // 重新计算分页（由于内存过滤）
    Page<CoachVO> voPage = new Page<>(query.getCurrent(), query.getSize());
    voPage.setTotal(voList.size());
    int start = (query.getCurrent() - 1) * query.getSize();
    int end = Math.min(start + query.getSize(), voList.size());
    voPage.setRecords(start < voList.size() ? voList.subList(start, end) : new ArrayList<>());

    return voPage;
}
```

### Step 5: 修改 SysCoachProfileController

- [ ] **在 SysCoachProfileController.java 添加 page 接口**

```java
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.dto.CoachQueryDTO;
import com.boxinggym.vo.CoachVO;

/**
 * 分页查询教练
 */
@Operation(summary = "分页查询教练")
@GetMapping("/page")
public Result<Page<CoachVO>> page(CoachQueryDTO query) {
    return Result.success(sysCoachProfileService.page(query));
}
```

### Step 6: 修改前端 API

- [ ] **在 coach-profile.ts 添加 pageCoach 函数并修改 listCoach**

```typescript
/** 查询教练分页列表 */
export async function pageCoach(query: CoachQuery) {
  const params: Record<string, any> = {
    current: query.pageNum || 1,
    size: query.pageSize || 10
  }
  if (query.name) params.name = query.name
  if (query.status !== undefined && query.status !== '') params.status = query.status === '0' ? 1 : 0

  const data = await request.get<{ records: any[]; total: number }>('/coach-profile/page', { params })
  return {
    rows: data.records.map((item: any) => ({
      id: item.id,
      coachNo: item.coachNo,
      name: item.name,
      gender: String(item.gender ?? 0) as '0' | '1',
      phone: item.phone || '',
      specialties: item.specialties || '',
      level: String(item.level ?? 1) as '1' | '2' | '3' | '4',
      status: item.status === 1 ? '0' : (item.status === 0 ? '1' : '2') as '0' | '1' | '2',
      imageUrl: item.imageUrl || '',
      description: item.description || '',
      email: item.email || '',
      birthday: item.birthday || '',
      address: item.address || '',
      hireDate: item.hireDate || '',
      createTime: item.createTime,
      updateTime: item.updateTime
    })),
    total: data.total
  }
}

/** 查询教练列表（改为调用分页接口）*/
export async function listCoach(query: CoachQuery) {
  return pageCoach(query)
}
```

### Step 7: 提交

- [ ] **提交代码**

```bash
git add boxing-gym-backend/src/main/java/com/boxinggym/dto/CoachQueryDTO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/vo/CoachVO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/service/SysCoachProfileService.java \
        boxing-gym-backend/src/main/java/com/boxinggym/service/impl/SysCoachProfileServiceImpl.java \
        boxing-gym-backend/src/main/java/com/boxinggym/controller/SysCoachProfileController.java \
        boxing-gym-frontend/src/api/coach-profile.ts
git commit -m "feat(coach): 添加教练管理服务端分页接口"
```

---

## Task 4: 签到管理模块

**Files:**
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/dto/TrainingRecordQueryDTO.java`
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/vo/TrainingRecordVO.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/service/TrainingRecordService.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/service/impl/TrainingRecordServiceImpl.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/controller/TrainingRecordController.java`
- Modify: `boxing-gym-frontend/src/api/training-record.ts`

### Step 1: 创建 TrainingRecordQueryDTO

- [ ] **创建 TrainingRecordQueryDTO.java**

```java
package com.boxinggym.dto;

import lombok.Data;

/**
 * 签到记录查询条件
 */
@Data
public class TrainingRecordQueryDTO {
    private Integer current = 1;
    private Integer size = 10;
    private Long memberId;
    private Long scheduleId;
    private Integer status;
}
```

### Step 2: 创建 TrainingRecordVO

- [ ] **创建 TrainingRecordVO.java**

```java
package com.boxinggym.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 签到记录视图对象
 */
@Data
public class TrainingRecordVO {
    private Long id;
    private String recordNo;
    private Long scheduleId;
    private Long courseId;
    private String courseName;
    private Long coachId;
    private String coachName;
    private Long memberId;
    private String memberNo;
    private String memberName;
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
```

### Step 3: 修改 TrainingRecordService 接口

- [ ] **在 TrainingRecordService.java 添加 page 方法**

```java
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.dto.TrainingRecordQueryDTO;
import com.boxinggym.vo.TrainingRecordVO;

/**
 * 分页查询签到记录
 */
Page<TrainingRecordVO> page(TrainingRecordQueryDTO query);
```

### Step 4: 实现 TrainingRecordServiceImpl

- [ ] **在 TrainingRecordServiceImpl.java 实现 page 方法**

```java
@Override
public Page<TrainingRecordVO> page(TrainingRecordQueryDTO query) {
    Page<TrainingRecord> page = new Page<>(query.getCurrent(), query.getSize());
    LambdaQueryWrapper<TrainingRecord> wrapper = new LambdaQueryWrapper<>();

    if (query.getMemberId() != null) {
        wrapper.eq(TrainingRecord::getMemberId, query.getMemberId());
    }
    if (query.getScheduleId() != null) {
        wrapper.eq(TrainingRecord::getScheduleId, query.getScheduleId());
    }
    if (query.getStatus() != null) {
        wrapper.eq(TrainingRecord::getStatus, query.getStatus());
    }
    wrapper.orderByDesc(TrainingRecord::getCreateTime);

    this.page(page, wrapper);

    // 获取关联数据
    Map<Long, Member> memberMap = new HashMap<>();
    Map<Long, CourseSchedule> scheduleMap = new HashMap<>();
    Map<Long, Course> courseMap = new HashMap<>();
    Map<Long, SysUser> coachMap = new HashMap<>();

    List<Long> memberIds = page.getRecords().stream()
            .map(TrainingRecord::getMemberId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    List<Long> scheduleIds = page.getRecords().stream()
            .map(TrainingRecord::getScheduleId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

    if (!memberIds.isEmpty()) {
        memberMap = memberService.listByIds(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, m -> m));
    }
    if (!scheduleIds.isEmpty()) {
        scheduleMap = courseScheduleService.listByIds(scheduleIds).stream()
                .collect(Collectors.toMap(CourseSchedule::getId, s -> s));

        List<Long> courseIds = scheduleMap.values().stream()
                .map(CourseSchedule::getCourseId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        List<Long> coachIds = scheduleMap.values().stream()
                .map(CourseSchedule::getCoachId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (!courseIds.isEmpty()) {
            courseMap = courseService.listByIds(courseIds).stream()
                    .collect(Collectors.toMap(Course::getId, c -> c));
        }
        if (!coachIds.isEmpty()) {
            coachMap = sysUserService.listByIds(coachIds).stream()
                    .collect(Collectors.toMap(SysUser::getId, u -> u));
        }
    }

    // 转换为 VO
    Map<Long, Member> finalMemberMap = memberMap;
    Map<Long, CourseSchedule> finalScheduleMap = scheduleMap;
    Map<Long, Course> finalCourseMap = courseMap;
    Map<Long, SysUser> finalCoachMap = coachMap;
    Page<TrainingRecordVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
    List<TrainingRecordVO> voList = page.getRecords().stream().map(record -> {
        TrainingRecordVO vo = new TrainingRecordVO();
        vo.setId(record.getId());
        vo.setRecordNo(record.getRecordNo());
        vo.setScheduleId(record.getScheduleId());
        vo.setMemberId(record.getMemberId());
        vo.setCoachId(record.getCoachId());
        vo.setCheckinTime(record.getCheckinTime());
        vo.setCheckoutTime(record.getCheckoutTime());
        vo.setStatus(record.getStatus());
        vo.setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime());

        Member member = finalMemberMap.get(record.getMemberId());
        if (member != null) {
            vo.setMemberNo(member.getMemberNo());
            vo.setMemberName(member.getName());
        }

        CourseSchedule schedule = finalScheduleMap.get(record.getScheduleId());
        if (schedule != null) {
            vo.setCourseId(schedule.getCourseId());
            vo.setCoachId(schedule.getCoachId());

            Course course = finalCourseMap.get(schedule.getCourseId());
            if (course != null) {
                vo.setCourseName(course.getName());
            }

            SysUser coach = finalCoachMap.get(schedule.getCoachId());
            if (coach != null) {
                vo.setCoachName(coach.getRealName());
            }
        }

        return vo;
    }).collect(Collectors.toList());
    voPage.setRecords(voList);

    return voPage;
}
```

### Step 5: 修改 TrainingRecordController

- [ ] **在 TrainingRecordController.java 添加 page 接口**

```java
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.dto.TrainingRecordQueryDTO;
import com.boxinggym.vo.TrainingRecordVO;

/**
 * 分页查询签到记录
 */
@Operation(summary = "分页查询签到记录")
@GetMapping("/page")
public Result<Page<TrainingRecordVO>> page(TrainingRecordQueryDTO query) {
    return Result.success(trainingRecordService.page(query));
}
```

### Step 6: 修改前端 API

- [ ] **在 training-record.ts 添加 pageTrainingRecord 函数并修改 listTrainingRecord**

```typescript
/** 查询签到记录分页列表 */
export async function pageTrainingRecord(query: any) {
  const params: Record<string, any> = {
    current: query.pageNum || 1,
    size: query.pageSize || 10
  }
  if (query.memberId) params.memberId = query.memberId
  if (query.scheduleId) params.scheduleId = query.scheduleId
  if (query.status !== undefined && query.status !== '') params.status = query.status

  const data = await request.get<{ records: any[]; total: number }>('/training-record/page', { params })
  return {
    rows: data.records.map(mapTraining),
    total: data.total
  }
}

/** 查询签到记录列表（改为调用分页接口）*/
export async function listTrainingRecord(query: any) {
  return pageTrainingRecord(query)
}
```

### Step 7: 提交

- [ ] **提交代码**

```bash
git add boxing-gym-backend/src/main/java/com/boxinggym/dto/TrainingRecordQueryDTO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/vo/TrainingRecordVO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/service/TrainingRecordService.java \
        boxing-gym-backend/src/main/java/com/boxinggym/service/impl/TrainingRecordServiceImpl.java \
        boxing-gym-backend/src/main/java/com/boxinggym/controller/TrainingRecordController.java \
        boxing-gym-frontend/src/api/training-record.ts
git commit -m "feat(training): 添加签到管理服务端分页接口"
```

---

## Task 5: 财务管理模块

**Files:**
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/dto/FinanceOrderQueryDTO.java`
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/vo/FinanceOrderVO.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/service/FinanceOrderService.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/service/impl/FinanceOrderServiceImpl.java`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/controller/FinanceOrderController.java`
- Modify: `boxing-gym-frontend/src/api/finance-order.ts`

### Step 1: 创建 FinanceOrderQueryDTO

- [ ] **创建 FinanceOrderQueryDTO.java**

```java
package com.boxinggym.dto;

import lombok.Data;

/**
 * 财务订单查询条件
 */
@Data
public class FinanceOrderQueryDTO {
    private Integer current = 1;
    private Integer size = 10;
    private String orderNo;
    private Integer type;
    private String memberNo;
    private String memberName;
    private Integer paymentStatus;
}
```

### Step 2: 创建 FinanceOrderVO

- [ ] **创建 FinanceOrderVO.java**

```java
package com.boxinggym.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务订单视图对象
 */
@Data
public class FinanceOrderVO {
    private Long id;
    private String orderNo;
    private Long memberId;
    private String memberNo;
    private String memberName;
    private Integer type;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private Integer payMethod;
    private Integer paymentStatus;
    private String remark;
    private LocalDateTime createTime;
}
```

### Step 3: 修改 FinanceOrderService 接口

- [ ] **在 FinanceOrderService.java 添加 page 方法**

```java
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.dto.FinanceOrderQueryDTO;
import com.boxinggym.vo.FinanceOrderVO;

/**
 * 分页查询财务订单
 */
Page<FinanceOrderVO> page(FinanceOrderQueryDTO query);
```

### Step 4: 实现 FinanceOrderServiceImpl

- [ ] **在 FinanceOrderServiceImpl.java 实现 page 方法**

```java
@Override
public Page<FinanceOrderVO> page(FinanceOrderQueryDTO query) {
    Page<FinanceOrder> page = new Page<>(query.getCurrent(), query.getSize());
    LambdaQueryWrapper<FinanceOrder> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(query.getOrderNo())) {
        wrapper.like(FinanceOrder::getOrderNo, query.getOrderNo());
    }
    if (query.getType() != null) {
        wrapper.eq(FinanceOrder::getType, query.getType());
    }
    if (query.getPaymentStatus() != null) {
        wrapper.eq(FinanceOrder::getPaymentStatus, query.getPaymentStatus());
    }
    wrapper.orderByDesc(FinanceOrder::getCreateTime);

    // 先查询订单
    this.page(page, wrapper);

    // 获取会员信息
    List<Long> memberIds = page.getRecords().stream()
            .map(FinanceOrder::getMemberId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

    Map<Long, Member> memberMap = new HashMap<>();
    if (!memberIds.isEmpty()) {
        memberMap = memberService.listByIds(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, m -> m));
    }

    // 转换为 VO 并过滤
    Map<Long, Member> finalMemberMap = memberMap;
    List<FinanceOrderVO> voList = page.getRecords().stream()
            .map(order -> {
                Member member = finalMemberMap.get(order.getMemberId());

                // 按会员号过滤
                if (StringUtils.hasText(query.getMemberNo()) && (member == null || !member.getMemberNo().contains(query.getMemberNo()))) {
                    return null;
                }
                // 按会员名过滤
                if (StringUtils.hasText(query.getMemberName()) && (member == null || !member.getName().contains(query.getMemberName()))) {
                    return null;
                }

                FinanceOrderVO vo = new FinanceOrderVO();
                vo.setId(order.getId());
                vo.setOrderNo(order.getOrderNo());
                vo.setMemberId(order.getMemberId());
                vo.setType(order.getType());
                vo.setAmount(order.getAmount());
                vo.setPaidAmount(order.getPaidAmount());
                vo.setPayMethod(order.getPayMethod());
                vo.setPaymentStatus(order.getPaymentStatus());
                vo.setRemark(order.getRemark());
                vo.setCreateTime(order.getCreateTime());

                if (member != null) {
                    vo.setMemberNo(member.getMemberNo());
                    vo.setMemberName(member.getName());
                }

                return vo;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    // 重新计算分页（由于内存过滤）
    Page<FinanceOrderVO> voPage = new Page<>(query.getCurrent(), query.getSize());
    voPage.setTotal(voList.size());
    int start = (query.getCurrent() - 1) * query.getSize();
    int end = Math.min(start + query.getSize(), voList.size());
    voPage.setRecords(start < voList.size() ? voList.subList(start, end) : new ArrayList<>());

    return voPage;
}
```

### Step 5: 修改 FinanceOrderController

- [ ] **在 FinanceOrderController.java 添加 page 接口**

```java
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.dto.FinanceOrderQueryDTO;
import com.boxinggym.vo.FinanceOrderVO;

/**
 * 分页查询财务订单
 */
@Operation(summary = "分页查询财务订单")
@GetMapping("/page")
public Result<Page<FinanceOrderVO>> page(FinanceOrderQueryDTO query) {
    return Result.success(financeOrderService.page(query));
}
```

### Step 6: 修改前端 API

- [ ] **在 finance-order.ts 添加 pageFinanceOrder 函数并修改 listFinanceOrder**

```typescript
/** 查询财务订单分页列表 */
export async function pageFinanceOrder(query: FinanceOrderQuery) {
  const params: Record<string, any> = {
    current: query.pageNum || 1,
    size: query.pageSize || 10
  }
  if (query.orderNo) params.orderNo = query.orderNo
  if (query.orderType) params.type = query.orderType === '0' ? 1 : (query.orderType === '1' ? 2 : (query.orderType === '2' ? 3 : 4))
  if (query.memberNo) params.memberNo = query.memberNo
  if (query.memberName) params.memberName = query.memberName
  if (query.paymentStatus) params.paymentStatus = query.paymentStatus

  const data = await request.get<{ records: any[]; total: number }>('/finance-order/page', { params })
  return {
    rows: data.records.map(mapFinance),
    total: data.total
  }
}

/** 查询财务订单列表（改为调用分页接口）*/
export async function listFinanceOrder(query: FinanceOrderQuery) {
  return pageFinanceOrder(query)
}
```

### Step 7: 提交

- [ ] **提交代码**

```bash
git add boxing-gym-backend/src/main/java/com/boxinggym/dto/FinanceOrderQueryDTO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/vo/FinanceOrderVO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/service/FinanceOrderService.java \
        boxing-gym-backend/src/main/java/com/boxinggym/service/impl/FinanceOrderServiceImpl.java \
        boxing-gym-backend/src/main/java/com/boxinggym/controller/FinanceOrderController.java \
        boxing-gym-frontend/src/api/finance-order.ts
git commit -m "feat(finance): 添加财务管理服务端分页接口"
```

---

## Task 6: 最终验证

### Step 1: 编译后端

- [ ] **编译后端项目**

```bash
cd boxing-gym-backend && mvn -DskipTests compile
```

Expected: BUILD SUCCESS

### Step 2: 编译前端

- [ ] **编译前端项目**

```bash
cd boxing-gym-frontend && npm run build
```

Expected: 构建成功

### Step 3: 提交最终修改

- [ ] **提交所有修改**

```bash
git add -A
git commit -m "feat: 完成分页接口改造"
```
