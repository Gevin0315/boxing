package com.boxinggym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员卡使用记录实体
 */
@Data
@TableName("card_usage_record")
public class CardUsageRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会员卡ID
     */
    private Long memberCardId;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 使用类型: 1-激活, 2-签到, 3-过期, 4-作废
     */
    private Integer usageType;

    /**
     * 排课ID
     */
    private Long scheduleId;

    /**
     * 训练记录ID
     */
    private Long trainingRecordId;

    /**
     * 操作前次数
     */
    private Integer sessionsBefore;

    /**
     * 操作后次数
     */
    private Integer sessionsAfter;

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
     * 操作人ID
     */
    private Long createBy;
}
