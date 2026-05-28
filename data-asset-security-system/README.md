# 数据资产安全及分类分级管理系统

## 项目简介

数据资产安全及分类分级管理系统是一个基于 Spring Boot 3.x + Vue 3.x 的企业级数据安全治理平台，旨在帮助企业建立完善的数据资产分类分级管理体系，实现数据资产的精细化管理。

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
│   │   │   │   ├── entity/           # 实体类
│   │   │   │   ├── common/           # 公共模块（BaseEntity、统一响应等）
│   │   │   │   ├── dto/              # 数据传输对象
│   │   │   │   ├── vo/               # 视图对象
│   │   │   │   ├── security/         # 安全模块（JWT、Spring Security）
│   │   │   │   └── utils/            # 工具类
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   ├── extract-all-mock-data.js  # 从Mock提取数据脚本
│   ├── generate-full-sync-sql.py # 生成同步SQL脚本
│   ├── sync-mock-data.sql        # 数据同步SQL
│   ├── create-tables-complete.sql # 完整建表SQL
│   └── pom.xml
├── simple-backend/          # Mock后端（Node.js + Express）
│   ├── server.js            # Mock服务器主文件
│   ├── package.json
│   └── README.md
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/             # API接口定义
│   │   ├── views/           # 页面组件
│   │   ├── stores/          # 状态管理
│   │   ├── router/          # 路由配置
│   │   └── utils/           # 工具函数（含request.ts）
│   └── package.json
├── start-mock.sh            # 启动Mock后端
├── start-real.sh            # 启动Spring Boot后端
├── start-frontend.sh        # 启动前端
├── start-all.sh             # 一键启动所有服务
├── stop-backend.sh          # 停止后端
├── stop-frontend.sh         # 停止前端
├── stop-all.sh              # 一键停止所有服务
├── restart.sh               # 重启后端
├── status.sh                # 查看服务状态
├── BACKEND_SYNC_GUIDE.md    # 后端同步指南
├── SERVICE_MANAGEMENT.md    # 服务管理指南
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

### 一键启动（推荐）

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

### 5. 数据治理模块 ⚠️ (数据库表已创建,代码待实现)
#### 5.1 数据质量探查
- 质量规则定义（quality_rule表）
- 质量探查任务（quality_probe_task表）
- 质量探查结果（quality_probe_result表）
- 质量告警（quality_alert表）

#### 5.2 敏感数据识别
- 敏感识别规则（sensitive_ident_rule表）
- 敏感识别结果（sensitive_ident_result表）
- 支持字段名匹配、正则表达式、样本匹配

#### 5.3 数据脱敏
- 脱敏策略（mask_strategy表）
- 脱敏白名单（mask_whitelist表）
- 支持多种脱敏算法：掩码、替换、哈希、加密、截断、打乱

#### 5.4 数据血缘与影响分析
- 血缘关系（lineage_relation表）
- 影响分析（impact_analysis表）
- 元数据版本（metadata_version表）

#### 5.5 数据生命周期管理
- 生命周期策略（lc_policy表）
- 生命周期状态（lc_status表）

#### 5.6 数据标准与合规
- 数据标准（data_standard表）
- 合规条款（compliance_clause表）
- 合规评估结果（compliance_eval_result表）
- 标准符合性结果（standard_compliance_result表）
- 治理KPI（governance_kpi表）

### 6. 审批流程管理 ⚠️ (数据库表已创建,代码待实现)
- 审批流程定义（Flowable工作流）
- 审批流程实例
- 审批任务管理
- 分类分级审批

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

#### 数据治理表（数据库已创建，代码待实现）
| 表名 | 说明 | 状态 |
|------|------|------|
| quality_rule | 质量规则 | ⚠️ 表已建 |
| quality_probe_task | 质量探查任务 | ⚠️ 表已建 |
| quality_probe_result | 质量探查结果 | ⚠️ 表已建 |
| quality_alert | 质量告警 | ⚠️ 表已建 |
| sensitive_ident_rule | 敏感识别规则 | ⚠️ 表已建 |
| sensitive_ident_result | 敏感识别结果 | ⚠️ 表已建 |
| mask_strategy | 脱敏策略 | ⚠️ 表已建 |
| mask_whitelist | 脱敏白名单 | ⚠️ 表已建 |
| lineage_relation | 血缘关系 | ⚠️ 表已建 |
| impact_analysis | 影响分析 | ⚠️ 表已建 |
| metadata_version | 元数据版本 | ⚠️ 表已建 |
| lc_policy | 生命周期策略 | ⚠️ 表已建 |
| lc_status | 生命周期状态 | ⚠️ 表已建 |
| data_standard | 数据标准 | ⚠️ 表已建 |
| compliance_clause | 合规条款 | ⚠️ 表已建 |
| compliance_eval_result | 合规评估结果 | ⚠️ 表已建 |
| standard_compliance_result | 标准符合性结果 | ⚠️ 表已建 |
| governance_kpi | 治理KPI | ⚠️ 表已建 |

#### 审批流程表（数据库已创建，代码待实现）
| 表名 | 说明 | 状态 |
|------|------|------|
| approval_process_definition | 审批流程定义 | ⚠️ 表已建 |
| approval_process_instance | 审批流程实例 | ⚠️ 表已建 |
| approval_task | 审批任务 | ⚠️ 表已建 |

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
