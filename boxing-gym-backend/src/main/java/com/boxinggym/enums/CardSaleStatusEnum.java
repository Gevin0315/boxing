package com.boxinggym.enums;

import lombok.Getter;

/**
 * 会员卡定义销售状态枚举
 */
@Getter
public enum CardSaleStatusEnum {

    /**
     * 停售
     */
    OFF_SHELF(0, "停售"),

    /**
     * 在售
     */
    ON_SHELF(1, "在售");

    private final Integer code;
    private final String description;

    CardSaleStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return 枚举
     */
    public static CardSaleStatusEnum fromCode(Integer code) {
        for (CardSaleStatusEnum status : CardSaleStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的销售状态代码: " + code);
    }

    /**
     * 验证代码是否有效
     *
     * @param code 代码
     * @return 是否有效
     */
    public static boolean isValidCode(Integer code) {
        for (CardSaleStatusEnum status : CardSaleStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
