package com.boxinggym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务订单实体
 */
@Data
@TableName("finance_order")
public class FinanceOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 金额（正数为充值，负数为退款）
     */
    private BigDecimal amount;

    /**
     * 类型: 1-充值, 2-退款, 3-课程消费
     */
    private Integer type;

    /**
     * 支付方式: 1-微信, 2-支付宝, 3-现金, 4-刷卡, 5-系统扣减
     */
    private Integer payMethod;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
        /**
     * 创建时间
     */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 操作人ID
     */
    private Long createBy;

    /**
     * 已付金额
     */
    private BigDecimal paidAmount;

    /**
     * 支付状态: 0-未支付, 1-已支付, 2-部分支付
     */
    private Integer paymentStatus;
}
