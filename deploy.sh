#!/bin/bash

# 自动部署脚本
# 用法: ./deploy.sh [环境]
# 环境: dev, test, prod (默认: prod)

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        log_error "$1 未安装，请先安装 $1"
        exit 1
    fi
}

# 主函数
main() {
    # 获取环境参数
    ENV=${1:-prod}
    log_info "部署环境: $ENV"

    # 检查必要命令
    log_info "检查必要命令..."
    check_command docker
    check_command docker-compose
    check_command git

    # 拉取最新代码
    log_info "拉取最新代码..."
    git pull origin main

    # 设置环境变量文件
    ENV_FILE=".env.$ENV"
    if [ ! -f "$ENV_FILE" ]; then
        log_warn "环境文件 $ENV_FILE 不存在，使用默认配置"
        ENV_FILE=".env"
    fi

    # 停止旧容器
    log_info "停止旧容器..."
    docker-compose down || true

    # 构建并启动新容器
    log_info "构建并启动新容器..."
    docker-compose up -d --build

    # 等待服务启动
    log_info "等待服务启动..."
    sleep 30

    # 检查服务状态
    log_info "检查服务状态..."
    docker-compose ps

    # 健康检查
    log_info "执行健康检查..."
    check_health

    log_info "部署完成！"
    log_info "访问地址: http://localhost"
}

# 健康检查
check_health() {
    # 检查后端
    if curl -f http://localhost:8080/api/actuator/health &> /dev/null; then
        log_info "后端服务健康"
    else
        log_warn "后端服务健康检查失败"
    fi

    # 检查前端
    if curl -f http://localhost &> /dev/null; then
        log_info "前端服务健康"
    else
        log_warn "前端服务健康检查失败"
    fi
}

# 回滚函数
rollback() {
    log_warn "开始回滚..."
    git checkout HEAD~1
    docker-compose down
    docker-compose up -d --build
    log_info "回滚完成"
}

# 查看日志
logs() {
    docker-compose logs -f
}

# 帮助信息
help() {
    echo "用法: $0 [命令] [环境]"
    echo ""
    echo "命令:"
    echo "  deploy [env]  部署到指定环境 (默认: prod)"
    echo "  rollback      回滚到上一个版本"
    echo "  logs          查看服务日志"
    echo "  help          显示帮助信息"
    echo ""
    echo "环境:"
    echo "  dev   开发环境"
    echo "  test  测试环境"
    echo "  prod  生产环境"
}

# 根据参数执行不同命令
case "$1" in
    deploy|"")
        main $2
        ;;
    rollback)
        rollback
        ;;
    logs)
        logs
        ;;
    help|--help|-h)
        help
        ;;
    *)
        log_error "未知命令: $1"
        help
        exit 1
        ;;
esac
