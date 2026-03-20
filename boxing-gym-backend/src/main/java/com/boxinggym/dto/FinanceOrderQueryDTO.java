package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 财务订单查询条件
 */
@Data
@Schema(description = "财务订单查询参数")
public class FinanceOrderQueryDTO {

    @Schema(description = "当前页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单类型: 1-充值, 2-退款, 3-课程消费")
    private Integer type;

    @Schema(description = "会员编号")
    private String memberNo;

    @Schema(description = "会员姓名")
    private String memberName;

    @Schema(description = "支付状态: 0-未支付, 1-已支付, 2-部分支付")
    private Integer paymentStatus;
}
