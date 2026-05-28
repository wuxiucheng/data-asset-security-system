# 🚀 快速启动指南

## ⚠️ 问题诊断结果

**问题**: 前端API请求返回404错误

**原因**: **后端服务未启动！**

当前只有前端服务在运行（端口5173），后端Java服务（端口8080）未启动。

## 🔧 解决方案

### 方案1: 使用Maven启动（推荐）

```bash
# 1. 进入后端目录
cd data-asset-security-system/backend

# 2. 启动后端服务
mvn spring-boot:run

# 等待看到以下日志表示启动成功：
# Started DataAssetSecurityApplication in X.XXX seconds
```

### 方案2: 打包后启动

```bash
# 1. 进入后端目录
cd data-asset-security-system/backend

# 2. 打包应用
mvn clean package -DskipTests

# 3. 启动应用
java -jar target/data-asset-security-0.0.1-SNAPSHOT.jar
```

## 📋 启动前检查清单

在启动后端服务前，请确认：

### 1. MySQL数据库已启动

```bash
# macOS
brew services start mysql

# 验证
mysql -u root -p -e "SELECT 1;"
```

### 2. Redis已启动

```bash
# macOS
brew services start redis

# 验证
redis-cli ping
# 应该返回: PONG
```

### 3. 数据库表已创建

```bash
# 连接数据库
mysql -u root -p

# 检查表
USE data_asset_security;
SHOW TABLES;

# 应该看到以下表：
# sys_user, sys_role, sys_permission
# auth_session, auth_login_log, auth_mfa_config 等
```

如果表不存在，执行初始化脚本：

```bash
cd data-asset-security-system/backend/src/main/resources/db

# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS data_asset_security CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 执行脚本
mysql -u root -p data_asset_security < init.sql
mysql -u root -p data_asset_security < auth_tables.sql
```

## 🎯 完整启动流程

### 步骤1: 启动基础服务

```bash
# 启动MySQL
brew services start mysql

# 启动Redis
brew services start redis

# 验证服务状态
mysql -u root -p -e "SELECT 1;" && echo "✅ MySQL OK"
redis-cli ping && echo "✅ Redis OK"
```

### 步骤2: 初始化数据库（首次运行）

```bash
cd data-asset-security-system/backend/src/main/resources/db

# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS data_asset_security CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 执行初始化脚本
mysql -u root -p data_asset_security < init.sql
mysql -u root -p data_asset_security < auth_tables.sql

echo "✅ 数据库初始化完成"
```

### 步骤3: 启动后端服务

```bash
# 打开新终端窗口
cd data-asset-security-system/backend

# 启动后端
mvn spring-boot:run

# 等待看到: Started DataAssetSecurityApplication in X.XXX seconds
echo "✅ 后端服务启动完成"
```

### 步骤4: 启动前端服务

```bash
# 打开新终端窗口
cd data-asset-security-system/frontend

# 安装依赖（首次运行）
npm install

# 启动前端
npm run dev

# 访问 http://localhost:5173
echo "✅ 前端服务启动完成"
```

## 🧪 验证服务状态

### 1. 检查后端服务

```bash
# 测试后端API
curl http://localhost:8080/api/user/list

# 如果返回JSON数据，说明后端正常
```

### 2. 检查前端服务

```bash
# 访问前端
open http://localhost:5173

# 或者使用curl
curl http://localhost:5173
```

### 3. 测试登录功能

```bash
# 使用curl测试登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 应该返回token和用户信息
```

## 📊 服务端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| MySQL | 3306 | 数据库服务 |
| Redis | 6379 | 缓存服务 |
| 后端API | 8080 | Spring Boot后端 |
| 前端UI | 5173 | Vue.js前端 |

## 🔍 常见问题

### Q1: 端口8080被占用

```bash
# 查找占用端口的进程
lsof -i :8080

# 终止进程
kill -9 <PID>

# 或者修改端口
# 编辑 application.yml
server:
  port: 8081
```

### Q2: 数据库连接失败

```bash
# 检查MySQL是否运行
brew services list | grep mysql

# 启动MySQL
brew services start mysql

# 检查连接
mysql -u root -p
```

### Q3: Redis连接失败

```bash
# 检查Redis是否运行
brew services list | grep redis

# 启动Redis
brew services start redis

# 测试连接
redis-cli ping
```

### Q4: Maven依赖下载失败

```bash
# 清理Maven缓存
cd data-asset-security-system/backend
mvn clean

# 重新下载依赖
mvn dependency:resolve

# 重新编译
mvn compile
```

## 📝 启动脚本

创建一个启动脚本方便后续使用：

```bash
#!/bin/bash
# start-all.sh

echo "🚀 启动数据资产安全系统..."

# 启动MySQL
echo "启动MySQL..."
brew services start mysql

# 启动Redis
echo "启动Redis..."
brew services start redis

# 等待服务启动
sleep 3

# 启动后端
echo "启动后端服务..."
cd data-asset-security-system/backend
mvn spring-boot:run &

# 等待后端启动
sleep 10

# 启动前端
echo "启动前端服务..."
cd ../frontend
npm run dev &

echo "✅ 所有服务启动完成！"
echo "前端地址: http://localhost:5173"
echo "后端地址: http://localhost:8080/api"
```

## 🎯 下一步

启动所有服务后：

1. ✅ 访问 http://localhost:5173
2. ✅ 使用 admin/admin123 登录
3. ✅ 测试多因素认证功能
4. ✅ 测试会话管理功能
5. ✅ 测试审计日志功能

---

**创建时间**: 2025-06-17
**问题**: 后端服务未启动导致API 404错误
**解决**: 启动后端Spring Boot服务

🎯 请按照上述步骤启动后端服务！