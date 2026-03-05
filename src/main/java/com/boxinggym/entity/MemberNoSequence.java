package com.boxinggym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.boxinggym.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 会员号序列实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_no_sequence")
public class MemberNoSequence extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会员号
     */
    private String memberNo;

    /**
     * 是否已使用: 0-未使用, 1-已使用
     */
    private Integer isUsed;

    /**
     * 使用时间
     */
    private LocalDateTime usedTime;

    /**
     * 关联会员ID
     */
    private Long memberId;
}
