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
    `phone` VARCHAR(11) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表（馆主、前台、教练）';

-- sys_coach_profile (教练扩展信息表)
CREATE TABLE `sys_coach_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '关联系统用户ID',
    `specialty` VARCHAR(50) COMMENT '擅长流派: 拳击/泰拳/柔术等',
    `intro` VARCHAR(500) COMMENT '教练简介',
    `img_url` VARCHAR(255) COMMENT '头像图片URL',
    `hourly_rate` DECIMAL(10, 2) COMMENT '课时费（元/小时）',
    `gender` TINYINT COMMENT '性别: 0-男, 1-女',
    `phone` VARCHAR(11) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `id_card` VARCHAR(18) COMMENT '身份证号',
    `birthday` DATE COMMENT '生日',
    `address` VARCHAR(255) COMMENT '地址',
    `level` TINYINT COMMENT '等级: 1-初级 2-中级 3-高级 4-资深',
    `hire_date` DATE COMMENT '入职日期',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_level` (`level`),
    KEY `idx_hire_date` (`hire_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练扩展信息表';

-- ============================================
-- 4.2 会员中心模块
-- ============================================

-- member (会员表)
CREATE TABLE `member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '会员姓名',
    `phone` VARCHAR(11) NOT NULL COMMENT '手机号（唯一）',
    `gender` TINYINT NOT NULL DEFAULT 1 COMMENT '性别: 0-女, 1-男',
    `balance` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '储值余额（元）',
    `remaining_course_count` INT NOT NULL DEFAULT 0 COMMENT '剩余课时数',
    `card_expire_date` DATE COMMENT '会员卡有效期',
    `avatar_url` VARCHAR(255) COMMENT '头像URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `birthday` DATE COMMENT '生日',
    `id_card` VARCHAR(18) COMMENT '身份证号',
    `address` VARCHAR(255) COMMENT '地址',
    `emergency_contact` VARCHAR(50) COMMENT '紧急联系人',
    `emergency_phone` VARCHAR(11) COMMENT '紧急联系电话',
    `membership_level` VARCHAR(20) COMMENT '会员等级: normal, silver, gold, diamond',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_card_expire_date` (`card_expire_date`),
    KEY `idx_membership_level` (`membership_level`),
    KEY `idx_status` (`status`)
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
    `category` VARCHAR(50) COMMENT '课程分类: boxing, muay_thai, kickboxing, fitness, self_defense, kids',
    `level` VARCHAR(20) COMMENT '课程难度: beginner, intermediate, advanced',
    `max_capacity` INT COMMENT '最大人数',
    `price` DECIMAL(10, 2) COMMENT '课程价格',
    `coach_id` BIGINT COMMENT '教练ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_type` (`type`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`)
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
    `classroom` VARCHAR(50) COMMENT '教室',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_end_time` (`end_time`),
    KEY `idx_status` (`status`),
    KEY `idx_classroom` (`classroom`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='排课记录表';

-- ============================================
-- 4.4 订单与记录模块
-- ============================================

-- finance_order (充值/交易订单表)
CREATE TABLE `finance_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `member_card_id` BIGINT COMMENT '关联会员卡ID',
    `amount` DECIMAL(10, 2) NOT NULL COMMENT '金额（正数为充值，负数为退款）',
    `type` TINYINT NOT NULL COMMENT '类型: 1-充值, 2-退款, 3-课程消费',
    `order_category` TINYINT DEFAULT 1 COMMENT '订单类别: 1-储值充值, 2-购卡, 3-课程消费',
    `pay_method` TINYINT COMMENT '支付方式: 1-微信, 2-支付宝, 3-现金, 4-刷卡, 5-系统扣减',
    `remark` VARCHAR(255) COMMENT '备注',
    `paid_amount` DECIMAL(10, 2) COMMENT '已付金额',
    `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态: 0-未支付, 1-已支付, 2-部分支付',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '操作人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_member_card_id` (`member_card_id`),
    KEY `idx_type` (`type`),
    KEY `idx_order_category` (`order_category`),
    KEY `idx_payment_status` (`payment_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='财务订单表';

-- training_record (上课/签到记录表)
CREATE TABLE `training_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `record_no` VARCHAR(32) NOT NULL COMMENT '记录编号',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `schedule_id` BIGINT NOT NULL COMMENT '排课记录ID',
    `coach_id` BIGINT NOT NULL COMMENT '教练ID（冗余，方便统计）',
    `member_card_id` BIGINT COMMENT '使用的会员卡ID',
    `card_type_used` VARCHAR(20) COMMENT '使用的卡类型',
    `checkin_time` DATETIME COMMENT '签到时间',
    `checkout_time` DATETIME COMMENT '签退时间',
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
    KEY `idx_member_card_id` (`member_card_id`),
    KEY `idx_status` (`status`),
    KEY `idx_checkin_time` (`checkin_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='上课签到记录表';

-- ============================================
-- 4.5 会员卡系统模块
-- ============================================

-- membership_card (卡片定义表)
CREATE TABLE `membership_card` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `card_name` VARCHAR(100) NOT NULL COMMENT '卡名称',
    `card_category` TINYINT NOT NULL COMMENT '卡分类: 1-团课期限卡, 2-团课次卡, 3-私教次卡',
    `card_type` VARCHAR(20) NOT NULL COMMENT '卡类型',
    `duration_days` INT COMMENT '有效期天数（期限卡）',
    `session_count` INT COMMENT '包含次数（次卡）',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '售价',
    `original_price` DECIMAL(10, 2) COMMENT '原价',
    `activation_deadline_days` INT DEFAULT 30 COMMENT '激活期限天数',
    `validity_days_after_activation` INT COMMENT '激活后有效期（次卡）',
    `description` VARCHAR(500) COMMENT '描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '0-停售, 1-在售',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_card_category` (`card_category`),
    KEY `idx_card_type` (`card_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员卡定义表';

-- member_card (会员持卡记录表)
CREATE TABLE `member_card` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `card_id` BIGINT NOT NULL COMMENT '卡片定义ID',
    `card_category` TINYINT NOT NULL COMMENT '卡分类',
    `card_type` VARCHAR(20) NOT NULL COMMENT '卡类型',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0-未激活, 1-生效中, 2-已过期, 3-已作废',
    `total_sessions` INT COMMENT '总次数',
    `remaining_sessions` INT COMMENT '剩余次数',
    `purchase_time` DATETIME NOT NULL COMMENT '购买时间',
    `activation_deadline` DATE COMMENT '激活截止日期',
    `activation_time` DATETIME COMMENT '激活时间',
    `start_date` DATE COMMENT '生效开始日期',
    `expire_date` DATE COMMENT '失效日期',
    `last_checkin_time` DATETIME COMMENT '最后签到时间',
    `order_id` BIGINT COMMENT '关联订单ID',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_card_id` (`card_id`),
    KEY `idx_status` (`status`),
    KEY `idx_expire_date` (`expire_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员持卡记录表';

-- card_usage_record (卡片使用记录表)
CREATE TABLE `card_usage_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `record_no` VARCHAR(32) NOT NULL COMMENT '记录编号',
    `member_card_id` BIGINT NOT NULL COMMENT '会员卡ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `usage_type` TINYINT NOT NULL COMMENT '1-激活, 2-签到, 3-过期, 4-作废',
    `schedule_id` BIGINT COMMENT '排课ID',
    `training_record_id` BIGINT COMMENT '训练记录ID',
    `sessions_before` INT COMMENT '操作前次数',
    `sessions_after` INT COMMENT '操作后次数',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT COMMENT '操作人ID',
    PRIMARY KEY (`id`),
    KEY `idx_member_card_id` (`member_card_id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_usage_type` (`usage_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员卡使用记录表';

-- ============================================
-- 初始化数据
-- ============================================

-- 初始化超级管理员用户 (密码: admin123，BCrypt加密后)
-- 注意：实际生产环境请修改默认密码
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`, `phone`, `email`, `remark`)
VALUES ('admin', '$2a$10$oeTbo6W.fvhSHNzI1yTLf.DCkXqDPNJBpoMfg/4QUQWCpC7TjRiFm', '系统管理员', 'ROLE_ADMIN', 1, '13800000000', 'admin@boxinggym.com', '超级管理员账号');

-- 初始化前台用户 (密码: reception123)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`, `phone`, `email`, `remark`)
VALUES ('reception', '$2a$10$oeTbo6W.fvhSHNzI1yTLf.DCkXqDPNJBpoMfg/4QUQWCpC7TjRiFm', '前台小美', 'ROLE_RECEPTION', 1, '13800000001', 'reception@boxinggym.com', '前台接待员');

-- 初始化教练用户 (密码: coach123)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`, `phone`, `email`, `remark`)
VALUES
('coach_zhang', '$2a$10$oeTbo6W.fvhSHNzI1yTLf.DCkXqDPNJBpoMfg/4QUQWCpC7TjRiFm', '张教练', 'ROLE_COACH', 1, '13800000002', 'zhang@boxinggym.com', '拳击教练'),
('coach_li', '$2a$10$oeTbo6W.fvhSHNzI1yTLf.DCkXqDPNJBpoMfg/4QUQWCpC7TjRiFm', '李教练', 'ROLE_COACH', 1, '13800000003', 'li@boxinggym.com', '泰拳教练'),
('coach_wang', '$2a$10$oeTbo6W.fvhSHNzI1yTLf.DCkXqDPNJBpoMfg/4QUQWCpC7TjRiFm', '王教练', 'ROLE_COACH', 1, '13800000004', 'wang@boxinggym.com', '柔术教练');

-- 初始化教练扩展信息
INSERT INTO `sys_coach_profile` (`user_id`, `specialty`, `intro`, `hourly_rate`, `gender`, `phone`, `email`, `id_card`, `birthday`, `address`, `level`, `hire_date`)
VALUES
(3, '拳击', '10年拳击教学经验，曾获全国拳击锦标赛冠军', 300.00, 0, '13800000002', 'zhang@boxinggym.com', '110101199001011234', '1990-01-01', '北京市朝阳区建国路88号', 4, '2020-01-15'),
(4, '泰拳', '泰国训练经历，专业泰拳教练认证', 280.00, 0, '13800000003', 'li@boxinggym.com', '110101199203052345', '1992-03-05', '北京市海淀区中关村大街1号', 3, '2021-03-20'),
(5, '巴西柔术', '巴西柔术紫带，MMA综合格斗教练', 320.00, 1, '13800000004', 'wang@boxinggym.com', '110101199508103456', '1995-08-10', '北京市西城区金融街10号', 3, '2021-06-10');

-- 初始化示例课程数据
INSERT INTO `course` (`code`, `name`, `description`, `type`, `duration`, `category`, `level`, `max_capacity`, `price`, `coach_id`, `sort_order`) VALUES
('C001', '拳击基础', '拳击基础课程，适合零基础学员，学习基本拳法和步法', 1, 60, 'boxing', 'beginner', 15, 99.00, 3, 1),
('C002', '泰拳入门', '泰拳基本动作与实战技巧，包含肘击、膝击等技术', 1, 60, 'muay_thai', 'beginner', 12, 109.00, 4, 2),
('C003', '巴西柔术', '巴西柔术地面缠斗技巧，学习 submissions 和防守技术', 1, 90, 'fitness', 'intermediate', 10, 129.00, 5, 3),
('C004', '拳击私教', '一对一拳击私教课程，根据学员水平定制训练计划', 2, 60, 'boxing', 'beginner', 1, 399.00, 3, 4),
('C005', '泰拳私教', '一对一泰拳私教课程，专业技术指导', 2, 60, 'muay_thai', 'intermediate', 1, 379.00, 4, 5),
('C006', '健身搏击', '结合搏击动作的有氧健身课程，燃烧卡路里', 1, 45, 'fitness', 'beginner', 20, 79.00, NULL, 6),
('C007', '少儿拳击', '面向6-12岁儿童的拳击启蒙课程', 1, 45, 'kids', 'beginner', 10, 89.00, 3, 7),
('C008', '女子防身术', '实用的女子防身技巧，提高自我保护能力', 1, 60, 'self_defense', 'beginner', 15, 99.00, 5, 8);

-- 初始化会员数据（移除 member_no 字段）
INSERT INTO `member` (`name`, `phone`, `gender`, `balance`, `remaining_course_count`, `card_expire_date`, `status`, `birthday`, `id_card`, `address`, `emergency_contact`, `emergency_phone`, `membership_level`, `remark`) VALUES
('张三', '13900000001', 1, 5000.00, 30, '2025-12-31', 1, '1990-05-15', '110101199005151234', '北京市朝阳区望京街道', '张父', '13900000002', 'gold', 'VIP会员'),
('李四', '13900000003', 1, 3000.00, 20, '2025-06-30', 1, '1992-08-20', '110101199208202345', '北京市海淀区中关村', '李母', '13900000004', 'silver', '普通会员'),
('王五', '13900000005', 1, 10000.00, 50, '2026-12-31', 1, '1988-12-10', '110101198812103456', '北京市西城区金融街', '王妻', '13900000006', 'diamond', '至尊会员'),
('赵六', '13900000007', 0, 2000.00, 15, '2025-03-15', 1, '1995-03-25', '110101199503254567', '北京市东城区王府井', '赵父', '13900000008', 'normal', '新会员'),
('钱七', '13900000009', 1, 8000.00, 40, '2025-09-30', 1, '1991-07-08', '110101199107085678', '北京市丰台区方庄', '钱妻', '13900000010', 'gold', '活跃会员');

-- 初始化排课数据（未来一周的课程）
INSERT INTO `course_schedule` (`course_id`, `coach_id`, `start_time`, `end_time`, `max_people`, `current_people`, `status`, `classroom`, `remark`) VALUES
-- 今天
(1, 3, DATE_ADD(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 3 HOUR), 15, 8, 0, 'A教室', '拳击基础课'),
(2, 4, DATE_ADD(NOW(), INTERVAL 4 HOUR), DATE_ADD(NOW(), INTERVAL 5 HOUR), 12, 5, 0, 'B教室', '泰拳入门课'),
-- 明天
(3, 5, DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 10 HOUR), DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 11.5 HOUR), 10, 3, 0, 'C教室', '巴西柔术'),
(6, 3, DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 14 HOUR), DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 14.75 HOUR), 20, 12, 0, 'A教室', '健身搏击'),
-- 后天
(1, 3, DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 10 HOUR), DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 11 HOUR), 15, 6, 0, 'A教室', '拳击基础'),
(7, 3, DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 15 HOUR), DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 15.75 HOUR), 10, 4, 0, 'D教室', '少儿拳击'),
-- 3天后
(8, 5, DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 19 HOUR), DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 20 HOUR), 15, 2, 0, 'B教室', '女子防身术'),
(4, 3, DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 14 HOUR), DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 15 HOUR), 1, 0, 0, '私教区', '拳击私教');

