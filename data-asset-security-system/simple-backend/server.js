const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 8080;

app.use(cors());
app.use(express.json());

// ==================== 模拟数据 ====================

// 用户数据
const mockUsers = [
  { userId: 1, username: 'admin', realName: '系统管理员', email: 'admin@example.com', phone: '13800138000', status: 'ACTIVE' },
  { userId: 2, username: 'user1', realName: '张三', email: 'zhangsan@example.com', phone: '13800138001', status: 'ACTIVE' },
  { userId: 3, username: 'user2', realName: '李四', email: 'lisi@example.com', phone: '13800138002', status: 'ACTIVE' },
  { userId: 4, username: 'user3', realName: '王五', email: 'wangwu@example.com', phone: '13800138003', status: 'INACTIVE' },
];

// 角色数据
const mockRoles = [
  { roleId: 1, roleCode: 'SYSTEM_ADMIN', roleName: '系统管理员', roleType: 'SYSTEM_ADMIN', status: 'ACTIVE', roleDescription: '负责系统配置和用户管理' },
  { roleId: 2, roleCode: 'DATA_ADMIN', roleName: '数据管理员', roleType: 'DATA_ADMIN', status: 'ACTIVE', roleDescription: '负责分类分级标准制定和维护' },
  { roleId: 3, roleCode: 'APPROVER', roleName: '数据审批人', roleType: 'APPROVER', status: 'ACTIVE', roleDescription: '负责分类分级申请审批' },
  { roleId: 4, roleCode: 'OWNER', roleName: '数据责任人', roleType: 'OWNER', status: 'ACTIVE', roleDescription: '负责数据资产的日常维护和管理' },
  { roleId: 5, roleCode: 'USER', roleName: '普通用户', roleType: 'USER', status: 'ACTIVE', roleDescription: '查看已授权的数据资产信息' },
];

// 权限数据
const mockPermissions = [
  { permissionId: 1, permissionCode: 'user:create', permissionName: '创建用户', permissionType: 'BUTTON', parentId: null, status: 'ACTIVE' },
  { permissionId: 2, permissionCode: 'user:edit', permissionName: '编辑用户', permissionType: 'BUTTON', parentId: null, status: 'ACTIVE' },
  { permissionId: 3, permissionCode: 'user:delete', permissionName: '删除用户', permissionType: 'BUTTON', parentId: null, status: 'ACTIVE' },
  { permissionId: 4, permissionCode: 'role:create', permissionName: '创建角色', permissionType: 'BUTTON', parentId: null, status: 'ACTIVE' },
  { permissionId: 5, permissionCode: 'role:edit', permissionName: '编辑角色', permissionType: 'BUTTON', parentId: null, status: 'ACTIVE' },
  { permissionId: 6, permissionCode: 'role:delete', permissionName: '删除角色', permissionType: 'BUTTON', parentId: null, status: 'ACTIVE' },
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
  { assetId: 1, assetCode: 'ASSET001', assetName: '客户信息表', assetType: 'DATABASE', systemName: 'CRM系统', databaseType: 'MYSQL', databaseName: 'crm_db', tableName: 'customer_info', status: 'ACTIVE', departmentId: 2, ownerId: 2, classificationId: 2, gradingId: 2 },
  { assetId: 2, assetCode: 'ASSET002', assetName: '订单数据表', assetType: 'DATABASE', systemName: '订单系统', databaseType: 'MYSQL', databaseName: 'order_db', tableName: 'order_info', status: 'ACTIVE', departmentId: 3, ownerId: 3, classificationId: 3, gradingId: 2 },
  { assetId: 3, assetCode: 'ASSET003', assetName: '财务报表文件', assetType: 'FILE', systemName: '财务系统', databaseType: null, databaseName: null, tableName: null, status: 'ACTIVE', departmentId: 4, ownerId: 4, classificationId: 4, gradingId: 3 },
  { assetId: 4, assetCode: 'ASSET004', assetName: '员工信息API', assetType: 'API', systemName: '人事系统', databaseType: null, databaseName: null, tableName: null, status: 'ACTIVE', departmentId: 5, ownerId: 2, classificationId: 5, gradingId: 2 },
];

