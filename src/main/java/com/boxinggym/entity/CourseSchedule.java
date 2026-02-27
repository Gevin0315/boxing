package com.boxinggym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.boxinggym.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 排课记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_schedule")
public class CourseSchedule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程ID（关联course表）
     */
    private Long courseId;

    /**
     * 教练ID（关联sys_user表）
     */
    private Long coachId;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 最大人数
     */
    private Integer maxPeople;

    /**
     * 已约人数
     */
    private Integer currentPeople;

    /**
     * 状态: 0-未开始, 1-进行中, 2-已结束, 3-已取消
     */
    private Integer status;
}
