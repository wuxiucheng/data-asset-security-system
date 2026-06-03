#!/bin/bash

# 数据库表结构修复脚本
# 用于修复因表结构与实体类不匹配导致的500错误

echo "=========================================="
echo "数据库表结构修复脚本"
echo "=========================================="
echo ""

# 数据库连接信息
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="data_asset_security"
DB_USER="root"
DB_PASS="1q2w3e4r"

# SQL文件路径
SQL_FILE="/Users/wuxiucheng/分级分类/data-asset-security-system/backend/src/main/resources/db/init.sql"

echo "即将执行以下操作："
echo "1. 连接到MySQL数据库: $DB_NAME"
echo "2. 执行初始化SQL文件: $SQL_FILE"
echo "   - 创建缺失的表（lineage_relation, impact_analysis, metadata_version等）"
echo "   - 添加缺失的字段（transformation, path, confidence等）"
echo ""

read -p "是否继续？(y/n): " confirm

if [ "$confirm" != "y" ]; then
    echo "操作已取消"
    exit 0
fi

echo ""
echo "开始执行SQL..."

# 执行SQL
mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" < "$SQL_FILE"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 数据库表结构修复成功！"
    echo ""
    echo "修复内容："
    echo "  - lineage_relation 表（包含 transformation, path, confidence, source 字段）"
    echo "  - impact_analysis 表"
    echo "  - metadata_version 表"
    echo "  - lc_policy 表（包含 archive_action, delete_action, schedule_cron 字段）"
    echo "  - lc_status 表"
    echo "  - data_standard 表（包含 definition, constraints, example 字段）"
    echo "  - compliance_clause 表"
    echo "  - governance_kpi 表"
    echo ""
    echo "请重启后端服务以使更改生效："
    echo "  cd /Users/wuxiucheng/分级分类/data-asset-security-system/backend"
    echo "  ./restart.sh"
else
    echo ""
    echo "❌ 执行失败，请检查错误信息"
fi
