# 数据资产安全及分类分级管理系统

## 项目简介

数据资产安全及分类分级管理系统是一个基于Spring Boot 3.x + Vue 3.x的企业级数据安全治理平台，旨在帮助企业建立完善的数据资产分类分级管理体系，实现数据资产的精细化管理。

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

## 项目结构

```
data-asset-security-system/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/dataasset/security/
│   │   │   │       ├── config/           # 配置类
│   │   │   │       ├── controller/       # 控制器
│   │   │   │       ├── service/          # 业务逻辑
│   │   │   │       ├── mapper/           # 数据访问
│   │   │   │       ├── entity/           # 实体类
│   │   │   │       ├── dto/              # 数据传输对象
│   │   │   │       ├── vo/               # 视图对象
│   │   │   │       ├── common/           # 公共模块
│   │   │   │       └── utils/            # 工具类
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/             # API接口
│   │   ├── assets/          # 静态资源
│   │   ├── components/      # 组件
│   │   ├── composables/     # 组合式函数
│   │   ├── router/          # 路由配置
│   │   ├── stores/          # 状态管理
│   │   ├── types/           # 类型定义
│   │   ├── utils/           # 工具函数
│   │   └── views/           # 页面组件
│   └── package.json
├── docker-compose.yml       # Docker编排配置
└── README.md
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- Maven 3.8+
- Docker & Docker Compose（可选）

### 1. 启动基础服务

使用Docker Compose启动MySQL、Redis、Elasticsearch、RabbitMQ：

```bash
docker-compose up -d
```

### 2. 启动后端服务

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

API文档地址：http://localhost:8080/api/doc.html

### 3. 启动前端服务

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:5173 启动

## 核心功能

### 1. 用户权限管理
- 用户管理
- 角色管理
- 权限管理
- 三权分立控制

### 2. 责任体系管理
- 责任部门管理
- 责任人管理
- 组织架构同步

### 3. 分类分级管理
- 分类标准管理
- 分类管理
- 分级标准管理
- 分类分级规则配置
- 自动分类分级

### 4. 数据资产管理
- 数据资产登记
- 数据资产查询
- 数据字段管理
- 字段级分类分级

### 5. 审批流程管理
- 审批流程定义
- 审批流程实例
- 审批操作
- 分类分级审批

### 6. 统计分析
- 资产统计概览
- 趋势分析
- 报表导出

## 开发规范

### 后端开发规范
- 遵循阿里巴巴Java开发手册
- 使用CheckStyle进行代码检查
- 使用SonarQube进行代码质量分析
- 代码注释使用标准JavaDoc格式

### 前端开发规范
- 遵循Vue.js风格指南
- 使用ESLint进行代码检查
- 使用Prettier进行代码格式化
- 组件命名使用大驼峰（PascalCase）

### Git提交规范

```
<type>(<scope>): <subject>

<body>

<footer>
```

类型（type）：
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具相关

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题，请联系开发团队。
