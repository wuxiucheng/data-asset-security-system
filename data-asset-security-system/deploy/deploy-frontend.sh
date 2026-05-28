#!/bin/bash
set -euo pipefail

# =========================
# 🚀 一键部署前端到远程服务器
# 构建 → 上传 → 远程解压 → 配置Nginx
# =========================

# ===== 项目路径 =====
LOCAL_DIR="$HOME/分级分类/data-asset-security-system/frontend"
BUILD_DIR="dist"

# ===== 远程配置 =====
REMOTE_USER="root"
REMOTE_HOST="47.94.52.217"  # mysql-server-new
REMOTE_PORT="22022"         # 请修改为实际SSH端口
REMOTE_KEY="$HOME/.ssh/id_ed25519"  # 请修改为实际密钥路径
REMOTE_PATH="/root/data-asset-security"
REMOTE_FRONTEND_PATH="/root/data-asset-security/frontend"

# ===== Step 1: build =====
echo "📦 1. 进入前端目录"
cd "$LOCAL_DIR"

echo "🔧 2. 安装依赖"
npm install

echo "🏗️ 3. 构建生产版本"
npm run build

# ===== Step 2: package =====
echo "📦 4. 打包构建产物"
tar -czf frontend-dist.tar.gz dist/
DIST_SIZE=$(ls -lh frontend-dist.tar.gz | awk '{print $5}')
echo "📊 打包大小: $DIST_SIZE"

# ===== Step 3: upload =====
echo "🚚 5. 上传到服务器"
scp -i "$REMOTE_KEY" -P "$REMOTE_PORT" frontend-dist.tar.gz \
    "$REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH/"

# ===== Step 4: remote deploy =====
echo "🖥️ 6. 远程部署"
ssh -i "$REMOTE_KEY" -p "$REMOTE_PORT" "$REMOTE_USER@$REMOTE_HOST" bash <<EOF
set -euo pipefail

echo "📁 创建前端目录"
mkdir -p $REMOTE_FRONTEND_PATH

echo "📦 解压构建产物"
cd $REMOTE_PATH
tar -xzf frontend-dist.tar.gz
mv dist/* $REMOTE_FRONTEND_PATH/ 2>/dev/null || true
rm -rf dist frontend-dist.tar.gz

echo "✅ 前端部署完成"
echo "🌐 访问地址: http://$REMOTE_HOST"
EOF

echo "✅ 部署完成"
echo "🌐 前端地址: http://$REMOTE_HOST"
