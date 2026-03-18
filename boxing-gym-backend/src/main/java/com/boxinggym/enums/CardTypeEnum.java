package com.boxinggym.enums;

import lombok.Getter;

/**
 * 卡类型枚举
 */
@Getter
public enum CardTypeEnum {

    /**
     * 月卡 - 团课期限卡，30天有效
     */
    MONTHLY("MONTHLY", "月卡", CardCategoryEnum.GROUP_TIME, 30, null),

    /**
     * 季卡 - 团课期限卡，90天有效
     */
    QUARTERLY("QUARTERLY", "季卡", CardCategoryEnum.GROUP_TIME, 90, null),

    /**
     * 年卡 - 团课期限卡，365天有效
     */
    YEARLY("YEARLY", "年卡", CardCategoryEnum.GROUP_TIME, 365, null),

    /**
     * 单次日卡 - 团课次卡，1次，当日有效
     */
    SESSION_GROUP_1("SESSION_GROUP_1", "单次日卡", CardCategoryEnum.GROUP_SESSION, null, 1),

    /**
     * 10次团课卡 - 团课次卡，10次
     */
    SESSION_GROUP_10("SESSION_GROUP_10", "10次团课卡", CardCategoryEnum.GROUP_SESSION, null, 10),

    /**
     * 20次团课卡 - 团课次卡，20次
     */
    SESSION_GROUP_20("SESSION_GROUP_20", "20次团课卡", CardCategoryEnum.GROUP_SESSION, null, 20),

    /**
     * 5次私教卡 - 私教次卡，5次
     */
    SESSION_PRIVATE_5("SESSION_PRIVATE_5", "5次私教卡", CardCategoryEnum.PRIVATE_SESSION, null, 5),

    /**
     * 10次私教卡 - 私教次卡，10次
     */
    SESSION_PRIVATE_10("SESSION_PRIVATE_10", "10次私教卡", CardCategoryEnum.PRIVATE_SESSION, null, 10),

    /**
     * 20次私教卡 - 私教次卡，20次
     */
    SESSION_PRIVATE_20("SESSION_PRIVATE_20", "20次私教卡", CardCategoryEnum.PRIVATE_SESSION, null, 20);

    private final String code;
    private final String description;
    private final CardCategoryEnum category;
    private final Integer durationDays;
    private final Integer sessionCount;

    CardTypeEnum(String code, String description, CardCategoryEnum category, Integer durationDays, Integer sessionCount) {
        this.code = code;
        this.description = description;
        this.category = category;
        this.durationDays = durationDays;
        this.sessionCount = sessionCount;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return 枚举
     */
    public static CardTypeEnum fromCode(String code) {
        for (CardTypeEnum type : CardTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的卡类型代码: " + code);
    }

    /**
     * 判断是否为期限卡
     *
     * @return 是否为期限卡
     */
    public boolean isTimeCard() {
        return this.category == CardCategoryEnum.GROUP_TIME;
    }

    /**
     * 判断是否为团课卡（期限卡或次卡）
     *
     * @return 是否为团课卡
     */
    public boolean isGroupCard() {
        return this.category == CardCategoryEnum.GROUP_TIME || this.category == CardCategoryEnum.GROUP_SESSION;
    }

    /**
     * 判断是否为私教卡
     *
     * @return 是否为私教卡
     */
    public boolean isPrivateCard() {
        return this.category == CardCategoryEnum.PRIVATE_SESSION;
    }
}
