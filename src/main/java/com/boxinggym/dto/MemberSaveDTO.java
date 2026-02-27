package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 会员保存 DTO
 */
@Data
@Schema(description = "会员保存请求")
public class MemberSaveDTO {

    @Schema(description = "会员ID（修改时必传）")
    private Long id;

    @NotBlank(message = "会员姓名不能为空")
    @Schema(description = "会员姓名")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @NotNull(message = "性别不能为空")
    @Schema(description = "性别: 0-女, 1-男")
    private Integer gender;

    @Schema(description = "储值余额（元）")
    private BigDecimal balance = BigDecimal.ZERO;

    @Schema(description = "剩余课时数")
    private Integer remainingCourseCount = 0;

    @Schema(description = "会员卡有效期")
    private LocalDate cardExpireDate;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "状态: 0-禁用, 1-正常")
    private Integer status = 1;
}
