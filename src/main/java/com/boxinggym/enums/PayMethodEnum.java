package com.boxinggym.enums;

import lombok.Getter;

/**
 * 支付方式枚举
 */
@Getter
public enum PayMethodEnum {

    /**
     * 微信
     */
    WECHAT(1, "微信"),

    /**
     * 支付宝
     */
    ALIPAY(2, "支付宝"),

    /**
     * 现金
     */
    CASH(3, "现金"),

    /**
     * 刷卡
     */
    CARD(4, "刷卡"),

    /**
     * 系统扣减
     */
    SYSTEM(5, "系统扣减");

    private final Integer code;
    private final String description;

    PayMethodEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static PayMethodEnum fromCode(Integer code) {
        for (PayMethodEnum method : PayMethodEnum.values()) {
            if (method.getCode().equals(code)) {
                return method;
            }
        }
        throw new IllegalArgumentException("无效的支付方式代码: " + code);
    }
}
