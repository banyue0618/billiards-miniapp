-- 创建数据库（业务库）
CREATE DATABASE IF NOT EXISTS `billiards_saas` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE `billiards_saas`;

-- 用户表（多租户 + 多小程序AppId）
CREATE TABLE `bls_user` (
                            `id` bigint(20) UNSIGNED AUTO_INCREMENT COMMENT '用户ID',
                            `openid` varchar(64) DEFAULT NULL COMMENT '微信OpenID',
                            `unionid` varchar(64) DEFAULT NULL COMMENT '微信UnionID',
                            `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称（微信昵称）',
                            `user_name` varchar(50) DEFAULT NULL COMMENT '用户名（用户自己设置）',
                            `avatar_url` varchar(255) DEFAULT NULL COMMENT '用户头像URL',
                            `gender` tinyint DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
                            `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
                            `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
                            `status` tinyint DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
                            `create_time` datetime NOT NULL COMMENT '创建时间',
                            `update_time` datetime NOT NULL COMMENT '更新时间',
                            `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                            `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                            `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_user_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


-- 门店表（隶属于商户）
CREATE TABLE `bls_store` (
                             `id` varchar(36) NOT NULL COMMENT '门店ID',
                             `merchant_id` varchar(36) NOT NULL COMMENT '所属商家ID',
                             `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
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
                             KEY `idx_merchant_id` (`merchant_id`),
                             KEY `idx_tenant` (`tenant_id`),
                             KEY `idx_tenant_merchant` (`tenant_id`, `merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店表';

-- 价格规则（商户级，支持门店覆盖）
CREATE TABLE `bls_price_rule` (
                                  `id` varchar(36) NOT NULL COMMENT '规则ID',
                                  `merchant_id` varchar(36) NOT NULL COMMENT '所属商家ID',
                                  `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                  `store_id` varchar(36) DEFAULT NULL COMMENT '门店级规则（可空）',
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
                                  KEY `idx_merchant_id` (`merchant_id`),
                                  KEY `idx_tenant` (`tenant_id`),
                                  KEY `idx_store_id` (`store_id`),
                                  UNIQUE KEY `uk_tenant_merchant_name` (`tenant_id`, `merchant_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格规则表';

-- 桌台表
CREATE TABLE `bls_table` (
                             `id` varchar(36) NOT NULL COMMENT '桌台ID',
                             `store_id` varchar(36) NOT NULL COMMENT '所属门店ID',
                             `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                             `merchant_id` varchar(36) NOT NULL COMMENT '商家ID(冗余)',
                             `table_number` VARCHAR(20) NOT NULL COMMENT '桌台编号',
                             `table_prefix` varchar(10) NOT NULL COMMENT '桌台号前缀',
                             `table_numeric` int NOT NULL COMMENT '桌台号',
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
                             `version` int DEFAULT 0 COMMENT '版本id（每次状态变更+1）',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_store_table_number` (`store_id`, `table_prefix`, `table_numeric`),
                             KEY `idx_store_id` (`store_id`),
                             KEY `idx_price_rule_id` (`price_rule_id`),
                             KEY `idx_tenant` (`tenant_id`),
                             KEY `idx_tenant_merchant` (`tenant_id`, `merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='桌台表';

-- 订单表
CREATE TABLE `bls_order` (
                             `id` varchar(36) NOT NULL COMMENT '订单ID',
                             `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                             `merchant_id` varchar(36) NOT NULL COMMENT '商家ID',
                             `order_no` varchar(32) NOT NULL COMMENT '订单编号',
                             `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                             `channel` varchar(36) NOT NULL COMMENT '下单渠道（APP/小程序/公众号/WEB/H5）',
                             `store_id` varchar(36) NOT NULL COMMENT '门店ID',
                             `store_name` varchar(100) DEFAULT NULL COMMENT '门店名称',
                             `table_id` varchar(36) NOT NULL COMMENT '桌台ID',
                             `table_number` varchar(10) DEFAULT NULL COMMENT '桌台号',
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
                             UNIQUE KEY `uk_tenant_order_no` (`tenant_id`, `order_no`),
                             KEY `idx_user_id` (`user_id`),
                             KEY `idx_store_id` (`store_id`),
                             KEY `idx_table_id` (`table_id`),
                             KEY `idx_price_rule_id` (`price_rule_id`),
                             KEY `idx_status` (`status`),
                             KEY `idx_tenant_user` (`tenant_id`, `user_id`),
                             KEY `idx_tenant_store` (`tenant_id`, `store_id`),
                             KEY `idx_tenant_merchant` (`tenant_id`, `merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 桌台使用记录表
CREATE TABLE `bls_table_usage` (
                                   `id` varchar(36) NOT NULL COMMENT '记录ID',
                                   `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                   `merchant_id` varchar(36) NOT NULL COMMENT '商家ID',
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
                                   KEY `idx_tenant` (`tenant_id`),
                                   KEY `idx_tenant_merchant` (`tenant_id`, `merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='桌台使用记录表';

-- 充值支付记录表
CREATE TABLE `bls_pay_record` (
                                  `id` varchar(36) NOT NULL COMMENT '记录ID',
                                  `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                  `merchant_id` varchar(36) NOT NULL COMMENT '商家ID',
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
                                  UNIQUE KEY `uk_tenant_merchant_pay_no` (`tenant_id`, `merchant_id`, `pay_no`),
                                  KEY `idx_user_id` (`user_id`),
                                  KEY `idx_transaction_id` (`transaction_id`),
                                  KEY `idx_payment_status` (`payment_status`),
                                  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值支付记录表';

-- 退款记录表
CREATE TABLE `bls_refund_record` (
                                     `id` varchar(36) NOT NULL COMMENT '记录ID',
                                     `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                     `merchant_id` varchar(36) NOT NULL COMMENT '商家ID',
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
                                     UNIQUE KEY `uk_tenant_merchant_pay_record` (`tenant_id`, `merchant_id`, `pay_record_id`),
                                     KEY `idx_refund_user_id` (`user_id`),
                                     KEY `idx_refund_order_id` (`order_id`),
                                     KEY `idx_refund_refund_status` (`refund_status`),
                                     KEY `idx_refund_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';

-- 用户钱包账户表（按商户隔离）
CREATE TABLE `bls_wallet_account` (
                                      `id` varchar(36) NOT NULL COMMENT '记录ID',
                                      `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                      `merchant_id` varchar(36) NOT NULL COMMENT '商家ID',
                                      `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                      `balance` decimal(10,2) DEFAULT 0 COMMENT '当前余额',
                                      `freeze_amount` decimal(10,2) DEFAULT 0 COMMENT '冻结金额',
                                      `total_recharge` decimal(10,2) DEFAULT 0 COMMENT '累计充值',
                                      `total_refund` decimal(10,2) DEFAULT 0 COMMENT '累计退款',
                                      `remark` varchar(255) DEFAULT NULL COMMENT '备注',
                                      `create_time` datetime NOT NULL COMMENT '创建时间',
                                      `update_time` datetime NOT NULL COMMENT '更新时间',
                                      `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                      `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                      `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_tenant_merchant_user` (`tenant_id`, `merchant_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包账户';

-- 用户钱包流水表
CREATE TABLE `bls_wallet_transaction` (
                                          `id` varchar(36) NOT NULL COMMENT '记录ID',
                                          `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                          `merchant_id` varchar(36) NOT NULL COMMENT '商家ID',
                                          `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                          `trans_type` varchar(16) DEFAULT NULL COMMENT '交易类型:RECHARGE/CONSUME/REFUND',
                                          `amount` decimal(10,2) DEFAULT 0 COMMENT '交易金额',
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
                                          KEY `idx_tenant_merchant_user` (`tenant_id`, `merchant_id`, `user_id`),
                                          KEY `idx_tenant_merchant_time` (`tenant_id`, `merchant_id`, `update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包流水';

-- 会员用户表（按商户隔离）
CREATE TABLE `bls_member_user` (
                                   `id` varchar(32) NOT NULL COMMENT '会员ID',
                                   `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                   `merchant_id` varchar(36) NOT NULL COMMENT '商家ID',
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
                                   UNIQUE KEY `uk_tenant_merchant_user` (`tenant_id`, `merchant_id`, `user_id`),
                                   KEY `idx_level_code` (`level_code`),
                                   KEY `idx_total_amount` (`total_amount`),
                                   KEY `idx_points` (`points`),
                                   KEY `idx_level_expire` (`level_expire_time`),
                                   KEY `idx_last_consume` (`last_consume_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员用户表';

-- 会员变更记录表
CREATE TABLE `bls_member_change_log` (
                                         `id` varchar(36) NOT NULL COMMENT '记录ID',
                                         `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                         `merchant_id` varchar(36) NOT NULL COMMENT '商家ID',
                                         `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                         `change_type` varchar(20) NOT NULL COMMENT '变更类型 NEW/RENEWAL/UPGRADE/EXPIRED',
                                         `before_level` varchar(32) DEFAULT NULL COMMENT '变更前等级',
                                         `after_level` varchar(32) DEFAULT NULL COMMENT '变更后等级',
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
                                         KEY `idx_change_type` (`change_type`),
                                         KEY `idx_create_time` (`create_time`),
                                         KEY `idx_tenant_merchant` (`tenant_id`, `merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员变更记录表';

-- 会员等级配置（商户可自定义；NULL表示租户级模板）
CREATE TABLE `bls_member_level_config` (
                                           `id` varchar(32) NOT NULL COMMENT '配置ID',
                                           `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                           `merchant_id` varchar(36) DEFAULT NULL COMMENT '商家ID(可空=租户级模板)',
                                           `level_code` varchar(32) NOT NULL COMMENT '等级编码',
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
                                           UNIQUE KEY `uk_tenant_merchant_level` (`tenant_id`, `merchant_id`, `level_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级配置表';

-- 会员权益（可商户级，NULL=租户级）
CREATE TABLE `bls_member_benefit` (
                                      `id` varchar(32) NOT NULL COMMENT '权益ID',
                                      `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                      `merchant_id` varchar(36) DEFAULT NULL COMMENT '商家ID(可空=租户级)',
                                      `name` varchar(64) NOT NULL COMMENT '权益名称',
                                      `type` tinyint(1) NOT NULL COMMENT '权益类型：1-折扣 2-赠送 3-积分 4-特权',
                                      `applicable_levels` varchar(64) NOT NULL COMMENT '适用等级编码，多个用逗号分隔',
                                      `benefit_value` varchar(64) NOT NULL COMMENT '权益值（如折扣率、赠送时长、积分倍率等）',
                                      `benefit_rules` text DEFAULT NULL COMMENT '权益规则（JSON格式）',
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
                                      `tags` varchar(255) DEFAULT NULL COMMENT '权益标签',
                                      `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_tenant_merchant` (`tenant_id`, `merchant_id`),
                                      KEY `idx_type` (`type`),
                                      KEY `idx_status` (`status`),
                                      KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员权益表';

-- 积分规则（可商户级，NULL=租户级）
CREATE TABLE `bls_points_rule` (
                                   `id` varchar(32) NOT NULL COMMENT '规则ID',
                                   `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                   `merchant_id` varchar(36) DEFAULT NULL COMMENT '商家ID(可空=租户级)',
                                   `name` varchar(64) NOT NULL COMMENT '规则名称',
                                   `type` tinyint(1) NOT NULL COMMENT '规则类型：1-获取 2-消耗',
                                   `scene` tinyint(2) NOT NULL COMMENT '积分场景：获取1消费 2签到 3活动 4评价 5首次绑定 6邀请；消耗21抵扣 22兑换商品 23兑换券',
                                   `value_type` tinyint(1) NOT NULL COMMENT '积分值类型：1-固定值 2-比例值',
                                   `points_value` decimal(10,2) NOT NULL COMMENT '积分值',
                                   `max_points` int NOT NULL DEFAULT 0 COMMENT '封顶积分值（0不封顶）',
                                   `rule_config` text DEFAULT NULL COMMENT '规则配置（JSON）',
                                   `level_bonus` text DEFAULT NULL COMMENT '等级加成配置（JSON）',
                                   `time_bonus` text DEFAULT NULL COMMENT '时段加成配置（JSON）',
                                   `effective_time` datetime DEFAULT NULL COMMENT '生效时间',
                                   `expire_time` datetime DEFAULT NULL COMMENT '失效时间',
                                   `description` varchar(500) DEFAULT NULL COMMENT '规则描述',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                   `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                   `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态：0-启用 1-禁用',
                                   `enable_activity_bonus` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否参与活动加成：0-否 1-是',
                                   `validity_days` int NOT NULL DEFAULT 0 COMMENT '积分有效期（天）：0永久',
                                   `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_tenant_merchant_rule` (`tenant_id`, `merchant_id`, `name`),
                                   KEY `idx_type_scene` (`type`, `scene`),
                                   KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分规则表';

-- 会员积分记录表（按商户隔离）
CREATE TABLE `bls_member_points_record` (
                                            `id` varchar(32) NOT NULL COMMENT '记录ID',
                                            `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                            `merchant_id` varchar(36) NOT NULL COMMENT '商家ID',
                                            `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                            `points` int NOT NULL COMMENT '积分数量',
                                            `type` tinyint(1) NOT NULL COMMENT '变动类型：1-增加 2-消费 3-过期',
                                            `scene` tinyint(2) NOT NULL COMMENT '业务场景场景，对应规则表',
                                            `rule_id` varchar(32) NOT NULL COMMENT '规则ID',
                                            `business_id` varchar(32) DEFAULT NULL COMMENT '关联业务ID（如订单ID等）',
                                            `description` varchar(255) DEFAULT NULL COMMENT '积分描述',
                                            `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
                                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                            `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                            `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                            PRIMARY KEY (`id`),
                                            KEY `idx_tenant_merchant_user` (`tenant_id`, `merchant_id`, `user_id`),
                                            KEY `idx_type_scene` (`type`, `scene`),
                                            KEY `idx_create_time` (`create_time`),
                                            KEY `idx_expire_time` (`expire_time`),
                                            UNIQUE KEY `uk_business_id_type_scene` (`business_id`, `type`, `scene`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分记录表';

-- 积分有效期表（按商户隔离）
CREATE TABLE `bls_member_points_validity` (
                                              `id` varchar(32) NOT NULL COMMENT '记录ID',
                                              `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                              `merchant_id` varchar(36) NOT NULL COMMENT '商家ID',
                                              `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                              `points` int NOT NULL COMMENT '积分数量',
                                              `remaining_points` int NOT NULL COMMENT '剩余积分数量',
                                              `expire_time` datetime NOT NULL COMMENT '过期时间',
                                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                              `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                              PRIMARY KEY (`id`),
                                              KEY `idx_tenant_merchant_user_expire` (`tenant_id`, `merchant_id`, `user_id`, `expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分有效期表';

-- 会员积分消费详情表
CREATE TABLE bls_member_points_consume_detail (
  id              varchar(32) PRIMARY KEY COMMENT '记录ID',
  tenant_id       varchar(20) NOT NULL COMMENT '租户编号',
  merchant_id     varchar(36) NOT NULL COMMENT '商家ID',
  user_id         bigint(20) NOT NULL COMMENT '用户ID',
  record_id       varchar(32) NOT NULL COMMENT '指向消费类 bls_member_points_record.id',
  validity_id     varchar(32) NOT NULL COMMENT '指向被扣的批次 bls_member_points_validity.id',
  points          int NOT NULL COMMENT '本行扣减的积分（正数表示扣减量）',
  create_time     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by       varchar(36) DEFAULT NULL COMMENT '创建者',
  update_by       varchar(36) DEFAULT NULL COMMENT '更新者',
  is_delete       tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  KEY idx_record (record_id),
  KEY idx_validity (validity_id),
  KEY idx_user (tenant_id, merchant_id, user_id),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分消费详情表';



-- 支付服务商配置（门店 > 商户 > 租户 覆盖）
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
  select tenant_id, merchant_id, store_id, sub_mch_id
from bls_pay_channel_config
where app_id = #{appId}
  and tenant_id = #{tenantId}
  and (store_id = #{storeId} or store_id is null)
  and (merchant_id = #{merchantId} or merchant_id is null)
order by (store_id is not null) desc, (merchant_id is not null) desc
limit 1;

 */


-- 租户所属用户表
CREATE TABLE bls_user_tenant (
     id              bigint(20) PRIMARY KEY,
     user_id         bigint(20) NOT NULL COMMENT '平台用户ID',
     tenant_id       varchar(20) NOT NULL COMMENT '租户编号',
     merchant_id       varchar(20) NOT NULL COMMENT '商户id',
     tenant_user_name varchar(50) DEFAULT NULL COMMENT '租户内的用户名/昵称',
     tenant_phone    varchar(20) DEFAULT NULL COMMENT '租户内绑定手机号',
     role            varchar(20) DEFAULT 'USER' COMMENT '角色(USER/MEMBER/ADMIN)',
     is_member       tinyint(1) DEFAULT 0 COMMENT '是否在该租户下是会员',
     status          tinyint NOT NULL DEFAULT 0 COMMENT '状态 0-正常 1-禁用 2-注销',
     first_time      datetime NOT NULL COMMENT '首次登录时间',
     last_time       datetime NOT NULL COMMENT '最后登录时间',
     create_time     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     update_time     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     `create_by` varchar(36) DEFAULT NULL,
     `update_by` varchar(36) DEFAULT NULL,
     `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
     UNIQUE KEY uk_user_tenant (user_id, tenant_id),
     KEY idx_tenant (tenant_id),
     KEY idx_user (user_id),
     KEY idx_role (tenant_id, role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台用户-租户映射';

-- 账户流水
-- 账户流水一级菜单
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (5000, '账户流水', 0, 5, 'wallet', null, 1, 0, 'M', '0', '0', null, 'example', 1, now(), '台球管理目录');


-- 菜单 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(5001, '用户钱包账户', '5000', '1', 'walletAccount', 'billiards/walletAccount/index', 1, 0, 'C', '0', '0', 'billiards:walletAccount:list', '#', 103, 1, sysdate(), null, null, '用户钱包账户菜单');

-- 按钮 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(50011, '用户钱包账户查询', 5001, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:walletAccount:query',        '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(50012, '用户钱包账户新增', 5001, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:walletAccount:add',          '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(50013, '用户钱包账户修改', 5001, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:walletAccount:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(50014, '用户钱包账户删除', 5001, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:walletAccount:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(50015, '用户钱包账户导出', 5001, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:walletAccount:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(5002, '用户钱包流水', '5000', '1', 'walletTransaction', 'billiards/walletTransaction/index', 1, 0, 'C', '0', '0', 'billiards:walletTransaction:list', '#', 103, 1, sysdate(), null, null, '用户钱包流水菜单');

-- 按钮 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(50021, '用户钱包流水查询', 5002, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:walletTransaction:query',        '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(50022, '用户钱包流水新增', 5002, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:walletTransaction:add',          '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(50023, '用户钱包流水修改', 5002, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:walletTransaction:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(50024, '用户钱包流水删除', 5002, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:walletTransaction:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(50025, '用户钱包流水导出', 5002, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:walletTransaction:export',       '#', 103, 1, sysdate(), null, null, '');


-- 初始化会员等级配置数据
INSERT INTO `bls_member_level_config` (`id`, `tenant_id`, `merchant_id`, `level_code`, `level_name`, `required_amount`, `discount`, `monthly_free_minutes`,
                                   `points_multiplier`, `birthday_discount`, `friend_privilege_count`, `vip_service`, `reservation_privilege`, `description`)
VALUES
    ('1','000000','1', 'normal', '普通会员', 0, 0.95, 0, 1.00, NULL, 0, 0, 0, '入门级别会员，享受9.5折优惠'),
    ('2','000000','1', 'silver', '银牌会员', 1000, 0.90, 0, 1.20, 0.85, 0, 0, 1, '中级会员，享受9折优惠，生日特惠8.5折'),
    ('3','000000','1', 'gold', '金牌会员', 3000, 0.85, 120, 1.50, 0.80, 1, 1, 1, '高级会员，享受8.5折优惠，每月赠送2小时'),
    ('4','000000','1', 'diamond', '钻石会员', 10000, 0.80, 300, 2.00, 0.75, 2, 1, 1, '至尊级别，享受8折优惠，每月赠送5小时，可带2位朋友享会员价');

-- 初始化基础积分规则
INSERT INTO `bls_points_rule` (`id` , `tenant_id`, `merchant_id`, `name`, `type`, `scene`, `value_type`, `points_value`, `description`, `status`)
VALUES
    ('1','000000','1', '消费积分', 1, 1, 1, 1.00, '每消费1元获得1积分', 1),
    ('2','000000','1', '每日签到', 1, 2, 1, 5.00, '每日签到获得5积分', 1),
    ('3','000000','1', '首次绑定', 1, 5, 1, 100.00, '首次绑定手机号获得100积分', 1),
    ('4','000000','1', '邀请好友', 1, 6, 1, 50.00, '成功邀请好友获得50积分', 1),
    ('5','000000','1', '积分抵扣', 2, 1, 1, 0.01, '100积分抵扣1元', 1);

-- 菜单 SQL

-- 台球管理一级菜单
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2000, '台球管理', 0, 5, 'billiards', null, 1, 0, 'M', '0', '0', null, 'example', 1, now(), '台球管理目录');

-- 门店管理
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2001, '门店管理', 2000, 1, 'store', 'billiards/store/index', 1, 0, 'C', '0', '0', 'billiards:store:list', 'store', 1, now(), '门店管理菜单');

-- 桌台管理
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2002, '桌台管理', 2000, 2, 'table', 'billiards/table/index', 1, 0, 'C', '0', '0', 'billiards:table:list', 'table', 1, now(), '桌台管理菜单');

-- 计费规则
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2003, '计费规则', 2000, 3, 'priceRule', 'billiards/priceRule/index', 1, 0, 'C', '0', '0', 'billiards:priceRule:list', 'money', 1, now(), '计费规则菜单');

-- 订单管理
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2004, '订单管理', 2000, 4, 'order', 'billiards/order/index', 1, 0, 'C', '0', '0', 'billiards:order:list', 'order', 1, now(), '订单管理菜单');

-- 营业概况
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2005, '营业概况', 2000, 5, 'dashboard', 'billiards/dashboard/index', 1, 0, 'C', '0', '0', 'billiards:dashboard:view', 'data-board', 1, now(), '营业概况菜单');

-- 门店管理按钮
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, perms, menu_type, visible, status, is_frame, is_cache, icon, create_by, create_time, remark) values
                                                                                                                                                                       (2010, '门店查询', 2001, 1, '', 'billiards:store:query', 'F', '0', '0', 1, 0, '#', 1, now(), '门店查询按钮'),
                                                                                                                                                                       (2011, '门店新增', 2001, 2, '', 'billiards:store:add', 'F', '0', '0', 1, 0, '#', 1, now(), '门店新增按钮'),
                                                                                                                                                                       (2012, '门店修改', 2001, 3, '', 'billiards:store:edit', 'F', '0', '0', 1, 0, '#', 1, now(), '门店修改按钮'),
                                                                                                                                                                       (2013, '门店删除', 2001, 4, '', 'billiards:store:remove', 'F', '0', '0', 1, 0, '#', 1, now(), '门店删除按钮'),
                                                                                                                                                                       (2014, '门店导出', 2001, 5, '', 'billiards:store:export', 'F', '0', '0', 1, 0, '#', 1, now(), '门店导出按钮');

-- 桌台管理按钮
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, perms, menu_type, visible, status, is_frame, is_cache, icon, create_by, create_time, remark) values
                                                                                                                                                                       (2020, '桌台查询', 2002, 1, '', 'billiards:table:query', 'F', '0', '0', 1, 0, '#', 1, now(), '桌台查询按钮'),
                                                                                                                                                                       (2021, '桌台新增', 2002, 2, '', 'billiards:table:add', 'F', '0', '0', 1, 0, '#', 1, now(), '桌台新增按钮'),
                                                                                                                                                                       (2022, '桌台修改', 2002, 3, '', 'billiards:table:edit', 'F', '0', '0', 1, 0, '#', 1, now(), '桌台修改按钮'),
                                                                                                                                                                       (2023, '桌台删除', 2002, 4, '', 'billiards:table:remove', 'F', '0', '0', 1, 0, '#', 1, now(), '桌台删除按钮'),
                                                                                                                                                                       (2024, '桌台导出', 2002, 5, '', 'billiards:table:export', 'F', '0', '0', 1, 0, '#', 1, now(), '桌台导出按钮');

-- 计费规则按钮
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, perms, menu_type, visible, status, is_frame, is_cache, icon, create_by, create_time, remark) values
                                                                                                                                                                       (2030, '计费规则查询', 2003, 1, '', 'billiards:priceRule:query', 'F', '0', '0', 1, 0, '#', 1, now(), '计费规则查询按钮'),
                                                                                                                                                                       (2031, '计费规则新增', 2003, 2, '', 'billiards:priceRule:add', 'F', '0', '0', 1, 0, '#', 1, now(), '计费规则新增按钮'),
                                                                                                                                                                       (2032, '计费规则修改', 2003, 3, '', 'billiards:priceRule:edit', 'F', '0', '0', 1, 0, '#', 1, now(), '计费规则修改按钮'),
                                                                                                                                                                       (2033, '计费规则删除', 2003, 4, '', 'billiards:priceRule:remove', 'F', '0', '0', 1, 0, '#', 1, now(), '计费规则删除按钮'),
                                                                                                                                                                       (2034, '计费规则导出', 2003, 5, '', 'billiards:priceRule:export', 'F', '0', '0', 1, 0, '#', 1, now(), '计费规则导出按钮');

-- 订单管理按钮
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, perms, menu_type, visible, status, is_frame, is_cache, icon, create_by, create_time, remark) values
                                                                                                                                                                       (2040, '订单查询', 2004, 1, '', 'billiards:order:query', 'F', '0', '0', 1, 0, '#', 1, now(), '订单查询按钮'),
                                                                                                                                                                       (2041, '订单新增', 2004, 2, '', 'billiards:order:add', 'F', '0', '0', 1, 0, '#', 1, now(), '订单新增按钮'),
                                                                                                                                                                       (2042, '订单修改', 2004, 3, '', 'billiards:order:edit', 'F', '0', '0', 1, 0, '#', 1, now(), '订单修改按钮'),
                                                                                                                                                                       (2043, '订单删除', 2004, 4, '', 'billiards:order:remove', 'F', '0', '0', 1, 0, '#', 1, now(), '订单删除按钮'),
                                                                                                                                                                       (2044, '订单导出', 2004, 5, '', 'billiards:order:export', 'F', '0', '0', 1, 0, '#', 1, now(), '订单导出按钮');

-- 营业概况按钮（如有）
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, perms, menu_type, visible, status, is_frame, is_cache, icon, create_by, create_time, remark) values
    (2050, '营业概况查看', 2005, 1, '', 'billiards:dashboard:view', 'F', '0', '0', 1, 0, '#', 1, now(), '营业概况查看按钮');



INSERT INTO billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, `path`, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES(3000, '会员管理', 0, 6, 'star', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'example', NULL, 1, '2025-04-27 16:37:19', NULL, NULL, '会员管理目录');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(3001, '积分规则配置', '3000', '1', 'pointsRule', 'billiards/pointsRule/index', 1, 0, 'C', '0', '0', 'billiards:pointsRule:list', '#', 103, 1, sysdate(), null, null, '积分规则菜单');

-- 按钮 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30011, '积分规则查询', 3001, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:pointsRule:query',        '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30012, '积分规则新增', 3001, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:pointsRule:add',          '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30013, '积分规则修改', 3001, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:pointsRule:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30014, '积分规则删除', 3001, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:pointsRule:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30015, '积分规则导出', 3001, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:pointsRule:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(3002, '会员用户', '3000', '1', 'memberUser', 'billiards/memberUser/index', 1, 0, 'C', '0', '0', 'billiards:memberUser:list', '#', 103, 1, sysdate(), null, null, '会员用户菜单');

-- 按钮 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30021, '会员用户查询', 3002, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberUser:query',        '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30022, '会员用户新增', 3002, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberUser:add',          '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30023, '会员用户修改', 3002, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberUser:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30024, '会员用户删除', 3002, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberUser:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30025, '会员用户导出', 3002, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberUser:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(3003, '积分有效期', '3000', '1', 'memberPointsValidity', 'billiards/memberPointsValidity/index', 1, 0, 'C', '1', '1', 'billiards:memberPointsValidity:list', '#', 103, 1, sysdate(), null, null, '积分有效期菜单');

-- 按钮 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30031, '积分有效期查询', 3003, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsValidity:query',        '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30032, '积分有效期新增', 3003, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsValidity:add',          '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30033, '积分有效期修改', 3003, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsValidity:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30034, '积分有效期删除', 3003, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsValidity:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30035, '积分有效期导出', 3003, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsValidity:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(3004, '会员积分记录', '3000', '1', 'memberPointsRecord', 'billiards/memberPointsRecord/index', 1, 0, 'C', '0', '0', 'billiards:memberPointsRecord:list', '#', 103, 1, sysdate(), null, null, '会员积分记录菜单');

-- 按钮 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30041, '会员积分记录查询', 3004, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsRecord:query',        '#', 103, 1, sysdate(), null, null, '');

-- insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
-- values(30042, '会员积分记录新增', 3004, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsRecord:add',          '#', 103, 1, sysdate(), null, null, '');
--
-- insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
-- values(30043, '会员积分记录修改', 3004, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsRecord:edit',         '#', 103, 1, sysdate(), null, null, '');
--
-- insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
-- values(30044, '会员积分记录删除', 3004, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsRecord:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30045, '会员积分记录导出', 3004, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberPointsRecord:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(3005, '会员等级配置', '3000', '1', 'memberLevelConfig', 'billiards/memberLevelConfig/index', 1, 0, 'C', '0', '0', 'billiards:memberLevelConfig:list', '#', 103, 1, sysdate(), null, null, '会员等级配置菜单');

-- 按钮 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30051, '会员等级配置查询', 3005, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberLevelConfig:query',        '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30052, '会员等级配置新增', 3005, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberLevelConfig:add',          '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30053, '会员等级配置修改', 3005, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberLevelConfig:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30054, '会员等级配置删除', 3005, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberLevelConfig:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30055, '会员等级配置导出', 3005, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberLevelConfig:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(3006, '会员权益配置', '3000', '1', 'memberBenefit', 'billiards/memberBenefit/index', 1, 0, 'C', '1', '1', 'billiards:memberBenefit:list', '#', 103, 1, sysdate(), null, null, '会员权益菜单');

-- 按钮 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30061, '会员权益查询', 3006, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberBenefit:query',        '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30062, '会员权益新增', 3006, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberBenefit:add',          '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30063, '会员权益修改', 3006, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberBenefit:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30064, '会员权益删除', 3006, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberBenefit:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(30065, '会员权益导出', 3006, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:memberBenefit:export',       '#', 103, 1, sysdate(), null, null, '');


-- 用户账单流水


insert into billiards_admin.sys_dict_type values(15, '000000', '启停状态', 'enable_status',    103, 1, sysdate(), null, null, '启停用列表');
insert into billiards_admin.sys_dict_type values(16, '000000', '积分规则类型', 'points_rule_type',    103, 1, sysdate(), null, null, '积分规则类型');
insert into billiards_admin.sys_dict_type values(17, '000000', '积分变更场景', 'points_scene',    103, 1, sysdate(), null, null, '积分变更场景');
insert into billiards_admin.sys_dict_type values(18, '000000', '积分变动类型', 'points_change_type',    103, 1, sysdate(), null, null, '积分变动类型');

insert into billiards_admin.sys_dict_data values(46, '000000', 1,  '停用',     '0',       'enable_status',  '',   'danger', 'N', 103, 1, sysdate(), null, null, '停用状态');
insert into billiards_admin.sys_dict_data values(47, '000000', 2,  '启用',     '1',       'enable_status',  '',   'primary',  'Y', 103, 1, sysdate(), null, null, '启用状态');

insert into billiards_admin.sys_dict_data values(48, '000000', 1,  '获取',     '1',       'points_rule_type',  '',   'primary',  'N', 103, 1, sysdate(), null, null, '获取积分');
insert into billiards_admin.sys_dict_data values(49, '000000', 2,  '消耗',     '2',       'points_rule_type',  '',   'danger',  'N', 103, 1, sysdate(), null, null, '消耗积分');


-- 积分场景：获取1消费 2签到 3活动 4评价 5首次绑定 6邀请；消耗21抵扣 22兑换商品 23兑换券
insert into billiards_admin.sys_dict_data values(50, '000000', 1,  '消费',     '1',       'points_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分变更场景');
insert into billiards_admin.sys_dict_data values(51, '000000', 2,  '签到',     '2',       'points_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分变更场景');
insert into billiards_admin.sys_dict_data values(52, '000000', 3,  '活动',     '3',       'points_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分变更场景');
insert into billiards_admin.sys_dict_data values(53, '000000', 4,  '评价',     '4',       'points_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分变更场景');
insert into billiards_admin.sys_dict_data values(54, '000000', 5,  '首次绑定',     '5',       'points_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分变更场景');
insert into billiards_admin.sys_dict_data values(55, '000000', 6,  '邀请',     '6',       'points_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分变更场景');
insert into billiards_admin.sys_dict_data values(56, '000000', 7,  '抵扣',     '21',       'points_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分变更场景');
insert into billiards_admin.sys_dict_data values(57, '000000', 8,  '兑换商品',     '22',       'points_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分变更场景');
insert into billiards_admin.sys_dict_data values(58, '000000', 9,  '兑换券',     '23',       'points_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分变更场景');

-- 积分变动类型：1-增加 2-消费 3-过期
insert into billiards_admin.sys_dict_data values(59, '000000', 1,  '增加',     '1',       'points_change_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分变动类型');
insert into billiards_admin.sys_dict_data values(60, '000000', 2,  '消费',     '2',       'points_change_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分变动类型');
insert into billiards_admin.sys_dict_data values(61, '000000', 3,  '过期',     '3',       'points_change_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分变动类型');

-- 会员权益 权益类型：1-折扣 2-赠送 3-积分 4-特权
insert into billiards_admin.sys_dict_type values(19, '000000', '会员权益类型', 'member_benefit_type',    103, 1, sysdate(), null, null, '会员权益类型');
insert into billiards_admin.sys_dict_data values(62, '000000', 1,  '折扣',     '1',       'member_benefit_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员权益类型');
insert into billiards_admin.sys_dict_data values(63, '000000', 2,  '赠送',     '2',       'member_benefit_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员权益类型');
insert into billiards_admin.sys_dict_data values(64, '000000', 3,  '积分',     '3',       'member_benefit_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员权益类型');
insert into billiards_admin.sys_dict_data values(65, '000000', 4,  '特权',     '4',       'member_benefit_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员权益类型');

-- 会员等级 普通、白银、黄金、钻石
insert into billiards_admin.sys_dict_type values(20, '000000', '会员等级', 'member_level_icon',    103, 1, sysdate(), null, null, '会员等级');
insert into billiards_admin.sys_dict_data values(66, '000000', 1,  '普通会员',     'normal',       'member_level_icon',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员等级');
insert into billiards_admin.sys_dict_data values(67, '000000', 2,  '白银会员',     'silver',       'member_level_icon',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员等级');
insert into billiards_admin.sys_dict_data values(68, '000000', 3,  '黄金会员',     'gold',       'member_level_icon',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员等级');
insert into billiards_admin.sys_dict_data values(69, '000000', 4,  '钻石会员',     'diamond',       'member_level_icon',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员等级');

-- 会员折扣率 95折、9折、85折、8折
insert into billiards_admin.sys_dict_type values(21, '000000', '会员折扣率', 'member_discount_rate',    103, 1, sysdate(), null, null, '会员折扣率');
insert into billiards_admin.sys_dict_data values(70, '000000', 1,  '95折',     '0.95',       'member_discount_rate',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员折扣率');
insert into billiards_admin.sys_dict_data values(71, '000000', 2,  '9折',     '0.90',       'member_discount_rate',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员折扣率');
insert into billiards_admin.sys_dict_data values(72, '000000', 3,  '85折',     '0.85',       'member_discount_rate',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员折扣率');
insert into billiards_admin.sys_dict_data values(73, '000000', 4,  '8折',     '0.80',       'member_discount_rate',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员折扣率');

-- 积分获取倍率 1倍、1.2倍、1.5倍、2倍
insert into billiards_admin.sys_dict_type values(22, '000000', '积分获取倍率', 'points_multiplier',    103, 1, sysdate(), null, null, '积分获取倍率');
insert into billiards_admin.sys_dict_data values(74, '000000', 1,  '1倍',     '1.00',       'points_multiplier',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分获取倍率');
insert into billiards_admin.sys_dict_data values(75, '000000', 2,  '1.2倍',     '1.20',       'points_multiplier',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分获取倍率');
insert into billiards_admin.sys_dict_data values(76, '000000', 3,  '1.5倍',     '1.50',       'points_multiplier',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分获取倍率');
insert into billiards_admin.sys_dict_data values(77, '000000', 4,  '2倍',     '2.00',       'points_multiplier',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分获取倍率');

-- 会员等级编码 normal、silver、gold、diamond
insert into billiards_admin.sys_dict_type values(23, '000000', '会员等级编码', 'member_level_code',    103, 1, sysdate(), null, null, '会员等级编码');
insert into billiards_admin.sys_dict_data values(78, '000000', 0,  '普通会员',     'normal',       'member_level_code',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员等级编码');
insert into billiards_admin.sys_dict_data values(79, '000000', 1,  '白银会员',     'silver',       'member_level_code',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员等级编码');
insert into billiards_admin.sys_dict_data values(80, '000000', 2,  '黄金会员',     'gold',       'member_level_code',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员等级编码');
insert into billiards_admin.sys_dict_data values(81, '000000', 3,  '钻石会员',     'diamond',       'member_level_code',  '',   '',  'N', 103, 1, sysdate(), null, null, '会员等级编码');

-- 积分值类型 1-固定值 2-百分比
insert into billiards_admin.sys_dict_type values(24, '000000', '积分值类型', 'points_value_type',    103, 1, sysdate(), null, null, '积分值类型');
insert into billiards_admin.sys_dict_data values(82, '000000', 1,  '固定值',     '1',       'points_value_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分值类型');
insert into billiards_admin.sys_dict_data values(83, '000000', 2,  '百分比',     '2',       'points_value_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '积分值类型');

-- 交易类型
insert into billiards_admin.sys_dict_type values(25, '000000', '交易类型', 'transaction_type',    103, 1, sysdate(), null, null, '交易类型');
insert into billiards_admin.sys_dict_data values(84, '000000', 1,  '充值',     'RECHARGE',       'transaction_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '交易类型');
insert into billiards_admin.sys_dict_data values(85, '000000', 2,  '消费',     'CONSUME',       'transaction_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '交易类型');
insert into billiards_admin.sys_dict_data values(86, '000000', 3,  '退款',     'REFUND',       'transaction_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '交易类型');

CREATE TABLE `bls_event_outbox` (
                                    `id` varchar(36) NOT NULL COMMENT '事件ID',
                                    `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                    `merchant_id` varchar(36) NOT NULL COMMENT '商家ID',
                                    `aggregate_type` varchar(32) NOT NULL COMMENT '聚合根类型，如 ORDER',
                                    `aggregate_id` varchar(64) NOT NULL COMMENT '聚合根ID，如订单ID',
                                    `event_type` varchar(64) NOT NULL COMMENT '事件类型，如 ORDER_COMPLETED/REFUND_REQUESTED',
                                    `payload` text NOT NULL COMMENT '事件载荷JSON',
                                    `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-NEW 1-SENT 2-FAILED 3-PROCESSING',
                                    `retry_count` int NOT NULL DEFAULT 0 COMMENT '重试次数',
                                    `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间',
                                    `last_error` varchar(255) DEFAULT NULL COMMENT '最后一次错误信息',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                    `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                    `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_idem` (`tenant_id`, `aggregate_type`, `aggregate_id`, `event_type`),
                                    KEY `idx_tenant_status` (`tenant_id`, `status`, `create_time`),
                                    KEY `idx_agg` (`aggregate_type`, `aggregate_id`),
                                    KEY `idx_merchant_time` (`merchant_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本地消息表(Outbox)';


-- 门店状态 0-营业中 1-休息中 2-已满 3-停业
insert into billiards_admin.sys_dict_type values(26, '000000', '门店状态', 'store_business_status',    103, 1, sysdate(), null, null, '门店状态');
insert into billiards_admin.sys_dict_data values(87, '000000', 0,  '营业中',     '0',       'store_business_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '门店状态');
insert into billiards_admin.sys_dict_data values(88, '000000', 1,  '休息中',     '1',       'store_business_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '门店状态');
insert into billiards_admin.sys_dict_data values(89, '000000', 2,  '已满',     '2',       'store_business_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '门店状态');
insert into billiards_admin.sys_dict_data values(90, '000000', 3,  '停业',     '3',       'store_business_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '门店状态');

-- 桌台状态 table_status 0-空闲 1-使用中 2-维修中 3-锁定
insert into billiards_admin.sys_dict_type values(27, '000000', '桌台状态', 'table_status',    103, 1, sysdate(), null, null, '桌台状态');
insert into billiards_admin.sys_dict_data values(91, '000000', 0,  '空闲',     '0',       'table_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '桌台状态');
insert into billiards_admin.sys_dict_data values(92, '000000', 1,  '使用中',     '1',       'table_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '桌台状态');
insert into billiards_admin.sys_dict_data values(93, '000000', 2,  '维修中',     '2',       'table_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '桌台状态');
insert into billiards_admin.sys_dict_data values(94, '000000', 3,  '锁定',     '3',       'table_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '桌台状态');

-- 订单状态 order_status 0-进行中 1-已完成 2-已取消
insert into billiards_admin.sys_dict_type values(28, '000000', '订单状态', 'order_status',    103, 1, sysdate(), null, null, '订单状态');
insert into billiards_admin.sys_dict_data values(95, '000000', 0,  '进行中',     '0',       'order_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '订单状态');
insert into billiards_admin.sys_dict_data values(96, '000000', 1,  '已完成',     '1',       'order_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '订单状态');
insert into billiards_admin.sys_dict_data values(97, '000000', 2,  '已取消',     '2',       'order_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '订单状态');

-- 本地消息处理状态 0-待处理 1-处理中 2-处理完成 3-处理失败
insert into billiards_admin.sys_dict_type values(29, '000000', '本地消息处理状态', 'local_message_process_status',    103, 1, sysdate(), null, null, '本地消息处理状态');
insert into billiards_admin.sys_dict_data values(98, '000000', 0,  '待处理',     '0',       'local_message_process_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '本地消息处理状态');
insert into billiards_admin.sys_dict_data values(99, '000000', 1,  '处理完成',     '1',       'local_message_process_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '本地消息处理状态');
insert into billiards_admin.sys_dict_data values(100, '000000', 2,  '处理失败',     '2',       'local_message_process_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '本地消息处理状态');
insert into billiards_admin.sys_dict_data values(101, '000000', 3,  '处理中',     '3',       'local_message_process_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '本地消息处理状态');


-- ================================
-- 1. 设备表：iot_device
-- ================================
CREATE TABLE `bls_iot_device` (
                                  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                                  `merchant_id` varchar(36) NOT NULL COMMENT '所属商家ID',
                                  `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                  `code` VARCHAR(100) NOT NULL UNIQUE COMMENT '设备唯一编号',
                                  `name` VARCHAR(200) NOT NULL COMMENT '设备名称',
                                  `type` VARCHAR(50) NOT NULL COMMENT '设备类型：light/lock/speaker/other',
                                  `protocol` VARCHAR(50) NOT NULL COMMENT '协议类型：mqtt/http/modbus',
                                  `protocol_config` JSON NULL COMMENT '协议配置（topic/ip/port等）',
                                  `status` VARCHAR(20) DEFAULT 'offline' COMMENT '设备状态：online/offline/error',
                                  `last_heartbeat` DATETIME NULL COMMENT '最后心跳时间',
                                  `remark` VARCHAR(500) NULL COMMENT '备注信息',
                                  `create_time` datetime NOT NULL COMMENT '创建时间',
                                  `update_time` datetime NOT NULL COMMENT '更新时间',
                                  `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                  `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                  `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT设备表';


-- ================================
-- 2. 设备业务绑定表：iot_device_binding
-- ================================
CREATE TABLE `bls_iot_device_binding` (
                                          `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                                          `merchant_id` varchar(36) NOT NULL COMMENT '所属商家ID',
                                          `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                          `table_id`  varchar(32) NOT NULL COMMENT '台桌ID（外键）',
                                          `store_id`  varchar(32) NOT NULL COMMENT '门店ID',
                                          `scene` VARCHAR(50) NOT NULL COMMENT '业务场景：open_table/close_table/timeout等',
                                          `device_code` VARCHAR(100) NOT NULL COMMENT '设备编号（外键）',
                                          `command` VARCHAR(50) NOT NULL COMMENT '控制命令：turn_on/turn_off/play_audio等',
                                          `params` JSON NULL COMMENT '命令参数（如音量、文件名等）',
                                          `execute_order` INT DEFAULT 1 COMMENT '执行顺序（同场景多个设备时）',
                                          `mqtt_topic` VARCHAR(100)  COMMENT '绑定之后设备的MQTT主题',
                                          `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用：1启用，0禁用',
                                          `create_time` datetime NOT NULL COMMENT '创建时间',
                                          `update_time` datetime NOT NULL COMMENT '更新时间',
                                          `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                          `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                          `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                          CONSTRAINT fk_device_binding_device FOREIGN KEY (`device_code`) REFERENCES `bls_iot_device`(`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备业务绑定表（定义场景与设备动作映射）';

CREATE INDEX idx_binding_table_scene ON bls_iot_device_binding(table_id, scene);
CREATE INDEX idx_binding_device_code ON bls_iot_device_binding(device_code);


-- ================================
-- 3. 设备控制日志表：iot_control_log
-- ================================
CREATE TABLE `bls_iot_control_log` (
                                       `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                                       `merchant_id` varchar(36) NOT NULL COMMENT '所属商家ID',
                                       `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                       `device_code` VARCHAR(100) NOT NULL COMMENT '设备编号',
                                       `command` VARCHAR(50) NOT NULL COMMENT '控制命令',
                                       `params` JSON NULL COMMENT '命令参数',
                                       `trigger_by` VARCHAR(50) DEFAULT 'system' COMMENT '触发来源：order/admin/system',
                                       `trigger_scene` VARCHAR(50) NULL COMMENT '触发场景：open_table/close_table等',
                                       `order_id` BIGINT NULL COMMENT '关联订单ID',
                                       `table_id` BIGINT NULL COMMENT '关联台桌ID',
                                       `status` VARCHAR(20) DEFAULT 'pending' COMMENT '执行状态：success/failed/timeout/pending',
                                       `error_msg` TEXT NULL COMMENT '失败原因',
                                       `retry_count` INT DEFAULT 0 COMMENT '重试次数',
                                       `execute_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
                                       `response_time` INT NULL COMMENT '响应耗时（毫秒）',
                                       `create_time` datetime NOT NULL COMMENT '创建时间',
                                       `update_time` datetime NOT NULL COMMENT '更新时间',
                                       `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                       `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                       `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备控制日志表（记录执行命令历史）';

CREATE INDEX idx_log_device ON bls_iot_control_log(device_code);
CREATE INDEX idx_log_table ON bls_iot_control_log(table_id);
CREATE INDEX idx_log_status ON bls_iot_control_log(status);


-- ================================
-- 4. 设备告警表：iot_device_alert
-- ================================
CREATE TABLE `bls_iot_device_alert` (
                                        `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                                        `merchant_id` varchar(36) NOT NULL COMMENT '所属商家ID',
                                        `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                        `alert_type` VARCHAR(50) NOT NULL COMMENT '告警类型：offline/control_failed等',
                                        `alert_level` VARCHAR(20) DEFAULT 'warning' COMMENT '告警级别：info/warning/error/critical',
                                        `device_code` VARCHAR(100) NOT NULL COMMENT '设备编号',
                                        `device_name` VARCHAR(200) NULL COMMENT '设备名称',
                                        `table_id` BIGINT NULL COMMENT '关联台桌ID',
                                        `store_id` BIGINT NULL COMMENT '所属门店ID',
                                        `alert_content` TEXT NULL COMMENT '告警内容描述',
                                        `alert_data` JSON NULL COMMENT '告警详细数据（如失败次数、成功率等）',
                                        `status` VARCHAR(20) DEFAULT 'pending' COMMENT '处理状态：pending/resolved/ignored',
                                        `handler` VARCHAR(100) NULL COMMENT '处理人',
                                        `handle_time` DATETIME NULL COMMENT '处理时间',
                                        `handle_remark` TEXT NULL COMMENT '处理备注',
                                        `resolved_time` DATETIME NULL COMMENT '解决时间',
                                        `create_time` datetime NOT NULL COMMENT '告警产生时间',
                                        `update_time` datetime NOT NULL COMMENT '更新时间',
                                        `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                        `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                        `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备告警表（记录设备异常信息）';

CREATE INDEX idx_alert_device ON bls_iot_device_alert(device_code);
CREATE INDEX idx_alert_status ON bls_iot_device_alert(status);
CREATE INDEX idx_alert_store ON bls_iot_device_alert(store_id);


CREATE TABLE bls_iot_device_heartbeat_log (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                              `merchant_id` varchar(36) NOT NULL COMMENT '所属商家ID',
                                              `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                              device_code VARCHAR(64) NOT NULL COMMENT '设备编码（与iot_device表关联）',
                                              ip_address VARCHAR(64) NULL COMMENT '设备IP地址（可选）',
                                              firmware_version VARCHAR(32) NULL COMMENT '固件版本（可选）',
                                              heartbeat_time DATETIME NOT NULL COMMENT '心跳上报时间',
                                              signal_strength INT NULL COMMENT 'WiFi信号强度（dBm，可选）',
                                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              INDEX idx_device_time (device_code, heartbeat_time)
) COMMENT='IoT设备心跳记录表';

-- 日志表只需要记录7天的时间即可
CREATE EVENT ev_clear_heartbeat_log
ON SCHEDULE EVERY 1 DAY
DO
DELETE FROM bls_iot_device_heartbeat_log
WHERE heartbeat_time < NOW() - INTERVAL 7 DAY;


-- 字典数据：设备类型
insert into billiards_admin.sys_dict_type values(30, '000000', '设备类型', 'device_type',    103, 1, sysdate(), null, null, '设备类型');
insert into billiards_admin.sys_dict_data values(102, '000000', 0,  '灯',     'light',       'device_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备类型');
insert into billiards_admin.sys_dict_data values(103, '000000', 1,  '锁',     'lock',       'device_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备类型');
insert into billiards_admin.sys_dict_data values(104, '000000', 2,  '音箱',     'speaker',       'device_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备类型');
insert into billiards_admin.sys_dict_data values(105, '000000', 3,  '其他',     'other',       'device_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备类型');

-- 字典数据：设备状态
insert into billiards_admin.sys_dict_type values(31, '000000', '设备状态', 'device_status',    103, 1, sysdate(), null, null, '设备状态');
insert into billiards_admin.sys_dict_data values(106, '000000', 0,  '在线',     'online',       'device_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备状态');
insert into billiards_admin.sys_dict_data values(107, '000000', 1,  '离线',     'offline',       'device_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备状态');
insert into billiards_admin.sys_dict_data values(108, '000000', 2,  '故障',     'error',       'device_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备状态');

-- 字典数据：设备协议类型
insert into billiards_admin.sys_dict_type values(32, '000000', '设备协议类型', 'device_protocol_type',    103, 1, sysdate(), null, null, '设备协议类型');
insert into billiards_admin.sys_dict_data values(109, '000000', 0,  'MQTT',     'mqtt',       'device_protocol_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备协议类型');
insert into billiards_admin.sys_dict_data values(110, '000000', 1,  'HTTP',     'http',       'device_protocol_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备协议类型');
insert into billiards_admin.sys_dict_data values(111, '000000', 2,  'Modbus',     'modbus',       'device_protocol_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备协议类型');

-- 字典数据：设备场景
insert into billiards_admin.sys_dict_type values(33, '000000', '设备场景', 'device_scene',    103, 1, sysdate(), null, null, '设备场景');
insert into billiards_admin.sys_dict_data values(112, '000000', 0,  '开灯',     'turn_on',       'device_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备场景');
insert into billiards_admin.sys_dict_data values(113, '000000', 1,  '关灯',     'turn_off',       'device_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备场景');
insert into billiards_admin.sys_dict_data values(114, '000000', 2,  '开锁',     'unlock',       'device_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备场景');
insert into billiards_admin.sys_dict_data values(115, '000000', 3,  '关锁',     'lock',       'device_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备场景');
insert into billiards_admin.sys_dict_data values(116, '000000', 4,  '播放音频',     'play_audio',       'device_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备场景');
insert into billiards_admin.sys_dict_data values(117, '000000', 5,  '停止音频',     'stop_audio',       'device_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备场景');
insert into billiards_admin.sys_dict_data values(118, '000000', 6,  '其他',     'other',       'device_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备场景');

-- 字典数据：设备控制命令
insert into billiards_admin.sys_dict_type values(34, '000000', '设备控制命令', 'device_control_command',    103, 1, sysdate(), null, null, '设备控制命令');
insert into billiards_admin.sys_dict_data values(119, '000000', 0,  '开灯',     'turn_on',       'device_control_command',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令');
insert into billiards_admin.sys_dict_data values(120, '000000', 1,  '关灯',     'turn_off',       'device_control_command',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令');
insert into billiards_admin.sys_dict_data values(121, '000000', 2,  '开锁',     'unlock',       'device_control_command',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令');
insert into billiards_admin.sys_dict_data values(122, '000000', 3,  '关锁',     'lock',       'device_control_command',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令');
insert into billiards_admin.sys_dict_data values(123, '000000', 4,  '播放音频',     'play_audio',       'device_control_command',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令');
insert into billiards_admin.sys_dict_data values(124, '000000', 5,  '停止音频',     'stop_audio',       'device_control_command',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令');
insert into billiards_admin.sys_dict_data values(125, '000000', 6,  '其他',     'other',       'device_control_command',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令');

-- 字典数据：设备控制命令触发来源
insert into billiards_admin.sys_dict_type values(35, '000000', '设备控制命令触发来源', 'device_control_command_trigger_source',    103, 1, sysdate(), null, null, '设备控制命令触发来源');
insert into billiards_admin.sys_dict_data values(126, '000000', 0,  '订单',     'order',       'device_control_command_trigger_source',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令触发来源');
insert into billiards_admin.sys_dict_data values(127, '000000', 1,  '管理员',     'admin',       'device_control_command_trigger_source',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令触发来源');
insert into billiards_admin.sys_dict_data values(128, '000000', 2,  '系统',     'system',       'device_control_command_trigger_source',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令触发来源');

-- 字典数据：设备控制命令执行状态
insert into billiards_admin.sys_dict_type values(36, '000000', '设备控制命令执行状态', 'device_control_command_execute_status',    103, 1, sysdate(), null, null, '设备控制命令执行状态');
insert into billiards_admin.sys_dict_data values(129, '000000', 0,  '成功',     'success',       'device_control_command_execute_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令执行状态');
insert into billiards_admin.sys_dict_data values(130, '000000', 1,  '失败',     'failed',       'device_control_command_execute_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令执行状态');
insert into billiards_admin.sys_dict_data values(131, '000000', 2,  '超时',     'timeout',       'device_control_command_execute_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令执行状态');
insert into billiards_admin.sys_dict_data values(132, '000000', 3,  '待处理',     'pending',       'device_control_command_execute_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令执行状态');

-- 字典数据：设备控制命令处理状态
insert into billiards_admin.sys_dict_type values(37, '000000', '设备控制命令处理状态', 'device_control_command_process_status',    103, 1, sysdate(), null, null, '设备控制命令处理状态');
insert into billiards_admin.sys_dict_data values(133, '000000', 0,  '待处理',     'pending',       'device_control_command_process_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令处理状态');
insert into billiards_admin.sys_dict_data values(134, '000000', 1,  '处理中',     'processing',       'device_control_command_process_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令处理状态');
insert into billiards_admin.sys_dict_data values(135, '000000', 2,  '处理完成',     'completed',       'device_control_command_process_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令处理状态');
insert into billiards_admin.sys_dict_data values(136, '000000', 3,  '处理失败',     'failed',       'device_control_command_process_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令处理状态');

-- 字典数据：设备控制命令处理结果
insert into billiards_admin.sys_dict_type values(38, '000000', '设备控制命令处理结果', 'device_control_command_process_result',    103, 1, sysdate(), null, null, '设备控制命令处理结果');
insert into billiards_admin.sys_dict_data values(137, '000000', 0,  '成功',     'success',       'device_control_command_process_result',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令处理结果');
insert into billiards_admin.sys_dict_data values(138, '000000', 1,  '失败',     'failed',       'device_control_command_process_result',  '',   '',  'N', 103, 1, sysdate(), null, null, '设备控制命令处理结果');

-- 字典数据：告警级别
insert into billiards_admin.sys_dict_type values(39, '000000', '告警级别', 'alert_level',    103, 1, sysdate(), null, null, '告警级别');
insert into billiards_admin.sys_dict_data values(139, '000000', 0,  '信息',     'info',       'alert_level',  '',   '',  'N', 103, 1, sysdate(), null, null, '告警级别');
insert into billiards_admin.sys_dict_data values(140, '000000', 1,  '警告',     'warning',       'alert_level',  '',   '',  'N', 103, 1, sysdate(), null, null, '告警级别');
insert into billiards_admin.sys_dict_data values(141, '000000', 2,  '错误',     'error',       'alert_level',  '',   '',  'N', 103, 1, sysdate(), null, null, '告警级别');
insert into billiards_admin.sys_dict_data values(142, '000000', 3,  '严重',     'critical',       'alert_level',  '',   '',  'N', 103, 1, sysdate(), null, null, '告警级别');

-- 字典数据：告警处理状态
insert into billiards_admin.sys_dict_type values(40, '000000', '告警处理状态', 'alert_process_status',    103, 1, sysdate(), null, null, '告警处理状态');
insert into billiards_admin.sys_dict_data values(143, '000000', 0,  '待处理',     'pending',       'alert_process_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '告警处理状态');
insert into billiards_admin.sys_dict_data values(144, '000000', 1,  '处理中',     'processing',       'alert_process_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '告警处理状态');
insert into billiards_admin.sys_dict_data values(145, '000000', 2,  '处理完成',     'completed',       'alert_process_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '告警处理状态');

-- 字典数据： 告警类型
insert into billiards_admin.sys_dict_type values(41, '000000', '告警类型', 'alert_type',    103, 1, sysdate(), null, null, '告警类型');
insert into billiards_admin.sys_dict_data values(146, '000000', 0,  '离线',     'offline',       'alert_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '告警类型');
insert into billiards_admin.sys_dict_data values(147, '000000', 1,  '故障',     'error',       'alert_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '告警类型');
insert into billiards_admin.sys_dict_data values(148, '000000', 2,  '超时',     'timeout',       'alert_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '告警类型');
insert into billiards_admin.sys_dict_data values(149, '000000', 3,  '其他',     'other',       'alert_type',  '',   '',  'N', 103, 1, sysdate(), null, null, '告警类型');

-- 字典数据： 触发场景
insert into billiards_admin.sys_dict_type values(42, '000000', '触发场景', 'trigger_scene',    103, 1, sysdate(), null, null, '触发场景');
insert into billiards_admin.sys_dict_data values(150, '000000', 0,  '开台',     'open_table',       'trigger_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '触发场景');
insert into billiards_admin.sys_dict_data values(151, '000000', 1,  '关台',     'close_table',       'trigger_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '触发场景');
insert into billiards_admin.sys_dict_data values(152, '000000', 2,  '超时提醒',     'timeout_warning',       'trigger_scene',  '',   '',  'N', 103, 1, sysdate(), null, null, '触发场景');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (6000, 'iot设备管理', 0, 6, 'iot', null, 1, 0, 'M', '0', '0', null, 'iot', 1, now(), 'iot设备及配置管理');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(60001, '设备控制日志', 6000, '1', 'iotControlLog', 'billiards/iotControlLog/index', 1, 0, 'C', '0', '0', 'billiards:iotControlLog:list', '#', 103, 1, sysdate(), null, null, '设备控制日志（记录执行命令历史）菜单');

-- 按钮 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600011, '设备控制日志查询', 60001, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotControlLog:query',        '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600012, '设备控制日志新增', 60001, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotControlLog:add',          '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600013, '设备控制日志修改', 60001, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotControlLog:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600014, '设备控制日志删除', 60001, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotControlLog:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600015, '设备控制日志导出', 60001, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotControlLog:export',       '#', 103, 1, sysdate(), null, null, '');


-- 菜单 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(60002, '设备告警记录', 6000, '1', 'iotDeviceAlert', 'billiards/iotDeviceAlert/index', 1, 0, 'C', '0', '0', 'billiards:iotDeviceAlert:list', '#', 103, 1, sysdate(), null, null, '设备告警（记录设备异常信息）菜单');

-- 按钮 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600021, '设备告警查询', 60002, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDeviceAlert:query',        '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600022, '设备告警新增', 60002, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDeviceAlert:add',          '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600023, '设备告警修改', 60002, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDeviceAlert:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600024, '设备告警删除', 60002, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDeviceAlert:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600025, '设备告警导出', 60002, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDeviceAlert:export',       '#', 103, 1, sysdate(), null, null, '');


-- 菜单 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(60003, '设备业务绑定', 6000, '1', 'iotDeviceBinding', 'billiards/iotDeviceBinding/index', 1, 0, 'C', '0', '0', 'billiards:iotDeviceBinding:list', '#', 103, 1, sysdate(), null, null, '设备业务绑定（定义场景与设备动作映射）菜单');

-- 按钮 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600031, '设备业务绑定查询', 60003, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDeviceBinding:query',        '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600032, '设备业务绑定新增', 60003, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDeviceBinding:add',          '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600033, '设备业务绑定修改', 60003, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDeviceBinding:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600034, '设备业务绑定删除', 60003, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDeviceBinding:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600035, '设备业务绑定导出', 60003, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDeviceBinding:export',       '#', 103, 1, sysdate(), null, null, '');

-- 菜单 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(60004, 'IoT设备', 6000, '1', 'iotDevice', 'billiards/iotDevice/index', 1, 0, 'C', '0', '0', 'billiards:iotDevice:list', '#', 103, 1, sysdate(), null, null, 'IoT设备菜单');

-- 按钮 SQL
insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600041, 'IoT设备查询', 60004, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDevice:query',        '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600042, 'IoT设备新增', 60004, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDevice:add',          '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600043, 'IoT设备修改', 60004, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDevice:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600044, 'IoT设备删除', 60004, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDevice:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into billiards_admin.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(600045, 'IoT设备导出', 60004, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:iotDevice:export',       '#', 103, 1, sysdate(), null, null, '');


-- 新增预约功能

USE `billiards_saas`;
CREATE TABLE bls_reservation (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                 `merchant_id` varchar(36) NOT NULL COMMENT '所属商家ID',
                                 `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                 reservation_no VARCHAR(64) NOT NULL COMMENT '预约编号（可用于展示或查询）',
                                 user_id BIGINT NOT NULL COMMENT '预约用户ID',
                                 store_id VARCHAR(32) NOT NULL COMMENT '门店ID',
                                 table_id VARCHAR(32) NOT NULL COMMENT '台球桌ID',
                                 `table_number` varchar(10) DEFAULT NULL COMMENT '桌台号',
                                 `store_name` varchar(100) DEFAULT NULL COMMENT '门店名称',
                                 start_time DATETIME NOT NULL COMMENT '预约开始时间',
                                 end_time DATETIME NOT NULL COMMENT '预约结束时间',
                                 status TINYINT DEFAULT 0 COMMENT '状态：0=预约中,1=已到店,3=已取消,4=已过期',
                                 pay_status TINYINT DEFAULT 0 COMMENT '支付状态：0=未支付,1=已支付,2=已退款',
                                 pay_amount DECIMAL(10,2) DEFAULT 0 COMMENT '支付金额(定金：元)',
                                 pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
                                 check_in_time DATETIME DEFAULT NULL COMMENT '到店确认时间（扫码/签到）',
                                 cancel_time DATETIME DEFAULT NULL COMMENT '取消时间',
                                 remark VARCHAR(255) DEFAULT NULL COMMENT '备注（如包厢号、特殊说明）',
                                 `create_time` datetime NOT NULL COMMENT '告警产生时间',
                                 `update_time` datetime NOT NULL COMMENT '更新时间',
                                 `create_by` varchar(36) DEFAULT NULL COMMENT '创建者',
                                 `update_by` varchar(36) DEFAULT NULL COMMENT '更新者',
                                 `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                 UNIQUE KEY uk_reservation_no (reservation_no),
                                 INDEX idx_user (user_id),
                                 KEY `idx_merchant_id` (`merchant_id`),
                                 KEY `idx_tenant` (`tenant_id`),
                                 KEY `idx_tenant_merchant` (`tenant_id`, `merchant_id`),
                                 INDEX idx_table_time (table_id, start_time, end_time),
                                 INDEX idx_tennant_table_time (tenant_id, table_id, start_time, end_time),
                                 INDEX idx_table_status_time (`table_id`, `status`, `start_time`, `end_time`)
) COMMENT='用户预约记录表';


USE `billiards_admin`;
-- 预约配置
INSERT INTO sys_config (config_id, tenant_id, config_name, config_key, config_value, config_type, remark, create_time) VALUES
                                                                                                                           (2001, '000000', '每次最短预约时长（分钟）', 'reserve.min_duration_minutes', '30', 'N', '每次最短预约时长，单位：分钟', NOW()),
                                                                                                                           (2002, '000000', '每次最长预约时长（分钟）', 'reserve.max_duration_minutes', '120', 'N', '每次最长预约时长，单位：分钟', NOW()),
                                                                                                                           (2003, '000000', '最多可提前预约天数', 'reserve.advance_reserve_days', '7', 'N', '用户最多可提前预约天数', NOW()),
                                                                                                                           (2005, '000000', '开始后未签到自动过期（分钟）', 'reserve.expire_after_start_minutes', '10', 'N', '预约开始后多少分钟未签到则自动过期并释放', NOW()),
                                                                                                                           (2006, '000000', '开始前提醒时间（分钟）', 'reserve.remind_before_start_minutes', '15', 'N', '预约开始前多少分钟发送提醒', NOW()),

                                                                                                                           (2008, '000000', '是否启用定金/预付', 'reserve.require_deposit', 'false', 'N', '是否在预约时要求支付定金（true/false）', NOW()),
                                                                                                                           (2009, '000000', '定金金额（元）', 'reserve.deposit_amount', '10.00', 'N', '定金默认金额，单位：元', NOW()),
                                                                                                                           (2010, '000000', '定金退款策略', 'reserve.deposit_refund_policy', 'before_30min', 'N', '定金退款策略示例：before_30min/never/always', NOW()),
                                                                                                                           (2011, '000000', '取消/不可退宽限（分钟）', 'reserve.refund_grace_period_minutes', '30', 'N', '开始前多少分钟内取消不可退款（配合refund_policy）', NOW()),

                                                                                                                           (2012, '000000', '每用户每日最大预约次数', 'reserve.max_per_day', '2', 'N', '每个用户每天最多可创建多少次预约', NOW()),
                                                                                                                           (2013, '000000', '单用户最大进行中预约数', 'reserve.max_pending_reservations', '1', 'N', '单个用户同时存在的未完成/进行中预约上限', NOW()),
                                                                                                                           (2014, '000000', '连续爽约次数阈值', 'reserve.ban_after_no_show_count', '3', 'N', '连续多少次爽约后触发封禁或强制策略', NOW()),
                                                                                                                           (2015, '000000', '爽约封禁天数', 'reserve.ban_days_after_no_show', '7', 'N', '触发封禁后禁止预约的天数', NOW()),

                                                                                                                           (2016, '000000', '门店是否启用预约功能（默认）', 'reserve.enable_for_store', 'true', 'N', '门店级开关的默认值（具体门店可覆盖）', NOW()),
                                                                                                                           (2017, '000000', '单次最多可预约桌数', 'reserve.max_tables_per_user', '1', 'N', '单次预约最多可占用几张桌子', NOW()),
                                                                                                                           (2018, '000000', '时间粒度（分钟）', 'reserve.time_unit_minutes', '30', 'N', '预约时间的粒度（例如30分钟一档）', NOW()),
                                                                                                                           (2027, '000000', '预约可选时间段', 'reserve.opening_hours', '10:00-23:00', 'N', '预约可选时间段（例如10:00-23:00`）', NOW()),


                                                                                                                           (2019, '000000', '是否启用提醒推送', 'reserve.enable_reminder', 'true', 'N', '是否开启预约提醒', NOW()),
                                                                                                                           (2020, '000000', '微信提醒模板ID', 'reserve.reminder_template_id', 'WX_TEMPLATE_RESERVE', 'N', '微信模板消息ID（示例，替换真实ID）', NOW()),
                                                                                                                           (2021, '000000', '取消通知微信模板消息ID', 'reserve.cancel_notify_template_id', 'WX_TEMPLATE_CANCEL_NOTIFY', 'N', '取消通知微信模板消息ID', NOW()),
                                                                                                                           (2022, '000000', '是否推送取消通知', 'reserve.notify_on_cancel', 'true', 'N', '是否推送取消通知', NOW()),


                                                                                                                           (2023, '000000', '定时任务检查间隔（分钟）', 'reserve.auto_check_interval_minutes', '5', 'N', '后台定时任务执行检查的间隔，单位：分钟', NOW()),
                                                                                                                           (2025, '000000', '管理员是否允许覆写预约', 'reserve.allow_admin_override', 'true', 'N', '管理员是否可手动调整预约', NOW()),
                                                                                                                           (2026, '000000', '是否记录预约操作日志', 'reserve.allow_admin_override', 'true', 'N', '是否记录预约操作日志', NOW());


-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(7000, '预约管理', '0', '7', 'reserve', null, 1, 0, 'M', '0', '0', null, 'reserve', 103, 1, sysdate(), null, null, '用户预约记录菜单');



insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(70001, '用户预约管理', '7000', '1', 'reservation', 'billiards/reservation/index', 1, 0, 'C', '0', '0', 'billiards:reservation:list', '#', 103, 1, sysdate(), null, null, '用户预约记录菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(700011, '用户预约记录查询', 70001, '1',  '#', '', 1, 0, 'F', '0', '0', 'billiards:reservation:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(700012, '用户预约记录新增', 70001, '2',  '#', '', 1, 0, 'F', '0', '0', 'billiards:reservation:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(700013, '用户预约记录修改', 70001, '3',  '#', '', 1, 0, 'F', '0', '0', 'billiards:reservation:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(700014, '用户预约记录删除', 70001, '4',  '#', '', 1, 0, 'F', '0', '0', 'billiards:reservation:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(700015, '用户预约记录导出', 70001, '5',  '#', '', 1, 0, 'F', '0', '0', 'billiards:reservation:export',       '#', 103, 1, sysdate(), null, null, '');



-- 预约状态 reservation_status 0-预约中 1-已到店 2-已完成 3-已取消 4-已过期
insert into billiards_admin.sys_dict_type values(43, '000000', '预约状态', 'reservation_status',    103, 1, sysdate(), null, null, '预约状态');
insert into billiards_admin.sys_dict_data values(153, '000000', 0,  '预约中',     '0',       'reservation_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '预约状态');
insert into billiards_admin.sys_dict_data values(154, '000000', 1,  '已到店',     '1',       'reservation_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '预约状态');
insert into billiards_admin.sys_dict_data values(155, '000000', 2,  '已完成',     '2',       'reservation_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '预约状态');
insert into billiards_admin.sys_dict_data values(156, '000000', 3,  '已取消',     '3',       'reservation_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '预约状态');
insert into billiards_admin.sys_dict_data values(157, '000000', 4,  '已过期',     '4',       'reservation_status',  '',   '',  'N', 103, 1, sysdate(), null, null, '预约状态');


USE `billiards_saas`;
-- 定时任务执行记录表
CREATE TABLE bls_schedule_task_log (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                       task_code VARCHAR(64) NOT NULL COMMENT '任务编码',
                                       task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
                                       start_time DATETIME NOT NULL COMMENT '开始时间',
                                       end_time DATETIME NOT NULL COMMENT '结束时间',
                                       duration_ms BIGINT NOT NULL COMMENT '执行时长（毫秒）',
                                       status VARCHAR(10) NOT NULL COMMENT '状态：SUCCESS/FAIL',
                                       error_msg VARCHAR(255) DEFAULT NULL COMMENT '错误信息',
                                       create_time DATETIME NOT NULL COMMENT '创建时间',
                                       remark VARCHAR(255) DEFAULT NULL COMMENT '备注'
) COMMENT='定时任务执行记录表';



