package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量删除请求 DTO
 */
@Data
@Schema(description = "批量删除请求")
public class BatchDeleteDTO {

    @NotEmpty(message = "ID列表不能为空")
    @Schema(description = "要删除的ID列表", example = "[1, 2, 3]")
    private List<Long> ids;
}
