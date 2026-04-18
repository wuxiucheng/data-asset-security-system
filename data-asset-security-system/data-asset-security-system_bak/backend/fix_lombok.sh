#!/bin/bash

# 修复Lombok问题的脚本

find src/main/java/com/dataasset/security/dto -name "*.java" -type f | while read file; do
    echo "Processing: $file"
    
    # 检查文件是否包含@Data注解
    if grep -q "@Data" "$file"; then
        # 获取所有private字段
        fields=$(grep -E "^\s+private\s+\w+\s+\w+\s*;" "$file" | sed 's/.*private\s\+\w\+\s\+\(\w\+\)\s*;.*/\1/')
        
        # 在文件末尾的}之前添加getter/setter方法
        if [ -n "$fields" ]; then
            # 创建临时文件
            temp_file=$(mktemp)
            
            # 复制文件内容到临时文件，但在最后的}之前添加getter/setter
            awk -v fields="$fields" '
            BEGIN {
                split(fields, field_array, " ")
                for (i in field_array) {
                    field = field_array[i]
                    gsub(/^[[:space:]]+|[[:space:]]+$/, "", field)
                    if (length(field) > 0) {
                        print "    public " field " get" field "() {"
                        print "        return " field ";"
                        print "    }"
                        print ""
                    }
                }
            }
            /^}/ {
                exit
            }
            {
                print $0
            }
            ' "$file" > "$temp_file"
            
            # 替换原文件
            mv "$temp_file" "$file"
            echo "  Added getter/setter methods for: $fields"
        fi
    fi
done

echo "Lombok fix completed!"
