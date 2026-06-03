-- 自动从Spring Boot实体类生成建表SQL
USE data_asset_security;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS grading_standard;
DROP TABLE IF EXISTS data_asset;
DROP TABLE IF EXISTS approval_process_instance;
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS data_grading;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS auth_password_history;
DROP TABLE IF EXISTS auth_mfa_config;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS data_classification;
DROP TABLE IF EXISTS owner;
DROP TABLE IF EXISTS data_field;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS auth_account_lock;
DROP TABLE IF EXISTS approval_task;
DROP TABLE IF EXISTS auth_login_log;
DROP TABLE IF EXISTS auth_session;
DROP TABLE IF EXISTS approval_process_definition;
DROP TABLE IF EXISTS classification_standard;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE sys_user_role (
  user_role_id BIGINT NOT NULL,
  user_id BIGINT,
  role_id BIGINT,
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_role_id)
);

CREATE TABLE grading_standard (
  standard_id BIGINT NOT NULL AUTO_INCREMENT,
  standard_code VARCHAR(255),
  standard_name VARCHAR(255),
  standard_description VARCHAR(500),
  version VARCHAR(255),
  publish_date VARCHAR(255),
  publish_unit VARCHAR(255),
  scope VARCHAR(255),
  status VARCHAR(255) DEFAULT 'ACTIVE',
  creator_id BIGINT,
  updater_id BIGINT,
  PRIMARY KEY (standard_id)
);

CREATE TABLE data_asset (
  asset_id BIGINT NOT NULL AUTO_INCREMENT,
  asset_name VARCHAR(255),
  asset_code VARCHAR(255),
  asset_type VARCHAR(255),
  system_name VARCHAR(255),
  database_type VARCHAR(255),
  database_host VARCHAR(255),
  database_port INT,
  database_name VARCHAR(255),
  table_name VARCHAR(255),
  asset_description VARCHAR(500),
  department_id BIGINT,
  owner_id BIGINT,
  classification_id BIGINT,
  grading_id BIGINT,
  sensitivity_score INT,
  data_volume_level VARCHAR(255),
  access_frequency VARCHAR(255),
  importance_level VARCHAR(255),
  status VARCHAR(255) DEFAULT 'ACTIVE',
  contains_sensitive_data TINYINT,
  sensitive_data_type VARCHAR(255),
  last_scan_time DATETIME,
  last_scan_result VARCHAR(255),
  expire_time DATETIME,
  remarks VARCHAR(255),
  PRIMARY KEY (asset_id)
);

CREATE TABLE approval_process_instance (
  instance_id BIGINT NOT NULL AUTO_INCREMENT,
  definition_id BIGINT,
  process_instance_id VARCHAR(255),
  process_definition_key VARCHAR(255),
  process_definition_name VARCHAR(255),
  process_type VARCHAR(255),
  business_type VARCHAR(255),
  business_id BIGINT,
  business_title VARCHAR(255),
  applicant_id BIGINT,
  applicant_name VARCHAR(255),
  applicant_department_id BIGINT,
  applicant_department_name VARCHAR(255),
  apply_time DATETIME,
  current_task_id VARCHAR(255),
  current_task_name VARCHAR(255),
  current_assignee_id BIGINT,
  current_assignee_name VARCHAR(255),
  process_status VARCHAR(255),
  approval_result VARCHAR(255),
  approval_comment VARCHAR(255),
  completed_time DATETIME,
  process_variables VARCHAR(255),
  remarks VARCHAR(255),
  PRIMARY KEY (instance_id)
);

CREATE TABLE audit_log (
  log_id BIGINT NOT NULL AUTO_INCREMENT,
  operation_type VARCHAR(255),
  module VARCHAR(255),
  operation_description VARCHAR(500),
  operator_id BIGINT,
  operator_name VARCHAR(255),
  operation_time DATETIME,
  object_type VARCHAR(255),
  object_id BIGINT,
  object_name VARCHAR(255),
  operation_content VARCHAR(255),
  operation_result VARCHAR(255),
  ip_address VARCHAR(255),
  user_agent VARCHAR(255),
  request_method VARCHAR(255),
  request_url VARCHAR(255),
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (log_id)
);

