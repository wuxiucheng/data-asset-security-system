# Mock后端与Spring Boot后端同步指南

## 问题背景

项目存在两个后端：
1. **Mock后端** (simple-backend): Node.js + Express，内存存储
2. **Spring Boot后端** (backend): Spring Boot + MySQL，数据持久化

**核心原则：以Spring Boot实体类为准，Mock数据按实体类字段同步到MySQL**

## 同步架构

```
Mock server.js
    ↓ (extract-all-mock-data.js)
JSON数据
    ↓ (generate-full-sync-sql.py)
sync-mock-data.sql
    ↓ (mysql执行)
MySQL数据库
    ↓ (MyBatis-Plus)
Spring Boot后端
```

## 同步工具

| 文件 | 用途 |
|------|------|
| `extract-all-mock-data.js` | 从Mock server.js源码提取所有数据为JSON |
| `generate-full-sync-sql.py` | 读取JSON，按MySQL实际表结构生成INSERT SQL |
| `sync-mock-data.sql` | 最终生成的数据同步SQL（含BCrypt密码） |
| `create-tables-complete.sql` | 完整建表SQL（从当前MySQL导出） |

## 数据结构映射

### 表名映射

| Mock数据变量 | MySQL表名 | Spring Boot实体类 |
|-------------|----------|-----------------|
| mockUsers | sys_user | SysUser |
| mockRoles | sys_role | SysRole |
| mockPermissions | sys_permission | SysPermission |
| mockUserRoles | sys_user_role | SysUserRole |
| mockDepartments | department | Department |
| mockOwners | owner | Owner |
| mockClassificationStandards | classification_standard | ClassificationStandard |
| mockClassifications | data_classification | DataClassification |
| mockGradingStandards | grading_standard | GradingStandard |
| mockGradings | data_grading | DataGrading |
| mockAssets | data_asset | DataAsset |
| mockFields | data_field | DataField |

### 字段命名转换

**规则：Mock驼峰命名 → MySQL下划线命名（MyBatis-Plus自动转换）**

```
Mock (camelCase)        MySQL (snake_case)
departmentId       →   department_id
assetName          →   asset_name
isPrimaryKey       →   is_primary_key
classificationCode →   classification_code
```

**MyBatis-Plus配置：**
```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

### BaseEntity公共字段

所有业务实体继承`BaseEntity`，MySQL表必须包含以下字段：

| 字段 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| creator_id | BIGINT | 创建人ID | NULL |
| created_time | DATETIME | 创建时间 | CURRENT_TIMESTAMP |
| updater_id | BIGINT | 更新人ID | NULL |
| updated_time | DATETIME | 更新时间 | CURRENT_TIMESTAMP ON UPDATE |
| deleted | INT | 逻辑删除 | 0 |

### 特殊字段处理

| 场景 | Mock | MySQL | 处理方式 |
|------|------|-------|---------|
| 密码 | 明文 | BCrypt哈希 | Python bcrypt生成，`$2b$` → `$2a$` |
| 布尔值 | true/false | TINYINT 0/1 | MyBatis-Plus自动转换 |
| 空值 | null | NULL | SQL中直接使用NULL |

## API兼容性

### 响应码

| 后端 | 成功码 | 前端兼容 |
|------|--------|---------|
| Mock | 0 | `code === 0 \|\| code === 200` |
| Spring Boot | 200 | 同上 |

### 分页响应

| 后端 | 列表字段 | 前端处理 |
|------|---------|---------|
| Mock | `data.list` | 直接使用 |
| Spring Boot | `data.records` | 响应拦截器自动转换 `records → list` |

### API路径

前端已统一为Spring Boot的RESTful风格：

| 前端调用 | Mock路径 | Spring Boot路径 |
|---------|---------|----------------|
| 部门列表 | GET /department/list | POST /department/page |
| 资产列表 | GET /asset/list | POST /asset/page |
| 权限列表 | GET /permission/list | GET /permission/all |

## 同步操作

### 完整同步流程

```bash
# 1. 从Mock提取数据
node backend/extract-all-mock-data.js > /tmp/all-mock-data.json