-- 初始化财务订单数据
INSERT INTO `finance_order` (`order_no`, `member_id`, `amount`, `type`, `order_category`, `pay_method`, `remark`, `paid_amount`, `payment_status`, `create_by`) VALUES
('FO202401010001', 1, 5000.00, 1, 1, 1, '会员充值', 5000.00, 1, 1),
('FO202401020001', 2, 3000.00, 1, 1, 2, '会员充值', 3000.00, 1, 1),
('FO202401030001', 3, 10000.00, 1, 1, 3, '会员充值-钻石卡', 10000.00, 1, 1),
('FO202401050001', 4, 2000.00, 1, 1, 4, '会员充值', 2000.00, 1, 2),
('FO202401060001', 5, 8000.00, 1, 1, 1, '会员充值-金卡', 8000.00, 1, 1),
('FO202402010001', 1, -99.00, 3, 3, 5, '课程消费-拳击基础', 99.00, 1, 1),
('FO202402020001', 3, -129.00, 3, 3, 5, '课程消费-巴西柔术', 129.00, 1, 1);

-- 初始化训练记录数据
INSERT INTO `training_record` (`record_no`, `member_id`, `schedule_id`, `coach_id`, `checkin_time`, `checkout_time`, `status`, `remark`, `create_by`) VALUES
('TR202402010001', 1, 1, 3, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 1, '正常签到', 2),
('TR202402010002', 2, 1, 3, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 1, '正常签到', 2),
('TR202402020001', 3, 3, 5, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 1, '正常签到', 2),
('TR202402030001', 1, 4, 3, NULL, NULL, 0, '已预约', 1),
('TR202402030002', 5, 4, 3, NULL, NULL, 0, '已预约', 1);

