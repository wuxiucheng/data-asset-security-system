#!/usr/bin/env python3
"""
从Mock JSON数据，按MySQL实际表结构生成完整INSERT SQL
"""
import json
import subprocess

with open('/tmp/all-mock-data.json') as f:
    data = json.load(f)

def esc(val):
    if val is None: return 'NULL'
    if isinstance(val, bool): return '1' if val else '0'
    if isinstance(val, (int, float)): return str(val)
    return "'" + str(val).replace("'", "\\'") + "'"

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

def gen_insert(table, field_map, row):
    """生成INSERT语句，只插入表中存在的字段"""
    cols = get_table_columns(table)
    actual_fields = []
    actual_values = []
    for mock_key, db_col in field_map.items():
        if db_col in cols:
            actual_fields.append(db_col)
            val = row.get(mock_key)
            actual_values.append(esc(val))
    return f"INSERT INTO {table} ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});"

sql = []
sql.append("-- 从Mock后端完整同步数据到MySQL（自动生成）")
sql.append("USE data_asset_security;\n")

# 清空所有表
sql.append("SET FOREIGN_KEY_CHECKS = 0;")
for t in ["data_field", "data_asset", "data_grading", "data_classification",
          "grading_standard", "classification_standard", "owner", "department",
          "sys_user_role", "sys_role_permission", "sys_permission", "sys_role", "sys_user"]:
    sql.append(f"DELETE FROM {t};")
sql.append("SET FOREIGN_KEY_CHECKS = 1;\n")

# ========== sys_user ==========
# BCrypt密码: admin123 -> $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
# 123456 -> $2a$10$dXJ3SW6G7P501n3l1E3Kne6v8v8v8v8v8v8v8v8v8v8v8v8v8v8v8
sql.append("-- 系统用户")
# 用Spring Boot的BCrypt加密密码
# 先用明文占位，后面用Spring Boot生成
bcrypt_admin = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi'
bcrypt_user = '$2a$10$dXJ3SW6G7P501n3l1E3Kne6v8v8v8v8v8v8v8v8v8v8v8v8v8v8v8'

for u in data['users']:
    pwd = bcrypt_admin if u['username'] == 'admin' else bcrypt_user
    sql.append(
        f"INSERT INTO sys_user (user_id, username, password, real_name, email, phone, status) "
        f"VALUES ({u['userId']}, {esc(u['username'])}, {esc(pwd)}, "
        f"{esc(u['realName'])}, {esc(u['email'])}, {esc(u['phone'])}, {esc(u['status'])});"
    )

# ========== sys_role ==========
sql.append("\n-- 系统角色")
for r in data['roles']:
    sql.append(
        f"INSERT INTO sys_role (role_id, role_code, role_name, role_description, role_type, sort_order, status) "
        f"VALUES ({r['roleId']}, {esc(r['roleCode'])}, {esc(r['roleName'])}, "
        f"{esc(r.get('roleDescription',''))}, {esc(r.get('roleType',''))}, "
        f"{r['roleId']}, {esc(r['status'])});"
    )

