#!/bin/bash

# 数据资产安全系统 - 统一管理脚本
# 用途：整合所有启动、停止、重启、状态查看功能

PROJECT_DIR="/Users/wuxiucheng/分级分类/data-asset-security-system"
BACKEND_DIR="$PROJECT_DIR/backend"
FRONTEND_DIR="$PROJECT_DIR/frontend"
MYSQL_BIN="/usr/local/mysql/bin"
MAVEN_BIN="/usr/local/apache-maven-3.8.7/bin"
LOG_DIR="$PROJECT_DIR/logs"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印帮助信息
print_help() {
    echo "=========================================="
    echo "  数据资产安全系统 - 统一管理脚本"
    echo "=========================================="
    echo ""
    echo "用法: ./manage.sh <命令> [选项]"
    echo ""
    echo "命令:"
    echo "  start [real|mock] [build]  启动服务"
    echo "                             real: Spring Boot后端（默认）"
    echo "                             mock: Mock后端"
    echo "                             build: 重新编译后端（已修复字段映射）"
    echo ""
    echo "  stop                       停止所有服务"
    echo "  restart [real|mock] [build] 重启服务"
    echo "                             build: 重新编译（推荐）"
    echo "  status                     查看服务状态"
    echo "  logs [backend|frontend]    查看日志"
    echo "  build                      仅编译后端项目"
    echo "  test                       测试所有API端点"
    echo "  help                       显示帮助信息"
    echo ""
    echo "示例:"
    echo "  ./manage.sh start              # 启动Spring Boot后端 + 前端"
    echo "  ./manage.sh start real build   # 重新编译并启动（修复字段映射后）"
    echo "  ./manage.sh start mock         # 启动Mock后端 + 前端"
    echo "  ./manage.sh stop               # 停止所有服务"
    echo "  ./manage.sh restart real build # 重新编译并重启"
    echo "  ./manage.sh status             # 查看状态"
    echo "  ./manage.sh test               # 测试API"
    echo "  ./manage.sh logs backend       # 查看后端日志"
    echo ""
    echo "注意："
    echo "  - 已修复实体类字段映射问题（LineageRelation, LcPolicy, DataStandard等）"
    echo "  - 首次启动或修改实体类后，请使用 'build' 参数重新编译"
    echo ""
}

# 检查MySQL
check_mysql() {
    echo -e "${BLUE}>>> 检查MySQL数据库...${NC}"
    if ! $MYSQL_BIN/mysqladmin ping -uroot -p1q2w3e4r --silent 2>/dev/null; then
        echo -e "${RED}❌ MySQL未运行${NC}"
        return 1
    fi
    echo -e "${GREEN}✅ MySQL运行正常${NC}"
    return 0
}

# 检查数据库
check_database() {
    if ! $MYSQL_BIN/mysql -uroot -p1q2w3e4r -e "USE data_asset_security" 2>/dev/null; then
        echo -e "${YELLOW}>>> 创建数据库...${NC}"
        $MYSQL_BIN/mysql -uroot -p1q2w3e4r -e "CREATE DATABASE data_asset_security CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;" 2>/dev/null
        echo -e "${GREEN}✅ 数据库创建成功${NC}"
    fi
}

# 编译后端
build_backend() {
    echo -e "${BLUE}>>> 编译Spring Boot项目...${NC}"
    cd "$BACKEND_DIR"
    
    if ! $MAVEN_BIN/mvn clean package -DskipTests > /tmp/maven-build.log 2>&1; then
        echo -e "${RED}❌ 编译失败，查看日志: tail -f /tmp/maven-build.log${NC}"
        return 1
    fi
    
    echo -e "${GREEN}✅ 编译成功${NC}"
    return 0
}

