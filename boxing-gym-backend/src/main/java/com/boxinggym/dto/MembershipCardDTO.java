package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员卡定义DTO
 */
@Data
@Schema(description = "会员卡定义DTO")
public class MembershipCardDTO {

    @Schema(description = "卡编码")
    @NotBlank(message = "卡编码不能为空")
    private String cardCode;

    @Schema(description = "卡名称")
    @NotBlank(message = "卡名称不能为空")
    private String cardName;

    @Schema(description = "卡分类: 1-团课期限卡, 2-团课次卡, 3-私教次卡")
    @NotNull(message = "卡分类不能为空")
    private Integer cardCategory;

    @Schema(description = "卡类型")
    @NotBlank(message = "卡类型不能为空")
    private String cardType;

    @Schema(description = "有效期天数（期限卡）")
    @Min(value = 1, message = "有效期天数必须大于0")
    private Integer durationDays;

    @Schema(description = "包含次数（次卡）")
    @Min(value = 1, message = "包含次数必须大于0")
    private Integer sessionCount;

    @Schema(description = "售价")
    @NotNull(message = "售价不能为空")
    @Min(value = 0, message = "售价不能为负数")
    private BigDecimal price;

    @Schema(description = "原价")
    @Min(value = 0, message = "原价不能为负数")
    private BigDecimal originalPrice;

    @Schema(description = "激活期限天数")
    @Min(value = 1, message = "激活期限天数必须大于0")
    private Integer activationDeadlineDays;

    @Schema(description = "激活后有效期（次卡）")
    @Min(value = 1, message = "激活后有效期必须大于0")
    private Integer validityDaysAfterActivation;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态: 0-停售, 1-在售")
    private Integer status;

    @Schema(description = "排序")
    private Integer sortOrder;
}
