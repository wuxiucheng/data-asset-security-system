#!/bin/bash

# 数据资产安全系统 - 状态检查脚本
# 用途：检查所有服务的运行状态

echo "=========================================="
echo "  数据资产安全系统 - 服务状态"
echo "=========================================="
echo ""

# 检查MySQL
echo ">>> MySQL数据库"
if mysqladmin ping -uroot -p1q2w3e4r --silent 2>/dev/null; then
    echo "  状态: ✅ 运行中"
    MYSQL_VERSION=$(mysql -uroot -p1q2w3e4r -e "SELECT VERSION();" 2>/dev/null | tail -1)
    echo "  版本: $MYSQL_VERSION"
    
    # 检查数据库
    if mysql -uroot -p1q2w3e4r -e "USE data_asset_security" 2>/dev/null; then
        TABLE_COUNT=$(mysql -uroot -p1q2w3e4r data_asset_security -e "SHOW TABLES;" 2>/dev/null | wc -l)
        echo "  数据库: data_asset_security (✅ 存在)"
        echo "  表数量: $((TABLE_COUNT - 1))张"
    else
        echo "  数据库: data_asset_security (❌ 不存在)"
    fi
else
    echo "  状态: ❌ 未运行"
fi
echo ""

# 检查Redis
echo ">>> Redis缓存"
if redis-cli ping 2>/dev/null | grep -q "PONG"; then
    echo "  状态: ✅ 运行中"
    REDIS_INFO=$(redis-cli INFO server 2>/dev/null | grep "redis_version" | cut -d: -f2 | tr -d '\r')
    echo "  版本: $REDIS_INFO"
else
    echo "  状态: ⚠️  未运行（可选）"
fi
echo ""

# 检查RabbitMQ
echo ">>> RabbitMQ消息队列"
if rabbitmqctl status 2>/dev/null | grep -q "running"; then
    echo "  状态: ✅ 运行中"
else
    echo "  状态: ⚠️  未运行（可选）"
fi
echo ""

# 检查后端服务
echo ">>> 后端服务"
BACKEND_PID=$(lsof -i :8080 -n -P | grep LISTEN | awk '{print $2}')

if [ -n "$BACKEND_PID" ]; then
    PROCESS_INFO=$(ps -p $BACKEND_PID -o command= 2>/dev/null)
    
    if echo "$PROCESS_INFO" | grep -q "node server.js"; then
        echo "  类型: Mock后端（Node.js + Express）"
        echo "  状态: ✅ 运行中"
        echo "  进程ID: $BACKEND_PID"
        echo "  端口: 8080"
        echo "  API地址: http://localhost:8080/api"
        echo "  数据存储: 内存（重启丢失）"
        echo "  日志文件: /tmp/mock-backend.log"
    elif echo "$PROCESS_INFO" | grep -q "data-asset-security"; then
        echo "  类型: Spring Boot后端"
        echo "  状态: ✅ 运行中"
        echo "  进程ID: $BACKEND_PID"
        echo "  端口: 8080"
        echo "  API地址: http://localhost:8080/api"
        echo "  API文档: http://localhost:8080/api/doc.html"
        echo "  数据存储: MySQL数据库（持久化）"
        echo "  日志文件: /tmp/spring-boot.log"
    else
        echo "  类型: 未知"
        echo "  状态: ✅ 运行中"
        echo "  进程ID: $BACKEND_PID"
    fi
else
    echo "  状态: ❌ 未运行"
fi
echo ""

# 检查前端服务
echo ">>> 前端服务"
FRONTEND_PID=$(lsof -i :5173 -n -P | grep LISTEN | awk '{print $2}')

if [ -n "$FRONTEND_PID" ]; then
    echo "  状态: ✅ 运行中"
    echo "  进程ID: $FRONTEND_PID"
    echo "  端口: 5173"
    echo "  访问地址: http://localhost:5173"
    echo "  技术栈: Vue3 + Element Plus + Vite"
    echo "  日志文件: /tmp/frontend.log"
else
    echo "  状态: ❌ 未运行"
fi
echo ""

# 总结
echo "=========================================="
echo "  快速操作"
echo "=========================================="
echo ""
echo "一键操作："
echo "  ./start-all.sh       # 启动所有服务（Spring Boot + 前端）"
echo "  ./start-all.sh mock  # 启动所有服务（Mock + 前端）"
echo "  ./stop-all.sh        # 停止所有服务"
echo ""
echo "单独操作："
echo "  ./start-frontend.sh  # 启动前端"
echo "  ./stop-frontend.sh   # 停止前端"
echo "  ./start-mock.sh      # 启动Mock后端"
echo "  ./start-real.sh      # 启动Spring Boot后端"
echo "  ./stop-backend.sh    # 停止后端"
echo ""
echo "其他操作："
echo "  ./status.sh          # 查看服务状态"
echo "  ./restart.sh         # 重启后端服务"
echo ""
