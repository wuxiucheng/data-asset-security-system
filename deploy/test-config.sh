#!/bin/bash
# 测试配置加载

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env.deploy"

echo "========================================"
echo "  测试部署配置加载"
echo "========================================"
echo ""

if [ -f "$ENV_FILE" ]; then
    echo "✅ 找到配置文件: $ENV_FILE"
    echo ""
    
    # 加载配置
    while IFS='=' read -r key value; do
        [[ "$key" =~ ^#.*$ ]] && continue
        [[ -z "$key" ]] && continue
        value="${value%\"}"
        value="${value#\"}"
        export "$key=$value"
    done < "$ENV_FILE"
    
    echo "📋 加载的配置:"
    echo "  JVM_OPTS: $JVM_OPTS"
    echo "  SERVER_PORT: $SERVER_PORT"
    echo "  DB_HOST: $DB_HOST"
    echo "  DB_PORT: $DB_PORT"
    echo "  DB_NAME: $DB_NAME"
    echo "  DB_USERNAME: $DB_USERNAME"
    echo "  REDIS_HOST: $REDIS_HOST"
    echo "  RABBITMQ_HOST: $RABBITMQ_HOST"
    echo ""
    
    echo "🔧 测试JVM命令:"
    echo "  java $JVM_OPTS -jar app.jar"
    echo ""
    
    # 验证JVM参数格式
    if [[ "$JVM_OPTS" == -Xms* && "$JVM_OPTS" == *-Xmx* ]]; then
        echo "✅ JVM参数格式正确"
    else
        echo "❌ JVM参数格式错误"
        exit 1
    fi
    
    echo ""
    echo "========================================"
    echo "  ✅ 配置测试通过"
    echo "========================================"
else
    echo "❌ 配置文件不存在: $ENV_FILE"
    exit 1
fi
