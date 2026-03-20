package com.boxinggym.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务订单视图对象
 */
@Data
@Schema(description = "财务订单响应")
public class FinanceOrderVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "会员姓名")
    private String memberName;

    @Schema(description = "订单类型: 1-充值, 2-退款, 3-课程消费")
    private Integer type;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "已付金额")
    private BigDecimal paidAmount;

    @Schema(description = "支付方式: 1-微信, 2-支付宝, 3-现金, 4-刷卡, 5-系统扣减")
    private Integer payMethod;

    @Schema(description = "支付状态: 0-未支付, 1-已支付, 2-部分支付")
    private Integer paymentStatus;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
