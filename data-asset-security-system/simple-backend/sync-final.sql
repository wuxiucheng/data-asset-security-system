SET FOREIGN_KEY_CHECKS=0;
DELETE FROM sys_user_role;
DELETE FROM sys_role_permission;
DELETE FROM data_field;
DELETE FROM data_asset;
DELETE FROM data_grading;
DELETE FROM data_classification;
DELETE FROM grading_standard;
DELETE FROM classification_standard;
DELETE FROM owner;
DELETE FROM department;
DELETE FROM sys_permission;
DELETE FROM sys_role;
DELETE FROM sys_user;
SET FOREIGN_KEY_CHECKS=1;

-- sys_user
INSERT INTO sys_user (user_id, username, password, real_name, email, phone, status, created_by, updated_by, deleted) VALUES (1, 'admin', '$2a$12$cR3YHcq.gUUkLtaJipTcHeRaQbRYlGCrNMZkeJsxNlFunDlKDJqju', '系统管理员', 'admin@example.com', '13800138000', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_user (user_id, username, password, real_name, email, phone, status, created_by, updated_by, deleted) VALUES (2, 'user1', '$2a$12$MU4w83mEKb45eq06GH.afevsgfOs7KMOMFr72zOT8gOAa8xLLMxK6', '张三', 'zhangsan@example.com', '13800138001', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_user (user_id, username, password, real_name, email, phone, status, created_by, updated_by, deleted) VALUES (3, 'user2', '$2a$12$JyEc8yLxKM5.ydpNGV00muM2JjgRC5qFizY7TXZ9x3HrUEZ.k16Ty', '李四', 'lisi@example.com', '13800138002', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_user (user_id, username, password, real_name, email, phone, status, created_by, updated_by, deleted) VALUES (4, 'user3', '$2a$12$CA/6AsytLyWXqVf.rqNTT.ho3XiyfVAaL.mCHlXuM5hLPKy5loCse', '王五', 'wangwu@example.com', '13800138003', 'INACTIVE', 1, 1, 0);

-- sys_role
INSERT INTO sys_role (role_id, role_name, role_code, role_type, role_description, status, created_by, updated_by, deleted) VALUES (1, '系统管理员', 'SYSTEM_ADMIN', 'SYSTEM_ADMIN', '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_role (role_id, role_name, role_code, role_type, role_description, status, created_by, updated_by, deleted) VALUES (2, '数据管理员', 'DATA_ADMIN', 'DATA_ADMIN', '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_role (role_id, role_name, role_code, role_type, role_description, status, created_by, updated_by, deleted) VALUES (3, '数据审批人', 'APPROVER', 'APPROVER', '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_role (role_id, role_name, role_code, role_type, role_description, status, created_by, updated_by, deleted) VALUES (4, '数据责任人', 'OWNER', 'OWNER', '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_role (role_id, role_name, role_code, role_type, role_description, status, created_by, updated_by, deleted) VALUES (5, '普通用户', 'USER', 'USER', '', 'ACTIVE', 1, 1, 0);

-- sys_permission
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (1, '系统管理', 'system', 'MENU', NULL, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (2, '用户管理', 'system:user', 'MENU', 1, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (3, '创建用户', 'system:user:create', 'BUTTON', 2, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (4, '编辑用户', 'system:user:edit', 'BUTTON', 2, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (5, '删除用户', 'system:user:delete', 'BUTTON', 2, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (6, '查看用户', 'system:user:view', 'BUTTON', 2, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (7, '角色管理', 'system:role', 'MENU', 1, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (8, '创建角色', 'system:role:create', 'BUTTON', 7, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (9, '编辑角色', 'system:role:edit', 'BUTTON', 7, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (10, '删除角色', 'system:role:delete', 'BUTTON', 7, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (11, '查看角色', 'system:role:view', 'BUTTON', 7, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (12, '权限管理', 'system:permission', 'MENU', 1, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (13, '查看权限', 'system:permission:view', 'BUTTON', 12, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (14, '数据资产管理', 'data', 'MENU', NULL, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (15, '资产列表', 'data:asset', 'MENU', 14, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (16, '创建资产', 'data:asset:create', 'BUTTON', 15, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (17, '编辑资产', 'data:asset:edit', 'BUTTON', 15, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (18, '删除资产', 'data:asset:delete', 'BUTTON', 15, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (19, '导出资产', 'data:asset:export', 'BUTTON', 15, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (20, '分类分级管理', 'data:classification', 'MENU', 14, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (21, '查看分类', 'data:classification:view', 'BUTTON', 20, '', 'ACTIVE', 1, 1, 0);
INSERT INTO sys_permission (permission_id, permission_name, permission_code, permission_type, parent_id, path, status, created_by, updated_by, deleted) VALUES (22, '创建分类', 'data:classification:create', 'BUTTON', 20, '', 'ACTIVE', 1, 1, 0);

-- department
INSERT INTO department (department_id, department_name, department_code, parent_id, status, created_by, updated_by, deleted) VALUES (1, '根部门', 'ROOT', NULL, 'ACTIVE', 1, 1, 0);
INSERT INTO department (department_id, department_name, department_code, parent_id, status, created_by, updated_by, deleted) VALUES (2, '技术部', 'DEPT001', 1, 'ACTIVE', 1, 1, 0);
INSERT INTO department (department_id, department_name, department_code, parent_id, status, created_by, updated_by, deleted) VALUES (3, '业务部', 'DEPT002', 1, 'ACTIVE', 1, 1, 0);
INSERT INTO department (department_id, department_name, department_code, parent_id, status, created_by, updated_by, deleted) VALUES (4, '财务部', 'DEPT003', 1, 'ACTIVE', 1, 1, 0);
INSERT INTO department (department_id, department_name, department_code, parent_id, status, created_by, updated_by, deleted) VALUES (5, '研发组', 'DEPT004', 2, 'ACTIVE', 1, 1, 0);

-- owner
INSERT INTO owner (owner_id, employee_no, name, department_id, position, contact_phone, email, status, created_by, updated_by, deleted) VALUES (1, 'ADMIN001', '系统管理员', 1, '系统管理员', '13800138000', 'admin@example.com', 'ACTIVE', 1, 1, 0);
INSERT INTO owner (owner_id, employee_no, name, department_id, position, contact_phone, email, status, created_by, updated_by, deleted) VALUES (2, 'EMP001', '张三', 2, '技术主管', '13800138001', 'zhangsan@example.com', 'ACTIVE', 1, 1, 0);
INSERT INTO owner (owner_id, employee_no, name, department_id, position, contact_phone, email, status, created_by, updated_by, deleted) VALUES (3, 'EMP002', '李四', 3, '业务主管', '13800138002', 'lisi@example.com', 'ACTIVE', 1, 1, 0);
INSERT INTO owner (owner_id, employee_no, name, department_id, position, contact_phone, email, status, created_by, updated_by, deleted) VALUES (4, 'EMP003', '王五', 4, '财务主管', '13800138003', 'wangwu@example.com', 'ACTIVE', 1, 1, 0);

-- classification_standard
INSERT INTO classification_standard (standard_id, standard_name, standard_code, version, status) VALUES (1, '数据资产分类标准V1.0', 'STD001', '1.0', 'ACTIVE');
INSERT INTO classification_standard (standard_id, standard_name, standard_code, version, status) VALUES (2, '数据资产分类标准V2.0', 'STD002', '2.0', 'DRAFT');

-- grading_standard
INSERT INTO grading_standard (standard_id, standard_name, standard_code, version, status) VALUES (1, '数据资产分级标准V1.0', 'GRD001', '1.0', 'ACTIVE');

-- data_classification
INSERT INTO data_classification (classification_id, classification_name, classification_code, parent_id, standard_id, classification_description, status, creator_id, updater_id, deleted) VALUES (1, '业务数据', 'BIZ', NULL, 1, '', 'ACTIVE', 1, 1, 0);
INSERT INTO data_classification (classification_id, classification_name, classification_code, parent_id, standard_id, classification_description, status, creator_id, updater_id, deleted) VALUES (2, '客户数据', 'BIZ_CUST', 1, 1, '', 'ACTIVE', 1, 1, 0);
INSERT INTO data_classification (classification_id, classification_name, classification_code, parent_id, standard_id, classification_description, status, creator_id, updater_id, deleted) VALUES (3, '产品数据', 'BIZ_PROD', 1, 1, '', 'ACTIVE', 1, 1, 0);
INSERT INTO data_classification (classification_id, classification_name, classification_code, parent_id, standard_id, classification_description, status, creator_id, updater_id, deleted) VALUES (4, '财务数据', 'FIN', NULL, 1, '', 'ACTIVE', 1, 1, 0);
INSERT INTO data_classification (classification_id, classification_name, classification_code, parent_id, standard_id, classification_description, status, creator_id, updater_id, deleted) VALUES (5, '人力资源数据', 'HR', NULL, 1, '', 'ACTIVE', 1, 1, 0);

-- data_grading
INSERT INTO data_grading (grading_id, grading_name, grading_code, level_value, standard_id, grading_description, status, creator_id, updater_id, deleted) VALUES (1, '一级', 'L1', 1, 1, '', 'ACTIVE', 1, 1, 0);
INSERT INTO data_grading (grading_id, grading_name, grading_code, level_value, standard_id, grading_description, status, creator_id, updater_id, deleted) VALUES (2, '二级', 'L2', 1, 1, '', 'ACTIVE', 1, 1, 0);
INSERT INTO data_grading (grading_id, grading_name, grading_code, level_value, standard_id, grading_description, status, creator_id, updater_id, deleted) VALUES (3, '三级', 'L3', 1, 1, '', 'ACTIVE', 1, 1, 0);
INSERT INTO data_grading (grading_id, grading_name, grading_code, level_value, standard_id, grading_description, status, creator_id, updater_id, deleted) VALUES (4, '四级', 'L4', 1, 1, '', 'ACTIVE', 1, 1, 0);

-- data_asset
INSERT INTO data_asset (asset_id, asset_name, asset_code, asset_type, system_name, database_type, database_name, table_name, department_id, owner_id, classification_id, grading_id, status, asset_description, creator_id, updater_id, deleted) VALUES (1, '客户信息表', 'ASSET001', 'DATABASE', 'CRM系统', 'MYSQL', 'crm_db', 'customer_info', 2, 2, 2, 2, 'ACTIVE', '', 1, 1, 0);
INSERT INTO data_asset (asset_id, asset_name, asset_code, asset_type, system_name, database_type, database_name, table_name, department_id, owner_id, classification_id, grading_id, status, asset_description, creator_id, updater_id, deleted) VALUES (2, '订单数据表', 'ASSET002', 'DATABASE', '订单系统', 'MYSQL', 'order_db', 'order_info', 3, 3, 3, 2, 'ACTIVE', '', 1, 1, 0);
INSERT INTO data_asset (asset_id, asset_name, asset_code, asset_type, system_name, database_type, database_name, table_name, department_id, owner_id, classification_id, grading_id, status, asset_description, creator_id, updater_id, deleted) VALUES (3, '财务报表文件', 'ASSET003', 'FILE', '财务系统', 'None', 'None', 'None', 4, 4, 4, 3, 'ACTIVE', '', 1, 1, 0);
INSERT INTO data_asset (asset_id, asset_name, asset_code, asset_type, system_name, database_type, database_name, table_name, department_id, owner_id, classification_id, grading_id, status, asset_description, creator_id, updater_id, deleted) VALUES (4, '员工信息API', 'ASSET004', 'API', '人事系统', 'None', 'None', 'None', 5, 2, 5, 2, 'ACTIVE', '', 1, 1, 0);

-- data_field
INSERT INTO data_field (field_id, field_name, field_code, field_type, asset_id, is_primary_key, nullable, classification_id, grading_id, field_description, creator_id, deleted) VALUES (1, 'customer_id', '客户ID', 'BIGINT', 1, 1, 1, 2, 2, '', 1, 0);
INSERT INTO data_field (field_id, field_name, field_code, field_type, asset_id, is_primary_key, nullable, classification_id, grading_id, field_description, creator_id, deleted) VALUES (2, 'customer_name', '客户姓名', 'VARCHAR', 1, 0, 1, 2, 3, '', 1, 0);
INSERT INTO data_field (field_id, field_name, field_code, field_type, asset_id, is_primary_key, nullable, classification_id, grading_id, field_description, creator_id, deleted) VALUES (3, 'customer_phone', '联系电话', 'VARCHAR', 1, 0, 1, 2, 3, '', 1, 0);

-- sys_user_role
INSERT INTO sys_user_role (user_role_id, user_id, role_id) VALUES (1, 1, 1);
INSERT INTO sys_user_role (user_role_id, user_id, role_id) VALUES (2, 1, 2);
INSERT INTO sys_user_role (user_role_id, user_id, role_id) VALUES (3, 2, 5);
INSERT INTO sys_user_role (user_role_id, user_id, role_id) VALUES (4, 3, 4);
INSERT INTO sys_user_role (user_role_id, user_id, role_id) VALUES (5, 3, 5);
INSERT INTO sys_user_role (user_role_id, user_id, role_id) VALUES (6, 4, 5);

-- sys_role_permission
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