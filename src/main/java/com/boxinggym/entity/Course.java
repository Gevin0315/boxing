package com.boxinggym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.boxinggym.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 课程定义实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class Course extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程名称（如: 泰拳入门）
     */
    private String name;

    /**
     * 课程代码
     */
    private String code;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverImg;

    /**
     * 课程类型: 1-团课, 2-私教
     */
    private Integer type;

    /**
     * 课程时长（分钟）
     */
    private Integer duration;

    /**
     * 状态: 0-停用, 1-启用
     */
    private Integer status;

    /**
     * 排序（越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 课程分类: boxing, muay_thai, kickboxing, fitness, self_defense, kids
     */
    private String category;

    /**
     * 课程难度: beginner, intermediate, advanced
     */
    private String level;

    /**
     * 最大人数
     */
    private Integer maxCapacity;

    /**
     * 课程价格
     */
    private BigDecimal price;

    /**
     * 教练ID
     */
    private Long coachId;
}
