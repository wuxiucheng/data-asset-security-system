-- 修复数据库表结构以匹配实体类定义
-- 执行时间: 2026-06-02
-- 问题: 实体类字段与数据库表结构不匹配导致500错误

USE data_asset_security;

-- 1. 修复 lineage_relation 表
ALTER TABLE lineage_relation 
ADD COLUMN IF NOT EXISTS transformation VARCHAR(500) COMMENT '转换逻辑描述' AFTER relation_type,
ADD COLUMN IF NOT EXISTS path VARCHAR(500) COMMENT '路径描述' AFTER level,
ADD COLUMN IF NOT EXISTS confidence INT COMMENT '置信度：0-100' AFTER path,
ADD COLUMN IF NOT EXISTS source VARCHAR(32) COMMENT '来源：MANUAL/AUTO/DISCOVERED' AFTER confidence;

-- 2. 修复 lc_policy 表（如果需要）
ALTER TABLE lc_policy
ADD COLUMN IF NOT EXISTS archive_action VARCHAR(32) COMMENT '归档动作' AFTER retention_days,
ADD COLUMN IF NOT EXISTS delete_action VARCHAR(32) COMMENT '删除动作' AFTER archive_action,
ADD COLUMN IF NOT EXISTS schedule_cron VARCHAR(100) COMMENT '调度Cron表达式' AFTER delete_action;

-- 3. 修复 data_standard 表（如果需要）
ALTER TABLE data_standard
ADD COLUMN IF NOT EXISTS definition TEXT COMMENT '定义' AFTER description,
ADD COLUMN IF NOT EXISTS constraints TEXT COMMENT '约束' AFTER definition,
ADD COLUMN IF NOT EXISTS example TEXT COMMENT '示例' AFTER constraints;

-- 4. 检查并创建缺失的表（如果不存在）

-- 创建 impact_analysis 表（如果不存在）
CREATE TABLE IF NOT EXISTS impact_analysis (
    analysis_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分析ID',
    change_asset_id BIGINT COMMENT '变更资产ID',
    change_field_id BIGINT COMMENT '变更字段ID',
    change_type VARCHAR(32) COMMENT '变更类型',
    impact_level VARCHAR(32) COMMENT '影响级别',
    impact_scope TEXT COMMENT '影响范围',
    affected_assets TEXT COMMENT '受影响资产',
    analysis_status VARCHAR(32) COMMENT '分析状态',
    analysis_time DATETIME COMMENT '分析时间',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影响分析表';

-- 创建 metadata_version 表（如果不存在）
CREATE TABLE IF NOT EXISTS metadata_version (
    version_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '版本ID',
    asset_id BIGINT COMMENT '资产ID',
    field_id BIGINT COMMENT '字段ID',
    version_number VARCHAR(32) COMMENT '版本号',
    change_type VARCHAR(32) COMMENT '变更类型',
    change_description TEXT COMMENT '变更描述',
    old_value TEXT COMMENT '旧值',
    new_value TEXT COMMENT '新值',
    change_time DATETIME COMMENT '变更时间',
    changer_id BIGINT COMMENT '变更人ID',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='元数据版本表';

-- 创建 lc_status 表（如果不存在）
CREATE TABLE IF NOT EXISTS lc_status (
    status_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '状态ID',
    policy_id BIGINT COMMENT '策略ID',
    asset_id BIGINT COMMENT '资产ID',
    field_id BIGINT COMMENT '字段ID',
    lifecycle_stage VARCHAR(32) COMMENT '生命周期阶段',
    stage_start_time DATETIME COMMENT '阶段开始时间',
    stage_end_time DATETIME COMMENT '阶段结束时间',
    days_remaining INT COMMENT '剩余天数',
    last_action_time DATETIME COMMENT '最后动作时间',
    last_action VARCHAR(32) COMMENT '最后动作',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生命周期状态表';

-- 创建 compliance_clause 表（如果不存在）
CREATE TABLE IF NOT EXISTS compliance_clause (
    clause_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '条款ID',
    clause_name VARCHAR(128) COMMENT '条款名称',
    clause_code VARCHAR(64) COMMENT '条款编码',
    regulation VARCHAR(128) COMMENT '法规',
    description TEXT COMMENT '描述',
    requirements TEXT COMMENT '要求',
    status VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合规条款表';

-- 创建 governance_kpi 表（如果不存在）
CREATE TABLE IF NOT EXISTS governance_kpi (
    kpi_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'KPI ID',
    kpi_name VARCHAR(128) COMMENT 'KPI名称',
    kpi_code VARCHAR(64) COMMENT 'KPI编码',
    kpi_type VARCHAR(32) COMMENT 'KPI类型',
    target_value DECIMAL(10,2) COMMENT '目标值',
    actual_value DECIMAL(10,2) COMMENT '实际值',
    unit VARCHAR(32) COMMENT '单位',
    period VARCHAR(32) COMMENT '周期',
    status VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='治理KPI表';

-- 验证修复
SELECT 'Table structure fix completed!' AS message;
