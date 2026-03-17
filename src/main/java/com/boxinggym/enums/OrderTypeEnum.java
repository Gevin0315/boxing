package com.boxinggym.enums;

import lombok.Getter;

/**
 * 订单类型枚举
 */
@Getter
public enum OrderTypeEnum {

    /**
     * 充值
     */
    RECHARGE(1, "充值"),

    /**
     * 退款
     */
    REFUND(2, "退款"),

    /**
     * 课程消费
     */
    COURSE_CONSUME(3, "课程消费");

    private final Integer code;
    private final String description;

    OrderTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static OrderTypeEnum fromCode(Integer code) {
        for (OrderTypeEnum type : OrderTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的订单类型代码: " + code);
    }
}