-- 初始化会员卡定义数据（移除 card_code 字段）
INSERT INTO `membership_card` (`card_name`, `card_category`, `card_type`, `duration_days`, `session_count`, `price`, `original_price`, `activation_deadline_days`, `validity_days_after_activation`, `description`, `status`, `sort_order`) VALUES
-- 团课期限卡
('团课月卡', 1, 'MONTHLY', 30, NULL, 299.00, 399.00, 30, NULL, '团课月卡，30天内不限次数参加团课', 1, 1),
('团课季卡', 1, 'QUARTERLY', 90, NULL, 699.00, 1197.00, 30, NULL, '团课季卡，90天内不限次数参加团课', 1, 2),
('团课年卡', 1, 'YEARLY', 365, NULL, 1999.00, 4788.00, 30, NULL, '团课年卡，365天内不限次数参加团课', 1, 3),
-- 团课次卡
('单次日卡', 2, 'SESSION_GROUP_1', NULL, 1, 79.00, 99.00, 30, 1, '单次团课卡，当日有效', 1, 4),
('10次团课卡', 2, 'SESSION_GROUP_10', NULL, 10, 699.00, 990.00, 30, 30, '10次团课卡，激活后30天内有效', 1, 5),
('20次团课卡', 2, 'SESSION_GROUP_20', NULL, 20, 1199.00, 1980.00, 30, 60, '20次团课卡，激活后60天内有效', 1, 6),
-- 私教次卡
('5次私教卡', 3, 'SESSION_PRIVATE_5', NULL, 5, 1799.00, 1995.00, 30, 90, '5次私教卡，激活后90天内有效', 1, 7),
('10次私教卡', 3, 'SESSION_PRIVATE_10', NULL, 10, 3299.00, 3990.00, 30, 180, '10次私教卡，激活后180天内有效', 1, 8),
('20次私教卡', 3, 'SESSION_PRIVATE_20', NULL, 20, 5999.00, 7980.00, 30, 365, '20次私教卡，激活后365天内有效', 1, 9);

