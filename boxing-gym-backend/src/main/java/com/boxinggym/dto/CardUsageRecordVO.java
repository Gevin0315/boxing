package com.boxinggym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 卡片使用记录展示VO
 */
@Data
@Schema(description = "卡片使用记录展示VO")
public class CardUsageRecordVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "记录编号")
    private String recordNo;

    @Schema(description = "会员卡ID")
    private Long memberCardId;

    @Schema(description = "会员卡号")
    private String cardNo;

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "会员姓名")
    private String memberName;

    @Schema(description = "使用类型: 1-激活, 2-签到, 3-过期, 4-作废")
    private Integer usageType;

    @Schema(description = "使用类型描述")
    private String usageTypeDesc;

    @Schema(description = "排课ID")
    private Long scheduleId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "训练记录ID")
    private Long trainingRecordId;

    @Schema(description = "操作前次数")
    private Integer sessionsBefore;

    @Schema(description = "操作后次数")
    private Integer sessionsAfter;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "操作人ID")
    private Long createBy;

    @Schema(description = "操作人姓名")
    private String operatorName;
}
