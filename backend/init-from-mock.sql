-- 数据资产安全系统 - 从Mock数据初始化
USE data_asset_security;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE data_field;
TRUNCATE TABLE data_asset;
TRUNCATE TABLE data_grading;
TRUNCATE TABLE data_classification;
TRUNCATE TABLE grading_standard;
TRUNCATE TABLE classification_standard;
TRUNCATE TABLE owner;
TRUNCATE TABLE department;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO department (department_id, department_code, department_name, sort_order, status, created_time, updated_time) VALUES
(1, 'DEPT001', '技术部', 1, 'ACTIVE', NOW(), NOW()),
(2, 'DEPT002', '业务部', 2, 'ACTIVE', NOW(), NOW()),
(3, 'DEPT003', '财务部', 3, 'ACTIVE', NOW(), NOW()),
(4, 'DEPT004', '人事部', 4, 'ACTIVE', NOW(), NOW()),
(5, 'DEPT005', '市场部', 5, 'ACTIVE', NOW(), NOW());

INSERT INTO owner (owner_id, employee_no, name, department_id, position, contact_phone, email, status, created_time, updated_time) VALUES
(1, 'EMP001', '张三', 1, '技术总监', '13800138001', 'zhangsan@example.com', 'ACTIVE', NOW(), NOW()),
(2, 'EMP002', '李四', 2, '业务经理', '13800138002', 'lisi@example.com', 'ACTIVE', NOW(), NOW()),
(3, 'EMP003', '王五', 3, '财务主管', '13800138003', 'wangwu@example.com', 'ACTIVE', NOW(), NOW()),
(4, 'EMP004', '赵六', 4, '人事主管', '13800138004', 'zhaoliu@example.com', 'ACTIVE', NOW(), NOW()),
(5, 'EMP005', '钱七', 5, '市场经理', '13800138005', 'qianqi@example.com', 'ACTIVE', NOW(), NOW());

INSERT INTO classification_standard (standard_id, standard_name, standard_code, version, description, status, create_time, update_time) VALUES
(1, '企业数据分类标准', 'STD001', '1.0', '企业数据分类标准', 'ACTIVE', NOW(), NOW());

INSERT INTO data_classification (classification_id, standard_id, classification_code, classification_name, classification_description, parent_id, level, sort_order, status, created_time, updated_time) VALUES
(1, 1, 'BIZ_DATA', '业务数据', '业务相关数据', NULL, 1, 1, 'ACTIVE', NOW(), NOW()),
(2, 1, 'CUST_DATA', '客户数据', '客户相关数据', 1, 2, 1, 'ACTIVE', NOW(), NOW()),
(3, 1, 'ORDER_DATA', '订单数据', '订单相关数据', 1, 2, 2, 'ACTIVE', NOW(), NOW()),
(4, 1, 'FIN_DATA', '财务数据', '财务相关数据', NULL, 1, 2, 'ACTIVE', NOW(), NOW()),
(5, 1, 'HR_DATA', '人力资源数据', '人力资源相关数据', NULL, 1, 3, 'ACTIVE', NOW(), NOW());

INSERT INTO grading_standard (standard_id, standard_name, standard_code, version, description, status, create_time, update_time) VALUES
(1, '企业数据分级标准', 'STD001', '1.0', '企业数据分级标准', 'ACTIVE', NOW(), NOW());

INSERT INTO data_grading (grading_id, standard_id, grading_code, grading_name, level, description, color, status, create_time, update_time) VALUES
(1, 1, 'L1', 'L1-公开', 1, '可公开的数据', '#67C23A', 'ACTIVE', NOW(), NOW()),
(2, 1, 'L2', 'L2-内部', 2, '仅内部使用的数据', '#409EFF', 'ACTIVE', NOW(), NOW()),
(3, 1, 'L3', 'L3-敏感', 3, '敏感数据，需授权访问', '#E6A23C', 'ACTIVE', NOW(), NOW()),
(4, 1, 'L4', 'L4-机密', 4, '机密数据，严格管控', '#F56C6C', 'ACTIVE', NOW(), NOW());

INSERT INTO data_asset (asset_id, asset_code, asset_name, asset_type, system_name, database_type, database_name, table_name, department_id, owner_id, classification_id, grading_id, status, asset_description, created_time, updated_time) VALUES
(1, 'ASSET001', '客户信息表', 'DATABASE', 'CRM系统', 'MYSQL', 'crm_db', 'customer_info', 2, 2, 2, 2, 'ACTIVE', '客户基本信息表', NOW(), NOW()),
(2, 'ASSET002', '订单数据表', 'DATABASE', '订单系统', 'MYSQL', 'order_db', 'order_info', 3, 3, 3, 2, 'ACTIVE', '订单信息表', NOW(), NOW()),
(3, 'ASSET003', '财务报表文件', 'FILE', '财务系统', NULL, NULL, NULL, 4, 4, 4, 3, 'ACTIVE', '财务报表文件', NOW(), NOW()),
(4, 'ASSET004', '员工信息API', 'API', '人事系统', NULL, NULL, NULL, 5, 2, 5, 2, 'ACTIVE', '员工信息接口', NOW(), NOW());

INSERT INTO data_field (field_id, asset_id, field_name, field_code, field_type, is_primary_key, is_nullable, is_sensitive, classification_id, grading_id, description, create_time, update_time) VALUES
(1, 1, 'customer_id', '客户ID', 'BIGINT', 1, 1, 0, 2, 2, '客户唯一标识', NOW(), NOW()),
(2, 1, 'customer_name', '客户姓名', 'VARCHAR', 0, 1, 0, 2, 3, '客户姓名', NOW(), NOW()),
(3, 1, 'customer_phone', '联系电话', 'VARCHAR', 0, 0, 0, 2, 3, '客户联系电话', NOW(), NOW());

SELECT '初始化完成' AS message;
