const express = require('express');
const cors = require('cors');
const QRCode = require('qrcode');
const multer = require('multer');
const XLSX = require('xlsx');
const app = express();
const PORT = 8080;

// 配置multer用于文件上传
const upload = multer({ dest: 'uploads/' });

app.use(cors());
app.use(express.json());

// ==================== 模拟数据 ====================

// 审计日志记录函数
let auditLogIdCounter = 100;
const addAuditLog = (logData) => {
  const log = {
    logId: auditLogIdCounter++,
    operationType: logData.operationType || 'QUERY',
    module: logData.module || '系统',
    objectType: logData.objectType || '对象',
    objectId: logData.objectId || null,
    objectName: logData.objectName || '',
    operationDescription: logData.description || '',
    operationResult: logData.result || 'SUCCESS',
    errorMessage: logData.errorMessage || null,
    operatorId: logData.operatorId || 1,
    operatorName: logData.operatorName || '系统管理员',
    operatorUsername: logData.operatorUsername || 'admin',
    operationTime: new Date().toISOString(),
    operationIp: logData.ip || '127.0.0.1',
    operationLocation: logData.location || '本地',
    userAgent: logData.userAgent || 'Chrome/120.0',
    requestUrl: logData.url || '',
    requestMethod: logData.method || 'GET',
    executionTime: logData.executionTime || Math.floor(Math.random() * 200) + 50
  };
  
  // 添加到日志数组开头（最新的在前面）
  mockAuditLogs.unshift(log);
  
  // 保持日志数量不超过1000条
  if (mockAuditLogs.length > 1000) {
    mockAuditLogs.pop();
  }
  
  return log;
};

// ==================== 模拟数据 ====================

// 用户数据
const mockUsers = [
  { userId: 1, username: 'admin', realName: '系统管理员', email: 'admin@example.com', phone: '13800138000', status: 'ACTIVE', roleIds: [1, 2] },
  { userId: 2, username: 'user1', realName: '张三', email: 'zhangsan@example.com', phone: '13800138001', status: 'ACTIVE', roleIds: [5] },
  { userId: 3, username: 'user2', realName: '李四', email: 'lisi@example.com', phone: '13800138002', status: 'ACTIVE', roleIds: [4, 5] },
  { userId: 4, username: 'user3', realName: '王五', email: 'wangwu@example.com', phone: '13800138003', status: 'INACTIVE', roleIds: [5] },
];

// 用户角色关联数据
const mockUserRoles = [
  { userId: 1, roleId: 1 }, // admin - 系统管理员
  { userId: 1, roleId: 2 }, // admin - 数据管理员
  { userId: 2, roleId: 5 }, // user1 - 普通用户
  { userId: 3, roleId: 4 }, // user2 - 数据责任人
  { userId: 3, roleId: 5 }, // user2 - 普通用户
  { userId: 4, roleId: 5 }, // user3 - 普通用户
];

// 角色数据
const mockRoles = [
  { roleId: 1, roleCode: 'SYSTEM_ADMIN', roleName: '系统管理员', roleType: 'SYSTEM_ADMIN', status: 'ACTIVE', roleDescription: '负责系统配置和用户管理' },
  { roleId: 2, roleCode: 'DATA_ADMIN', roleName: '数据管理员', roleType: 'DATA_ADMIN', status: 'ACTIVE', roleDescription: '负责分类分级标准制定和维护' },
  { roleId: 3, roleCode: 'APPROVER', roleName: '数据审批人', roleType: 'APPROVER', status: 'ACTIVE', roleDescription: '负责分类分级申请审批' },
  { roleId: 4, roleCode: 'OWNER', roleName: '数据责任人', roleType: 'OWNER', status: 'ACTIVE', roleDescription: '负责数据资产的日常维护和管理' },
  { roleId: 5, roleCode: 'USER', roleName: '普通用户', roleType: 'USER', status: 'ACTIVE', roleDescription: '查看已授权的数据资产信息' },
];

// 权限数据（树形结构）
const mockPermissions = [
  // 系统管理菜单
  {
    permissionId: 1,
    permissionCode: 'system',
    permissionName: '系统管理',
    permissionType: 'MENU',
    permissionUrl: '/system',
    permissionMethod: null,
    parentId: null,
    sortOrder: 1,
    status: 'ACTIVE'
  },
  // 用户管理菜单
  {
    permissionId: 2,
    permissionCode: 'system:user',
    permissionName: '用户管理',
    permissionType: 'MENU',
    permissionUrl: '/user',
    permissionMethod: null,
    parentId: 1,
    sortOrder: 1,
    status: 'ACTIVE'
  },
  // 用户管理按钮权限
  {
    permissionId: 3,
    permissionCode: 'system:user:create',
    permissionName: '创建用户',
    permissionType: 'BUTTON',
    permissionUrl: '/api/user/create',
    permissionMethod: 'POST',
    parentId: 2,
    sortOrder: 1,
    status: 'ACTIVE'
  },
  {
    permissionId: 4,
    permissionCode: 'system:user:edit',
    permissionName: '编辑用户',
    permissionType: 'BUTTON',
    permissionUrl: '/api/user/update',
    permissionMethod: 'PUT',
    parentId: 2,
    sortOrder: 2,
    status: 'ACTIVE'
  },
  {
    permissionId: 5,
    permissionCode: 'system:user:delete',
    permissionName: '删除用户',
    permissionType: 'BUTTON',
    permissionUrl: '/api/user/delete',
    permissionMethod: 'DELETE',
    parentId: 2,
    sortOrder: 3,
    status: 'ACTIVE'
  },
  {
    permissionId: 6,
    permissionCode: 'system:user:view',
    permissionName: '查看用户',
    permissionType: 'BUTTON',
    permissionUrl: '/api/user/list',
    permissionMethod: 'GET',
    parentId: 2,
    sortOrder: 4,
    status: 'ACTIVE'
  },
  // 角色管理菜单
  {
    permissionId: 7,
    permissionCode: 'system:role',
    permissionName: '角色管理',
    permissionType: 'MENU',
    permissionUrl: '/role',
    permissionMethod: null,
    parentId: 1,
    sortOrder: 2,
    status: 'ACTIVE'
  },
  // 角色管理按钮权限
  {
    permissionId: 8,
    permissionCode: 'system:role:create',
    permissionName: '创建角色',
    permissionType: 'BUTTON',
    permissionUrl: '/api/role/create',
    permissionMethod: 'POST',
    parentId: 7,
    sortOrder: 1,
    status: 'ACTIVE'
  },
  {
    permissionId: 9,
    permissionCode: 'system:role:edit',
    permissionName: '编辑角色',
    permissionType: 'BUTTON',
    permissionUrl: '/api/role/update',
    permissionMethod: 'PUT',
    parentId: 7,
    sortOrder: 2,
    status: 'ACTIVE'
  },
  {
    permissionId: 10,
    permissionCode: 'system:role:delete',
    permissionName: '删除角色',
    permissionType: 'BUTTON',
    permissionUrl: '/api/role/delete',
    permissionMethod: 'DELETE',
    parentId: 7,
    sortOrder: 3,
    status: 'ACTIVE'
  },
  {
    permissionId: 11,
    permissionCode: 'system:role:view',
    permissionName: '查看角色',
    permissionType: 'BUTTON',
    permissionUrl: '/api/role/list',
    permissionMethod: 'GET',
    parentId: 7,
    sortOrder: 4,
    status: 'ACTIVE'
  },
  // 权限管理菜单
  {
    permissionId: 12,
    permissionCode: 'system:permission',
    permissionName: '权限管理',
    permissionType: 'MENU',
    permissionUrl: '/permission',
    permissionMethod: null,
    parentId: 1,
    sortOrder: 3,
    status: 'ACTIVE'
  },
  {
    permissionId: 13,
    permissionCode: 'system:permission:view',
    permissionName: '查看权限',
    permissionType: 'BUTTON',
    permissionUrl: '/api/permission/list',
    permissionMethod: 'GET',
    parentId: 12,
    sortOrder: 1,
    status: 'ACTIVE'
  },
  // 数据资产管理菜单
  {
    permissionId: 14,
    permissionCode: 'data',
    permissionName: '数据资产管理',
    permissionType: 'MENU',
    permissionUrl: '/data',
    permissionMethod: null,
    parentId: null,
    sortOrder: 2,
    status: 'ACTIVE'
  },
  {
    permissionId: 15,
    permissionCode: 'data:asset',
    permissionName: '资产列表',
    permissionType: 'MENU',
    permissionUrl: '/asset',
    permissionMethod: null,
    parentId: 14,
    sortOrder: 1,
    status: 'ACTIVE'
  },
  {
    permissionId: 16,
    permissionCode: 'data:asset:create',
    permissionName: '创建资产',
    permissionType: 'BUTTON',
    permissionUrl: '/api/asset/create',
    permissionMethod: 'POST',
    parentId: 15,
    sortOrder: 1,
    status: 'ACTIVE'
  },
  {
    permissionId: 17,
    permissionCode: 'data:asset:edit',
    permissionName: '编辑资产',
    permissionType: 'BUTTON',
    permissionUrl: '/api/asset/update',
    permissionMethod: 'PUT',
    parentId: 15,
    sortOrder: 2,
    status: 'ACTIVE'
  },
  {
    permissionId: 18,
    permissionCode: 'data:asset:delete',
    permissionName: '删除资产',
    permissionType: 'BUTTON',
    permissionUrl: '/api/asset/delete',
    permissionMethod: 'DELETE',
    parentId: 15,
    sortOrder: 3,
    status: 'ACTIVE'
  },
  {
    permissionId: 19,
    permissionCode: 'data:asset:export',
    permissionName: '导出资产',
    permissionType: 'BUTTON',
    permissionUrl: '/api/asset/export',
    permissionMethod: 'GET',
    parentId: 15,
    sortOrder: 4,
    status: 'ACTIVE'
  },
  // 分类分级管理菜单
  {
    permissionId: 20,
    permissionCode: 'data:classification',
    permissionName: '分类分级管理',
    permissionType: 'MENU',
    permissionUrl: '/classification',
    permissionMethod: null,
    parentId: 14,
    sortOrder: 2,
    status: 'ACTIVE'
  },
  {
    permissionId: 21,
    permissionCode: 'data:classification:view',
    permissionName: '查看分类',
    permissionType: 'BUTTON',
    permissionUrl: '/api/classification/list',
    permissionMethod: 'GET',
    parentId: 20,
    sortOrder: 1,
    status: 'ACTIVE'
  },
  {
    permissionId: 22,
    permissionCode: 'data:classification:create',
    permissionName: '创建分类',
    permissionType: 'BUTTON',
    permissionUrl: '/api/classification/create',
    permissionMethod: 'POST',
    parentId: 20,
    sortOrder: 2,
    status: 'ACTIVE'
  }
];

// 部门数据
const mockDepartments = [
  { departmentId: 1, departmentCode: 'ROOT', departmentName: '根部门', parentId: null, status: 'ACTIVE', leaderId: 1, contactPhone: '13800138000' },
  { departmentId: 2, departmentCode: 'DEPT001', departmentName: '技术部', parentId: 1, status: 'ACTIVE', leaderId: 2, contactPhone: '13800138001' },
  { departmentId: 3, departmentCode: 'DEPT002', departmentName: '业务部', parentId: 1, status: 'ACTIVE', leaderId: 3, contactPhone: '13800138002' },
  { departmentId: 4, departmentCode: 'DEPT003', departmentName: '财务部', parentId: 1, status: 'ACTIVE', leaderId: 4, contactPhone: '13800138003' },
  { departmentId: 5, departmentCode: 'DEPT004', departmentName: '研发组', parentId: 2, status: 'ACTIVE', leaderId: 2, contactPhone: '13800138001' },
];

// 责任人数据
const mockOwners = [
  { ownerId: 1, employeeNo: 'ADMIN001', name: '系统管理员', departmentId: 1, position: '系统管理员', contactPhone: '13800138000', email: 'admin@example.com', status: 'ACTIVE' },
  { ownerId: 2, employeeNo: 'EMP001', name: '张三', departmentId: 2, position: '技术主管', contactPhone: '13800138001', email: 'zhangsan@example.com', status: 'ACTIVE' },
  { ownerId: 3, employeeNo: 'EMP002', name: '李四', departmentId: 3, position: '业务主管', contactPhone: '13800138002', email: 'lisi@example.com', status: 'ACTIVE' },
  { ownerId: 4, employeeNo: 'EMP003', name: '王五', departmentId: 4, position: '财务主管', contactPhone: '13800138003', email: 'wangwu@example.com', status: 'ACTIVE' },
];

// 分类标准数据
const mockClassificationStandards = [
  { standardId: 1, standardCode: 'STD001', standardName: '数据资产分类标准V1.0', standardDescription: '数据资产分类分级管理标准', version: '1.0', publishDate: '2025-01-01', status: 'ACTIVE' },
  { standardId: 2, standardCode: 'STD002', standardName: '数据资产分类标准V2.0', standardDescription: '数据资产分类分级管理标准升级版', version: '2.0', publishDate: '2025-06-01', status: 'DRAFT' },
];

// 数据分类数据
const mockClassifications = [
  { classificationId: 1, standardId: 1, classificationCode: 'BIZ', classificationName: '业务数据', parentId: null, level: 1, status: 'ACTIVE' },
  { classificationId: 2, standardId: 1, classificationCode: 'BIZ_CUST', classificationName: '客户数据', parentId: 1, level: 2, status: 'ACTIVE' },
  { classificationId: 3, standardId: 1, classificationCode: 'BIZ_PROD', classificationName: '产品数据', parentId: 1, level: 2, status: 'ACTIVE' },
  { classificationId: 4, standardId: 1, classificationCode: 'FIN', classificationName: '财务数据', parentId: null, level: 1, status: 'ACTIVE' },
  { classificationId: 5, standardId: 1, classificationCode: 'HR', classificationName: '人力资源数据', parentId: null, level: 1, status: 'ACTIVE' },
];

// 分级标准数据
const mockGradingStandards = [
  { standardId: 1, standardCode: 'GRD001', standardName: '数据资产分级标准V1.0', standardDescription: '数据资产安全等级标准', version: '1.0', publishDate: '2025-01-01', status: 'ACTIVE' },
];

