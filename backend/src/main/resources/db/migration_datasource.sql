-- 增量迁移脚本：数据源配置功能
-- 执行前请确保已连接到 data_asset_security 数据库

-- 1. 创建数据源配置表
CREATE TABLE IF NOT EXISTS data_source_config (
    data_source_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '数据源ID',
    data_source_name VARCHAR(128) NOT NULL COMMENT '数据源名称',
    database_type VARCHAR(32) NOT NULL COMMENT '数据库类型：MYSQL, ORACLE, POSTGRESQL, SQLSERVER',
    host VARCHAR(128) NOT NULL COMMENT '数据库地址',
    port INT NOT NULL COMMENT '数据库端口',
    database_name VARCHAR(64) NOT NULL COMMENT '数据库名称',
    username VARCHAR(64) COMMENT '用户名',
    password VARCHAR(256) COMMENT '密码（加密存储）',
    connection_params VARCHAR(500) COMMENT '额外连接参数',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
    last_test_time DATETIME COMMENT '最后测试连接时间',
    last_test_result VARCHAR(500) COMMENT '最后测试连接结果',
    remarks VARCHAR(500) COMMENT '备注',
    creator_id BIGINT COMMENT '创建人ID',
    updater_id BIGINT COMMENT '更新人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_database_type (database_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据源配置表';

-- 2. data_asset 表新增 data_source_id 字段
ALTER TABLE data_asset ADD COLUMN IF NOT EXISTS data_source_id BIGINT COMMENT '关联数据源ID' AFTER table_name;
