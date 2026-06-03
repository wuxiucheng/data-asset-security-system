-- 数据资产安全系统 - 初始化数据
-- 用途：初始化系统基础数据

USE data_asset_security;

-- 1. 初始化部门数据
INSERT INTO department (department_id, department_code, department_name, parent_id, sort_order, status, created_time, updated_time) VALUES
(1, 'TECH', '技术部', NULL, 1, 'ACTIVE', NOW(), NOW()),
(2, 'BIZ', '业务部', NULL, 2, 'ACTIVE', NOW(), NOW()),
(3, 'FIN', '财务部', NULL, 3, 'ACTIVE', NOW(), NOW()),
(4, 'HR', '人事部', NULL, 4, 'ACTIVE', NOW(), NOW()),
(5, 'MKT', '市场部', NULL, 5, 'ACTIVE', NOW(), NOW());

-- 2. 初始化责任人数据
INSERT INTO owner (owner_id, owner_name, owner_code, department_id, position, phone, email, status, create_time, update_time) VALUES
(1, '张三', 'ZS001', 1, '技术总监', '13800138001', 'zhangsan@example.com', 'ACTIVE', NOW(), NOW()),
(2, '李四', 'LS001', 2, '业务经理', '13800138002', 'lisi@example.com', 'ACTIVE', NOW(), NOW()),
(3, '王五', 'WW001', 3, '财务主管', '13800138003', 'wangwu@example.com', 'ACTIVE', NOW(), NOW()),
(4, '赵六', 'ZL001', 4, '人事主管', '13800138004', 'zhaoliu@example.com', 'ACTIVE', NOW(), NOW()),
(5, '钱七', 'QQ001', 5, '市场经理', '13800138005', 'qianqi@example.com', 'ACTIVE', NOW(), NOW());

-- 3. 初始化分类标准
INSERT INTO classification_standard (standard_id, standard_name, standard_code, version, description, status, create_time, update_time) VALUES
(1, '数据分类标准', 'DCS001', '1.0', '企业数据分类标准', 'ACTIVE', NOW(), NOW());

-- 4. 初始化数据分类
INSERT INTO data_classification (classification_id, classification_name, classification_code, standard_id, parent_id, level, path, description, status, create_time, update_time) VALUES
(1, '业务数据', 'BIZ_DATA', 1, NULL, 1, '/1', '业务相关数据', 'ACTIVE', NOW(), NOW()),
(2, '客户数据', 'CUST_DATA', 1, 1, 2, '/1/2', '客户相关数据', 'ACTIVE', NOW(), NOW()),
(3, '订单数据', 'ORDER_DATA', 1, 1, 2, '/1/3', '订单相关数据', 'ACTIVE', NOW(), NOW()),
(4, '财务数据', 'FIN_DATA', 1, NULL, 1, '/4', '财务相关数据', 'ACTIVE', NOW(), NOW()),
(5, '人力资源数据', 'HR_DATA', 1, NULL, 1, '/5', '人力资源相关数据', 'ACTIVE', NOW(), NOW());

-- 5. 初始化分级标准
INSERT INTO grading_standard (standard_id, standard_name, standard_code, version, description, status, create_time, update_time) VALUES
(1, '数据分级标准', 'DGS001', '1.0', '企业数据分级标准', 'ACTIVE', NOW(), NOW());

-- 6. 初始化数据分级
INSERT INTO data_grading (grading_id, grading_name, grading_code, standard_id, level, description, color, status, create_time, update_time) VALUES
(1, 'L1-公开', 'L1', 1, 1, '可公开的数据', '#67C23A', 'ACTIVE', NOW(), NOW()),
(2, 'L2-内部', 'L2', 1, 2, '仅内部使用的数据', '#409EFF', 'ACTIVE', NOW(), NOW()),
(3, 'L3-敏感', 'L3', 1, 3, '敏感数据，需授权访问', '#E6A23C', 'ACTIVE', NOW(), NOW()),
(4, 'L4-机密', 'L4', 1, 4, '机密数据，严格管控', '#F56C6C', 'ACTIVE', NOW(), NOW());

-- 7. 初始化数据资产
INSERT INTO data_asset (asset_id, asset_code, asset_name, asset_type, system_name, database_type, database_name, table_name, department_id, owner_id, classification_id, grading_id, status, asset_description, created_time, updated_time) VALUES
(1, 'ASSET001', '客户信息表', 'TABLE', 'CRM系统', 'MySQL', 'crm_db', 'customer_info', 1, 1, 2, 3, 'ACTIVE', '客户基本信息表', NOW(), NOW()),
(2, 'ASSET002', '订单数据表', 'TABLE', '订单系统', 'MySQL', 'order_db', 'order_info', 2, 2, 3, 2, 'ACTIVE', '订单信息表', NOW(), NOW()),
(3, 'ASSET003', '财务报表文件', 'FILE', '财务系统', NULL, NULL, NULL, 3, 3, 4, 4, 'ACTIVE', '财务报表文件', NOW(), NOW()),
(4, 'ASSET004', '员工信息API', 'API', '人事系统', NULL, NULL, NULL, 4, 4, 5, 3, 'ACTIVE', '员工信息接口', NOW(), NOW());

-- 8. 初始化数据字段
INSERT INTO data_field (field_id, asset_id, field_name, field_code, field_type, is_primary_key, is_nullable, is_sensitive, classification_id, grading_id, description, create_time, update_time) VALUES
(1, 1, '客户ID', 'customer_id', 'BIGINT', 1, 0, 1, 2, 3, '客户唯一标识', NOW(), NOW()),
(2, 1, '客户姓名', 'customer_name', 'VARCHAR', 0, 0, 1, 2, 3, '客户姓名', NOW(), NOW()),
(3, 1, '联系电话', 'customer_phone', 'VARCHAR', 0, 1, 1, 2, 3, '客户联系电话', NOW(), NOW()),
(4, 2, '订单ID', 'order_id', 'BIGINT', 1, 0, 0, 3, 2, '订单唯一标识', NOW(), NOW()),
(5, 2, '订单金额', 'order_amount', 'DECIMAL', 0, 0, 1, 3, 3, '订单金额', NOW(), NOW());

-- 完成提示
SELECT '数据初始化完成！' AS message;
