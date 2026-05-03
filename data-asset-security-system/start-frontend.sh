#!/bin/bash

# 数据资产安全系统 - 前端启动脚本
# 用途：启动Vue3前端应用

PROJECT_DIR="/Users/wuxiucheng/分级分类/data-asset-security-system"
FRONTEND_DIR="$PROJECT_DIR/frontend"
LOG_FILE="/tmp/frontend.log"

echo "=========================================="
echo "  数据资产安全系统 - 前端启动"
echo "=========================================="
echo ""

# 检查Node.js
echo ">>> 检查Node.js环境..."
if ! command -v node &> /dev/null; then
    echo "❌ Node.js未安装"
    echo "请先安装Node.js: https://nodejs.org/"
    exit 1
fi

NODE_VERSION=$(node -v)
echo "✅ Node.js版本: $NODE_VERSION"
echo ""

# 检查npm
echo ">>> 检查npm..."
NPM_VERSION=$(npm -v)
echo "✅ npm版本: $NPM_VERSION"
echo ""

# 停止现有前端服务
echo ">>> 停止现有前端服务..."
lsof -i :5173 -n -P | grep LISTEN | awk '{print $2}' | xargs kill 2>/dev/null
sleep 2

# 检查端口是否释放
if lsof -i :5173 -n -P | grep LISTEN > /dev/null 2>&1; then
    echo "❌ 端口5173仍被占用，请手动停止"
    exit 1
fi
echo "✅ 端口5173已释放"
echo ""

# 检查依赖
cd "$FRONTEND_DIR"
if [ ! -d "node_modules" ]; then
    echo ">>> 安装前端依赖..."
    npm install
    if [ $? -ne 0 ]; then
        echo "❌ 依赖安装失败"
        exit 1
    fi
    echo "✅ 依赖安装成功"
    echo ""
fi

# 启动前端
echo ">>> 启动前端应用..."
nohup npm run dev > "$LOG_FILE" 2>&1 &
FRONTEND_PID=$!

echo ">>> 等待应用启动..."
sleep 5

# 检查启动状态
if lsof -i :5173 -n -P | grep LISTEN > /dev/null 2>&1; then
    echo "✅ 前端启动成功"
    echo ""
    echo "服务信息："
    echo "  - 进程ID: $FRONTEND_PID"
    echo "  - 端口: 5173"
    echo "  - 访问地址: http://localhost:5173"
    echo "  - 日志文件: $LOG_FILE"
    echo ""
    echo "技术栈: Vue3 + Element Plus + Vite"
    echo ""
    echo "提示："
    echo "  - 确保后端服务已启动（./start-mock.sh 或 ./start-real.sh）"
    echo "  - 默认用户: admin / admin123"
    echo ""
    echo "查看日志: tail -f $LOG_FILE"
    echo "停止服务: ./stop-frontend.sh"
else
    echo "❌ 前端启动失败"
    echo "查看日志: tail -50 $LOG_FILE"
    exit 1
fi
