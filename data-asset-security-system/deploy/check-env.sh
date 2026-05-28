#!/bin/bash
set -euo pipefail

# =========================
# 🔍 服务器环境检查脚本
# =========================

# ===== 远程配置 =====
REMOTE_USER="root"
REMOTE_HOST="47.94.52.217"  # mysql-server-new
REMOTE_PORT="22022"
REMOTE_KEY="$HOME/.ssh/id_ed25519"

echo "🔍 开始检查服务器环境..."
echo "服务器: $REMOTE_HOST:$REMOTE_PORT"
echo ""

# 测试SSH连接
echo "=== 1. SSH连接测试 ==="
if ssh -o ConnectTimeout=10 -o StrictHostKeyChecking=no -i "$REMOTE_KEY" -p "$REMOTE_PORT" "$REMOTE_USER@$REMOTE_HOST" "echo '✅ SSH连接成功'" 2>/dev/null; then
    echo ""
else
    echo "❌ SSH连接失败"
    echo "请检查以下配置:"
    echo "  - 服务器IP: $REMOTE_HOST"
    echo "  - SSH端口: $REMOTE_PORT"
    echo "  - SSH密钥: $REMOTE_KEY"
    exit 1
fi

# 执行环境检查
ssh -i "$REMOTE_KEY" -p "$REMOTE_PORT" "$REMOTE_USER@$REMOTE_HOST" bash <<'EOF'

echo "=== 2. 系统信息 ==="
echo "操作系统: $(cat /etc/os-release | grep PRETTY_NAME | cut -d'=' -f2 | tr -d '\"')"
echo "内核版本: $(uname -r)"
echo "主机名: $(hostname)"
echo ""

echo "=== 3. Java环境 ==="
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1)
    echo "✅ Java已安装: $JAVA_VERSION"
    JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    echo "   JAVA_HOME: $JAVA_HOME"
else
    echo "❌ Java未安装"
    echo "   请安装 Java 17 或更高版本"
fi
echo ""

echo "=== 4. 端口占用情况 ==="
echo "检查常用端口 (8080, 80, 3306, 6379, 5672):"
for port in 8080 80 3306 6379 5672; do
    if netstat -tlnp 2>/dev/null | grep -q ":$port "; then
        echo "  ⚠️  端口 $port 已被占用"
        netstat -tlnp 2>/dev/null | grep ":$port " | awk '{print "     " $0}'
    else
        echo "  ✅ 端口 $port 可用"
    fi
done
echo ""

echo "=== 5. 磁盘空间 ==="
echo "根分区:"
df -h / | tail -1 | awk '{print "  总容量: " $2 "\n  已使用: " $3 " (" $5 ")\n  可用空间: " $4}'
echo ""

echo "=== 6. 内存信息 ==="
free -h | awk '/Mem:/ {print "  总内存: " $2 "\n  已使用: " $3 "\n  可用内存: " $7}'
echo ""

echo "=== 7. 已安装服务 ==="
echo "Nginx:"
if command -v nginx &> /dev/null; then
    echo "  ✅ 已安装: $(nginx -v 2>&1)"
else
    echo "  ❌ 未安装"
fi

echo "MySQL:"
if command -v mysql &> /dev/null; then
    echo "  ✅ 已安装: $(mysql --version)"
else
    echo "  ❌ 未安装"
fi

echo "Redis:"
if command -v redis-server &> /dev/null; then
    echo "  ✅ 已安装: $(redis-server --version)"
else
    echo "  ❌ 未安装"
fi
echo ""

echo "=== 8. 部署目录检查 ==="
DEPLOY_DIR="/root/data-asset-security"
if [ -d "$DEPLOY_DIR" ]; then
    echo "✅ 部署目录已存在: $DEPLOY_DIR"
    echo "目录内容:"
    ls -lh "$DEPLOY_DIR" 2>/dev/null | tail -5
else
    echo "⚠️  部署目录不存在: $DEPLOY_DIR"
    echo "将在部署时自动创建"
fi
echo ""

echo "=== 9. 防火墙状态 ==="
if command -v firewall-cmd &> /dev/null; then
    echo "FirewallD 状态:"
    firewall-cmd --state 2>/dev/null || echo "  未运行"
    echo "开放的端口:"
    firewall-cmd --list-ports 2>/dev/null || echo "  无"
elif command -v ufw &> /dev/null; then
    echo "UFW 状态:"
    ufw status 2>/dev/null | head -5
else
    echo "未检测到防火墙"
fi
echo ""

echo "=== 检查完成 ==="
EOF

echo ""
echo "💡 建议:"
echo "  1. 确保Java 17+已安装"
echo "  2. 确保端口8080和80可用"
echo "  3. 确保有足够的磁盘空间(至少10GB)"
echo "  4. 建议安装Nginx用于前端部署"
