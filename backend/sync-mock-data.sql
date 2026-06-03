-- 从Mock后端完整同步数据到MySQL（自动生成）
USE data_asset_security;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM data_field;
DELETE FROM data_asset;
DELETE FROM data_grading;
DELETE FROM data_classification;
DELETE FROM grading_standard;
DELETE FROM classification_standard;
DELETE FROM owner;
DELETE FROM department;
DELETE FROM sys_user_role;
DELETE FROM sys_role_permission;
DELETE FROM sys_permission;
DELETE FROM sys_role;
DELETE FROM sys_user;
SET FOREIGN_KEY_CHECKS = 1;

-- 系统用户
INSERT INTO sys_user (user_id, username, password, real_name, email, phone, status) VALUES (1, 'admin', '$2a$10$01bROie5KZjN8JM4yULtX.bl44JS9vkB61WAFC2LZ2zOz4jttRfVG', '系统管理员', 'admin@example.com', '13800138000', 'ACTIVE');
INSERT INTO sys_user (user_id, username, password, real_name, email, phone, status) VALUES (2, 'user1', '$2a$10$Zk81cagRQKQHAL6NihaH/.RkmJsrUDDZ45tJAnYUmEY19uG0oFfb6', '张三', 'zhangsan@example.com', '13800138001', 'ACTIVE');
INSERT INTO sys_user (user_id, username, password, real_name, email, phone, status) VALUES (3, 'user2', '$2a$10$Zk81cagRQKQHAL6NihaH/.RkmJsrUDDZ45tJAnYUmEY19uG0oFfb6', '李四', 'lisi@example.com', '13800138002', 'ACTIVE');
INSERT INTO sys_user (user_id, username, password, real_name, email, phone, status) VALUES (4, 'user3', '$2a$10$Zk81cagRQKQHAL6NihaH/.RkmJsrUDDZ45tJAnYUmEY19uG0oFfb6', '王五', 'wangwu@example.com', '13800138003', 'INACTIVE');

-- 系统角色
INSERT INTO sys_role (role_id, role_code, role_name, role_description, role_type, sort_order, status) VALUES (1, 'SYSTEM_ADMIN', '系统管理员', '负责系统配置和用户管理', 'SYSTEM_ADMIN', 1, 'ACTIVE');
INSERT INTO sys_role (role_id, role_code, role_name, role_description, role_type, sort_order, status) VALUES (2, 'DATA_ADMIN', '数据管理员', '负责分类分级标准制定和维护', 'DATA_ADMIN', 2, 'ACTIVE');
INSERT INTO sys_role (role_id, role_code, role_name, role_description, role_type, sort_order, status) VALUES (3, 'APPROVER', '数据审批人', '负责分类分级申请审批', 'APPROVER', 3, 'ACTIVE');
INSERT INTO sys_role (role_id, role_code, role_name, role_description, role_type, sort_order, status) VALUES (4, 'OWNER', '数据责任人', '负责数据资产的日常维护和管理', 'OWNER', 4, 'ACTIVE');
INSERT INTO sys_role (role_id, role_code, role_name, role_description, role_type, sort_order, status) VALUES (5, 'USER', '普通用户', '查看已授权的数据资产信息', 'USER', 5, 'ACTIVE');

-- 系统权限
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (1, 'system', '系统管理', 'MENU', NULL, 1, 'ACTIVE', '/system');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (2, 'system:user', '用户管理', 'MENU', 1, 1, 'ACTIVE', '/user');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (3, 'system:user:create', '创建用户', 'BUTTON', 2, 1, 'ACTIVE', '/api/user/create');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (4, 'system:user:edit', '编辑用户', 'BUTTON', 2, 2, 'ACTIVE', '/api/user/update');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (5, 'system:user:delete', '删除用户', 'BUTTON', 2, 3, 'ACTIVE', '/api/user/delete');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (6, 'system:user:view', '查看用户', 'BUTTON', 2, 4, 'ACTIVE', '/api/user/list');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (7, 'system:role', '角色管理', 'MENU', 1, 2, 'ACTIVE', '/role');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (8, 'system:role:create', '创建角色', 'BUTTON', 7, 1, 'ACTIVE', '/api/role/create');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (9, 'system:role:edit', '编辑角色', 'BUTTON', 7, 2, 'ACTIVE', '/api/role/update');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (10, 'system:role:delete', '删除角色', 'BUTTON', 7, 3, 'ACTIVE', '/api/role/delete');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (11, 'system:role:view', '查看角色', 'BUTTON', 7, 4, 'ACTIVE', '/api/role/list');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (12, 'system:permission', '权限管理', 'MENU', 1, 3, 'ACTIVE', '/permission');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (13, 'system:permission:view', '查看权限', 'BUTTON', 12, 1, 'ACTIVE', '/api/permission/list');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (14, 'data', '数据资产管理', 'MENU', NULL, 2, 'ACTIVE', '/data');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (15, 'data:asset', '资产列表', 'MENU', 14, 1, 'ACTIVE', '/asset');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (16, 'data:asset:create', '创建资产', 'BUTTON', 15, 1, 'ACTIVE', '/api/asset/create');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (17, 'data:asset:edit', '编辑资产', 'BUTTON', 15, 2, 'ACTIVE', '/api/asset/update');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (18, 'data:asset:delete', '删除资产', 'BUTTON', 15, 3, 'ACTIVE', '/api/asset/delete');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (19, 'data:asset:export', '导出资产', 'BUTTON', 15, 4, 'ACTIVE', '/api/asset/export');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (20, 'data:classification', '分类分级管理', 'MENU', 14, 2, 'ACTIVE', '/classification');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (21, 'data:classification:view', '查看分类', 'BUTTON', 20, 1, 'ACTIVE', '/api/classification/list');
INSERT INTO sys_permission (permission_id, permission_code, permission_name, permission_type, parent_id, sort_order, status, path) VALUES (22, 'data:classification:create', '创建分类', 'BUTTON', 20, 2, 'ACTIVE', '/api/classification/create');

