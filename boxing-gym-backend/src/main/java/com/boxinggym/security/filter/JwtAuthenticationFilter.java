package com.boxinggym.security.filter;

import com.boxinggym.entity.SysUser;
import com.boxinggym.service.SysUserService;
import com.boxinggym.utils.JwtUtil;
import com.boxinggym.utils.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SysUserService sysUserService;
    private final RedisUtil redisUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, SysUserService sysUserService, RedisUtil redisUtil) {
        this.jwtUtil = jwtUtil;
        this.sysUserService = sysUserService;
        this.redisUtil = redisUtil;
    }

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN_BLACKLIST_PREFIX = "gym:token:blacklist:";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取 Token
        String token = getTokenFromRequest(request);

        // 如果有 Token 且有效
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            // 检查 Token 是否在黑名单中
            String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
            if (Boolean.TRUE.equals(redisUtil.hasKey(blacklistKey))) {
                // Token 在黑名单中，直接放行（后续会返回 401）
                filterChain.doFilter(request, response);
                return;
            }

            // 获取用户信息
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            // 从数据库加载用户信息
            try {
                com.boxinggym.security.UserDetailsImpl userDetails = new com.boxinggym.security.UserDetailsImpl(
                        sysUserService.lambdaQuery()
                                .eq(SysUser::getUsername, username)
                                .one(),
                        role
                );

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置到 Security 上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (UsernameNotFoundException e) {
                // 用户不存在，清除认证信息
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
