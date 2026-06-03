# 实体类字段映射修复说明

## 问题原因

数据库表字段名与Java实体类字段名不匹配，导致MyBatis-Plus无法正确映射，引发500错误。

## 已修复的实体类

### 1. LineageRelation.java
**问题字段映射：**
- 实体类 `transformation` → 数据库 `transform_desc`
- 实体类 `source` → 数据库 `source_type`

**修复方式：**
```java
@TableField("transform_desc")
private String transformation;

@TableField("source_type")
private String source;
```

### 2. LcPolicy.java
**问题字段映射：**
- 实体类 `policyType` → 数据库 `grading_level`
- 实体类 `retentionDays` → 数据库 `retention_period`
- 实体类 `archiveAction` → 数据库 `expire_action`

**修复方式：**
```java
@TableField("grading_level")
private String policyType;

@TableField("retention_period")
private Integer retentionDays;

@TableField("expire_action")
private String archiveAction;
```

### 3. DataStandard.java
**问题字段映射：**
- 实体类 `description` → 数据库 `standard_content`

**修复方式：**
```java
@TableField("standard_content")
private String description;
```

### 4. ComplianceClause.java
**问题字段映射：**
- 实体类 `clauseName` → 数据库 `clause_content`
- 实体类 `regulation` → 数据库 `regulation_source`
- 实体类 `description` → 数据库 `eval_method`
- 实体类 `status` → 数据库 `compliance_status`

**修复方式：**
```java
@TableField("clause_content")
private String clauseName;

@TableField("regulation_source")
private String regulation;

@TableField("eval_method")
private String description;

@TableField("compliance_status")
private String status;
```

### 5. GovernanceKpi.java
**问题字段映射：**
- 实体类 `actualValue` → 数据库 `kpi_value`
- 实体类 `targetValue` → 数据库 `kpi_target`

**修复方式：**
```java
@TableField("kpi_value")
private Double actualValue;

@TableField("kpi_target")
private Double targetValue;
```

## 下一步操作

### 方法1：重新编译并重启（推荐）

```bash
# 1. 停止后端服务
ps aux | grep "data-asset-security-1.0.0.jar" | grep -v grep | awk '{print $2}' | xargs kill

# 2. 重新编译
cd /Users/wuxiucheng/分级分类/data-asset-security-system/backend
mvn clean package -DskipTests

# 3. 启动后端
java -jar target/data-asset-security-1.0.0.jar --spring.profiles.active=dev
```

### 方法2：使用IDE重新编译

如果您使用IDE（如IntelliJ IDEA）：
1. 点击 Build → Rebuild Project
2. 停止当前运行的应用
3. 重新运行应用

## 验证修复

修复后，测试以下API端点：

```bash
# 1. 登录获取token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. 测试血缘关系API
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/lineage/relation/list

# 3. 测试生命周期API
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/lifecycle/policy/list

# 4. 测试合规管理API
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/compliance/standard/list
```

## 注意事项

1. **不要修改数据库表结构**：数据库已有数据，修改表结构可能导致数据丢失
2. **使用@TableField注解**：这是MyBatis-Plus提供的标准方式，不影响代码可读性
3. **保持实体类字段名语义化**：实体类字段名应保持业务语义，通过注解映射到数据库字段

## 修复时间

2026-06-02
