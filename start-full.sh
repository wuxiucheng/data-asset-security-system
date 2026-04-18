#!/bin/bash

# 数据资产安全及分类分级管理系统 - 完整启动脚本（前端+后端）
# 作者: CodeArts代码智能体
# 日期: 2025-06-16

echo "========================================="
echo "数据资产安全及分类分级管理系统"
echo "完整启动脚本（前端+后端）"
echo "========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 项目路径（你的路径不变）
PROJECT_ROOT="/Users/wuxiucheng/分级分类"
BACKEND_DIR="$PROJECT_ROOT/data-asset-security-system/backend"
FRONTEND_DIR="$PROJECT_ROOT/data-asset-security-system/frontend"

# 数据库配置（你原来的）
DB_NAME="data_asset_security"
DB_USER="root"
DB_PASSWORD="1q2w3e4r"
DB_HOST="127.0.0.1"

# 函数：打印信息
print_success() { echo -e "${GREEN}✓ $1${NC}"; }
print_error() { echo -e "${RED}✗ $1${NC}"; }
print_warning() { echo -e "${YELLOW}⚠ $1${NC}"; }
print_info() { echo -e "${BLUE}ℹ $1${NC}"; }

# 函数：检查命令
check_command() {
    if ! command -v $1 &> /dev/null; then return 1; fi
    return 0
}

# 构建 MySQL 命令（带密码，修复核心问题）
MYSQL_AUTH="-h$DB_HOST -u$DB_USER"
if [ -n "$DB_PASSWORD" ]; then
  MYSQL_AUTH="$MYSQL_AUTH -p$DB_PASSWORD"
fi

# ==============================================
# 1. 检查环境
# ==============================================
echo "步骤 1/8: 检查运行环境..."
echo ""

# 检查Java
if check_command java; then
    JAVA_VERSION=$(java -version 2>&1 | head -1)
    print_success "Java已安装: $JAVA_VERSION"
else
    print_error "Java未安装，请先安装JDK 17+"
    exit 1
fi

# 检查Maven
if check_command mvn; then
    MVN_VERSION=$(mvn -version 2>&1 | head -1)
    print_success "Maven已安装: $MVN_VERSION"
else
    print_error "Maven未安装，请先安装Maven 3.6+"
    exit 1
fi

# 检查MySQL
if check_command mysql; then
    MYSQL_VERSION=$(mysql --version 2>&1 | head -1)
    print_success "MySQL已安装: $MYSQL_VERSION"
else
    print_error "MySQL未安装，请先安装MySQL 5.7+"
    exit 1
fi

# 检查Node.js
if check_command node; then
    NODE_VERSION=$(node --version)
    print_success "Node.js已安装: $NODE_VERSION"
else
    print_error "Node.js未安装，请先安装Node.js 16+"
    exit 1
fi

# 检查npm
if check_command npm; then
    NPM_VERSION=$(npm --version)
    print_success "npm已安装: $NPM_VERSION"
else
    print_error "npm未安装"
    exit 1
fi

# 检查Redis
if check_command redis-cli; then
    REDIS_VERSION=$(redis-cli --version)
    print_success "Redis已安装: $REDIS_VERSION"
else
    print_warning "Redis未安装，建议安装Redis 6.0+以获得更好性能"
fi

echo ""

# ==============================================
# 2. 检查数据库（已修复：带密码）
# ==============================================
echo "步骤 2/8: 检查数据库..."
echo ""

DB_EXISTS=$(mysql $MYSQL_AUTH -e "SHOW DATABASES LIKE '$DB_NAME';" 2>/dev/null | grep -c "$DB_NAME")

if [ "$DB_EXISTS" -eq 0 ]; then
    print_warning "数据库 $DB_NAME 不存在，正在创建..."

    mysql $MYSQL_AUTH -e "CREATE DATABASE IF NOT EXISTS $DB_NAME DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

    if [ $? -eq 0 ]; then
        print_success "数据库 $DB_NAME 创建成功"
    else
        print_error "数据库创建失败！"
        echo "请检查：MySQL账号/密码/主机/权限"
        exit 1
    fi
else
    print_success "数据库 $DB_NAME 已存在"
fi

echo ""

# ==============================================
# 3. 初始化数据库（已修复：带密码）
# ==============================================
echo "步骤 3/8: 初始化数据库..."
echo ""

INIT_SQL="$BACKEND_DIR/src/main/resources/db/init.sql"

if [ -f "$INIT_SQL" ]; then
    print_info "正在执行初始化脚本..."
    mysql $MYSQL_AUTH $DB_NAME < "$INIT_SQL"

    if [ $? -eq 0 ]; then
        print_success "数据库初始化成功"
    else
        print_error "数据库初始化失败"
        exit 1
    fi
