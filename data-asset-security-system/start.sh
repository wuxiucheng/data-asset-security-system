#!/bin/bash

# 数据资产安全系统 - 统一启动脚本
# 版本: v1.0.4
# 日期: 2025-06-16

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 项目根目录
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$PROJECT_ROOT/frontend"
BACKEND_DIR="$PROJECT_ROOT/simple-backend"

# PID文件
FRONTEND_PID_FILE="$PROJECT_ROOT/frontend.pid"
BACKEND_PID_FILE="$PROJECT_ROOT/backend.pid"

# 日志文件
FRONTEND_LOG="$PROJECT_ROOT/frontend.log"
BACKEND_LOG="$PROJECT_ROOT/backend.log"

# 打印带颜色的消息
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# 打印标题
print_title() {
    echo ""
    print_message "$BLUE" "╔════════════════════════════════════════════════════════════════╗"
    print_message "$BLUE" "║                                                              ║"
    print_message "$BLUE" "║   数据资产安全及分类分级管理系统 - 启动脚本                  ║"
    print_message "$BLUE" "║   Data Asset Security and Classification Management System   ║"
    print_message "$BLUE" "║                                                              ║"
    print_message "$BLUE" "╚════════════════════════════════════════════════════════════════╝"
    echo ""
}

# 检查端口是否被占用
check_port() {
    local port=$1
    local service=$2
    
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        print_message "$YELLOW" "⚠️  端口 $port 已被占用 ($service)"
        return 1
    fi
    return 0
}

# 检查依赖
check_dependencies() {
    print_message "$YELLOW" "🔍 检查系统依赖..."
    
    # 检查Node.js
    if ! command -v node &> /dev/null; then
        print_message "$RED" "❌ Node.js 未安装，请先安装 Node.js"
        exit 1
    fi
    
    # 检查npm
    if ! command -v npm &> /dev/null; then
        print_message "$RED" "❌ npm 未安装，请先安装 npm"
        exit 1
    fi
    
    print_message "$GREEN" "✅ Node.js 版本: $(node --version)"
    print_message "$GREEN" "✅ npm 版本: $(npm --version)"
}

# 安装前端依赖
install_frontend_deps() {
    print_message "$YELLOW" "📦 安装前端依赖..."
    cd "$FRONTEND_DIR"
    
    if [ ! -d "node_modules" ]; then
        npm install
        print_message "$GREEN" "✅ 前端依赖安装完成"
    else
        print_message "$GREEN" "✅ 前端依赖已存在"
    fi
}

# 安装后端依赖
install_backend_deps() {
    print_message "$YELLOW" "📦 安装后端依赖..."
    cd "$BACKEND_DIR"
    
    if [ ! -d "node_modules" ]; then
        npm install
        print_message "$GREEN" "✅ 后端依赖安装完成"
    else
        print_message "$GREEN" "✅ 后端依赖已存在"
    fi
}

# 启动后端服务
start_backend() {
    print_message "$YELLOW" "🚀 启动后端服务..."
    
    # 检查是否已经在运行
    if [ -f "$BACKEND_PID_FILE" ]; then
        local pid=$(cat "$BACKEND_PID_FILE")
        if ps -p $pid > /dev/null 2>&1; then
            print_message "$YELLOW" "⚠️  后端服务已在运行 (PID: $pid)"
            return 0
        else
            rm "$BACKEND_PID_FILE"
        fi
    fi
    
    # 检查端口
    if ! check_port 8080 "后端服务"; then
        print_message "$RED" "❌ 无法启动后端服务，端口 8080 被占用"
        exit 1
    fi
    
    cd "$BACKEND_DIR"
    nohup node server.js > "$BACKEND_LOG" 2>&1 &
    local pid=$!
    echo $pid > "$BACKEND_PID_FILE"
    
    # 等待服务启动
    sleep 3
    
    if ps -p $pid > /dev/null 2>&1; then
        print_message "$GREEN" "✅ 后端服务启动成功 (PID: $pid)"
        print_message "$GREEN" "📍 后端地址: http://localhost:8080"
    else
        print_message "$RED" "❌ 后端服务启动失败，请查看日志: $BACKEND_LOG"
        exit 1
    fi
}

# 启动前端服务
start_frontend() {
    print_message "$YELLOW" "🚀 启动前端服务..."
    
    # 检查是否已经在运行
    if [ -f "$FRONTEND_PID_FILE" ]; then
        local pid=$(cat "$FRONTEND_PID_FILE")
        if ps -p $pid > /dev/null 2>&1; then
            print_message "$YELLOW" "⚠️  前端服务已在运行 (PID: $pid)"
            return 0
        else
            rm "$FRONTEND_PID_FILE"
        fi
    fi
    
    # 检查端口
    if ! check_port 5173 "前端服务"; then
        print_message "$RED" "❌ 无法启动前端服务，端口 5173 被占用"
        exit 1
    fi
    
    cd "$FRONTEND_DIR"
    nohup npm run dev > "$FRONTEND_LOG" 2>&1 &
    local pid=$!
    echo $pid > "$FRONTEND_PID_FILE"
    
    # 等待服务启动
    sleep 5
    
    if ps -p $pid > /dev/null 2>&1; then
        print_message "$GREEN" "✅ 前端服务启动成功 (PID: $pid)"
        print_message "$GREEN" "📍 前端地址: http://localhost:5173"
    else
        print_message "$RED" "❌ 前端服务启动失败，请查看日志: $FRONTEND_LOG"
        exit 1
    fi
}