// 数据分级数据
const mockGradings = [
  { gradingId: 1, standardId: 1, gradingCode: 'L1', gradingName: '一级', gradingDescription: '公开数据，无敏感信息', levelValue: 1, status: 'ACTIVE' },
  { gradingId: 2, standardId: 1, gradingCode: 'L2', gradingName: '二级', gradingDescription: '内部数据，仅限内部使用', levelValue: 2, status: 'ACTIVE' },
  { gradingId: 3, standardId: 1, gradingCode: 'L3', gradingName: '三级', gradingDescription: '敏感数据，包含个人信息', levelValue: 3, status: 'ACTIVE' },
  { gradingId: 4, standardId: 1, gradingCode: 'L4', gradingName: '四级', gradingDescription: '核心数据，关系企业生存', levelValue: 4, status: 'ACTIVE' },
];

// 数据资产数据
const mockAssets = [
  { assetId: 1, assetCode: 'ASSET001', assetName: '客户信息表', assetType: 'DATABASE', systemName: 'CRM系统', databaseType: 'MYSQL', databaseHost: '192.168.1.100', databasePort: 3306, databaseName: 'crm_db', tableName: 'customer_info', status: 'ACTIVE', departmentId: 2, ownerId: 2, classificationId: 2, gradingId: 2, rowCount: 12580 },
  { assetId: 2, assetCode: 'ASSET002', assetName: '订单数据表', assetType: 'DATABASE', systemName: '订单系统', databaseType: 'MYSQL', databaseHost: '192.168.1.101', databasePort: 3306, databaseName: 'order_db', tableName: 'order_info', status: 'ACTIVE', departmentId: 3, ownerId: 3, classificationId: 3, gradingId: 2, rowCount: 58930 },
  { assetId: 3, assetCode: 'ASSET003', assetName: '财务报表文件', assetType: 'FILE', systemName: '财务系统', databaseType: null, databaseName: null, tableName: null, status: 'ACTIVE', departmentId: 4, ownerId: 4, classificationId: 4, gradingId: 3, rowCount: null },
  { assetId: 4, assetCode: 'ASSET004', assetName: '员工信息API', assetType: 'API', systemName: '人事系统', databaseType: null, databaseName: null, tableName: null, status: 'ACTIVE', departmentId: 5, ownerId: 2, classificationId: 5, gradingId: 2, rowCount: null },
];

// 数据字段数据
const mockFields = [
  { fieldId: 1, assetId: 1, fieldName: 'customer_id', fieldCode: '客户ID', fieldType: 'BIGINT', isPrimaryKey: 1, isRequired: 1, classificationId: 2, gradingId: 2, status: 'ACTIVE', rowCount: 12580 },
  { fieldId: 2, assetId: 1, fieldName: 'customer_name', fieldCode: '客户姓名', fieldType: 'VARCHAR', isPrimaryKey: 0, isRequired: 1, classificationId: 2, gradingId: 3, status: 'ACTIVE', rowCount: 12540 },
  { fieldId: 3, assetId: 1, fieldName: 'customer_phone', fieldCode: '联系电话', fieldType: 'VARCHAR', isPrimaryKey: 0, isRequired: 0, classificationId: 2, gradingId: 3, status: 'ACTIVE', rowCount: 9800 },
];

// 统计数据
const mockStatistics = {
  totalAssets: 150,
  byDepartment: [
    { name: '技术部', value: 80 },
    { name: '业务部', value: 50 },
    { name: '财务部', value: 15 },
    { name: '人事部', value: 5 },
  ],
  byClassification: [
    { name: '业务数据', value: 100 },
    { name: '财务数据', value: 30 },
    { name: '人力资源数据', value: 20 },
  ],
  byGrading: [
    { name: 'L1', value: 50 },
    { name: 'L2', value: 60 },
    { name: 'L3', value: 30 },
    { name: 'L4', value: 10 },
  ],
  byStatus: [
    { name: '草稿', value: 20 },
    { name: '启用', value: 100 },
    { name: '停用', value: 30 },
  ],
};

// 趋势数据
const mockTrendData = {
  dates: ['2025-01', '2025-02', '2025-03', '2025-04', '2025-05', '2025-06'],
  assetGrowth: [100, 120, 135, 145, 150, 150],
  classificationGrowth: [80, 95, 110, 120, 130, 130],
  gradingGrowth: [60, 75, 90, 100, 110, 110],
};

// ==================== API路由 ====================

// 认证相关
app.post('/api/auth/login', (req, res) => {
  const { username, password } = req.body;
  
  // 查找用户
  const user = mockUsers.find(u => u.username === username);
  
  if (!user) {
    // 记录登录失败日志
    addAuditLog({
      operationType: 'LOGIN',
      module: '认证模块',
      objectType: '用户',
      objectName: username,
      description: `用户登录失败：用户名不存在`,
      result: 'FAILURE',
      errorMessage: '用户名不存在',
      url: '/api/auth/login',
      method: 'POST'
    });
    return res.status(401).json({ code: 401, message: '用户名不存在' });
  }
  
  // 简单密码验证（实际应该验证加密后的密码）
  // admin用户的密码是admin123，其他用户的密码默认是123456
  const expectedPassword = username === 'admin' ? 'admin123' : '123456';
  
  if (password !== expectedPassword) {
    // 记录登录失败日志
    addAuditLog({
      operationType: 'LOGIN',
      module: '认证模块',
      objectType: '用户',
      objectId: user.userId,
      objectName: username,
      description: `用户登录失败：密码错误`,
      result: 'FAILURE',
      errorMessage: '密码错误',
      url: '/api/auth/login',
      method: 'POST'
    });
    return res.status(401).json({ code: 401, message: '密码错误' });
  }
  
  // 检查用户状态
  if (user.status !== 'ACTIVE') {
    // 记录登录失败日志
    addAuditLog({
      operationType: 'LOGIN',
      module: '认证模块',
      objectType: '用户',
      objectId: user.userId,
      objectName: username,
      description: `用户登录失败：用户已被禁用`,
      result: 'FAILURE',
      errorMessage: '用户已被禁用',
      url: '/api/auth/login',
      method: 'POST'
    });
    return res.status(401).json({ code: 401, message: '用户已被禁用' });
  }
  
  // 记录登录成功日志
  addAuditLog({
    operationType: 'LOGIN',
    module: '认证模块',
    objectType: '用户',
    objectId: user.userId,
    objectName: username,
    description: `用户登录成功`,
    result: 'SUCCESS',
    url: '/api/auth/login',
    method: 'POST'
  });
  
  // 创建会话记录
  const sessionId = Date.now();
  const newSession = {
    sessionId: sessionId,
    userId: user.userId,
    username: user.username,
    loginTime: new Date().toISOString(),
    lastAccessTime: new Date().toISOString(),
    expireTime: new Date(Date.now() + 86400000).toISOString(),
    loginIp: '127.0.0.1',
    deviceInfo: 'MacBook Pro',
    browserInfo: 'Chrome 120.0',
    status: 'ACTIVE',
    token: 'mock-access-token-' + sessionId
  };
  mockSessions.push(newSession);
  
  // 登录成功
  res.json({
    code: 0,
    message: '登录成功',
    data: {
      accessToken: newSession.token,
      refreshToken: 'mock-refresh-token-' + Date.now(),
      tokenType: 'Bearer',
      expiresIn: 86400,
      userInfo: {
        userId: user.userId,
        username: user.username,
        realName: user.realName,
        email: user.email,
        phone: user.phone,
        roles: username === 'admin' ? ['SYSTEM_ADMIN', 'DATA_ADMIN'] : ['USER'],
        permissions: username === 'admin' ? ['*'] : ['user:view', 'asset:view'],
        mfaEnabled: false
      }
    }
  });
});

app.post('/api/auth/logout', (req, res) => {
  // 删除会话记录（实际应该根据token删除）
  if (mockSessions.length > 1) {
    mockSessions.pop(); // 删除最后一个会话
  }
  
  // 记录登出审计日志
  addAuditLog({
    operationType: 'LOGOUT',
    module: '认证模块',
    objectType: '用户',
    description: '用户登出',
    result: 'SUCCESS',
    url: '/api/auth/logout',
    method: 'POST'
  });
  
  res.json({ code: 0, message: '登出成功' });
});

app.get('/api/auth/userInfo', (req, res) => {
  res.json({
    code: 0,
    data: {
      userId: 1,
      username: 'admin',
      realName: '系统管理员',
      email: 'admin@example.com',
      roles: ['SYSTEM_ADMIN', 'DATA_ADMIN'],
    },
  });
});

// 更新个人信息
app.put('/api/auth/updateProfile', (req, res) => {
  const { realName, email, phone } = req.body;
  res.json({
    code: 0,
    message: '个人信息更新成功',
    data: {
      userId: 1,
      username: 'admin',
      realName: realName || '系统管理员',
      email: email || 'admin@example.com',
      phone: phone || '13800138000',
      roles: ['SYSTEM_ADMIN', 'DATA_ADMIN'],
    }
  });
});

// 修改密码
app.post('/api/auth/changePassword', (req, res) => {
  const { oldPassword, newPassword, userId } = req.body;

  // 管理员重置用户密码
  if (userId && newPassword) {
    // 确保userId是数字类型
    const userIdNum = parseInt(userId);
    
    // 验证用户是否存在
    const userIndex = mockUsers.findIndex(u => u.userId === userIdNum);
    if (userIndex === -1) {
      return res.json({ code: 404, message: '用户不存在' });
    }
    
    // 记录审计日志
    const user = mockUsers[userIndex];
    addAuditLog({
      operationType: 'UPDATE',
      module: '用户管理',
      objectType: '用户',
      objectId: userIdNum,
      objectName: user.username,
      description: `重置用户密码：${user.realName}`,
      result: 'SUCCESS',
      url: '/api/auth/changePassword',
      method: 'POST'
    });
    
    return res.json({ code: 0, message: '密码重置成功' });
  }

  // 用户自己修改密码
  if (!oldPassword || !newPassword) {
    return res.json({ code: 400, message: '请输入当前密码和新密码' });
  }

  if (oldPassword === newPassword) {
    return res.json({ code: 400, message: '新密码不能与当前密码相同' });
  }

  // 记录审计日志
  addAuditLog({
    operationType: 'UPDATE',
    module: '认证模块',
    objectType: '用户',
    description: '用户修改密码',
    result: 'SUCCESS',
    url: '/api/auth/changePassword',
    method: 'POST'
  });

  res.json({ code: 0, message: '密码修改成功' });
});

// 获取当前用户信息
app.get('/api/auth/current-user', (req, res) => {
  res.json({
    code: 0,
    data: {
      userId: 1,
      username: 'admin',
      realName: '系统管理员',
      email: 'admin@example.com',
      phone: '13800138000',
      roles: ['SYSTEM_ADMIN', 'DATA_ADMIN'],
      permissions: ['*'],
      mfaEnabled: false
    }
  });
});

// 刷新Token
app.post('/api/auth/refresh-token', (req, res) => {
  const { refreshToken } = req.body;

  if (!refreshToken) {
    return res.json({ code: 400, message: '刷新令牌不能为空' });
  }

  res.json({
    code: 0,
    data: {
      accessToken: 'mock-access-token-' + Date.now(),
      refreshToken: 'mock-refresh-token-' + Date.now(),
      tokenType: 'Bearer',
      expiresIn: 86400
    }
  });
});

// 验证Token
app.post('/api/auth/validate-token', (req, res) => {
  res.json({
    code: 0,
    data: {
      valid: true
    }
  });
});

// 撤销Token
app.post('/api/auth/revoke-token', (req, res) => {
  res.json({ code: 0, message: 'Token已撤销' });
});

// 用户管理
app.get('/api/user/list', (req, res) => {
  const { pageNum = 1, pageSize = 10, username, status } = req.query;
  let filteredUsers = [...mockUsers];
  
  if (username) {
    filteredUsers = filteredUsers.filter(u => u.username.includes(username));
  }
  if (status) {
    filteredUsers = filteredUsers.filter(u => u.status === status);
  }
  
  const start = (pageNum - 1) * pageSize;
  const end = start + parseInt(pageSize);
  const list = filteredUsers.slice(start, end);
  
  // 记录审计日志
  addAuditLog({
    operationType: 'QUERY',
    module: '用户管理',
    objectType: '用户',
    description: `查询用户列表（共${filteredUsers.length}条）`,
    result: 'SUCCESS',
    url: '/api/user/list',
    method: 'GET'
  });
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredUsers.length,
    },
  });
});

app.post('/api/user/create', (req, res) => {
  // 计算新的用户ID（取最大ID + 1）
  const maxId = Math.max(...mockUsers.map(u => u.userId), 0);
  const newUser = {
    ...req.body,
    userId: maxId + 1,
    status: req.body.status || 'ACTIVE',
    roleIds: req.body.roleIds || [5], // 默认分配普通用户角色
  };
  mockUsers.push(newUser);
  
  // 添加用户角色关联
  if (newUser.roleIds && newUser.roleIds.length > 0) {
    newUser.roleIds.forEach(roleId => {
      mockUserRoles.push({ userId: newUser.userId, roleId });
    });
  }
  
  // 记录审计日志
  addAuditLog({
    operationType: 'CREATE',
    module: '用户管理',
    objectType: '用户',
    objectId: newUser.userId,
    objectName: newUser.username,
    description: `创建用户：${newUser.realName}`,
    result: 'SUCCESS',
    url: '/api/user/create',
    method: 'POST'
  });
  
  res.json({ code: 0, message: '用户创建成功', data: newUser });
});