// 数据字段数据
const mockFields = [
  { fieldId: 1, assetId: 1, fieldName: 'customer_id', fieldCode: '客户ID', fieldType: 'BIGINT', isPrimaryKey: 1, isRequired: 1, classificationId: 2, gradingId: 2, status: 'ACTIVE' },
  { fieldId: 2, assetId: 1, fieldName: 'customer_name', fieldCode: '客户姓名', fieldType: 'VARCHAR', isPrimaryKey: 0, isRequired: 1, classificationId: 2, gradingId: 3, status: 'ACTIVE' },
  { fieldId: 3, assetId: 1, fieldName: 'customer_phone', fieldCode: '联系电话', fieldType: 'VARCHAR', isPrimaryKey: 0, isRequired: 0, classificationId: 2, gradingId: 3, status: 'ACTIVE' },
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
  if (username === 'admin' && password === 'admin123') {
    res.json({
      code: 0,
      message: '登录成功',
      data: {
        token: 'mock-jwt-token-' + Date.now(),
        userInfo: {
          userId: 1,
          username: 'admin',
          realName: '系统管理员',
          email: 'admin@example.com',
          roles: ['SYSTEM_ADMIN', 'DATA_ADMIN'],
        },
      },
    });
  } else {
    res.status(401).json({ code: 401, message: '用户名或密码错误' });
  }
});

app.post('/api/auth/logout', (req, res) => {
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
  const { oldPassword, newPassword } = req.body;
  
  // 简单验证
  if (!oldPassword || !newPassword) {
    return res.json({ code: 400, message: '请输入当前密码和新密码' });
  }
  
  if (oldPassword === newPassword) {
    return res.json({ code: 400, message: '新密码不能与当前密码相同' });
  }
  
  res.json({ code: 0, message: '密码修改成功' });
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
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredUsers.length,
    },
  });
});

app.post('/api/user/create', (req, res) => {
  const newUser = {
    userId: mockUsers.length + 1,
    ...req.body,
    status: req.body.status || 'ACTIVE',
  };
  mockUsers.push(newUser);
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
    mockUsers.splice(index, 1);
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
  
  const start = (pageNum - 1) * pageSize;
  const end = start + parseInt(pageSize);
  const list = filteredRoles.slice(start, end);
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredRoles.length,
    },
  });
});

app.post('/api/role/create', (req, res) => {
  const newRole = {
    roleId: mockRoles.length + 1,
    ...req.body,
    status: req.body.status || 'ACTIVE',
  };
  mockRoles.push(newRole);
  res.json({ code: 0, message: '角色创建成功', data: newRole });
});

app.put('/api/role/update', (req, res) => {
  const { roleId } = req.body;
  const index = mockRoles.findIndex(r => r.roleId === roleId);
  if (index !== -1) {
    mockRoles[index] = { ...mockRoles[index], ...req.body };
    res.json({ code: 0, message: '角色更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '角色不存在' });
  }
});

app.delete('/api/role/delete/:roleId', (req, res) => {
  const { roleId } = req.params;
  const index = mockRoles.findIndex(r => r.roleId === parseInt(roleId));
  if (index !== -1) {
    mockRoles.splice(index, 1);
    res.json({ code: 0, message: '角色删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '角色不存在' });
  }
});

// 权限管理
app.get('/api/permission/list', (req, res) => {
  const { pageNum = 1, pageSize = 10, permissionName, status } = req.query;
  let filteredPermissions = [...mockPermissions];
  
  if (permissionName) {
    filteredPermissions = filteredPermissions.filter(p => p.permissionName.includes(permissionName));
  }
  if (status) {
    filteredPermissions = filteredPermissions.filter(p => p.status === status);
  }
  
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
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredDepartments.length,
    },
  });
});

app.post('/api/department/create', (req, res) => {
  const newDepartment = {
    departmentId: mockDepartments.length + 1,
    ...req.body,
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
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredOwners.length,
    },
  });
});

app.post('/api/owner/create', (req, res) => {
  const newOwner = {
    ownerId: mockOwners.length + 1,
    ...req.body,
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
  const newStandard = {
    standardId: mockClassificationStandards.length + 1,
    ...req.body,
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
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredClassifications.length,
    },
  });
});

