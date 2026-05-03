#!/bin/bash

# 数据资产安全系统 - 前端停止脚本
# 用途：停止前端服务

echo "=========================================="
echo "  数据资产安全系统 - 停止前端服务"
echo "=========================================="
echo ""

# 查找5173端口的进程
FRONTEND_PID=$(lsof -i :5173 -n -P | grep LISTEN | awk '{print $2}')

if [ -z "$FRONTEND_PID" ]; then
    echo "✅ 没有运行中的前端服务"
    exit 0
fi

# 获取进程信息
PROCESS_INFO=$(ps -p $FRONTEND_PID -o command= 2>/dev/null)

echo ">>> 发现运行中的前端服务："
echo "  - 进程ID: $FRONTEND_PID"
echo "  - 端口: 5173"
echo "  - 进程信息: $PROCESS_INFO"
echo ""

# 停止服务
echo ">>> 停止服务..."
kill $FRONTEND_PID 2>/dev/null

sleep 2

# 检查是否停止成功
if lsof -i :5173 -n -P | grep LISTEN > /dev/null 2>&1; then
    echo ">>> 强制停止..."
    kill -9 $FRONTEND_PID 2>/dev/null
    sleep 1
fi

if lsof -i :5173 -n -P | grep LISTEN > /dev/null 2>&1; then
    echo "❌ 停止失败，请手动停止"
    echo "   kill -9 $FRONTEND_PID"
    exit 1
else
    echo "✅ 前端服务已停止"
    echo ""
    echo "提示："
    echo "  - 启动前端: ./start-frontend.sh"
fi
