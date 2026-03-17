package com.boxinggym.enums;

import lombok.Getter;

/**
 * 擅长流派枚举
 */
@Getter
public enum SpecialtyEnum {

    /**
     * 拳击
     */
    BOXING("boxing", "拳击"),

    /**
     * 泰拳
     */
    MUAY_THAI("muay_thai", "泰拳"),

    /**
     * 巴西柔术
     */
    BJJ("bjj", "巴西柔术"),

    /**
     * 自由搏击
     */
    KICKBOXING("kickboxing", "自由搏击"),

    /**
     * 摔跤
     */
    WRESTLING("wrestling", "摔跤"),

    /**
     * 综合
     */
    MMA("mma", "综合格斗");

    private final String code;
    private final String description;

    SpecialtyEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static SpecialtyEnum fromCode(String code) {
        for (SpecialtyEnum specialty : SpecialtyEnum.values()) {
            if (specialty.getCode().equals(code)) {
                return specialty;
            }
        }
        throw new IllegalArgumentException("无效的流派代码: " + code);
    }
}
