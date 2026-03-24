package com.boxinggym.enums;

import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
public enum PaymentStatusEnum {

    /**
     * 待支付
     */
    PENDING(0, "待支付"),

    /**
     * 已支付
     */
    PAID(1, "已支付"),

    /**
     * 部分支付
     */
    PARTIAL(2, "部分支付"),

    /**
     * 已取消
     */
    CANCELLED(3, "已取消");

    private final Integer code;
    private final String description;

    PaymentStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static PaymentStatusEnum fromCode(Integer code) {
        for (PaymentStatusEnum status : PaymentStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的支付状态代码: " + code);
    }
}
