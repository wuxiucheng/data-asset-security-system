#!/bin/bash

# 为实体类添加getter/setter方法的增强脚本

process_entity() {
    local file="$1"
    local classname=$(basename "$file" .java)
    
    echo "处理实体类: $classname"
    
    # 提取所有private字段（排除static和final字段）
    fields=$(grep -E "^\s+private\s+(?!static)(?!final)\w+\s+\w+\s*;" "$file" | sed 's/.*private\s\+\(\w\+\)\s\+\(\w\+\)\s*;.*/\1 \2/')
    
    if [ -z "$fields" ]; then
        echo "  没有找到需要处理的字段"
        return
    fi
    
    # 创建临时文件
    local temp_file=$(mktemp)
    
    # 标记是否已经在构造函数和getter/setter之间
    local in_methods=false
    
    # 处理文件内容
    awk -v classname="$classname" -v fields="$fields" '
    BEGIN {
        # 解析字段
        n = split(fields, field_array, "\n")
        for (i = 1; i <= n; i++) {
            if (field_array[i] ~ /^[[:space:]]*$/) continue
            split(field_array[i], parts, " ")
            type = parts[1]
            name = parts[2]
            # 首字母大写
            capital_name = toupper(substr(name, 1, 1)) substr(name, 2)
            field_types[i] = type
            field_names[i] = name
            field_capitals[i] = capital_name
        }
    }
    
    # 检测是否已经存在getter/setter方法
    /^[[:space:]]*public[[:space:]]+.*get[A-Z]/ {
        has_getter_setter = 1
    }
    
    # 在最后的}之前添加getter/setter
    /^}/ {
        if (!has_getter_setter) {
            # 添加空行
            print ""
            
            # 添加getter/setter方法
            for (i = 1; i <= n; i++) {
                if (field_types[i] == "" || field_names[i] == "") continue
                
                type = field_types[i]
                name = field_names[i]
                capital_name = field_capitals[i]
                
                # Getter方法
                print "    public " type " get" capital_name "() {"
                print "        return " name ";"
                print "    }"
                print ""
                
                # Setter方法
                print "    public void set" capital_name "(" type " " name ") {"
                print "        this." name " = " name ";"
                print "    }"
                print ""
            }
        }
        print $0
        next
    }
    
    {
        print $0
    }
    ' "$file" > "$temp_file"
    
    # 替换原文件
    mv "$temp_file" "$file"
    echo "  处理完成"
}

# 处理所有实体类
for file in src/main/java/com/dataasset/security/entity/*.java; do
    if [ -f "$file" ]; then
        process_entity "$file"
    fi
done

echo "所有实体类处理完成!"
