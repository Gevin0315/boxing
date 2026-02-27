package com.boxinggym.enums;

import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
public enum GenderEnum {

    /**
     * 女
     */
    FEMALE(0, "女"),

    /**
     * 男
     */
    MALE(1, "男");

    private final Integer code;
    private final String description;

    GenderEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static GenderEnum fromCode(Integer code) {
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.getCode().equals(code)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("无效的性别代码: " + code);
    }
}
