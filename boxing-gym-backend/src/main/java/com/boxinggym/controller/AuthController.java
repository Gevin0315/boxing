package com.boxinggym.controller;

import com.boxinggym.common.Result;
import com.boxinggym.dto.LoginRequest;
import com.boxinggym.dto.LoginResponse;
import com.boxinggym.entity.SysUser;
import com.boxinggym.enums.RoleEnum;
import com.boxinggym.security.UserDetailsImpl;
import com.boxinggym.service.SysUserService;
import com.boxinggym.utils.JwtUtil;
import com.boxinggym.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.boxinggym.common.Result.*;

/**
 * 认证 Controller
 */
@Tag(name = "认证管理", description = "登录、登出等认证相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    private static final String TOKEN_BLACKLIST_PREFIX = "gym:token:blacklist:";

    /**
     * 登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 查询用户
        SysUser user = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, request.getUsername())
                .one();

        // 用户不存在
        if (user == null) {
            return fail("用户名或密码错误");
        }

        // 密码错误
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return fail("用户名或密码错误");
        }

        // 账号禁用
        if (user.getStatus() == 0) {
            return fail("账号已被禁用，请联系管理员");
        }

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        // 获取角色描述
        String roleDesc = RoleEnum.fromCode(user.getRole()).getDescription();
        // 构建响应
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .roleDesc(roleDesc)
                .build();
        return success("登录成功", response);
    }

    /**
     * 登出
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return fail("无效的认证信息");
        }

        // 提取 Token
        String token = authorization.replace("Bearer ", "");

        // 将 Token 加入黑名单
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        long remainingTime = jwtUtil.getExpirationDateFromToken(token).getTime() - System.currentTimeMillis();
        if (remainingTime > 0) {
            redisUtil.set(blacklistKey, "1", remainingTime / 1000);
        }
        return success("登出成功");
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<SysUser> getCurrentUser() {
        // 从 Security 上下文获取当前用户
        Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null || principal instanceof String) {
            return fail("未登录或认证失效");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        return success("获取成功", userDetails.getUser());
    }
}
