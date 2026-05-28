#!/bin/bash
set -euo pipefail

# =========================
# 🎨 前端智能部署脚本
# =========================

REMOTE_USER="root"
REMOTE_HOST="47.94.52.217"
REMOTE_PORT="22022"
REMOTE_KEY="$HOME/.ssh/id_ed25519"
REMOTE_FRONTEND_PATH="/var/www/data-asset-security"
LOCAL_DIR="$HOME/分级分类/data-asset-security-system/frontend"

# ===== 颜色输出 =====
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# ===== 帮助信息 =====
show_help() {
    echo "用法: $0 [命令]"
    echo ""
    echo "命令:"
    echo "  full         完整部署（构建+上传）"
    echo "  quick        快速部署（仅上传，不构建）"
    echo "  status       查看部署状态"
    echo "  clean        清理旧文件"
    echo "  help         显示帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 full      # 完整部署"
    echo "  $0 quick     # 快速部署（使用已有构建）"
}

# ===== SSH 命令封装 =====
ssh_cmd() {
    ssh -i "$REMOTE_KEY" -p "$REMOTE_PORT" "$REMOTE_USER@$REMOTE_HOST" "$@"
}

# ===== 完整部署 =====
deploy_full() {
    echo -e "${GREEN}📦 完整部署流程${NC}"
    
    # 1. 构建
    echo -e "${YELLOW}1. 构建前端${NC}"
    cd "$LOCAL_DIR"
    npm run build
    
    # 2. 上传
    echo -e "${YELLOW}2. 上传文件${NC}"
    tar -czf frontend-dist.tar.gz dist/
    scp -i "$REMOTE_KEY" -P "$REMOTE_PORT" frontend-dist.tar.gz \
        "$REMOTE_USER@$REMOTE_HOST:/tmp/"
    
    # 3. 解压部署
    echo -e "${YELLOW}3. 部署到服务器${NC}"
    ssh_cmd bash <<'EOF'
set -e
mkdir -p /var/www/data-asset-security
cd /tmp
tar -xzf frontend-dist.tar.gz
rm -rf /var/www/data-asset-security/*
cp -r dist/* /var/www/data-asset-security/
rm -rf dist frontend-dist.tar.gz
echo "✅ 前端部署完成"
EOF
    
    rm -f frontend-dist.tar.gz
    echo -e "${GREEN}✅ 完整部署完成${NC}"
    echo -e "${GREEN}🌐 访问地址: http://47.94.52.217:8081${NC}"
}

# ===== 快速部署 =====
deploy_quick() {
    echo -e "${GREEN}🚀 快速部署流程${NC}"
    
    # 检查 dist 目录
    if [ ! -d "$LOCAL_DIR/dist" ]; then
        echo -e "${RED}❌ dist 目录不存在，请先构建${NC}"
        echo "运行: $0 full"
        return 1
    fi
    
    # 上传
    echo -e "${YELLOW}1. 上传文件${NC}"
    cd "$LOCAL_DIR"
    tar -czf frontend-dist.tar.gz dist/
    scp -i "$REMOTE_KEY" -P "$REMOTE_PORT" frontend-dist.tar.gz \
        "$REMOTE_USER@$REMOTE_HOST:/tmp/"
    
    # 解压部署
    echo -e "${YELLOW}2. 部署到服务器${NC}"
    ssh_cmd bash <<'EOF'
set -e
mkdir -p /var/www/data-asset-security
cd /tmp
tar -xzf frontend-dist.tar.gz
rm -rf /var/www/data-asset-security/*
cp -r dist/* /var/www/data-asset-security/
rm -rf dist frontend-dist.tar.gz
echo "✅ 前端部署完成"
EOF
    
    rm -f frontend-dist.tar.gz
    echo -e "${GREEN}✅ 快速部署完成${NC}"
}

# ===== 查看状态 =====
show_status() {
    echo -e "${GREEN}📊 前端部署状态${NC}"
    ssh_cmd bash <<'EOF'
echo "=== 部署目录 ==="
ls -lh /var/www/data-asset-security/ | head -10

echo ""
echo "=== 文件数量 ==="
find /var/www/data-asset-security -type f | wc -l

echo ""
echo "=== 总大小 ==="
du -sh /var/www/data-asset-security

echo ""
echo "=== Nginx 状态 ==="
systemctl status nginx --no-pager | head -5
EOF
}

# ===== 清理旧文件 =====
clean_old() {
    echo -e "${YELLOW}清理旧文件...${NC}"
    ssh_cmd "rm -rf /var/www/data-asset-security/*"
    echo -e "${GREEN}✅ 清理完成${NC}"
}

# ===== 主逻辑 =====
case "${1:-help}" in
    full)
        deploy_full
        ;;
    quick)
        deploy_quick
        ;;
    status)
        show_status
        ;;
    clean)
        clean_old
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo -e "${RED}未知命令: $1${NC}"
        show_help
        exit 1
        ;;
esac
