#!/bin/bash

# 后端服务重新编译和重启脚本
# 用于应用实体类字段映射修复

echo "=========================================="
echo "后端服务重新编译和重启"
echo "=========================================="
echo ""

BACKEND_DIR="/Users/wuxiucheng/分级分类/data-asset-security-system/backend"
JAR_FILE="$BACKEND_DIR/target/data-asset-security-1.0.0.jar"

# 1. 停止现有服务
echo "1. 停止现有后端服务..."
PID=$(ps aux | grep "data-asset-security-1.0.0.jar" | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    kill $PID
    sleep 3
    echo "   ✅ 已停止进程: $PID"
else
    echo "   ℹ️  没有运行中的服务"
fi

echo ""
echo "2. 重新编译项目..."
echo "   请选择编译方式："
echo "   [1] 使用 Maven (推荐)"
echo "   [2] 使用 IDE (IntelliJ IDEA / Eclipse)"
echo "   [3] 跳过编译，直接启动（仅当已手动编译时选择）"
echo ""
read -p "请选择 (1/2/3): " choice

case $choice in
    1)
        echo ""
        echo "   使用 Maven 编译..."
        cd "$BACKEND_DIR"

        # 检查mvn命令
        if ! command -v mvn &> /dev/null; then
            echo "   ❌ Maven 未安装或不在PATH中"
            echo "   请先安装Maven或使用IDE编译"
            exit 1
        fi

        mvn clean package -DskipTests

        if [ $? -ne 0 ]; then
            echo "   ❌ 编译失败"
            exit 1
        fi

        echo "   ✅ 编译成功"
        ;;

    2)
        echo ""
        echo "   请在IDE中执行以下操作："
        echo "   IntelliJ IDEA: Build → Rebuild Project"
        echo "   Eclipse: Project → Clean → Build Project"
        echo ""
        read -p "   编译完成后按回车继续..."
        ;;

    3)
        echo "   ℹ️  跳过编译步骤"
        ;;

    *)
        echo "   ❌ 无效选择"
        exit 1
        ;;
esac

echo ""
echo "3. 启动后端服务..."

if [ ! -f "$JAR_FILE" ]; then
    echo "   ❌ JAR文件不存在: $JAR_FILE"
    exit 1
fi

cd "$BACKEND_DIR"
nohup java -jar "$JAR_FILE" --spring.profiles.active=dev > /Users/wuxiucheng/分级分类/data-asset-security-system/logs/backend.log 2>&1 &

sleep 5

# 检查是否启动成功
NEW_PID=$(ps aux | grep "data-asset-security-1.0.0.jar" | grep -v grep | awk '{print $2}')
if [ -n "$NEW_PID" ]; then
    echo "   ✅ 后端服务启动成功"
    echo "   进程ID: $NEW_PID"
    echo "   日志文件: /Users/wuxiucheng/分级分类/data-asset-security-system/logs/backend.log"
    echo ""
    echo "4. 测试API..."

    sleep 10

    # 测试登录
    echo "   测试登录API..."
    LOGIN_RESULT=$(curl -s -X POST http://localhost:8080/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"admin123"}')

    if echo "$LOGIN_RESULT" | grep -q '"code":200'; then
        echo "   ✅ 登录API正常"

        # 提取token
        TOKEN=$(echo "$LOGIN_RESULT" | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['accessToken'])" 2>/dev/null)

        if [ -n "$TOKEN" ]; then
            # 测试血缘关系API
            echo "   测试血缘关系API..."
            LINEAGE_RESULT=$(curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/lineage/relation/list)

            if echo "$LINEAGE_RESULT" | grep -q '"code":200'; then
                echo "   ✅ 血缘关系API正常"
            else
                echo "   ❌ 血缘关系API异常"
                echo "   响应: $LINEAGE_RESULT"
            fi

            # 测试生命周期API
            echo "   测试生命周期API..."
            LC_RESULT=$(curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/lifecycle/policy/list)

            if echo "$LC_RESULT" | grep -q '"code":200'; then
                echo "   ✅ 生命周期API正常"
            else
                echo "   ❌ 生命周期API异常"
                echo "   响应: $LC_RESULT"
            fi

            # 测试合规管理API
            echo "   测试合规管理API..."
            COMPLIANCE_RESULT=$(curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/compliance/standard/list)

            if echo "$COMPLIANCE_RESULT" | grep -q '"code":200'; then
                echo "   ✅ 合规管理API正常"
            else
                echo "   ❌ 合规管理API异常"
                echo "   响应: $COMPLIANCE_RESULT"
            fi
        fi
    else
        echo "   ❌ 登录API异常"
        echo "   响应: $LOGIN_RESULT"
    fi
else
    echo "   ❌ 后端服务启动失败"
    echo "   请检查日志: /Users/wuxiucheng/分级分类/data-asset-security-system/logs/backend.log"
fi

echo ""
echo "=========================================="
echo "操作完成"
echo "=========================================="