else
    print_error "找不到初始化脚本: $INIT_SQL"
    exit 1
fi

echo ""

# ==============================================
# 4. 编译后端
# ==============================================
echo "步骤 4/8: 编译后端项目..."
echo ""

cd "$BACKEND_DIR" || exit 1

# 自动创建日志目录
mkdir -p logs

echo "清理中..."
mvn clean -q
if [ $? -ne 0 ]; then print_error "清理失败"; exit 1; fi

echo "编译中..."
mvn compile -q
if [ $? -ne 0 ]; then print_error "编译失败"; exit 1; fi

echo "打包中..."
mvn package -DskipTests -q
if [ $? -ne 0 ]; then print_error "打包失败"; exit 1; fi

print_success "后端打包完成"
echo ""

# ==============================================
# 5. 检查Redis
# ==============================================
echo "步骤 5/8: 检查Redis服务..."
echo ""

if check_command redis-cli; then
    REDIS_PING=$(redis-cli ping 2>/dev/null)
    if [ "$REDIS_PING" = "PONG" ]; then
        print_success "Redis 正常运行"
    else
        print_warning "Redis 未启动，尝试启动..."
        if [[ "$OSTYPE" == "darwin"* ]]; then
            brew services start redis 2>/dev/null
            sleep 2
        fi
    fi
fi

# ==============================================
# 6. 启动后端
# ==============================================
echo "步骤 6/8: 启动后端服务..."
echo ""

JAR_FILE="$BACKEND_DIR/target/data-asset-security-1.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
    print_error "JAR 包不存在: $JAR_FILE"
    exit 1
fi

# 关闭旧进程
pkill -f "data-asset-security" 2>/dev/null
sleep 1

# 启动
nohup java -jar "$JAR_FILE" > "$BACKEND_DIR/logs/application.log" 2>&1 &
print_info "后端启动中，请等待 8 秒..."
sleep 8

# 检查
if ps aux | grep "data-asset-security" | grep -v grep; then
    print_success "后端启动成功！"
else
    print_error "后端启动失败！"
    exit 1
fi

echo ""

# ==============================================
# 7. 前端依赖
# ==============================================
echo "步骤 7/8: 检查前端依赖..."
echo ""

cd "$FRONTEND_DIR" || exit 1
mkdir -p logs

if [ ! -d "node_modules" ]; then
    print_warning "安装前端依赖..."
    npm install
    if [ $? -ne 0 ]; then print_error "前端依赖安装失败"; exit 1; fi
fi

print_success "前端依赖正常"
echo ""

# ==============================================
# 8. 启动前端
# ==============================================
echo "步骤 8/8: 启动前端服务..."
echo ""

pkill -f "vite" 2>/dev/null
sleep 1

nohup npm run dev > "$FRONTEND_DIR/logs/frontend.log" 2>&1 &
print_info "前端启动中，请等待 5 秒..."
sleep 5

if ps aux | grep "vite" | grep -v grep; then
    print_success "前端启动成功！"
else
    print_error "前端启动失败！"
    exit 1
fi

echo ""
echo "========================================="
echo "🎉 系统启动成功！"
echo "========================================="
echo ""
echo "📱 访问地址："
echo "   - 前端界面: http://localhost:5173"
echo "   - 后端API: http://localhost:8080"
echo "   - API文档: http://localhost:8080/doc.html"
echo "   - 健康检查: http://localhost:8080/actuator/health"
echo ""
echo "👤 默认登录信息："
echo "   - 用户名: admin"
echo "   - 密码: admin123"
echo ""
echo "📋 常用命令："
echo "   - 查看后端日志: tail -f $BACKEND_DIR/logs/application.log"
echo "   - 查看前端日志: tail -f $FRONTEND_DIR/logs/frontend.log"
echo "   - 停止后端: pkill -f data-asset-security"
echo "   - 停止前端: pkill -f vite"
echo "   - 重启系统: $0"
echo ""
echo "📖 详细文档："
echo "   - 项目总结: $PROJECT_ROOT/PROJECT_SUMMARY.md"
echo "   - 部署指南: $PROJECT_ROOT/DEPLOYMENT_GUIDE.md"
echo "   - 快速入门: $PROJECT_ROOT/QUICK_START.md"
echo ""
echo "========================================="
echo ""
print_info "提示：首次启动可能需要等待10-30秒，请耐心等待..."
print_info "如果前端无法访问，请检查防火墙设置或端口占用情况"
echo ""
print_success "所有步骤完成！系统已成功启动，可以开始使用了！"
echo ""
