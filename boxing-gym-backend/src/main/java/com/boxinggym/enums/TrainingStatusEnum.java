package com.boxinggym.enums;

import lombok.Getter;

/**
 * 上课记录状态枚举
 */
@Getter
public enum TrainingStatusEnum {

    /**
     * 已预约
     */
    BOOKED(0, "已预约"),

    /**
     * 已签到
     */
    CHECKED_IN(1, "已签到"),

    /**
     * 已取消
     */
    CANCELLED(2, "已取消"),

    /**
     * 旷课
     */
    ABSENT(3, "旷课");

    private final Integer code;
    private final String description;

    TrainingStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static TrainingStatusEnum fromCode(Integer code) {
        for (TrainingStatusEnum status : TrainingStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的上课记录状态代码: " + code);
    }
}
