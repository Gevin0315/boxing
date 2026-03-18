package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 激活会员卡请求DTO
 */
@Data
@Schema(description = "激活会员卡请求DTO")
public class ActivateCardDTO {

    @Schema(description = "会员卡ID")
    @NotNull(message = "会员卡ID不能为空")
    private Long memberCardId;

    @Schema(description = "备注")
    private String remark;
}
