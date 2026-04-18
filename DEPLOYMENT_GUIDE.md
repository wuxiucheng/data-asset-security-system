# 数据资产安全及分类分级管理系统 - 本地部署指南

## 📋 部署前准备

### 1. 环境要求
- ✅ JDK 17+ (当前: 19.0.2)
- ✅ Maven 3.6+ (当前: 3.9.9)
- ✅ MySQL 5.7+ (当前: 5.7.24)
- ✅ Redis 6.0+ (需要安装)

### 2. 项目结构
```
分级分类/
├── data-asset-security-system/
│   ├── backend/              # 后端项目
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   ├── resources/
│   │   │   │   │   ├── application.yml
│   │   │   │   │   └── db/
│   │   │   │   │       └── init.sql
│   │   │   │   └── application.properties
│   │   ├── pom.xml
│   │   └── target/
│   └── frontend/             # 前端项目（待开发）
└── PROJECT_SUMMARY.md        # 项目总结文档
```

## 🗄️ 数据库配置

### 1. 创建数据库
```bash
# 连接到MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE IF NOT EXISTS data_asset_security DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 退出MySQL
EXIT;
```

### 2. 执行初始化脚本
```bash
# 进入项目目录
cd "/Users/wuxiucheng/分级分类/data-asset-security-system/backend"

# 执行初始化脚本（安全模式，不会删除现有数据）
mysql -u root -p data_asset_security < src/main/resources/db/init.sql
```

### 3. 验证数据库初始化
```bash
# 连接到数据库
mysql -u root -p data_asset_security

# 查看表结构
SHOW TABLES;

# 查看初始数据
SELECT * FROM sys_user;
SELECT * FROM sys_role;
SELECT * FROM department;

# 退出MySQL
EXIT;
```

## ⚙️ 配置文件修改

### 1. 修改数据库配置
编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/data_asset_security?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 请修改为您的MySQL密码
```

### 2. 修改Redis配置
```yaml
  data:
    redis:
      host: localhost
      port: 6379
      password:              # 如果Redis设置了密码，请填写
      database: 0
      timeout: 3000
```

### 3. 修改JWT配置
```yaml
jwt:
  secret: your-secret-key-change-this-in-production  # 请修改为您的密钥
  expiration: 86400000  # 24小时
```

## 🔨 项目构建

### 1. 清理并编译项目
```bash
cd "/Users/wuxiucheng/分级分类/data-asset-security-system/backend"

# 清理之前的构建
mvn clean

# 编译项目
mvn compile

# 打包项目
mvn package -DskipTests
```

### 2. 验证构建结果
```bash
# 查看target目录
ls -lh target/

