#!/bin/bash
set -euo pipefail

# =========================
# 🚀 一键部署全栈应用到远程服务器
# 后端打包 → 前端构建 → 上传 → 远程部署
# =========================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# ===== 远程配置 =====
REMOTE_USER="root"
REMOTE_HOST="47.94.52.27"  # 请修改为实际服务器IP
REMOTE_PORT="22022"         # 请修改为实际SSH端口
REMOTE_KEY="$HOME/.ssh/id_ed25519"  # 请修改为实际密钥路径
REMOTE_PATH="/root/data-asset-security"

echo "=========================================="
echo "  🚀 数据资产安全系统 - 一键部署"
echo "=========================================="
echo ""
echo "📍 目标服务器: $REMOTE_USER@$REMOTE_HOST:$REMOTE_PORT"
echo "📍 部署路径: $REMOTE_PATH"
echo ""

# ===== Step 1: 部署后端 =====
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📦 Step 1/2: 部署后端"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
"$SCRIPT_DIR/deploy-backend.sh"

# ===== Step 2: 部署前端 =====
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📦 Step 2/2: 部署前端"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
"$SCRIPT_DIR/deploy-frontend.sh"

# ===== 完成 =====
echo ""
echo "=========================================="
echo "  ✅ 部署完成"
echo "=========================================="
echo ""
echo "🌐 前端地址: http://$REMOTE_HOST"
echo "🌐 API地址: http://$REMOTE_HOST:8080"
echo "📚 API文档: http://$REMOTE_HOST:8080/api/doc.html"
echo ""
