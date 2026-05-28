# 数据资产安全及分类分级管理系统 - 快速入门指南

## 🚀 一键启动

### 方式一：使用启动脚本（推荐）

```bash
# 进入项目目录
cd "/Users/wuxiucheng/分级分类"

# 运行启动脚本
./start.sh
```

### 方式二：手动启动

```bash
# 1. 初始化数据库
mysql -u root -p data_asset_security < /Users/wuxiucheng/分级分类/data-asset-security-system/backend/src/main/resources/db/init.sql

# 2. 编译启动
cd "/Users/wuxiucheng/分级分类/data-asset-security-system/backend"
mvn clean package -DskipTests
java -jar target/data-asset-security-1.0.0.jar
```

## 📱 访问系统

### 系统地址
- **API文档**: http://localhost:8080/doc.html
- **健康检查**: http://localhost:8080/actuator/health

### 默认登录信息
- **用户名**: `admin`
- **密码**: `admin123`

## 🎯 快速体验流程

### 第一步：登录系统
1. 访问 http://localhost:8080/doc.html
2. 点击"认证管理" → "登录接口"
3. 使用 admin/admin123 登录
4. 复制返回的JWT Token

### 第二步：创建部门
1. 点击"责任体系管理" → "部门管理"
2. 点击"创建部门"
3. 填写部门信息：
   ```
   部门编码: DEPT001
   部门名称: 技术部
   部门描述: 负责系统开发和维护
   ```
4. 点击"执行"

### 第三步：创建责任人
1. 点击"责任体系管理" → "责任人管理"
2. 点击"创建责任人"
3. 填写责任人信息：
   ```
   工号: EMP001
   姓名: 张三
   所属部门ID: 1
   职务: 技术主管
   ```
4. 点击"执行"

### 第四步：创建分类标准
1. 点击"分类分级管理" → "分类标准管理"
2. 点击"创建分类标准"
3. 填写标准信息：
   ```
   标准编码: STD001
   标准名称: 数据资产分类标准V1.0
   标准描述: 企业数据资产分类分级管理标准
   版本: 1.0
   ```
4. 点击"执行"

### 第五步：创建数据分类
1. 点击"分类分级管理" → "数据分类管理"
2. 点击"创建数据分类"
3. 填写分类信息：
   ```
   标准ID: 1
   分类编码: BIZ
   分类名称: 业务数据
   分类描述: 企业业务相关数据
   层级: 1
   ```
4. 点击"执行"

### 第六步：创建分级标准
1. 点击"分类分级管理" → "分级标准管理"
2. 点击"创建分级标准"
3. 填写标准信息：
   ```
   标准编码: GRD001
   标准名称: 数据资产分级标准V1.0
   标准描述: 企业数据资产安全等级标准
   版本: 1.0
   ```
4. 点击"执行"

### 第七步：创建数据分级
1. 点击"分类分级管理" → "数据分级管理"
2. 点击"创建数据分级"
3. 填写分级信息：
   ```
   标准ID: 1
   分级编码: L2
   分级名称: 二级
   分级描述: 内部数据，仅限内部使用
   等级值: 2
   颜色标识: #FFA500
   ```
4. 点击"执行"

### 第八步：注册数据资产
1. 点击"数据资产管理" → "数据资产管理"
2. 点击"创建数据资产"
3. 填写资产信息：
   ```
   资产名称: 客户信息表
   资产编码: ASSET001
   资产类型: DATABASE
   所属系统: CRM系统
   数据库类型: MYSQL
   数据库名称: crm_db
   表名: customer_info
   责任部门ID: 1
   责任人ID: 1
   分类ID: 1
   分级ID: 1
   ```
4. 点击"执行"

### 第九步：查看统计数据
1. 点击"统计分析" → "资产统计概览"
2. 查看各类统计数据
3. 点击"趋势分析"查看数据趋势

## 🛠️ 常用操作

### 查看日志
```bash
# 实时查看应用日志
tail -f /Users/wuxiucheng/分级分类/data-asset-security-system/backend/logs/application.log

# 查看错误日志
grep ERROR /Users/wuxiucheng/分级分类/data-asset-security-system/backend/logs/application.log

# 查看最近100行日志
tail -100 /Users/wuxiucheng/分级分类/data-asset-security-system/backend/logs/application.log
```

