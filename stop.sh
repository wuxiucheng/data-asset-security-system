#!/bin/bash

# 数据资产安全及分类分级管理系统 - 停止脚本
# 作者: CodeArts代码智能体
# 日期: 2025-06-16

echo "========================================="
echo "数据资产安全及分类分级管理系统"
echo "停止脚本"
echo "========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 函数：打印成功信息
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# 函数：打印错误信息
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# 函数：打印警告信息
print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

# 1. 查找运行中的进程
echo "正在查找运行中的应用进程..."

RUNNING_PROCESSES=$(ps aux | grep "data-asset-security" | grep java | grep -v grep)

if [ -z "$RUNNING_PROCESSES" ]; then
    print_warning "没有找到运行中的应用进程"
    echo ""
    echo "系统当前未运行"
    exit 0
fi

# 显示运行中的进程
echo ""
echo "找到以下运行中的进程："
echo ""
echo "$RUNNING_PROCESSES" | while read -r line; do
    PID=$(echo "$line" | awk '{print $2}')
    echo "  PID: $PID"
    echo "  命令: $line"
    echo ""
done

# 2. 停止进程
echo "正在停止应用进程..."

# 获取所有进程PID
PIDS=$(ps aux | grep "data-asset-security" | grep java | grep -v grep | awk '{print $2}')

# 停止进程
for PID in $PIDS; do
    echo "正在停止进程 PID: $PID"

    # 先尝试优雅停止
    kill $PID 2>/dev/null

    # 等待3秒
    sleep 3

    # 检查进程是否还在运行
    if ps -p $PID > /dev/null 2>&1; then
        print_warning "进程 $PID 未响应，强制停止..."
        kill -9 $PID 2>/dev/null
    fi

    if ! ps -p $PID > /dev/null 2>&1; then
        print_success "进程 $PID 已停止"
    else
        print_error "进程 $PID 停止失败"
    fi
done

# 3. 验证进程是否完全停止
echo ""
echo "正在验证进程状态..."

sleep 2

REMAINING_PROCESSES=$(ps aux | grep "data-asset-security" | grep java | grep -v grep)

if [ -z "$REMAINING_PROCESSES" ]; then
    print_success "所有应用进程已成功停止"
else
    print_error "仍有进程在运行，请手动检查"
    echo ""
    echo "$REMAINING_PROCESSES"
    exit 1
fi

echo ""
echo "========================================="
print_success "系统已成功停止"
echo "========================================="
echo ""
echo "💡 提示："
echo "   - 查看日志: tail -f /Users/wuxiucheng/分级分类/data-asset-security-system/backend/logs/application.log"
echo "   - 重新启动: /Users/wuxiucheng/分级分类/start.sh"
echo ""
