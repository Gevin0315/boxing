package com.boxinggym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 排课响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "排课信息响应")
public class CourseScheduleVO {

    @Schema(description = "排课ID")
    private Long id;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程类型: 1-团课, 2-私教")
    private Integer courseType;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "教练姓名")
    private String coachName;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "最大人数")
    private Integer maxPeople;

    @Schema(description = "已约人数")
    private Integer currentPeople;

    @Schema(description = "剩余名额")
    private Integer remainingPeople;

    @Schema(description = "状态: 0-未开始, 1-进行中, 2-已结束, 3-已取消")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;
}
