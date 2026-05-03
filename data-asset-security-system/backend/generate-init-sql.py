#!/usr/bin/env python3
import mysql.connector

# 连接数据库
conn = mysql.connector.connect(
    host='localhost',
    user='root',
    password='1q2w3e4r',
    database='data_asset_security'
)
cursor = conn.cursor()

# 获取表结构
def get_table_structure(table_name):
    cursor.execute(f"DESCRIBE {table_name}")
    return cursor.fetchall()

# 生成INSERT语句
tables = {
    'department': [
        (1, 'DEPT001', '技术部', 1, 'ACTIVE'),
        (2, 'DEPT002', '业务部', 2, 'ACTIVE'),
        (3, 'DEPT003', '财务部', 3, 'ACTIVE'),
        (4, 'DEPT004', '人事部', 4, 'ACTIVE'),
        (5, 'DEPT005', '市场部', 5, 'ACTIVE'),
    ],
    'owner': [
        (1, 'EMP001', '张三', 1, '技术总监', '13800138001', 'zhangsan@example.com', 'ACTIVE'),
        (2, 'EMP002', '李四', 2, '业务经理', '13800138002', 'lisi@example.com', 'ACTIVE'),
        (3, 'EMP003', '王五', 3, '财务主管', '13800138003', 'wangwu@example.com', 'ACTIVE'),
    ],
}

print("-- 自动生成的初始化SQL")
print("USE data_asset_security;\n")

for table, data in tables.items():
    structure = get_table_structure(table)
    fields = [row[0] for row in structure if not row[2] == 'PRI' or row[5] != 'auto_increment']
    
    print(f"-- {table}")
    for row in data:
        values = []
        for i, val in enumerate(row):
            if val is None:
                values.append('NULL')
            elif isinstance(val, str):
                values.append(f"'{val}'")
            else:
                values.append(str(val))
        
        print(f"INSERT INTO {table} VALUES ({', '.join(values)});")

conn.close()
