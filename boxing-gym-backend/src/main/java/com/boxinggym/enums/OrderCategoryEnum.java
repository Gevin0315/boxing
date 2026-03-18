package com.boxinggym.enums;

import lombok.Getter;

/**
 * 订单分类枚举
 */
@Getter
public enum OrderCategoryEnum {

    /**
     * 储值充值
     */
    RECHARGE(1, "储值充值"),

    /**
     * 购卡
     */
    PURCHASE_CARD(2, "购卡"),

    /**
     * 课程消费
     */
    COURSE_CONSUME(3, "课程消费");

    private final Integer code;
    private final String description;

    OrderCategoryEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return 枚举
     */
    public static OrderCategoryEnum fromCode(Integer code) {
        for (OrderCategoryEnum category : OrderCategoryEnum.values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("无效的订单分类代码: " + code);
    }
}