# 停止服务
stop_services() {
    print_message "$YELLOW" "🛑 停止服务..."
    
    # 停止前端
    if [ -f "$FRONTEND_PID_FILE" ]; then
        local pid=$(cat "$FRONTEND_PID_FILE")
        if ps -p $pid > /dev/null 2>&1; then
            kill $pid
            print_message "$GREEN" "✅ 前端服务已停止 (PID: $pid)"
        fi
        rm "$FRONTEND_PID_FILE"
    fi
    
    # 停止后端
    if [ -f "$BACKEND_PID_FILE" ]; then
        local pid=$(cat "$BACKEND_PID_FILE")
        if ps -p $pid > /dev/null 2>&1; then
            kill $pid
            print_message "$GREEN" "✅ 后端服务已停止 (PID: $pid)"
        fi
        rm "$BACKEND_PID_FILE"
    fi
}

# 查看状态
check_status() {
    print_message "$YELLOW" "📊 服务状态检查..."
    
    # 检查前端
    if [ -f "$FRONTEND_PID_FILE" ]; then
        local pid=$(cat "$FRONTEND_PID_FILE")
        if ps -p $pid > /dev/null 2>&1; then
            print_message "$GREEN" "✅ 前端服务运行中 (PID: $pid)"
            print_message "$GREEN" "📍 地址: http://localhost:5173"
        else
            print_message "$RED" "❌ 前端服务未运行"
            rm "$FRONTEND_PID_FILE"
        fi
    else
        print_message "$RED" "❌ 前端服务未运行"
    fi
    
    # 检查后端
    if [ -f "$BACKEND_PID_FILE" ]; then
        local pid=$(cat "$BACKEND_PID_FILE")
        if ps -p $pid > /dev/null 2>&1; then
            print_message "$GREEN" "✅ 后端服务运行中 (PID: $pid)"
            print_message "$GREEN" "📍 地址: http://localhost:8080"
        else
            print_message "$RED" "❌ 后端服务未运行"
            rm "$BACKEND_PID_FILE"
        fi
    else
        print_message "$RED" "❌ 后端服务未运行"
    fi
}

# 显示日志
show_logs() {
    local service=$1
    
    if [ "$service" = "frontend" ] || [ "$service" = "all" ]; then
        print_message "$YELLOW" "📋 前端日志:"
        if [ -f "$FRONTEND_LOG" ]; then
            tail -f "$FRONTEND_LOG"
        else
            print_message "$RED" "❌ 前端日志文件不存在"
        fi
    fi
    
    if [ "$service" = "backend" ] || [ "$service" = "all" ]; then
        print_message "$YELLOW" "📋 后端日志:"
        if [ -f "$BACKEND_LOG" ]; then
            tail -f "$BACKEND_LOG"
        else
            print_message "$RED" "❌ 后端日志文件不存在"
        fi
    fi
}

# 显示帮助
show_help() {
    cat << EOF
数据资产安全系统 - 启动脚本

用法: ./start.sh [选项]

选项:
    start       启动所有服务 (默认)
    stop        停止所有服务
    restart     重启所有服务
    status      查看服务状态
    logs        查看所有日志
    logs-frontend 查看前端日志
    logs-backend  查看后端日志
    install     安装依赖
    help        显示此帮助信息

示例:
    ./start.sh           # 启动所有服务
    ./start.sh stop      # 停止所有服务
    ./start.sh status    # 查看服务状态
    ./start.sh logs      # 查看所有日志

默认账号:
    用户名: admin
    密码: admin123

访问地址:
    前端: http://localhost:5173
    后端: http://localhost:8080

EOF
}

# 主函数
main() {
    print_title
    
    case "${1:-start}" in
        start)
            check_dependencies
            install_frontend_deps
            install_backend_deps
            start_backend
            start_frontend
            echo ""
            print_message "$GREEN" "🎉 系统启动完成！"
            print_message "$GREEN" "📍 前端地址: http://localhost:5173"
            print_message "$GREEN" "📍 后端地址: http://localhost:8080"
            print_message "$GREEN" "👤 默认账号: admin / admin123"
            echo ""
            print_message "$YELLOW" "💡 提示: 使用 './start.sh logs' 查看日志"
            print_message "$YELLOW" "💡 提示: 使用 './start.sh stop' 停止服务"
            ;;
        stop)
            stop_services
            print_message "$GREEN" "✅ 所有服务已停止"
            ;;
        restart)
            stop_services
            sleep 2
            check_dependencies
            start_backend
            start_frontend
            print_message "$GREEN" "✅ 服务重启完成"
            ;;
        status)
            check_status
            ;;
        logs)
            show_logs "all"
            ;;
        logs-frontend)
            show_logs "frontend"
            ;;
        logs-backend)
            show_logs "backend"
            ;;
        install)
            check_dependencies
            install_frontend_deps
            install_backend_deps
            print_message "$GREEN" "✅ 依赖安装完成"
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            print_message "$RED" "❌ 未知选项: $1"
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
