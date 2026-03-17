package com.boxinggym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员信息响应")
public class MemberVO {

    @Schema(description = "会员ID")
    private Long id;

    @Schema(description = "会员编号")
    private String memberNo;

    @Schema(description = "会员姓名")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "性别: 0-女, 1-男")
    private Integer gender;

    @Schema(description = "性别描述")
    private String genderDesc;

    @Schema(description = "储值余额（元）")
    private BigDecimal balance;

    @Schema(description = "剩余课时数")
    private Integer remainingCourseCount;

    @Schema(description = "会员卡有效期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cardExpireDate;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "状态: 0-禁用, 1-正常")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
