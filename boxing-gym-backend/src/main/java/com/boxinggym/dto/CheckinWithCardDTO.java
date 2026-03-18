package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 使用卡片签到请求DTO
 */
@Data
@Schema(description = "使用卡片签到请求DTO")
public class CheckinWithCardDTO {

    @Schema(description = "会员卡ID（可选，不传则自动选择）")
    private Long memberCardId;

    @Schema(description = "会员ID")
    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    @Schema(description = "排课ID")
    @NotNull(message = "排课ID不能为空")
    private Long scheduleId;

    @Schema(description = "备注")
    private String remark;
}
