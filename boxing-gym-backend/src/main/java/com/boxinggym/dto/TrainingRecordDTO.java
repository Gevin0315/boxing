package com.boxinggym.dto;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

/**
 * 上课签到记录 DTO（包含关联信息）
 */
@Data
@FieldNameConstants
public class TrainingRecordDTO {
    private Long id;
    private String recordNo;
    private Long memberId;
    private String memberNo;      // 会员编号
    private String memberName;    // 会员姓名
    private Long scheduleId;
    private Long coachId;
    private String coachName;    // 教练姓名
    private Long courseId;
    private String courseName;    // 课程名称
    private String courseType;    // 课程类型标签
    private String classroom;     // 教室
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;
    private Integer status;
    private String statusLabel;   // 状态标签
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
