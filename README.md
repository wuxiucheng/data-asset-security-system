# 数据资产安全及分类分级管理系统

## 项目简介

数据资产安全及分类分级管理系统是一个基于 Spring Boot 3.x + Vue 3.x 的企业级数据安全治理平台，旨在帮助企业建立完善的数据资产分类分级管理体系，实现数据资产的精细化管理。

## 最新更新 (2026-06-02)

### ✅ v1.0.3 - 统计数据修复

**修复内容：**
1. **资产统计修复** - 统计数据现在正确显示680条资产（之前只显示8条）
2. **趋势分析修复** - 修复数据格式不匹配问题，趋势分析页面正常显示
3. **JVM配置优化** - 修复部署时内存配置过小导致服务崩溃的问题

**统计数据：**
- 总资产数: 680条 ✅
- 按部门分布: 技术部573条、财务部100条等 ✅
- 按分类分布: 客户数据、产品数据、财务数据等 ✅
- 按分级分布: 一级、二级、三级、四级 ✅

### ✅ v1.0.2 - 实体类字段映射修复

已修复所有实体类与数据库表字段映射问题，所有API测试通过：
- LineageRelation - 数据血缘关系
- LcPolicy - 生命周期策略
- LcStatus - 生命周期状态
- DataStandard - 数据标准
- ComplianceClause - 合规条款
- GovernanceKpi - 治理KPI
- ImpactAnalysis - 影响分析

详细说明请查看：[FIELD_MAPPING_FIX_SUMMARY.md](./FIELD_MAPPING_FIX_SUMMARY.md)

## 技术栈

### 后端技术栈
- **核心框架**: Spring Boot 3.2.3
- **ORM框架**: MyBatis-Plus 3.5.5
- **安全框架**: Spring Security 6.x + JWT
- **工作流引擎**: Flowable 7.0.1
- **缓存**: Redis 7.x
- **消息队列**: RabbitMQ 3.x
- **数据库**: MySQL 8.0+
- **API文档**: Knife4j 4.4.0

### 前端技术栈
- **核心框架**: Vue 3.4.x + TypeScript 5.x
- **UI组件库**: Element Plus 2.6.x
- **状态管理**: Pinia 2.x
- **路由管理**: Vue Router 4.x
- **HTTP客户端**: Axios 1.x
- **图表库**: ECharts 5.x
- **构建工具**: Vite 5.x

### Mock后端（开发调试用）
- **运行时**: Node.js 18+
- **框架**: Express 4.x
- **数据存储**: 内存（重启丢失）
- **用途**: 前端开发、API测试、功能演示

## 项目结构

```
data-asset-security-system/
├── backend/                 # Spring Boot后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/dataasset/security/
│   │   │   │   ├── config/           # 配置类
│   │   │   │   ├── controller/       # 控制器
│   │   │   │   ├── service/          # 业务逻辑
│   │   │   │   ├── mapper/           # 数据访问
│   │   │   │   ├── entity/           # 实体类（已修复字段映射）
│   │   │   │   ├── common/           # 公共模块（BaseEntity、统一响应等）
│   │   │   │   ├── dto/              # 数据传输对象
│   │   │   │   ├── vo/               # 视图对象
│   │   │   │   ├── security/         # 安全模块（JWT、Spring Security）
│   │   │   │   └── utils/            # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/
│   │   │           └── init.sql      # 数据库初始化脚本（已更新）
│   │   └── test/
│   ├── ENTITY_FIELD_MAPPING_FIX.md   # 字段映射修复说明
│   └── pom.xml
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/             # API接口定义
│   │   ├── views/           # 页面组件
│   │   ├── stores/          # 状态管理
│   │   ├── router/          # 路由配置
│   │   └── utils/           # 工具函数（含request.ts）
│   └── package.json
├── manage.sh                # 统一管理脚本（推荐使用）
├── FIELD_MAPPING_FIX_SUMMARY.md  # 字段映射修复总结
└── README.md
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- Maven 3.8+
- MySQL 8.0+（Spring Boot后端需要）
- Redis 6.0+（可选）
- RabbitMQ 3.8+（可选）

### 使用统一管理脚本（推荐）

```bash
# 启动服务（首次启动或修改实体类后）
./manage.sh start real build

# 测试所有API
./manage.sh test

# 查看服务状态
./manage.sh status

# 重启服务（如需重新编译）
./manage.sh restart real build

