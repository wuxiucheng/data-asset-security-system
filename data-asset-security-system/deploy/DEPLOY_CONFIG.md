# 部署配置说明

## 快速开始

### 1. 配置部署参数

```bash
# 复制配置模板
cp deploy/.env.deploy.example deploy/.env.deploy

# 编辑配置文件
vim deploy/.env.deploy
```

### 2. 修改必要配置

**数据库配置**（必须修改）：
```bash
DB_PASSWORD=your_mysql_password_here  # 改为线上MySQL密码
```

**其他配置**（可选）：
```bash
# 数据库
DB_HOST=localhost
DB_PORT=3306
DB_NAME=data_asset_security
DB_USERNAME=root

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# JWT
JWT_SECRET=data-asset-security-jwt-secret-key-production-2026

# 服务
SERVER_PORT=8082
JVM_OPTS=-Xms256m -Xmx512m
```

### 3. 执行部署

**一键部署**：
```bash
./deploy/deploy-all.sh
```

**分步部署**：
```bash
# 完整部署
./deploy/backend.sh full
./deploy/frontend.sh full

# 快速部署（本地已构建）
./deploy/backend.sh quick
./deploy/frontend.sh quick
```

## 部署命令

### 后端部署 (backend.sh)

| 命令 | 说明 | 使用场景 |
|------|------|----------|
| `full` | 完整部署（构建+上传+重启） | 首次部署或大改动 |
| `quick` | 快速部署（仅上传+重启） | 本地已构建 |
| `config` | 仅更新配置文件 | 仅改配置 |
| `restart` | 重启服务 | 重启服务 |
| `stop` | 停止服务 | 停止服务 |
| `start` | 启动服务 | 启动服务 |
| `status` | 查看服务状态 | 查看状态 |
| `logs` | 查看实时日志 | 调试问题 |

### 前端部署 (frontend.sh)

| 命令 | 说明 | 使用场景 |
|------|------|----------|
| `full` | 完整部署（构建+上传） | 首次部署或大改动 |
| `quick` | 快速部署（仅上传） | 本地已构建 |
| `status` | 查看部署状态 | 查看状态 |
| `clean` | 清理旧文件 | 清理文件 |

## 常见问题

### 1. 配置文件不存在

**错误**：
```
⚠️  配置文件不存在: deploy/.env.deploy
```

**解决**：
```bash
cp deploy/.env.deploy.example deploy/.env.deploy
vim deploy/.env.deploy  # 修改配置
```

### 2. 数据库连接失败

**错误**：
```
Access denied for user 'root'@'localhost'
```

**解决**：
1. 检查 `.env.deploy` 中的数据库密码是否正确
2. 确认数据库用户有权限访问 `data_asset_security` 数据库

### 3. 服务启动失败

**查看日志**：
```bash
./deploy/backend.sh logs
```

**检查状态**：
```bash
./deploy/backend.sh status
```

## 安全提示

⚠️ **重要**：
- `deploy/.env.deploy` 包含敏感信息，已加入 `.gitignore`
- 不要将此文件提交到Git
- 生产环境建议使用更强的JWT密钥
- 建议为应用创建专用的数据库用户

## 创建专用数据库用户

```sql
-- 连接MySQL
mysql -uroot -p

-- 创建用户
CREATE USER 'dataasset'@'localhost' IDENTIFIED BY 'YourStrongPassword';
CREATE USER 'dataasset'@'%' IDENTIFIED BY 'YourStrongPassword';

-- 授权
GRANT ALL PRIVILEGES ON data_asset_security.* TO 'dataasset'@'localhost';
GRANT ALL PRIVILEGES ON data_asset_security.* TO 'dataasset'@'%';

-- 刷新权限
FLUSH PRIVILEGES;
```

然后更新 `.env.deploy`：
```bash
DB_USERNAME=dataasset
DB_PASSWORD=YourStrongPassword
```
