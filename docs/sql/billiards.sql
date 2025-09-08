-- 创建数据库
CREATE DATABASE IF NOT EXISTS `billiards` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE `billiards`;

-- 创建用户表
CREATE TABLE `bls_user` (
                        `id` bigint(20) UNSIGNED AUTO_INCREMENT COMMENT '用户ID',
                        `openid` varchar(64) DEFAULT NULL COMMENT '微信OpenID',
                        `unionid` varchar(64) DEFAULT NULL COMMENT '微信UnionID',
                        `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称（微信昵称）',
                        `user_name` varchar(50) DEFAULT NULL COMMENT '用户名（用户自己设置）',
                        `avatar_url` varchar(255) DEFAULT NULL COMMENT '用户头像URL',
                        `gender` tinyint DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
                        `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
                        `is_member` tinyint DEFAULT 0 COMMENT '是否是会员 0-否 1-是',
                        `member_level` int DEFAULT NULL COMMENT '会员等级',
                        `member_expire` datetime DEFAULT NULL COMMENT '会员过期时间',
                        `points` int DEFAULT 0 COMMENT '积分',
                        `balance` DECIMAL(10,2) default 0 COMMENT '金额，精确到分',
                        `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
                        `status` tinyint DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
                        `create_time` datetime NOT NULL COMMENT '创建时间',
                        `update_time` datetime NOT NULL COMMENT '更新时间',
                        `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                        `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                        `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_openid` (`openid`),
                        UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


-- 创建门店表
CREATE TABLE `bls_store` (
                         `id` varchar(36) NOT NULL COMMENT '门店ID',
                         `merchant_id` varchar(36) NOT NULL COMMENT '所属商家ID',
                         `name` varchar(100) NOT NULL COMMENT '门店名称',
                         `cover_image` varchar(255) DEFAULT NULL COMMENT '门店封面图URL',
                         `province` varchar(50) DEFAULT NULL COMMENT '省份',
                         `city` varchar(50) DEFAULT NULL COMMENT '城市',
                         `district` varchar(50) DEFAULT NULL COMMENT '区县',
                         `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
                         `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度坐标',
                         `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度坐标',
                         `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
                         `business_hours` varchar(100) DEFAULT NULL COMMENT '营业时间',
                         `announcement` text DEFAULT NULL COMMENT '公告',
                         `status` tinyint DEFAULT 0 COMMENT '状态 0-正常 1-休息 2-停业',
                         `create_time` datetime NOT NULL COMMENT '创建时间',
                         `update_time` datetime NOT NULL COMMENT '更新时间',
                         `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                         `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                         `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                         PRIMARY KEY (`id`),
                         KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店表';

-- 创建价格规则表
CREATE TABLE `bls_price_rule` (
                              `id` varchar(36) NOT NULL COMMENT '规则ID',
                              `merchant_id` varchar(36) NOT NULL COMMENT '所属商家ID',
                              `name` varchar(100) NOT NULL COMMENT '规则名称',
                              `rule_type` tinyint NOT NULL COMMENT '规则类型 1-标准计费 2-阶梯计费',
                              `price_unit` decimal(10,2) DEFAULT NULL COMMENT '单价(元/分钟)',
                              `member_price` decimal(10,2) DEFAULT NULL COMMENT '会员价格(元/分钟)',
                              `member_discount` decimal(10,2) DEFAULT NULL COMMENT '会员折扣',
                              `min_minutes` int DEFAULT NULL COMMENT '最低消费时长(分钟)',
                              `max_price` decimal(10,2) DEFAULT NULL COMMENT '封顶价格',
                              `ladder_rules` text DEFAULT NULL COMMENT '阶梯计费规则JSON',
                              `default_type` int DEFAULT NULL COMMENT '默认适用的桌台类型',
                              `status` tinyint DEFAULT 0 COMMENT '状态 0-启用 1-停用',
                              `create_time` datetime NOT NULL COMMENT '创建时间',
                              `update_time` datetime NOT NULL COMMENT '更新时间',
                              `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                              `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                              `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                              PRIMARY KEY (`id`),
                              KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格规则表';

-- 创建桌台表
CREATE TABLE `bls_table` (
                                   `id` varchar(36) NOT NULL COMMENT '桌台ID',
                                   `store_id` varchar(36) NOT NULL COMMENT '所属门店ID',
                                   `table_number` VARCHAR(20) NOT NULL COMMENT '桌台编号',
                                   `table_prefix` VARCHAR(10) NOT NULL COMMENT '桌台号前缀',
                                   `table_numeric` INT NOT NULL COMMENT '桌台号',
                                   `table_type` tinyint DEFAULT 1 COMMENT '桌台类型 1-普通 2-专业 3-大师',
                                   `description` varchar(255) DEFAULT NULL COMMENT '桌台描述',
                                   `image` varchar(255) DEFAULT NULL COMMENT '桌台图片URL',
                                   `qrcode_url` varchar(255) DEFAULT NULL COMMENT '二维码URL',
                                   `price_rule_id` varchar(36) DEFAULT NULL COMMENT '计费规则ID',
                                   `status` tinyint DEFAULT 0 COMMENT '状态 0-空闲 1-使用中 2-维修中 3-锁定',
                                   `create_time` datetime NOT NULL COMMENT '创建时间',
                                   `update_time` datetime NOT NULL COMMENT '更新时间',
                                   `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                   `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                   `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                   `version` INT DEFAULT 0 COMMENT '版本id（每次状态的变更，版本id都会+1）',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_store_table_number` (`store_id`, `table_prefix`, `table_numeric`),
                                   KEY `idx_store_id` (`store_id`),
                                   KEY `idx_price_rule_id` (`price_rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='桌台表';

-- 创建订单表
CREATE TABLE `bls_order` (
                         `id` varchar(36) NOT NULL COMMENT '订单ID',
                         `order_no` varchar(32) NOT NULL COMMENT '订单编号',
                         `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                         `channel` varchar(36) NOT NULL COMMENT '下单渠道（APP/小程序/公众号/WEB/H5）',
                         `store_id` varchar(36) NOT NULL COMMENT '门店ID',
                         `store_name` varchar(100) DEFAULT NULL COMMENT '门店名称',
                         `table_id` varchar(36) NOT NULL COMMENT '桌台ID',
                         `table_number` varchar(10) NULL COMMENT '桌台号',
                         `price_rule_id` varchar(36) NOT NULL COMMENT '计费规则ID',
                         `start_time` datetime NOT NULL COMMENT '开始时间',
                         `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                         `duration` int DEFAULT NULL COMMENT '使用时长(分钟)',
                         `original_amount` decimal(10,2) DEFAULT NULL COMMENT '原始金额',
                         `discount_amount` decimal(10,2) DEFAULT 0.00 COMMENT '折扣金额',
                         `actual_amount` decimal(10,2) DEFAULT NULL COMMENT '实际金额',
                         `payment_status` tinyint DEFAULT 0 COMMENT '支付状态 0-未支付 1-已支付 2-退款中',
                         `status` tinyint DEFAULT 0 COMMENT '状态 0-进行中 1-已完成',
                         `create_time` datetime NOT NULL COMMENT '创建时间',
                         `update_time` datetime NOT NULL COMMENT '更新时间',
                         `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                         `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                         `remark` varchar(255) DEFAULT NULL COMMENT '备注',
                         `complete_flag` tinyint DEFAULT 1 COMMENT '完成标记 1-用户结束 2-管理员结束 3-超时结束',
                         `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_order_no` (`order_no`),
                         KEY `idx_user_id` (`user_id`),
                         KEY `idx_store_id` (`store_id`),
                         KEY `idx_table_id` (`table_id`),
                         KEY `idx_price_rule_id` (`price_rule_id`),
                         KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 创建桌台使用记录表
CREATE TABLE `bls_table_usage` (
    `id` varchar(36) NOT NULL COMMENT '记录ID',
    `store_id` varchar(36) NOT NULL COMMENT '门店ID',
    `table_id` varchar(36) NOT NULL COMMENT '桌台ID',
    `order_id` varchar(36) NOT NULL COMMENT '订单ID',
    `user_id` varchar(36) NOT NULL COMMENT '用户ID',
    `start_time` datetime NOT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `duration` int DEFAULT NULL COMMENT '使用时长(分钟)',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
    `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
    `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (`id`),
    KEY `idx_store_id` (`store_id`),
    KEY `idx_table_id` (`table_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_start_time` (`start_time`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='桌台使用记录表';

-- 创建会员变更记录表
CREATE TABLE `bls_member_change_log` (
    `id` varchar(36) NOT NULL COMMENT '记录ID',
    `user_id` varchar(36) NOT NULL COMMENT '用户ID',
    `store_id` varchar(36) NOT NULL COMMENT '门店ID',
    `change_type` varchar(20) NOT NULL COMMENT '变更类型 NEW-新增会员 RENEWAL-续费 UPGRADE-升级 EXPIRED-过期',
    `before_level` int DEFAULT NULL COMMENT '变更前等级',
    `after_level` int DEFAULT NULL COMMENT '变更后等级',
    `before_expire` datetime DEFAULT NULL COMMENT '变更前过期时间',
    `after_expire` datetime DEFAULT NULL COMMENT '变更后过期时间',
    `order_id` varchar(36) DEFAULT NULL COMMENT '关联订单ID',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
    `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
    `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_store_id` (`store_id`),
    KEY `idx_change_type` (`change_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员变更记录表';

CREATE TABLE `bls_pay_record` (
  `id` varchar(36) NOT NULL COMMENT '记录ID',
  `pay_no` varchar(32) NOT NULL COMMENT '充值流水号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `openid` varchar(64) NOT NULL COMMENT '用户微信openid',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '微信支付交易号',
  `amount` decimal(10,2) NOT NULL COMMENT '充值金额',
  `channel` varchar(20) NOT NULL COMMENT '支付渠道（详见枚举类：OrderChannelEnum）',
  `payment_status` tinyint DEFAULT 0 COMMENT '支付状态 0-未支付 1-已支付 2-已退款',
  `notify_time` datetime DEFAULT NULL COMMENT '支付回调通知时间',
  `notify_data` text DEFAULT NULL COMMENT '支付回调原始数据',
  `last_query_time` datetime DEFAULT NULL COMMENT '最后查询时间（主动查询支付结果）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
  `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_no` (`pay_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_transaction_id` (`transaction_id`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值支付记录表';

CREATE TABLE `bls_refund_record` (
  `id` varchar(36) NOT NULL COMMENT '记录ID',
  `pay_record_id` varchar(32) NOT NULL COMMENT 'bls_pay_record的充值记录id',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '微信支付交易号',
  `order_id` varchar(32) NOT NULL COMMENT 'bls_order订单记录id',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `refund_status` tinyint DEFAULT 0 COMMENT '退款状态 0-退款中 1-退款成功 2-退款失败',
  `notify_time` datetime DEFAULT NULL COMMENT '回调通知时间',
  `notify_data` text DEFAULT NULL COMMENT '回调原始数据',
  `last_query_time` datetime DEFAULT NULL COMMENT '最后查询时间（主动查询退款结果）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
  `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_record_id` (`pay_record_id`),
  KEY `idx_refund_user_id` (`user_id`),
  KEY `idx_refund_order_id` (`order_id`),
  KEY `idx_refund_refund_status` (`refund_status`),
  KEY `idx_refund_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';


CREATE TABLE `bls_wallet_account` (
  `id` varchar(36) NOT NULL COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `balance` decimal(10,2) default 0 COMMENT '当前余额',
  `freeze_amount` decimal(10,2) default 0 COMMENT '冻结金额',
  `total_recharge` decimal(10,2) default 0 COMMENT '累计充值',
  `total_refund` decimal(10,2) default 0 COMMENT '累计退款',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
  `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包账户';

CREATE TABLE `bls_wallet_transaction` (
  `id` varchar(36) NOT NULL COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `trans_type` varchar(16) DEFAULT NULL COMMENT '交易类型:RECHARGE/CONSUME/REFUND',
  `amount` decimal(10,2) default 0 COMMENT '交易金额',
  `related_id` varchar(36) NOT NULL COMMENT '关联记录ID（如PayRecord或Order）',
  `source_pay_id` varchar(36) DEFAULT NULL COMMENT '来源的充值记录（用于退款）',
  `transaction_id` varchar(36) DEFAULT NULL COMMENT '微信交易id',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
  `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包流水';


-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1931569081557364738, '用户钱包账户', '3000', '1', 'walletAccount', 'system/walletAccount/index', 1, 0, 'C', '0', '0', 'system:walletAccount:list', '#', 103, 1, sysdate(), null, null, '用户钱包账户菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1931569081557364739, '用户钱包账户查询', 1931569081557364738, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:walletAccount:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1931569081557364740, '用户钱包账户新增', 1931569081557364738, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:walletAccount:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1931569081557364741, '用户钱包账户修改', 1931569081557364738, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:walletAccount:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1931569081557364742, '用户钱包账户删除', 1931569081557364738, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:walletAccount:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1931569081557364743, '用户钱包账户导出', 1931569081557364738, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:walletAccount:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1931569079007227906, '用户钱包流水', '3000', '1', 'walletTransaction', 'system/walletTransaction/index', 1, 0, 'C', '0', '0', 'system:walletTransaction:list', '#', 103, 1, sysdate(), null, null, '用户钱包流水菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1931569079007227907, '用户钱包流水查询', 1931569079007227906, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:walletTransaction:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1931569079007227908, '用户钱包流水新增', 1931569079007227906, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:walletTransaction:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1931569079007227909, '用户钱包流水修改', 1931569079007227906, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:walletTransaction:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1931569079007227910, '用户钱包流水删除', 1931569079007227906, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:walletTransaction:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1931569079007227911, '用户钱包流水导出', 1931569079007227906, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:walletTransaction:export',       '#', 103, 1, sysdate(), null, null, '');


-- 会员相关表
-- 会员用户表
CREATE TABLE `bls_member_user` (
                                   `id` varchar(32) NOT NULL COMMENT '会员ID',
                                   `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                   `level_code` int NOT NULL DEFAULT 0 COMMENT '当前等级编码',
                                   `total_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '累计消费金额',
                                   `points` int NOT NULL DEFAULT 0 COMMENT '当前积分',
                                   `monthly_used_minutes` int NOT NULL DEFAULT 0 COMMENT '本月已使用免费时长（分钟）',
                                   `level_expire_time` datetime DEFAULT NULL COMMENT '等级有效期',
                                   `last_consume_time` datetime DEFAULT NULL COMMENT '最近消费时间',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                   `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                   `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态：0-正常 1-禁用',
                                   `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_user_id` (`user_id`),
                                   KEY `idx_level_code` (`level_code`),
                                   KEY `idx_total_amount` (`total_amount`),
                                   KEY `idx_points` (`points`),
                                   KEY `idx_level_expire` (`level_expire_time`),
                                   KEY `idx_last_consume` (`last_consume_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员用户表';

-- 会员等级配置表
CREATE TABLE `bls_member_level_config` (
                                           `id` varchar(32) NOT NULL COMMENT '配置ID',
                                           `level_code` int NOT NULL COMMENT '等级编码',
                                           `level_name` varchar(32) NOT NULL COMMENT '等级名称',
                                           `required_amount` int NOT NULL DEFAULT 0 COMMENT '所需累计消费金额',
                                           `discount` decimal(3,2) NOT NULL COMMENT '折扣率',
                                           `monthly_free_minutes` int NOT NULL DEFAULT 0 COMMENT '每月赠送时长（分钟）',
                                           `points_multiplier` decimal(3,2) NOT NULL DEFAULT 1.00 COMMENT '积分获取倍率',
                                           `birthday_discount` decimal(3,2) DEFAULT NULL COMMENT '生日特权折扣率',
                                           `friend_privilege_count` int NOT NULL DEFAULT 0 COMMENT '可带朋友享受会员价的人数',
                                           `vip_service` tinyint(1) NOT NULL DEFAULT 0 COMMENT '专属客服服务 0-否 1-是',
                                           `reservation_privilege` tinyint(1) NOT NULL DEFAULT 0 COMMENT '预约特权 0-否 1-是',
                                           `level_icon` varchar(255) DEFAULT NULL COMMENT '等级图标',
                                           `level_background` varchar(255) DEFAULT NULL COMMENT '等级背景图',
                                           `description` varchar(500) DEFAULT NULL COMMENT '等级描述',
                                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                           `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                           `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态 0-启用 1-禁用',
                                           `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `uk_level_code` (`level_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级配置表';

-- 会员权益表
CREATE TABLE `bls_member_benefit` (
                                      `id` varchar(32) NOT NULL COMMENT '权益ID',
                                      `name` varchar(64) NOT NULL COMMENT '权益名称',
                                      `type` tinyint(1) NOT NULL COMMENT '权益类型：1-折扣 2-赠送 3-积分 4-特权',
                                      `applicable_levels` varchar(64) NOT NULL COMMENT '适用等级编码，多个用逗号分隔',
                                      `benefit_value` varchar(64) NOT NULL COMMENT '权益值（如折扣率、赠送时长、积分倍率等）',
                                      `benefit_rules` text DEFAULT NULL COMMENT '权益规则（JSON格式，存储具体规则配置）',
                                      `effective_time` datetime DEFAULT NULL COMMENT '生效时间',
                                      `expire_time` datetime DEFAULT NULL COMMENT '失效时间',
                                      `icon` varchar(255) DEFAULT NULL COMMENT '权益图标',
                                      `description` varchar(500) DEFAULT NULL COMMENT '权益描述',
                                      `instructions` varchar(1000) DEFAULT NULL COMMENT '使用说明',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                      `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                      `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态：0-启用 1-禁用',
                                      `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
                                      `is_limited` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否限时权益：0-永久 1-限时',
                                      `is_holiday` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否节日特权：0-否 1-是',
                                      `tags` varchar(255) DEFAULT NULL COMMENT '权益标签，多个用逗号分隔',
                                      `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_type` (`type`),
                                      KEY `idx_status` (`status`),
                                      KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员权益表';

-- 积分规则表
CREATE TABLE `bls_points_rule` (
                                   `id` varchar(32) NOT NULL COMMENT '规则ID',
                                   `name` varchar(64) NOT NULL COMMENT '规则名称',
                                   `type` tinyint(1) NOT NULL COMMENT '规则类型：1-获取 2-消耗',
                                   `scene` tinyint(2) NOT NULL COMMENT '积分场景：获取场景：1-消费 2-签到 3-活动 4-评价 5-首次绑定 6-邀请好友；消耗场景：1-抵扣 2-兑换商品 3-兑换优惠券',
                                   `value_type` tinyint(1) NOT NULL COMMENT '积分值类型：1-固定值 2-比例值',
                                   `points_value` decimal(10,2) NOT NULL COMMENT '积分值',
                                   `max_points` int NOT NULL DEFAULT 0 COMMENT '封顶积分值（0表示不封顶）',
                                   `rule_config` text DEFAULT NULL COMMENT '规则配置（JSON格式，存储具体规则）',
                                   `level_bonus` text DEFAULT NULL COMMENT '等级加成配置（JSON格式，存储各等级的加成比例）',
                                   `time_bonus` text DEFAULT NULL COMMENT '时段加成配置（JSON格式，存储特殊时段的加成比例）',
                                   `effective_time` datetime DEFAULT NULL COMMENT '生效时间',
                                   `expire_time` datetime DEFAULT NULL COMMENT '失效时间',
                                   `description` varchar(500) DEFAULT NULL COMMENT '规则描述',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                   `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                   `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态：0-启用 1-禁用',
                                   `enable_activity_bonus` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否参与活动加成：0-否 1-是',
                                   `validity_days` int NOT NULL DEFAULT 0 COMMENT '积分有效期（天）：0表示永久有效',
                                   `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_type_scene` (`type`, `scene`),
                                   KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分规则表';

-- 会员积分记录表
CREATE TABLE `bls_member_points_record` (
                                            `id` varchar(32) NOT NULL COMMENT '记录ID',
                                            `user_id` varchar(32) NOT NULL COMMENT '用户ID',
                                            `points` int NOT NULL COMMENT '积分数量（正数表示获取，负数表示消耗）',
                                            `type` tinyint(1) NOT NULL COMMENT '类型：1-获取 2-消耗',
                                            `scene` tinyint(2) NOT NULL COMMENT '场景，与积分规则表场景对应',
                                            `rule_id` varchar(32) NOT NULL COMMENT '对应的规则ID',
                                            `business_id` varchar(32) DEFAULT NULL COMMENT '关联业务ID（如订单ID、活动ID等）',
                                            `description` varchar(255) DEFAULT NULL COMMENT '积分描述',
                                            `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
                                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                            `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                            `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                            PRIMARY KEY (`id`),
                                            KEY `idx_user_id` (`user_id`),
                                            KEY `idx_type_scene` (`type`, `scene`),
                                            KEY `idx_create_time` (`create_time`),
                                            KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分记录表';

CREATE TABLE `bls_member_points_validity` (
                                              `id` varchar(32) NOT NULL COMMENT '记录ID',
                                              `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                              `points` int NOT NULL COMMENT '积分数量',
                                              `remaining_points` int NOT NULL COMMENT '剩余积分数量',
                                              `expire_time` datetime NOT NULL COMMENT '过期时间',
                                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                              `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                              `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                              PRIMARY KEY (`id`),
                                              KEY `idx_user_expire` (`user_id`, `expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分有效期表';


-- 初始化会员等级配置数据
INSERT INTO `member_level_config` (`id`, `level_code`, `level_name`, `required_amount`, `discount`, `monthly_free_minutes`,
                                   `points_multiplier`, `birthday_discount`, `friend_privilege_count`, `vip_service`, `reservation_privilege`, `description`)
VALUES
    ('1', 0, '普通会员', 0, 0.95, 0, 1.00, NULL, 0, 0, 0, '入门级别会员，享受9.5折优惠'),
    ('2', 1, '银牌会员', 1000, 0.90, 0, 1.20, 0.85, 0, 0, 1, '中级会员，享受9折优惠，生日特惠8.5折'),
    ('3', 2, '金牌会员', 3000, 0.85, 120, 1.50, 0.80, 1, 1, 1, '高级会员，享受8.5折优惠，每月赠送2小时'),
    ('4', 3, '钻石会员', 10000, 0.80, 300, 2.00, 0.75, 2, 1, 1, '至尊级别，享受8折优惠，每月赠送5小时，可带2位朋友享会员价');

-- 初始化基础积分规则
INSERT INTO `points_rule` (`id`, `name`, `type`, `scene`, `value_type`, `points_value`, `description`, `status`)
VALUES
    ('1', '消费积分', 1, 1, 2, 1.00, '每消费1元获得1积分', 0),
    ('2', '每日签到', 1, 2, 1, 5.00, '每日签到获得5积分', 0),
    ('3', '首次绑定', 1, 5, 1, 100.00, '首次绑定手机号获得100积分', 0),
    ('4', '邀请好友', 1, 6, 1, 50.00, '成功邀请好友获得50积分', 0),
    ('5', '积分抵扣', 2, 1, 2, 0.01, '100积分抵扣1元', 0);

-- 菜单 SQL

-- 台球管理一级菜单
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2000, '台球管理', 0, 5, 'billiards', null, 1, 0, 'M', '0', '0', null, 'example', 1, now(), '台球管理目录');

-- 门店管理
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2001, '门店管理', 2000, 1, 'store', 'billiards/store/index', 1, 0, 'C', '0', '0', 'billiards:store:list', 'store', 1, now(), '门店管理菜单');

-- 桌台管理
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2002, '桌台管理', 2000, 2, 'table', 'billiards/table/index', 1, 0, 'C', '0', '0', 'billiards:table:list', 'table', 1, now(), '桌台管理菜单');

-- 计费规则
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2003, '计费规则', 2000, 3, 'priceRule', 'billiards/priceRule/index', 1, 0, 'C', '0', '0', 'billiards:priceRule:list', 'money', 1, now(), '计费规则菜单');

-- 订单管理
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2004, '订单管理', 2000, 4, 'order', 'billiards/order/index', 1, 0, 'C', '0', '0', 'billiards:order:list', 'order', 1, now(), '订单管理菜单');

-- 营业概况
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2005, '营业概况', 2000, 5, 'dashboard', 'billiards/dashboard/index', 1, 0, 'C', '0', '0', 'billiards:dashboard:view', 'data-board', 1, now(), '营业概况菜单');

-- 门店管理按钮
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, perms, menu_type, visible, status, is_frame, is_cache, icon, create_by, create_time, remark) values
                                                                                                                                                                       (2010, '门店查询', 2001, 1, '', 'billiards:store:query', 'F', '0', '0', 1, 0, '#', 1, now(), '门店查询按钮'),
                                                                                                                                                                       (2011, '门店新增', 2001, 2, '', 'billiards:store:add', 'F', '0', '0', 1, 0, '#', 1, now(), '门店新增按钮'),
                                                                                                                                                                       (2012, '门店修改', 2001, 3, '', 'billiards:store:edit', 'F', '0', '0', 1, 0, '#', 1, now(), '门店修改按钮'),
                                                                                                                                                                       (2013, '门店删除', 2001, 4, '', 'billiards:store:remove', 'F', '0', '0', 1, 0, '#', 1, now(), '门店删除按钮'),
                                                                                                                                                                       (2014, '门店导出', 2001, 5, '', 'billiards:store:export', 'F', '0', '0', 1, 0, '#', 1, now(), '门店导出按钮');

-- 桌台管理按钮
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, perms, menu_type, visible, status, is_frame, is_cache, icon, create_by, create_time, remark) values
                                                                                                                                                                       (2020, '桌台查询', 2002, 1, '', 'billiards:table:query', 'F', '0', '0', 1, 0, '#', 1, now(), '桌台查询按钮'),
                                                                                                                                                                       (2021, '桌台新增', 2002, 2, '', 'billiards:table:add', 'F', '0', '0', 1, 0, '#', 1, now(), '桌台新增按钮'),
                                                                                                                                                                       (2022, '桌台修改', 2002, 3, '', 'billiards:table:edit', 'F', '0', '0', 1, 0, '#', 1, now(), '桌台修改按钮'),
                                                                                                                                                                       (2023, '桌台删除', 2002, 4, '', 'billiards:table:remove', 'F', '0', '0', 1, 0, '#', 1, now(), '桌台删除按钮'),
                                                                                                                                                                       (2024, '桌台导出', 2002, 5, '', 'billiards:table:export', 'F', '0', '0', 1, 0, '#', 1, now(), '桌台导出按钮');

-- 计费规则按钮
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, perms, menu_type, visible, status, is_frame, is_cache, icon, create_by, create_time, remark) values
                                                                                                                                                                       (2030, '计费规则查询', 2003, 1, '', 'billiards:priceRule:query', 'F', '0', '0', 1, 0, '#', 1, now(), '计费规则查询按钮'),
                                                                                                                                                                       (2031, '计费规则新增', 2003, 2, '', 'billiards:priceRule:add', 'F', '0', '0', 1, 0, '#', 1, now(), '计费规则新增按钮'),
                                                                                                                                                                       (2032, '计费规则修改', 2003, 3, '', 'billiards:priceRule:edit', 'F', '0', '0', 1, 0, '#', 1, now(), '计费规则修改按钮'),
                                                                                                                                                                       (2033, '计费规则删除', 2003, 4, '', 'billiards:priceRule:remove', 'F', '0', '0', 1, 0, '#', 1, now(), '计费规则删除按钮'),
                                                                                                                                                                       (2034, '计费规则导出', 2003, 5, '', 'billiards:priceRule:export', 'F', '0', '0', 1, 0, '#', 1, now(), '计费规则导出按钮');

-- 订单管理按钮
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, perms, menu_type, visible, status, is_frame, is_cache, icon, create_by, create_time, remark) values
                                                                                                                                                                       (2040, '订单查询', 2004, 1, '', 'billiards:order:query', 'F', '0', '0', 1, 0, '#', 1, now(), '订单查询按钮'),
                                                                                                                                                                       (2041, '订单新增', 2004, 2, '', 'billiards:order:add', 'F', '0', '0', 1, 0, '#', 1, now(), '订单新增按钮'),
                                                                                                                                                                       (2042, '订单修改', 2004, 3, '', 'billiards:order:edit', 'F', '0', '0', 1, 0, '#', 1, now(), '订单修改按钮'),
                                                                                                                                                                       (2043, '订单删除', 2004, 4, '', 'billiards:order:remove', 'F', '0', '0', 1, 0, '#', 1, now(), '订单删除按钮'),
                                                                                                                                                                       (2044, '订单导出', 2004, 5, '', 'billiards:order:export', 'F', '0', '0', 1, 0, '#', 1, now(), '订单导出按钮');

-- 营业概况按钮（如有）
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, perms, menu_type, visible, status, is_frame, is_cache, icon, create_by, create_time, remark) values
    (2050, '营业概况查看', 2005, 1, '', 'billiards:dashboard:view', 'F', '0', '0', 1, 0, '#', 1, now(), '营业概况查看按钮');



INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES(3000, '会员管理', 0, 6, 'star', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'billiards', NULL, 1, '2025-04-27 16:37:19', NULL, NULL, '会员管理目录');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892013977251841, '积分规则', '3000', '1', 'pointsRule', 'billiards/pointsRule/index', 1, 0, 'C', '0', '0', 'billiards:pointsRule:list', '#', 103, 1, sysdate(), null, null, '积分规则菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892013977251842, '积分规则查询', 1934892013977251841, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:pointsRule:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892013977251843, '积分规则新增', 1934892013977251841, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:pointsRule:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892013977251844, '积分规则修改', 1934892013977251841, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:pointsRule:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892013977251845, '积分规则删除', 1934892013977251841, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:pointsRule:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892013977251846, '积分规则导出', 1934892013977251841, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:pointsRule:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892013520072706, '会员用户', '3000', '1', 'memberUser', 'billiards/memberUser/index', 1, 0, 'C', '0', '0', 'billiards:memberUser:list', '#', 103, 1, sysdate(), null, null, '会员用户菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892013520072707, '会员用户查询', 1934892013520072706, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberUser:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892013520072708, '会员用户新增', 1934892013520072706, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberUser:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892013520072709, '会员用户修改', 1934892013520072706, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberUser:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892013520072710, '会员用户删除', 1934892013520072706, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberUser:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892013520072711, '会员用户导出', 1934892013520072706, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberUser:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012018511874, '积分有效期', '3000', '1', 'memberPointsValidity', 'billiards/memberPointsValidity/index', 1, 0, 'C', '0', '0', 'billiards:memberPointsValidity:list', '#', 103, 1, sysdate(), null, null, '积分有效期菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012018511875, '积分有效期查询', 1934892012018511874, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsValidity:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012018511876, '积分有效期新增', 1934892012018511874, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsValidity:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012018511877, '积分有效期修改', 1934892012018511874, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsValidity:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012018511878, '积分有效期删除', 1934892012018511874, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsValidity:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012018511879, '积分有效期导出', 1934892012018511874, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsValidity:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012995784706, '会员积分记录', '3000', '1', 'memberPointsRecord', 'billiards/memberPointsRecord/index', 1, 0, 'C', '0', '0', 'billiards:memberPointsRecord:list', '#', 103, 1, sysdate(), null, null, '会员积分记录菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012995784707, '会员积分记录查询', 1934892012995784706, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsRecord:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012995784708, '会员积分记录新增', 1934892012995784706, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsRecord:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012995784709, '会员积分记录修改', 1934892012995784706, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsRecord:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012995784710, '会员积分记录删除', 1934892012995784706, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsRecord:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012995784711, '会员积分记录导出', 1934892012995784706, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsRecord:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012475691009, '会员等级配置', '3000', '1', 'memberLevelConfig', 'billiards/memberLevelConfig/index', 1, 0, 'C', '0', '0', 'billiards:memberLevelConfig:list', '#', 103, 1, sysdate(), null, null, '会员等级配置菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012475691010, '会员等级配置查询', 1934892012475691009, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberLevelConfig:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012475691011, '会员等级配置新增', 1934892012475691009, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberLevelConfig:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012475691012, '会员等级配置修改', 1934892012475691009, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberLevelConfig:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012475691013, '会员等级配置删除', 1934892012475691009, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberLevelConfig:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892012475691014, '会员等级配置导出', 1934892012475691009, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberLevelConfig:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892014627368961, '会员权益', '3000', '1', 'memberBenefit', 'billiards/memberBenefit/index', 1, 0, 'C', '0', '0', 'billiards:memberBenefit:list', '#', 103, 1, sysdate(), null, null, '会员权益菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892014627368962, '会员权益查询', 1934892014627368961, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberBenefit:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892014627368963, '会员权益新增', 1934892014627368961, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberBenefit:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892014627368964, '会员权益修改', 1934892014627368961, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberBenefit:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892014627368965, '会员权益删除', 1934892014627368961, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberBenefit:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1934892014627368966, '会员权益导出', 1934892014627368961, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberBenefit:export',       '#', 103, 1, sysdate(), null, null, '');


-- 支付服务商配置（门店 > 商户 > 租户 覆盖）（目前未使用，以后需求扩展使用,TenantResolver）
CREATE TABLE `bls_pay_channel_config` (
  `id` varchar(36) NOT NULL COMMENT 'ID',
  `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
  `merchant_id` varchar(36) DEFAULT NULL COMMENT '商家ID(可空=租户级)',
  `store_id` varchar(36) DEFAULT NULL COMMENT '门店ID(可空=商户/租户级)',
  `app_id` varchar(64) NOT NULL COMMENT 'AppId',
  `sub_mch_id` varchar(32) NOT NULL COMMENT '子商户号',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0启用 1停用',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `create_by` varchar(36) DEFAULT NULL,
  `update_by` varchar(36) DEFAULT NULL,
  `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_store_app` (`tenant_id`, `store_id`, `app_id`),
  UNIQUE KEY `uk_tenant_merchant_app` (`tenant_id`, `merchant_id`, `app_id`),
  UNIQUE KEY `uk_tenant_app` (`tenant_id`, `app_id`),
  KEY `idx_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付服务商配置(门店>商户>租户)';

/**
  -- 优先门店/商户配置，退化到租户级
  select tenant_id from bls_pay_channel_config
  where app_id = #{appId}
  order by (store_id is not null) desc, (merchant_id is not null) desc
  limit 1
 */