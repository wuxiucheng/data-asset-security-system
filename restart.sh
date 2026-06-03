#!/bin/bash

# 数据资产安全系统 - 重启脚本
# 用途：重启后端服务

echo "=========================================="
echo "  数据资产安全系统 - 重启服务"
echo "=========================================="
echo ""

# 参数检查
BACKEND_TYPE=${1:-"real"}  # 默认重启Spring Boot

if [ "$BACKEND_TYPE" = "mock" ]; then
    echo ">>> 重启Mock后端..."
    ./stop-backend.sh
    echo ""
    ./start-mock.sh
elif [ "$BACKEND_TYPE" = "real" ]; then
    echo ">>> 重启Spring Boot后端..."
    ./stop-backend.sh
    echo ""
    ./start-real.sh
else
    echo "用法: ./restart.sh [mock|real]"
    echo "  mock - 重启Mock后端"
    echo "  real - 重启Spring Boot后端（默认）"
    exit 1
fi
