#!/bin/bash
set -euo pipefail

# =========================
# 🚀 智能部署脚本 - 支持增量更新
# =========================

# ===== 加载配置文件 =====
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env.deploy"

if [ -f "$ENV_FILE" ]; then
    # 加载配置文件
    set -a
    source "$ENV_FILE"
    set +a
else
    echo "⚠️  配置文件不存在: $ENV_FILE"
    echo "请复制 .env.deploy.example 并修改配置"
    exit 1
fi

# ===== 配置 =====
REMOTE_USER="root"
REMOTE_HOST="47.94.52.217"
REMOTE_PORT="22022"
REMOTE_KEY="$HOME/.ssh/id_ed25519"
REMOTE_BACKEND_PATH="/root/data-asset-security/backend"
LOCAL_DIR="$HOME/分级分类/data-asset-security-system/backend"

# ===== 颜色输出 =====
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# ===== 帮助信息 =====
show_help() {
    echo "用法: $0 [命令] [选项]"
    echo ""
    echo "命令:"
    echo "  full         完整部署（构建+上传+重启）"
    echo "  quick        快速部署（仅上传+重启，不构建）"
    echo "  config       仅更新配置文件"
    echo "  restart      仅重启服务"
    echo "  stop         停止服务"
    echo "  start        启动服务"
    echo "  status       查看服务状态"
    echo "  logs         查看实时日志"
    echo "  help         显示帮助信息"
    echo ""
    echo "选项:"
    echo "  --build      强制重新构建"
    echo "  --no-restart 不重启服务"
    echo ""
    echo "示例:"
    echo "  $0 full              # 完整部署"
    echo "  $0 quick             # 快速部署（使用已有JAR）"
    echo "  $0 config            # 仅更新配置"
    echo "  $0 restart           # 重启服务"
    echo "  $0 logs              # 查看日志"
}

# ===== SSH 命令封装 =====
ssh_cmd() {
    ssh -i "$REMOTE_KEY" -p "$REMOTE_PORT" "$REMOTE_USER@$REMOTE_HOST" "$@"
}

# ===== 检查本地 JAR 是否存在 =====
check_local_jar() {
    JAR_FILE=$(find "$LOCAL_DIR/target" -name "*.jar" -type f 2>/dev/null | grep -v "\.original" | head -1)
    if [ -z "$JAR_FILE" ]; then
        echo -e "${RED}❌ 未找到本地 JAR 文件${NC}"
        echo "请先运行: $0 full"
        return 1
    fi
    echo -e "${GREEN}✅ 找到本地 JAR: $(basename $JAR_FILE) ($(ls -lh $JAR_FILE | awk '{print $5}'))${NC}"
}

# ===== 完整部署 =====
deploy_full() {
    echo -e "${GREEN}📦 完整部署流程${NC}"
    
    # 1. 构建
    echo -e "${YELLOW}1. 构建项目${NC}"
    cd "$LOCAL_DIR"
    mvn clean package -DskipTests
    
    # 2. 上传
    echo -e "${YELLOW}2. 上传 JAR 文件${NC}"
    JAR_FILE=$(find target -name "*.jar" -type f | grep -v "\.original" | head -1)
    scp -i "$REMOTE_KEY" -P "$REMOTE_PORT" "$JAR_FILE" \
        "$REMOTE_USER@$REMOTE_HOST:$REMOTE_BACKEND_PATH/app.jar"
    
    # 3. 重启
    if [ "${1:-}" != "--no-restart" ]; then
        echo -e "${YELLOW}3. 重启服务${NC}"
        restart_service
    fi
    
    echo -e "${GREEN}✅ 完整部署完成${NC}"
}

# ===== 快速部署（不构建）=====
deploy_quick() {
    echo -e "${GREEN}🚀 快速部署流程${NC}"
    
    # 1. 检查本地 JAR
    check_local_jar || return 1
    
    # 2. 上传
    echo -e "${YELLOW}1. 上传 JAR 文件${NC}"
    JAR_FILE=$(find "$LOCAL_DIR/target" -name "*.jar" -type f | grep -v "\.original" | head -1)
    scp -i "$REMOTE_KEY" -P "$REMOTE_PORT" "$JAR_FILE" \
        "$REMOTE_USER@$REMOTE_HOST:$REMOTE_BACKEND_PATH/app.jar"
    
    # 3. 重启
    echo -e "${YELLOW}2. 重启服务${NC}"
    restart_service
    
    echo -e "${GREEN}✅ 快速部署完成${NC}"
}

