# 数据资产安全及分类分级管理系统

## 快速启动指南

### 启动系统

```bash
cd /Users/wuxiucheng/分级分类
./start-quick.sh
```

启动后访问：
- 前端界面: http://localhost:5173
- 后端API: http://localhost:8080

### 停止系统

```bash
./stop-quick.sh
```

### 默认登录信息

- 用户名: `admin`
- 密码: `admin123`

## 系统说明

### 当前运行模式

**快速启动模式（前端 + Mock后端）**
- ✅ 前端：Vue3 + Element Plus + Vite
- ✅ 后端：Node.js + Express（Mock服务器）
- ✅ 完整的82个API接口
- ✅ 所有前端功能可正常使用

### 为什么使用Mock后端？

真实的Java Spring Boot后端由于Lombok配置问题暂时无法启动，因此使用Mock服务器模拟后端API，以便：
1. 前端开发不受影响
2. 可以完整演示所有功能
3. 可以进行UI/UX测试

### Mock后端特点

- 所有数据存储在内存中，重启后重置
- MFA验证码验证是模拟的（任意6位数字可通过）
- 完整实现了82个RESTful API接口
- 支持CORS跨域请求

## 项目结构

```
分级分类/
├── start-quick.sh          # 启动脚本
├── stop-quick.sh           # 停止脚本
├── README.md               # 本文档
└── data-asset-security-system/
    ├── frontend/           # Vue3前端
    ├── backend/            # Java后端（暂不可用）
    └── simple-backend/     # Mock后端（当前使用）
        ├── server.js       # Mock服务器主文件
        └── README.md       # Mock后端详细文档
```

## 功能模块

### 已实现功能

1. **用户管理** - 用户增删改查、角色分配
2. **角色管理** - 角色增删改查、权限分配
3. **权限管理** - 权限树展示
4. **部门管理** - 部门树形结构管理
5. **责任人管理** - 数据责任人信息管理
6. **分类标准** - 数据分类标准制定和发布
7. **数据分类** - 数据分类树形管理
8. **分级标准** - 数据分级标准制定和发布
9. **数据分级** - 数据分级管理
10. **数据资产** - 数据资产清单管理、导入导出
11. **字段管理** - 数据字段分类分级标注
12. **统计分析** - 资产统计、趋势分析
13. **会话管理** - 用户会话监控和管理
14. **MFA设置** - 多因素认证配置
15. **审计日志** - 操作日志查询和统计
16. **报告管理** - 各类报告生成和导出

## 常见问题

### Q: 为什么有些功能数据不保存？
A: Mock后端使用内存存储，重启后数据会重置。这是正常现象。

### Q: MFA验证码是什么？
A: 任意6位数字都可以通过验证（例如：123456）。

### Q: 如何查看日志？
A: 
```bash
# 前端日志
tail -f data-asset-security-system/frontend/logs/frontend.log

# 后端日志
tail -f data-asset-security-system/simple-backend/logs/backend.log
```

### Q: 端口被占用怎么办？
A: 运行 `./stop-quick.sh` 停止服务，然后重新启动。

## 技术栈

### 前端
- Vue 3
- Element Plus
- Vite
- TypeScript
- Pinia
- Vue Router

### Mock后端
- Node.js
- Express
- CORS

### 真实后端（暂不可用）
- Spring Boot
- MyBatis-Plus
- MySQL
- Redis
- RabbitMQ

## 更新日志

### 2026-04-27
- ✅ 完善Mock后端，新增18个API接口
- ✅ 修复前端404错误
- ✅ 清理不相干的启停脚本
- ✅ 创建完整的使用文档

## 相关文档

- [Mock后端详细文档](data-asset-security-system/simple-backend/README.md)
- [项目总结](PROJECT_SUMMARY.md)
- [前端总结](FRONTEND_SUMMARY.md)