# 启动后端
start_backend() {
    local backend_type=$1
    local need_build=$2
    
    echo ""
    echo -e "${BLUE}=========================================="
    echo "  启动后端服务"
    echo -e "==========================================${NC}"
    echo ""
    
    # 停止现有后端
    stop_backend_silent
    
    if [ "$backend_type" = "mock" ]; then
        echo -e "${YELLOW}>>> 启动Mock后端...${NC}"
        cd "$PROJECT_DIR"
        if [ ! -f "mock-backend.js" ]; then
            echo -e "${RED}❌ mock-backend.js 不存在${NC}"
            return 1
        fi
        nohup node mock-backend.js > "$LOG_DIR/mock-backend.log" 2>&1 &
        sleep 3
        echo -e "${GREEN}✅ Mock后端启动成功 (端口: 3000)${NC}"
    else
        # Real backend
        if ! check_mysql; then
            return 1
        fi
        check_database
        
        # 检查是否需要编译
        JAR_FILE="$BACKEND_DIR/target/data-asset-security-1.0.0.jar"
        if [ "$need_build" = "build" ] || [ ! -f "$JAR_FILE" ]; then
            if ! build_backend; then
                return 1
            fi
        fi
        
        echo -e "${YELLOW}>>> 启动Spring Boot应用...${NC}"
        cd "$BACKEND_DIR"
        nohup java -jar "$JAR_FILE" --spring.profiles.active=dev > "$LOG_DIR/backend.log" 2>&1 &
        
        # 等待启动
        echo -e "${YELLOW}>>> 等待应用启动...${NC}"
        for i in {1..30}; do
            if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
                echo -e "${GREEN}✅ Spring Boot启动成功${NC}"
                echo "   - 端口: 8080"
                echo "   - API: http://localhost:8080/api"
                echo "   - 文档: http://localhost:8080/api/doc.html"
                return 0
            fi
            sleep 1
        done
        
        echo -e "${RED}❌ Spring Boot启动超时${NC}"
        return 1
    fi
}

# 启动前端
start_frontend() {
    echo ""
    echo -e "${BLUE}=========================================="
    echo "  启动前端服务"
    echo -e "==========================================${NC}"
    echo ""
    
    # 停止现有前端
    stop_frontend_silent
    
    echo -e "${YELLOW}>>> 启动前端开发服务器...${NC}"
    cd "$FRONTEND_DIR"
    
    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}>>> 安装依赖...${NC}"
        npm install > /dev/null 2>&1
    fi
    
    nohup npm run dev > "$LOG_DIR/frontend.log" 2>&1 &
    
    sleep 5
    echo -e "${GREEN}✅ 前端启动成功${NC}"
    echo "   - 地址: http://localhost:5173"
}

# 停止后端（静默）
stop_backend_silent() {
    lsof -i :8080 -n -P | grep LISTEN | awk '{print $2}' | xargs kill 2>/dev/null
    lsof -i :3000 -n -P | grep LISTEN | awk '{print $2}' | xargs kill 2>/dev/null
    sleep 1
}

# 停止前端（静默）
stop_frontend_silent() {
    lsof -i :5173 -n -P | grep LISTEN | awk '{print $2}' | xargs kill 2>/dev/null
    sleep 1
}

# 停止所有服务
stop_all() {
    echo ""
    echo -e "${BLUE}=========================================="
    echo "  停止所有服务"
    echo -e "==========================================${NC}"
    echo ""
    
    echo -e "${YELLOW}>>> 停止后端服务...${NC}"
    stop_backend_silent
    echo -e "${GREEN}✅ 后端已停止${NC}"
    
    echo -e "${YELLOW}>>> 停止前端服务...${NC}"
    stop_frontend_silent
    echo -e "${GREEN}✅ 前端已停止${NC}"
}

# 查看状态
show_status() {
    echo ""
    echo -e "${BLUE}=========================================="
    echo "  服务状态"
    echo -e "==========================================${NC}"
    echo ""
    
    # 后端状态
    echo "【后端服务】"
    if lsof -i :8080 -n -P | grep LISTEN > /dev/null 2>&1; then
        PID=$(lsof -i :8080 -n -P | grep LISTEN | awk '{print $2}')
        echo -e "  状态: ${GREEN}运行中${NC}"
        echo "  端口: 8080"
        echo "  进程: $PID"
        echo "  类型: Spring Boot"
    elif lsof -i :3000 -n -P | grep LISTEN > /dev/null 2>&1; then
        PID=$(lsof -i :3000 -n -P | grep LISTEN | awk '{print $2}')
        echo -e "  状态: ${GREEN}运行中${NC}"
        echo "  端口: 3000"
        echo "  进程: $PID"
        echo "  类型: Mock"
    else
        echo -e "  状态: ${RED}未运行${NC}"
    fi
    
    echo ""
    
    # 前端状态
    echo "【前端服务】"
    if lsof -i :5173 -n -P | grep LISTEN > /dev/null 2>&1; then
        PID=$(lsof -i :5173 -n -P | grep LISTEN | awk '{print $2}')
        echo -e "  状态: ${GREEN}运行中${NC}"
        echo "  端口: 5173"
        echo "  进程: $PID"
    else
        echo -e "  状态: ${RED}未运行${NC}"
    fi
    
    echo ""
    
    # MySQL状态
    echo "【MySQL数据库】"
    if $MYSQL_BIN/mysqladmin ping -uroot -p1q2w3e4r --silent 2>/dev/null; then
        echo -e "  状态: ${GREEN}运行中${NC}"
    else
        echo -e "  状态: ${RED}未运行${NC}"
    fi
}

