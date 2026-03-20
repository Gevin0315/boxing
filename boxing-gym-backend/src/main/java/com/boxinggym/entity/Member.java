package com.boxinggym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.boxinggym.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 会员实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member")
public class Member extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会员姓名
     */
    private String name;

    /**
     * 手机号（唯一）
     */
    private String phone;

    /**
     * 性别: 0-女, 1-男
     */
    private Integer gender;

    /**
     * 储值余额（元）
     */
    private BigDecimal balance;

    /**
     * 剩余课时数
     */
    private Integer remainingCourseCount;

    /**
     * 会员卡有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cardExpireDate;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 状态: 0-禁用, 1-正常
     */
    private Integer status;

    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 地址
     */
    private String address;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    private String emergencyPhone;

    /**
     * 会员等级: 1-普通 2-银卡 3-金卡 4-钻石
     */
    private String membershipLevel;

    /**
     * 备注
     */
    private String remark;
}