-- 初始化会员持卡记录数据
INSERT INTO `member_card` (`member_id`, `card_id`, `card_category`, `card_type`, `status`, `total_sessions`, `remaining_sessions`, `purchase_time`, `activation_deadline`, `activation_time`, `start_date`, `expire_date`, `last_checkin_time`, `order_id`, `remark`) VALUES
-- 张三(会员ID=1)的卡
(1, 3, 1, 'YEARLY', 1, NULL, NULL, '2024-01-15 10:30:00', '2024-02-14', '2024-01-20 09:00:00', '2024-01-20', '2025-01-20', DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, '团课年卡-生效中'),
(1, 8, 3, 'SESSION_PRIVATE_10', 1, 10, 7, '2024-02-01 14:20:00', '2024-03-03', '2024-02-05 11:00:00', '2024-02-05', '2024-08-05', DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, '10次私教卡-生效中'),
-- 李四(会员ID=2)的卡
(2, 2, 1, 'QUARTERLY', 1, NULL, NULL, '2024-01-20 15:45:00', '2024-02-19', '2024-01-25 10:00:00', '2024-01-25', '2024-04-25', DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, '团课季卡-生效中'),
(2, 5, 2, 'SESSION_GROUP_10', 1, 10, 6, '2024-03-10 09:15:00', '2024-04-09', '2024-03-15 14:00:00', '2024-03-15', '2024-04-14', DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, '10次团课卡-生效中'),
-- 王五(会员ID=3)的卡
(3, 3, 1, 'YEARLY', 1, NULL, NULL, '2023-12-01 11:00:00', '2023-12-31', '2023-12-05 09:00:00', '2023-12-05', '2024-12-05', DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, '团课年卡-生效中'),
(3, 9, 3, 'SESSION_PRIVATE_20', 1, 20, 15, '2024-02-15 16:30:00', '2024-03-17', '2024-02-20 10:00:00', '2024-02-20', '2025-02-20', DATE_SUB(NOW(), INTERVAL 5 DAY), NULL, '20次私教卡-生效中'),
-- 赵六(会员ID=4)的卡
(4, 4, 2, 'SESSION_GROUP_1', 2, 1, 0, '2024-02-10 13:00:00', '2024-03-12', '2024-02-10 13:00:00', '2024-02-10', '2024-02-10', '2024-02-10 14:30:00', NULL, '单次日卡-已过期'),
(4, 1, 1, 'MONTHLY', 0, NULL, NULL, '2024-03-15 10:00:00', '2024-04-14', NULL, NULL, NULL, NULL, NULL, '团课月卡-未激活'),
-- 钱七(会员ID=5)的卡
(5, 2, 1, 'QUARTERLY', 1, NULL, NULL, '2024-01-25 09:30:00', '2024-02-24', '2024-01-28 11:00:00', '2024-01-28', '2024-04-28', DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, '团课季卡-生效中'),
(5, 6, 2, 'SESSION_GROUP_20', 1, 20, 12, '2024-02-20 14:00:00', '2024-03-21', '2024-02-25 09:00:00', '2024-02-25', '2024-04-25', DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, '20次团课卡-生效中'),
-- 已过期和已作废的卡
(1, 1, 1, 'MONTHLY', 2, NULL, NULL, '2023-10-01 10:00:00', '2023-10-31', '2023-10-05 09:00:00', '2023-10-05', '2023-11-05', '2023-11-03 18:00:00', NULL, '团课月卡-已过期'),
(2, 5, 2, 'SESSION_GROUP_10', 3, 10, 3, '2023-11-15 11:00:00', '2023-12-15', '2023-11-20 10:00:00', '2023-11-20', '2023-12-20', '2023-12-10 15:00:00', NULL, '10次团课卡-已作废(会员申请退款)');

