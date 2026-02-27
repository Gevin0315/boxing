package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 充值请求 DTO
 */
@Data
@Schema(description = "会员充值请求")
public class RechargeDTO {

    @NotNull(message = "会员ID不能为空")
    @Schema(description = "会员ID")
    private Long memberId;

    @NotNull(message = "充值金额不能为空")
    @Positive(message = "充值金额必须大于0")
    @Schema(description = "充值金额（元）")
    private BigDecimal amount;

    @NotNull(message = "支付方式不能为空")
    @Schema(description = "支付方式: 1-微信, 2-支付宝, 3-现金, 4-刷卡")
    private Integer payMethod;

    @Schema(description = "备注")
    private String remark;
}
