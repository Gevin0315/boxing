package com.boxinggym.enums;

import lombok.Getter;

/**
 * 排课状态枚举
 */
@Getter
public enum ScheduleStatusEnum {

    /**
     * 未开始
     */
    NOT_STARTED(0, "未开始"),

    /**
     * 进行中
     */
    IN_PROGRESS(1, "进行中"),

    /**
     * 已结束
     */
    FINISHED(2, "已结束"),

    /**
     * 已取消
     */
    CANCELLED(3, "已取消");

    private final Integer code;
    private final String description;

    ScheduleStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static ScheduleStatusEnum fromCode(Integer code) {
        for (ScheduleStatusEnum status : ScheduleStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的排课状态代码: " + code);
    }
}
