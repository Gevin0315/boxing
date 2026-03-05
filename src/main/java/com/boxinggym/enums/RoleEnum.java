package com.boxinggym.enums;

import lombok.Getter;

/**
 * 角色枚举
 */
@Getter
public enum RoleEnum {

    /**
     * 超级管理员（馆主）
     */
    ADMIN("ROLE_ADMIN", "超级管理员"),

    /**
     * 前台
     */
    RECEPTION("ROLE_RECEPTION", "前台"),

    /**
     * 教练
     */
    COACH("ROLE_COACH", "教练");

    private final String code;
    private final String description;

    RoleEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据角色代码获取枚举
     */
    public static RoleEnum fromCode(String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("角色不能为空");
        }

        for (RoleEnum role : RoleEnum.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("无效的角色代码: " + code);
    }
}
