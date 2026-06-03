# Flowable 配置说明

## 问题背景

在部署过程中遇到 Flowable 工作流引擎的 Liquibase 数据库迁移问题：
- 错误：`Table 'FLW_EVENT_DEPLOYMENT' already exists`
- 原因：Liquibase 变更记录表存在但记录为空，导致尝试重新创建已存在的表

## 解决方案

### 1. 数据库修复（已完成）

为 Flowable 的各个引擎插入了正确的 Liquibase 变更记录：

```sql
-- Event Registry
INSERT INTO FLW_EV_DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, EXECTYPE, MD5SUM, DESCRIPTION, COMMENTS, LIQUIBASE) VALUES
('1', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', NOW(), 1, 'EXECUTED', '8:1b0c48c9cf7945be799d868a2626d687', 'createTable tableName=FLW_EVENT_DEPLOYMENT', '', '4.9.0'),
('2', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', NOW(), 2, 'EXECUTED', '8:0ea825feb8e470558f0b5754352b9cda', 'createTable tableName=FLW_EVENT_RESOURCE', '', '4.9.0'),
('3', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', NOW(), 3, 'EXECUTED', '8:3c2bb293350b5cbe6504331980c9dcee', 'createTable tableName=FLW_EVENT_DEFINITION', '', '4.9.0');
```

### 2. 应用配置（已完成）

在 `application.yml` 中添加：

```yaml
flowable:
  database-schema-update: true
  async-executor-activate: true
  deployment-mode: single-engine
  liquibase-enabled: false

spring.liquibase.enabled: false
```

### 3. 部署脚本统一（已完成）

所有部署脚本已统一添加以下启动参数：

```bash
--flowable.database-schema-update=false
--flowable.async-executor-activate=false
```

## 部署脚本说明

### 可用的部署脚本

1. **backend.sh** - 智能后端部署脚本（推荐）
   ```bash
   ./backend.sh full      # 完整部署（构建+上传+重启）
   ./backend.sh quick     # 快速部署（仅上传+重启）
   ./backend.sh restart   # 重启服务
   ./backend.sh status    # 查看状态
   ./backend.sh logs      # 查看日志
   ```

2. **deploy-backend.sh** - 简化版后端部署脚本
   ```bash
   ./deploy-backend.sh              # 完整部署
   ./deploy-backend.sh --build-only # 仅构建
   ```

3. **frontend.sh** - 前端部署脚本
   ```bash
   ./frontend.sh full    # 完整部署
   ./frontend.sh quick   # 快速部署
   ./frontend.sh status  # 查看状态
   ```

4. **deploy-all.sh** - 一键部署全栈
   ```bash
   ./deploy-all.sh  # 部署前后端
   ```

### 配置文件

- `.env.deploy` - 部署环境配置（数据库、Redis、RabbitMQ等）
- `.env.deploy.example` - 配置模板

## Flowable 配置参数说明

| 参数 | 值 | 说明 |
|------|-----|------|
| `flowable.database-schema-update` | false | 禁用自动schema更新，使用现有表 |
| `flowable.async-executor-activate` | false | 禁用异步执行器 |
| `flowable.liquibase-enabled` | false | 禁用Flowable的Liquibase |
| `spring.liquibase.enabled` | false | 禁用Spring的Liquibase |

## 验证部署

部署成功后，可以通过以下方式验证：

1. 检查服务状态
   ```bash
   ./backend.sh status
   ```

2. 检查端口监听
   ```bash
   ssh root@47.94.52.217 "netstat -tlnp | grep 8082"
   ```

3. 查看启动日志
   ```bash
   ./backend.sh logs
   ```

4. 访问API
   ```bash
   curl http://47.94.52.217:8082/api/actuator/health
   ```

## 注意事项

1. **首次部署**：数据库中已有Flowable表，不需要重新创建
2. **后续部署**：使用相同的配置，不会触发schema更新
3. **数据库迁移**：如需更新Flowable表结构，需要手动执行SQL或临时启用schema更新
4. **版本升级**：升级Flowable版本时，需要检查是否需要数据库迁移

## 相关文件

- 应用配置：`backend/src/main/resources/application.yml`
- 环境配置：`deploy/.env.deploy`
- 后端部署：`deploy/backend.sh`, `deploy/deploy-backend.sh`
- 前端部署：`deploy/frontend.sh`, `deploy/deploy-frontend.sh`
- 全栈部署：`deploy/deploy-all.sh`