app.put('/api/user/update', (req, res) => {
  const { userId } = req.body;
  const index = mockUsers.findIndex(u => u.userId === userId);
  if (index !== -1) {
    mockUsers[index] = { ...mockUsers[index], ...req.body };
    res.json({ code: 0, message: '用户更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '用户不存在' });
  }
});

app.delete('/api/user/delete/:userId', (req, res) => {
  const { userId } = req.params;
  const index = mockUsers.findIndex(u => u.userId === parseInt(userId));
  if (index !== -1) {
    const deletedUser = mockUsers[index];
    mockUsers.splice(index, 1);
    
    // 删除用户角色关联
    const userRoleIndices = [];
    mockUserRoles.forEach((ur, idx) => {
      if (ur.userId === parseInt(userId)) {
        userRoleIndices.push(idx);
      }
    });
    userRoleIndices.reverse().forEach(idx => mockUserRoles.splice(idx, 1));
    
    // 记录审计日志
    addAuditLog({
      operationType: 'DELETE',
      module: '用户管理',
      objectType: '用户',
      objectId: parseInt(userId),
      objectName: deletedUser.username,
      description: `删除用户：${deletedUser.realName}`,
      result: 'SUCCESS',
      url: '/api/user/delete/' + userId,
      method: 'DELETE'
    });
    
    res.json({ code: 0, message: '用户删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '用户不存在' });
  }
});

// 角色管理
app.get('/api/role/list', (req, res) => {
  const { pageNum = 1, pageSize = 10, roleName, status } = req.query;
  let filteredRoles = [...mockRoles];
  
  if (roleName) {
    filteredRoles = filteredRoles.filter(r => r.roleName.includes(roleName));
  }
  if (status) {
    filteredRoles = filteredRoles.filter(r => r.status === status);
  }
  
  // 为每个角色添加用户数量统计
  const rolesWithUserCount = filteredRoles.map(role => {
    const userCount = mockUserRoles.filter(ur => ur.roleId === role.roleId).length;
    return {
      ...role,
      userCount
    };
  });
  
  const start = (pageNum - 1) * pageSize;
  const end = start + parseInt(pageSize);
  const list = rolesWithUserCount.slice(start, end);
  
  // 记录审计日志
  addAuditLog({
    operationType: 'QUERY',
    module: '角色管理',
    objectType: '角色',
    description: `查询角色列表（共${filteredRoles.length}条）`,
    result: 'SUCCESS',
    url: '/api/role/list',
    method: 'GET'
  });
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredRoles.length,
    },
  });
});

app.post('/api/role/create', (req, res) => {
  // 计算新的角色ID（取最大ID + 1）
  const maxId = Math.max(...mockRoles.map(r => r.roleId), 0);
  const newRole = {
    ...req.body,
    roleId: maxId + 1,
    status: req.body.status || 'ACTIVE',
  };
  mockRoles.push(newRole);
  
  // 记录审计日志
  addAuditLog({
    operationType: 'CREATE',
    module: '角色管理',
    objectType: '角色',
    objectId: newRole.roleId,
    objectName: newRole.roleName,
    description: `创建角色：${newRole.roleName}`,
    result: 'SUCCESS',
    url: '/api/role/create',
    method: 'POST'
  });
  
  res.json({ code: 0, message: '角色创建成功', data: newRole });
});

app.put('/api/role/update', (req, res) => {
  const { roleId } = req.body;
  const index = mockRoles.findIndex(r => r.roleId === roleId);
  if (index !== -1) {
    const oldRole = mockRoles[index];
    mockRoles[index] = { ...mockRoles[index], ...req.body };
    
    // 记录审计日志
    addAuditLog({
      operationType: 'UPDATE',
      module: '角色管理',
      objectType: '角色',
      objectId: roleId,
      objectName: oldRole.roleName,
      description: `更新角色：${oldRole.roleName}`,
      result: 'SUCCESS',
      url: '/api/role/update',
      method: 'PUT'
    });
    
    res.json({ code: 0, message: '角色更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '角色不存在' });
  }
});

app.delete('/api/role/delete/:roleId', (req, res) => {
  const { roleId } = req.params;
  const index = mockRoles.findIndex(r => r.roleId === parseInt(roleId));
  if (index !== -1) {
    const deletedRole = mockRoles[index];
    mockRoles.splice(index, 1);
    
    // 记录审计日志
    addAuditLog({
      operationType: 'DELETE',
      module: '角色管理',
      objectType: '角色',
      objectId: parseInt(roleId),
      objectName: deletedRole.roleName,
      description: `删除角色：${deletedRole.roleName}`,
      result: 'SUCCESS',
      url: '/api/role/delete/' + roleId,
      method: 'DELETE'
    });
    
    res.json({ code: 0, message: '角色删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '角色不存在' });
  }
});

// 获取角色权限
app.get('/api/role/permissions/:roleId', (req, res) => {
  const { roleId } = req.params;
  // 模拟返回角色的权限ID列表
  // 系统管理员角色拥有所有权限
  if (parseInt(roleId) === 1) {
    const allPermissionIds = mockPermissions.map(p => p.permissionId);
    res.json({
      code: 0,
      data: allPermissionIds
    });
  } else {
    // 其他角色返回部分权限
    res.json({
      code: 0,
      data: [1, 2, 3, 4, 14, 15] // 示例权限ID
    });
  }
});

// 分配权限
app.post('/api/role/assignPermissions', (req, res) => {
  const { roleId, permissionIds } = req.body;

  if (!roleId || !permissionIds) {
    return res.json({ code: 400, message: '参数不完整' });
  }

  // 模拟分配权限（实际应该保存到数据库）
  res.json({ code: 0, message: '权限分配成功' });
});

// 权限管理
// 构建权限树形结构的辅助函数
function buildPermissionTree(permissions, parentId = null) {
  return permissions
    .filter(p => p.parentId === parentId)
    .map(p => ({
      ...p,
      children: buildPermissionTree(permissions, p.permissionId)
    }));
}

app.get('/api/permission/list', (req, res) => {
  const { pageNum = 1, pageSize = 10, permissionName, status } = req.query;
  let filteredPermissions = [...mockPermissions];

  if (permissionName) {
    filteredPermissions = filteredPermissions.filter(p => p.permissionName.includes(permissionName));
  }
  if (status) {
    filteredPermissions = filteredPermissions.filter(p => p.status === status);
  }

  // 记录审计日志
  addAuditLog({
    operationType: 'QUERY',
    module: '权限管理',
    objectType: '权限',
    description: `查询权限列表（共${filteredPermissions.length}条）`,
    result: 'SUCCESS',
    url: '/api/permission/list',
    method: 'GET'
  });

  // 如果请求的是大量数据（pageSize=1000），返回树形结构
  if (pageSize == 1000) {
    const tree = buildPermissionTree(filteredPermissions);
    res.json({
      code: 0,
      data: {
        list: tree,
        total: filteredPermissions.length,
      },
    });
  } else {
    // 否则返回分页列表
    const start = (pageNum - 1) * pageSize;
    const end = start + parseInt(pageSize);
    const list = filteredPermissions.slice(start, end);

    res.json({
      code: 0,
      data: {
        list,
        total: filteredPermissions.length,
      },
    });
  }
});

// 获取权限树
app.get('/api/permission/tree', (req, res) => {
  const tree = buildPermissionTree(mockPermissions);
  res.json({ code: 0, data: tree });
});

// 更新权限
app.put('/api/permission/update', (req, res) => {
  const { permissionId, status, permissionName, permissionCode, sortOrder } = req.body;
  
  const index = mockPermissions.findIndex(p => p.permissionId === permissionId);
  if (index === -1) {
    return res.status(404).json({ code: 404, message: '权限不存在' });
  }

  const oldPermission = mockPermissions[index];
  
  // 更新权限信息
  if (status) mockPermissions[index].status = status;
  if (permissionName) mockPermissions[index].permissionName = permissionName;
  if (permissionCode) mockPermissions[index].permissionCode = permissionCode;
  if (sortOrder !== undefined) mockPermissions[index].sortOrder = sortOrder;

  // 记录审计日志
  addAuditLog({
    operationType: 'UPDATE',
    module: '权限管理',
    objectType: '权限',
    objectId: permissionId,
    objectName: oldPermission.permissionName,
    description: `更新权限：${oldPermission.permissionName}`,
    result: 'SUCCESS',
    url: '/api/permission/update',
    method: 'PUT'
  });

  res.json({ code: 0, message: '权限更新成功' });
});

// 部门管理
app.get('/api/department/tree', (req, res) => {
  const { departmentName, status } = req.query;
  let filteredDepartments = [...mockDepartments];

  if (departmentName) {
    filteredDepartments = filteredDepartments.filter(d => d.departmentName.includes(departmentName));
  }
  if (status) {
    filteredDepartments = filteredDepartments.filter(d => d.status === status);
  }

  res.json({ code: 0, data: filteredDepartments });
});

app.get('/api/department/list', (req, res) => {
  const { pageNum = 1, pageSize = 10, departmentName, status } = req.query;
  let filteredDepartments = [...mockDepartments];
  
  if (departmentName) {
    filteredDepartments = filteredDepartments.filter(d => d.departmentName.includes(departmentName));
  }
  if (status) {
    filteredDepartments = filteredDepartments.filter(d => d.status === status);
  }
  
  const start = (pageNum - 1) * pageSize;
  const end = start + parseInt(pageSize);
  const list = filteredDepartments.slice(start, end);
  
  // 记录审计日志
  addAuditLog({
    operationType: 'QUERY',
    module: '部门管理',
    objectType: '部门',
    description: `查询部门列表（共${filteredDepartments.length}条）`,
    result: 'SUCCESS',
    url: '/api/department/list',
    method: 'GET'
  });
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredDepartments.length,
    },
  });
});

app.post('/api/department/create', (req, res) => {
  const maxId = Math.max(...mockDepartments.map(d => d.departmentId), 0);
  const newDepartment = {
    ...req.body,
    departmentId: maxId + 1,
    status: req.body.status || 'ACTIVE',
  };
  mockDepartments.push(newDepartment);
  res.json({ code: 0, message: '部门创建成功', data: newDepartment });
});

app.put('/api/department/update', (req, res) => {
  const { departmentId } = req.body;
  const index = mockDepartments.findIndex(d => d.departmentId === departmentId);
  if (index !== -1) {
    mockDepartments[index] = { ...mockDepartments[index], ...req.body };
    res.json({ code: 0, message: '部门更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '部门不存在' });
  }
});

app.delete('/api/department/delete/:departmentId', (req, res) => {
  const { departmentId } = req.params;
  const index = mockDepartments.findIndex(d => d.departmentId === parseInt(departmentId));
  if (index !== -1) {
    mockDepartments.splice(index, 1);
    res.json({ code: 0, message: '部门删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '部门不存在' });
  }
});

// 责任人管理
app.get('/api/owner/list', (req, res) => {
  const { pageNum = 1, pageSize = 10, name, status } = req.query;
  let filteredOwners = [...mockOwners];
  
  if (name) {
    filteredOwners = filteredOwners.filter(o => o.name.includes(name));
  }
  if (status) {
    filteredOwners = filteredOwners.filter(o => o.status === status);
  }
  
  const start = (pageNum - 1) * pageSize;
  const end = start + parseInt(pageSize);
  const list = filteredOwners.slice(start, end);
  
  // 记录审计日志
  addAuditLog({
    operationType: 'QUERY',
    module: '责任人管理',
    objectType: '责任人',
    description: `查询责任人列表（共${filteredOwners.length}条）`,
    result: 'SUCCESS',
    url: '/api/owner/list',
    method: 'GET'
  });
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredOwners.length,
    },
  });
});

app.post('/api/owner/create', (req, res) => {
  const maxId = Math.max(...mockOwners.map(o => o.ownerId), 0);
  const newOwner = {
    ...req.body,
    ownerId: maxId + 1,
    status: req.body.status || 'ACTIVE',
  };
  mockOwners.push(newOwner);
  res.json({ code: 0, message: '责任人创建成功', data: newOwner });
});

app.put('/api/owner/update', (req, res) => {
  const { ownerId } = req.body;
  const index = mockOwners.findIndex(o => o.ownerId === ownerId);
  if (index !== -1) {
    mockOwners[index] = { ...mockOwners[index], ...req.body };
    res.json({ code: 0, message: '责任人更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '责任人不存在' });
  }
});

app.delete('/api/owner/delete/:ownerId', (req, res) => {
  const { ownerId } = req.params;
  const index = mockOwners.findIndex(o => o.ownerId === parseInt(ownerId));
  if (index !== -1) {
    mockOwners.splice(index, 1);
    res.json({ code: 0, message: '责任人删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '责任人不存在' });
  }
});

// 分类标准管理
app.get('/api/classificationStandard/list', (req, res) => {
  const { pageNum = 1, pageSize = 10, standardName, status } = req.query;
  let filteredStandards = [...mockClassificationStandards];
  
  if (standardName) {
    filteredStandards = filteredStandards.filter(s => s.standardName.includes(standardName));
  }
  if (status) {
    filteredStandards = filteredStandards.filter(s => s.status === status);
  }
  
  const start = (pageNum - 1) * pageSize;
  const end = start + parseInt(pageSize);
  const list = filteredStandards.slice(start, end);
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredStandards.length,
    },
  });
});

app.post('/api/classificationStandard/create', (req, res) => {
  const maxId = Math.max(...mockClassificationStandards.map(s => s.standardId), 0);
  const newStandard = {
    ...req.body,
    standardId: maxId + 1,
    status: req.body.status || 'DRAFT',
  };
  mockClassificationStandards.push(newStandard);
  res.json({ code: 0, message: '分类标准创建成功', data: newStandard });
});

app.put('/api/classificationStandard/update', (req, res) => {
  const { standardId } = req.body;
  const index = mockClassificationStandards.findIndex(s => s.standardId === standardId);
  if (index !== -1) {
    mockClassificationStandards[index] = { ...mockClassificationStandards[index], ...req.body };
    res.json({ code: 0, message: '分类标准更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '分类标准不存在' });
  }
});

app.delete('/api/classificationStandard/delete/:standardId', (req, res) => {
  const { standardId } = req.params;
  const index = mockClassificationStandards.findIndex(s => s.standardId === parseInt(standardId));
  if (index !== -1) {
    mockClassificationStandards.splice(index, 1);
    res.json({ code: 0, message: '分类标准删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '分类标准不存在' });
  }
});

// 数据分类管理
app.get('/api/classification/tree/:standardId', (req, res) => {
  const { standardId } = req.params;
  const tree = buildTree(mockClassifications.filter(c => c.standardId === parseInt(standardId)));
  res.json({ code: 0, data: tree });
});

app.get('/api/classification/list', (req, res) => {
  const { pageNum = 1, pageSize = 10, classificationName, status } = req.query;
  let filteredClassifications = [...mockClassifications];
  
  if (classificationName) {
    filteredClassifications = filteredClassifications.filter(c => c.classificationName.includes(classificationName));
  }
  if (status) {
    filteredClassifications = filteredClassifications.filter(c => c.status === status);
  }
  
  const start = (pageNum - 1) * pageSize;
  const end = start + parseInt(pageSize);
  const list = filteredClassifications.slice(start, end);
  
  // 记录审计日志
  addAuditLog({
    operationType: 'QUERY',
    module: '分类管理',
    objectType: '数据分类',
    description: `查询数据分类列表（共${filteredClassifications.length}条）`,
    result: 'SUCCESS',
    url: '/api/classification/list',
    method: 'GET'
  });
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredClassifications.length,
    },
  });
});

app.post('/api/classification/create', (req, res) => {
  const maxId = Math.max(...mockClassifications.map(c => c.classificationId), 0);
  const newClassification = {
    ...req.body,
    classificationId: maxId + 1,
    status: req.body.status || 'ACTIVE',
  };
  mockClassifications.push(newClassification);
  
  // 记录审计日志
  addAuditLog({
    operationType: 'CREATE',
    module: '分类管理',
    objectType: '数据分类',
    objectId: newClassification.classificationId,
    objectName: newClassification.classificationName,
    description: `创建数据分类：${newClassification.classificationName}`,
    result: 'SUCCESS',
    url: '/api/classification/create',
    method: 'POST'
  });
  
  res.json({ code: 0, message: '数据分类创建成功', data: newClassification });
});

app.put('/api/classification/update', (req, res) => {
  const { classificationId } = req.body;
  const index = mockClassifications.findIndex(c => c.classificationId === classificationId);
  if (index !== -1) {
    const oldClassification = mockClassifications[index];
    mockClassifications[index] = { ...mockClassifications[index], ...req.body };
    
    // 记录审计日志
    addAuditLog({
      operationType: 'UPDATE',
      module: '分类管理',
      objectType: '数据分类',
      objectId: classificationId,
      objectName: oldClassification.classificationName,
      description: `更新数据分类：${oldClassification.classificationName}`,
      result: 'SUCCESS',
      url: '/api/classification/update',
      method: 'PUT'
    });
    
    res.json({ code: 0, message: '数据分类更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '数据分类不存在' });
  }
});

app.delete('/api/classification/delete/:classificationId', (req, res) => {
  const { classificationId } = req.params;
  const index = mockClassifications.findIndex(c => c.classificationId === parseInt(classificationId));
  if (index !== -1) {
    const deletedClassification = mockClassifications[index];
    mockClassifications.splice(index, 1);
    
    // 记录审计日志
    addAuditLog({
      operationType: 'DELETE',
      module: '分类管理',
      objectType: '数据分类',
      objectId: parseInt(classificationId),
      objectName: deletedClassification.classificationName,
      description: `删除数据分类：${deletedClassification.classificationName}`,
      result: 'SUCCESS',
      url: '/api/classification/delete/' + classificationId,
      method: 'DELETE'
    });
    
    res.json({ code: 0, message: '数据分类删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '数据分类不存在' });
  }
});

// 分级标准管理
app.get('/api/gradingStandard/list', (req, res) => {
  const { pageNum = 1, pageSize = 10, standardName, status } = req.query;
  let filteredStandards = [...mockGradingStandards];
  
  if (standardName) {
    filteredStandards = filteredStandards.filter(s => s.standardName.includes(standardName));
  }
  if (status) {
    filteredStandards = filteredStandards.filter(s => s.status === status);
  }
  
  const start = (pageNum - 1) * pageSize;
  const end = start + parseInt(pageSize);
  const list = filteredStandards.slice(start, end);
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredStandards.length,
    },
  });
});

app.post('/api/gradingStandard/create', (req, res) => {
  const maxId = Math.max(...mockGradingStandards.map(s => s.standardId), 0);
  const newStandard = {
    ...req.body,
    standardId: maxId + 1,
    status: req.body.status || 'DRAFT',
  };
  mockGradingStandards.push(newStandard);
  res.json({ code: 0, message: '分级标准创建成功', data: newStandard });
});

app.put('/api/gradingStandard/update', (req, res) => {
  const { standardId } = req.body;
  const index = mockGradingStandards.findIndex(s => s.standardId === standardId);
  if (index !== -1) {
    mockGradingStandards[index] = { ...mockGradingStandards[index], ...req.body };
    res.json({ code: 0, message: '分级标准更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '分级标准不存在' });
  }
});

app.delete('/api/gradingStandard/delete/:standardId', (req, res) => {
  const { standardId } = req.params;
  const index = mockGradingStandards.findIndex(s => s.standardId === parseInt(standardId));
  if (index !== -1) {
    mockGradingStandards.splice(index, 1);
    res.json({ code: 0, message: '分级标准删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '分级标准不存在' });
  }
});

// 数据分级管理
app.get('/api/grading/list', (req, res) => {
  const { pageNum = 1, pageSize = 10, gradingName, status } = req.query;
  let filteredGradings = [...mockGradings];
  
  if (gradingName) {
    filteredGradings = filteredGradings.filter(g => g.gradingName.includes(gradingName));
  }
  if (status) {
    filteredGradings = filteredGradings.filter(g => g.status === status);
  }
  
  const start = (pageNum - 1) * pageSize;
  const end = start + parseInt(pageSize);
  const list = filteredGradings.slice(start, end);
  
  // 记录审计日志
  addAuditLog({
    operationType: 'QUERY',
    module: '分级管理',
    objectType: '数据分级',
    description: `查询数据分级列表（共${filteredGradings.length}条）`,
    result: 'SUCCESS',
    url: '/api/grading/list',
    method: 'GET'
  });
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredGradings.length,
    },
  });
});

app.post('/api/grading/create', (req, res) => {
  const maxId = Math.max(...mockGradings.map(g => g.gradingId), 0);
  const newGrading = {
    ...req.body,
    gradingId: maxId + 1,
    status: req.body.status || 'ACTIVE',
  };
  mockGradings.push(newGrading);
  
  // 记录审计日志
  addAuditLog({
    operationType: 'CREATE',
    module: '分级管理',
    objectType: '数据分级',
    objectId: newGrading.gradingId,
    objectName: newGrading.gradingName,
    description: `创建数据分级：${newGrading.gradingName}`,
    result: 'SUCCESS',
    url: '/api/grading/create',
    method: 'POST'
  });
  
  res.json({ code: 0, message: '数据分级创建成功', data: newGrading });
});

app.put('/api/grading/update', (req, res) => {
  const { gradingId } = req.body;
  const index = mockGradings.findIndex(g => g.gradingId === gradingId);
  if (index !== -1) {
    const oldGrading = mockGradings[index];
    mockGradings[index] = { ...mockGradings[index], ...req.body };
    
    // 记录审计日志
    addAuditLog({
      operationType: 'UPDATE',
      module: '分级管理',
      objectType: '数据分级',
      objectId: gradingId,
      objectName: oldGrading.gradingName,
      description: `更新数据分级：${oldGrading.gradingName}`,
      result: 'SUCCESS',
      url: '/api/grading/update',
      method: 'PUT'
    });
    
    res.json({ code: 0, message: '数据分级更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '数据分级不存在' });
  }
});

app.delete('/api/grading/delete/:gradingId', (req, res) => {
  const { gradingId } = req.params;
  const index = mockGradings.findIndex(g => g.gradingId === parseInt(gradingId));
  if (index !== -1) {
    const deletedGrading = mockGradings[index];
    mockGradings.splice(index, 1);
    
    // 记录审计日志
    addAuditLog({
      operationType: 'DELETE',
      module: '分级管理',
      objectType: '数据分级',
      objectId: parseInt(gradingId),
      objectName: deletedGrading.gradingName,
      description: `删除数据分级：${deletedGrading.gradingName}`,
      result: 'SUCCESS',
      url: '/api/grading/delete/' + gradingId,
      method: 'DELETE'
    });
    
    res.json({ code: 0, message: '数据分级删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '数据分级不存在' });
  }
});

// 数据资产管理
app.get('/api/asset/list', (req, res) => {
  const { pageNum = 1, pageSize = 10, assetName, assetCode, assetType, status } = req.query;
  let filteredAssets = [...mockAssets];

  if (assetName) {
    filteredAssets = filteredAssets.filter(a => a.assetName.includes(assetName));
  }
  if (assetCode) {
    filteredAssets = filteredAssets.filter(a => a.assetCode.includes(assetCode));
  }
  if (assetType) {
    filteredAssets = filteredAssets.filter(a => a.assetType === assetType);
  }
  if (status) {
    filteredAssets = filteredAssets.filter(a => a.status === status);
  }

  const start = (pageNum - 1) * pageSize;
  const end = start + parseInt(pageSize);
  const list = filteredAssets.slice(start, end);

  // 记录审计日志
  addAuditLog({
    operationType: 'QUERY',
    module: '数据资产管理',
    objectType: '数据资产',
    description: `查询数据资产列表（共${filteredAssets.length}条）`,
    result: 'SUCCESS',
    url: '/api/asset/list',
    method: 'GET'
  });

  res.json({
    code: 0,
    data: {
      list,
      total: filteredAssets.length,
    },
  });
});

// 资产详情
app.get('/api/asset/detail/:assetId', (req, res) => {
  const assetId = parseInt(req.params.assetId);
  const asset = mockAssets.find(a => a.assetId === assetId);
  if (!asset) {
    return res.json({ code: 404, message: '资产不存在' });
  }
  res.json({ code: 0, message: '操作成功', data: { ...asset } });
});

app.post('/api/asset/create', (req, res) => {
  const maxId = Math.max(...mockAssets.map(a => a.assetId), 0);
  const newAsset = {
    ...req.body,
    assetId: maxId + 1,
    status: req.body.status || 'DRAFT',
  };
  mockAssets.push(newAsset);
  
  // 记录审计日志
  addAuditLog({
    operationType: 'CREATE',
    module: '数据资产管理',
    objectType: '数据资产',
    objectId: newAsset.assetId,
    objectName: newAsset.assetName,
    description: `创建数据资产：${newAsset.assetName}`,
    result: 'SUCCESS',
    url: '/api/asset/create',
    method: 'POST'
  });
  
  res.json({ code: 0, message: '数据资产创建成功', data: newAsset });
});

app.put('/api/asset/update', (req, res) => {
  const { assetId } = req.body;
  const index = mockAssets.findIndex(a => a.assetId === assetId);
  if (index !== -1) {
    const oldAsset = mockAssets[index];
    mockAssets[index] = { ...mockAssets[index], ...req.body };
    
    // 记录审计日志
    addAuditLog({
      operationType: 'UPDATE',
      module: '数据资产管理',
      objectType: '数据资产',
      objectId: assetId,
      objectName: oldAsset.assetName,
      description: `更新数据资产：${oldAsset.assetName}`,
      result: 'SUCCESS',
      url: '/api/asset/update',
      method: 'PUT'
    });
    
    res.json({ code: 0, message: '数据资产更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '数据资产不存在' });
  }
});

app.delete('/api/asset/delete/:assetId', (req, res) => {
  const { assetId } = req.params;
  const index = mockAssets.findIndex(a => a.assetId === parseInt(assetId));
  if (index !== -1) {
    const deletedAsset = mockAssets[index];
    mockAssets.splice(index, 1);
    
    // 记录审计日志
    addAuditLog({
      operationType: 'DELETE',
      module: '数据资产管理',
      objectType: '数据资产',
      objectId: parseInt(assetId),
      objectName: deletedAsset.assetName,
      description: `删除数据资产：${deletedAsset.assetName}`,
      result: 'SUCCESS',
      url: '/api/asset/delete/' + assetId,
      method: 'DELETE'
    });
    
    res.json({ code: 0, message: '数据资产删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '数据资产不存在' });
  }
});

// 下载批量导入模板
app.get('/api/asset/import-template', (req, res) => {
  // 记录审计日志
  addAuditLog({
    operationType: 'QUERY',
    module: '数据资产管理',
    objectType: '导入模板',
    description: '下载批量导入模板',
    result: 'SUCCESS',
    url: '/api/asset/import-template',
    method: 'GET'
  });
  
  // 返回模板文件信息
  res.json({
    code: 0,
    data: {
      templateName: '数据资产批量导入模板.csv',
      templateUrl: '/templates/asset-import-template.csv',
      columns: [
        { name: '资产名称', field: 'assetName', required: true, example: '客户信息表' },
        { name: '资产编码', field: 'assetCode', required: true, example: 'ASSET001' },
        { name: '资产类型', field: 'assetType', required: true, example: 'DATABASE/TABLE/FILE/API' },
        { name: '数据库类型', field: 'databaseType', required: false, example: 'MySQL/Oracle/PostgreSQL' },
        { name: '数据库名称', field: 'databaseName', required: false, example: 'crm_db' },
        { name: '表名', field: 'tableName', required: false, example: 'customer_info' },
        { name: '所属系统', field: 'systemName', required: false, example: 'CRM系统' },
        { name: '部门ID', field: 'departmentId', required: false, example: '1' },
        { name: '责任人ID', field: 'ownerId', required: false, example: '1' },
        { name: '分类ID', field: 'classificationId', required: false, example: '1' },
        { name: '分级ID', field: 'gradingId', required: false, example: '1' },
        { name: '描述', field: 'description', required: false, example: '资产描述' }
      ]
    }
  });
});

// 批量导入数据资产
app.post('/api/asset/import', upload.single('file'), (req, res) => {
  try {
    let importData = [];
    const fs = require('fs');
    
    // 如果有上传文件，解析文件内容
    if (req.file) {
      const fileExt = req.file.originalname.split('.').pop().toLowerCase();
      
      if (fileExt === 'csv') {
        // 解析CSV文件
        const fileContent = fs.readFileSync(req.file.path, 'utf-8');
        const lines = fileContent.split('\n').filter(line => line.trim());
        if (lines.length > 1) {
          // 跳过表头，从第二行开始解析数据
          for (let i = 1; i < lines.length; i++) {
            const values = lines[i].split(',').map(v => v.trim());
            if (values.length >= 3) {
              importData.push({
                assetName: values[0],
                assetCode: values[1],
                assetType: values[2],
                databaseType: values[3] || '',
                databaseName: values[4] || '',
                tableName: values[5] || '',
                systemName: values[6] || '',
                departmentId: values[7] || null,
                ownerId: values[8] || null,
                classificationId: values[9] || null,
                gradingId: values[10] || null,
                description: values[11] || ''
              });
            }
          }
        }
      } else if (fileExt === 'xlsx' || fileExt === 'xls') {
        // 解析Excel文件
        const workbook = XLSX.readFile(req.file.path);
        const sheetName = workbook.SheetNames[0];
        const worksheet = workbook.Sheets[sheetName];
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
        
        if (jsonData.length > 1) {
          // 跳过表头，从第二行开始解析数据
          for (let i = 1; i < jsonData.length; i++) {
            const values = jsonData[i];
            if (values && values.length >= 3) {
              importData.push({
                assetName: String(values[0] || '').trim(),
                assetCode: String(values[1] || '').trim(),
                assetType: String(values[2] || '').trim(),
                databaseType: String(values[3] || '').trim(),
                databaseName: String(values[4] || '').trim(),
                tableName: String(values[5] || '').trim(),
                systemName: String(values[6] || '').trim(),
                departmentId: values[7] || null,
                ownerId: values[8] || null,
                classificationId: values[9] || null,
                gradingId: values[10] || null,
                description: String(values[11] || '').trim()
              });
            }
          }
        }
      }
      
      // 删除临时文件
      fs.unlinkSync(req.file.path);
    }
    
    let successCount = 0;
    let failCount = 0;
    const errors = [];
    const importedAssets = [];
    
    // 如果没有解析到数据，使用示例数据
    if (importData.length === 0) {
      importData = [
        { assetName: '导入资产1', assetCode: 'IMPORT001', assetType: 'DATABASE', systemName: '测试系统' },
        { assetName: '导入资产2', assetCode: 'IMPORT002', assetType: 'TABLE', systemName: '测试系统' },
        { assetName: '导入资产3', assetCode: 'IMPORT003', assetType: 'FILE', systemName: '测试系统' }
      ];
    }
    
    // 批量创建数据资产
    importData.forEach((item, index) => {
      try {
        // 验证必填字段
        if (!item.assetName || !item.assetCode || !item.assetType) {
          failCount++;
          errors.push({
            row: index + 1,
            error: '缺少必填字段',
            data: item
          });
          return;
        }
        
        // 检查资产编码是否重复
        const existingAsset = mockAssets.find(a => a.assetCode === item.assetCode);
        if (existingAsset) {
          failCount++;
          errors.push({
            row: index + 1,
            error: `资产编码 ${item.assetCode} 已存在`,
            data: item
          });
          return;
        }
        
        // 创建新资产
        const maxId = Math.max(...mockAssets.map(a => a.assetId), 0);
        const newAsset = {
          assetId: maxId + 1,
          assetName: item.assetName,
          assetCode: item.assetCode,
          assetType: item.assetType,
          databaseType: item.databaseType || '',
          databaseName: item.databaseName || '',
          tableName: item.tableName || '',
          systemName: item.systemName || '',
          departmentId: parseInt(item.departmentId) || null,
          ownerId: parseInt(item.ownerId) || null,
          classificationId: parseInt(item.classificationId) || null,
          gradingId: parseInt(item.gradingId) || null,
          description: item.description || '',
          status: 'DRAFT',
          createTime: new Date().toISOString(),
          updateTime: new Date().toISOString()
        };
        
        mockAssets.push(newAsset);
        importedAssets.push(newAsset);
        successCount++;
        
      } catch (error) {
        failCount++;
        errors.push({
          row: index + 1,
          error: error.message,
          data: item
        });
      }
    });
    
    // 记录审计日志
    addAuditLog({
      operationType: 'CREATE',
      module: '数据资产管理',
      objectType: '数据资产',
      description: `批量导入数据资产（成功${successCount}条，失败${failCount}条）`,
      result: failCount > 0 ? 'PARTIAL_SUCCESS' : 'SUCCESS',
      url: '/api/asset/import',
      method: 'POST'
    });
    
    // 返回导入结果
    res.json({
      code: 0,
      message: `批量导入完成：成功${successCount}条，失败${failCount}条`,
      data: {
        successCount,
        failCount,
        total: importData.length,
        importedAssets,
        errors: errors.length > 0 ? errors : undefined
      }
    });
    
  } catch (error) {
    console.error('批量导入失败:', error);
    
    // 记录失败审计日志
    addAuditLog({
      operationType: 'CREATE',
      module: '数据资产管理',
      objectType: '数据资产',
      description: '批量导入数据资产失败',
      result: 'FAILURE',
      errorMessage: error.message,
      url: '/api/asset/import',
      method: 'POST'
    });
    
    res.json({
      code: 500,
      message: '批量导入失败：' + error.message
    });
  }
});

// 批量导出数据资产
app.get('/api/asset/export', (req, res) => {
  const { assetIds, outputFormat } = req.query;
  
  // 记录审计日志
  addAuditLog({
    operationType: 'EXPORT',
    module: '数据资产管理',
    objectType: '数据资产',
    description: `批量导出数据资产（格式：${outputFormat || 'Excel'}）`,
    result: 'SUCCESS',
    url: '/api/asset/export',
    method: 'GET'
  });
  
  // 返回导出文件信息
  res.json({
    code: 0,
    data: {
      fileName: '数据资产导出_' + new Date().toISOString().split('T')[0] + '.xlsx',
      filePath: '/exports/assets_' + Date.now() + '.xlsx',
      totalCount: mockAssets.length
    }
  });
});

// 字段管理
// 字段批量导入模板下载（必须放在 :assetId 路由前面）
app.get('/api/asset/fields/import-template', (req, res) => {
  // 记录审计日志
  addAuditLog({
    operationType: 'QUERY',
    module: '字段管理',
    objectType: '导入模板',
    description: '下载字段批量导入模板',
    result: 'SUCCESS',
    url: '/api/asset/fields/import-template',
    method: 'GET'
  });
  
  res.json({
    code: 0,
    data: {
      templateName: '字段批量导入模板.csv',
      columns: [
        { name: '所属资产ID', field: 'assetId', required: true, example: '1' },
        { name: '字段名称', field: 'fieldName', required: true, example: '用户ID' },
        { name: '字段编码', field: 'fieldCode', required: true, example: 'user_id' },
        { name: '字段类型', field: 'fieldType', required: true, example: 'STRING/INTEGER/DECIMAL/DATE/BOOLEAN' },
        { name: '是否主键', field: 'isPrimaryKey', required: false, example: 'true/false' },
        { name: '是否可空', field: 'isNullable', required: false, example: 'true/false' },
        { name: '是否敏感', field: 'isSensitive', required: false, example: 'true/false' },
        { name: '分类ID', field: 'classificationId', required: false, example: '1' },
        { name: '分级ID', field: 'gradingId', required: false, example: '1' },
        { name: '描述', field: 'description', required: false, example: '字段描述' }
      ]
    }
  });
});

// 获取资产字段列表
app.get('/api/asset/fields/:assetId', (req, res) => {
  const { assetId } = req.params;
  const fields = mockFields.filter(f => f.assetId === parseInt(assetId));
  res.json({ code: 0, data: fields });
});

// 字段批量导入
app.post('/api/asset/fields/import', upload.single('file'), (req, res) => {
  try {
    let importData = [];
    const fs = require('fs');
    
    // 如果有上传文件，解析文件内容
    if (req.file) {
      const fileExt = req.file.originalname.split('.').pop().toLowerCase();
      
      if (fileExt === 'csv') {
        // 解析CSV文件
        const fileContent = fs.readFileSync(req.file.path, 'utf-8');
        const lines = fileContent.split('\n').filter(line => line.trim());
        if (lines.length > 1) {
          for (let i = 1; i < lines.length; i++) {
            const values = lines[i].split(',').map(v => v.trim());
            if (values.length >= 4) {
              importData.push({
                assetId: values[0],
                fieldName: values[1],
                fieldCode: values[2],
                fieldType: values[3],
                isPrimaryKey: values[4] || 'false',
                isNullable: values[5] || 'true',
                isSensitive: values[6] || 'false',
                classificationId: values[7] || null,
                gradingId: values[8] || null,
                description: values[9] || ''
              });
            }
          }
        }
      } else if (fileExt === 'xlsx' || fileExt === 'xls') {
        // 解析Excel文件
        const workbook = XLSX.readFile(req.file.path);
        const sheetName = workbook.SheetNames[0];
        const worksheet = workbook.Sheets[sheetName];
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
        
        if (jsonData.length > 1) {
          for (let i = 1; i < jsonData.length; i++) {
            const values = jsonData[i];
            if (values && values.length >= 4) {
              importData.push({
                assetId: String(values[0] || '').trim(),
                fieldName: String(values[1] || '').trim(),
                fieldCode: String(values[2] || '').trim(),
                fieldType: String(values[3] || '').trim(),
                isPrimaryKey: String(values[4] || 'false').trim(),
                isNullable: String(values[5] || 'true').trim(),
                isSensitive: String(values[6] || 'false').trim(),
                classificationId: values[7] || null,
                gradingId: values[8] || null,
                description: String(values[9] || '').trim()
              });
            }
          }
        }
      }
      
      fs.unlinkSync(req.file.path);
    }
    
    let successCount = 0;
    let failCount = 0;
    const errors = [];
    const importedFields = [];
    
    // 如果没有解析到数据，使用示例数据
    if (importData.length === 0) {
      importData = [
        { assetId: '1', fieldName: '导入字段1', fieldCode: 'import_field1', fieldType: 'STRING' },
        { assetId: '1', fieldName: '导入字段2', fieldCode: 'import_field2', fieldType: 'INTEGER' },
        { assetId: '1', fieldName: '导入字段3', fieldCode: 'import_field3', fieldType: 'DATE' }
      ];
    }
    
    // 批量创建字段
    importData.forEach((item, index) => {
      try {
        // 验证必填字段
        if (!item.assetId || !item.fieldName || !item.fieldCode || !item.fieldType) {
          failCount++;
          errors.push({ row: index + 1, error: '缺少必填字段', data: item });
          return;
        }
        
        // 检查资产是否存在
        const asset = mockAssets.find(a => a.assetId === parseInt(item.assetId));
        if (!asset) {
          failCount++;
          errors.push({ row: index + 1, error: `资产ID ${item.assetId} 不存在`, data: item });
          return;
        }
        
        // 检查字段编码是否重复
        const existingField = mockFields.find(f => 
          f.assetId === parseInt(item.assetId) && f.fieldCode === item.fieldCode
        );
        if (existingField) {
          failCount++;
          errors.push({ row: index + 1, error: `字段编码 ${item.fieldCode} 已存在`, data: item });
          return;
        }
        
        // 创建新字段
        const maxId = Math.max(...mockFields.map(f => f.fieldId), 0);
        const newField = {
          fieldId: maxId + 1,
          assetId: parseInt(item.assetId),
          fieldName: item.fieldName,
          fieldCode: item.fieldCode,
          fieldType: item.fieldType,
          isPrimaryKey: item.isPrimaryKey === 'true',
          isNullable: item.isNullable !== 'false',
          isSensitive: item.isSensitive === 'true',
          classificationId: parseInt(item.classificationId) || null,
          gradingId: parseInt(item.gradingId) || null,
          description: item.description || '',
          createTime: new Date().toISOString()
        };
        
        mockFields.push(newField);
        importedFields.push(newField);
        successCount++;
        
      } catch (error) {
        failCount++;
        errors.push({ row: index + 1, error: error.message, data: item });
      }
    });
    
    // 记录审计日志
    addAuditLog({
      operationType: 'CREATE',
      module: '字段管理',
      objectType: '字段',
      description: `批量导入字段（成功${successCount}条，失败${failCount}条）`,
      result: failCount > 0 ? 'PARTIAL_SUCCESS' : 'SUCCESS',
      url: '/api/asset/fields/import',
      method: 'POST'
    });
    
    res.json({
      code: 0,
      message: `批量导入完成：成功${successCount}条，失败${failCount}条`,
      data: { successCount, failCount, total: importData.length, importedFields, errors: errors.length > 0 ? errors : undefined }
    });
    
  } catch (error) {
    console.error('批量导入字段失败:', error);
    addAuditLog({
      operationType: 'CREATE',
      module: '字段管理',
      objectType: '字段',
      description: '批量导入字段失败',
      result: 'FAILURE',
      errorMessage: error.message,
      url: '/api/asset/fields/import',
      method: 'POST'
    });
    res.json({ code: 500, message: '批量导入失败：' + error.message });
  }
});

// 统计分析
app.get('/api/statistics/asset', (req, res) => {
  // 记录审计日志
  addAuditLog({
    operationType: 'QUERY',
    module: '统计分析',
    objectType: '统计数据',
    description: '查询资产统计信息',
    result: 'SUCCESS',
    url: '/api/statistics/asset',
    method: 'GET'
  });
  
  res.json({ code: 0, data: mockStatistics });
});

app.get('/api/statistics/trend', (req, res) => {
  const { type, startDate, endDate } = req.query;
  
  // 记录审计日志
  addAuditLog({
    operationType: 'QUERY',
    module: '统计分析',
    objectType: '趋势数据',
    description: `查询趋势数据（类型：${type || '全部'}）`,
    result: 'SUCCESS',
    url: '/api/statistics/trend',
    method: 'GET'
  });
  
  let filteredData = { ...mockTrendData };

  // 根据类型筛选数据
  if (type === 'asset') {
    // 只返回资产相关数据
    filteredData = {
      ...mockTrendData,
      classificationGrowth: mockTrendData.dates.map(() => 0),
      gradingGrowth: mockTrendData.dates.map(() => 0)
    };
  } else if (type === 'classification') {
    // 只返回分类相关数据
    filteredData = {
      ...mockTrendData,
      assetGrowth: mockTrendData.dates.map(() => 0),
      gradingGrowth: mockTrendData.dates.map(() => 0)
    };
  } else if (type === 'grading') {
    // 只返回分级相关数据
    filteredData = {
      ...mockTrendData,
      assetGrowth: mockTrendData.dates.map(() => 0),
      classificationGrowth: mockTrendData.dates.map(() => 0)
    };
  }

  // 根据日期范围筛选数据
  if (startDate && endDate) {
    const startDateObj = new Date(startDate);
    const endDateObj = new Date(endDate);

    const filteredDates = mockTrendData.dates.filter((date) => {
      const dateObj = new Date(date);
      return dateObj >= startDateObj && dateObj <= endDateObj;
    });

    const startIndex = mockTrendData.dates.indexOf(filteredDates[0]);
    const endIndex = mockTrendData.dates.indexOf(filteredDates[filteredDates.length - 1]) + 1;

    filteredData = {
      ...filteredData,
      dates: filteredDates,
      assetGrowth: mockTrendData.assetGrowth.slice(startIndex, endIndex),
      classificationGrowth: mockTrendData.classificationGrowth.slice(startIndex, endIndex),
      gradingGrowth: mockTrendData.gradingGrowth.slice(startIndex, endIndex)
    };
  }

  res.json({ code: 0, data: filteredData });
});

// ==================== 报告相关API ====================

// 生成资产清单报告
app.get('/api/report/asset-list/generate', (req, res) => {
  const { assetType, departmentId, status, includeFieldDetails } = req.query;
  let filteredAssets = [...mockAssets];

  // 筛选逻辑
  if (assetType) {
    filteredAssets = filteredAssets.filter(a => a.assetType === assetType);
  }
  if (status) {
    filteredAssets = filteredAssets.filter(a => a.status === status);
  }

  // 记录审计日志
  addAuditLog({
    operationType: 'CREATE',
    module: '报告管理',
    objectType: '资产清单报告',
    description: `生成资产清单报告（共${filteredAssets.length}条）`,
    result: 'SUCCESS',
    url: '/api/report/asset-list/generate',
    method: 'GET'
  });

  // 添加部门和责任人信息
  const reportData = filteredAssets.map(asset => {
    const department = mockDepartments.find(d => d.departmentId === asset.departmentId);
    const owner = mockOwners.find(o => o.ownerId === asset.ownerId);
    const classification = mockClassifications.find(c => c.classificationId === asset.classificationId);
    const grading = mockGradings.find(g => g.gradingId === asset.gradingId);

    return {
      assetName: asset.assetName,
      assetCode: asset.assetCode,
      assetType: asset.assetType,
      systemName: asset.systemName,
      departmentName: department ? department.departmentName : '',
      ownerName: owner ? owner.name : '',
      classificationName: classification ? classification.classificationName : '',
      gradingName: grading ? grading.gradingName : '',
      status: asset.status,
      createTime: asset.createTime,
      includeFieldDetails: includeFieldDetails === 'true'
    };
  });

  res.json({
    code: 0,
    data: {
      list: reportData,
      total: reportData.length
    }
  });
});

// 导出资产清单报告
app.get('/api/report/asset-list/export', (req, res) => {
  try {
    const { assetType, departmentId, status, outputFormat } = req.query;
    let filteredAssets = [...mockAssets];

    // 筛选逻辑
    if (assetType) {
      filteredAssets = filteredAssets.filter(a => a.assetType === assetType);
    }
    if (status) {
      filteredAssets = filteredAssets.filter(a => a.status === status);
    }

    // 记录审计日志
    addAuditLog({
      operationType: 'EXPORT',
      module: '报告管理',
      objectType: '资产清单报告',
      description: `导出资产清单报告（格式：${outputFormat || 'CSV'}，共${filteredAssets.length}条）`,
      result: 'SUCCESS',
      url: '/api/report/asset-list/export',
      method: 'GET'
    });

    // 添加部门和责任人信息
    const reportData = filteredAssets.map(asset => {
      const department = mockDepartments.find(d => d.departmentId === asset.departmentId);
      const owner = mockOwners.find(o => o.ownerId === asset.ownerId);
      const classification = mockClassifications.find(c => c.classificationId === asset.classificationId);
      const grading = mockGradings.find(g => g.gradingId === asset.gradingId);

      return {
        assetName: asset.assetName,
        assetCode: asset.assetCode,
        assetType: asset.assetType,
        systemName: asset.systemName,
        departmentName: department ? department.departmentName : '',
        ownerName: owner ? owner.name : '',
        classificationName: classification ? classification.classificationName : '',
        gradingName: grading ? grading.gradingName : '',
        status: asset.status,
        createTime: asset.createTime || new Date().toISOString().split('T')[0]
      };
    });

    // 根据输出格式生成文件
    if (outputFormat === 'pdf') {
      // PDF格式（文本格式模拟）
      const pdfContent = `
数据资产清单报告
=====================

生成时间: ${new Date().toLocaleString()}
报告类型: 数据资产清单

一、总体概况
-----------
总资产数: ${reportData.length}

二、资产明细
-----------
${reportData.map((item, index) => `
${index + 1}. ${item.assetName}
   资产编码: ${item.assetCode}
   资产类型: ${item.assetType}
   所属系统: ${item.systemName}
   所属部门: ${item.departmentName}
   责任人: ${item.ownerName}
   数据分类: ${item.classificationName}
   数据分级: ${item.gradingName}
   状态: ${item.status}
   创建时间: ${item.createTime}
`).join('\n')}

=====================
报告结束
      `;

      const timestamp = new Date().getTime();
      const filename = `asset_list_report_${timestamp}.txt`;
      const encodedFilename = encodeURIComponent(filename);

      res.setHeader('Content-Type', 'text/plain; charset=utf-8');
      res.setHeader('Content-Disposition', `attachment; filename="${encodedFilename}"`);
      res.send(pdfContent);

    } else {
      // Excel格式（CSV格式）
      const csvHeader = '资产名称,资产编码,资产类型,所属系统,所属部门,责任人,数据分类,数据分级,状态,创建时间\n';
      const csvRows = reportData.map(item =>
        `${item.assetName},${item.assetCode},${item.assetType},${item.systemName},${item.departmentName},${item.ownerName},${item.classificationName},${item.gradingName},${item.status},${item.createTime}`
      ).join('\n');

      const csvContent = '\uFEFF' + csvHeader + csvRows; // 添加BOM解决中文乱码

      const timestamp = new Date().getTime();
      const filename = `asset_list_report_${timestamp}.csv`;
      const encodedFilename = encodeURIComponent(filename);

      res.setHeader('Content-Type', 'text/csv; charset=utf-8');
      res.setHeader('Content-Disposition', `attachment; filename="${encodedFilename}"`);
      res.send(csvContent);
    }
  } catch (error) {
    console.error('导出资产清单报告失败:', error);
    res.status(500).json({
      code: 500,
      message: '导出失败：' + error.message
    });
  }
});

// 生成分类分级统计报告
app.get('/api/report/classification-stats/generate', (req, res) => {
  const { dimension } = req.query;

  // 记录审计日志
  addAuditLog({
    operationType: 'CREATE',
    module: '报告管理',
    objectType: '分类统计报告',
    description: `生成分类统计报告（维度：${dimension || '全部'}）`,
    result: 'SUCCESS',
    url: '/api/report/classification-stats/generate',
    method: 'GET'
  });

  // 统计数据
  const totalAssets = mockAssets.length;
  const classifiedAssets = mockAssets.filter(a => a.classificationId).length;
  const gradedAssets = mockAssets.filter(a => a.gradingId).length;
  const highLevelAssets = mockAssets.filter(a => {
    const grading = mockGradings.find(g => g.gradingId === a.gradingId);
    return grading && (grading.gradingName === '绝密' || grading.gradingName === '机密');
  }).length;

  // 分类分布
  const classificationDistribution = [
    { value: 35, name: '个人信息' },
    { value: 25, name: '业务数据' },
    { value: 20, name: '财务数据' },
    { value: 15, name: '技术数据' },
    { value: 5, name: '其他数据' }
  ];

  // 分级分布
  const gradingDistribution = [
    { name: '绝密', value: 8 },
    { name: '机密', value: 15 },
    { name: '秘密', value: 27 },
    { name: '内部', value: 35 },
    { name: '公开', value: 15 }
  ];

  // 部门统计
  const departmentStats = {
    departments: ['技术部', '业务部', '财务部'],
    categories: ['个人信息', '业务数据', '财务数据'],
    series: [
      {
        name: '个人信息',
        type: 'bar',
        stack: 'total',
        data: [15, 12, 8]
      },
      {
        name: '业务数据',
        type: 'bar',
        stack: 'total',
        data: [10, 18, 5]
      },
      {
        name: '财务数据',
        type: 'bar',
        stack: 'total',
        data: [5, 2, 13]
      }
    ]
  };

  // 详细数据
  const detailData = [
    { category: '个人信息', count: 35, percentage: 35, trend: 5.2, description: '包含客户姓名、电话、地址等敏感信息' },
    { category: '业务数据', count: 25, percentage: 25, trend: 3.1, description: '包含订单、产品、交易等业务信息' },
    { category: '财务数据', count: 20, percentage: 20, trend: -2.3, description: '包含财务报表、预算、成本等信息' },
    { category: '技术数据', count: 15, percentage: 15, trend: 1.8, description: '包含系统配置、日志、监控等信息' },
    { category: '其他数据', count: 5, percentage: 5, trend: 0.5, description: '其他类型的数据资产' }
  ];

  res.json({
    code: 0,
    data: {
      totalAssets,
      classificationCoverage: ((classifiedAssets / totalAssets) * 100).toFixed(1),
      gradingCoverage: ((gradedAssets / totalAssets) * 100).toFixed(1),
      highLevelAssets,
      classificationDistribution,
      gradingDistribution,
      departmentStats,
      detailData
    }
  });
});

// 导出分类分级统计报告
app.get('/api/report/classification-stats/export', (req, res) => {
  try {
    const { dimension, outputFormat } = req.query;

    // 记录审计日志
    addAuditLog({
      operationType: 'EXPORT',
      module: '报告管理',
      objectType: '分类统计报告',
      description: `导出分类统计报告（维度：${dimension || '全部'}，格式：${outputFormat || 'Excel'}）`,
      result: 'SUCCESS',
      url: '/api/report/classification-stats/export',
      method: 'GET'
    });

    // 获取统计数据（重新生成以确保数据一致性）
    const totalAssets = mockAssets.length;
    const classifiedAssets = mockAssets.filter(a => a.classificationId).length;
    const gradedAssets = mockAssets.filter(a => a.gradingId).length;
    const highLevelAssets = mockAssets.filter(a => {
      const grading = mockGradings.find(g => g.gradingId === a.gradingId);
      return grading && (grading.gradingName === '绝密' || grading.gradingName === '机密');
    }).length;

    // 分类分布
    const classificationDistribution = [
      { value: 35, name: '个人信息' },
      { value: 25, name: '业务数据' },
      { value: 20, name: '财务数据' },
      { value: 15, name: '技术数据' },
      { value: 5, name: '其他数据' }
    ];

    // 分级分布
    const gradingDistribution = [
      { name: '绝密', value: 8 },
      { name: '机密', value: 15 },
      { name: '秘密', value: 27 },
      { name: '内部', value: 35 },
      { name: '公开', value: 15 }
    ];

    // 部门统计
    const departmentStats = {
      departments: ['技术部', '业务部', '财务部'],
      categories: ['个人信息', '业务数据', '财务数据'],
      series: [
        {
          name: '个人信息',
          type: 'bar',
          stack: 'total',
          data: [15, 12, 8]
        },
        {
          name: '业务数据',
          type: 'bar',
          stack: 'total',
          data: [10, 18, 5]
        },
        {
          name: '财务数据',
          type: 'bar',
          stack: 'total',
          data: [5, 2, 13]
        }
      ]
    };

    // 详细数据
    const detailData = [
      { category: '个人信息', count: 35, percentage: 35, trend: 5.2, description: '包含客户姓名、电话、地址等敏感信息' },
      { category: '业务数据', count: 25, percentage: 25, trend: 3.1, description: '包含订单、产品、交易等业务信息' },
      { category: '财务数据', count: 20, percentage: 20, trend: -2.3, description: '包含财务报表、预算、成本等信息' },
      { category: '技术数据', count: 15, percentage: 15, trend: 1.8, description: '包含系统配置、日志、监控等信息' },
      { category: '其他数据', count: 5, percentage: 5, trend: 0.5, description: '其他类型的数据资产' }
    ];

    // 根据输出格式生成文件
    if (outputFormat === 'html') {
      // HTML格式
      const htmlContent = `
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>数据分类分级统计报告</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
        .header { text-align: center; margin-bottom: 30px; }
        .section { margin-bottom: 30px; }
        .metric { display: inline-block; margin: 10px 20px; padding: 15px; background: #f5f5f5; border-radius: 5px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
        tr:nth-child(even) { background-color: #f9f9f9; }
    </style>
</head>
<body>
    <div class="header">
        <h1>数据分类分级统计报告</h1>
        <p>生成时间: ${new Date().toLocaleString()}</p>
    </div>

    <div class="section">
        <h2>一、总体概况</h2>
        <div class="metric">总资产数: <strong>${totalAssets}</strong></div>
        <div class="metric">分类覆盖率: <strong>${((classifiedAssets / totalAssets) * 100).toFixed(1)}%</strong></div>
        <div class="metric">分级覆盖率: <strong>${((gradedAssets / totalAssets) * 100).toFixed(1)}%</strong></div>
        <div class="metric">高级别资产: <strong>${highLevelAssets}</strong></div>
    </div>

    <div class="section">
        <h2>二、分类分布</h2>
        <table>
            <tr><th>类别</th><th>数量</th><th>占比</th><th>趋势</th><th>说明</th></tr>
            ${detailData.map(item => `
                <tr>
                    <td>${item.category}</td>
                    <td>${item.count}</td>
                    <td>${item.percentage}%</td>
                    <td>${item.trend >= 0 ? '+' : ''}${item.trend}%</td>
                    <td>${item.description}</td>
                </tr>
            `).join('')}
        </table>
    </div>

    <div class="section">
        <h2>三、分级分布</h2>
        <table>
            <tr><th>级别</th><th>数量</th><th>占比</th></tr>
            ${gradingDistribution.map(item => `
                <tr>
                    <td>${item.name}</td>
                    <td>${item.value}</td>
                    <td>${item.value}%</td>
                </tr>
            `).join('')}
        </table>
    </div>

    <div class="section">
        <h2>四、部门统计</h2>
        <table>
            <tr><th>部门</th><th>个人信息</th><th>业务数据</th><th>财务数据</th><th>总计</th></tr>
            ${departmentStats.departments.map((dept, index) => `
                <tr>
                    <td>${dept}</td>
                    <td>${departmentStats.series[0].data[index]}</td>
                    <td>${departmentStats.series[1].data[index]}</td>
                    <td>${departmentStats.series[2].data[index]}</td>
                    <td>${departmentStats.series[0].data[index] + departmentStats.series[1].data[index] + departmentStats.series[2].data[index]}</td>
                </tr>
            `).join('')}
        </table>
    </div>

    <div class="section">
        <h2>五、详细数据</h2>
        <table>
            <tr><th>类别</th><th>数量</th><th>占比</th><th>趋势</th><th>说明</th></tr>
            ${detailData.map(item => `
                <tr>
                    <td>${item.category}</td>
                    <td>${item.count}</td>
                    <td>${item.percentage}%</td>
                    <td>${item.trend >= 0 ? '+' : ''}${item.trend}%</td>
                    <td>${item.description}</td>
                </tr>
            `).join('')}
        </table>
    </div>
</body>
</html>
      `;

      const timestamp = new Date().getTime();
      const filename = `classification_stats_report_${timestamp}.html`;
      const encodedFilename = encodeURIComponent(filename);

      res.setHeader('Content-Type', 'text/html; charset=utf-8');
      res.setHeader('Content-Disposition', `attachment; filename="${encodedFilename}"`);
      res.send(htmlContent);

    } else if (outputFormat === 'pdf') {
      // PDF格式（文本格式模拟）
      const pdfContent = `
数据分类分级统计报告
=====================

生成时间: ${new Date().toLocaleString()}
统计维度: ${dimension || '综合统计'}

一、总体概况
-----------
总资产数: ${totalAssets}
分类覆盖率: ${((classifiedAssets / totalAssets) * 100).toFixed(1)}%
分级覆盖率: ${((gradedAssets / totalAssets) * 100).toFixed(1)}%
高级别资产: ${highLevelAssets}

二、分类分布
-----------
${classificationDistribution.map(item => `${item.name}: ${item.value} (${item.value}%)`).join('\n')}

三、分级分布
-----------
${gradingDistribution.map(item => `${item.name}: ${item.value} (${item.value}%)`).join('\n')}

四、部门统计
-----------
${departmentStats.departments.map((dept, index) => 
  `${dept}: ${departmentStats.series[0].data[index]} (个人信息) + ${departmentStats.series[1].data[index]} (业务数据) + ${departmentStats.series[2].data[index]} (财务数据) = ${departmentStats.series[0].data[index] + departmentStats.series[1].data[index] + departmentStats.series[2].data[index]} (总计)`
).join('\n')}

五、详细数据
-----------
${detailData.map(item => 
  `${item.category}: ${item.count} (${item.percentage}%), 趋势: ${item.trend >= 0 ? '+' : ''}${item.trend}%, ${item.description}`
).join('\n')}

=====================
报告结束
      `;

      const timestamp = new Date().getTime();
      const filename = `classification_stats_report_${timestamp}.txt`;
      const encodedFilename = encodeURIComponent(filename);

      res.setHeader('Content-Type', 'text/plain; charset=utf-8');
      res.setHeader('Content-Disposition', `attachment; filename="${encodedFilename}"`);
      res.send(pdfContent);

    } else {
      // Excel格式（CSV格式）
      const csvHeader = '类别,数量,占比,趋势,说明\n';
      const csvRows = detailData.map(item =>
        `${item.category},${item.count},${item.percentage},${item.trend},"${item.description}"`
      ).join('\n');

      const csvContent = '\uFEFF' + csvHeader + csvRows; // 添加BOM解决中文乱码

      const timestamp = new Date().getTime();
      const filename = `classification_stats_report_${timestamp}.csv`;
      const encodedFilename = encodeURIComponent(filename);

      res.setHeader('Content-Type', 'text/csv; charset=utf-8');
      res.setHeader('Content-Disposition', `attachment; filename="${encodedFilename}"`);
      res.send(csvContent);
    }
  } catch (error) {
    console.error('导出分类分级统计报告失败:', error);
    res.status(500).json({
      code: 500,
      message: '导出失败：' + error.message
    });
  }
});

// 获取报告生成历史
app.get('/api/report/history', (req, res) => {
  const { reportType } = req.query;
  let mockHistory = [
    {
      id: 1,
      reportName: '数据资产清单报告_20250418',
      reportType: '数据资产清单',
      outputFormat: 'excel',
      generatedBy: 'admin',
      generationTime: '2025-04-18 10:30:00',
      downloadCount: 3
    },
    {
      id: 2,
      reportName: '数据分类分级统计报告_20250418',
      reportType: '分类分级统计',
      outputFormat: 'pdf',
      generatedBy: 'admin',
      generationTime: '2025-04-18 11:15:00',
      downloadCount: 5
    }
  ];

  if (reportType) {
    mockHistory = mockHistory.filter(item => item.reportType === reportType);
  }

  res.json({ code: 0, data: mockHistory });
});

// 删除报告
app.delete('/api/report/:reportId', (req, res) => {
  const { reportId } = req.params;
  // 模拟删除操作
  res.json({ code: 0, message: '报告删除成功' });
});

// 下载报告
app.get('/api/report/download/:reportId', (req, res) => {
  const { reportId } = req.params;
  
  // 记录审计日志
  addAuditLog({
    operationType: 'EXPORT',
    module: '报告管理',
    objectType: '报告',
    objectId: parseInt(reportId),
    description: `下载报告（ID：${reportId}）`,
    result: 'SUCCESS',
    url: '/api/report/download/' + reportId,
    method: 'GET'
  });
  
  // 返回报告文件信息
  res.json({
    code: 0,
    data: {
      fileName: '报告_' + reportId + '.xlsx',
      filePath: '/reports/report_' + reportId + '.xlsx'
    }
  });
});

// ==================== 审计日志相关API ====================

// 模拟审计日志数据
const mockAuditLogs = [
  {
    logId: 1,
    operationType: 'LOGIN',
    module: '认证模块',
    objectType: '用户',
    objectId: 1,
    objectName: 'admin',
    operationDescription: '用户登录',
    operationResult: 'SUCCESS',
    operatorId: 1,
    operatorName: '系统管理员',
    operatorUsername: 'admin',
    operationTime: new Date().toISOString(),
    operationIp: '127.0.0.1',
    operationLocation: '本地',
    userAgent: 'Chrome/120.0',
    requestUrl: '/api/auth/login',
    requestMethod: 'POST',
    executionTime: 150
  },
  {
    logId: 2,
    operationType: 'CREATE',
    module: '数据资产管理',
    objectType: '数据资产',
    objectId: 1,
    objectName: '客户信息表',
    operationDescription: '创建数据资产',
    operationResult: 'SUCCESS',
    operatorId: 1,
    operatorName: '系统管理员',
    operatorUsername: 'admin',
    operationTime: new Date(Date.now() - 3600000).toISOString(),
    operationIp: '127.0.0.1',
    operationLocation: '本地',
    userAgent: 'Chrome/120.0',
    requestUrl: '/api/asset/create',
    requestMethod: 'POST',
    executionTime: 200
  }
];

// 分页查询审计日志
app.post('/api/audit-logs/query', (req, res) => {
  const { pageNum = 1, pageSize = 10, operationType, moduleName, operatorUsername, operationResult, startTime, endTime, keyword } = req.body;

  let filteredLogs = [...mockAuditLogs];

  // 筛选条件
  if (operationType) {
    filteredLogs = filteredLogs.filter(log => log.operationType === operationType);
  }
  if (moduleName) {
    filteredLogs = filteredLogs.filter(log => log.module.includes(moduleName));
  }
  if (operatorUsername) {
    filteredLogs = filteredLogs.filter(log => log.operatorUsername.includes(operatorUsername));
  }
  if (operationResult) {
    filteredLogs = filteredLogs.filter(log => log.operationResult === operationResult);
  }
  if (keyword) {
    filteredLogs = filteredLogs.filter(log =>
      log.operationDescription?.includes(keyword) ||
      log.objectName?.includes(keyword)
    );
  }

  const start = (pageNum - 1) * pageSize;
  const end = start + pageSize;
  const records = filteredLogs.slice(start, end);

  res.json({
    code: 0,
    data: {
      records,
      total: filteredLogs.length,
      size: pageSize,
      current: pageNum,
      pages: Math.ceil(filteredLogs.length / pageSize)
    }
  });
});

// 统计审计日志
app.post('/api/audit-logs/statistics', (req, res) => {
  res.json({
    code: 0,
    data: {
      totalOperations: mockAuditLogs.length,
      successCount: mockAuditLogs.filter(l => l.operationResult === 'SUCCESS').length,
      failureCount: mockAuditLogs.filter(l => l.operationResult === 'FAILURE').length,
      successRate: 100,
      operationTypeStats: {
        'LOGIN': 1,
        'CREATE': 1,
        'UPDATE': 0,
        'DELETE': 0
      },
      moduleStats: {
        '认证模块': 1,
        '数据资产管理': 1
      },
      userStats: {
        'admin': 2
      },
      dateStats: {}
    }
  });
});

// 导出审计日志
app.post('/api/audit-logs/export', (req, res) => {
  const { format } = req.body;
  
  // 记录审计日志
  addAuditLog({
    operationType: 'EXPORT',
    module: '审计日志',
    objectType: '审计日志',
    description: `导出审计日志（格式：${format || 'CSV'}）`,
    result: 'SUCCESS',
    url: '/api/audit-logs/export',
    method: 'POST'
  });
  
  res.json({
    code: 0,
    data: {
      filePath: '/tmp/audit-logs-export.csv'
    }
  });
});

// 归档审计日志
app.post('/api/audit-logs/archive', (req, res) => {
  // 记录审计日志
  addAuditLog({
    operationType: 'UPDATE',
    module: '审计日志',
    objectType: '审计日志',
    description: '归档审计日志',
    result: 'SUCCESS',
    url: '/api/audit-logs/archive',
    method: 'POST'
  });
  
  res.json({
    code: 0,
    data: {
      count: 0
    }
  });
});

// 清理已归档的审计日志
app.post('/api/audit-logs/clean', (req, res) => {
  // 记录审计日志
  addAuditLog({
    operationType: 'DELETE',
    module: '审计日志',
    objectType: '审计日志',
    description: '清理已归档的审计日志',
    result: 'SUCCESS',
    url: '/api/audit-logs/clean',
    method: 'POST'
  });
  
  res.json({
    code: 0,
    data: {
      count: 0
    }
  });
});

// ==================== Session相关API ====================

// 模拟会话数据
const mockSessions = [
  {
    sessionId: 1,
    userId: 1,
    username: 'admin',
    loginTime: new Date(Date.now() - 3600000).toISOString(),
    lastAccessTime: new Date().toISOString(),
    expireTime: new Date(Date.now() + 82800000).toISOString(),
    loginIp: '127.0.0.1',
    deviceInfo: 'MacBook Pro',
    browserInfo: 'Chrome 120.0',
    status: 'ACTIVE'
  }
];

// 获取当前会话信息
app.get('/api/session/current', (req, res) => {
  res.json({
    code: 0,
    data: mockSessions[0]
  });
});

// 获取用户活跃会话列表
app.get('/api/session/user/:userId', (req, res) => {
  const { userId } = req.params;
  const sessions = mockSessions.filter(s => s.userId === parseInt(userId));
  res.json({
    code: 0,
    data: sessions
  });
});

// 获取当前用户的活跃会话列表
app.get('/api/session/my-sessions', (req, res) => {
  res.json({
    code: 0,
    data: mockSessions
  });
});

// 强制下线指定会话
app.post('/api/session/:sessionId/force-logout', (req, res) => {
  const { sessionId } = req.params;
  const index = mockSessions.findIndex(s => s.sessionId === parseInt(sessionId));
  if (index !== -1) {
    const session = mockSessions[index];
    mockSessions.splice(index, 1);
    
    // 记录审计日志
    addAuditLog({
      operationType: 'DELETE',
      module: '会话管理',
      objectType: '会话',
      objectId: parseInt(sessionId),
      objectName: session.username,
      description: `强制下线用户会话：${session.username}`,
      result: 'SUCCESS',
      url: '/api/session/' + sessionId + '/force-logout',
      method: 'POST'
    });
    
    res.json({ code: 0, message: '会话已强制下线' });
  } else {
    res.status(404).json({ code: 404, message: '会话不存在' });
  }
});

// 强制下线当前用户的所有会话
app.post('/api/session/force-logout-all', (req, res) => {
  mockSessions.length = 0;
  res.json({ code: 0, message: '所有会话已强制下线' });
});

// 清理过期会话
app.post('/api/session/clean-expired', (req, res) => {
  const now = new Date();
  const expiredCount = mockSessions.filter(s => new Date(s.expireTime) < now).length;
  // 清理过期会话
  for (let i = mockSessions.length - 1; i >= 0; i--) {
    if (new Date(mockSessions[i].expireTime) < now) {
      mockSessions.splice(i, 1);
    }
  }
  res.json({ code: 0, message: `已清理${expiredCount}个过期会话` });
});

// ==================== MFA相关API ====================

// MFA状态数据
let mfaEnabled = false;
let mfaConfig = null;

// 检查MFA状态
app.get('/api/mfa/status', (req, res) => {
  res.json({
    code: 0,
    data: {
      enabled: mfaEnabled
    }
  });
});

// 生成MFA设置信息
app.get('/api/mfa/setup', async (req, res) => {
  // 生成模拟的MFA设置信息
  const secret = 'JBSWY3DPEHPK3PXP'; // 模拟密钥
  const qrCodeUrl = `otpauth://totp/DataAssetSecurity:admin?secret=${secret}&issuer=DataAssetSecurity`;

  try {
    // 生成真实的二维码图片（Base64格式）
    const qrCodeImage = await QRCode.toDataURL(qrCodeUrl, {
      width: 300,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF'
      }
    });

    res.json({
      code: 0,
      data: {
        userId: 1,
        username: 'admin',
        mfaType: 'TOTP',
        secret: secret,
        qrCodeUrl: qrCodeUrl,
        qrCodeImage: qrCodeImage, // 真实的二维码图片
        enabled: false
      }
    });
  } catch (err) {
    console.error('生成二维码失败:', err);
    res.json({
      code: 500,
      message: '生成二维码失败'
    });
  }
});

// 启用MFA
app.post('/api/mfa/enable', (req, res) => {
  const { mfaType, secret, verificationCode } = req.body;

  // 简单验证
  if (!mfaType || !secret || !verificationCode) {
    return res.json({ code: 400, message: '参数不完整' });
  }

  // 模拟验证码验证（实际应该验证TOTP码）
  if (verificationCode.length !== 6) {
    return res.json({ code: 400, message: '验证码格式不正确' });
  }

  mfaEnabled = true;
  mfaConfig = {
    userId: 1,
    username: 'admin',
    mfaType: mfaType,
    secret: secret,
    enabled: true,
    enabledTime: new Date().toISOString(),
    lastVerifiedTime: new Date().toISOString()
  };

  res.json({ code: 0, message: 'MFA启用成功' });
});

// 验证MFA验证码
app.post('/api/mfa/verify', (req, res) => {
  const { userId, code, useBackupCode } = req.body;

  if (!code) {
    return res.json({ code: 400, message: '验证码不能为空' });
  }

  // 模拟验证（实际应该验证TOTP码或备用码）
  const isValid = code.length === 6 && /^\d+$/.test(code);

  res.json({
    code: 0,
    data: {
      valid: isValid
    }
  });
});

// 禁用MFA
app.post('/api/mfa/disable', (req, res) => {
  const { password } = req.query;

  if (!password) {
    return res.json({ code: 400, message: '密码不能为空' });
  }

  // 简单验证密码（实际应该验证用户密码）
  if (password !== 'admin123') {
    return res.json({ code: 400, message: '密码不正确' });
  }

  mfaEnabled = false;
  mfaConfig = null;

  res.json({ code: 0, message: 'MFA禁用成功' });
});

// 获取MFA配置
app.get('/api/mfa/config', (req, res) => {
  if (!mfaEnabled || !mfaConfig) {
    return res.json({ code: 404, message: 'MFA未启用' });
  }

  res.json({
    code: 0,
    data: mfaConfig
  });
});

// 生成备用码
app.post('/api/mfa/backup-codes', (req, res) => {
  // 生成8个随机备用码
  const backupCodes = [];
  for (let i = 0; i < 8; i++) {
    const code = Math.random().toString(36).substring(2, 8).toUpperCase() +
                 Math.random().toString(36).substring(2, 8).toUpperCase();
    backupCodes.push(code);
  }

  res.json({
    code: 0,
    data: backupCodes
  });
});

// 健康检查
app.get('/actuator/health', (req, res) => {
  res.json({ status: 'UP' });
});

// 辅助函数：构建树形结构
function buildTree(items, parentId = null) {
  return items
    .filter(item => item.parentId === parentId)
    .map(item => ({
      ...item,
      children: buildTree(items, item.classificationId),
    }));
}

// ============================================================
// Spring Boot兼容接口：POST /xxx/page 分页查询
// 前端统一使用POST分页接口，Mock需要兼容
// ============================================================

// 通用分页兼容函数：将GET /list的数据转换为POST /page的格式
function createPageHandler(listPath, dataGetter) {
  return (req, res) => {
    // 获取原始列表数据
    const originalRes = {
      jsonData: null,
      json(data) { this.jsonData = data; }
    };
    dataGetter(req, originalRes);
    const originalData = originalRes.jsonData;
    if (!originalData || originalData.code !== 0) {
      return res.json(originalData || { code: 500, message: '内部错误' });
    }
    const list = originalData.data?.list || originalData.data || [];
    const total = originalData.data?.total || list.length;
    const { page = 1, size = 10 } = req.body || {};
    const start = (page - 1) * size;
    const records = Array.isArray(list) ? list.slice(start, start + size) : list;
    res.json({
      code: 0,
      message: '操作成功',
      data: {
        records,
        total,
        size,
        current: page,
        pages: Math.ceil(total / size)
      }
    });
  };
}

// 部门分页
app.post('/api/department/page', (req, res) => {
  const list = mockDepartments.map(d => ({ ...d }));
  const { page = 1, size = 10 } = req.body || {};
  const start = (page - 1) * size;
  res.json({
    code: 0, message: '操作成功',
    data: { records: list.slice(start, start + size), total: list.length, size, current: page, pages: Math.ceil(list.length / size) }
  });
});

// 责任人分页
app.post('/api/owner/page', (req, res) => {
  const list = mockOwners.map(o => ({ ...o }));
  const { page = 1, size = 10 } = req.body || {};
  const start = (page - 1) * size;
  res.json({
    code: 0, message: '操作成功',
    data: { records: list.slice(start, start + size), total: list.length, size, current: page, pages: Math.ceil(list.length / size) }
  });
});

// 资产分页
app.post('/api/asset/page', (req, res) => {
  let list = mockAssets.map(a => ({ ...a }));
  const { page = 1, size = 10, keyword, assetType, status, departmentId, classificationId, gradingId, databaseType, databaseHost, databaseName, assetName, assetCode } = req.body || {};
  if (keyword) list = list.filter(a => a.assetName.includes(keyword) || a.assetCode.includes(keyword));
  if (assetName) list = list.filter(a => a.assetName && a.assetName.includes(assetName));
  if (assetCode) list = list.filter(a => a.assetCode && a.assetCode.includes(assetCode));
  if (assetType) list = list.filter(a => a.assetType === assetType);
  if (status) list = list.filter(a => a.status === status);
  if (departmentId) list = list.filter(a => a.departmentId === departmentId);
  if (classificationId) list = list.filter(a => a.classificationId === classificationId);
  if (gradingId) list = list.filter(a => a.gradingId === gradingId);
  if (databaseType) list = list.filter(a => a.databaseType === databaseType);
  if (databaseHost) list = list.filter(a => a.databaseHost && a.databaseHost.includes(databaseHost));
  if (databaseName) list = list.filter(a => a.databaseName && a.databaseName.includes(databaseName));
  const start = (page - 1) * size;
  res.json({
    code: 0, message: '操作成功',
    data: { records: list.slice(start, start + size), total: list.length, size, current: page, pages: Math.ceil(list.length / size) }
  });
});

// 资产数据条数刷新
app.post('/api/asset/refresh-row-count/:assetId', (req, res) => {
  const assetId = parseInt(req.params.assetId);
  const asset = mockAssets.find(a => a.assetId === assetId);
  if (!asset) {
    return res.json({ code: 404, message: '资产不存在' });
  }
  if (asset.assetType !== 'DATABASE' || !asset.databaseHost || !asset.databaseName || !asset.tableName) {
    return res.json({ code: 400, message: '资产缺少数据库连接信息，无法查询数据条数' });
  }
  // 模拟查询：生成随机rowCount
  const rowCount = Math.floor(Math.random() * 99000) + 1000;
  asset.rowCount = rowCount;
  res.json({
    code: 0, message: '数据条数刷新成功',
    data: { assetId, rowCount }
  });
});

// 字段数据条数刷新
app.post('/api/asset/field/refresh-row-count/:fieldId', (req, res) => {
  const fieldId = parseInt(req.params.fieldId);
  const field = mockFields.find(f => f.fieldId === fieldId);
  if (!field) {
    return res.json({ code: 404, message: '字段不存在' });
  }
  const asset = mockAssets.find(a => a.assetId === field.assetId);
  if (!asset) {
    return res.json({ code: 404, message: '字段所属资产不存在' });
  }
  if (asset.assetType !== 'DATABASE' || !asset.databaseHost || !asset.databaseName || !asset.tableName) {
    return res.json({ code: 400, message: '所属资产缺少数据库连接信息，无法查询字段数据条数' });
  }
  // 模拟查询：字段rowCount <= 资产rowCount
  const maxCount = asset.rowCount || 80000;
  const rowCount = Math.floor(Math.random() * Math.min(maxCount, 80000)) + 500;
  field.rowCount = rowCount;
  res.json({
    code: 0, message: '字段数据条数刷新成功',
    data: { fieldId, rowCount }
  });
});

// 批量刷新任务存储
const batchRefreshTasks = {};

// 批量刷新数据条数
app.post('/api/asset/batch-refresh-row-count', (req, res) => {
  const { assetIds = [], refreshScope = 'ASSET_AND_FIELD' } = req.body || {};
  if (!assetIds.length) {
    return res.json({ code: 400, message: '请选择至少一条资产' });
  }
  const taskId = 'task_' + Date.now();
  const task = {
    taskId,
    status: 'RUNNING',
    totalCount: assetIds.length,
    completedCount: 0,
    currentAssetName: '',
    results: []
  };
  batchRefreshTasks[taskId] = task;

  // 模拟异步逐条处理
  let index = 0;
  const processNext = () => {
    if (index >= assetIds.length) {
      task.status = 'COMPLETED';
      return;
    }
    const assetId = assetIds[index];
    const asset = mockAssets.find(a => a.assetId === assetId);
    if (asset) {
      task.currentAssetName = asset.assetName;
      const rowCount = Math.floor(Math.random() * 99000) + 1000;
      asset.rowCount = rowCount;
      task.results.push({ assetId, assetName: asset.assetName, status: 'SUCCESS', rowCount, errorMessage: null });

      // 如果需要同时刷新字段
      if (refreshScope === 'ASSET_AND_FIELD') {
        const fields = mockFields.filter(f => f.assetId === assetId);
        fields.forEach(f => {
          f.rowCount = Math.floor(Math.random() * Math.min(rowCount, 80000)) + 500;
        });
      }
    } else {
      task.results.push({ assetId, assetName: '', status: 'FAILED', rowCount: null, errorMessage: '资产不存在' });
    }
    index++;
    task.completedCount = index;
    setTimeout(processNext, 200);
  };
  setTimeout(processNext, 100);

  res.json({ code: 0, message: '批量刷新任务已提交', data: { taskId } });
});

// 批量刷新进度查询
app.get('/api/asset/batch-refresh-progress/:taskId', (req, res) => {
  const task = batchRefreshTasks[req.params.taskId];
  if (!task) {
    return res.json({ code: 404, message: '任务不存在' });
  }
  res.json({ code: 0, message: '操作成功', data: task });
});

// 分类分页
app.post('/api/classification/page', (req, res) => {
  const list = mockClassifications.map(c => ({ ...c }));
  const { page = 1, size = 10 } = req.body || {};
  const start = (page - 1) * size;
  res.json({
    code: 0, message: '操作成功',
    data: { records: list.slice(start, start + size), total: list.length, size, current: page, pages: Math.ceil(list.length / size) }
  });
});

// 分级分页
app.post('/api/grading/page', (req, res) => {
  const list = mockGradings.map(g => ({ ...g }));
  const { page = 1, size = 10 } = req.body || {};
  const start = (page - 1) * size;
  res.json({
    code: 0, message: '操作成功',
    data: { records: list.slice(start, start + size), total: list.length, size, current: page, pages: Math.ceil(list.length / size) }
  });
});

// 角色分页
app.post('/api/role/page', (req, res) => {
  const list = mockRoles.map(r => ({ ...r }));
  const { page = 1, size = 10 } = req.body || {};
  const start = (page - 1) * size;
  res.json({
    code: 0, message: '操作成功',
    data: { records: list.slice(start, start + size), total: list.length, size, current: page, pages: Math.ceil(list.length / size) }
  });
});

// 用户分页
app.post('/api/user/page', (req, res) => {
  const list = mockUsers.map(u => ({ ...u }));
  const { page = 1, size = 10 } = req.body || {};
  const start = (page - 1) * size;
  res.json({
    code: 0, message: '操作成功',
    data: { records: list.slice(start, start + size), total: list.length, size, current: page, pages: Math.ceil(list.length / size) }
  });
});

// 权限全量接口（兼容Spring Boot的 /permission/all）
app.get('/api/permission/all', (req, res) => {
  res.json({ code: 0, message: '操作成功', data: mockPermissions.map(p => ({ ...p })) });
});

// 分类标准分页
app.post('/api/classificationStandard/page', (req, res) => {
  const list = mockClassificationStandards.map(s => ({ ...s }));
  const { page = 1, size = 10 } = req.body || {};
  const start = (page - 1) * size;
  res.json({
    code: 0, message: '操作成功',
    data: { records: list.slice(start, start + size), total: list.length, size, current: page, pages: Math.ceil(list.length / size) }
  });
});

// 分级标准分页
app.post('/api/gradingStandard/page', (req, res) => {
  const list = mockGradingStandards.map(s => ({ ...s }));
  const { page = 1, size = 10 } = req.body || {};
  const start = (page - 1) * size;
  res.json({
    code: 0, message: '操作成功',
    data: { records: list.slice(start, start + size), total: list.length, size, current: page, pages: Math.ceil(list.length / size) }
  });
});

// 审计日志分页兼容
app.post('/api/audit-logs/query', (req, res) => {
  const list = mockAuditLogs.map(l => ({ ...l }));
  const { page = 1, size = 10 } = req.body || {};
  const start = (page - 1) * size;
  res.json({
    code: 0, message: '操作成功',
    data: { records: list.slice(start, start + size), total: list.length, size, current: page, pages: Math.ceil(list.length / size) }
  });
});

app.listen(PORT, () => {
  console.log(`Mock后端服务运行在 http://localhost:${PORT}`);
  console.log(`API文档: http://localhost:${PORT}/doc.html`);
});
