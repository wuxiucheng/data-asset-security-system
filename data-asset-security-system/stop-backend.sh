#!/bin/bash

# 数据资产安全系统 - 后端停止脚本
# 用途：停止所有后端服务

echo "=========================================="
echo "  数据资产安全系统 - 停止后端服务"
echo "=========================================="
echo ""

# 查找8080端口的进程
BACKEND_PID=$(lsof -i :8080 -n -P | grep LISTEN | awk '{print $2}')

if [ -z "$BACKEND_PID" ]; then
    echo "✅ 没有运行中的后端服务"
    exit 0
fi

# 获取进程信息
PROCESS_INFO=$(ps -p $BACKEND_PID -o command= 2>/dev/null)

echo ">>> 发现运行中的后端服务："
echo "  - 进程ID: $BACKEND_PID"
echo "  - 端口: 8080"
echo "  - 进程信息: $PROCESS_INFO"
echo ""

# 判断后端类型
if echo "$PROCESS_INFO" | grep -q "node server.js"; then
    BACKEND_TYPE="Mock后端（Node.js）"
elif echo "$PROCESS_INFO" | grep -q "data-asset-security"; then
    BACKEND_TYPE="Spring Boot后端"
else
    BACKEND_TYPE="未知后端"
fi

echo ">>> 后端类型: $BACKEND_TYPE"
echo ""

# 停止服务
echo ">>> 停止服务..."
kill $BACKEND_PID 2>/dev/null

sleep 2

# 检查是否停止成功
if lsof -i :8080 -n -P | grep LISTEN > /dev/null 2>&1; then
    echo ">>> 强制停止..."
    kill -9 $BACKEND_PID 2>/dev/null
    sleep 1
fi

if lsof -i :8080 -n -P | grep LISTEN > /dev/null 2>&1; then
    echo "❌ 停止失败，请手动停止"
    echo "   kill -9 $BACKEND_PID"
    exit 1
else
    echo "✅ 后端服务已停止"
    echo ""
    echo "提示："
    echo "  - 启动Mock后端: ./start-mock.sh"
    echo "  - 启动Spring Boot: ./start-real.sh"
fi
