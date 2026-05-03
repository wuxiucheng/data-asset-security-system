# 授权认证功能 - 快速部署指南

## 📋 前置条件

### 系统要求
- **操作系统**: Windows/Linux/MacOS
- **Java**: JDK 17+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Maven**: 3.6+

### 开发工具
- **IDE**: IntelliJ IDEA / VS Code
- **浏览器**: Chrome / Firefox / Edge
- **API测试工具**: Postman / Apifox

## 🚀 快速开始

### 1. 数据库初始化

#### 1.1 创建数据库
```sql
-- 登录MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE IF NOT EXISTS data_asset_security DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE data_asset_security;
```

#### 1.2 执行初始化脚本
```bash
# 进入后端目录
cd data-asset-security-system/backend

# 执行基础表结构初始化
mysql -u root -p data_asset_security < src/main/resources/db/init.sql

# 执行认证相关表初始化
mysql -u root -p data_asset_security < src/main/resources/db/auth_tables.sql
```

#### 1.3 验证数据库
```sql
-- 查看已创建的表
SHOW TABLES;

-- 验证用户表
SELECT * FROM sys_user;

-- 验证角色表
SELECT * FROM sys_role;
```

### 2. 后端服务启动

#### 2.1 配置文件修改
编辑 `backend/src/main/resources/application.yml`:

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/data_asset_security?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password  # 修改为你的MySQL密码

  # Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      password:  # 如果有密码请配置

# JWT配置
jwt:
  secret: data-asset-security-system-jwt-secret-key-2025  # 生产环境请修改
  expiration: 86400000  # 24小时
```

#### 2.2 启动后端服务
```bash
# 进入后端目录
cd data-asset-security-system/backend

# 清理并安装依赖
mvn clean install

# 启动服务（开发模式）
mvn spring-boot:run

# 或者打包后启动
mvn clean package
java -jar target/data-asset-security-1.0.0.jar
```

#### 2.3 验证后端服务
```bash
# 检查服务是否启动成功
curl http://localhost:8080/api/actuator/health

# 访问API文档
# 浏览器打开: http://localhost:8080/api/doc.html
```

### 3. 前端应用启动

#### 3.1 安装依赖
```bash
# 进入前端目录
cd data-asset-security-system/frontend

# 安装依赖
npm install
```

#### 3.2 启动开发服务器
```bash
# 启动开发服务器
npm run dev

# 服务将在 http://localhost:5173 启动
```

#### 3.3 构建生产版本
```bash
# 构建生产版本
npm run build

# 构建产物在 dist 目录
```

### 4. 验证完整功能

#### 4.1 测试登录功能
1. 访问: `http://localhost:5173`
2. 输入默认账号: `admin / admin123`
3. 点击登录按钮
4. 登录成功后跳转到首页

#### 4.2 测试MFA功能
1. 登录后访问MFA设置页面
2. 点击"启用多因素认证"
3. 下载Google Authenticator应用
4. 扫描二维码
5. 输入验证码确认设置
6. 保存备用码

#### 4.3 测试会话管理
1. 访问会话管理页面
2. 查看当前会话信息
3. 查看活跃会话列表
4. 测试强制下线功能

#### 4.4 测试审计日志
1. 访问审计日志页面
2. 使用筛选条件查询日志
3. 查看统计数据
4. 测试导出功能

## 🔧 常见问题解决

### 1. 数据库连接失败
**问题**: `Communications link failure`
**解决**:
- 检查MySQL服务是否启动
- 检查数据库地址和端口配置
- 检查用户名和密码是否正确
- 检查防火墙设置

### 2. Redis连接失败
**问题**: `Unable to connect to Redis`
**解决**:
- 检查Redis服务是否启动
- 检查Redis地址和端口配置
- 检查Redis密码配置
- 检查Redis是否允许远程连接

### 3. 前端API调用失败
**问题**: `Network Error` 或 `404 Not Found`
**解决**:
- 检查后端服务是否启动
- 检查API地址配置
- 检查CORS配置
- 检查网络连接

### 4. MFA验证失败
**问题**: 验证码总是显示错误
**解决**:
- 检查设备时间是否同步
- 检查二维码是否扫描正确
- 检查密钥是否正确
- 尝试使用备用码

### 5. Token过期问题
**问题**: 频繁要求重新登录
**解决**:
- 检查Token过期时间配置
- 检查Token刷新机制
- 检查本地存储是否正常
- 检查网络连接

## 📊 性能优化建议

