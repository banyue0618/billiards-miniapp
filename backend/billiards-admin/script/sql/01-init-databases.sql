-- ==========================================
-- 台球厅SaaS系统数据库初始化脚本
-- ==========================================
-- 此脚本在MySQL容器启动时自动执行
-- 用于创建系统所需的数据库

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================
-- 创建数据库
-- ==========================================

-- 创建管理端数据库
CREATE DATABASE IF NOT EXISTS `billiards_admin`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 创建SaaS平台数据库
CREATE DATABASE IF NOT EXISTS `billiards_saas`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- ==========================================
-- 创建用户并授权
-- ==========================================

-- 创建应用专用用户（生产环境推荐）
-- 密码由部署脚本动态生成并替换
CREATE USER IF NOT EXISTS 'billiards_admin'@'%' IDENTIFIED BY '${BILLIARDS_DB_PASSWORD}';

-- 授权管理端数据库权限
GRANT ALL PRIVILEGES ON billiards_admin.* TO 'billiards_admin'@'%';

-- 授权SaaS平台数据库权限
GRANT ALL PRIVILEGES ON billiards_saas.* TO 'billiards_admin'@'%';

-- ==========================================
-- 配置root用户远程访问权限（Docker环境需要）
-- ==========================================

-- 为root用户添加远程访问权限
-- GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;

-- 刷新权限
FLUSH PRIVILEGES;

-- ==========================================
-- 使用root用户访问（Docker环境简化配置）
-- ==========================================
-- 注意：生产环境建议使用专用用户，不要直接使用root

-- 显示创建的数据库
SHOW DATABASES;

-- 输出初始化完成信息
SELECT '数据库初始化完成！' AS message;
SELECT 'billiards_admin数据库已创建' AS admin_db;
SELECT 'billiards_saas数据库已创建' AS saas_db;

SET FOREIGN_KEY_CHECKS = 1;
