# API 404错误诊断和解决方案

## 🔍 问题分析

根据错误信息，前端请求返回404错误，说明后端API路径无法找到。可能的原因：

1. **后端服务未启动** - 最常见的原因
2. **数据库表未创建** - 认证相关表不存在
3. **服务依赖缺失** - MfaService、SessionService等未正确注入
4. **路径配置问题** - 控制器路径与前端API不匹配

## 📋 诊断步骤

### 步骤1: 检查后端服务状态

```bash
# 检查后端服务是否运行
curl http://localhost:8080/api/health

# 或者检查进程
ps aux | grep java
```

**预期结果**: 后端服务应该正在运行

**如果服务未运行**:
```bash
# 进入后端目录
cd data-asset-security-system/backend

# 启动后端服务
mvn spring-boot:run

# 或者如果已经打包
java -jar target/data-asset-security-0.0.1-SNAPSHOT.jar
```

### 步骤2: 检查数据库表

```sql
-- 连接MySQL数据库
mysql -u root -p

-- 使用数据库
USE data_asset_security;

-- 检查认证相关表是否存在
SHOW TABLES LIKE 'auth_%';

-- 预期结果应该包含以下表：
-- auth_session
-- auth_login_log
-- auth_mfa_config
-- auth_account_lock
-- auth_rate_limit
-- auth_audit_log
-- auth_permission_check
-- auth_security_event
-- auth_user_device
-- auth_backup_code
```

**如果表不存在**:
```bash
# 执行数据库初始化脚本
cd data-asset-security-system/backend/src/main/resources/db

# 执行基础表脚本
mysql -u root -p data_asset_security < init.sql

# 执行认证表脚本
mysql -u root -p data_asset_security < auth_tables.sql
```

### 步骤3: 检查API路径

```bash
# 测试MFA API
curl -X GET http://localhost:8080/api/mfa/status \
  -H "Authorization: Bearer YOUR_TOKEN"

# 测试Session API
curl -X GET http://localhost:8080/api/session/current \
  -H "Authorization: Bearer YOUR_TOKEN"

# 测试AuditLog API
curl -X GET http://localhost:8080/api/audit-logs/list \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 步骤4: 检查后端日志

```bash
# 查看后端启动日志
tail -f data-asset-security-system/backend/logs/spring.log

# 或者查看控制台输出
# 查找是否有错误信息，特别是：
# - Bean创建失败
# - 数据库连接失败
# - 表不存在错误
```

## 🔧 解决方案

### 方案1: 启动后端服务

```bash
# 1. 进入后端目录
cd data-asset-security-system/backend

# 2. 确保依赖已安装
mvn clean install

# 3. 启动服务
mvn spring-boot:run

# 4. 等待服务启动完成（看到"Started DataAssetSecurityApplication"日志）
```

### 方案2: 初始化数据库

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS data_asset_security CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 执行初始化脚本
cd data-asset-security-system/backend/src/main/resources/db

# 3. 执行基础表脚本
mysql -u root -p data_asset_security < init.sql

# 4. 执行认证表脚本
mysql -u root -p data_asset_security < auth_tables.sql

# 5. 验证表创建成功
mysql -u root -p -e "USE data_asset_security; SHOW TABLES LIKE 'auth_%';"
```

### 方案3: 检查配置文件

检查 `application.yml` 配置：

```yaml
# 确认以下配置正确
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/data_asset_security
    username: root
    password: your_password

  # Redis配置
  data:
    redis:
      host: localhost
      port: 6379
```

### 方案4: 检查依赖注入

如果后端启动时报错找不到Bean，检查：

1. **MfaService是否正确实现**
```java
@Service
public class MfaServiceImpl implements MfaService {
    // 实现代码
}
```

2. **SessionService是否正确实现**
```java
@Service
public class SessionServiceImpl implements SessionService {
    // 实现代码
}
```

3. **AuditLogService是否正确实现**
```java
@Service
public class AuditLogServiceImpl implements AuditLogService {
    // 实现代码
}
```