CREATE TABLE sys_role (
  role_id BIGINT NOT NULL,
  role_code VARCHAR(255),
  role_name VARCHAR(255),
  role_description VARCHAR(500),
  role_type VARCHAR(255),
  status VARCHAR(255) DEFAULT 'ACTIVE',
  sort_order INT,
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0,
  PRIMARY KEY (role_id)
);

CREATE TABLE data_grading (
  grading_id BIGINT NOT NULL AUTO_INCREMENT,
  standard_id BIGINT,
  grading_code VARCHAR(255),
  grading_name VARCHAR(255),
  grading_description VARCHAR(500),
  level_value INT,
  color_code VARCHAR(255),
  security_requirements VARCHAR(255),
  sort_order INT,
  status VARCHAR(255) DEFAULT 'ACTIVE',
  creator_id BIGINT,
  updater_id BIGINT,
  PRIMARY KEY (grading_id)
);

CREATE TABLE department (
  department_id BIGINT NOT NULL,
  department_code VARCHAR(255),
  department_name VARCHAR(255),
  leader_id BIGINT,
  contact_phone VARCHAR(255),
  department_description VARCHAR(500),
  parent_id BIGINT,
  sort_order INT,
  status VARCHAR(255) DEFAULT 'ACTIVE',
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0,
  PRIMARY KEY (department_id)
);

CREATE TABLE auth_password_history (
  history_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT,
  password VARCHAR(255),
  change_time DATETIME,
  change_type VARCHAR(255),
  operator_id BIGINT,
  operator_name VARCHAR(255),
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (history_id)
);

CREATE TABLE auth_mfa_config (
  config_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT,
  mfa_type VARCHAR(255),
  secret_key VARCHAR(255),
  phone VARCHAR(255),
  email VARCHAR(255),
  qr_code_url VARCHAR(255),
  backup_codes VARCHAR(255),
  status VARCHAR(255) DEFAULT 'ACTIVE',
  enabled_time DATETIME,
  last_verified_time DATETIME,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (config_id)
);

CREATE TABLE sys_user (
  user_id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(255),
  password VARCHAR(255),
  real_name VARCHAR(255),
  email VARCHAR(255),
  phone VARCHAR(255),
  status VARCHAR(255) DEFAULT 'ACTIVE',
  last_login_time DATETIME,
  last_login_ip VARCHAR(255),
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0,
  PRIMARY KEY (user_id)
);

CREATE TABLE data_classification (
  classification_id BIGINT NOT NULL AUTO_INCREMENT,
  standard_id BIGINT,
  classification_code VARCHAR(255),
  classification_name VARCHAR(255),
  classification_description VARCHAR(500),
  parent_id BIGINT,
  level INT,
  sort_order INT,
  status VARCHAR(255) DEFAULT 'ACTIVE',
  creator_id BIGINT,
  updater_id BIGINT,
  PRIMARY KEY (classification_id)
);

CREATE TABLE owner (
  owner_id BIGINT NOT NULL,
  employee_no VARCHAR(255),
  name VARCHAR(255),
  department_id BIGINT,
  position VARCHAR(255),
  contact_phone VARCHAR(255),
  email VARCHAR(255),
  user_account VARCHAR(255),
  status VARCHAR(255) DEFAULT 'ACTIVE',
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0,
  PRIMARY KEY (owner_id)
);

CREATE TABLE data_field (
  field_id BIGINT NOT NULL AUTO_INCREMENT,
  asset_id BIGINT,
  field_name VARCHAR(255),
  field_code VARCHAR(255),
  field_type VARCHAR(255),
  field_length INT,
  nullable TINYINT,
  is_primary_key TINYINT,
  field_description VARCHAR(500),
  classification_id BIGINT,
  grading_id BIGINT,
  contains_sensitive_data TINYINT,
  sensitive_data_type VARCHAR(255),
  default_value VARCHAR(255),
  sort_order INT,
  status VARCHAR(255) DEFAULT 'ACTIVE',
  PRIMARY KEY (field_id)
);

CREATE TABLE sys_role_permission (
  role_permission_id BIGINT NOT NULL,
  role_id BIGINT,
  permission_id BIGINT,
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (role_permission_id)
);

