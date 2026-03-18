package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 购卡请求DTO
 */
@Data
@Schema(description = "购卡请求DTO")
public class PurchaseCardDTO {

    @Schema(description = "会员ID")
    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    @Schema(description = "卡片定义ID")
    @NotNull(message = "卡片定义ID不能为空")
    private Long cardId;

    @Schema(description = "支付方式: 1-微信, 2-支付宝, 3-现金, 4-刷卡, 5-系统扣减")
    @NotNull(message = "支付方式不能为空")
    private Integer payMethod;

    @Schema(description = "实际支付金额（用于处理优惠）")
    @Min(value = 0, message = "支付金额不能为负数")
    private BigDecimal paidAmount;

    @Schema(description = "备注")
    private String remark;
}
