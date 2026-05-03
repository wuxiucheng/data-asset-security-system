-- 数据资产安全系统 - 按照Spring Boot实体类重建表
-- 说明：字段名使用Spring Boot期望的下划线格式

USE data_asset_security;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS data_field;
DROP TABLE IF EXISTS data_asset;
DROP TABLE IF EXISTS data_grading;
DROP TABLE IF EXISTS data_classification;
DROP TABLE IF EXISTS grading_standard;
DROP TABLE IF EXISTS classification_standard;
DROP TABLE IF EXISTS owner;
DROP TABLE IF EXISTS department;
SET FOREIGN_KEY_CHECKS = 1;

-- 部门表
CREATE TABLE department (
  department_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  department_code VARCHAR(64) NOT NULL UNIQUE,
  department_name VARCHAR(128) NOT NULL,
  leader_id BIGINT,
  contact_phone VARCHAR(20),
  department_description VARCHAR(500),
  parent_id BIGINT,
  sort_order INT DEFAULT 0,
  status VARCHAR(16) DEFAULT 'ACTIVE',
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 责任人表
CREATE TABLE owner (
  owner_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  employee_no VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(64) NOT NULL,
  department_id BIGINT,
  position VARCHAR(64),
  contact_phone VARCHAR(20),
  email VARCHAR(128),
  user_account VARCHAR(64),
  status VARCHAR(16) DEFAULT 'ACTIVE',
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 分类标准表
CREATE TABLE classification_standard (
  standard_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  standard_name VARCHAR(128) NOT NULL,
  standard_code VARCHAR(64) NOT NULL UNIQUE,
  version VARCHAR(32),
  description VARCHAR(500),
  status VARCHAR(16) DEFAULT 'ACTIVE',
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 数据分类表
CREATE TABLE data_classification (
  classification_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  standard_id BIGINT NOT NULL,
  classification_code VARCHAR(64) NOT NULL,
  classification_name VARCHAR(128) NOT NULL,
  classification_description VARCHAR(500),
  parent_id BIGINT,
  level INT DEFAULT 1,
  sort_order INT DEFAULT 0,
  status VARCHAR(16) DEFAULT 'ACTIVE',
  creator_id BIGINT,
  updater_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 分级标准表
CREATE TABLE grading_standard (
  standard_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  standard_name VARCHAR(128) NOT NULL,
  standard_code VARCHAR(64) NOT NULL UNIQUE,
  version VARCHAR(32),
  description VARCHAR(500),
  status VARCHAR(16) DEFAULT 'ACTIVE',
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 数据分级表
CREATE TABLE data_grading (
  grading_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  standard_id BIGINT NOT NULL,
  grading_code VARCHAR(64) NOT NULL,
  grading_name VARCHAR(128) NOT NULL,
  level INT DEFAULT 1,
  description VARCHAR(500),
  color VARCHAR(20),
  status VARCHAR(16) DEFAULT 'ACTIVE',
  creator_id BIGINT,
  updater_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 数据资产表（完全按照Spring Boot实体类）
CREATE TABLE data_asset (
  asset_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  asset_name VARCHAR(128) NOT NULL,
  asset_code VARCHAR(64) NOT NULL UNIQUE,
  asset_type VARCHAR(32) NOT NULL,
  system_name VARCHAR(64),
  database_type VARCHAR(32),
  database_host VARCHAR(128),
  database_port INT,
  database_name VARCHAR(64),
  table_name VARCHAR(64),
  asset_description VARCHAR(500),
  department_id BIGINT,
  owner_id BIGINT,
  classification_id BIGINT,
  grading_id BIGINT,
  sensitivity_score INT,
  data_volume_level VARCHAR(16),
  access_frequency VARCHAR(16),
  importance_level VARCHAR(16),
  status VARCHAR(16) DEFAULT 'DRAFT',
  contains_sensitive_data TINYINT DEFAULT 0,
  sensitive_data_type VARCHAR(128),
  last_scan_time DATETIME,
  last_scan_result VARCHAR(32),
  expire_time DATETIME,
  remarks VARCHAR(500),
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 数据字段表
CREATE TABLE data_field (
  field_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  asset_id BIGINT NOT NULL,
  field_name VARCHAR(128) NOT NULL,
  field_code VARCHAR(128) NOT NULL,
  field_type VARCHAR(32) NOT NULL,
  field_length INT,
  field_precision INT,
  field_scale INT,
  default_value VARCHAR(256),
  is_primary_key TINYINT DEFAULT 0,
  is_nullable TINYINT DEFAULT 1,
  is_unique TINYINT DEFAULT 0,
  is_sensitive TINYINT DEFAULT 0,
  is_encrypted TINYINT DEFAULT 0,
  classification_id BIGINT,
  grading_id BIGINT,
  sensitive_type VARCHAR(32),
  risk_level VARCHAR(16),
  field_description VARCHAR(500),
  status VARCHAR(16) DEFAULT 'ACTIVE',
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

-- 插入Mock数据
INSERT INTO department (department_id, department_code, department_name, sort_order, status) VALUES
(1, 'DEPT001', '技术部', 1, 'ACTIVE'),
(2, 'DEPT002', '业务部', 2, 'ACTIVE'),
(3, 'DEPT003', '财务部', 3, 'ACTIVE'),
(4, 'DEPT004', '人事部', 4, 'ACTIVE'),
(5, 'DEPT005', '市场部', 5, 'ACTIVE');

INSERT INTO owner (owner_id, employee_no, name, department_id, position, contact_phone, email, status) VALUES
(1, 'EMP001', '张三', 1, '技术总监', '13800138001', 'zhangsan@example.com', 'ACTIVE'),
(2, 'EMP002', '李四', 2, '业务经理', '13800138002', 'lisi@example.com', 'ACTIVE'),
(3, 'EMP003', '王五', 3, '财务主管', '13800138003', 'wangwu@example.com', 'ACTIVE'),
(4, 'EMP004', '赵六', 4, '人事主管', '13800138004', 'zhaoliu@example.com', 'ACTIVE'),
(5, 'EMP005', '钱七', 5, '市场经理', '13800138005', 'qianqi@example.com', 'ACTIVE');

INSERT INTO classification_standard (standard_id, standard_name, standard_code, version, description, status) VALUES
(1, '企业数据分类标准', 'STD001', '1.0', '企业数据分类标准', 'ACTIVE');

INSERT INTO data_classification (classification_id, standard_id, classification_code, classification_name, classification_description, parent_id, level, sort_order, status) VALUES
(1, 1, 'BIZ_DATA', '业务数据', '业务相关数据', NULL, 1, 1, 'ACTIVE'),
(2, 1, 'CUST_DATA', '客户数据', '客户相关数据', 1, 2, 1, 'ACTIVE'),
(3, 1, 'ORDER_DATA', '订单数据', '订单相关数据', 1, 2, 2, 'ACTIVE'),
(4, 1, 'FIN_DATA', '财务数据', '财务相关数据', NULL, 1, 2, 'ACTIVE'),
(5, 1, 'HR_DATA', '人力资源数据', '人力资源相关数据', NULL, 1, 3, 'ACTIVE');

INSERT INTO grading_standard (standard_id, standard_name, standard_code, version, description, status) VALUES
(1, '企业数据分级标准', 'STD001', '1.0', '企业数据分级标准', 'ACTIVE');

INSERT INTO data_grading (grading_id, standard_id, grading_code, grading_name, level, description, color, status) VALUES
(1, 1, 'L1', 'L1-公开', 1, '可公开的数据', '#67C23A', 'ACTIVE'),
(2, 1, 'L2', 'L2-内部', 2, '仅内部使用的数据', '#409EFF', 'ACTIVE'),
(3, 1, 'L3', 'L3-敏感', 3, '敏感数据，需授权访问', '#E6A23C', 'ACTIVE'),
(4, 1, 'L4', 'L4-机密', 4, '机密数据，严格管控', '#F56C6C', 'ACTIVE');

INSERT INTO data_asset (asset_id, asset_code, asset_name, asset_type, system_name, database_type, database_name, table_name, department_id, owner_id, classification_id, grading_id, status, asset_description) VALUES
(1, 'ASSET001', '客户信息表', 'TABLE', 'CRM系统', 'MySQL', 'crm_db', 'customer_info', 2, 2, 2, 2, 'ACTIVE', '客户基本信息表'),
(2, 'ASSET002', '订单数据表', 'TABLE', '订单系统', 'MySQL', 'order_db', 'order_info', 3, 3, 3, 2, 'ACTIVE', '订单信息表'),
(3, 'ASSET003', '财务报表文件', 'FILE', '财务系统', NULL, NULL, NULL, 4, 4, 4, 3, 'ACTIVE', '财务报表文件'),
(4, 'ASSET004', '员工信息API', 'API', '人事系统', NULL, NULL, NULL, 5, 2, 5, 2, 'ACTIVE', '员工信息接口');

INSERT INTO data_field (field_id, asset_id, field_name, field_code, field_type, is_primary_key, is_nullable, is_sensitive, classification_id, grading_id, field_description) VALUES
(1, 1, '客户ID', 'customer_id', 'BIGINT', 1, 0, 1, 2, 2, '客户唯一标识'),
(2, 1, '客户姓名', 'customer_name', 'VARCHAR', 0, 0, 1, 2, 3, '客户姓名'),
(3, 1, '联系电话', 'customer_phone', 'VARCHAR', 0, 1, 1, 2, 3, '客户联系电话');

SELECT '表重建和数据初始化完成！' AS message;