CREATE TABLE sys_permission (
  permission_id BIGINT NOT NULL,
  permission_code VARCHAR(255),
  permission_name VARCHAR(255),
  permission_type VARCHAR(255),
  parent_id BIGINT,
  path VARCHAR(255),
  component VARCHAR(255),
  icon VARCHAR(255),
  sort_order INT,
  status VARCHAR(255) DEFAULT 'ACTIVE',
  created_by BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0,
  PRIMARY KEY (permission_id)
);

CREATE TABLE auth_account_lock (
  lock_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT,
  username VARCHAR(255),
  lock_type VARCHAR(255),
  lock_reason VARCHAR(255),
  lock_time DATETIME,
  unlock_time DATETIME,
  lock_duration INT,
  status VARCHAR(255) DEFAULT 'ACTIVE',
  unlock_operator_id BIGINT,
  unlock_operator_name VARCHAR(255),
  unlock_reason VARCHAR(255),
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (lock_id)
);

CREATE TABLE approval_task (
  task_id BIGINT NOT NULL AUTO_INCREMENT,
  instance_id BIGINT,
  process_instance_id VARCHAR(255),
  task_id_flowable VARCHAR(255),
  task_name VARCHAR(255),
  task_definition_key VARCHAR(255),
  assignee_id BIGINT,
  assignee_name VARCHAR(255),
  candidate_groups VARCHAR(255),
  task_status VARCHAR(255),
  task_start_time DATETIME,
  task_end_time DATETIME,
  approval_result VARCHAR(255),
  approval_comment VARCHAR(255),
  priority VARCHAR(255),
  due_date DATETIME,
  task_variables VARCHAR(255),
  PRIMARY KEY (task_id)
);

CREATE TABLE auth_login_log (
  log_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT,
  username VARCHAR(255),
  real_name VARCHAR(255),
  login_time DATETIME,
  login_ip VARCHAR(255),
  login_location VARCHAR(255),
  device_info VARCHAR(255),
  browser_info VARCHAR(255),
  login_status VARCHAR(255),
  failure_reason VARCHAR(255),
  session_id VARCHAR(255),
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (log_id)
);

CREATE TABLE auth_session (
  session_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT,
  username VARCHAR(255),
  token VARCHAR(255),
  refresh_token VARCHAR(255),
  login_time DATETIME,
  last_access_time DATETIME,
  expire_time DATETIME,
  login_ip VARCHAR(255),
  device_info VARCHAR(255),
  browser_info VARCHAR(255),
  status VARCHAR(255) DEFAULT 'ACTIVE',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (session_id)
);

CREATE TABLE approval_process_definition (
  definition_id BIGINT NOT NULL AUTO_INCREMENT,
  process_definition_key VARCHAR(255),
  process_definition_name VARCHAR(255),
  process_definition_description VARCHAR(500),
  process_type VARCHAR(255),
  version INT,
  bpmn_process_definition_id VARCHAR(255),
  bpmn_process_definition_key VARCHAR(255),
  deployment_id VARCHAR(255),
  resource_name VARCHAR(255),
  bpmn_content VARCHAR(255),
  process_image VARCHAR(255),
  status VARCHAR(255) DEFAULT 'ACTIVE',
  enabled TINYINT,
  creator_id BIGINT,
  updater_id BIGINT,
  PRIMARY KEY (definition_id)
);

CREATE TABLE classification_standard (
  standard_id BIGINT NOT NULL AUTO_INCREMENT,
  standard_code VARCHAR(255),
  standard_name VARCHAR(255),
  standard_description VARCHAR(500),
  version VARCHAR(255),
  publish_date VARCHAR(255),
  publish_unit VARCHAR(255),
  scope VARCHAR(255),
  status VARCHAR(255) DEFAULT 'ACTIVE',
  creator_id BIGINT,
  updater_id BIGINT,
  PRIMARY KEY (standard_id)
);