app.post('/api/classification/create', (req, res) => {
  const newClassification = {
    classificationId: mockClassifications.length + 1,
    ...req.body,
    status: req.body.status || 'ACTIVE',
  };
  mockClassifications.push(newClassification);
  res.json({ code: 0, message: '数据分类创建成功', data: newClassification });
});

app.put('/api/classification/update', (req, res) => {
  const { classificationId } = req.body;
  const index = mockClassifications.findIndex(c => c.classificationId === classificationId);
  if (index !== -1) {
    mockClassifications[index] = { ...mockClassifications[index], ...req.body };
    res.json({ code: 0, message: '数据分类更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '数据分类不存在' });
  }
});

app.delete('/api/classification/delete/:classificationId', (req, res) => {
  const { classificationId } = req.params;
  const index = mockClassifications.findIndex(c => c.classificationId === parseInt(classificationId));
  if (index !== -1) {
    mockClassifications.splice(index, 1);
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
  const newStandard = {
    standardId: mockGradingStandards.length + 1,
    ...req.body,
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
  
  res.json({
    code: 0,
    data: {
      list,
      total: filteredGradings.length,
    },
  });
});

app.post('/api/grading/create', (req, res) => {
  const newGrading = {
    gradingId: mockGradings.length + 1,
    ...req.body,
    status: req.body.status || 'ACTIVE',
  };
  mockGradings.push(newGrading);
  res.json({ code: 0, message: '数据分级创建成功', data: newGrading });
});

app.put('/api/grading/update', (req, res) => {
  const { gradingId } = req.body;
  const index = mockGradings.findIndex(g => g.gradingId === gradingId);
  if (index !== -1) {
    mockGradings[index] = { ...mockGradings[index], ...req.body };
    res.json({ code: 0, message: '数据分级更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '数据分级不存在' });
  }
});

app.delete('/api/grading/delete/:gradingId', (req, res) => {
  const { gradingId } = req.params;
  const index = mockGradings.findIndex(g => g.gradingId === parseInt(gradingId));
  if (index !== -1) {
    mockGradings.splice(index, 1);
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

  res.json({
    code: 0,
    data: {
      list,
      total: filteredAssets.length,
    },
  });
});

app.post('/api/asset/create', (req, res) => {
  const newAsset = {
    assetId: mockAssets.length + 1,
    ...req.body,
    status: req.body.status || 'DRAFT',
  };
  mockAssets.push(newAsset);
  res.json({ code: 0, message: '数据资产创建成功', data: newAsset });
});

app.put('/api/asset/update', (req, res) => {
  const { assetId } = req.body;
  const index = mockAssets.findIndex(a => a.assetId === assetId);
  if (index !== -1) {
    mockAssets[index] = { ...mockAssets[index], ...req.body };
    res.json({ code: 0, message: '数据资产更新成功' });
  } else {
    res.status(404).json({ code: 404, message: '数据资产不存在' });
  }
});

app.delete('/api/asset/delete/:assetId', (req, res) => {
  const { assetId } = req.params;
  const index = mockAssets.findIndex(a => a.assetId === parseInt(assetId));
  if (index !== -1) {
    mockAssets.splice(index, 1);
    res.json({ code: 0, message: '数据资产删除成功' });
  } else {
    res.status(404).json({ code: 404, message: '数据资产不存在' });
  }
});

// 字段管理
app.get('/api/asset/fields/:assetId', (req, res) => {
  const { assetId } = req.params;
  const fields = mockFields.filter(f => f.assetId === parseInt(assetId));
  res.json({ code: 0, data: fields });
});

// 统计分析
app.get('/api/statistics/asset', (req, res) => {
  res.json({ code: 0, data: mockStatistics });
});

app.get('/api/statistics/trend', (req, res) => {
  res.json({ code: 0, data: mockTrendData });
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

app.listen(PORT, () => {
  console.log(`Mock后端服务运行在 http://localhost:${PORT}`);
  console.log(`API文档: http://localhost:${PORT}/doc.html`);
});
