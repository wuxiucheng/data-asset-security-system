#!/bin/bash

# 数据资产安全系统 - 一键启动所有服务
# 用途：启动前端和后端服务

echo "=========================================="
echo "  数据资产安全系统 - 一键启动"
echo "=========================================="
echo ""

# 参数检查
BACKEND_TYPE=${1:-"real"}  # 默认启动Spring Boot

if [ "$BACKEND_TYPE" = "mock" ]; then
    echo ">>> 启动模式: Mock后端 + 前端"
    echo ""
    
    # 启动Mock后端
    echo ">>> [1/2] 启动Mock后端..."
    ./start-mock.sh
    if [ $? -ne 0 ]; then
        echo "❌ Mock后端启动失败"
        exit 1
    fi
    echo ""
    
elif [ "$BACKEND_TYPE" = "real" ]; then
    echo ">>> 启动模式: Spring Boot后端 + 前端"
    echo ""
    
    # 启动Spring Boot后端
    echo ">>> [1/2] 启动Spring Boot后端..."
    ./start-real.sh
    if [ $? -ne 0 ]; then
        echo "❌ Spring Boot后端启动失败"
        exit 1
    fi
    echo ""
    
else
    echo "用法: ./start-all.sh [mock|real]"
    echo "  mock - 启动Mock后端 + 前端"
    echo "  real - 启动Spring Boot后端 + 前端（默认）"
    exit 1
fi

# 启动前端
echo ">>> [2/2] 启动前端..."
./start-frontend.sh
if [ $? -ne 0 ]; then
    echo "❌ 前端启动失败"
    exit 1
fi

echo ""
echo "=========================================="
echo "  所有服务启动完成"
echo "=========================================="
echo ""
echo "访问地址："
echo "  - 前端应用: http://localhost:5173"
echo "  - 后端API: http://localhost:8080/api"
if [ "$BACKEND_TYPE" = "real" ]; then
    echo "  - API文档: http://localhost:8080/api/doc.html"
fi
echo ""
echo "默认用户: admin / admin123"
echo ""
echo "查看状态: ./status.sh"
echo "停止所有: ./stop-all.sh"
