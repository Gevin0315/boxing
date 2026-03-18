package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 作废会员卡请求DTO
 */
@Data
@Schema(description = "作废会员卡请求DTO")
public class VoidCardDTO {

    @Schema(description = "会员卡ID")
    @NotNull(message = "会员卡ID不能为空")
    private Long memberCardId;

    @Schema(description = "作废原因")
    @NotBlank(message = "作废原因不能为空")
    private String reason;
}
