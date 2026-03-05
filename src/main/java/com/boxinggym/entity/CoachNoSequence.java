package com.boxinggym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.boxinggym.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 教练号序列实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("coach_no_sequence")
public class CoachNoSequence extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 教练号
     */
    private String coachNo;

    /**
     * 是否已使用: 0-未使用, 1-已使用
     */
    private Integer isUsed;

    /**
     * 使用时间
     */
    private LocalDateTime usedTime;

    /**
     * 关联用户ID
     */
    private Long userId;
}
