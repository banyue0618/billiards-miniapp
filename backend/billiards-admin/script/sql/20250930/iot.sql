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




