package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 课程查询条件 DTO
 */
@Data
@Schema(description = "课程查询参数")
public class CourseQueryDTO {

    @Schema(description = "当前页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;

    @Schema(description = "课程名称（模糊查询）")
    private String name;

    @Schema(description = "课程类型: 1-团课, 2-私教")
    private Integer type;

    @Schema(description = "课程分类")
    private String category;

    @Schema(description = "课程难度")
    private String level;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "状态: 0-停用, 1-启用")
    private Integer status;
}
