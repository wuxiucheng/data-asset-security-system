#!/bin/bash

# 数据资产安全系统 - Mock后端启动脚本
# 用途：启动Mock后端（Node.js + Express，内存存储）

PROJECT_DIR="/Users/wuxiucheng/分级分类/data-asset-security-system"
LOG_FILE="/tmp/mock-backend.log"

echo "=========================================="
echo "  数据资产安全系统 - Mock后端启动"
echo "=========================================="
echo ""

# 停止所有后端服务
echo ">>> 停止现有后端服务..."
lsof -i :8080 -n -P | grep LISTEN | awk '{print $2}' | xargs kill 2>/dev/null
sleep 2

# 检查端口是否释放
if lsof -i :8080 -n -P | grep LISTEN > /dev/null 2>&1; then
    echo "❌ 端口8080仍被占用，请手动停止"
    exit 1
fi

echo "✅ 端口8080已释放"
echo ""

# 启动Mock后端
echo ">>> 启动Mock后端..."
cd "$PROJECT_DIR/simple-backend"

# 检查依赖
if [ ! -d "node_modules" ]; then
    echo ">>> 安装依赖..."
    npm install
fi

# 启动服务
nohup node server.js > "$LOG_FILE" 2>&1 &
MOCK_PID=$!

sleep 3

# 检查启动状态
if lsof -i :8080 -n -P | grep LISTEN > /dev/null 2>&1; then
    echo "✅ Mock后端启动成功"
    echo ""
    echo "服务信息："
    echo "  - 进程ID: $MOCK_PID"
    echo "  - 端口: 8080"
    echo "  - API地址: http://localhost:8080/api"
    echo "  - 日志文件: $LOG_FILE"
    echo ""
    echo "数据存储: 内存（重启后数据丢失）"
    echo "适用场景: 前端开发、API测试、功能演示"
    echo ""
    echo "查看日志: tail -f $LOG_FILE"
    echo "停止服务: ./stop-backend.sh"
else
    echo "❌ Mock后端启动失败"
    echo "查看日志: tail -50 $LOG_FILE"
    exit 1
fi
