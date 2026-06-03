#!/usr/bin/env python3
"""
直接从Mock server.js源码提取数据，按MySQL表结构生成SQL
"""
import re

MOCK_FILE = "/Users/wuxiucheng/分级分类/data-asset-security-system/simple-backend/server.js"

with open(MOCK_FILE, "r") as f:
    content = f.read()

# 提取Mock数据数组
def extract_mock_array(name, content):
    pattern = rf"const {name} = \[(.*?)\];"
    match = re.search(pattern, content, re.DOTALL)
    if match:
        return match.group(1)
    return ""

# 提取对象列表
def extract_objects(array_str):
    # 简单解析：找到每个 { ... } 块
    objects = []
    depth = 0
    start = -1
    for i, c in enumerate(array_str):
        if c == '{':
            if depth == 0:
                start = i
            depth += 1
        elif c == '}':
            depth -= 1
            if depth == 0 and start >= 0:
                obj_str = array_str[start:i+1]
                obj = {}
                # 解析 key: value 对
                for m in re.finditer(r"(\w+):\s*(?:'([^']*)'|\"([^\"]*)\"|(\d+)|(true|false|null))", obj_str):
                    key = m.group(1)
                    value = m.group(2) or m.group(3) or m.group(4) or m.group(5)
                    obj[key] = value
                objects.append(obj)
                start = -1
    return objects

print("=== 从Mock server.js提取数据 ===\n")

# 提取各数据
departments = extract_objects(extract_mock_array("mockDepartments", content))
owners = extract_objects(extract_mock_array("mockOwners", content))
classifications = extract_objects(extract_mock_array("mockClassifications", content))
gradings = extract_objects(extract_mock_array("mockGradings", content))
assets = extract_objects(extract_mock_array("mockAssets", content))
fields = extract_objects(extract_mock_array("mockFields", content))

print(f"部门: {len(departments)} 条")
print(f"责任人: {len(owners)} 条")
print(f"分类: {len(classifications)} 条")
print(f"分级: {len(gradings)} 条")
print(f"资产: {len(assets)} 条")
print(f"字段: {len(fields)} 条")

# 生成SQL
sql = []
sql.append("-- 从Mock后端同步数据到MySQL（自动生成）")
sql.append("USE data_asset_security;\n")
sql.append("SET FOREIGN_KEY_CHECKS = 0;")
for t in ["data_field", "data_asset", "data_grading", "data_classification",
          "grading_standard", "classification_standard", "owner", "department"]:
    sql.append(f"DELETE FROM {t};")
sql.append("SET FOREIGN_KEY_CHECKS = 1;\n")

# 部门
sql.append("-- 部门")
for d in departments:
    sql.append(
        f"INSERT INTO department (department_id, department_code, department_name, sort_order, status, created_time, updated_time) "
        f"VALUES ({d.get('deptId',0)}, 'DEPT{d.get('deptId',0):03d}', '{d.get('deptName','')}', "
        f"{d.get('deptId',0)}, '{d.get('status','ACTIVE')}', NOW(), NOW());"
    )

# 责任人
sql.append("\n-- 责任人")
for o in owners:
    sql.append(
        f"INSERT INTO owner (owner_id, employee_no, name, department_id, position, contact_phone, email, status, created_time, updated_time) "
        f"VALUES ({o.get('ownerId',0)}, 'EMP{o.get('ownerId',0):03d}', '{o.get('ownerName','')}', "
        f"{o.get('deptId','NULL')}, '{o.get('position','')}', '{o.get('phone','')}', "
        f"'{o.get('email','')}', '{o.get('status','ACTIVE')}', NOW(), NOW());"
    )

# 分类标准
sql.append("\n-- 分类标准")
sql.append(
    "INSERT INTO classification_standard (standard_id, standard_code, standard_name, version, status, created_time, updated_time) "
    "VALUES (1, 'STD001', '企业数据分类标准', '1.0', 'ACTIVE', NOW(), NOW());"
)

