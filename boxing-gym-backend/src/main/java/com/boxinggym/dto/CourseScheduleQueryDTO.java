package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 排课查询条件 DTO
 */
@Data
@Schema(description = "排课查询参数")
public class CourseScheduleQueryDTO {

    @Schema(description = "当前页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "状态: 0-未开始, 1-进行中, 2-已结束, 3-已取消")
    private Integer status;

    @Schema(description = "开始日期 (yyyy-MM-dd)")
    private String startDate;

    @Schema(description = "结束日期 (yyyy-MM-dd)")
    private String endDate;
}
