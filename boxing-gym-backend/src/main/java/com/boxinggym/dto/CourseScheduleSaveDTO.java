package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 排课保存 DTO
 */
@Data
@Schema(description = "排课保存请求")
public class CourseScheduleSaveDTO {

    @Schema(description = "排课ID（修改时必传）")
    private Long id;

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID")
    private Long courseId;

    @NotNull(message = "教练ID不能为空")
    @Schema(description = "教练ID")
    private Long coachId;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @NotNull(message = "最大人数不能为空")
    @Schema(description = "最大人数")
    private Integer maxPeople;

    @Schema(description = "状态: 0-未开始, 1-进行中, 2-已结束, 3-已取消")
    private Integer status = 0;
}
