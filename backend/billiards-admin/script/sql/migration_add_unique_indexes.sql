-- 数据库迁移脚本：添加唯一索引以保证并发幂等性
-- 执行时间：2025-10-11
-- 说明：为 bls_member_points_record 和 bls_event_outbox 表添加唯一索引

-- =====================================================================
-- 1. 为 bls_member_points_record 表添加唯一索引
-- 作用：防止同一业务ID（如订单ID）重复生成积分记录
-- =====================================================================

-- 检查并删除可能存在的重复数据（可选，根据实际情况决定是否执行）
-- DELETE t1 FROM bls_member_points_record t1
-- INNER JOIN bls_member_points_record t2
-- WHERE t1.id > t2.id
--   AND t1.business_id = t2.business_id
--   AND t1.type = t2.type
--   AND t1.scene = t2.scene;

-- 添加唯一索引（如果索引已存在会报错，可忽略）
ALTER TABLE bls_member_points_record 
ADD UNIQUE KEY `uk_business_id_type_scene` (`business_id`, `type`, `scene`);

-- =====================================================================
-- 2. 为 bls_event_outbox 表添加唯一索引（仅适用于 billiards 数据库）
-- 作用：防止同一聚合根和事件类型重复创建消息记录
-- =====================================================================

-- 检查并删除可能存在的重复数据（可选）
-- DELETE t1 FROM bls_event_outbox t1
-- INNER JOIN bls_event_outbox t2
-- WHERE t1.id > t2.id
--   AND t1.aggregate_type = t2.aggregate_type
--   AND t1.aggregate_id = t2.aggregate_id
--   AND t1.event_type = t2.event_type;

-- 添加唯一索引（如果索引已存在会报错，可忽略）
ALTER TABLE bls_event_outbox 
ADD UNIQUE KEY `uk_aggregate_event` (`aggregate_type`, `aggregate_id`, `event_type`);

-- 更新 status 字段注释，添加新的状态 3-PROCESSING
ALTER TABLE bls_event_outbox 
MODIFY COLUMN `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-NEW 1-SENT 2-FAILED 3-PROCESSING';

-- =====================================================================
-- 说明：
-- 1. business_id 唯一索引确保同一订单不会重复增加积分
-- 2. aggregate_event 唯一索引确保同一业务事件不会重复创建
-- 3. 如果表中已存在重复数据，需要先清理重复数据后再添加索引
-- 4. 多租户（SaaS）版本的索引已在 billiards-saas.sql 中定义
-- =====================================================================

