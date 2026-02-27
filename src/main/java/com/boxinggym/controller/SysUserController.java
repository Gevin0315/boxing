package com.boxinggym.controller;

import com.boxinggym.common.Result;
import com.boxinggym.entity.SysUser;
import com.boxinggym.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        existing.setRealName(user.getRealName());
        existing.setRole(user.getRole());
        existing.setStatus(user.getStatus());
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
