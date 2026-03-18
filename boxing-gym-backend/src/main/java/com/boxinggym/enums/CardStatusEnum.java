package com.boxinggym.enums;

import lombok.Getter;

/**
 * 卡状态枚举
 */
@Getter
public enum CardStatusEnum {

    /**
     * 未激活
     */
    INACTIVE(0, "未激活"),

    /**
     * 生效中
     */
    ACTIVE(1, "生效中"),

    /**
     * 已过期
     */
    EXPIRED(2, "已过期"),

    /**
     * 已作废
     */
    VOIDED(3, "已作废");

    private final Integer code;
    private final String description;

    CardStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return 枚举
     */
    public static CardStatusEnum fromCode(Integer code) {
        for (CardStatusEnum status : CardStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的卡状态代码: " + code);
    }
}
