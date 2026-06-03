-- 授权认证系统 - 补充数据库表
-- 添加会话管理、登录日志、审计日志等认证相关表

USE data_asset_security;

-- 1. 认证会话表
CREATE TABLE IF NOT EXISTS auth_session (
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    token VARCHAR(512) NOT NULL COMMENT '访问令牌',
    refresh_token VARCHAR(512) COMMENT '刷新令牌',
    login_time DATETIME NOT NULL COMMENT '登录时间',
    last_access_time DATETIME NOT NULL COMMENT '最后访问时间',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    login_ip VARCHAR(64) COMMENT '登录IP',
    device_info VARCHAR(255) COMMENT '设备信息',
    browser_info VARCHAR(255) COMMENT '浏览器信息',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INVALID, EXPIRED',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_token (token(255)),
    INDEX idx_status (status),
    INDEX idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认证会话表';

-- 2. 登录日志表
CREATE TABLE IF NOT EXISTS auth_login_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    real_name VARCHAR(64) COMMENT '真实姓名',
    login_time DATETIME NOT NULL COMMENT '登录时间',
    login_ip VARCHAR(64) COMMENT '登录IP',
    login_location VARCHAR(128) COMMENT '登录地点',
    device_info VARCHAR(255) COMMENT '设备信息',
    browser_info VARCHAR(255) COMMENT '浏览器信息',
    login_status VARCHAR(16) NOT NULL COMMENT '登录状态：SUCCESS, FAILURE, LOCKED',
    failure_reason VARCHAR(255) COMMENT '失败原因',
    session_id VARCHAR(64) COMMENT '会话ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_username (username),
    INDEX idx_login_time (login_time),
    INDEX idx_login_status (login_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- 3. 密码历史表
CREATE TABLE IF NOT EXISTS auth_password_history (
    history_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '历史ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    change_time DATETIME NOT NULL COMMENT '修改时间',
    change_type VARCHAR(16) NOT NULL COMMENT '修改类型：INIT, RESET, MODIFY, ADMIN_RESET',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(64) COMMENT '操作人姓名',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_change_time (change_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='密码历史表';

-- 4. 多因素认证配置表
CREATE TABLE IF NOT EXISTS auth_mfa_config (
    config_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    mfa_type VARCHAR(32) NOT NULL COMMENT 'MFA类型：TOTP, SMS, EMAIL',
    secret_key VARCHAR(255) COMMENT '密钥',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(128) COMMENT '邮箱',
    qr_code_url VARCHAR(500) COMMENT '二维码URL',
    backup_codes TEXT COMMENT '备用码',
    status VARCHAR(16) NOT NULL DEFAULT 'INACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
    enabled_time DATETIME COMMENT '启用时间',
    last_verified_time DATETIME COMMENT '最后验证时间',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_mfa_type (mfa_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='多因素认证配置表';

-- 5. 账户锁定记录表
CREATE TABLE IF NOT EXISTS auth_account_lock (
    lock_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '锁定ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    lock_type VARCHAR(32) NOT NULL COMMENT '锁定类型：PASSWORD_FAILURE, ADMIN_LOCK, SECURITY_RISK',
    lock_reason VARCHAR(255) COMMENT '锁定原因',
    lock_time DATETIME NOT NULL COMMENT '锁定时间',
    unlock_time DATETIME COMMENT '解锁时间',
    lock_duration INT COMMENT '锁定时长（分钟）',
    status VARCHAR(16) NOT NULL DEFAULT 'LOCKED' COMMENT '状态：LOCKED, UNLOCKED',
    unlock_operator_id BIGINT COMMENT '解锁操作人ID',
    unlock_operator_name VARCHAR(64) COMMENT '解锁操作人姓名',
    unlock_reason VARCHAR(255) COMMENT '解锁原因',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_username (username),
    INDEX idx_lock_time (lock_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户锁定记录表';

-- 6. 操作审计日志表（增强版）
CREATE TABLE IF NOT EXISTS auth_audit_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    operation_type VARCHAR(32) NOT NULL COMMENT '操作类型：LOGIN, LOGOUT, CREATE, UPDATE, DELETE, QUERY, APPROVE',
    module_name VARCHAR(64) NOT NULL COMMENT '模块名称：USER, ROLE, PERMISSION, ASSET, CLASSIFICATION, GRADING',
    object_type VARCHAR(64) COMMENT '对象类型',
    object_id BIGINT COMMENT '对象ID',
    object_name VARCHAR(255) COMMENT '对象名称',
    operation_content TEXT COMMENT '操作内容',
    operation_result VARCHAR(16) NOT NULL COMMENT '操作结果：SUCCESS, FAILURE',
    error_message TEXT COMMENT '错误信息',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(64) COMMENT '操作人姓名',
    operator_username VARCHAR(64) COMMENT '操作人用户名',
    operation_time DATETIME NOT NULL COMMENT '操作时间',
    operation_ip VARCHAR(64) COMMENT '操作IP',
    operation_location VARCHAR(128) COMMENT '操作地点',
    user_agent VARCHAR(500) COMMENT '用户代理',
    request_url VARCHAR(255) COMMENT '请求URL',
    request_method VARCHAR(16) COMMENT '请求方法',
    request_params TEXT COMMENT '请求参数',
    execution_time INT COMMENT '执行时间（毫秒）',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_operation_type (operation_type),
    INDEX idx_module_name (module_name),
    INDEX idx_operator_id (operator_id),
    INDEX idx_operation_time (operation_time),
    INDEX idx_operation_result (operation_result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作审计日志表';

-- 7. 权限验证日志表
CREATE TABLE IF NOT EXISTS auth_permission_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    resource_type VARCHAR(32) NOT NULL COMMENT '资源类型：API, MENU, BUTTON',
    resource_path VARCHAR(255) NOT NULL COMMENT '资源路径',
    resource_name VARCHAR(128) COMMENT '资源名称',
    permission_code VARCHAR(64) COMMENT '权限编码',
    permission_name VARCHAR(128) COMMENT '权限名称',
    verify_result VARCHAR(16) NOT NULL COMMENT '验证结果：GRANTED, DENIED',
    deny_reason VARCHAR(255) COMMENT '拒绝原因',
    verify_time DATETIME NOT NULL COMMENT '验证时间',
    request_ip VARCHAR(64) COMMENT '请求IP',
    request_method VARCHAR(16) COMMENT '请求方法',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_username (username),
    INDEX idx_resource_path (resource_path(255)),
    INDEX idx_verify_result (verify_result),
    INDEX idx_verify_time (verify_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限验证日志表';

-- 8. 接口限流记录表
CREATE TABLE IF NOT EXISTS auth_rate_limit_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    limit_key VARCHAR(128) NOT NULL COMMENT '限流键',
    limit_type VARCHAR(32) NOT NULL COMMENT '限流类型：IP, USER, API',
    limit_value VARCHAR(64) COMMENT '限流值',
    request_time DATETIME NOT NULL COMMENT '请求时间',
    request_ip VARCHAR(64) COMMENT '请求IP',
    request_url VARCHAR(255) COMMENT '请求URL',
    request_method VARCHAR(16) COMMENT '请求方法',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(64) COMMENT '用户名',
    is_limited TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否被限流：0-否，1-是',
    limit_threshold INT COMMENT '限流阈值',
    current_count INT COMMENT '当前计数',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_limit_key (limit_key),
    INDEX idx_limit_type (limit_type),
    INDEX idx_request_time (request_time),
    INDEX idx_is_limited (is_limited)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='接口限流记录表';

-- 9. Token黑名单表
CREATE TABLE IF NOT EXISTS auth_token_blacklist (
    blacklist_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '黑名单ID',
    token VARCHAR(512) NOT NULL COMMENT 'Token',
    token_type VARCHAR(16) NOT NULL COMMENT 'Token类型：ACCESS, REFRESH',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    blacklist_reason VARCHAR(255) COMMENT '加入黑名单原因',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_token (token(255)),
    INDEX idx_user_id (user_id),
    INDEX idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Token黑名单表';

-- 10. 安全事件表
CREATE TABLE IF NOT EXISTS auth_security_event (
    event_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '事件ID',
    event_type VARCHAR(32) NOT NULL COMMENT '事件类型：BRUTE_FORCE, ABNORMAL_LOGIN, PRIVILEGE_ESCALATION, DATA_BREACH',
    event_level VARCHAR(16) NOT NULL COMMENT '事件级别：LOW, MEDIUM, HIGH, CRITICAL',
    event_title VARCHAR(255) NOT NULL COMMENT '事件标题',
    event_description TEXT COMMENT '事件描述',
    related_user_id BIGINT COMMENT '相关用户ID',
    related_username VARCHAR(64) COMMENT '相关用户名',
    source_ip VARCHAR(64) COMMENT '来源IP',
    source_location VARCHAR(128) COMMENT '来源地点',
    event_time DATETIME NOT NULL COMMENT '事件时间',
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING, INVESTIGATING, RESOLVED, CLOSED',
    handler_id BIGINT COMMENT '处理人ID',
    handler_name VARCHAR(64) COMMENT '处理人姓名',
    handle_time DATETIME COMMENT '处理时间',
    handle_result VARCHAR(255) COMMENT '处理结果',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_event_type (event_type),
    INDEX idx_event_level (event_level),
    INDEX idx_related_user_id (related_user_id),
    INDEX idx_event_time (event_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='安全事件表';

-- 完成
SELECT '授权认证系统补充表创建完成！' AS message;