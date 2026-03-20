package com.boxinggym.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 签到记录视图对象
 */
@Data
@Schema(description = "签到记录信息")
public class TrainingRecordVO {
    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "记录编号")
    private String recordNo;

    @Schema(description = "排课ID")
    private Long scheduleId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "教练姓名")
    private String coachName;

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "会员编号")
    private String memberNo;

    @Schema(description = "会员姓名")
    private String memberName;

    @Schema(description = "签到时间")
    private LocalDateTime checkinTime;

    @Schema(description = "签退时间")
    private LocalDateTime checkoutTime;

    @Schema(description = "状态: 0-已预约, 1-已签到, 2-已取消, 3-旷课")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
