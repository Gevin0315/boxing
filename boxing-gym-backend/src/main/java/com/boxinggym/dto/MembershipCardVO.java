package com.boxinggym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员卡定义展示VO
 */
@Data
@Schema(description = "会员卡定义展示VO")
public class MembershipCardVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "卡编码")
    private String cardCode;

    @Schema(description = "卡名称")
    private String cardName;

    @Schema(description = "卡分类: 1-团课期限卡, 2-团课次卡, 3-私教次卡")
    private Integer cardCategory;

    @Schema(description = "卡分类描述")
    private String cardCategoryDesc;

    @Schema(description = "卡类型")
    private String cardType;

    @Schema(description = "卡类型描述")
    private String cardTypeDesc;

    @Schema(description = "有效期天数（期限卡）")
    private Integer durationDays;

    @Schema(description = "包含次数（次卡）")
    private Integer sessionCount;

    @Schema(description = "售价")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "激活期限天数")
    private Integer activationDeadlineDays;

    @Schema(description = "激活后有效期（次卡）")
    private Integer validityDaysAfterActivation;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态: 0-停售, 1-在售")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