-- 初始化卡片使用记录数据
INSERT INTO `card_usage_record` (`record_no`, `member_card_id`, `member_id`, `usage_type`, `schedule_id`, `training_record_id`, `sessions_before`, `sessions_after`, `remark`, `create_by`) VALUES
-- 张三的卡使用记录 (member_card_id=1, 年卡)
('UR20240120001', 1, 1, 1, NULL, NULL, NULL, NULL, '团课年卡激活', 2),
('UR20240125001', 1, 1, 2, 1, 1, NULL, NULL, '拳击基础课签到', 2),
('UR20240201001', 1, 1, 2, 3, 3, NULL, NULL, '巴西柔术课签到', 2),
-- 张三的私教卡使用记录 (member_card_id=2)
('UR20240205001', 2, 1, 1, NULL, NULL, 10, 10, '10次私教卡激活', 2),
('UR20240210001', 2, 1, 2, 8, NULL, 10, 9, '拳击私教签到', 2),
('UR20240215001', 2, 1, 2, 8, NULL, 9, 8, '拳击私教签到', 3),
('UR20240220001', 2, 1, 2, 8, NULL, 8, 7, '拳击私教签到', 3),
-- 李四的卡使用记录
('UR20240125002', 3, 2, 1, NULL, NULL, NULL, NULL, '团课季卡激活', 2),
('UR20240201002', 3, 2, 2, 1, 2, NULL, NULL, '拳击基础课签到', 2),
('UR20240205002', 3, 2, 2, 2, NULL, NULL, NULL, '泰拳入门课签到', 2),
('UR20240315001', 4, 2, 1, NULL, NULL, 10, 10, '10次团课卡激活', 2),
('UR20240318001', 4, 2, 2, 4, NULL, 10, 9, '健身搏击课签到', 2),
('UR20240320001', 4, 2, 2, 5, NULL, 9, 8, '拳击基础课签到', 2),
('UR20240322001', 4, 2, 2, 6, NULL, 8, 7, '泰拳课签到', 2),
('UR20240325001', 4, 2, 2, 7, NULL, 7, 6, '巴西柔术课签到', 2),
-- 王五的卡使用记录
('UR20231205001', 5, 3, 1, NULL, NULL, NULL, NULL, '团课年卡激活', 2),
('UR20240101001', 5, 3, 2, 1, NULL, NULL, NULL, '新年第一练-拳击基础', 2),
('UR20240105001', 5, 3, 2, 2, NULL, NULL, NULL, '泰拳课签到', 2),
('UR20240205002', 6, 3, 1, NULL, NULL, 20, 20, '20次私教卡激活', 2),
('UR20240210002', 6, 3, 2, 8, NULL, 20, 19, '泰拳私教签到', 4),
('UR20240215002', 6, 3, 2, 8, NULL, 19, 18, '泰拳私教签到', 4),
('UR20240220002', 6, 3, 2, 8, NULL, 18, 17, '泰拳私教签到', 4),
('UR20240225001', 6, 3, 2, 8, NULL, 17, 16, '泰拳私教签到', 4),
('UR20240301001', 6, 3, 2, 8, NULL, 16, 15, '泰拳私教签到', 4),
-- 赵六的卡使用记录
('UR20240210003', 7, 4, 1, NULL, NULL, 1, 1, '单次日卡激活并使用', 2),
('UR20240210004', 7, 4, 2, 5, NULL, 1, 0, '单次日卡签到', 2),
('UR20240215003', 7, 4, 3, NULL, NULL, 0, 0, '卡片过期', NULL),
-- 钱七的卡使用记录
('UR20240128001', 8, 5, 1, NULL, NULL, NULL, NULL, '团课季卡激活', 2),
('UR20240201003', 8, 5, 2, 1, NULL, NULL, NULL, '拳击基础课签到', 2),
('UR20240208001', 8, 5, 2, 2, NULL, NULL, NULL, '泰拳课签到', 2),
('UR20240225002', 9, 5, 1, NULL, NULL, 20, 20, '20次团课卡激活', 2),
('UR20240228001', 9, 5, 2, 3, NULL, 20, 19, '巴西柔术课签到', 2),
('UR20240301002', 9, 5, 2, 4, NULL, 19, 18, '健身搏击课签到', 2),
('UR20240305001', 9, 5, 2, 1, NULL, 18, 17, '拳击基础课签到', 2),
('UR20240308001', 9, 5, 2, 2, NULL, 17, 16, '泰拳课签到', 2),
('UR20240312001', 9, 5, 2, 6, NULL, 16, 15, '健身搏击课签到', 2),
('UR20240315002', 9, 5, 2, 7, NULL, 15, 14, '巴西柔术课签到', 2),
('UR20240318002', 9, 5, 2, 5, NULL, 14, 13, '拳击基础课签到', 2),
('UR20240320002', 9, 5, 2, 1, NULL, 13, 12, '拳击基础课签到', 2),
-- 已作废卡片的记录
('UR20231120001', 11, 2, 1, NULL, NULL, 10, 10, '10次团课卡激活', 2),
('UR20231125001', 11, 2, 2, NULL, NULL, 10, 9, '团课签到', 2),
('UR20231201001', 11, 2, 2, NULL, NULL, 9, 8, '团课签到', 2),
('UR20231205002', 11, 2, 2, NULL, NULL, 8, 7, '团课签到', 2),
('UR20231210001', 11, 2, 2, NULL, NULL, 7, 6, '团课签到', 2),
('UR20231215001', 11, 2, 2, NULL, NULL, 6, 5, '团课签到', 2),
('UR20231220001', 11, 2, 2, NULL, NULL, 5, 4, '团课签到', 2),
('UR20231225001', 11, 2, 2, NULL, NULL, 4, 3, '团课签到', 2),
('UR20231228001', 11, 2, 4, NULL, NULL, 3, 3, '会员申请退款-卡片作废', 1);
