#!/bin/bash

# 数据资产安全及分类分级管理系统 - 停止脚本（前端+模拟后端）
# 作者: CodeArts代码智能体
# 日期: 2025-06-16

echo "========================================="
echo "数据资产安全及分类分级管理系统"
echo "停止脚本（前端+模拟后端）"
echo "========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 项目路径
PROJECT_ROOT="/Users/wuxiucheng/分级分类"
FRONTEND_DIR="$PROJECT_ROOT/data-asset-security-system/frontend"
BACKEND_DIR="$PROJECT_ROOT/data-asset-security-system/simple-backend"

# 函数：打印成功信息
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# 函数：打印错误信息
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# 函数：打印信息信息
print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

# 1. 停止前端服务
echo "步骤 1/2: 停止前端服务..."
echo ""

FRONTEND_PROCESS=$(ps aux | grep "vite" | grep node | grep -v grep)

if [ -z "$FRONTEND_PROCESS" ]; then
    print_info "没有找到运行中的前端进程"
else
    echo "正在停止前端进程..."
    pkill -f vite
    
    sleep 2
    
    FRONTEND_PROCESS=$(ps aux | grep "vite" | grep node | grep -v grep)
    if [ -z "$FRONTEND_PROCESS" ]; then
        print_success "前端进程已成功停止"
    else
        print_error "前端进程停止失败，请手动检查"
    fi
fi

echo ""

# 2. 停止后端服务
echo "步骤 2/2: 停止后端服务..."
echo ""

BACKEND_PROCESS=$(ps aux | grep "node server.js" | grep -v grep)

if [ -z "$BACKEND_PROCESS" ]; then
    print_info "没有找到运行中的后端进程"
else
    echo "正在停止后端进程..."
    pkill -f "node server.js"
    
    sleep 2
    
    BACKEND_PROCESS=$(ps aux | grep "node server.js" | grep -v grep)
    if [ -z "$BACKEND_PROCESS" ]; then
        print_success "后端进程已成功停止"
    else
        print_error "后端进程停止失败，请手动检查"
    fi
fi

echo ""
echo "========================================="
print_success "系统已成功停止"
echo "========================================="
echo ""
echo "💡 提示："
echo "   - 查看前端日志: tail -f $FRONTEND_DIR/logs/frontend.log"
echo "   - 查看后端日志: tail -f $BACKEND_DIR/logs/backend.log"
echo "   - 重新启动: $PROJECT_ROOT/start-quick.sh"
echo ""
