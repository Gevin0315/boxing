package com.boxinggym.enums;

import lombok.Getter;

/**
 * 卡片使用类型枚举
 */
@Getter
public enum CardUsageTypeEnum {

    /**
     * 激活
     */
    ACTIVATE(1, "激活"),

    /**
     * 签到
     */
    CHECKIN(2, "签到"),

    /**
     * 过期
     */
    EXPIRE(3, "过期"),

    /**
     * 作废
     */
    VOID(4, "作废");

    private final Integer code;
    private final String description;

    CardUsageTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return 枚举
     */
    public static CardUsageTypeEnum fromCode(Integer code) {
        for (CardUsageTypeEnum type : CardUsageTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的卡片使用类型代码: " + code);
    }
}