-- 用户角色关联
INSERT INTO sys_user_role (user_role_id, user_id, role_id) VALUES (1, 1, 1);
INSERT INTO sys_user_role (user_role_id, user_id, role_id) VALUES (2, 1, 2);
INSERT INTO sys_user_role (user_role_id, user_id, role_id) VALUES (3, 2, 5);
INSERT INTO sys_user_role (user_role_id, user_id, role_id) VALUES (4, 3, 4);
INSERT INTO sys_user_role (user_role_id, user_id, role_id) VALUES (5, 3, 5);
INSERT INTO sys_user_role (user_role_id, user_id, role_id) VALUES (6, 4, 5);

-- 角色权限关联（admin拥有所有权限）
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (1, 1, 1);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (2, 1, 2);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (3, 1, 3);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (4, 1, 4);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (5, 1, 5);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (6, 1, 6);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (7, 1, 7);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (8, 1, 8);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (9, 1, 9);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (10, 1, 10);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (11, 1, 11);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (12, 1, 12);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (13, 1, 13);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (14, 1, 14);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (15, 1, 15);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (16, 1, 16);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (17, 1, 17);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (18, 1, 18);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (19, 1, 19);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (20, 1, 20);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (21, 1, 21);
INSERT INTO sys_role_permission (role_permission_id, role_id, permission_id) VALUES (22, 1, 22);

-- 部门
INSERT INTO department (department_id, department_code, department_name, parent_id, leader_id, contact_phone, sort_order, status) VALUES (1, 'ROOT', '根部门', NULL, 1, '13800138000', 1, 'ACTIVE');
INSERT INTO department (department_id, department_code, department_name, parent_id, leader_id, contact_phone, sort_order, status) VALUES (2, 'DEPT001', '技术部', 1, 2, '13800138001', 2, 'ACTIVE');
INSERT INTO department (department_id, department_code, department_name, parent_id, leader_id, contact_phone, sort_order, status) VALUES (3, 'DEPT002', '业务部', 1, 3, '13800138002', 3, 'ACTIVE');
INSERT INTO department (department_id, department_code, department_name, parent_id, leader_id, contact_phone, sort_order, status) VALUES (4, 'DEPT003', '财务部', 1, 4, '13800138003', 4, 'ACTIVE');
INSERT INTO department (department_id, department_code, department_name, parent_id, leader_id, contact_phone, sort_order, status) VALUES (5, 'DEPT004', '研发组', 2, 2, '13800138001', 5, 'ACTIVE');

-- 责任人
INSERT INTO owner (owner_id, employee_no, name, department_id, position, contact_phone, email, status) VALUES (1, 'ADMIN001', '系统管理员', 1, '系统管理员', '13800138000', 'admin@example.com', 'ACTIVE');
INSERT INTO owner (owner_id, employee_no, name, department_id, position, contact_phone, email, status) VALUES (2, 'EMP001', '张三', 2, '技术主管', '13800138001', 'zhangsan@example.com', 'ACTIVE');
INSERT INTO owner (owner_id, employee_no, name, department_id, position, contact_phone, email, status) VALUES (3, 'EMP002', '李四', 3, '业务主管', '13800138002', 'lisi@example.com', 'ACTIVE');
INSERT INTO owner (owner_id, employee_no, name, department_id, position, contact_phone, email, status) VALUES (4, 'EMP003', '王五', 4, '财务主管', '13800138003', 'wangwu@example.com', 'ACTIVE');

-- 分类标准
INSERT INTO classification_standard (standard_id, standard_code, standard_name, standard_description, version, publish_date, status) VALUES (1, 'STD001', '数据资产分类标准V1.0', '数据资产分类分级管理标准', '1.0', '2025-01-01', 'ACTIVE');
INSERT INTO classification_standard (standard_id, standard_code, standard_name, standard_description, version, publish_date, status) VALUES (2, 'STD002', '数据资产分类标准V2.0', '数据资产分类分级管理标准升级版', '2.0', '2025-06-01', 'DRAFT');

