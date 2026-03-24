package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 取消订单请求DTO
 */
@Data
@Schema(description = "取消订单请求DTO")
public class CancelOrderDTO {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "取消原因")
    private String reason;
}
