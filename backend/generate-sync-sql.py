#!/usr/bin/env python3
"""
从Mock JSON数据，按MySQL实际表结构生成INSERT SQL
"""
import json
import subprocess

with open('/tmp/mock-data.json') as f:
    data = json.load(f)

def esc(val):
    if val is None: return 'NULL'
    if isinstance(val, bool): return '1' if val else '0'
    if isinstance(val, (int, float)): return str(val)
    return "'" + str(val).replace("'", "\\'") + "'"

# 获取表的实际字段
def get_table_columns(table):
    result = subprocess.run(
        ['mysql', '-uroot', '-p1q2w3e4r', 'data_asset_security', '-e', f'DESCRIBE {table};'],
        capture_output=True, text=True
    )
    cols = []
    for line in result.stdout.strip().split('\n')[1:]:
        col = line.split('\t')[0]
        cols.append(col)
    return cols

sql = []
sql.append("-- 从Mock后端同步数据到MySQL（自动生成）")
sql.append("USE data_asset_security;\n")
sql.append("SET FOREIGN_KEY_CHECKS = 0;")
for t in ["data_field", "data_asset", "data_grading", "data_classification",
          "grading_standard", "classification_standard", "owner", "department"]:
    sql.append(f"DELETE FROM {t};")
sql.append("SET FOREIGN_KEY_CHECKS = 1;\n")

# 部门
cols = get_table_columns('department')
print(f"department字段: {cols}")
sql.append("-- 部门")
for d in data['departments']:
    fields = ['department_id', 'department_code', 'department_name', 'parent_id', 'leader_id', 'contact_phone', 'sort_order', 'status']
    values = [d['departmentId'], d.get('departmentCode'), d.get('departmentName'), d.get('parentId'), d.get('leaderId'), d.get('contactPhone'), d['departmentId'], d.get('status','ACTIVE')]
    # 只保留表中存在的字段
    actual_fields = [f for f in fields if f in cols]
    actual_values = [esc(v) for f, v in zip(fields, values) if f in cols]
    sql.append(f"INSERT INTO department ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# 责任人
cols = get_table_columns('owner')
print(f"owner字段: {cols}")
sql.append("\n-- 责任人")
for o in data['owners']:
    fields = ['owner_id', 'employee_no', 'name', 'department_id', 'position', 'contact_phone', 'email', 'status']
    values = [o['ownerId'], o.get('employeeNo'), o.get('name'), o.get('departmentId'), o.get('position',''), o.get('contactPhone',''), o.get('email',''), o.get('status','ACTIVE')]
    actual_fields = [f for f in fields if f in cols]
    actual_values = [esc(v) for f, v in zip(fields, values) if f in cols]
    sql.append(f"INSERT INTO owner ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# 分类标准
cols = get_table_columns('classification_standard')
print(f"classification_standard字段: {cols}")
sql.append("\n-- 分类标准")
for s in data['classificationStandards']:
    fields = ['standard_id', 'standard_code', 'standard_name', 'standard_description', 'version', 'publish_date', 'status']
    values = [s['standardId'], s.get('standardCode'), s.get('standardName'), s.get('standardDescription',''), s.get('version',''), s.get('publishDate',''), s.get('status','ACTIVE')]
    actual_fields = [f for f in fields if f in cols]
    actual_values = [esc(v) for f, v in zip(fields, values) if f in cols]
    sql.append(f"INSERT INTO classification_standard ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# 分类
cols = get_table_columns('data_classification')
print(f"data_classification字段: {cols}")
sql.append("\n-- 数据分类")
for c in data['classifications']:
    fields = ['classification_id', 'standard_id', 'classification_code', 'classification_name', 'parent_id', 'level', 'sort_order', 'status']
    values = [c['classificationId'], c.get('standardId',1), c.get('classificationCode',''), c.get('classificationName',''), c.get('parentId'), c.get('level',1), c['classificationId'], c.get('status','ACTIVE')]
    actual_fields = [f for f in fields if f in cols]
    actual_values = [esc(v) for f, v in zip(fields, values) if f in cols]
    sql.append(f"INSERT INTO data_classification ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# 分级标准
cols = get_table_columns('grading_standard')
print(f"grading_standard字段: {cols}")
sql.append("\n-- 分级标准")
for s in data['gradingStandards']:
    fields = ['standard_id', 'standard_code', 'standard_name', 'standard_description', 'version', 'publish_date', 'status']
    values = [s['standardId'], s.get('standardCode'), s.get('standardName'), s.get('standardDescription',''), s.get('version',''), s.get('publishDate',''), s.get('status','ACTIVE')]
    actual_fields = [f for f in fields if f in cols]
    actual_values = [esc(v) for f, v in zip(fields, values) if f in cols]
    sql.append(f"INSERT INTO grading_standard ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# 分级
cols = get_table_columns('data_grading')
print(f"data_grading字段: {cols}")
sql.append("\n-- 数据分级")
for g in data['gradings']:
    fields = ['grading_id', 'standard_id', 'grading_code', 'grading_name', 'grading_description', 'level_value', 'sort_order', 'status']
    values = [g['gradingId'], g.get('standardId',1), g.get('gradingCode',''), g.get('gradingName',''), g.get('gradingDescription',''), g.get('levelValue',1), g['gradingId'], g.get('status','ACTIVE')]
    actual_fields = [f for f in fields if f in cols]
    actual_values = [esc(v) for f, v in zip(fields, values) if f in cols]
    sql.append(f"INSERT INTO data_grading ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# 资产
cols = get_table_columns('data_asset')
print(f"data_asset字段: {cols}")
sql.append("\n-- 数据资产")
for a in data['assets']:
    fields = ['asset_id', 'asset_code', 'asset_name', 'asset_type', 'system_name', 'database_type', 'database_name', 'table_name', 'department_id', 'owner_id', 'classification_id', 'grading_id', 'status']
    values = [a['assetId'], a.get('assetCode',''), a.get('assetName',''), a.get('assetType',''), a.get('systemName',''), a.get('databaseType'), a.get('databaseName'), a.get('tableName'), a.get('departmentId'), a.get('ownerId'), a.get('classificationId'), a.get('gradingId'), a.get('status','DRAFT')]
    actual_fields = [f for f in fields if f in cols]
    actual_values = [esc(v) for f, v in zip(fields, values) if f in cols]
    sql.append(f"INSERT INTO data_asset ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# 字段
cols = get_table_columns('data_field')
print(f"data_field字段: {cols}")
sql.append("\n-- 数据字段")
for f in data['fields']:
    fields = ['field_id', 'asset_id', 'field_name', 'field_code', 'field_type', 'is_primary_key', 'nullable', 'classification_id', 'grading_id', 'status']
    values = [f['fieldId'], f['assetId'], f.get('fieldName',''), f.get('fieldCode',''), f.get('fieldType',''), f.get('isPrimaryKey',False), f.get('isRequired',True), f.get('classificationId'), f.get('gradingId'), f.get('status','ACTIVE')]
    actual_fields = [fi for fi in fields if fi in cols]
    actual_values = [esc(v) for fi, v in zip(fields, values) if fi in cols]
    sql.append(f"INSERT INTO data_field ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

sql.append("\nSELECT '数据同步完成！' AS message;")

output = "\n".join(sql)
path = "/Users/wuxiucheng/分级分类/data-asset-security-system/backend/sync-mock-data.sql"
with open(path, "w") as f:
    f.write(output)

print(f"\n✅ SQL文件已生成: {path}")
