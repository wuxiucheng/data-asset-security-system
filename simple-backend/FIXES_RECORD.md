# Mock服务器修复记录

## 重要提示
⚠️ **所有修复都已保存在 server.js 中，请勿随意覆盖或删除**

## 已修复的问题清单

### 1. 登录功能修复 ✅
**问题**：只支持admin用户登录，其他用户返回401
**修复**：支持所有用户登录，返回正确的数据格式
**文件位置**：server.js 第380行
**测试命令**：
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"123456"}'
```

### 2. 重置密码功能修复 ✅
**问题**：管理员重置用户密码失败
**修复**：支持两种场景（用户修改密码 + 管理员重置密码）
**文件位置**：server.js 第437行
**测试命令**：
```bash
curl -X POST http://localhost:8080/api/auth/changePassword \
  -H "Content-Type: application/json" \
  -d '{"userId":2,"newPassword":"123456"}'
```

### 3. 权限管理功能修复 ✅
**问题**：
- 权限数据只有扁平结构，没有树形层级
- 缺少状态切换功能
- 请求方法显示不正确

**修复**：
- 重新设计权限数据（22个权限项，完整树形结构）
- 添加权限更新API
- 修复前端显示逻辑

**文件位置**：
- server.js 第29-227行（权限数据）
- server.js 第698行（权限更新API）

**测试命令**：
```bash
curl "http://localhost:8080/api/permission/list?pageNum=1&pageSize=1000"
curl http://localhost:8080/api/permission/tree
curl -X PUT http://localhost:8080/api/permission/update \
  -H "Content-Type: application/json" \
  -d '{"permissionId":1,"status":"INACTIVE"}'
```

### 4. MFA功能修复 ✅
**问题**：MFA相关API缺失，返回404
**修复**：添加完整的MFA API（7个接口）
**文件位置**：server.js 第1800行左右
**测试命令**：
```bash
curl http://localhost:8080/api/mfa/status
curl http://localhost:8080/api/mfa/setup
```

### 5. 会话管理功能修复 ✅
**问题**：会话管理API缺失，返回404
**修复**：添加完整的会话管理API（6个接口）
**文件位置**：server.js 第1700行左右
**测试命令**：
```bash
curl http://localhost:8080/api/session/current
curl http://localhost:8080/api/session/my-sessions
```

### 6. 审计日志功能修复 ✅
**问题**：审计日志API缺失，返回404
**修复**：添加完整的审计日志API（5个接口）
**文件位置**：server.js 第1600行左右
**测试命令**：
```bash
curl -X POST http://localhost:8080/api/audit-logs/query \
  -H "Content-Type: application/json" \
  -d '{"pageNum":1,"pageSize":10}'
```

### 7. 角色权限功能修复 ✅
**问题**：角色权限相关API缺失
**修复**：添加获取角色权限和分配权限API
**文件位置**：server.js 第565行
**测试命令**：
```bash
curl http://localhost:8080/api/role/permissions/1
curl -X POST http://localhost:8080/api/role/assignPermissions \
  -H "Content-Type: application/json" \
  -d '{"roleId":1,"permissionIds":[1,2,3]}'
```

### 8. 认证相关API修复 ✅
**问题**：缺少部分认证相关API
**修复**：添加current-user、refresh-token、validate-token、revoke-token API
**文件位置**：server.js 第452-490行
**测试命令**：
```bash
curl http://localhost:8080/api/auth/current-user
curl -X POST http://localhost:8080/api/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"test"}'
```

### 9. 前端样式修复 ✅
**问题**：审计日志页面下拉框被压缩
**修复**：为所有表单控件添加固定宽度
**文件位置**：frontend/src/views/audit-log/index.vue

### 10. API路径修复 ✅
**问题**：userApi.changePassword路径错误
**修复**：从 /user/changePassword 改为 /auth/changePassword
**文件位置**：frontend/src/api/index.ts 第302行

## API统计

**总计：80个API接口**

### 按模块分类
- 认证相关：9个
- 用户管理：4个
- 角色管理：6个
- 权限管理：3个
- 部门管理：5个
- 责任人管理：4个
- 分类标准管理：5个
- 数据分类管理：5个
- 分级标准管理：5个
- 数据分级管理：4个
- 数据资产管理：7个
- 字段管理：5个
- 统计分析：2个
- 会话管理：6个
- MFA管理：7个
- 审计日志：5个
- 报告管理：6个
- 健康检查：1个

## 用户密码规则

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 系统管理员 |
| user1 | 123456 | 普通用户 |
| user2 | 123456 | 普通用户 |
| user3 | 123456 | 普通用户 |

## 注意事项

1. ⚠️ **不要删除或覆盖server.js中的任何代码**
2. ⚠️ **修改前请先备份**
3. ⚠️ **修改后请运行测试脚本验证**
4. ⚠️ **所有API都有 /api 前缀**（除了健康检查）

## 测试脚本

运行完整测试：
```bash
bash /tmp/check-all-fixes.sh
```

## 更新日期
2026-04-27