# 查看日志
./manage.sh logs backend

# 停止服务
./manage.sh stop
```

### 一键启动（传统方式）

```bash
# 启动Spring Boot后端 + 前端
./start-all.sh

# 或启动Mock后端 + 前端
./start-all.sh mock
```

### 分步启动

#### 1. 启动Spring Boot后端

```bash
# 确保MySQL已启动
mysql.server start

# 启动后端
./start-real.sh
```

后端服务将在 http://localhost:8080/api 启动
API文档地址：http://localhost:8080/api/doc.html

#### 2. 启动Mock后端（开发调试用）

```bash
./start-mock.sh
```

#### 3. 启动前端

```bash
./start-frontend.sh
# 或手动启动
cd frontend && npm install && npm run dev
```

前端服务将在 http://localhost:5173 启动

### 查看服务状态

```bash
./status.sh
```

## 默认账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 系统管理员、数据管理员 | 超级管理员 |
| user1 | 123456 | 普通用户 | 张三 |
| user2 | 123456 | 数据责任人、普通用户 | 李四 |
| user3 | 123456 | 普通用户 | 王五（已禁用） |

## 核心功能

### 1. 用户权限管理 ✅
- 用户管理（CRUD、状态管理、密码管理）
- 角色管理（CRUD、权限分配、三权分立）
- 权限管理（树形结构、菜单/按钮/API权限）
- MFA多因素认证

### 2. 责任体系管理 ✅
- 责任部门管理（树形结构）
- 责任人管理
- 组织架构同步

### 3. 分类分级管理 ✅
- 分类标准管理（版本管理、发布控制）
- 分类管理（树形结构、多级分类）
- 分级标准管理（L1-L4分级体系）
- 分级管理（安全要求、颜色标识）

### 4. 数据资产管理 ✅
- 数据资产登记与查询
- 数据字段管理
- 字段级分类分级
- 批量导入导出（CSV/Excel）
- 资产发现（数据库连接扫描，支持MySQL/PostgreSQL）
- 数据源配置管理
- 数据条数刷新

#### 4.1 分类分级辅助 ✅ (新增)
智能分类分级辅助系统，自动识别敏感字段并建议分级：

**核心功能**：
- **规则管理**：配置识别规则，支持字段名匹配、正则表达式、样本匹配
- **执行任务**：创建扫描任务，支持手动执行和定时调度
- **结果审核**：人工审核分级建议，批准后自动应用到字段
- **统计分析**：规则效果分析、任务执行统计、审核通过率

**预置规则**：
| 规则名称 | 匹配模式 | 建议分级 |
|---------|---------|---------|
| 手机号字段识别 | *phone*, *mobile* | L3 |
| 身份证号字段识别 | *idcard*, *身份证* | L4 |
| 姓名字段识别 | *name*, *姓名* | L2 |
| 邮箱字段识别 | *email*, *邮箱* | L2 |
| 地址字段识别 | *address*, *地址* | L3 |
| 银行卡号字段识别 | *bankcard*, *银行卡* | L4 |

**技术特性**：
- 异步执行引擎（@Async）
- 批量处理优化（每100条保存一次）
- 定时调度支持（Cron表达式）
- 规则匹配引擎（通配符+正则）
- 审核通过率统计

**操作流程**：
1. 配置规则 → 分类分级辅助
2. 创建任务 → 执行任务
3. 执行扫描 → 自动匹配
4. 审核结果 → 结果审核
5. 应用分级 → 资产字段

**API接口**：
- 规则管理：`/api/classificationAssistRule/*`
- 任务管理：`/api/classificationAssistTask/*`
- 结果审核：`/api/classificationAssistResult/*`
- 统计分析：`/api/classificationAssistStatistics/*`

### 5. 数据治理模块
#### 5.1 数据质量探查 ✅
- 质量规则定义与管理
- 质量探查任务创建与执行
- 质量探查结果展示
- 资产质量报告生成
- 质量告警（quality_alert表已建，功能待实现）

#### 5.2 敏感数据识别 ✅
- 敏感识别规则管理（字段名匹配、正则表达式、样本匹配）
- 敏感识别任务执行
- 识别结果展示与确认
- 内置规则初始化

#### 5.3 数据脱敏 ✅
- 脱敏策略管理（掩码、替换、哈希、加密、截断、打乱等算法）
- 脱敏算法应用
- 默认策略初始化
- 脱敏白名单（mask_whitelist表已建，功能待实现）

#### 5.4 数据血缘与影响分析 ⚠️ (数据库表已创建,代码待实现)
- 血缘关系（lineage_relation表）
- 影响分析（impact_analysis表）
- 元数据版本（metadata_version表）

#### 5.5 数据生命周期管理 ⚠️ (数据库表已创建,代码待实现)
- 生命周期策略（lc_policy表）
- 生命周期状态（lc_status表）

#### 5.6 数据标准与合规 ⚠️ (数据库表已创建,代码待实现)
- 数据标准（data_standard表）
- 合规条款（compliance_clause表）
- 合规评估结果（compliance_eval_result表）
- 标准符合性结果（standard_compliance_result表）
- 治理KPI（governance_kpi表）

### 6. 审批流程管理 ✅
- 审批流程定义（Flowable工作流）
- 审批流程实例管理
- 审批任务处理（待办、已办）
- 流程启动与审批

### 7. 统计分析 ✅
- 资产统计概览
- 趋势分析
- 报表导出

### 8. 审计日志 ✅
- 操作审计日志记录
- 日志查询与统计
- 日志归档与清理

## 双后端架构

项目支持两种后端模式：

| 特性 | Mock后端 | Spring Boot后端 |
|------|---------|----------------|
| 技术栈 | Node.js + Express | Spring Boot + MySQL |
| 数据存储 | 内存 | MySQL持久化 |
| 启动时间 | 1-2秒 | 8-10秒 |
| 适用场景 | 前端开发、API测试 | 生产环境、集成测试 |
| 数据持久化 | 否（重启丢失） | 是 |

### API兼容性

前端自动兼容两种后端：
- 响应码：Mock返回`code: 0`，Spring Boot返回`code: 200`，前端均兼容
- 分页响应：Spring Boot返回`records`，前端自动转换为`list`
- API路径：已统一为RESTful风格

### 数据同步

从Mock同步数据到MySQL：

```bash
# 1. 从Mock提取数据
node backend/extract-all-mock-data.js > /tmp/all-mock-data.json

# 2. 生成同步SQL
python3 backend/generate-full-sync-sql.py

# 3. 执行同步
mysql -uroot -p1q2w3e4r < backend/sync-mock-data.sql
```

## 生产环境部署

### 部署配置

生产环境部署配置文件位于 `deploy/.env.deploy`，需要根据实际情况修改：

```bash
# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=data_asset_security
DB_USERNAME=root
DB_PASSWORD=your_password

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# RabbitMQ配置
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# 服务配置
SERVER_PORT=8082

# JVM内存配置（重要！）
# 建议最小256m，最大512m（根据服务器内存调整）
# 如果服务器内存充足，可以设置为 -Xms512m -Xmx1024m
JVM_OPTS="-Xms256m -Xmx512m"
```

### JVM内存配置说明

**⚠️ 重要提示：** JVM内存配置过小会导致服务启动失败或运行时崩溃！

**推荐配置：**
- **最小配置**（1GB内存服务器）：`-Xms256m -Xmx512m`
- **推荐配置**（2GB内存服务器）：`-Xms512m -Xmx1024m`
- **高配配置**（4GB+内存服务器）：`-Xms1024m -Xmx2048m`

**常见问题：**
- ❌ `-Xms64m -Xmx128m` - 太小，会导致内存不足（OOM）
- ✅ `-Xms256m -Xmx512m` - 推荐，适合大多数生产环境

### 部署命令

```bash
# 完整部署（构建+上传+重启）
./deploy/backend.sh full

# 快速部署（仅上传+重启，不构建）
./deploy/backend.sh quick

# 查看服务状态
./deploy/backend.sh status

# 查看实时日志
./deploy/backend.sh logs

# 重启服务
./deploy/backend.sh restart
```

## 数据库

### 表结构

系统共40+张业务表（不含Flowable工作流表），核心表包括：

#### 基础管理表（已实现）
| 表名 | 说明 | 状态 |
|------|------|------|
| sys_user | 系统用户 | ✅ |
| sys_role | 系统角色 | ✅ |
| sys_permission | 系统权限 | ✅ |
| sys_user_role | 用户角色关联 | ✅ |
| sys_role_permission | 角色权限关联 | ✅ |
| department | 责任部门 | ✅ |
| owner | 责任人 | ✅ |
| audit_log | 审计日志 | ✅ |

#### 分类分级表（已实现）
| 表名 | 说明 | 状态 |
|------|------|------|
| classification_standard | 分类标准 | ✅ |
| data_classification | 数据分类 | ✅ |
| grading_standard | 分级标准 | ✅ |
| data_grading | 数据分级 | ✅ |

#### 数据资产表（已实现）
| 表名 | 说明 | 状态 |
|------|------|------|
| data_source_config | 数据源配置 | ✅ |
| data_asset | 数据资产 | ✅ |
| data_field | 数据字段 | ✅ |
| classification_assist_rule | 分类分级辅助规则 | ✅ |
| classification_assist_task | 分类分级辅助任务 | ✅ |
| classification_assist_result | 分类分级辅助结果 | ✅ |

#### 数据治理表
| 表名 | 说明 | 状态 |
|------|------|------|
| quality_rule | 质量规则 | ✅ |
| quality_probe_task | 质量探查任务 | ✅ |
| quality_probe_result | 质量探查结果 | ✅ |
| quality_alert | 质量告警 | ⚠️ 表已建，功能待实现 |
| sensitive_ident_rule | 敏感识别规则 | ✅ |
| sensitive_ident_result | 敏感识别结果 | ✅ |
| mask_strategy | 脱敏策略 | ✅ |
| mask_whitelist | 脱敏白名单 | ⚠️ 表已建，功能待实现 |
| lineage_relation | 血缘关系 | ⚠️ 表已建，功能待实现 |
| impact_analysis | 影响分析 | ⚠️ 表已建，功能待实现 |
| metadata_version | 元数据版本 | ⚠️ 表已建，功能待实现 |
| lc_policy | 生命周期策略 | ⚠️ 表已建，功能待实现 |
| lc_status | 生命周期状态 | ⚠️ 表已建，功能待实现 |
| data_standard | 数据标准 | ⚠️ 表已建，功能待实现 |
| compliance_clause | 合规条款 | ⚠️ 表已建，功能待实现 |
| compliance_eval_result | 合规评估结果 | ⚠️ 表已建，功能待实现 |
| standard_compliance_result | 标准符合性结果 | ⚠️ 表已建，功能待实现 |
| governance_kpi | 治理KPI | ⚠️ 表已建，功能待实现 |

#### 审批流程表
| 表名 | 说明 | 状态 |
|------|------|------|
| approval_process_definition | 审批流程定义 | ✅ |
| approval_process_instance | 审批流程实例 | ✅ |
| approval_task | 审批任务 | ✅ |

#### 认证安全表（已实现）
| 表名 | 说明 | 状态 |
|------|------|------|
| auth_session | 会话管理 | ✅ |
| auth_token_blacklist | Token黑名单 | ✅ |
| auth_mfa_config | MFA配置 | ✅ |
| auth_account_lock | 账户锁定 | ✅ |
| auth_login_log | 登录日志 | ✅ |
| auth_audit_log | 认证审计 | ✅ |
| auth_rate_limit_log | 限流日志 | ✅ |
| auth_security_event | 安全事件 | ✅ |
| auth_password_history | 密码历史 | ✅ |
| auth_permission_log | 权限日志 | ✅ |

### BaseEntity公共字段

所有业务实体继承`BaseEntity`，包含以下公共字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| creator_id | BIGINT | 创建人ID |
| created_time | DATETIME | 创建时间 |
| updater_id | BIGINT | 更新人ID |
| updated_time | DATETIME | 更新时间 |
| deleted | INT | 逻辑删除标记（0-未删除，1-已删除） |

## 开发规范

### 后端开发规范
- 遵循阿里巴巴Java开发手册
- 实体类继承BaseEntity，使用MyBatis-Plus逻辑删除
- Controller路径使用小写单数形式（如`/department`、`/asset`）
- 分页查询使用POST + `/page`路径
- 统一响应封装：`Result.success(data)`

### 前端开发规范
- 遵循Vue.js风格指南
- API定义集中在`src/api/index.ts`
- 列表查询使用POST分页接口
- 响应拦截器自动处理`records → list`转换

### Git提交规范

```
<type>(<scope>): <subject>
```

类型：`feat` | `fix` | `docs` | `style` | `refactor` | `test` | `chore`

## 许可证

本项目采用 MIT 许可证。
