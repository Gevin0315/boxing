package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单号响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单号响应")
public class OrderNoVO {

    @Schema(description = "订单号", example = "ORD20240101120000001")
    private String orderNo;
}