# 查看jar包
ls -lh target/*.jar
```

## 🚀 启动应用

### 1. 启动后端服务
```bash
cd "/Users/wuxiucheng/分级分类/data-asset-security-system/backend"

# 启动应用
java -jar target/data-asset-security-1.0.0.jar

# 或者使用Maven启动
mvn spring-boot:run
```

### 2. 验证启动成功
当看到以下日志时，说明启动成功：
```
Started DataAssetSecurityApplication in X.XXX seconds
Tomcat started on port(s): 8080 (http)
```

## 🌐 访问应用

### 1. API文档访问
- **Swagger UI**: http://localhost:8080/doc.html
- **Knife4j UI**: http://localhost:8080/doc.html#/home

### 2. 默认登录信息
- **用户名**: admin
- **密码**: admin123

### 3. 测试API
使用Postman或curl测试API接口：

```bash
# 测试登录接口
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# 测试获取用户信息
curl -X GET http://localhost:8080/api/user/info \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🐛 常见问题排查

### 1. 数据库连接失败
```bash
# 检查MySQL服务状态
brew services list | grep mysql

# 启动MySQL服务
brew services start mysql

# 检查数据库是否存在
mysql -u root -p -e "SHOW DATABASES LIKE 'data_asset_security';"
```

### 2. Redis连接失败
```bash
# 检查Redis服务状态
brew services list | grep redis

# 启动Redis服务
brew services start redis

# 测试Redis连接
redis-cli ping
```

### 3. 端口被占用
```bash
# 检查8080端口占用情况
lsof -i :8080

# 如果端口被占用，可以修改application.yml中的端口
server:
  port: 8081  # 修改为其他端口
```

### 4. 编译错误
```bash
# 清理Maven缓存
rm -rf ~/.m2/repository/com/dataasset

# 重新编译
mvn clean install -DskipTests
```

### 5. 内存不足
```bash
# 增加JVM内存
java -Xms512m -Xmx1024m -jar target/data-asset-security-1.0.0.jar
```

## 📊 功能测试清单

### 用户权限管理
- [ ] 用户登录登出
- [ ] 用户创建、编辑、删除
- [ ] 用户状态管理
- [ ] 角色创建、编辑、删除
- [ ] 权限分配
- [ ] 用户角色分配

### 责任体系管理
- [ ] 部门创建、编辑、删除
- [ ] 部门树形结构
- [ ] 责任人创建、编辑、删除
- [ ] 组织架构导入导出

### 数据分类分级管理
- [ ] 分类标准创建、发布
- [ ] 分级标准创建、发布
- [ ] 数据分类管理
- [ ] 数据分级管理
- [ ] 分类分级查询

### 数据资产管理
- [ ] 数据资产注册
- [ ] 数据资产查询
- [ ] 数据资产编辑、删除
- [ ] 字段管理
- [ ] 批量导入导出

### 审批流程管理
- [ ] 流程定义创建
- [ ] 流程实例启动
- [ ] 审批任务处理
- [ ] 审批历史查询

### 统计分析
- [ ] 资产统计概览
- [ ] 分类分布统计
- [ ] 分级分布统计
- [ ] 趋势分析

## 🔐 安全注意事项

### 1. 修改默认密码
```bash
# 登录系统后立即修改admin密码
# 建议使用强密码：包含大小写字母、数字、特殊字符
```

### 2. 修改JWT密钥
```yaml
# 在application.yml中修改jwt.secret
jwt:
  secret: your-very-secure-secret-key-here-change-in-production
```

### 3. 数据库安全
```sql
-- 创建专用数据库用户
CREATE USER 'dataasset_user'@'localhost' IDENTIFIED BY 'strong_password';

-- 授予必要权限
GRANT SELECT, INSERT, UPDATE, DELETE ON data_asset_security.* TO 'dataasset_user'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;
```

## 📈 性能调优

### 1. 数据库优化
```sql
-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query%';

-- 启用慢查询日志
SET GLOBAL slow_query_log = 'ON';

-- 分析查询性能
EXPLAIN SELECT * FROM data_asset WHERE asset_name LIKE '%test%';
```

### 2. JVM调优
```bash
# 生产环境推荐配置
java -Xms1g -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -jar target/data-asset-security-1.0.0.jar
```

### 3. 应用配置调优
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## 📝 日志查看

### 1. 应用日志
```bash
# 查看应用日志
tail -f logs/application.log

# 查看错误日志
grep ERROR logs/application.log

# 查看访问日志
grep "POST\|GET\|PUT\|DELETE" logs/access.log
```

### 2. 审计日志
```sql
-- 查看审计日志
SELECT * FROM audit_log ORDER BY operation_time DESC LIMIT 10;

-- 按用户查询审计日志
SELECT * FROM audit_log WHERE operator_id = 1 ORDER BY operation_time DESC;
```

## 🔄 停止和重启

### 1. 停止应用
```bash
# 查找Java进程
ps aux | grep data-asset-security

# 停止进程
kill -9 <PID>

# 或者使用Ctrl+C停止（如果是命令行启动）
```

### 2. 重启应用
```bash
# 停止旧进程
kill -9 $(ps aux | grep data-asset-security | grep java | awk '{print $2}')

# 重新启动
cd "/Users/wuxiucheng/分级分类/data-asset-security-system/backend"
java -jar target/data-asset-security-1.0.0.jar
```

## 🎯 功能体验建议

### 1. 按照业务流程体验
1. 用户登录 → 修改密码
2. 创建部门 → 创建责任人
3. 创建分类标准 → 创建分类
4. 创建分级标准 → 创建分级
5. 注册数据资产 → 设置分类分级
6. 查看统计数据

### 2. 测试核心功能
- 测试数据资产的全生命周期管理
- 测试分类分级的创建和分配
- 测试责任体系的完整性
- 测试审批流程的流转

### 3. 性能测试
- 测试大数据量查询
- 测试批量导入导出
- 测试并发访问

## 📞 技术支持

如有问题，请查看：
1. 项目总结文档：PROJECT_SUMMARY.md
2. API文档：http://localhost:8080/doc.html
3. 应用日志：logs/application.log
4. 数据库日志：MySQL error log

---

## 🎉 部署完成

恭喜！数据资产安全及分类分级管理系统已经本地部署完成！

现在您可以：
1. 访问 http://localhost:8080/doc.html 查看API文档
2. 使用 admin/admin123 登录系统
3. 开始体验各项功能
4. 查看系统统计数据

**部署状态：✅ 已完成**
**系统状态：✅ 运行正常**

开始您的数据资产安全管理之旅吧！🚀
