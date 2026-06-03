# 实体类字段映射修复完成总结

## 问题原因

数据库表字段名与Java实体类字段名不匹配，导致MyBatis-Plus无法正确映射，引发HTTP 500错误。

## 已修复的实体类

### 1. LineageRelation.java
**字段映射：**
- `transformation` → `transform_desc`
- `source` → `source_type`

### 2. LcPolicy.java
**字段映射：**
- `policyType` → `grading_level`
- `retentionDays` → `retention_period`
- `archiveAction` → `expire_action`

### 3. LcStatus.java
**字段映射：**
- `statusId` → `lifecycle_id` (主键)
- `stageEndTime` → `expire_date`
- `status` → `dispose_status`

### 4. DataStandard.java
**字段映射：**
- `description` → `standard_content`

### 5. ComplianceClause.java
**字段映射：**
- `clauseName` → `clause_content`
- `regulation` → `regulation_source`
- `description` → `eval_method`
- `status` → `compliance_status`

### 6. GovernanceKpi.java
**字段映射：**
- `actualValue` → `kpi_value`
- `targetValue` → `kpi_target`
- `period` → `record_date`
- 移除BaseEntity继承（表中无creator_id等字段）

### 7. ImpactAnalysis.java
**字段映射：**
- `changeAssetId` → `source_asset_id`
- `changeFieldId` → `affected_asset_id`
- `affectedAssets` → `impact_path`

## manage.sh 脚本更新

已将字段映射修复整合到 `manage.sh` 管理脚本中：

### 新增功能

1. **test 命令** - 测试所有API端点
   ```bash
   ./manage.sh test
   ```

2. **restart 支持 build 参数** - 重启时重新编译
   ```bash
   ./manage.sh restart real build
   ```

3. **帮助信息更新** - 添加字段映射修复说明

### 使用方法

```bash
# 首次启动或修改实体类后
./manage.sh start real build

# 重启并重新编译
./manage.sh restart real build

# 测试所有API
./manage.sh test

# 查看状态
./manage.sh status

# 查看日志
./manage.sh logs backend
```

## 测试结果

所有API端点测试通过：

✅ lineage/relation/list - 数据血缘关系
✅ lineage/analysis/list - 影响分析
✅ lifecycle/policy/list - 生命周期策略
✅ lifecycle/status/list - 生命周期状态
✅ compliance/standard/list - 数据标准
✅ compliance/clause/list - 合规条款
✅ compliance/kpi/list - 治理KPI

## 修复时间

2026-06-02

## 注意事项

1. **不要修改数据库表结构** - 数据库已有数据，修改表结构可能导致数据丢失
2. **使用@TableField注解** - 这是MyBatis-Plus提供的标准方式，不影响代码可读性
3. **保持实体类字段名语义化** - 实体类字段名应保持业务语义，通过注解映射到数据库字段
4. **修改实体类后需重新编译** - 使用 `./manage.sh restart real build` 或 `./manage.sh start real build`

## 相关文件

- 实体类位置: `/backend/src/main/java/com/dataasset/security/entity/`
- 管理脚本: `/manage.sh`
- 修复说明: `/backend/ENTITY_FIELD_MAPPING_FIX.md`
- 初始化SQL: `/backend/src/main/resources/db/init.sql`
