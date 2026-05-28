# 数据资产安全及分类分级管理系统

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Vue](https://img.shields.io/badge/Vue-3.x-brightgreen.svg)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)

一款企业级数据资产安全管理平台，实现数据资产的分类分级、安全管控、合规审计等核心功能。

## 📋 项目简介

本系统基于国家数据安全法律法规和行业标准，为企业提供完整的数据资产安全管理解决方案：

- **数据分类分级**：支持多维度数据分类和分级标准制定
- **资产清单管理**：全面管理企业数据资产，支持导入导出
- **安全合规审计**：完整的操作审计日志和合规报告
- **权限精细管控**：基于RBAC的细粒度权限管理
- **多因素认证**：支持MFA多因素认证，提升安全等级

## ✨ 核心功能

### 数据治理
- 📊 **分类标准管理** - 制定和发布数据分类标准
- 📈 **分级标准管理** - 制定和发布数据分级标准
- 🏷️ **数据分类标注** - 对数据资产进行分类标注
- 🔐 **数据分级标注** - 对数据资产进行分级标注
- 📝 **字段级标注** - 支持字段级别的分类分级

### 资产管理
- 📦 **资产清单** - 数据资产的增删改查
- 📥 **批量导入** - 支持Excel批量导入资产
- 📤 **数据导出** - 支持多种格式导出
- 🔍 **智能检索** - 多条件组合查询
- 📊 **统计分析** - 资产统计和趋势分析

### 安全管控
- 👥 **用户管理** - 用户账号和权限管理
- 🔑 **角色管理** - 角色和权限配置
- 🏢 **部门管理** - 组织架构管理
- 🛡️ **MFA认证** - 多因素身份认证
- 📝 **审计日志** - 完整的操作审计

### 报告中心
- 📄 **分类报告** - 数据分类统计报告
- 📊 **分级报告** - 数据分级统计报告
- 🔍 **审计报告** - 安全审计报告
- 📈 **合规报告** - 合规性分析报告

## 🚀 快速开始

### 方式一：快速启动（推荐新手）

使用快速启动脚本，无需配置环境：

```bash
# 启动系统
./start-quick.sh

# 停止系统
./stop-quick.sh
```

启动后访问：
- 前端界面: http://localhost:5173
- 后端API: http://localhost:8080

**默认登录信息**：
- 用户名: `admin`
- 密码: `admin123`

> 💡 **提示**：快速启动模式使用 Mock 后端，数据存储在内存中，重启后重置。适合快速体验和演示。

### 方式二：完整部署（生产环境）

#### 环境要求

- Node.js >= 16.x
- Java >= 17
- MySQL >= 8.0
- Redis >= 6.0
- Maven >= 3.6

### 安装部署

#### 1. 克隆项目

```bash
git clone https://github.com/wuxiucheng/data-asset-security-system.git
cd data-asset-security-system
```

#### 2. 配置环境

参考 [部署配置说明](data-asset-security-system/DEPLOYMENT_CONFIG.md) 配置数据库、Redis等环境。

#### 3. 启动服务

**开发环境**：
```bash
# 启动后端
cd data-asset-security-system/backend
mvn spring-boot:run

# 启动前端（新终端）
cd data-asset-security-system/frontend
npm install
npm run dev
```

**生产环境**：
```bash
# 设置环境变量
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=your-db-host
export DB_PASSWORD=your-password
export JWT_SECRET=your-secret

# 启动服务
java -jar backend.jar
```

#### 4. 访问系统

- 前端界面: http://localhost:5173
- 后端API: http://localhost:8080/api
- API文档: http://localhost:8080/api/doc.html

**默认账号**：
- 用户名: `admin`
- 密码: `admin123`

## 📖 文档

- [快速启动指南](data-asset-security-system/快速启动指南.md) - 快速上手
- [系统操作手册](data-asset-security-system/系统操作手册.md) - 详细操作说明
- [部署配置说明](data-asset-security-system/DEPLOYMENT_CONFIG.md) - 部署配置
- [部署指南](docs/DEPLOYMENT_GUIDE.md) - 详细部署步骤

## 🏗️ 项目结构

```
data-asset-security-system/
├── frontend/                    # Vue3前端项目
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── components/         # 公共组件
│   │   ├── api/                # API接口
│   │   ├── store/              # 状态管理
│   │   └── router/             # 路由配置
│   └── package.json
│
├── backend/                     # Spring Boot后端项目
│   ├── src/main/java/
│   │   └── com/dataasset/security/
│   │       ├── controller/     # 控制器
│   │       ├── service/        # 业务逻辑
│   │       ├── entity/         # 实体类
│   │       └── mapper/         # 数据访问
│   └── pom.xml
│
└── docs/                        # 项目文档
    ├── development/            # 开发文档
    └── README.md               # 文档目录
```

## 🛠️ 技术栈

### 前端技术
- **框架**: Vue 3 + TypeScript
- **UI库**: Element Plus
- **构建**: Vite
- **状态**: Pinia
- **路由**: Vue Router
- **HTTP**: Axios

### 后端技术
- **框架**: Spring Boot 3.x
- **ORM**: MyBatis-Plus
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **消息**: RabbitMQ
- **认证**: JWT + Spring Security
- **文档**: Knife4j (Swagger)

## 📊 系统截图

### 登录页面
支持多因素认证，提升系统安全性。

### 数据分类分级
可视化的分类分级标准管理，支持树形结构展示。

### 资产清单
完整的数据资产管理，支持批量导入导出。

### 统计分析
多维度数据统计和趋势分析图表。

## 🔒 安全特性

- ✅ **JWT认证** - 基于JWT的无状态认证
- ✅ **RBAC权限** - 基于角色的访问控制
- ✅ **MFA认证** - 多因素身份认证
- ✅ **操作审计** - 完整的操作日志记录
- ✅ **SQL注入防护** - MyBatis参数化查询
- ✅ **XSS防护** - 前端输入过滤
- ✅ **CSRF防护** - Token验证机制

## ❓ 常见问题

### Q: 快速启动模式的数据会保存吗？
A: 不会。快速启动使用 Mock 后端，数据存储在内存中，重启后重置。生产环境请使用完整部署。

### Q: MFA验证码是什么？
A: 快速启动模式下，任意6位数字都可以通过验证（例如：123456）。生产环境需配置真实的MFA服务。

### Q: 如何查看日志？
A:
```bash
# 前端日志
tail -f data-asset-security-system/frontend/logs/frontend.log

# 后端日志
tail -f data-asset-security-system/backend/logs/backend.log
```

### Q: 端口被占用怎么办？
A: 运行 `./stop-quick.sh` 停止服务，或手动杀掉占用端口的进程。

### Q: 支持哪些数据库？
A: 目前支持 MySQL 8.0+，理论上兼容 MySQL 5.7+。

## 📝 开发计划

- [ ] 数据脱敏功能
- [ ] 数据血缘分析
- [ ] 数据质量检测
- [ ] 风险评估模型
- [ ] 合规性自动检查
- [ ] 数据生命周期管理

## 📅 更新日志

### 2026-05-28
- ✅ 完善项目文档结构
- ✅ 优化配置管理，支持多环境部署
- ✅ 修复敏感信息泄露问题
- ✅ 排除临时和备份文件

### 2026-04-27
- ✅ 完善Mock后端，新增18个API接口
- ✅ 修复前端404错误
- ✅ 清理不相干的启停脚本
- ✅ 创建完整的使用文档

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 📞 联系方式

- 项目地址: https://github.com/wuxiucheng/data-asset-security-system
- 问题反馈: [Issues](https://github.com/wuxiucheng/data-asset-security-system/issues)

## 🙏 致谢

感谢以下开源项目：
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)

---

⭐ 如果这个项目对你有帮助，请给一个 Star 支持一下！
