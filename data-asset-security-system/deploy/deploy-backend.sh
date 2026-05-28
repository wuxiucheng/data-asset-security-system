#!/bin/bash
set -euo pipefail

# =========================
# 🚀 一键部署后端到远程服务器
# 打包 → 上传 → 远程解压 → 重启服务
# =========================

# ===== 项目路径 =====
LOCAL_DIR="$HOME/分级分类/data-asset-security-system/backend"
BUILD_ARCHIVE="backend-build.tar.gz"

# ===== 远程配置 =====
REMOTE_USER="root"
REMOTE_HOST="47.94.52.27"  # 请修改为实际服务器IP
REMOTE_PORT="22022"         # 请修改为实际SSH端口
REMOTE_KEY="$HOME/.ssh/id_ed25519"  # 请修改为实际密钥路径
REMOTE_PATH="/root/data-asset-security"
REMOTE_BACKEND_PATH="/root/data-asset-security/backend"

# ===== 检查是否只构建 =====
BUILD_ONLY=false
if [ "${1:-}" = "--build-only" ]; then
    BUILD_ONLY=true
fi

# ===== Step 1: build =====
echo "📦 1. 进入后端目录"
cd "$LOCAL_DIR"

echo "🔧 2. Maven 构建"
mvn clean package -DskipTests

# ===== Step 2: package =====
echo "📦 3. 查找JAR文件"
JAR_FILE=$(find target -name "*.jar" -type f | grep -v "\.original" | head -1)
if [ -z "$JAR_FILE" ]; then
    echo "❌ 未找到JAR文件"
    exit 1
fi
JAR_SIZE=$(ls -lh "$JAR_FILE" | awk '{print $5}')
echo "📊 JAR文件: $JAR_FILE ($JAR_SIZE)"

# ===== 如果只构建，到此结束 =====
if [ "$BUILD_ONLY" = true ]; then
    echo "✅ 构建完成（仅构建模式）"
    echo "📄 JAR文件: $LOCAL_DIR/$JAR_FILE"
    exit 0
fi

# ===== Step 3: 测试SSH连接 =====
echo "🔌 4. 测试SSH连接"
if ! ssh -o ConnectTimeout=5 -o StrictHostKeyChecking=no -i "$REMOTE_KEY" -p "$REMOTE_PORT" "$REMOTE_USER@$REMOTE_HOST" "echo 'SSH连接成功'" 2>/dev/null; then
    echo "❌ SSH连接失败"
    echo "   请检查以下配置:"
    echo "   - 服务器IP: $REMOTE_HOST"
    echo "   - SSH端口: $REMOTE_PORT"
    echo "   - SSH密钥: $REMOTE_KEY"
    echo "   - 用户名: $REMOTE_USER"
    echo ""
    echo "💡 提示: 可以使用 --build-only 参数仅进行本地构建"
    exit 1
fi

# ===== Step 4: upload =====
echo "🚚 5. 上传JAR到服务器"
scp -i "$REMOTE_KEY" -P "$REMOTE_PORT" "$JAR_FILE" \
    "$REMOTE_USER@$REMOTE_HOST:$REMOTE_BACKEND_PATH/app.jar"

# ===== Step 5: remote deploy =====
echo "🖥️ 6. 远程部署"
ssh -i "$REMOTE_KEY" -p "$REMOTE_PORT" "$REMOTE_USER@$REMOTE_HOST" bash <<EOF
set -euo pipefail

echo "📁 创建后端目录"
mkdir -p $REMOTE_BACKEND_PATH

echo "🚀 部署后端服务"

# 停止旧服务
if [ -f "$REMOTE_BACKEND_PATH/app.pid" ]; then
    echo "🛑 停止旧服务"
    kill \$(cat $REMOTE_BACKEND_PATH/app.pid) || true
    rm -f $REMOTE_BACKEND_PATH/app.pid
fi

# 启动新服务
echo "🚀 启动新服务"
cd $REMOTE_BACKEND_PATH
nohup java -jar app.jar --spring.profiles.active=prod > app.log 2>&1 &
echo \$! > app.pid

echo "⏳ 等待服务启动"
sleep 10

# 检查服务状态
if ps -p \$(cat app.pid) > /dev/null; then
    echo "✅ 后端服务启动成功"
    echo "📊 PID: \$(cat app.pid)"
else
    echo "❌ 后端服务启动失败"
    echo "📄 查看日志: tail -100 $REMOTE_BACKEND_PATH/app.log"
    exit 1
fi
EOF

echo "✅ 部署完成"
echo "🌐 API地址: http://$REMOTE_HOST:8080"