## 🚀 完整启动流程

### 1. 启动MySQL数据库

```bash
# macOS
brew services start mysql

# Linux
sudo systemctl start mysql

# 验证
mysql -u root -p -e "SELECT 1;"
```

### 2. 启动Redis

```bash
# macOS
brew services start redis

# Linux
sudo systemctl start redis

# 验证
redis-cli ping
```

### 3. 初始化数据库

```bash
cd data-asset-security-system/backend/src/main/resources/db

# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS data_asset_security CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 执行脚本
mysql -u root -p data_asset_security < init.sql
mysql -u root -p data_asset_security < auth_tables.sql
```

### 4. 启动后端服务

```bash
cd data-asset-security-system/backend

# 清理并编译
mvn clean install

# 启动服务
mvn spring-boot:run

# 等待看到日志: "Started DataAssetSecurityApplication in X seconds"
```

### 5. 启动前端服务

```bash
cd data-asset-security-system/frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 访问 http://localhost:5173
```

## 🧪 验证API可用性

### 使用curl测试

```bash
# 1. 登录获取token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 保存返回的token

# 2. 测试MFA状态
curl -X GET http://localhost:8080/api/mfa/status \
  -H "Authorization: Bearer YOUR_TOKEN"

# 3. 测试会话管理
curl -X GET http://localhost:8080/api/session/current \
  -H "Authorization: Bearer YOUR_TOKEN"

# 4. 测试审计日志
curl -X GET http://localhost:8080/api/audit-logs/list \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 使用浏览器测试

1. 打开浏览器开发者工具（F12）
2. 切换到Network标签
3. 访问前端页面
4. 查看API请求状态
5. 检查请求URL和响应状态码

## 📊 常见错误及解决

### 错误1: Connection refused

**原因**: 后端服务未启动

**解决**: 
```bash
cd data-asset-security-system/backend
mvn spring-boot:run
```

### 错误2: Table doesn't exist

**原因**: 数据库表未创建

**解决**:
```bash
cd data-asset-security-system/backend/src/main/resources/db
mysql -u root -p data_asset_security < auth_tables.sql
```

### 错误3: Bean creation exception

**原因**: 服务类未正确实现或依赖缺失

**解决**:
1. 检查服务实现类是否存在
2. 检查@Service注解是否添加
3. 检查依赖注入是否正确

### 错误4: Redis connection failed

**原因**: Redis未启动

**解决**:
```bash
# macOS
brew services start redis

# Linux
sudo systemctl start redis
```

## 📝 检查清单

在测试API之前，请确认：

- [ ] MySQL数据库已启动
- [ ] Redis服务已启动
- [ ] 数据库表已创建（包括auth_*表）
- [ ] 后端服务已启动（端口8080）
- [ ] 前端服务已启动（端口5173）
- [ ] 已登录系统获取有效token
- [ ] 网络连接正常

## 🎯 快速诊断命令

```bash
# 一键检查所有服务状态
echo "=== 检查MySQL ==="
mysql -u root -p -e "SELECT 1;" && echo "MySQL: OK" || echo "MySQL: FAILED"

echo "=== 检查Redis ==="
redis-cli ping && echo "Redis: OK" || echo "Redis: FAILED"

echo "=== 检查后端服务 ==="
curl -s http://localhost:8080/api/health && echo "Backend: OK" || echo "Backend: FAILED"

echo "=== 检查数据库表 ==="
mysql -u root -p -e "USE data_asset_security; SHOW TABLES LIKE 'auth_%';"
```

## 📞 获取帮助

如果以上步骤都无法解决问题，请提供：

1. 后端启动日志（特别是错误信息）
2. 数据库表列表（`SHOW TABLES;`）
3. 前端控制台错误信息
4. Network请求详情（URL、状态码、响应）

---

**诊断时间**: 2025-06-17
**问题类型**: API 404错误
**影响范围**: MFA、会话管理、审计日志功能

🎯 请按照上述步骤逐一排查问题！