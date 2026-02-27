package com.boxinggym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.boxinggym.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 教练扩展信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_coach_profile")
public class SysCoachProfile extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联系统用户ID
     */
    private Long userId;

    /**
     * 擅长流派: 拳击/泰拳/柔术等
     */
    private String specialty;

    /**
     * 教练简介
     */
    private String intro;

    /**
     * 头像图片URL
     */
    private String imgUrl;

    /**
     * 课时费（元/小时）
     */
    private BigDecimal hourlyRate;
}
