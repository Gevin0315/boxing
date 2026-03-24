package com.boxinggym.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 待支付订单响应VO
 */
@Data
@Schema(description = "待支付订单响应VO")
public class PendingOrderVO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "卡片定义ID")
    private Long cardId;

    @Schema(description = "卡片名称")
    private String cardName;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "支付方式")
    private Integer payMethod;

    @Schema(description = "支付方式描述")
    private String payMethodDesc;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
