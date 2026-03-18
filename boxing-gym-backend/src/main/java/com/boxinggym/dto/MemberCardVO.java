package com.boxinggym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员持卡记录展示VO
 */
@Data
@Schema(description = "会员持卡记录展示VO")
public class MemberCardVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "会员卡号")
    private String cardNo;

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "会员姓名")
    private String memberName;

    @Schema(description = "卡片定义ID")
    private Long cardId;

    @Schema(description = "卡名称")
    private String cardName;

    @Schema(description = "卡分类: 1-团课期限卡, 2-团课次卡, 3-私教次卡")
    private Integer cardCategory;

    @Schema(description = "卡分类描述")
    private String cardCategoryDesc;

    @Schema(description = "卡类型")
    private String cardType;

    @Schema(description = "卡类型描述")
    private String cardTypeDesc;

    @Schema(description = "状态: 0-未激活, 1-生效中, 2-已过期, 3-已作废")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "总次数")
    private Integer totalSessions;

    @Schema(description = "剩余次数")
    private Integer remainingSessions;

    @Schema(description = "购买时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime purchaseTime;

    @Schema(description = "激活截止日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate activationDeadline;

    @Schema(description = "激活时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activationTime;

    @Schema(description = "生效开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "失效日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    @Schema(description = "最后签到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastCheckinTime;

    @Schema(description = "关联订单ID")
    private Long orderId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "是否可以激活")
    private Boolean canBeActivated;

    @Schema(description = "是否可以使用签到")
    private Boolean canBeUsedForCheckin;

    @Schema(description = "剩余有效天数")
    private Integer remainingDays;
}
