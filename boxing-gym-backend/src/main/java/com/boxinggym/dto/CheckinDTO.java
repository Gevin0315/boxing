package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 签到请求 DTO
 */
@Data
@Schema(description = "会员签到请求")
public class CheckinDTO {

    @NotNull(message = "会员ID不能为空")
    @Schema(description = "会员ID")
    private Long memberId;

    @NotNull(message = "排课ID不能为空")
    @Schema(description = "排课ID")
    private Long scheduleId;

    @Schema(description = "备注")
    private String remark;
}
