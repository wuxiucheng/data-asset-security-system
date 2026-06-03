-- 分类分级辅助规则表
CREATE TABLE IF NOT EXISTS classification_assist_rule (
    rule_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规则ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_code VARCHAR(50) UNIQUE NOT NULL COMMENT '规则代码',
    rule_type VARCHAR(20) NOT NULL COMMENT '规则类型：FIELD_NAME-字段名匹配, FIELD_PATTERN-字段模式, FIELD_SAMPLE-样本匹配',

    -- 匹配条件
    field_name_pattern VARCHAR(200) COMMENT '字段名匹配模式（支持通配符）',
    field_value_pattern VARCHAR(500) COMMENT '字段值匹配模式（正则表达式）',
    sample_match_rule TEXT COMMENT '样本匹配规则JSON',

    -- 分级建议
    suggest_grading_id BIGINT COMMENT '建议分级ID',
    suggest_grading_reason VARCHAR(500) COMMENT '分级原因说明',
    priority INT DEFAULT 100 COMMENT '规则优先级（数字越小优先级越高）',

    -- 执行配置
    auto_apply TINYINT DEFAULT 0 COMMENT '是否自动应用：0-否，1-是',
    need_review TINYINT DEFAULT 1 COMMENT '是否需要人工审核：0-否，1-是',

    -- 状态
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用，INACTIVE-禁用',

    -- 统计
    apply_count INT DEFAULT 0 COMMENT '应用次数',
    last_apply_time DATETIME COMMENT '最后应用时间',

    -- 公共字段
    creator_id BIGINT,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updater_id BIGINT,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,

    INDEX idx_rule_type (rule_type),
    INDEX idx_status (status),
    INDEX idx_priority (priority)
) COMMENT='分类分级辅助规则';

-- 分类分级辅助结果表
CREATE TABLE IF NOT EXISTS classification_assist_result (
    result_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '结果ID',
    rule_id BIGINT NOT NULL COMMENT '规则ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    field_id BIGINT COMMENT '字段ID',

    -- 匹配信息
    match_type VARCHAR(20) COMMENT '匹配类型',
    match_value VARCHAR(500) COMMENT '匹配的值',

    -- 分级建议
    original_grading_id BIGINT COMMENT '原分级ID',
    suggest_grading_id BIGINT COMMENT '建议分级ID',
    grading_changed TINYINT DEFAULT 0 COMMENT '分级是否改变',

    -- 状态
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已批准，REJECTED-已拒绝',
    reviewer_id BIGINT COMMENT '审核人ID',
    review_time DATETIME COMMENT '审核时间',
    review_comment VARCHAR(500) COMMENT '审核意见',

    -- 执行信息
    execute_time DATETIME NOT NULL COMMENT '执行时间',
    execute_batch VARCHAR(50) COMMENT '执行批次号',

    -- 公共字段
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_rule (rule_id),
    INDEX idx_asset (asset_id),
    INDEX idx_status (status),
    INDEX idx_batch (execute_batch)
) COMMENT='分类分级辅助结果';

-- 分类分级辅助任务表
CREATE TABLE IF NOT EXISTS classification_assist_task (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    task_type VARCHAR(20) NOT NULL COMMENT '任务类型：MANUAL-手动，SCHEDULED-定时',

    -- 执行范围
    scope_type VARCHAR(20) COMMENT '范围类型：ALL-全部资产，DATASOURCE-指定数据源，ASSET-指定资产',
    scope_config TEXT COMMENT '范围配置JSON',

    -- 规则选择
    rule_ids TEXT COMMENT '执行的规则ID列表（逗号分隔）',

    -- 执行状态
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待执行，RUNNING-执行中，COMPLETED-已完成，FAILED-失败',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',

    -- 统计
    total_count INT DEFAULT 0 COMMENT '总数量',
    matched_count INT DEFAULT 0 COMMENT '匹配数量',
    applied_count INT DEFAULT 0 COMMENT '应用数量',

    -- 定时配置
    cron_expression VARCHAR(50) COMMENT 'Cron表达式',
    next_execute_time DATETIME COMMENT '下次执行时间',

    -- 公共字段
    creator_id BIGINT,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updater_id BIGINT,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,

    INDEX idx_status (status),
    INDEX idx_next_time (next_execute_time)
) COMMENT='分类分级辅助任务';

-- 插入预置规则
INSERT INTO classification_assist_rule (rule_name, rule_code, rule_type, field_name_pattern, suggest_grading_id, suggest_grading_reason, priority, auto_apply, need_review, status) VALUES
('手机号字段识别', 'PHONE_FIELD', 'FIELD_NAME', '*phone*,*mobile*,*手机*,*联系电话*', 3, '字段包含手机号码，属于个人隐私信息，建议定为L3级', 10, 0, 1, 'ACTIVE'),
('身份证号字段识别', 'IDCARD_FIELD', 'FIELD_NAME', '*idcard*,*id_card*,*身份证*,*证件号*', 4, '字段包含身份证号，属于敏感个人信息，建议定为L4级', 5, 0, 1, 'ACTIVE'),
('姓名字段识别', 'NAME_FIELD', 'FIELD_NAME', '*name*,*姓名*,*真实姓名*,*用户名*', 2, '字段包含姓名信息，属于个人信息，建议定为L2级', 20, 0, 1, 'ACTIVE'),
('邮箱字段识别', 'EMAIL_FIELD', 'FIELD_NAME', '*email*,*邮箱*,*mail*', 2, '字段包含邮箱地址，属于个人信息，建议定为L2级', 25, 0, 1, 'ACTIVE'),
('地址字段识别', 'ADDRESS_FIELD', 'FIELD_NAME', '*address*,*地址*,*住址*,*详细地址*', 3, '字段包含地址信息，属于个人隐私信息，建议定为L3级', 15, 0, 1, 'ACTIVE'),
('银行卡号字段识别', 'BANKCARD_FIELD', 'FIELD_NAME', '*bankcard*,*bank_card*,*银行卡*,*卡号*', 4, '字段包含银行卡号，属于敏感金融信息，建议定为L4级', 8, 0, 1, 'ACTIVE');
