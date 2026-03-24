package com.boxinggym.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 支付结果响应VO
 */
@Data
@Schema(description = "支付结果响应VO")
public class PaymentResultVO {

    @Schema(description = "会员卡ID")
    private Long cardId;

    @Schema(description = "状态：completed-已完成, cancelled-已取消")
    private String status;
}
