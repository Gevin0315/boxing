package com.boxinggym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.common.Result;
import com.boxinggym.dto.SysUserQueryDTO;
import com.boxinggym.entity.SysUser;
import com.boxinggym.enums.RoleEnum;
import com.boxinggym.service.SysUserService;
import com.boxinggym.vo.SysUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统用户 Controller
 */
@Tag(name = "系统用户管理", description = "系统用户相关接口")
@RestController
@RequestMapping("/api/sys-user")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class SysUserController {

    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户
     */
    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<Page<SysUserVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "真实姓名") @RequestParam(required = false) String realName,
            @Parameter(description = "手机号") @RequestParam(required = false) String phone,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "角色") @RequestParam(required = false) String role) {
        SysUserQueryDTO query = new SysUserQueryDTO();
        query.setCurrent(current);
        query.setSize(size);
        query.setUsername(username);
        query.setRealName(realName);
        query.setPhone(phone);
        query.setStatus(status);
        query.setRole(role);
        return Result.success(sysUserService.page(query));
    }

    /**
     * 获取角色列表
     */
    @Operation(summary = "获取角色列表")
    @GetMapping("/roles")
    public Result<List<Map<String, String>>> getRoles() {
        List<Map<String, String>> roles = Arrays.stream(RoleEnum.values())
                .map(role -> Map.of(
                        "value", role.getCode(),
                        "label", role.getDescription()
                ))
                .collect(Collectors.toList());
        return Result.success(roles);
    }

    /**
     * 查询所有用户
     */
    @Operation(summary = "查询所有用户")
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        List<SysUser> list = sysUserService.list();
        return Result.success(list);
    }

    /**
     * 根据ID查询用户
     */
    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @Operation(summary = "新增用户")
    @PostMapping
    public Result<String> add(@RequestBody SysUser user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return Result.validateFail("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return Result.validateFail("密码不能为空");
        }

        // 检查用户名是否已存在
        Long count = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, user.getUsername())
                .count();
        if (count > 0) {
            return Result.validateFail("用户名已存在");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        boolean success = sysUserService.save(user);
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    /**
     * 修改用户
     */
    @Operation(summary = "修改用户")
    @PutMapping
    public Result<String> update(@RequestBody SysUser user) {
        if (user.getId() == null) {
            return Result.validateFail("用户ID不能为空");
        }
        SysUser existing = sysUserService.getById(user.getId());
        if (existing == null) {
            return Result.notFound("用户不存在");
        }

        // 如果要修改用户名，检查新用户名是否被其他用户占用
        if (user.getUsername() != null && !user.getUsername().equals(existing.getUsername())) {
            Long count = sysUserService.lambdaQuery()
                    .eq(SysUser::getUsername, user.getUsername())
                    .ne(SysUser::getId, user.getId())
                    .count();
            if (count > 0) {
                return Result.validateFail("用户名已存在");
            }
            existing.setUsername(user.getUsername());
        }

        existing.setRealName(user.getRealName());
        existing.setRole(user.getRole());
        existing.setStatus(user.getStatus());
        existing.setPhone(user.getPhone());
        existing.setEmail(user.getEmail());
        existing.setRemark(user.getRemark());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        boolean success = sysUserService.updateById(existing);
        return success ? Result.success("修改成功") : Result.fail("修改失败");
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = sysUserService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }

    /**
     * 重置用户密码
     */
    @Operation(summary = "重置用户密码")
    @PutMapping("/password")
    public Result<String> resetPassword(@RequestParam Long id, @RequestParam String password) {
        if (password == null || password.isBlank()) {
            return Result.validateFail("密码不能为空");
        }

        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.notFound("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(password));
        boolean success = sysUserService.updateById(user);

        return success ? Result.success("密码重置成功") : Result.fail("密码重置失败");
    }

    /**
     * 修改用户状态
     */
    @Operation(summary = "修改用户状态")
    @PutMapping("/status")
    public Result<String> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        boolean success = sysUserService.lambdaUpdate()
                .eq(SysUser::getId, id)
                .set(SysUser::getStatus, status)
                .update();
        return success ? Result.success("状态修改成功") : Result.fail("状态修改失败");
    }
}
