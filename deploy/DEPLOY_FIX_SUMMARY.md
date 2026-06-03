# 部署配置修复总结

## 问题描述
本地测试没问题，但发布到服务器后出现同样的500错误。

## 根本原因
1. **JVM参数配置不一致**：
   - `.env.deploy`文件中：`JVM_OPTS="-Xms64m -Xmx128m"`（带引号）
   - `deploy-backend.sh`脚本中：硬编码`-Xms256m -Xmx512m`
   
2. **环境变量未正确传递**：
   - `deploy-backend.sh`脚本没有加载`.env.deploy`配置文件
   - 启动命令缺少数据库、Redis、RabbitMQ等配置参数

## 已修复的问题

### 1. 修复.env.deploy中的JVM参数引号
**文件**：`deploy/.env.deploy`

**修改前**：
```bash
JVM_OPTS="-Xms64m -Xmx128m"
```

**修改后**：
```bash
JVM_OPTS=-Xms64m -Xmx128m
```

**原因**：在shell脚本中，引号会导致参数传递错误。

### 2. 修复deploy-backend.sh脚本
**文件**：`deploy/deploy-backend.sh`

**修改内容**：
1. 添加环境配置加载：
```bash
# ===== 加载环境配置 =====
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env.deploy"

if [ -f "$ENV_FILE" ]; then
    set -a
    source "$ENV_FILE"
    set +a
else
    echo "⚠️  配置文件不存在: $ENV_FILE"
    exit 1
fi
```

2. 使用环境变量中的端口配置：
```bash
APP_PORT="${SERVER_PORT:-8082}"  # 从环境变量读取
```

3. 完善启动命令，传递所有必要参数：
```bash
nohup java $JVM_OPTS -jar app.jar \
    --spring.profiles.active=prod \
    --server.port=$APP_PORT \
    --spring.datasource.url="jdbc:mysql://$DB_HOST:$DB_PORT/$DB_NAME?..." \
    --spring.datasource.username="$DB_USERNAME" \
    --spring.datasource.password="$DB_PASSWORD" \
    --spring.data.redis.host="$REDIS_HOST" \
    --spring.data.redis.port="$REDIS_PORT" \
    --spring.data.redis.password="$REDIS_PASSWORD" \
    --spring.rabbitmq.host="$RABBITMQ_HOST" \
    --spring.rabbitmq.port="$RABBITMQ_PORT" \
    --spring.rabbitmq.username="$RABBITMQ_USERNAME" \
    --spring.rabbitmq.password="$RABBITMQ_PASSWORD" \
    --jwt.secret="$JWT_SECRET" \
    > app.log 2>&1 &
```

## 部署配置说明

### 推荐的部署方式

**方式1：使用backend.sh脚本（推荐）**
```bash
cd /Users/wuxiucheng/分级分类/data-asset-security-system/deploy
./backend.sh full      # 完整部署（构建+上传+重启）
./backend.sh quick     # 快速部署（仅上传+重启）
./backend.sh status    # 查看服务状态
./backend.sh logs      # 查看实时日志
```

**方式2：使用deploy-backend.sh脚本**
```bash
cd /Users/wuxiucheng/分级分类/data-asset-security-system/deploy
./deploy-backend.sh           # 完整部署
./deploy-backend.sh --build-only  # 仅构建
```

### 配置文件说明

**.env.deploy** - 生产环境配置
- 数据库连接信息
- Redis配置
- RabbitMQ配置
- JWT密钥
- JVM参数：`-Xms64m -Xmx128m`（小内存环境）
- 服务端口：`8082`

## 验证部署

### 1. 检查服务状态
```bash
./backend.sh status
```

### 2. 检查JVM参数
```bash
ssh root@47.94.52.217 -p 22022 "ps aux | grep java | grep app.jar"
```
应该看到：`java -Xms64m -Xmx128m -jar app.jar ...`

### 3. 检查端口监听
```bash
ssh root@47.94.52.217 -p 22022 "netstat -tlnp | grep 8082"
```

### 4. 查看日志
```bash
./backend.sh logs
```

## 注意事项

1. **JVM内存设置**：
   - 当前设置：`-Xms64m -Xmx128m`
   - 如果服务器内存充足，可以适当增加
   - 建议生产环境：`-Xms256m -Xmx512m`

2. **端口配置**：
   - 应用端口：`8082`（在.env.deploy中配置）
   - Nginx应该代理到此端口

3. **数据库连接**：
   - 确保MySQL服务正常运行
   - 检查数据库`data_asset_security`是否存在
   - 验证用户名密码是否正确

4. **Redis和RabbitMQ**：
   - 如果不使用，可以在启动命令中移除相关参数
   - 或者在application-prod.yml中禁用自动配置

## 下次部署步骤

1. 修改代码后，重新构建jar包：
```bash
./backend.sh full
```

2. 如果只是配置变更：
```bash
./backend.sh config
```

3. 如果jar包已存在，快速部署：
```bash
./backend.sh quick
```

## 故障排查

如果部署后仍然出现500错误：

1. 查看应用日志：
```bash
./backend.sh logs
```

2. 检查数据库连接：
```bash
ssh root@47.94.52.217 -p 22022 "mysql -u root -p'1Q2w3e4R#' -e 'SELECT 1'"
```

3. 检查API是否可访问：
```bash
curl http://47.94.52.217:8082/api/classificationAssistStatistics/overview
```

4. 检查Controller路径映射：
   - 确保所有Controller都没有`/api`前缀
   - 检查StatisticsController、ClassificationAssistResultController、ClassificationAssistTaskController

## 最新修复（2026-06-02）

### 问题：source .env.deploy报错
**错误信息**：
```
/Users/wuxiucheng/分级分类/data-asset-security-system/deploy/.env.deploy: line 29: -Xmx128m: command not found
```

**原因**：
使用`set -a; source .env.deploy; set +a`时，JVM_OPTS的值`"-Xms64m -Xmx128m"`会被shell尝试执行。

**解决方案**：
修改配置加载方式，不使用`source`命令，而是逐行读取并解析：

```bash
while IFS='=' read -r key value; do
    # 跳过注释和空行
    [[ "$key" =~ ^#.*$ ]] && continue
    [[ -z "$key" ]] && continue
    # 移除value的引号
    value="${value%\"}"
    value="${value#\"}"
    # 导出变量
    export "$key=$value"
done < "$ENV_FILE"
```

**修改的文件**：
1. `deploy/backend.sh` - 修改配置加载方式
2. `deploy/deploy-backend.sh` - 修改配置加载方式
3. `deploy/.env.deploy` - 保持JVM_OPTS带引号：`JVM_OPTS="-Xms64m -Xmx128m"`

**验证**：
```bash
cd /Users/wuxiucheng/分级分类/data-asset-security-system/deploy
./test-config.sh
```

应该看到：
```
✅ JVM参数格式正确
✅ 配置测试通过
```
