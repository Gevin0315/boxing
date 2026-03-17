package com.boxinggym.enums;

import lombok.Getter;

/**
 * 课程类型枚举
 */
@Getter
public enum CourseTypeEnum {

    /**
     * 团课
     */
    GROUP(1, "团课"),

    /**
     * 私教
     */
    PRIVATE(2, "私教");

    private final Integer code;
    private final String description;

    CourseTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static CourseTypeEnum fromCode(Integer code) {
        for (CourseTypeEnum type : CourseTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的课程类型代码: " + code);
    }
}
