package com.boxinggym.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程视图对象
 */
@Data
@Schema(description = "课程信息")
public class CourseVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程类型: 1-团课, 2-私教")
    private Integer courseType;

    @Schema(description = "课程分类")
    private String category;

    @Schema(description = "课程难度")
    private String level;

    @Schema(description = "课程时长（分钟）")
    private Integer duration;

    @Schema(description = "最大人数")
    private Integer maxCapacity;

    @Schema(description = "课程价格")
    private BigDecimal price;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "教练名称")
    private String coachName;

    @Schema(description = "封面图片URL")
    private String imageUrl;

    @Schema(description = "状态: 0-停用, 1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
