package com.boxinggym.controller;

import com.boxinggym.common.BusinessException;
import com.boxinggym.common.Result;
import com.boxinggym.dto.BatchDeleteDTO;
import com.boxinggym.entity.CourseSchedule;
import com.boxinggym.service.CourseScheduleService;
import com.boxinggym.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 排课记录 Controller
 */
@Tag(name = "排课管理", description = "排课记录相关接口")
@RestController
@RequestMapping("/api/course-schedule")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION','ROLE_COACH')")
public class CourseScheduleController {

    private final CourseScheduleService courseScheduleService;

    /**
     * 查询所有排课记录
     */
    @Operation(summary = "查询所有排课记录")
    @GetMapping("/list")
    public Result<List<CourseSchedule>> list() {
        List<CourseSchedule> list = courseScheduleService.lambdaQuery()
                .orderByAsc(CourseSchedule::getStartTime)
                .list();
        return Result.success(list);
    }

    /**
     * 根据ID查询排课记录
     */
    @Operation(summary = "根据ID查询排课记录")
    @GetMapping("/{id}")
    public Result<CourseSchedule> getById(@PathVariable Long id) {
        CourseSchedule schedule = courseScheduleService.getById(id);
        return Result.success(schedule);
    }

    /**
     * 根据教练ID查询排课记录
     */
    @Operation(summary = "根据教练ID查询排课记录")
    @GetMapping("/coach/{coachId}")
    public Result<List<CourseSchedule>> getByCoachId(@PathVariable Long coachId) {
        if (SecurityUtil.isCoach() && !coachId.equals(SecurityUtil.getCurrentUserId())) {
            throw new BusinessException(403, "教练只能查询自己的排课记录");
        }
        List<CourseSchedule> list = courseScheduleService.lambdaQuery()
                .eq(CourseSchedule::getCoachId, coachId)
                .orderByAsc(CourseSchedule::getStartTime)
                .list();
        return Result.success(list);
    }

    /**
     * 新增排课
     */
    @Operation(summary = "新增排课")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> add(@RequestBody CourseSchedule schedule) {
        boolean success = courseScheduleService.save(schedule);
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    /**
     * 修改排课
     */
    @Operation(summary = "修改排课")
    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> update(@RequestBody CourseSchedule schedule) {
        boolean success = courseScheduleService.updateById(schedule);
        return success ? Result.success("修改成功") : Result.fail("修改失败");
    }

    /**
     * 删除排课
     */
    @Operation(summary = "删除排课")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = courseScheduleService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }

    /**
     * 批量删除排课
     */
    @Operation(summary = "批量删除排课")
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> batchDelete(@Valid @RequestBody BatchDeleteDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Result.fail("ID列表不能为空");
        }
        boolean success = courseScheduleService.removeByIds(dto.getIds());
        return success ? Result.success("批量删除成功") : Result.fail("批量删除失败");
    }

    /**
     * 取消排课
     */
    @Operation(summary = "取消排课")
    @PutMapping("/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> cancel(@RequestParam Long id, @RequestParam(required = false) String reason) {
        boolean success = courseScheduleService.lambdaUpdate()
                .eq(CourseSchedule::getId, id)
                .set(CourseSchedule::getStatus, 3)
                .update();
        return success ? Result.success("取消成功") : Result.fail("取消失败");
    }

    /**
     * 完成排课
     */
    @Operation(summary = "完成排课")
    @PutMapping("/complete")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION','ROLE_COACH')")
    public Result<String> complete(@RequestParam Long id) {
        boolean success = courseScheduleService.lambdaUpdate()
                .eq(CourseSchedule::getId, id)
                .set(CourseSchedule::getStatus, 2)
                .update();
        return success ? Result.success("已完成") : Result.fail("操作失败");
    }

    /**
     * 获取排课日历数据
     */
    @Operation(summary = "获取排课日历数据")
    @GetMapping("/calendar")
    public Result<java.util.List<CourseSchedule>> getCalendar(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<CourseSchedule> list = courseScheduleService.lambdaQuery()
                .between(CourseSchedule::getStartTime, startDate, endDate)
                .orderByAsc(CourseSchedule::getStartTime)
                .list();
        return Result.success(list);
    }
}
