#!/usr/bin/env python3
"""
从Spring Boot实体类自动生成CREATE TABLE SQL
"""
import os
import re

BASE = "/Users/wuxiucheng/分级分类/data-asset-security-system/backend/src/main/java/com/dataasset/security/entity"

# Java类型到MySQL类型的映射
TYPE_MAP = {
    'Long': 'BIGINT',
    'Integer': 'INT',
    'String': 'VARCHAR(255)',
    'Boolean': 'TINYINT',
    'LocalDateTime': 'DATETIME',
    'BigDecimal': 'DECIMAL(19,2)',
    'Double': 'DOUBLE',
    'Float': 'FLOAT',
}

def camel_to_snake(name):
    """驼峰转下划线"""
    result = []
    for i, c in enumerate(name):
        if c.isupper() and i > 0:
            result.append('_')
        result.append(c.lower())
    return ''.join(result)

def parse_entity(filepath):
    """解析实体类文件"""
    with open(filepath) as f:
        content = f.read()
    
    # 获取表名
    table_match = re.search(r'@TableName\("(\w+)"\)', content)
    table_name = table_match.group(1) if table_match else None
    if not table_name:
        return None
    
    # 获取字段
    fields = []
    lines = content.split('\n')
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        
        # 检查@TableId
        if '@TableId' in line:
            # 提取列名
            col_match = re.search(r'value\s*=\s*"(\w+)"', line)
            col_name = col_match.group(1) if col_match else None
            # 下一行是字段定义
            i += 1
            while i < len(lines) and not lines[i].strip().startswith('private'):
                i += 1
            if i < len(lines):
                field_match = re.search(r'private\s+(\w+)\s+(\w+)', lines[i].strip())
                if field_match:
                    java_type, java_name = field_match.groups()
                    col = col_name or camel_to_snake(java_name)
                    fields.append({
                        'column': col,
                        'type': TYPE_MAP.get(java_type, 'VARCHAR(255)'),
                        'is_pk': True,
                        'auto_increment': 'AUTO' in line
                    })
        
        # 检查普通字段
        elif line.startswith('private') and 'serialVersionUID' not in line:
            field_match = re.search(r'private\s+(\w+)\s+(\w+)', line)
            if field_match:
                java_type, java_name = field_match.groups()
                col = camel_to_snake(java_name)
                mysql_type = TYPE_MAP.get(java_type, 'VARCHAR(255)')
                # 特殊处理：description字段用VARCHAR(500)
                if 'description' in col:
                    mysql_type = 'VARCHAR(500)'
                fields.append({
                    'column': col,
                    'type': mysql_type,
                    'is_pk': False,
                    'auto_increment': False
                })
        
        i += 1
    
    return {'table': table_name, 'fields': fields}

# 解析所有实体类
entities = []
for filename in os.listdir(BASE):
    if filename.endswith('.java') and filename != 'BaseEntity.java':
        filepath = os.path.join(BASE, filename)
        entity = parse_entity(filepath)
        if entity:
            entities.append(entity)
            print(f"  解析: {filename} -> {entity['table']} ({len(entity['fields'])} 字段)")

# 生成建表SQL
sql = []
sql.append("-- 自动从Spring Boot实体类生成建表SQL")
sql.append("USE data_asset_security;\n")
sql.append("SET FOREIGN_KEY_CHECKS = 0;")

for e in entities:
    sql.append(f"DROP TABLE IF EXISTS {e['table']};")

sql.append("SET FOREIGN_KEY_CHECKS = 1;\n")

for e in entities:
    sql.append(f"CREATE TABLE {e['table']} (")
    col_defs = []
    for f in e['fields']:
        col_def = f"  {f['column']} {f['type']}"
        if f['is_pk']:
            col_def += " NOT NULL"
            if f['auto_increment']:
                col_def += " AUTO_INCREMENT"
        elif f['column'] in ('created_time', 'updated_time'):
            col_def += " DEFAULT CURRENT_TIMESTAMP"
        elif f['column'] == 'deleted':
            col_def += " DEFAULT 0"
        elif f['column'] == 'status':
            col_def += " DEFAULT 'ACTIVE'"
        col_defs.append(col_def)
    
    # 主键
    pk_fields = [f for f in e['fields'] if f['is_pk']]
    if pk_fields:
        col_defs.append(f"  PRIMARY KEY ({pk_fields[0]['column']})")
    
    sql.append(",\n".join(col_defs))
    sql.append(");\n")

# 添加外键索引
sql.append("-- 添加索引")
for e in entities:
    for f in e['fields']:
        if f['column'].endswith('_id') and not f['is_pk']:
            idx_name = f"idx_{e['table']}_{f['column']}"
            sql.append(f"CREATE INDEX {idx_name} ON {e['table']}({f['column']});")

sql.append("\nSELECT '建表完成！' AS message;")

output = "\n".join(sql)
path = "/Users/wuxiucheng/分级分类/data-asset-security-system/backend/create-tables-from-entity.sql"
with open(path, "w") as f:
    f.write(output)

print(f"\n✅ 建表SQL已生成: {path}")
