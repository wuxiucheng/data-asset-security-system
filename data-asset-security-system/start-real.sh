#!/bin/bash

# 数据资产安全系统 - Spring Boot后端启动脚本
# 用途：启动真实后端（Spring Boot + MySQL，数据持久化）

PROJECT_DIR="/Users/wuxiucheng/分级分类/data-asset-security-system"
BACKEND_DIR="$PROJECT_DIR/backend"
LOG_FILE="/tmp/spring-boot.log"
JAR_FILE="$BACKEND_DIR/target/data-asset-security-1.0.0.jar"

echo "=========================================="
echo "  数据资产安全系统 - Spring Boot启动"
echo "=========================================="
echo ""

# 检查MySQL
echo ">>> 检查MySQL数据库..."
if ! mysqladmin ping -uroot -p1q2w3e4r --silent 2>/dev/null; then
    echo "❌ MySQL未运行，请先启动MySQL"
    echo "   macOS: mysql.server start"
    echo "   Linux: systemctl start mysql"
    exit 1
fi
echo "✅ MySQL运行正常"
echo ""

# 检查数据库是否存在
if ! mysql -uroot -p1q2w3e4r -e "USE data_asset_security" 2>/dev/null; then
    echo "❌ 数据库 data_asset_security 不存在"
    echo ">>> 创建数据库..."
    mysql -uroot -p1q2w3e4r -e "CREATE DATABASE data_asset_security CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
    echo "✅ 数据库创建成功"
fi
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

# 检查是否需要编译
if [ ! -f "$JAR_FILE" ]; then
    echo ">>> 编译Spring Boot项目..."
    cd "$BACKEND_DIR"
    mvn clean package -DskipTests
    
    if [ ! -f "$JAR_FILE" ]; then
        echo "❌ 编译失败"
        exit 1
    fi
    echo "✅ 编译成功"
    echo ""
fi

# 启动Spring Boot
echo ">>> 启动Spring Boot应用..."
cd "$BACKEND_DIR"
nohup java -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
SPRING_PID=$!

echo ">>> 等待应用启动..."
# 轮询等待，最多60秒
MAX_WAIT=60
WAITED=0
while [ $WAITED -lt $MAX_WAIT ]; do
    if lsof -i :8080 -n -P | grep LISTEN > /dev/null 2>&1; then
        break
    fi
    sleep 2
    WAITED=$((WAITED + 2))
done

# 检查启动状态
if lsof -i :8080 -n -P | grep LISTEN > /dev/null 2>&1; then
    echo "✅ Spring Boot启动成功"
    echo ""
    echo "服务信息："
    echo "  - 进程ID: $SPRING_PID"
    echo "  - 端口: 8080"
    echo "  - API地址: http://localhost:8080/api"
    echo "  - API文档: http://localhost:8080/api/doc.html"
    echo "  - 日志文件: $LOG_FILE"
    echo ""
    echo "数据存储: MySQL数据库（数据持久化）"
    echo "适用场景: 生产环境、集成测试、数据持久化"
    echo ""
    echo "默认用户: admin / admin123"
    echo ""
    echo "查看日志: tail -f $LOG_FILE"
    echo "停止服务: ./stop-backend.sh"
else
    echo "❌ Spring Boot启动失败"
    echo "查看日志: tail -50 $LOG_FILE"
    exit 1
fi
