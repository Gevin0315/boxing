-- ============================================
-- Boxing Gym Management System
-- Database Initialization Script
-- MySQL 8.0+
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `boxing_gym`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `boxing_gym`;

-- ============================================
-- 4.1 系统与权限模块
-- ============================================

-- sys_user (管理员/员工表)
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `role` VARCHAR(20) NOT NULL COMMENT '角色: ROLE_ADMIN(超级管理员), ROLE_RECEPTION(前台), ROLE_COACH(教练)',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表（馆主、前台、教练）';

-- sys_coach_profile (教练扩展信息表)
CREATE TABLE `sys_coach_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '关联系统用户ID',
    `specialty` VARCHAR(50) COMMENT '擅长流派: 拳击/泰拳/柔术等',
    `intro` VARCHAR(500) COMMENT '教练简介',
    `img_url` VARCHAR(255) COMMENT '头像图片URL',
    `hourly_rate` DECIMAL(10, 2) COMMENT '课时费（元/小时）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练扩展信息表';

-- ============================================
-- 4.2 会员中心模块
-- ============================================

-- member (会员表)
CREATE TABLE `member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `member_no` VARCHAR(20) NOT NULL COMMENT '会员编号（如: M000001）',
    `name` VARCHAR(50) NOT NULL COMMENT '会员姓名',
    `phone` VARCHAR(11) NOT NULL COMMENT '手机号（唯一）',
    `gender` TINYINT NOT NULL DEFAULT 1 COMMENT '性别: 0-女, 1-男',
    `balance` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '储值余额（元）',
    `remaining_course_count` INT NOT NULL DEFAULT 0 COMMENT '剩余课时数',
    `card_expire_date` DATE COMMENT '会员卡有效期',
    `avatar_url` VARCHAR(255) COMMENT '头像URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_member_no` (`member_no`),
    KEY `idx_card_expire_date` (`card_expire_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员信息表';

-- ============================================
-- 4.3 课程与排课模块
-- ============================================

-- course (课程定义表)
CREATE TABLE `course` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '课程名称（如: 泰拳入门）',
    `code` VARCHAR(20) NOT NULL COMMENT '课程代码',
    `description` VARCHAR(500) COMMENT '课程描述',
    `cover_img` VARCHAR(255) COMMENT '封面图片URL',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '课程类型: 1-团课, 2-私教',
    `duration` INT NOT NULL DEFAULT 60 COMMENT '课程时长（分钟）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-停用, 1-启用',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程定义表';

-- course_schedule (排课记录表)
CREATE TABLE `course_schedule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID（关联course表）',
    `coach_id` BIGINT NOT NULL COMMENT '教练ID（关联sys_user表）',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `max_people` INT NOT NULL DEFAULT 20 COMMENT '最大人数',
    `current_people` INT NOT NULL DEFAULT 0 COMMENT '已约人数',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-未开始, 1-进行中, 2-已结束, 3-已取消',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='排课记录表';

-- ============================================
-- 4.4 订单与记录模块
-- ============================================

-- finance_order (充值/交易订单表)
CREATE TABLE `finance_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `amount` DECIMAL(10, 2) NOT NULL COMMENT '金额（正数为充值，负数为退款）',
    `type` TINYINT NOT NULL COMMENT '类型: 1-充值, 2-退款, 3-课程消费',
    `pay_method` TINYINT COMMENT '支付方式: 1-微信, 2-支付宝, 3-现金, 4-刷卡, 5-系统扣减',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT COMMENT '操作人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_type` (`type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='财务订单表';

-- training_record (上课/签到记录表)
CREATE TABLE `training_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `record_no` VARCHAR(32) NOT NULL COMMENT '记录编号',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `schedule_id` BIGINT NOT NULL COMMENT '排课记录ID',
    `coach_id` BIGINT NOT NULL COMMENT '教练ID（冗余，方便统计）',
    `checkin_time` DATETIME COMMENT '签到时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-已预约, 1-已签到, 2-已取消, 3-旷课',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_record_no` (`record_no`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_schedule_id` (`schedule_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='上课签到记录表';

-- ============================================
-- 初始化数据
-- ============================================

-- 初始化超级管理员用户 (密码: admin123，BCrypt加密后)
-- 注意：实际生产环境请修改默认密码
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES ('admin', '$2a$10$oeTbo6W.fvhSHNzI1yTLf.DCkXqDPNJBpoMfg/4QUQWCpC7TjRiFm', '系统管理员', 'ROLE_ADMIN', 1);

-- 初始化示例课程数据
INSERT INTO `course` (`code`, `name`, `description`, `type`, `duration`) VALUES
('C001', '拳击基础', '拳击基础课程，适合零基础学员', 1, 60),
('C002', '泰拳入门', '泰拳基本动作与实战技巧', 1, 60),
('C003', '巴西柔术', '巴西柔术地面缠斗技巧', 1, 90),
('C004', '拳击私教', '一对一拳击私教课程', 2, 60),
('C005', '泰拳私教', '一对一泰拳私教课程', 2, 60);
