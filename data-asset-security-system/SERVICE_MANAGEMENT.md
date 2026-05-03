# 数据资产安全系统 - 服务管理指南

## 快速开始

### 一键启动（推荐）

```bash
# 启动Spring Boot后端 + 前端
./start-all.sh

# 或启动Mock后端 + 前端
./start-all.sh mock
```

### 查看服务状态
```bash
./status.sh
```

### 启动后端

#### 方式1：Spring Boot后端（生产环境，推荐）
```bash
./start-real.sh
```
- **技术栈**: Spring Boot 3.2.3 + MySQL 8.0
- **数据存储**: MySQL数据库（持久化）
- **适用场景**: 生产环境、集成测试、数据持久化
- **启动时间**: 8-10秒
- **前提**: MySQL已启动

#### 方式2：Mock后端（开发调试）
```bash
./start-mock.sh
```
- **技术栈**: Node.js + Express
- **数据存储**: 内存（重启丢失）
- **适用场景**: 前端开发、API测试、功能演示
- **启动时间**: 1-2秒
- **前提**: Node.js已安装

### 启动前端
```bash
./start-frontend.sh
# 或手动
cd frontend && npm run dev
```

### 停止服务
```bash
./stop-all.sh        # 停止所有
./stop-backend.sh    # 停止后端
./stop-frontend.sh   # 停止前端
```

### 重启服务
```bash
./restart.sh         # 重启Spring Boot后端
./restart.sh real    # 同上
./restart.sh mock    # 重启Mock后端
```

## 服务架构

### Spring Boot后端架构（推荐）
```
前端 (Vue3, :5173)
    ↓
Spring Boot后端 (:8080/api)
    ↓
MySQL数据库 (持久化)
    ↓
Redis缓存 (可选, :6379)
    ↓
RabbitMQ消息队列 (可选, :5672)
```

**特点：**
- 数据持久化，重启不丢失
- 完整的认证授权（JWT + RBAC）
- 工作流引擎（Flowable）
- 审计日志
- 生产环境可用

### Mock后端架构
```
前端 (Vue3, :5173)
    ↓
Mock后端 (Node.js + Express, :8080/api)
    ↓
内存存储 (mockUsers, mockAssets, ...)
```

**特点：**
- 启动快速，无需数据库
- 适合前端开发和API测试
- 数据不持久化，重启丢失

## 环境要求

### Spring Boot后端
- Java 17+
- Maven 3.6+
- MySQL 8.0+（必需）
- Redis 6.0+（可选）
- RabbitMQ 3.8+（可选）

### Mock后端
- Node.js 16+
- npm 8+

### 前端
- Node.js 18+
- npm 9+

## 数据库配置

### MySQL配置
```yaml
# backend/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/data_asset_security
    username: root
    password: 1q2w3e4r
```

### 初始化数据库

```bash
# 建表
mysql -uroot -p1q2w3e4r < backend/create-tables-complete.sql

# 同步Mock数据
mysql -uroot -p1q2w3e4r < backend/sync-mock-data.sql
```

## 默认账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 系统管理员、数据管理员 | 超级管理员 |
| user1 | 123456 | 普通用户 | 张三 |
| user2 | 123456 | 数据责任人、普通用户 | 李四 |
| user3 | 123456 | 普通用户 | 王五（已禁用） |

## 访问地址

| 服务 | URL | 说明 |
|------|-----|------|
| 前端应用 | http://localhost:5173 | Vue3前端 |
| Spring Boot API | http://localhost:8080/api | RESTful API |
| API文档 | http://localhost:8080/api/doc.html | Knife4j文档 |
| Mock API | http://localhost:8080/api | Mock接口 |

## 日志查看

```bash
# Spring Boot日志
tail -f /tmp/spring-boot.log

# Mock后端日志
tail -f /tmp/mock-backend.log

# 前端日志
# 浏览器开发者工具 Console
```

## 常见问题

### 1. 端口被占用
```bash
lsof -i :8080    # 查看8080端口占用
./stop-backend.sh  # 停止后端
```

### 2. MySQL连接失败
```bash
mysqladmin ping -uroot -p1q2w3e4r  # 检查MySQL状态
mysql.server start                  # macOS启动MySQL
```

### 3. 数据库不存在
```bash
mysql -uroot -p1q2w3e4r -e "CREATE DATABASE data_asset_security CHARACTER SET utf8mb4;"
mysql -uroot -p1q2w3e4r < backend/create-tables-complete.sql
mysql -uroot -p1q2w3e4r < backend/sync-mock-data.sql
```

### 4. 接口返回500错误
```bash
# 查看Spring Boot日志
tail -50 /tmp/spring-boot.log | grep -i "error\|exception"

# 常见原因：
# 1. 表缺少BaseEntity字段 → 执行create-tables-complete.sql
# 2. 表中没有数据 → 执行sync-mock-data.sql
# 3. MFA配置问题 → 已修复checkMfaEnabled返回false
```

### 5. 登录失败
```bash
# 检查用户表
mysql -uroot -p1q2w3e4r data_asset_security -e "SELECT user_id, username, status FROM sys_user;"

# 如果为空，重新同步数据
mysql -uroot -p1q2w3e4r < backend/sync-mock-data.sql
```

### 6. 编译失败
```bash
cd backend && mvn clean package -DskipTests
```

## 开发工作流

### 前端开发（使用Mock后端）
```bash
./start-mock.sh
cd frontend && npm run dev
# 访问 http://localhost:5173
```

### 集成测试（使用Spring Boot后端）
```bash
./start-real.sh
cd frontend && npm run dev
# 访问 http://localhost:5173，使用 admin/admin123 登录
```

### 生产部署
```bash
# 1. 编译前端
cd frontend && npm run build

# 2. 启动Spring Boot后端
./start-real.sh

# 3. 配置Nginx反向代理
```

## 数据同步

### 从Mock同步到MySQL
```bash
# 完整流程
node backend/extract-all-mock-data.js > /tmp/all-mock-data.json
python3 backend/generate-full-sync-sql.py
mysql -uroot -p1q2w3e4r < backend/sync-mock-data.sql
```

## 性能对比

| 指标 | Mock后端 | Spring Boot后端 |
|------|---------|----------------|
| 启动时间 | 1-2秒 | 8-10秒 |
| 内存占用 | ~50MB | ~500MB |
| 并发支持 | 低 | 高 |
| 数据持久化 | 否 | 是 |
| 生产可用 | 否 | 是 |

## 安全建议

- 修改默认数据库密码
- 生产环境配置HTTPS
- 启用防火墙
- 定期备份数据库
- 监控审计日志
- Mock后端仅用于开发环境，不要暴露到公网