# 2. 生成同步SQL（自动读取MySQL表结构，只插入存在的字段）
python3 backend/generate-full-sync-sql.py

# 3. 执行同步
mysql -uroot -p1q2w3e4r < backend/sync-mock-data.sql

# 4. 重启Spring Boot
./restart.sh real

# 5. 验证
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 仅建表

```bash
mysql -uroot -p1q2w3e4r < backend/create-tables-complete.sql
```

### 验证数据一致性

```bash
mysql -uroot -p1q2w3e4r data_asset_security -e "
SELECT 'department' AS tbl, COUNT(*) AS cnt FROM department
UNION ALL SELECT 'owner', COUNT(*) FROM owner
UNION ALL SELECT 'data_asset', COUNT(*) FROM data_asset
UNION ALL SELECT 'data_field', COUNT(*) FROM data_field
UNION ALL SELECT 'data_classification', COUNT(*) FROM data_classification
UNION ALL SELECT 'data_grading', COUNT(*) FROM data_grading;
"
```

## 开发流程

### 新增功能

```
1. 在Spring Boot实体类添加字段
   ↓
2. 在MySQL表添加对应字段（snake_case）
   ↓
3. 在Mock后端添加对应数据
   ↓
4. 更新同步脚本
   ↓
5. 测试验证
```

### 数据结构变更

```
1. 修改Spring Boot实体类
   ↓
2. ALTER TABLE添加/修改MySQL字段
   ↓
3. 更新create-tables-complete.sql
   ↓
4. 修改Mock数据
   ↓
5. 重新生成sync-mock-data.sql
   ↓
6. 测试验证
```

## 已知问题与修复

### 1. MFA登录Bug

**问题：** `AuthServiceImpl.checkMfaEnabled()`查询了`auth_session`表而非`auth_mfa_config`表

**修复：** 暂时让`checkMfaEnabled()`返回`false`，MFA功能待后续完善

### 2. 表缺少BaseEntity字段

**问题：** 手动建表时遗漏`creator_id`、`created_time`等BaseEntity公共字段

**修复：** 使用`create-tables-complete.sql`建表，或手动ALTER TABLE添加

### 3. API路径不匹配

**问题：** Spring Boot用RESTful路径（`/departments`），前端调Mock路径（`/department/list`）

**修复：**
- Controller路径改为单数形式：`/departments` → `/department`
- 前端API改为POST分页：`/department/list`(GET) → `/department/page`(POST)
- 响应拦截器自动转换：`records` → `list`

## 同步检查清单

### 数据结构检查
- [ ] MySQL表包含所有BaseEntity公共字段
- [ ] 字段名转换正确（驼峰→下划线）
- [ ] 字段类型匹配
- [ ] 逻辑删除字段`deleted`存在
- [ ] 主键、唯一键设置正确

### 数据内容检查
- [ ] 初始数据与Mock一致
- [ ] 用户密码使用BCrypt加密
- [ ] 关联关系正确（外键ID匹配）
- [ ] 状态值一致

### API接口检查
- [ ] Controller路径为单数形式
- [ ] 分页接口使用POST + /page
- [ ] 响应码兼容（0和200）
- [ ] 分页响应records→list转换正常

## 快速操作

```bash
# 完整重建
mysql -uroot -p1q2w3e4r < backend/create-tables-complete.sql
mysql -uroot -p1q2w3e4r < backend/sync-mock-data.sql

# 仅同步数据
node backend/extract-all-mock-data.js > /tmp/all-mock-data.json
python3 backend/generate-full-sync-sql.py
mysql -uroot -p1q2w3e4r < backend/sync-mock-data.sql

# 重启并验证
./restart.sh real
./status.sh
```