-- 数据分类
INSERT INTO data_classification (classification_id, standard_id, classification_code, classification_name, parent_id, level, status, sort_order) VALUES (1, 1, 'BIZ', '业务数据', NULL, 1, 'ACTIVE', 1);
INSERT INTO data_classification (classification_id, standard_id, classification_code, classification_name, parent_id, level, status, sort_order) VALUES (2, 1, 'BIZ_CUST', '客户数据', 1, 2, 'ACTIVE', 2);
INSERT INTO data_classification (classification_id, standard_id, classification_code, classification_name, parent_id, level, status, sort_order) VALUES (3, 1, 'BIZ_PROD', '产品数据', 1, 2, 'ACTIVE', 3);
INSERT INTO data_classification (classification_id, standard_id, classification_code, classification_name, parent_id, level, status, sort_order) VALUES (4, 1, 'FIN', '财务数据', NULL, 1, 'ACTIVE', 4);
INSERT INTO data_classification (classification_id, standard_id, classification_code, classification_name, parent_id, level, status, sort_order) VALUES (5, 1, 'HR', '人力资源数据', NULL, 1, 'ACTIVE', 5);

-- 分级标准
INSERT INTO grading_standard (standard_id, standard_code, standard_name, standard_description, version, publish_date, status) VALUES (1, 'GRD001', '数据资产分级标准V1.0', '数据资产安全等级标准', '1.0', '2025-01-01', 'ACTIVE');

-- 数据分级
INSERT INTO data_grading (grading_id, standard_id, grading_code, grading_name, grading_description, level_value, status, sort_order) VALUES (1, 1, 'L1', '一级', '公开数据，无敏感信息', 1, 'ACTIVE', 1);
INSERT INTO data_grading (grading_id, standard_id, grading_code, grading_name, grading_description, level_value, status, sort_order) VALUES (2, 1, 'L2', '二级', '内部数据，仅限内部使用', 2, 'ACTIVE', 2);
INSERT INTO data_grading (grading_id, standard_id, grading_code, grading_name, grading_description, level_value, status, sort_order) VALUES (3, 1, 'L3', '三级', '敏感数据，包含个人信息', 3, 'ACTIVE', 3);
INSERT INTO data_grading (grading_id, standard_id, grading_code, grading_name, grading_description, level_value, status, sort_order) VALUES (4, 1, 'L4', '四级', '核心数据，关系企业生存', 4, 'ACTIVE', 4);

-- 数据资产
INSERT INTO data_asset (asset_id, asset_code, asset_name, asset_type, system_name, database_type, database_name, table_name, department_id, owner_id, classification_id, grading_id, status) VALUES (1, 'ASSET001', '客户信息表', 'DATABASE', 'CRM系统', 'MYSQL', 'crm_db', 'customer_info', 2, 2, 2, 2, 'ACTIVE');
INSERT INTO data_asset (asset_id, asset_code, asset_name, asset_type, system_name, database_type, database_name, table_name, department_id, owner_id, classification_id, grading_id, status) VALUES (2, 'ASSET002', '订单数据表', 'DATABASE', '订单系统', 'MYSQL', 'order_db', 'order_info', 3, 3, 3, 2, 'ACTIVE');
INSERT INTO data_asset (asset_id, asset_code, asset_name, asset_type, system_name, database_type, database_name, table_name, department_id, owner_id, classification_id, grading_id, status) VALUES (3, 'ASSET003', '财务报表文件', 'FILE', '财务系统', NULL, NULL, NULL, 4, 4, 4, 3, 'ACTIVE');
INSERT INTO data_asset (asset_id, asset_code, asset_name, asset_type, system_name, database_type, database_name, table_name, department_id, owner_id, classification_id, grading_id, status) VALUES (4, 'ASSET004', '员工信息API', 'API', '人事系统', NULL, NULL, NULL, 5, 2, 5, 2, 'ACTIVE');

-- 数据字段
INSERT INTO data_field (field_id, asset_id, field_name, field_code, field_type, is_primary_key, nullable, classification_id, grading_id, status) VALUES (1, 1, 'customer_id', '客户ID', 'BIGINT', 1, 1, 2, 2, 'ACTIVE');
INSERT INTO data_field (field_id, asset_id, field_name, field_code, field_type, is_primary_key, nullable, classification_id, grading_id, status) VALUES (2, 1, 'customer_name', '客户姓名', 'VARCHAR', 0, 1, 2, 3, 'ACTIVE');
INSERT INTO data_field (field_id, asset_id, field_name, field_code, field_type, is_primary_key, nullable, classification_id, grading_id, status) VALUES (3, 1, 'customer_phone', '联系电话', 'VARCHAR', 0, 0, 2, 3, 'ACTIVE');

SELECT '数据同步完成！' AS message;