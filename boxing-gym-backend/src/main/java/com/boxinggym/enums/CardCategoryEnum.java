package com.boxinggym.enums;

import lombok.Getter;

/**
 * 卡分类枚举
 */
@Getter
public enum CardCategoryEnum {

    /**
     * 团课期限卡（月卡/季卡/年卡）
     */
    GROUP_TIME(1, "团课期限卡"),

    /**
     * 团课次卡
     */
    GROUP_SESSION(2, "团课次卡"),

    /**
     * 私教次卡
     */
    PRIVATE_SESSION(3, "私教次卡");

    private final Integer code;
    private final String description;

    CardCategoryEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return 枚举
     */
    public static CardCategoryEnum fromCode(Integer code) {
        for (CardCategoryEnum category : CardCategoryEnum.values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("无效的卡分类代码: " + code);
    }
}
