# 自动化部署指南

本文档介绍如何使用 Docker 和 GitHub Actions 实现自动化部署。

## 📋 前置要求

### 服务器要求
- 操作系统：Linux (Ubuntu 20.04+ 或 CentOS 7+)
- Docker: 20.10+
- Docker Compose: 2.0+
- Git: 2.0+
- 内存：至少 4GB
- 磁盘：至少 20GB

### 本地要求
- Docker: 20.10+
- Docker Compose: 2.0+

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/wuxiucheng/data-asset-security-system.git
cd data-asset-security-system
```

### 2. 配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑环境变量
vim .env
```

修改以下配置：
```env
DB_PASSWORD=your-secure-password
REDIS_PASSWORD=your-redis-password
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=your-rabbitmq-password
JWT_SECRET=your-secure-jwt-secret-at-least-32-characters
```

### 3. 一键部署

```bash
# 添加执行权限
chmod +x deploy.sh

# 部署到生产环境
./deploy.sh deploy prod

# 或直接运行
./deploy.sh
```

### 4. 查看服务状态

```bash
# 查看容器状态
docker-compose ps

# 查看日志
./deploy.sh logs
# 或
docker-compose logs -f
```

## 📦 Docker 部署

### 服务架构

```
┌─────────────────────────────────────────┐
│           Nginx (Frontend)              │
│              Port: 80                    │
└──────────────┬──────────────────────────┘
               │
               ├─→ Backend (Spring Boot)
               │      Port: 8080
               │
               ├─→ MySQL 8.0
               │      Port: 3306
               │
               ├─→ Redis 7
               │      Port: 6379
               │
               └─→ RabbitMQ 3
                      Port: 5672, 15672
```

### 手动部署

```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看日志
docker-compose logs -f [service_name]
```

### 单独构建服务

```bash
# 构建后端
docker-compose build backend

# 构建前端
docker-compose build frontend

# 重启后端
docker-compose up -d backend

# 重启前端
docker-compose up -d frontend
```

## 🔄 CI/CD 自动部署

### GitHub Actions 配置

项目已配置 GitHub Actions 自动部署工作流。

#### 1. 配置 GitHub Secrets

在 GitHub 仓库设置中添加以下 Secrets：

- `SERVER_HOST`: 服务器 IP 地址
- `SERVER_USERNAME`: 服务器用户名
- `SERVER_SSH_KEY`: 服务器 SSH 私钥

#### 2. 触发自动部署

自动部署会在以下情况触发：
- 推送代码到 `main` 分支
- 手动触发 workflow

#### 3. 查看部署状态

在 GitHub 仓库的 Actions 页面查看部署状态和日志。

### 手动触发部署

1. 进入 GitHub 仓库
2. 点击 Actions 标签
3. 选择 "Deploy to Production" workflow
4. 点击 "Run workflow"

## 🛠️ 部署脚本说明

### deploy.sh 脚本命令

```bash
# 部署到指定环境
./deploy.sh deploy [env]

# 回滚到上一个版本
./deploy.sh rollback

# 查看服务日志
./deploy.sh logs

# 显示帮助信息
./deploy.sh help
```

### 环境说明

- `dev`: 开发环境
- `test`: 测试环境
- `prod`: 生产环境（默认）

## 🔧 常用操作

### 查看服务状态

```bash
docker-compose ps
```

### 查看服务日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

### 进入容器

```bash
# 进入后端容器
docker-compose exec backend sh

# 进入前端容器
docker-compose exec frontend sh

# 进入 MySQL 容器
docker-compose exec mysql bash
```

### 数据库操作

```bash
# 连接 MySQL
docker-compose exec mysql mysql -uroot -p

# 备份数据库
docker-compose exec mysql mysqldump -uroot -p data_asset_security > backup.sql

# 恢复数据库
docker-compose exec -T mysql mysql -uroot -p data_asset_security < backup.sql
```

### 重启服务

```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart backend
docker-compose restart frontend
```

## 🔍 健康检查

### 检查服务健康

```bash
# 后端健康检查
curl http://localhost:8080/api/actuator/health

# 前端访问检查
curl http://localhost
```

### 查看容器资源使用

```bash
docker stats
```

## 🚨 故障排查

### 常见问题

#### 1. 容器启动失败

```bash
# 查看容器日志
docker-compose logs [service_name]

# 查看容器状态
docker-compose ps
```

#### 2. 数据库连接失败

检查 MySQL 容器是否正常：
```bash
docker-compose ps mysql
docker-compose logs mysql
```

#### 3. 内存不足

调整 Docker 内存限制：
```yaml
services:
  backend:
    deploy:
      resources:
        limits:
          memory: 1G
```

#### 4. 端口冲突

修改 docker-compose.yml 中的端口映射：
```yaml
services:
  frontend:
    ports:
      - "8080:80"  # 修改为其他端口
```

## 🔐 安全建议

1. **修改默认密码**：修改 .env 文件中的所有默认密码
2. **使用 HTTPS**：配置 SSL 证书
3. **防火墙配置**：只开放必要端口
4. **定期备份**：定期备份数据库和配置文件
5. **日志监控**：配置日志监控和告警

## 📊 性能优化

### 1. JVM 优化

修改后端 Dockerfile 中的 JVM 参数：
```dockerfile
ENV JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC"
```

### 2. MySQL 优化

在 docker-compose.yml 中添加 MySQL 配置：
```yaml
services:
  mysql:
    command:
      - --max_connections=1000
      - --innodb_buffer_pool_size=1G
```

### 3. Redis 优化

```yaml
services:
  redis:
    command: redis-server --requirepass ${REDIS_PASSWORD} --maxmemory 512mb
```

## 📝 更新部署

### 更新代码

```bash
# 拉取最新代码
git pull origin main

# 重新部署
./deploy.sh deploy prod
```

### 更新配置

```bash
# 修改配置后重启
docker-compose down
docker-compose up -d
```

## 🎯 访问地址

部署成功后，可通过以下地址访问：

- **前端**: http://your-server-ip
- **后端 API**: http://your-server-ip:8080/api
- **API 文档**: http://your-server-ip:8080/api/doc.html
- **RabbitMQ 管理**: http://your-server-ip:15672

默认登录账号：
- 用户名: `admin`
- 密码: `admin123`

---

如有问题，请查看日志或联系开发团队。
