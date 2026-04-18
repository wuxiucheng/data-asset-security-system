-- 数据资产安全及分类分级管理系统 - 数据库初始化脚本
-- 数据库版本: MySQL 8.0+
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- 注意：此脚本仅创建表结构和初始数据，不会删除现有数据库

-- 使用现有数据库或创建新数据库
CREATE DATABASE IF NOT EXISTS data_asset_security DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE data_asset_security;

-- 1. 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    real_name VARCHAR(64) COMMENT '真实姓名',
    email VARCHAR(128) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE, LOCKED',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(64) COMMENT '最后登录IP',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 2. 系统角色表
CREATE TABLE IF NOT EXISTS sys_role (
    role_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(64) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    role_description VARCHAR(500) COMMENT '角色描述',
    role_type VARCHAR(32) NOT NULL COMMENT '角色类型：SYSTEM_ADMIN, DATA_ADMIN, APPROVER, OWNER, USER',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_role_code (role_code),
    INDEX idx_role_type (role_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 3. 系统权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    permission_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    permission_code VARCHAR(64) NOT NULL UNIQUE COMMENT '权限编码',
    permission_name VARCHAR(128) NOT NULL COMMENT '权限名称',
    permission_type VARCHAR(32) NOT NULL COMMENT '权限类型：MENU, BUTTON, API',
    parent_id BIGINT COMMENT '父权限ID',
    resource_path VARCHAR(255) COMMENT '资源路径',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_permission_type (permission_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限表';

-- 4. 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_role_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户角色ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES sys_role(role_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 5. 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    role_permission_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色权限ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES sys_permission(permission_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 6. 审计日志表
CREATE TABLE IF NOT EXISTS audit_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    operation_type VARCHAR(32) NOT NULL COMMENT '操作类型：CREATE, UPDATE, DELETE, QUERY, APPROVE',
    object_type VARCHAR(64) NOT NULL COMMENT '对象类型：USER, ROLE, PERMISSION, ASSET, CLASSIFICATION, GRADING',
    object_id BIGINT COMMENT '对象ID',
    object_name VARCHAR(255) COMMENT '对象名称',
    operation_description VARCHAR(500) COMMENT '操作描述',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(64) COMMENT '操作人姓名',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    operation_ip VARCHAR(64) COMMENT '操作IP',
    operation_result VARCHAR(16) COMMENT '操作结果：SUCCESS, FAILURE',
    error_message TEXT COMMENT '错误信息',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_operation_type (operation_type),
    INDEX idx_object_type (object_type),
    INDEX idx_operator_id (operator_id),
    INDEX idx_operation_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- 7. 责任部门表
CREATE TABLE IF NOT EXISTS department (
    department_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    department_code VARCHAR(64) NOT NULL UNIQUE COMMENT '部门编码',
    department_name VARCHAR(128) NOT NULL COMMENT '部门名称',
    leader_id BIGINT COMMENT '部门负责人ID',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    department_description VARCHAR(500) COMMENT '部门描述',
    parent_id BIGINT COMMENT '上级部门ID',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_leader_id (leader_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='责任部门表';

-- 8. 责任人表
CREATE TABLE IF NOT EXISTS owner (
    owner_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '责任人ID',
    employee_no VARCHAR(64) NOT NULL UNIQUE COMMENT '工号',
    name VARCHAR(64) NOT NULL COMMENT '姓名',
    department_id BIGINT COMMENT '所属部门ID',
    position VARCHAR(64) COMMENT '职务',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(128) COMMENT '邮箱',
    user_account VARCHAR(64) COMMENT '用户账号',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_department_id (department_id),
    INDEX idx_user_account (user_account),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='责任人表';

-- 9. 数据分类标准表
CREATE TABLE IF NOT EXISTS classification_standard (
    standard_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标准ID',
    standard_code VARCHAR(64) NOT NULL UNIQUE COMMENT '标准编码',
    standard_name VARCHAR(128) NOT NULL COMMENT '标准名称',
    standard_description VARCHAR(500) COMMENT '标准描述',
    version VARCHAR(32) COMMENT '版本号',
    publish_date VARCHAR(32) COMMENT '发布日期',
    publish_unit VARCHAR(128) COMMENT '发布单位',
    scope VARCHAR(500) COMMENT '适用范围',
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT, PUBLISHED, ARCHIVED',
    creator_id BIGINT COMMENT '创建人ID',
    updater_id BIGINT COMMENT '更新人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_standard_code (standard_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据分类标准表';

-- 10. 数据分类表
CREATE TABLE IF NOT EXISTS data_classification (
    classification_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    standard_id BIGINT NOT NULL COMMENT '标准ID',
    classification_code VARCHAR(64) NOT NULL COMMENT '分类编码',
    classification_name VARCHAR(128) NOT NULL COMMENT '分类名称',
    classification_description VARCHAR(500) COMMENT '分类描述',
    parent_id BIGINT COMMENT '父分类ID',
    level INT NOT NULL DEFAULT 1 COMMENT '层级',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
    creator_id BIGINT COMMENT '创建人ID',
    updater_id BIGINT COMMENT '更新人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_standard_id (standard_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据分类表';

-- 11. 数据分级标准表
CREATE TABLE IF NOT EXISTS grading_standard (
    standard_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标准ID',
    standard_code VARCHAR(64) NOT NULL UNIQUE COMMENT '标准编码',
    standard_name VARCHAR(128) NOT NULL COMMENT '标准名称',
    standard_description VARCHAR(500) COMMENT '标准描述',
    version VARCHAR(32) COMMENT '版本号',
    publish_date VARCHAR(32) COMMENT '发布日期',
    publish_unit VARCHAR(128) COMMENT '发布单位',
    scope VARCHAR(500) COMMENT '适用范围',
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT, PUBLISHED, ARCHIVED',
    creator_id BIGINT COMMENT '创建人ID',
    updater_id BIGINT COMMENT '更新人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_standard_code (standard_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据分级标准表';

-- 12. 数据分级表
CREATE TABLE IF NOT EXISTS data_grading (
    grading_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分级ID',
    standard_id BIGINT NOT NULL COMMENT '标准ID',
    grading_code VARCHAR(64) NOT NULL COMMENT '分级编码',
    grading_name VARCHAR(128) NOT NULL COMMENT '分级名称',
    grading_description VARCHAR(500) COMMENT '分级描述',
    level_value INT NOT NULL COMMENT '等级值',
    color_code VARCHAR(32) COMMENT '颜色标识',
    security_requirements VARCHAR(1000) COMMENT '安全要求',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
    creator_id BIGINT COMMENT '创建人ID',
    updater_id BIGINT COMMENT '更新人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_standard_id (standard_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据分级表';

-- 13. 数据资产表
CREATE TABLE IF NOT EXISTS data_asset (
    asset_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资产ID',
    asset_name VARCHAR(128) NOT NULL COMMENT '资产名称',
    asset_code VARCHAR(64) NOT NULL UNIQUE COMMENT '资产编码',
    asset_type VARCHAR(32) NOT NULL COMMENT '资产类型：DATABASE, TABLE, FILE, API, OTHER',
    system_name VARCHAR(64) COMMENT '所属系统',
    database_type VARCHAR(32) COMMENT '数据库类型：MYSQL, ORACLE, POSTGRESQL, SQLSERVER, OTHER',
    database_host VARCHAR(128) COMMENT '数据库地址',
    database_port INT COMMENT '数据库端口',
    database_name VARCHAR(64) COMMENT '数据库名称',
    table_name VARCHAR(64) COMMENT '表名',
    asset_description VARCHAR(500) COMMENT '资产描述',
    department_id BIGINT COMMENT '责任部门ID',
    owner_id BIGINT COMMENT '责任人ID',
    classification_id BIGINT COMMENT '分类ID',
    grading_id BIGINT COMMENT '分级ID',
    sensitivity_score INT COMMENT '敏感度评分',
    data_volume_level VARCHAR(16) COMMENT '数据量级别：SMALL, MEDIUM, LARGE, HUGE',
    access_frequency VARCHAR(16) COMMENT '访问频率：LOW, MEDIUM, HIGH',
    importance_level VARCHAR(16) COMMENT '数据重要性：LOW, MEDIUM, HIGH, CRITICAL',
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT, ACTIVE, INACTIVE, ARCHIVED',
    contains_sensitive_data TINYINT(1) COMMENT '是否包含敏感数据',
    sensitive_data_type VARCHAR(128) COMMENT '敏感数据类型',
    last_scan_time DATETIME COMMENT '最后扫描时间',
    last_scan_result VARCHAR(500) COMMENT '最后扫描结果',
    expire_time DATETIME COMMENT '过期时间',
    remarks VARCHAR(500) COMMENT '备注',
    creator_id BIGINT COMMENT '创建人ID',
    updater_id BIGINT COMMENT '更新人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_asset_code (asset_code),
    INDEX idx_department_id (department_id),
    INDEX idx_owner_id (owner_id),
    INDEX idx_classification_id (classification_id),
    INDEX idx_grading_id (grading_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据资产表';

-- 14. 数据字段表
CREATE TABLE IF NOT EXISTS data_field (
    field_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字段ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    field_name VARCHAR(64) NOT NULL COMMENT '字段名称',
    field_code VARCHAR(64) NOT NULL COMMENT '字段编码',
    field_type VARCHAR(32) NOT NULL COMMENT '字段类型：STRING, INTEGER, DECIMAL, DATE, BOOLEAN, OTHER',
    field_length INT COMMENT '字段长度',
    nullable TINYINT(1) COMMENT '是否为空',
    is_primary_key TINYINT(1) COMMENT '是否为主键',
    field_description VARCHAR(500) COMMENT '字段描述',
    classification_id BIGINT COMMENT '分类ID',
    grading_id BIGINT COMMENT '分级ID',
    contains_sensitive_data TINYINT(1) COMMENT '是否包含敏感数据',
    sensitive_data_type VARCHAR(128) COMMENT '敏感数据类型',
    default_value VARCHAR(256) COMMENT '默认值',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_asset_id (asset_id),
    INDEX idx_classification_id (classification_id),
    INDEX idx_grading_id (grading_id),
    FOREIGN KEY (asset_id) REFERENCES data_asset(asset_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字段表';

-- 15. 审批流程定义表
CREATE TABLE IF NOT EXISTS approval_process_definition (
    definition_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '流程定义ID',
    process_definition_key VARCHAR(64) NOT NULL COMMENT '流程定义Key',
    process_definition_name VARCHAR(128) NOT NULL COMMENT '流程定义名称',
    process_definition_description VARCHAR(500) COMMENT '流程定义描述',
    process_type VARCHAR(32) NOT NULL COMMENT '流程类型：CLASSIFICATION_APPROVAL, GRADING_APPROVAL, ASSET_APPROVAL, OTHER',
    version INT COMMENT '流程版本',
    bpmn_process_definition_id VARCHAR(64) COMMENT 'BPMN流程定义ID',
    bpmn_process_definition_key VARCHAR(64) COMMENT 'BPMN流程定义Key',
    deployment_id VARCHAR(64) COMMENT '流程部署ID',
    resource_name VARCHAR(128) COMMENT '流程资源名称',
    bpmn_content TEXT COMMENT 'BPMN流程定义内容',
    process_image VARCHAR(500) COMMENT '流程图图片',
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT, ACTIVE, INACTIVE, ARCHIVED',
    enabled TINYINT(1) COMMENT '是否启用',
    creator_id BIGINT COMMENT '创建人ID',
    updater_id BIGINT COMMENT '更新人ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_process_definition_key (process_definition_key),
    INDEX idx_process_type (process_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批流程定义表';

-- 16. 审批流程实例表
CREATE TABLE IF NOT EXISTS approval_process_instance (
    instance_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '流程实例ID',
    definition_id BIGINT NOT NULL COMMENT '流程定义ID',
    process_instance_id VARCHAR(64) COMMENT '流程实例ID',
    process_definition_key VARCHAR(64) COMMENT '流程定义Key',
    process_definition_name VARCHAR(128) COMMENT '流程定义名称',
    process_type VARCHAR(32) COMMENT '流程类型',
    business_type VARCHAR(64) COMMENT '业务类型',
    business_id BIGINT COMMENT '业务ID',
    business_title VARCHAR(255) COMMENT '业务标题',
    applicant_id BIGINT COMMENT '申请人ID',
    applicant_name VARCHAR(64) COMMENT '申请人姓名',
    applicant_department_id BIGINT COMMENT '申请部门ID',
    applicant_department_name VARCHAR(128) COMMENT '申请部门名称',
    apply_time DATETIME COMMENT '申请时间',
    current_task_id VARCHAR(64) COMMENT '当前节点ID',
    current_task_name VARCHAR(128) COMMENT '当前节点名称',
    current_assignee_id BIGINT COMMENT '当前处理人ID',
    current_assignee_name VARCHAR(64) COMMENT '当前处理人姓名',
    process_status VARCHAR(16) COMMENT '流程状态：RUNNING, COMPLETED, CANCELLED, SUSPENDED',
    approval_result VARCHAR(16) COMMENT '审批结果：PENDING, APPROVED, REJECTED, CANCELLED',
    approval_comment VARCHAR(500) COMMENT '审批意见',
    completed_time DATETIME COMMENT '完成时间',
    process_variables TEXT COMMENT '流程变量',
    remarks VARCHAR(500) COMMENT '备注',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_definition_id (definition_id),
    INDEX idx_process_instance_id (process_instance_id),
    INDEX idx_applicant_id (applicant_id),
    INDEX idx_business_id (business_id),
    INDEX idx_process_status (process_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批流程实例表';

-- 17. 审批任务表
CREATE TABLE IF NOT EXISTS approval_task (
    task_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    instance_id BIGINT NOT NULL COMMENT '流程实例ID',
    process_instance_id VARCHAR(64) COMMENT '流程实例ID',
    task_id_flowable VARCHAR(64) COMMENT '任务ID',
    task_name VARCHAR(128) COMMENT '任务名称',
    task_definition_key VARCHAR(64) COMMENT '任务定义Key',
    assignee_id BIGINT COMMENT '处理人ID',
    assignee_name VARCHAR(64) COMMENT '处理人姓名',
    candidate_groups VARCHAR(255) COMMENT '候选人组',
    task_status VARCHAR(16) COMMENT '任务状态：PENDING, COMPLETED, DELEGATED, CANCELLED',
    task_start_time DATETIME COMMENT '任务开始时间',
    task_end_time DATETIME COMMENT '任务完成时间',
    approval_result VARCHAR(16) COMMENT '审批结果：PENDING, APPROVED, REJECTED, DELEGATED',
    approval_comment VARCHAR(500) COMMENT '审批意见',
    priority VARCHAR(16) COMMENT '优先级：LOW, NORMAL, HIGH, URGENT',
    due_date DATETIME COMMENT '到期时间',
    task_variables TEXT COMMENT '任务变量',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_instance_id (instance_id),
    INDEX idx_process_instance_id (process_instance_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_task_status (task_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批任务表';

-- 插入初始数据
-- 注意：只在表为空时插入初始数据

-- 插入系统管理员（如果不存在）
INSERT INTO sys_user (user_id, username, password, real_name, email, phone, status)
SELECT 1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@example.com', '13800138000', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

-- 插入角色（如果不存在）
INSERT INTO sys_role (role_id, role_code, role_name, role_description, role_type, status)
SELECT 1, 'SYSTEM_ADMIN', '系统管理员', '负责系统配置和用户管理', 'SYSTEM_ADMIN', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'SYSTEM_ADMIN');

INSERT INTO sys_role (role_id, role_code, role_name, role_description, role_type, status)
SELECT 2, 'DATA_ADMIN', '数据资产管理员', '负责分类分级标准制定和维护', 'DATA_ADMIN', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'DATA_ADMIN');

INSERT INTO sys_role (role_id, role_code, role_name, role_description, role_type, status)
SELECT 3, 'APPROVER', '数据审批人', '负责分类分级申请审批', 'APPROVER', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'APPROVER');

INSERT INTO sys_role (role_id, role_code, role_name, role_description, role_type, status)
SELECT 4, 'OWNER', '数据责任人', '负责数据资产的日常维护和管理', 'OWNER', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'OWNER');

INSERT INTO sys_role (role_id, role_code, role_name, role_description, role_type, status)
SELECT 5, 'USER', '普通用户', '查看已授权的数据资产信息', 'USER', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'USER');

-- 插入用户角色关联（如果不存在）
INSERT INTO sys_user_role (user_id, role_id)
SELECT 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = 1 AND role_id = 1);

-- 插入默认部门
INSERT INTO department (department_id, department_code, department_name, department_description, parent_id, sort_order, status)
SELECT 1, 'ROOT', '根部门', '系统根部门', NULL, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM department WHERE department_code = 'ROOT');

-- 插入默认责任人
INSERT INTO owner (owner_id, employee_no, name, department_id, position, status)
SELECT 1, 'ADMIN001', '系统管理员', 1, '系统管理员', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM owner WHERE employee_no = 'ADMIN001');

-- 完成
SELECT '数据库初始化完成！' AS message;
