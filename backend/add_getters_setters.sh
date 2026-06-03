#!/bin/bash

# 为实体类添加getter/setter方法

for file in src/main/java/com/dataasset/security/entity/*.java; do
    classname=$(basename "$file" .java)
    echo "处理: $classname"
    
    # 提取所有private字段
    fields=$(grep -E "^\s+private\s+\w+\s+\w+\s*;" "$file" | sed 's/.*private\s\+\(\w\+\)\s\+\(\w\+\)\s*;.*/\1 \2/')
    
    if [ -n "$fields" ]; then
        # 创建临时文件
        temp_file=$(mktemp)
        
        # 复制文件内容，但在最后的}之前添加getter/setter
        awk -v fields="$fields" '
        BEGIN {
            # 解析字段
            n = split(fields, field_lines, "\n")
            for (i = 1; i <= n; i++) {
                split(field_lines[i], parts, " ")
                type = parts[1]
                name = parts[2]
                # 首字母大写
                capital_name = toupper(substr(name, 1, 1)) substr(name, 2)
                
                # 生成getter
                print "    public " type " get" capital_name "() {"
                print "        return " name ";"
                print "    }"
                print ""
                
                # 生成setter
                print "    public void set" capital_name "(" type " " name ") {"
                print "        this." name " = " name ";"
                print "    }"
                print ""
            }
        }
        /^}/ {
            # 打印getter/setter方法
            for (i = 1; i <= n; i++) {
                split(field_lines[i], parts, " ")
                type = parts[1]
                name = parts[2]
                capital_name = toupper(substr(name, 1, 1)) substr(name, 2)
                
                print "    public " type " get" capital_name "() {"
                print "        return " name ";"
                print "    }"
                print ""
                
                print "    public void set" capital_name "(" type " " name ") {"
                print "        this." name " = " name ";"
                print "    }"
                print ""
            }
            # 打印原来的}
            print $0
            next
        }
        {
            print $0
        }
        ' "$file" > "$temp_file"
        
        # 替换原文件
        mv "$temp_file" "$file"
        echo "  添加了getter/setter方法"
    fi
done

echo "所有实体类处理完成!"