### 1. 数据库优化
```sql
-- 创建索引
CREATE INDEX idx_username ON sys_user(username);
CREATE INDEX idx_user_id ON auth_session(user_id);
CREATE INDEX idx_operation_time ON auth_audit_log(operation_time);

-- 定期清理过期数据
DELETE FROM auth_session WHERE expire_time < NOW();
DELETE FROM auth_token_blacklist WHERE expire_time < NOW();
```

### 2. Redis优化
```bash
# 设置Redis最大内存
redis-cli CONFIG SET maxmemory 1gb

# 设置内存淘汰策略
redis-cli CONFIG SET maxmemory-policy allkeys-lru

# 查看内存使用情况
redis-cli INFO memory
```

### 3. 应用优化
```yaml
# application.yml
spring:
  jackson:
    serialization:
      write-dates-as-timestamps: false  # 日期格式优化

server:
  compression:
    enabled: true  # 启用GZIP压缩

logging:
  level:
    com.dataasset.security: info  # 生产环境日志级别
```

## 🔒 安全配置建议

### 1. 生产环境配置
```yaml
# application-prod.yml
jwt:
  secret: ${JWT_SECRET:your-super-secret-key-change-in-production}
  expiration: 3600000  # 1小时

spring:
  datasource:
    password: ${DB_PASSWORD}
```

### 2. 环境变量配置
```bash
# .env文件
DB_HOST=localhost
DB_PORT=3306
DB_NAME=data_asset_security
DB_USERNAME=root
DB_PASSWORD=your_password
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your-secret-key
```

### 3. 安全加固
- 修改默认管理员密码
- 配置HTTPS证书
- 启用防火墙
- 定期备份数据
- 监控安全日志

## 📈 监控和日志

### 1. 应用监控
```bash
# 健康检查
curl http://localhost:8080/api/actuator/health

# 性能指标
curl http://localhost:8080/api/actuator/metrics

# 环境信息
curl http://localhost:8080/api/actuator/env
```

### 2. 日志查看
```bash
# 应用日志
tail -f backend/logs/application.log

# 访问日志
tail -f backend/logs/access.log

# 错误日志
tail -f backend/logs/error.log
```

### 3. 数据库监控
```sql
-- 查看连接数
SHOW PROCESSLIST;

-- 查看表大小
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
FROM information_schema.tables
WHERE table_schema = 'data_asset_security'
ORDER BY size_mb DESC;
```

## 🧪 测试指南

### 1. 单元测试
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=AuthServiceTest

# 查看测试报告
mvn surefire-report:report
```

### 2. 集成测试
```bash
# 运行集成测试
mvn verify

# 使用测试配置文件
mvn test -Dspring.profiles.active=test
```

### 3. API测试
```bash
# 使用curl测试登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 使用Postman测试
# 导入API文档中的接口进行测试
```

## 📝 维护指南

### 1. 日常维护
- 定期检查系统日志
- 监控数据库性能
- 清理过期数据
- 备份重要数据
- 更新系统依赖

### 2. 数据备份
```bash
# 数据库备份
mysqldump -u root -p data_asset_security > backup_$(date +%Y%m%d).sql

# Redis备份
redis-cli --rdb /path/to/backup/dump_$(date +%Y%m%d).rdb

# 应用配置备份
cp application.yml application.yml.bak
```

### 3. 数据清理
```sql
-- 清理30天前的审计日志
DELETE FROM auth_audit_log 
WHERE operation_time < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 清理已过期的会话
DELETE FROM auth_session 
WHERE expire_time < NOW();

-- 清理已过期的Token黑名单
DELETE FROM auth_token_blacklist 
WHERE expire_time < NOW();
```

## 🚀 生产部署

### 1. Docker部署
```bash
# 构建Docker镜像
docker build -t data-asset-security:latest .

# 使用Docker Compose启动
docker-compose up -d

# 查看日志
docker-compose logs -f
```

### 2. 传统部署
```bash
# 停止旧服务
./stop.sh

# 备份旧版本
mv data-asset-security.jar data-asset-security.jar.bak

# 部署新版本
cp target/data-asset-security-1.0.0.jar /opt/app/

# 启动新服务
./start.sh

# 检查服务状态
./status.sh
```

### 3. 负载均衡配置
```nginx
# nginx.conf示例
upstream backend {
    server 192.168.1.100:8080;
    server 192.168.1.101:8080;
    server 192.168.1.102:8080;
}

server {
    listen 80;
    server_name your-domain.com;

    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 📞 技术支持

### 问题反馈
- 提交Issue到项目仓库
- 联系技术支持团队
- 查看项目文档

### 获取帮助
- 阅读项目README
- 查看API文档
- 参考示例代码

---

**文档版本**: v1.0
**更新日期**: 2025-06-17
**维护团队**: Data Asset Security Team

🎯 快速部署指南完成！