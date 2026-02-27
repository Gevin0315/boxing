package com.boxinggym.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.boxinggym.common.BusinessException;
import com.boxinggym.common.Result;
import com.boxinggym.entity.SysCoachProfile;
import com.boxinggym.entity.SysUser;
import com.boxinggym.service.SysCoachProfileService;
import com.boxinggym.service.SysUserService;
import com.boxinggym.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教练扩展信息 Controller
 */
@Tag(name = "教练信息管理", description = "教练扩展信息相关接口")
@RestController
@RequestMapping("/api/coach-profile")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION','ROLE_COACH')")
public class SysCoachProfileController {

    private final SysCoachProfileService sysCoachProfileService;
    private final SysUserService sysUserService;

    /**
     * 查询所有教练信息
     */
    @Operation(summary = "查询所有教练信息")
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<List<SysCoachProfile>> list() {
        List<SysCoachProfile> list = sysCoachProfileService.list();
        return Result.success(list);
    }

    /**
     * 根据ID查询教练信息
     */
    @Operation(summary = "根据ID查询教练信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<SysCoachProfile> getById(@PathVariable Long id) {
        SysCoachProfile profile = sysCoachProfileService.getById(id);
        return Result.success(profile);
    }

    /**
     * 根据用户ID查询教练信息
     */
    @Operation(summary = "根据用户ID查询教练信息")
    @GetMapping("/user/{userId}")
    public Result<SysCoachProfile> getByUserId(@PathVariable Long userId) {
        if (SecurityUtil.isCoach() && !userId.equals(SecurityUtil.getCurrentUserId())) {
            throw new BusinessException(403, "教练只能查询自己的信息");
        }
        return Result.success(sysCoachProfileService.lambdaQuery()
                .eq(SysCoachProfile::getUserId, userId)
                .one());
    }

    /**
     * 新增教练信息
     */
    @Operation(summary = "新增教练信息")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> add(@RequestBody SysCoachProfile profile) {
        boolean success = sysCoachProfileService.save(profile);
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    /**
     * 修改教练信息
     */
    @Operation(summary = "修改教练信息")
    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> update(@RequestBody SysCoachProfile profile) {
        boolean success = sysCoachProfileService.updateById(profile);
        return success ? Result.success("修改成功") : Result.fail("修改失败");
    }

    /**
     * 删除教练信息
     */
    @Operation(summary = "删除教练信息")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = sysCoachProfileService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }

    /**
     * 修改教练状态（同时修改关联用户状态）
     */
    @Operation(summary = "修改教练状态")
    @PutMapping("/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        SysCoachProfile profile = sysCoachProfileService.getById(id);
        if (profile == null) {
            return Result.notFound("教练不存在");
        }

        // 更新用户状态
        boolean success = sysUserService.lambdaUpdate()
                .eq(SysUser::getId, profile.getUserId())
                .set(SysUser::getStatus, status)
                .update();

        return success ? Result.success("状态修改成功") : Result.fail("状态修改失败");
    }

    /**
     * 获取教练下拉选项
     */
    @Operation(summary = "获取教练下拉选项")
    @GetMapping("/options")
    public Result<List<Map<String, Object>>> getOptions() {
        List<SysCoachProfile> profiles = sysCoachProfileService.lambdaQuery()
                .isNotNull(SysCoachProfile::getUserId)
                .list();

        List<Map<String, Object>> options = profiles.stream()
                .filter(p -> {
                    SysUser user = sysUserService.getById(p.getUserId());
                    return user != null && user.getStatus() == 1;
                })
                .map(p -> {
                    SysUser user = sysUserService.getById(p.getUserId());
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", p.getId());
                    map.put("label", user != null ? user.getRealName() : "");
                    return map;
                })
                .collect(Collectors.toList());

        return Result.success(options);
    }

    /**
     * 生成教练号（返回用户ID作为教练号）
     */
    @Operation(summary = "生成教练号")
    @GetMapping("/generate-no")
    public Result<String> generateNo() {
        long count = sysUserService.lambdaQuery()
                .eq(SysUser::getRole, "ROLE_COACH")
                .count();
        return Result.success("C" + String.format("%06d", count + 1));
    }
}