-- 添加索引
CREATE INDEX idx_sys_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX idx_sys_user_role_role_id ON sys_user_role(role_id);
CREATE INDEX idx_grading_standard_creator_id ON grading_standard(creator_id);
CREATE INDEX idx_grading_standard_updater_id ON grading_standard(updater_id);
CREATE INDEX idx_data_asset_department_id ON data_asset(department_id);
CREATE INDEX idx_data_asset_owner_id ON data_asset(owner_id);
CREATE INDEX idx_data_asset_classification_id ON data_asset(classification_id);
CREATE INDEX idx_data_asset_grading_id ON data_asset(grading_id);
CREATE INDEX idx_approval_process_instance_definition_id ON approval_process_instance(definition_id);
CREATE INDEX idx_approval_process_instance_process_instance_id ON approval_process_instance(process_instance_id);
CREATE INDEX idx_approval_process_instance_business_id ON approval_process_instance(business_id);
CREATE INDEX idx_approval_process_instance_applicant_id ON approval_process_instance(applicant_id);
CREATE INDEX idx_approval_process_instance_applicant_department_id ON approval_process_instance(applicant_department_id);
CREATE INDEX idx_approval_process_instance_current_task_id ON approval_process_instance(current_task_id);
CREATE INDEX idx_approval_process_instance_current_assignee_id ON approval_process_instance(current_assignee_id);
CREATE INDEX idx_audit_log_operator_id ON audit_log(operator_id);
CREATE INDEX idx_audit_log_object_id ON audit_log(object_id);
CREATE INDEX idx_data_grading_standard_id ON data_grading(standard_id);
CREATE INDEX idx_data_grading_creator_id ON data_grading(creator_id);
CREATE INDEX idx_data_grading_updater_id ON data_grading(updater_id);
CREATE INDEX idx_department_leader_id ON department(leader_id);
CREATE INDEX idx_department_parent_id ON department(parent_id);
CREATE INDEX idx_auth_password_history_user_id ON auth_password_history(user_id);
CREATE INDEX idx_auth_password_history_operator_id ON auth_password_history(operator_id);
CREATE INDEX idx_auth_mfa_config_user_id ON auth_mfa_config(user_id);
CREATE INDEX idx_data_classification_standard_id ON data_classification(standard_id);
CREATE INDEX idx_data_classification_parent_id ON data_classification(parent_id);
CREATE INDEX idx_data_classification_creator_id ON data_classification(creator_id);
CREATE INDEX idx_data_classification_updater_id ON data_classification(updater_id);
CREATE INDEX idx_owner_department_id ON owner(department_id);
CREATE INDEX idx_data_field_asset_id ON data_field(asset_id);
CREATE INDEX idx_data_field_classification_id ON data_field(classification_id);
CREATE INDEX idx_data_field_grading_id ON data_field(grading_id);
CREATE INDEX idx_sys_role_permission_role_id ON sys_role_permission(role_id);
CREATE INDEX idx_sys_role_permission_permission_id ON sys_role_permission(permission_id);
CREATE INDEX idx_sys_permission_parent_id ON sys_permission(parent_id);
CREATE INDEX idx_auth_account_lock_user_id ON auth_account_lock(user_id);
CREATE INDEX idx_auth_account_lock_unlock_operator_id ON auth_account_lock(unlock_operator_id);
CREATE INDEX idx_approval_task_instance_id ON approval_task(instance_id);
CREATE INDEX idx_approval_task_process_instance_id ON approval_task(process_instance_id);
CREATE INDEX idx_approval_task_assignee_id ON approval_task(assignee_id);
CREATE INDEX idx_auth_login_log_user_id ON auth_login_log(user_id);
CREATE INDEX idx_auth_login_log_session_id ON auth_login_log(session_id);
CREATE INDEX idx_auth_session_user_id ON auth_session(user_id);
CREATE INDEX idx_approval_process_definition_bpmn_process_definition_id ON approval_process_definition(bpmn_process_definition_id);
CREATE INDEX idx_approval_process_definition_deployment_id ON approval_process_definition(deployment_id);
CREATE INDEX idx_approval_process_definition_creator_id ON approval_process_definition(creator_id);
CREATE INDEX idx_approval_process_definition_updater_id ON approval_process_definition(updater_id);
CREATE INDEX idx_classification_standard_creator_id ON classification_standard(creator_id);
CREATE INDEX idx_classification_standard_updater_id ON classification_standard(updater_id);

SELECT '建表完成！' AS message;