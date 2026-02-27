package com.boxinggym.controller;

import com.boxinggym.common.Result;
import com.boxinggym.entity.Course;
import com.boxinggym.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程 Controller
 */
@Tag(name = "课程管理", description = "课程相关接口")
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION','ROLE_COACH')")
public class CourseController {

    private final CourseService courseService;

    /**
     * 查询所有课程
     */
    @Operation(summary = "查询所有课程")
    @GetMapping("/list")
    public Result<List<Course>> list() {
        List<Course> list = courseService.lambdaQuery()
                .orderByAsc(Course::getSortOrder)
                .list();
        return Result.success(list);
    }

    /**
     * 查询启用状态的课程
     */
    @Operation(summary = "查询启用状态的课程")
    @GetMapping("/enabled")
    public Result<List<Course>> enabledList() {
        List<Course> list = courseService.lambdaQuery()
                .eq(Course::getStatus, 1)
                .orderByAsc(Course::getSortOrder)
                .list();
        return Result.success(list);
    }

    /**
     * 根据ID查询课程
     */
    @Operation(summary = "根据ID查询课程")
    @GetMapping("/{id}")
    public Result<Course> getById(@PathVariable Long id) {
        Course course = courseService.getById(id);
        return Result.success(course);
    }

    /**
     * 新增课程
     */
    @Operation(summary = "新增课程")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> add(@RequestBody Course course) {
        boolean success = courseService.save(course);
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    /**
     * 修改课程
     */
    @Operation(summary = "修改课程")
    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> update(@RequestBody Course course) {
        boolean success = courseService.updateById(course);
        return success ? Result.success("修改成功") : Result.fail("修改失败");
    }

    /**
     * 删除课程
     */
    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = courseService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }

    /**
     * 修改课程状态
     */
    @Operation(summary = "修改课程状态")
    @PutMapping("/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        boolean success = courseService.lambdaUpdate()
                .eq(com.boxinggym.entity.Course::getId, id)
                .set(com.boxinggym.entity.Course::getStatus, status)
                .update();
        return success ? Result.success("状态修改成功") : Result.fail("状态修改失败");
    }

    /**
     * 获取课程下拉选项（简化数据）
     */
    @Operation(summary = "获取课程下拉选项")
    @GetMapping("/options")
    public Result<java.util.List<java.util.Map<String, Object>>> getOptions() {
        java.util.List<java.util.Map<String, Object>> options = courseService.lambdaQuery()
                .select(com.boxinggym.entity.Course::getId, com.boxinggym.entity.Course::getName)
                .eq(com.boxinggym.entity.Course::getStatus, 1)
                .orderByAsc(com.boxinggym.entity.Course::getSortOrder)
                .list()
                .stream()
                .map(course -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("value", course.getId());
                    map.put("label", course.getName());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
        return Result.success(options);
    }
}