# ===== 仅更新配置 =====
update_config() {
    echo -e "${GREEN}📝 更新配置文件${NC}"
    
    CONFIG_FILE="$LOCAL_DIR/src/main/resources/application-prod.yml"
    if [ ! -f "$CONFIG_FILE" ]; then
        echo -e "${RED}❌ 配置文件不存在: $CONFIG_FILE${NC}"
        return 1
    fi
    
    echo -e "${YELLOW}上传配置文件...${NC}"
    scp -i "$REMOTE_KEY" -P "$REMOTE_PORT" "$CONFIG_FILE" \
        "$REMOTE_USER@$REMOTE_HOST:$REMOTE_BACKEND_PATH/application-prod.yml"
    
    echo -e "${YELLOW}重启服务...${NC}"
    restart_service
    
    echo -e "${GREEN}✅ 配置更新完成${NC}"
}

# ===== 重启服务 =====
restart_service() {
    echo -e "${YELLOW}重启后端服务...${NC}"
    ssh_cmd bash <<EOF
set -e
cd /root/data-asset-security/backend

# 停止旧服务
if [ -f app.pid ]; then
    OLD_PID=\$(cat app.pid)
    if ps -p \$OLD_PID > /dev/null 2>&1; then
        echo "停止旧服务 (PID: \$OLD_PID)"
        kill \$OLD_PID
        sleep 3
    fi
    rm -f app.pid
fi

# 启动新服务
echo "启动新服务..."
nohup java $JVM_OPTS -jar app.jar \
    --spring.profiles.active=prod \
    --server.port=$SERVER_PORT \
    --spring.datasource.url="jdbc:mysql://$DB_HOST:$DB_PORT/$DB_NAME?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true" \
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
    --flowable.database-schema-update=$FLOWABLE_DATABASE_SCHEMA_UPDATE \
    > app.log 2>&1 &
echo \$! > app.pid

sleep 5

# 检查状态
if ps -p \$(cat app.pid) > /dev/null 2>&1; then
    echo "✅ 服务启动成功 (PID: \$(cat app.pid))"
else
    echo "❌ 服务启动失败"
    tail -50 app.log
    exit 1
fi
EOF
}

# ===== 停止服务 =====
stop_service() {
    echo -e "${YELLOW}停止后端服务...${NC}"
    ssh_cmd bash <<'EOF'
cd /root/data-asset-security/backend
if [ -f app.pid ]; then
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null 2>&1; then
        kill $PID
        echo "✅ 服务已停止 (PID: $PID)"
    else
        echo "⚠️  服务未运行"
    fi
    rm -f app.pid
else
    echo "⚠️  未找到 PID 文件"
fi
EOF
}

# ===== 启动服务 =====
start_service() {
    echo -e "${YELLOW}启动后端服务...${NC}"
    restart_service
}

# ===== 查看状态 =====
show_status() {
    echo -e "${GREEN}📊 服务状态${NC}"
    ssh_cmd bash <<'EOF'
cd /root/data-asset-security/backend

echo "=== 进程状态 ==="
if [ -f app.pid ]; then
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null 2>&1; then
        echo "✅ 服务运行中 (PID: $PID)"
        ps -p $PID -o pid,ppid,%cpu,%mem,etime,cmd
    else
        echo "❌ 服务未运行"
    fi
else
    echo "⚠️  未找到 PID 文件"
fi

echo ""
echo "=== 端口监听 ==="
netstat -tlnp 2>/dev/null | grep :8082 || echo "端口 8082 未监听"

echo ""
echo "=== 内存使用 ==="
if [ -f app.pid ] && ps -p $(cat app.pid) > /dev/null 2>&1; then
    ps -p $(cat app.pid) -o %mem,rss | tail -1 | awk '{print "内存使用: " $1 "% (" $2/1024 " MB)"}'
fi

echo ""
echo "=== 最近日志 ==="
tail -10 app.log 2>/dev/null || echo "无日志"
EOF
}

# ===== 查看日志 =====
show_logs() {
    echo -e "${GREEN}📄 实时日志 (Ctrl+C 退出)${NC}"
    ssh_cmd "tail -f /root/data-asset-security/backend/app.log"
}

# ===== 主逻辑 =====
case "${1:-help}" in
    full)
        deploy_full "${2:-}"
        ;;
    quick)
        deploy_quick
        ;;
    config)
        update_config
        ;;
    restart)
        restart_service
        ;;
    stop)
        stop_service
        ;;
    start)
        start_service
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo -e "${RED}未知命令: $1${NC}"
        show_help
        exit 1
        ;;
esac
