package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 签到记录查询条件
 */
@Data
@Schema(description = "签到记录查询参数")
public class TrainingRecordQueryDTO {
    @Schema(description = "当前页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "排课ID")
    private Long scheduleId;

    @Schema(description = "状态: 0-已预约, 1-已签到, 2-已取消, 3-旷课")
    private Integer status;
}