# 查看日志
show_logs() {
    local service=$1

    if [ "$service" = "backend" ]; then
        echo -e "${BLUE}>>> 后端日志 (Ctrl+C退出):${NC}"
        tail -f "$LOG_DIR/backend.log"
    elif [ "$service" = "frontend" ]; then
        echo -e "${BLUE}>>> 前端日志 (Ctrl+C退出):${NC}"
        tail -f "$LOG_DIR/frontend.log"
    else
        echo -e "${RED}请指定服务: backend 或 frontend${NC}"
    fi
}

# 测试API
test_apis() {
    echo ""
    echo -e "${BLUE}=========================================="
    echo "  API测试"
    echo -e "==========================================${NC}"
    echo ""

    # 获取token
    echo -e "${YELLOW}>>> 登录获取Token...${NC}"
    LOGIN_RESULT=$(curl -s -X POST http://localhost:8080/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"admin123"}')

    TOKEN=$(echo "$LOGIN_RESULT" | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['accessToken'])" 2>/dev/null)

    if [ -z "$TOKEN" ]; then
        echo -e "${RED}❌ 登录失败，请检查后端服务是否启动${NC}"
        return 1
    fi

    echo -e "${GREEN}✅ 登录成功${NC}"
    echo ""

    # 测试API列表
    SUCCESS=0
    FAIL=0

    APIS=(
        "lineage/relation/list:数据血缘关系"
        "lineage/analysis/list:影响分析"
        "lifecycle/policy/list:生命周期策略"
        "lifecycle/status/list:生命周期状态"
        "compliance/standard/list:数据标准"
        "compliance/clause/list:合规条款"
        "compliance/kpi/list:治理KPI"
    )

    for api_desc in "${APIS[@]}"; do
        IFS=':' read -r api desc <<< "$api_desc"

        RESULT=$(curl -s -H "Authorization: Bearer $TOKEN" "http://localhost:8080/api/$api")
        CODE=$(echo "$RESULT" | python3 -c "import sys, json; print(json.load(sys.stdin)['code'])" 2>/dev/null)

        if [ "$CODE" = "200" ]; then
            echo -e "${GREEN}✅${NC} $desc ($api)"
            SUCCESS=$((SUCCESS + 1))
        else
            echo -e "${RED}❌${NC} $desc ($api) - 错误码: $CODE"
            FAIL=$((FAIL + 1))
        fi
    done

    echo ""
    echo -e "${BLUE}=========================================="
    echo -e "  测试结果: ${GREEN}成功 $SUCCESS 个${NC}"
    if [ $FAIL -gt 0 ]; then
        echo -e "           ${RED}失败 $FAIL 个${NC}"
    fi
    echo -e "==========================================${NC}"

    if [ $FAIL -eq 0 ]; then
        echo ""
        echo -e "${GREEN}🎉 所有API测试通过！${NC}"
        return 0
    else
        return 1
    fi
}

# 主逻辑
main() {
    # 创建日志目录
    mkdir -p "$LOG_DIR"
    
    case "$1" in
        start)
            BACKEND_TYPE=${2:-"real"}
            BUILD_PARAM=${3:-""}
            
            echo ""
            echo -e "${GREEN}=========================================="
            echo "  数据资产安全系统 - 启动服务"
            echo -e "==========================================${NC}"
            
            start_backend "$BACKEND_TYPE" "$BUILD_PARAM"
            start_frontend
            
            echo ""
            echo -e "${GREEN}=========================================="
            echo "  所有服务启动完成"
            echo -e "==========================================${NC}"
            echo ""
            echo "访问地址:"
            echo "  - 前端: http://localhost:5173"
            if [ "$BACKEND_TYPE" = "mock" ]; then
                echo "  - 后端: http://localhost:3000/api"
            else
                echo "  - 后端: http://localhost:8080/api"
                echo "  - 文档: http://localhost:8080/api/doc.html"
            fi
            echo ""
            echo "默认用户: admin / admin123"
            ;;
            
        stop)
            stop_all
            ;;
            
        restart)
            BACKEND_TYPE=${2:-"real"}
            BUILD_PARAM=${3:-""}
            stop_all
            sleep 2
            start_backend "$BACKEND_TYPE" "$BUILD_PARAM"
            start_frontend
            ;;

        status)
            show_status
            ;;

        logs)
            show_logs "$2"
            ;;

        build)
            build_backend
            ;;

        test)
            test_apis
            ;;

        help|--help|-h)
            print_help
            ;;

        *)
            print_help
            exit 1
            ;;
    esac
}

# 执行主逻辑
main "$@"
