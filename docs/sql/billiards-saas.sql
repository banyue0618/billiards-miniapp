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
                            `is_member` tinyint DEFAULT 0 COMMENT '是否是会员 0-否 1-是',
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

-- 会员变更记录表
CREATE TABLE `bls_member_change_log` (
                                         `id` varchar(36) NOT NULL COMMENT '记录ID',
                                         `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                         `merchant_id` varchar(36) NOT NULL COMMENT '商家ID',
                                         `user_id` varchar(36) NOT NULL COMMENT '用户ID',
                                         `store_id` varchar(36) NOT NULL COMMENT '门店ID',
                                         `change_type` varchar(20) NOT NULL COMMENT '变更类型 NEW/RENEWAL/UPGRADE/EXPIRED',
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
                                         KEY `idx_create_time` (`create_time`),
                                         KEY `idx_tenant_merchant` (`tenant_id`, `merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员变更记录表';

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

-- 会员等级配置（商户可自定义；NULL表示租户级模板）
CREATE TABLE `bls_member_level_config` (
                                           `id` varchar(32) NOT NULL COMMENT '配置ID',
                                           `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
                                           `merchant_id` varchar(36) DEFAULT NULL COMMENT '商家ID(可空=租户级模板)',
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
                                   `scene` tinyint(2) NOT NULL COMMENT '积分场景：获取1消费 2签到 3活动 4评价 5首次绑定 6邀请；消耗1抵扣 2兑换商品 3兑换券',
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
                                            `user_id` varchar(32) NOT NULL COMMENT '用户ID',
                                            `points` int NOT NULL COMMENT '积分数量（正数获得，负数消耗）',
                                            `type` tinyint(1) NOT NULL COMMENT '类型：1-获取 2-消耗',
                                            `scene` tinyint(2) NOT NULL COMMENT '场景，对应规则表',
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
                                            KEY `idx_expire_time` (`expire_time`)
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
                                              `is_delete` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                              PRIMARY KEY (`id`),
                                              KEY `idx_tenant_merchant_user_expire` (`tenant_id`, `merchant_id`, `user_id`, `expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分有效期表';

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