### 停止系统
```bash
# 使用停止脚本
cd "/Users/wuxiucheng/分级分类"
./stop.sh

# 或者手动停止
pkill -f data-asset-security
```

### 重启系统
```bash
# 先停止后启动
cd "/Users/wuxiucheng/分级分类"
./stop.sh
sleep 3
./start.sh
```

### 测试API接口
```bash
# 测试登录接口
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# 测试获取用户信息（需要替换YOUR_JWT_TOKEN）
curl -X GET http://localhost:8080/api/user/info \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📊 数据库操作

### 查看数据库
```bash
# 连接到数据库
mysql -u root -p data_asset_security

# 查看所有表
SHOW TABLES;

# 查看用户表
SELECT * FROM sys_user;

# 查看部门表
SELECT * FROM department;

# 查看数据资产表
SELECT * FROM data_asset;

# 退出数据库
EXIT;
```

### 重置数据库
```bash
# 删除并重建数据库
mysql -u root -p -e "DROP DATABASE IF EXISTS data_asset_security; CREATE DATABASE data_asset_security DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 重新初始化
mysql -u root -p data_asset_security < /Users/wuxiucheng/分级分类/data-asset-security-system/backend/src/main/resources/db/init.sql
```

## 🎨 系统功能概览

### 1. 用户权限管理
- ✅ 用户管理：创建、编辑、删除、查询
- ✅ 角色管理：创建、编辑、删除、权限分配
- ✅ 权限管理：树形结构、权限类型管理
- ✅ 用户角色：角色分配、权限管理

### 2. 责任体系管理
- ✅ 部门管理：树形结构、负责人分配
- ✅ 责任人管理：完整CRUD、部门关联
- ✅ 组织架构：导入、导出、同步

### 3. 分类分级管理
- ✅ 分类标准：版本管理、发布归档
- ✅ 数据分类：树形结构、多级分类
- ✅ 分级标准：版本管理、发布归档
- ✅ 数据分级：等级定义、安全要求

### 4. 数据资产管理
- ✅ 资产注册：完整信息管理
- ✅ 资产查询：多条件查询、分页
- ✅ 资产管理：编辑、删除、状态管理
- ✅ 字段管理：批量导入、字段信息

### 5. 审批流程管理
- ✅ 流程定义：BPMN流程设计
- ✅ 流程实例：启动、跟踪、管理
- ✅ 审批任务：分配、处理、委托
- ✅ 审批历史：完整记录

### 6. 统计分析
- ✅ 资产统计：多维度统计
- ✅ 趋势分析：时间序列分析
- ✅ 报表生成：可视化、导出

## 🔧 故障排除

### 问题1：端口被占用
```bash
# 查看端口占用
lsof -i :8080

# 停止占用端口的进程
kill -9 <PID>
```

### 问题2：数据库连接失败
```bash
# 检查MySQL服务状态
brew services list | grep mysql

# 启动MySQL服务
brew services start mysql

# 测试数据库连接
mysql -u root -p -e "SELECT 1;"
```

### 问题3：Redis连接失败
```bash
# 检查Redis服务状态
brew services list | grep redis

# 启动Redis服务
brew services start redis

# 测试Redis连接
redis-cli ping
```

### 问题4：内存不足
```bash
# 增加JVM内存启动
java -Xms512m -Xmx1024m -jar target/data-asset-security-1.0.0.jar
```

## 📚 详细文档

- **项目总结**: `/Users/wuxiucheng/分级分类/PROJECT_SUMMARY.md`
- **部署指南**: `/Users/wuxiucheng/分级分类/DEPLOYMENT_GUIDE.md`
- **API文档**: http://localhost:8080/doc.html

## 💡 提示

1. **首次使用**: 建议先登录后修改默认密码
2. **数据安全**: 生产环境请修改JWT密钥和数据库密码
3. **性能优化**: 建议安装Redis以获得更好的性能
4. **日志监控**: 定期查看应用日志，及时发现和解决问题

## 🎉 开始使用

现在您已经了解了系统的基本操作，可以开始探索和使用数据资产安全及分类分级管理系统了！

**系统状态**: ✅ 运行中
**API地址**: http://localhost:8080/doc.html
**默认账号**: admin / admin123

祝您使用愉快！🚀
