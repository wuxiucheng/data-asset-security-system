#!/bin/bash

# 数据资产安全及分类分级管理系统 - 快速启动脚本（前端+模拟后端）
# 作者: CodeArts代码智能体
# 日期: 2025-06-16

echo "========================================="
echo "数据资产安全及分类分级管理系统"
echo "快速启动脚本（前端+模拟后端）"
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

# 1. 检查前端服务
echo "步骤 1/2: 检查前端服务..."
echo ""

FRONTEND_PROCESS=$(ps aux | grep "vite" | grep node | grep -v grep)

if [ -n "$FRONTEND_PROCESS" ]; then
    print_success "前端服务正在运行"
else
    print_info "前端服务未运行，正在启动..."
    cd "$FRONTEND_DIR" || exit 1

    # 检查依赖是否安装
    if [ ! -d "node_modules" ]; then
        echo "正在安装前端依赖..."
        npm install
    fi

    # 启动前端服务
    mkdir -p logs
    nohup npm run dev > logs/frontend.log 2>&1 &
    
    echo "前端服务正在启动..."
    sleep 5
    
    # 检查是否启动成功
    FRONTEND_PROCESS=$(ps aux | grep "vite" | grep node | grep -v grep)
    if [ -n "$FRONTEND_PROCESS" ]; then
        print_success "前端服务启动成功"
    else
        print_error "前端服务启动失败，请查看日志: $FRONTEND_DIR/logs/frontend.log"
        exit 1
    fi
fi

echo ""

# 2. 检查后端服务
echo "步骤 2/2: 检查后端服务..."
echo ""

BACKEND_PROCESS=$(ps aux | grep "node server.js" | grep -v grep)

if [ -n "$BACKEND_PROCESS" ]; then
    print_success "后端服务正在运行"
else
    print_info "后端服务未运行，正在启动..."
    cd "$BACKEND_DIR" || exit 1

    # 检查依赖是否安装
    if [ ! -d "node_modules" ]; then
        echo "正在安装后端依赖..."
        npm init -y
        npm install express cors
    fi

    # 启动后端服务
    mkdir -p logs
    nohup node server.js > logs/backend.log 2>&1 &
    
    echo "后端服务正在启动..."
    sleep 3
    
    # 检查是否启动成功
    BACKEND_PROCESS=$(ps aux | grep "node server.js" | grep -v grep)
    if [ -n "$BACKEND_PROCESS" ]; then
        print_success "后端服务启动成功"
    else
        print_error "后端服务启动失败，请查看日志: $BACKEND_DIR/logs/backend.log"
        exit 1
    fi
fi

echo ""
echo "========================================="
echo "🎉 系统启动成功！"
echo "========================================="
echo ""
echo "📱 访问地址："
echo "   - 前端界面: http://localhost:5173"
echo "   - 后端API: http://localhost:8080"
echo "   - 健康检查: http://localhost:8080/actuator/health"
echo ""
echo "👤 默认登录信息："
echo "   - 用户名: admin"
echo "   - 密码: admin123"
echo ""
echo "📋 常用命令："
echo "   - 查看前端日志: tail -f $FRONTEND_DIR/logs/frontend.log"
echo "   - 查看后端日志: tail -f $BACKEND_DIR/logs/backend.log"
echo "   - 停止前端: pkill -f vite"
echo "   - 停止后端: pkill -f 'node server.js'"
echo "   - 重启系统: $0"
echo ""
echo "📖 详细文档："
echo "   - 项目总结: $PROJECT_ROOT/PROJECT_SUMMARY.md"
echo "   - 前端总结: $PROJECT_ROOT/FRONTEND_SUMMARY.md"
echo "   - 快速入门: $PROJECT_ROOT/QUICK_START.md"
echo ""
echo "========================================="
echo ""
print_info "提示："
echo "  - 当前使用的是模拟后端服务，用于演示前端功能"
echo "  - 完整的Java后端服务由于Lombok配置问题暂时无法启动"
echo "  - 前端界面功能完整，可以体验所有交互流程"
echo ""
print_success "所有步骤完成！系统已成功启动，可以开始使用了！"
echo ""
