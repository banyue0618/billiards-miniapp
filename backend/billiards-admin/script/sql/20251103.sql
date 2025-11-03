-- 新增预约功能

CREATE TABLE bls_reservation (
     id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
     `merchant_id` varchar(36) NOT NULL COMMENT '所属商家ID',
     `tenant_id` varchar(20) NOT NULL COMMENT '租户编号',
     reservation_no VARCHAR(64) NOT NULL COMMENT '预约编号（可用于展示或查询）',
     user_id BIGINT NOT NULL COMMENT '预约用户ID',
     store_id VARCHAR(32) NOT NULL COMMENT '门店ID',
     table_id VARCHAR(32) NOT NULL COMMENT '台球桌ID',
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
     INDEX idx_tennant_table_time (tenant_id, table_id, start_time, end_time)
) COMMENT='用户预约记录表';


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
values(7000, '预约管理', '0', '7', 'reservation', 'reservation', 1, 0, 'C', '0', '0', null, 'reservation', 103, 1, sysdate(), null, null, '用户预约记录菜单');



insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(70001, '用户预约记录', '3', '1', 'reservation', 'billiards/reservation/index', 1, 0, 'C', '0', '0', 'billiards:reservation:list', '#', 103, 1, sysdate(), null, null, '用户预约记录菜单');

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



