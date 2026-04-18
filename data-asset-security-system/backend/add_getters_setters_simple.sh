#!/bin/bash

# 简化版的getter/setter添加脚本

for file in src/main/java/com/dataasset/security/entity/*.java; do
    if [ -f "$file" ]; then
        classname=$(basename "$file" .java)
        echo "处理: $classname"
        
        # 检查是否已经有getter方法
        if grep -q "public.*get[A-Z].*()" "$file"; then
            echo "  已有getter/setter方法，跳过"
            continue
        fi
        
        # 提取字段（简化版）
        fields=$(grep "private [a-zA-Z]* [a-zA-Z]*;" "$file" | sed 's/.*private \([a-zA-Z]*\) \([a-zA-Z]*\);.*/\1 \2/')
        
        if [ -n "$fields" ]; then
            # 在文件末尾的}之前添加方法
            awk -v fields="$fields" '
            BEGIN {
                n = split(fields, f, "\n")
            }
            /^}/ {
                for (i = 1; i <= n; i++) {
                    if (f[i] ~ /^[[:space:]]*$/) continue
                    split(f[i], p, " ")
                    type = p[1]
                    name = p[2]
                    capital = toupper(substr(name, 1, 1)) substr(name, 2)
                    
                    print ""
                    print "    public " type " get" capital "() {"
                    print "        return " name ";"
                    print "    }"
                    print ""
                    print "    public void set" capital "(" type " " name ") {"
                    print "        this." name " = " name ";"
                    print "    }"
                }
                print $0
                next
            }
            { print $0 }
            ' "$file" > "${file}.tmp"
            mv "${file}.tmp" "$file"
            echo "  添加了getter/setter方法"
        fi
    fi
done

echo "处理完成!"