# ========== sys_permission ==========
sql.append("\n-- 系统权限")
perm_cols = get_table_columns('sys_permission')
for p in data['permissions']:
    fields = {
        'permissionId': 'permission_id',
        'permissionCode': 'permission_code',
        'permissionName': 'permission_name',
        'permissionType': 'permission_type',
        'parentId': 'parent_id',
        'sortOrder': 'sort_order',
        'status': 'status',
    }
    # Mock的permissionUrl映射到MySQL的path
    if 'permissionUrl' in p:
        fields['permissionUrl'] = 'path'
    
    actual_fields = []
    actual_values = []
    for mk, dc in fields.items():
        if dc in perm_cols:
            actual_fields.append(dc)
            actual_values.append(esc(p.get(mk)))
    sql.append(f"INSERT INTO sys_permission ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# ========== sys_user_role ==========
sql.append("\n-- 用户角色关联")
ur_cols = get_table_columns('sys_user_role')
for i, ur in enumerate(data['userRoles']):
    sql.append(
        f"INSERT INTO sys_user_role (user_role_id, user_id, role_id) "
        f"VALUES ({i+1}, {ur['userId']}, {ur['roleId']});"
    )

# ========== sys_role_permission ==========
sql.append("\n-- 角色权限关联（admin拥有所有权限）")
rp_cols = get_table_columns('sys_role_permission')
if rp_cols:
    # 给SYSTEM_ADMIN(roleId=1)所有权限
    for i, p in enumerate(data['permissions']):
        actual_fields = []
        actual_values = []
        field_map = {'id': 'id', 'roleId': 'role_id', 'permissionId': 'permission_id'}
        for mk, dc in field_map.items():
            if dc in rp_cols:
                actual_fields.append(dc)
        if 'id' in actual_fields:
            sql.append(f"INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES ({i+1}, 1, {p['permissionId']});")
        else:
            sql.append(f"INSERT INTO sys_role_permission (role_id, permission_id) VALUES (1, {p['permissionId']});")

# ========== department ==========
sql.append("\n-- 部门")
dept_cols = get_table_columns('department')
for d in data['departments']:
    actual_fields = []
    actual_values = []
    field_map = {
        'departmentId': 'department_id',
        'departmentCode': 'department_code',
        'departmentName': 'department_name',
        'parentId': 'parent_id',
        'leaderId': 'leader_id',
        'contactPhone': 'contact_phone',
    }
    for mk, dc in field_map.items():
        if dc in dept_cols:
            actual_fields.append(dc)
            actual_values.append(esc(d.get(mk)))
    if 'sort_order' in dept_cols:
        actual_fields.append('sort_order')
        actual_values.append(esc(d['departmentId']))
    if 'status' in dept_cols:
        actual_fields.append('status')
        actual_values.append(esc(d.get('status', 'ACTIVE')))
    sql.append(f"INSERT INTO department ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# ========== owner ==========
sql.append("\n-- 责任人")
owner_cols = get_table_columns('owner')
for o in data['owners']:
    actual_fields = []
    actual_values = []
    field_map = {
        'ownerId': 'owner_id',
        'employeeNo': 'employee_no',
        'name': 'name',
        'departmentId': 'department_id',
        'position': 'position',
        'contactPhone': 'contact_phone',
        'email': 'email',
        'status': 'status',
    }
    for mk, dc in field_map.items():
        if dc in owner_cols:
            actual_fields.append(dc)
            actual_values.append(esc(o.get(mk)))
    sql.append(f"INSERT INTO owner ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# ========== classification_standard ==========
sql.append("\n-- 分类标准")
cs_cols = get_table_columns('classification_standard')
for s in data['classificationStandards']:
    actual_fields = []
    actual_values = []
    field_map = {
        'standardId': 'standard_id',
        'standardCode': 'standard_code',
        'standardName': 'standard_name',
        'standardDescription': 'standard_description',
        'version': 'version',
        'publishDate': 'publish_date',
        'status': 'status',
    }
    for mk, dc in field_map.items():
        if dc in cs_cols:
            actual_fields.append(dc)
            actual_values.append(esc(s.get(mk)))
    sql.append(f"INSERT INTO classification_standard ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# ========== data_classification ==========
sql.append("\n-- 数据分类")
dc_cols = get_table_columns('data_classification')
for c in data['classifications']:
    actual_fields = []
    actual_values = []
    field_map = {
        'classificationId': 'classification_id',
        'standardId': 'standard_id',
        'classificationCode': 'classification_code',
        'classificationName': 'classification_name',
        'parentId': 'parent_id',
        'level': 'level',
        'status': 'status',
    }
    for mk, dc_name in field_map.items():
        if dc_name in dc_cols:
            actual_fields.append(dc_name)
            actual_values.append(esc(c.get(mk)))
    if 'sort_order' in dc_cols:
        actual_fields.append('sort_order')
        actual_values.append(esc(c['classificationId']))
    sql.append(f"INSERT INTO data_classification ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# ========== grading_standard ==========
sql.append("\n-- 分级标准")
gs_cols = get_table_columns('grading_standard')
for s in data['gradingStandards']:
    actual_fields = []
    actual_values = []
    field_map = {
        'standardId': 'standard_id',
        'standardCode': 'standard_code',
        'standardName': 'standard_name',
        'standardDescription': 'standard_description',
        'version': 'version',
        'publishDate': 'publish_date',
        'status': 'status',
    }
    for mk, dc_name in field_map.items():
        if dc_name in gs_cols:
            actual_fields.append(dc_name)
            actual_values.append(esc(s.get(mk)))
    sql.append(f"INSERT INTO grading_standard ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# ========== data_grading ==========
sql.append("\n-- 数据分级")
dg_cols = get_table_columns('data_grading')
for g in data['gradings']:
    actual_fields = []
    actual_values = []
    field_map = {
        'gradingId': 'grading_id',
        'standardId': 'standard_id',
        'gradingCode': 'grading_code',
        'gradingName': 'grading_name',
        'gradingDescription': 'grading_description',
        'levelValue': 'level_value',
        'status': 'status',
    }
    for mk, dc_name in field_map.items():
        if dc_name in dg_cols:
            actual_fields.append(dc_name)
            actual_values.append(esc(g.get(mk)))
    if 'sort_order' in dg_cols:
        actual_fields.append('sort_order')
        actual_values.append(esc(g['gradingId']))
    sql.append(f"INSERT INTO data_grading ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# ========== data_asset ==========
sql.append("\n-- 数据资产")
da_cols = get_table_columns('data_asset')
for a in data['assets']:
    actual_fields = []
    actual_values = []
    field_map = {
        'assetId': 'asset_id',
        'assetCode': 'asset_code',
        'assetName': 'asset_name',
        'assetType': 'asset_type',
        'systemName': 'system_name',
        'databaseType': 'database_type',
        'databaseName': 'database_name',
        'tableName': 'table_name',
        'departmentId': 'department_id',
        'ownerId': 'owner_id',
        'classificationId': 'classification_id',
        'gradingId': 'grading_id',
        'status': 'status',
    }
    for mk, dc_name in field_map.items():
        if dc_name in da_cols:
            actual_fields.append(dc_name)
            actual_values.append(esc(a.get(mk)))
    sql.append(f"INSERT INTO data_asset ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

# ========== data_field ==========
sql.append("\n-- 数据字段")
df_cols = get_table_columns('data_field')
for f in data['fields']:
    actual_fields = []
    actual_values = []
    field_map = {
        'fieldId': 'field_id',
        'assetId': 'asset_id',
        'fieldName': 'field_name',
        'fieldCode': 'field_code',
        'fieldType': 'field_type',
        'isPrimaryKey': 'is_primary_key',
        'isRequired': 'nullable',
        'classificationId': 'classification_id',
        'gradingId': 'grading_id',
        'status': 'status',
    }
    for mk, dc_name in field_map.items():
        if dc_name in df_cols:
            actual_fields.append(dc_name)
            actual_values.append(esc(f.get(mk)))
    sql.append(f"INSERT INTO data_field ({', '.join(actual_fields)}) VALUES ({', '.join(actual_values)});")

sql.append("\nSELECT '数据同步完成！' AS message;")

output = "\n".join(sql)
path = "/Users/wuxiucheng/分级分类/data-asset-security-system/backend/sync-mock-data.sql"
with open(path, "w") as f:
    f.write(output)

print(f"✅ SQL文件已生成: {path}")
print(f"   共 {len(sql)} 行")
