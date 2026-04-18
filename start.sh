#!/bin/bash

# 数据资产安全及分类分级管理系统 - 快速启动脚本
# 作者: CodeArts代码智能体
# 日期: 2025-06-16

echo "========================================="
echo "数据资产安全及分类分级管理系统"
echo "快速启动脚本"
echo "========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 项目路径
PROJECT_DIR="/Users/wuxiucheng/分级分类/data-asset-security-system/backend"
DB_NAME="data_asset_security"
DB_USER="root"
DB_PASSWORD="1q2w3e4r"

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

# 函数：检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        return 1
    fi
    return 0
}

# 1. 检查环境
echo "步骤 1/6: 检查运行环境..."
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

# 检查Redis
if check_command redis-cli; then
    REDIS_VERSION=$(redis-cli --version)
    print_success "Redis已安装: $REDIS_VERSION"
else
    print_warning "Redis未安装，建议安装Redis 6.0+以获得更好性能"
fi

echo ""

# 2. 检查数据库
echo "步骤 2/6: 检查数据库..."
echo ""

# 检查数据库是否存在
DB_EXISTS=$(mysql -u $DB_USER -e "SHOW DATABASES LIKE '$DB_NAME';" 2>/dev/null | grep -c "$DB_NAME")

if [ "$DB_EXISTS" -eq 0 ]; then
    print_warning "数据库 $DB_NAME 不存在，正在创建..."

    # 创建数据库
    mysql -u $DB_USER -e "CREATE DATABASE IF NOT EXISTS $DB_NAME DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null

    if [ $? -eq 0 ]; then
        print_success "数据库 $DB_NAME 创建成功"
    else
        print_error "数据库创建失败，请检查MySQL连接"
        exit 1
    fi
else
    print_success "数据库 $DB_NAME 已存在"
fi

echo ""

# 3. 初始化数据库
echo "步骤 3/6: 初始化数据库..."
echo ""

INIT_SQL="$PROJECT_DIR/src/main/resources/db/init.sql"

if [ -f "$INIT_SQL" ]; then
    echo "正在执行数据库初始化脚本..."
    mysql -u $DB_USER $DB_NAME < "$INIT_SQL"

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

# 4. 编译项目
echo "步骤 4/6: 编译项目..."
echo ""

cd "$PROJECT_DIR" || exit 1

echo "正在清理项目..."
mvn clean -q

if [ $? -ne 0 ]; then
    print_error "项目清理失败"
    exit 1
fi

print_success "项目清理完成"

echo "正在编译项目..."
mvn compile -q

if [ $? -ne 0 ]; then
    print_error "项目编译失败"
    exit 1
fi

print_success "项目编译完成"

echo "正在打包项目..."
mvn package -DskipTests -q

if [ $? -ne 0 ]; then
    print_error "项目打包失败"
    exit 1
fi

print_success "项目打包完成"

echo ""

# 5. 检查Redis
echo "步骤 5/6: 检查Redis服务..."
echo ""

if check_command redis-cli; then
    REDIS_PING=$(redis-cli ping 2>/dev/null)

    if [ "$REDIS_PING" == "PONG" ]; then
        print_success "Redis服务运行正常"
    else
        print_warning "Redis服务未运行，尝试启动..."

        # 尝试启动Redis（macOS）
        if [[ "$OSTYPE" == "darwin"* ]]; then
            brew services start redis 2>/dev/null
            sleep 2

            REDIS_PING=$(redis-cli ping 2>/dev/null)
            if [ "$REDIS_PING" == "PONG" ]; then
                print_success "Redis服务启动成功"
            else
                print_warning "Redis服务启动失败，系统将无法使用缓存功能"
            fi
        fi
    fi
else
    print_warning "Redis未安装，系统将无法使用缓存功能"
fi

echo ""

# 6. 启动应用
echo "步骤 6/6: 启动应用..."
echo ""

JAR_FILE="$PROJECT_DIR/target/data-asset-security-1.0.0.jar"

if [ -f "$JAR_FILE" ]; then
    print_success "找到应用包: $JAR_FILE"

    # 检查是否有正在运行的实例
    RUNNING_PROCESS=$(ps aux | grep "data-asset-security" | grep java | grep -v grep)

    if [ -n "$RUNNING_PROCESS" ]; then
        print_warning "检测到正在运行的实例，正在停止..."
        pkill -f "data-asset-security"
        sleep 2
    fi

    echo "正在启动应用..."
    echo "应用日志: $PROJECT_DIR/logs/application.log"
    echo ""

    # 启动应用
    nohup java -jar "$JAR_FILE" > "$PROJECT_DIR/logs/application.log" 2>&1 &

    echo "应用正在启动，请稍候..."
    sleep 5

    # 检查应用是否启动成功
    APP_PROCESS=$(ps aux | grep "data-asset-security" | grep java | grep -v grep)

    if [ -n "$APP_PROCESS" ]; then
        print_success "应用启动成功！"
        echo ""
        echo "========================================="
        echo "🎉 系统启动成功！"
        echo "========================================="
        echo ""
        echo "📱 访问地址："
        echo "   - API文档: http://localhost:8080/doc.html"
        echo "   - 健康检查: http://localhost:8080/actuator/health"
        echo ""
        echo "👤 默认登录信息："
        echo "   - 用户名: admin"
        echo "   - 密码: admin123"
        echo ""
        echo "📋 常用命令："
        echo "   - 查看日志: tail -f $PROJECT_DIR/logs/application.log"
        echo "   - 停止应用: pkill -f data-asset-security"
        echo "   - 重启应用: $0"
        echo ""
        echo "📖 详细文档："
        echo "   - 项目总结: /Users/wuxiucheng/分级分类/PROJECT_SUMMARY.md"
        echo "   - 部署指南: /Users/wuxiucheng/分级分类/DEPLOYMENT_GUIDE.md"
        echo ""
        echo "========================================="
    else
        print_error "应用启动失败，请查看日志: $PROJECT_DIR/logs/application.log"
        exit 1
    fi
else
    print_error "找不到应用包: $JAR_FILE"
    exit 1
fi

echo ""
print_success "所有步骤完成！"