# 分类
sql.append("\n-- 数据分类")
for c in classifications:
    pid = c.get('parentId')
    pid_str = str(pid) if pid and pid != 'null' else 'NULL'
    sql.append(
        f"INSERT INTO data_classification (classification_id, standard_id, classification_code, classification_name, classification_description, parent_id, level, sort_order, status, created_time, updated_time) "
        f"VALUES ({c.get('classificationId',0)}, 1, '{c.get('classificationCode','')}', "
        f"'{c.get('classificationName','')}', '{c.get('description','')}', "
        f"{pid_str}, {c.get('level',1)}, {c.get('classificationId',0)}, "
        f"'{c.get('status','ACTIVE')}', NOW(), NOW());"
    )

# 分级标准
sql.append("\n-- 分级标准")
sql.append(
    "INSERT INTO grading_standard (standard_id, standard_code, standard_name, version, status, created_time, updated_time) "
    "VALUES (1, 'STD001', '企业数据分级标准', '1.0', 'ACTIVE', NOW(), NOW());"
)

# 分级
sql.append("\n-- 数据分级")
for g in gradings:
    color = g.get('color', '')
    sql.append(
        f"INSERT INTO data_grading (grading_id, standard_id, grading_code, grading_name, level_value, color_code, status, created_time, updated_time) "
        f"VALUES ({g.get('gradingId',0)}, 1, '{g.get('gradingCode','')}', "
        f"'{g.get('gradingName','')}', {g.get('level',1)}, '{color}', "
        f"'{g.get('status','ACTIVE')}', NOW(), NOW());"
    )

# 资产
sql.append("\n-- 数据资产")
for a in assets:
    db_type = a.get('databaseType')
    db_name = a.get('databaseName')
    tbl_name = a.get('tableName')
    db_type_str = f"'{db_type}'" if db_type and db_type != 'null' else 'NULL'
    db_name_str = f"'{db_name}'" if db_name and db_name != 'null' else 'NULL'
    tbl_name_str = f"'{tbl_name}'" if tbl_name and tbl_name != 'null' else 'NULL'
    desc = a.get('description', '').replace("'", "\\'")
    sql.append(
        f"INSERT INTO data_asset (asset_id, asset_code, asset_name, asset_type, system_name, database_type, database_name, table_name, department_id, owner_id, classification_id, grading_id, status, asset_description, created_time, updated_time) "
        f"VALUES ({a.get('assetId',0)}, '{a.get('assetCode','')}', "
        f"'{a.get('assetName','')}', '{a.get('assetType','')}', "
        f"'{a.get('systemName','')}', {db_type_str}, {db_name_str}, {tbl_name_str}, "
        f"{a.get('departmentId','NULL')}, {a.get('ownerId','NULL')}, "
        f"{a.get('classificationId','NULL')}, {a.get('gradingId','NULL')}, "
        f"'{a.get('status','DRAFT')}', '{desc}', NOW(), NOW());"
    )

# 字段
sql.append("\n-- 数据字段")
for f in fields:
    is_pk = 1 if f.get('isPrimaryKey') in ('1', 'true', 1) else 0
    is_nullable = 1 if f.get('isNullable') in ('1', 'true', 1) else 0
    is_sensitive = 1 if f.get('isSensitive') in ('1', 'true', 1) else 0
    desc = f.get('description', '').replace("'", "\\'")
    sql.append(
        f"INSERT INTO data_field (field_id, asset_id, field_name, field_code, field_type, is_primary_key, nullable, contains_sensitive_data, classification_id, grading_id, field_description, status, created_time, updated_time) "
        f"VALUES ({f.get('fieldId',0)}, {f.get('assetId',0)}, "
        f"'{f.get('fieldName','')}', '{f.get('fieldCode','')}', "
        f"'{f.get('fieldType','')}', {is_pk}, {is_nullable}, {is_sensitive}, "
        f"{f.get('classificationId','NULL')}, {f.get('gradingId','NULL')}, "
        f"'{desc}', 'ACTIVE', NOW(), NOW());"
    )

sql.append("\nSELECT '数据同步完成！' AS message;")

output = "\n".join(sql)
path = "/Users/wuxiucheng/分级分类/data-asset-security-system/backend/sync-mock-data.sql"
with open(path, "w") as f:
    f.write(output)

print(f"\n✅ SQL文件已生成: {path}")
