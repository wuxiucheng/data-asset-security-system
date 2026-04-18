#!/bin/bash

echo "========================================="
echo "  数据资产安全及分类分级管理系统 "
echo "     【开发模式启动脚本】"
echo "  不打包、不编译、直接运行前后端 "
echo "========================================="
echo ""

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 路径（你的路径不动）
PROJECT_ROOT="/Users/wuxiucheng/分级分类"
BACKEND_DIR="$PROJECT_ROOT/data-asset-security-system/backend"
FRONTEND_DIR="$PROJECT_ROOT/data-asset-security-system/frontend"

# 数据库信息
DB_NAME="data_asset_security"
DB_USER="root"
DB_PASSWORD="1q2w3e4r"
MYSQL_AUTH="-h127.0.0.1 -u$DB_USER -p$DB_PASSWORD"

# 打印函数
success() { echo -e "${GREEN}✓ $1${NC}"; }
info()  { echo -e "${BLUE}ℹ $1${NC}"; }
warn()  { echo -e "${YELLOW}⚠ $1${NC}"; }
error() { echo -e "${RED}✗ $1${NC}"; }

# ==============================
# 1. 创建数据库（必须）
# ==============================
info "检查并创建数据库..."
mysql $MYSQL_AUTH -e "CREATE DATABASE IF NOT EXISTS $DB_NAME DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
success "数据库准备完成"

# ==============================
# 2. 关闭旧服务
# ==============================
info "关闭旧服务..."
pkill -f "java.*data-asset-security" 2>/dev/null
pkill -f "vite" 2>/dev/null
sleep 1

# ==============================
# 3. 启动后端（开发模式！！）
# ==============================
echo ""
info "启动后端服务（开发模式）..."
cd "$BACKEND_DIR" || exit 1
mkdir -p logs

# 重点：不打包！直接启动
nohup mvn spring-boot:run > logs/backend.log 2>&1 &
success "后端开发模式已启动"

# ==============================
# 4. 启动前端
# ==============================
echo ""
info "启动前端服务..."
cd "$FRONTEND_DIR" || exit 1
mkdir -p logs

# 没依赖就装
if [ ! -d "node_modules" ]; then
    warn "安装前端依赖..."
    npm install --registry=https://registry.npmmirror.com
fi

# 启动前端
nohup npm run dev > logs/frontend.log 2>&1 &
success "前端已启动"

# ==============================
# 完成
# ==============================
echo ""
echo "========================================="
echo " 🎉 前后端启动完成！"
echo "========================================="
echo ""
echo " 前端地址：http://localhost:5173"
echo " 后端地址：http://localhost:8080"
echo " 文档地址：http://localhost:8080/doc.html"
echo ""
echo " 查看后端日志：tail -f $BACKEND_DIR/logs/backend.log"
echo " 查看前端日志：tail -f $FRONTEND_DIR/logs/frontend.log"
echo ""