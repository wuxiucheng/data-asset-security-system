# Mock后端服务器

## 概述

这是一个基于Node.js + Express的模拟后端服务器，用于前端开发和演示。它实现了完整的RESTful API接口，模拟了真实后端的所有功能。

## 已实现的API（共82个）

### 1. 认证相关（5个）
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/userInfo` - 获取用户信息
- `PUT /api/auth/updateProfile` - 更新个人信息
- `POST /api/auth/changePassword` - 修改密码

### 2. 用户管理（4个）
- `GET /api/user/list` - 获取用户列表
- `POST /api/user/create` - 创建用户
- `PUT /api/user/update` - 更新用户
- `DELETE /api/user/delete/:userId` - 删除用户

### 3. 角色管理（6个）
- `GET /api/role/list` - 获取角色列表
- `POST /api/role/create` - 创建角色
- `PUT /api/role/update` - 更新角色
- `DELETE /api/role/delete/:roleId` - 删除角色
- `GET /api/role/permissions/:roleId` - 获取角色权限
- `POST /api/role/assignPermissions` - 分配权限

### 4. 权限管理（2个）
- `GET /api/permission/list` - 获取权限列表
- `GET /api/permission/tree` - 获取权限树

### 5. 部门管理（5个）
- `GET /api/department/tree` - 获取部门树
- `GET /api/department/list` - 获取部门列表
- `POST /api/department/create` - 创建部门
- `PUT /api/department/update` - 更新部门
- `DELETE /api/department/delete/:departmentId` - 删除部门

### 6. 责任人管理（4个）
- `GET /api/owner/list` - 获取责任人列表
- `POST /api/owner/create` - 创建责任人
- `PUT /api/owner/update` - 更新责任人
- `DELETE /api/owner/delete/:ownerId` - 删除责任人

### 7. 分类标准管理（5个）
- `GET /api/classificationStandard/list` - 获取分类标准列表
- `POST /api/classificationStandard/create` - 创建分类标准
- `PUT /api/classificationStandard/update` - 更新分类标准
- `DELETE /api/classificationStandard/delete/:standardId` - 删除分类标准
- `POST /api/classificationStandard/publish/:standardId` - 发布分类标准

### 8. 数据分类管理（5个）
- `GET /api/classification/tree/:standardId` - 获取分类树
- `GET /api/classification/list` - 获取分类列表
- `POST /api/classification/create` - 创建分类
- `PUT /api/classification/update` - 更新分类
- `DELETE /api/classification/delete/:classificationId` - 删除分类

### 9. 分级标准管理（5个）
- `GET /api/gradingStandard/list` - 获取分级标准列表
- `POST /api/gradingStandard/create` - 创建分级标准
- `PUT /api/gradingStandard/update` - 更新分级标准
- `DELETE /api/gradingStandard/delete/:standardId` - 删除分级标准
- `POST /api/gradingStandard/publish/:standardId` - 发布分级标准

### 10. 数据分级管理（4个）
- `GET /api/grading/list` - 获取分级列表
- `POST /api/grading/create` - 创建分级
- `PUT /api/grading/update` - 更新分级
- `DELETE /api/grading/delete/:gradingId` - 删除分级

### 11. 数据资产管理（7个）
- `GET /api/asset/list` - 获取资产列表
- `GET /api/asset/detail/:assetId` - 获取资产详情
- `POST /api/asset/create` - 创建资产
- `PUT /api/asset/update` - 更新资产
- `DELETE /api/asset/delete/:assetId` - 删除资产
- `POST /api/asset/import` - 导入资产
- `GET /api/asset/export` - 导出资产

### 12. 字段管理（5个）
- `GET /api/asset/fields/:assetId` - 获取资产字段
- `POST /api/asset/field/create` - 创建字段
- `PUT /api/asset/field/update` - 更新字段
- `DELETE /api/asset/field/delete/:fieldId` - 删除字段
- `POST /api/asset/field/batchUpdate` - 批量更新字段

### 13. 统计分析（2个）
- `GET /api/statistics/asset` - 获取资产统计
- `GET /api/statistics/trend` - 获取趋势数据

### 14. 会话管理（6个）
- `GET /api/session/current` - 获取当前会话
- `GET /api/session/user/:userId` - 获取用户会话
- `GET /api/session/my-sessions` - 获取我的会话
- `POST /api/session/:sessionId/force-logout` - 强制下线会话
- `POST /api/session/force-logout-all` - 强制下线所有会话
- `POST /api/session/clean-expired` - 清理过期会话

### 15. MFA管理（7个）
- `GET /api/mfa/status` - 检查MFA状态
- `GET /api/mfa/setup` - 生成MFA设置
- `POST /api/mfa/enable` - 启用MFA
- `POST /api/mfa/verify` - 验证MFA
- `POST /api/mfa/disable` - 禁用MFA
- `GET /api/mfa/config` - 获取MFA配置
- `POST /api/mfa/backup-codes` - 生成备用码

### 16. 审计日志（5个）
- `POST /api/audit-logs/query` - 查询审计日志
- `POST /api/audit-logs/statistics` - 统计审计日志
- `POST /api/audit-logs/export` - 导出审计日志
- `POST /api/audit-logs/archive` - 归档审计日志
- `POST /api/audit-logs/clean` - 清理审计日志

### 17. 报告管理（6个）
- `GET /api/report/asset-list/generate` - 生成资产清单报告
- `GET /api/report/asset-list/export` - 导出资产清单报告
- `GET /api/report/classification-stats/generate` - 生成分类统计报告
- `GET /api/report/classification-stats/export` - 导出分类统计报告
- `GET /api/report/history` - 获取报告历史
- `DELETE /api/report/:reportId` - 删除报告

## 快速启动

### 方式一：使用启停脚本（推荐）

```bash
# 启动系统（前端+mock后端）
cd /Users/wuxiucheng/分级分类
./start-quick.sh

# 停止系统
./stop-quick.sh
```

### 方式二：手动启动

```bash
# 1. 安装依赖
cd /Users/wuxiucheng/分级分类/data-asset-security-system/simple-backend
npm install

# 2. 启动服务
node server.js

# 服务将运行在 http://localhost:8080
```

## 默认登录信息

- 用户名: `admin`
- 密码: `admin123`

## 测试API

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 测试登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 测试MFA状态
curl http://localhost:8080/api/mfa/status

# 测试会话管理
curl http://localhost:8080/api/session/current

# 测试审计日志
curl -X POST http://localhost:8080/api/audit-logs/query \
  -H "Content-Type: application/json" \
  -d '{"pageNum":1,"pageSize":10}'
```

## 数据持久化

当前版本使用内存存储数据，重启服务后数据会重置。如需持久化数据，可以：
1. 使用文件存储（JSON文件）
2. 集成SQLite数据库
3. 连接真实的MySQL/PostgreSQL数据库

## 注意事项

1. 这是一个**模拟服务器**，仅用于前端开发和演示
2. 所有数据都是模拟数据，不会真实保存
3. MFA验证码验证是模拟的，任意6位数字都可以通过验证
4. 生产环境请使用真实的Java Spring Boot后端

## 更新日志

### 2026-04-27
- ✅ 新增MFA相关API（7个接口）
- ✅ 新增会话管理API（6个接口）
- ✅ 新增审计日志API（5个接口）
- ✅ 完善所有API接口，总计82个
- ✅ 修复前端404错误问题

## 技术栈

- Node.js
- Express
- CORS

## 相关文档

- [项目总结](../../PROJECT_SUMMARY.md)
- [前端总结](../../FRONTEND_SUMMARY.md)
- [快速入门](../../QUICK_START.md)
