package com.boxinggym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员卡定义实体
 */
@Data
@TableName("membership_card")
public class MembershipCard implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 卡分类: 1-团课期限卡, 2-团课次卡, 3-私教次卡
     */
    private Integer cardCategory;

    /**
     * 卡类型
     */
    private String cardType;

    /**
     * 有效期天数（期限卡）
     */
    private Integer durationDays;

    /**
     * 包含次数（次卡）
     */
    private Integer sessionCount;

    /**
     * 售价
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 激活期限天数
     */
    private Integer activationDeadlineDays;

    /**
     * 激活后有效期（次卡）
     */
    private Integer validityDaysAfterActivation;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态: 0-停售, 1-在售
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
