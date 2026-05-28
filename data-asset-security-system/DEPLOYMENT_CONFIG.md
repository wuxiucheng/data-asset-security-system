# 部署配置说明

## 环境配置

项目支持多环境配置，通过 `SPRING_PROFILES_ACTIVE` 环境变量切换：

- **dev**: 开发环境（默认）
- **test**: 测试环境
- **prod**: 生产环境

## 快速开始

### 1. 开发环境（默认）

直接启动即可，使用本地默认配置：

```bash
java -jar backend.jar
```

### 2. 测试环境

使用环境变量配置：

```bash
export SPRING_PROFILES_ACTIVE=test
export DB_HOST=your-test-db-host
export DB_PASSWORD=your-test-db-password
export JWT_SECRET=your-test-jwt-secret

java -jar backend.jar
```

### 3. 生产环境

**必须配置所有必需的环境变量**：

```bash
export SPRING_PROFILES_ACTIVE=prod

# 数据库配置（必填）
export DB_HOST=your-prod-db-host
export DB_PORT=3306
export DB_NAME=data_asset_security
export DB_USERNAME=your-db-username
export DB_PASSWORD=your-db-password

# Redis配置（必填）
export REDIS_HOST=your-redis-host
export REDIS_PORT=6379
export REDIS_PASSWORD=your-redis-password

# RabbitMQ配置（必填）
export RABBITMQ_HOST=your-rabbitmq-host
export RABBITMQ_PORT=5672
export RABBITMQ_USERNAME=your-rabbitmq-username
export RABBITMQ_PASSWORD=your-rabbitmq-password

# JWT配置（必填）
export JWT_SECRET=your-secure-jwt-secret-key

# 服务端口（可选）
export SERVER_PORT=8080

java -jar backend.jar
```

## Docker 部署

### 使用 docker-compose

创建 `.env` 文件：

```env
SPRING_PROFILES_ACTIVE=prod
DB_HOST=mysql
DB_PORT=3306
DB_NAME=data_asset_security
DB_USERNAME=root
DB_PASSWORD=your-secure-password
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=your-rabbitmq-password
JWT_SECRET=your-very-secure-jwt-secret-key-at-least-32-characters
```

启动服务：

```bash
docker-compose up -d
```

## Kubernetes 部署

使用 ConfigMap 和 Secret 管理配置：

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
stringData:
  DB_PASSWORD: "your-db-password"
  REDIS_PASSWORD: "your-redis-password"
  RABBITMQ_PASSWORD: "your-rabbitmq-password"
  JWT_SECRET: "your-jwt-secret"
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  SPRING_PROFILES_ACTIVE: "prod"
  DB_HOST: "mysql-service"
  DB_PORT: "3306"
  DB_NAME: "data_asset_security"
  DB_USERNAME: "root"
  REDIS_HOST: "redis-service"
  REDIS_PORT: "6379"
  RABBITMQ_HOST: "rabbitmq-service"
  RABBITMQ_PORT: "5672"
  RABBITMQ_USERNAME: "admin"
```

## 配置文件说明

| 文件 | 说明 | 是否提交到Git |
|------|------|--------------|
| `application.yml` | 主配置文件，包含公共配置 | ✅ 是 |
| `application-dev.yml` | 开发环境配置 | ✅ 是 |
| `application-test.yml` | 测试环境配置模板 | ✅ 是 |
| `application-prod.yml` | 生产环境配置模板 | ❌ 否（已在.gitignore） |
| `application.yml.template` | 配置模板参考 | ✅ 是 |

## 安全建议

1. **生产环境必须**：
   - 修改所有默认密码
   - 使用强密码（至少16位，包含大小写字母、数字、特殊字符）
   - JWT密钥至少32位随机字符串
   - 启用数据库SSL连接

2. **敏感信息管理**：
   - 使用环境变量或密钥管理服务
   - 不要在代码中硬编码密码
   - 不要将生产配置提交到Git

3. **网络安全**：
   - 数据库、Redis、RabbitMQ不要暴露公网
   - 使用VPC或内网通信
   - 配置防火墙规则

## 常见问题

### Q: 如何查看当前使用的配置？

A: 启动时查看日志，会显示 `active profiles: xxx`

### Q: 配置优先级是什么？

A: `环境变量 > application-{profile}.yml > application.yml`

### Q: 如何验证配置是否生效？

A: 使用 `/actuator/env` 端点查看运行时配置（需要开启actuator）
