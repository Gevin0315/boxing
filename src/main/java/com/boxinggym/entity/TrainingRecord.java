package com.boxinggym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.boxinggym.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 上课签到记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("training_record")
public class TrainingRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 记录编号
     */
    private String recordNo;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 排课记录ID
     */
    private Long scheduleId;

    /**
     * 教练ID（冗余，方便统计）
     */
    private Long coachId;

    /**
     * 签到时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkinTime;

    /**
     * 状态: 0-已预约, 1-已签到, 2-已取消, 3-旷课
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
