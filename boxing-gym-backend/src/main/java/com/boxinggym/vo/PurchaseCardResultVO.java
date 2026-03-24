package com.boxinggym.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 购卡结果响应VO
 */
@Data
@Schema(description = "购卡结果响应VO")
public class PurchaseCardResultVO {

    @Schema(description = "会员卡ID（即时完成时有值）")
    private Long cardId;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "状态：completed-已完成, pending-待支付")
    private String status;
}
