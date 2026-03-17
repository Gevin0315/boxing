package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员查询 DTO
 */
@Data
@Schema(description = "会员查询参数")
public class MemberQueryDTO {

    @Schema(description = "会员姓名（模糊查询）")
    private String name;

    @Schema(description = "手机号（模糊查询）")
    private String phone;

    @Schema(description = "状态: 0-禁用, 1-正常")
    private Integer status;

    @Schema(description = "当前页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;
}
