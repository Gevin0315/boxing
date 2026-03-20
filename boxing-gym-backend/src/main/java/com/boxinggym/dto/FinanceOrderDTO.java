package com.boxinggym.dto;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务订单 DTO（包含关联信息）
 */
@Data
@FieldNameConstants
public class FinanceOrderDTO {
    private Long id;
    private String orderNo;
    private Integer type;        // 1-充值 2-退款 3-课程消费
    private Long memberId;
    private String memberName;    // 会员姓名
    private Long courseId;
    private String courseName;    // 课程名称
    private String typeLabel; // 订单类型标签
    private String courseType; // 课程类型标签
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private Integer payMethod;
    private String payMethodLabel; // 支付方式标签
    private Integer paymentStatus;
    private String paymentStatusLabel; // 支付状态标签
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
