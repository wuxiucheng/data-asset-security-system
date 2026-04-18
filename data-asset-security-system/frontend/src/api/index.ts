import { http } from '@/utils/request'

// 用户登录
export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  userInfo: UserInfo
}

export interface UserInfo {
  userId: number
  username: string
  realName: string
  email: string
  phone: string
  avatar?: string
  roles: string[]
}

// 认证相关API
export const authApi = {
  // 登录
  login(data: LoginRequest) {
    return http.post<LoginResponse>('/auth/login', data)
  },

  // 登出
  logout() {
    return http.post('/auth/logout')
  },

  // 获取用户信息
  getUserInfo() {
    return http.get<UserInfo>('/auth/userInfo')
  },

  // 更新个人信息
  updateProfile(data: any) {
    return http.put('/auth/updateProfile', data)
  },

  // 修改密码
  changePassword(data: any) {
    return http.post('/auth/changePassword', data)
  },
}

// 用户管理
export interface User {
  userId: number
  username: string
  realName: string
  email: string
  phone: string
  status: string
  createTime: string
  updateTime: string
}

export const userApi = {
  // 获取用户列表
  getList(params: any) {
    return http.get<{ list: User[]; total: number }>('/user/list', { params })
  },

  // 创建用户
  create(data: any) {
    return http.post('/user/create', data)
  },

  // 更新用户
  update(data: any) {
    return http.put('/user/update', data)
  },

  // 删除用户
  delete(userId: number) {
    return http.delete(`/user/delete/${userId}`)
  },

  // 修改密码
  changePassword(data: any) {
    return http.post('/user/changePassword', data)
  },
}

// 角色管理
export interface Role {
  roleId: number
  roleCode: string
  roleName: string
  roleDescription: string
  roleType: string
  status: string
  createTime: string
}

export const roleApi = {
  // 获取角色列表
  getList(params: any) {
    return http.get<{ list: Role[]; total: number }>('/role/list', { params })
  },

  // 创建角色
  create(data: any) {
    return http.post('/role/create', data)
  },

  // 更新角色
  update(data: any) {
    return http.put('/role/update', data)
  },

  // 删除角色
  delete(roleId: number) {
    return http.delete(`/role/delete/${roleId}`)
  },

  // 获取角色权限
  getPermissions(roleId: number) {
    return http.get(`/role/permissions/${roleId}`)
  },

  // 分配权限
  assignPermissions(data: any) {
    return http.post('/role/assignPermissions', data)
  },
}

// 部门管理
export interface Department {
  departmentId: number
  departmentCode: string
  departmentName: string
  leaderId: number
  contactPhone: string
  departmentDescription: string
  parentId: number
  sortOrder: number
  status: string
  children?: Department[]
}

export const departmentApi = {
  // 获取部门树
  getTree(params?: any) {
    return http.get<Department[]>('/department/tree', { params })
  },

  // 获取部门列表
  getList(params: any) {
    return http.get<{ list: Department[]; total: number }>('/department/list', { params })
  },

  // 创建部门
  create(data: any) {
    return http.post('/department/create', data)
  },

  // 更新部门
  update(data: any) {
    return http.put('/department/update', data)
  },

  // 删除部门
  delete(departmentId: number) {
    return http.delete(`/department/delete/${departmentId}`)
  },
}

// 责任人管理
export interface Owner {
  ownerId: number
  employeeNo: string
  name: string
  departmentId: number
  position: string
  contactPhone: string
  email: string
  userAccount: string
  status: string
  createTime: string
}

export const ownerApi = {
  // 获取责任人列表
  getList(params: any) {
    return http.get<{ list: Owner[]; total: number }>('/owner/list', { params })
  },

  // 创建责任人
  create(data: any) {
    return http.post('/owner/create', data)
  },

  // 更新责任人
  update(data: any) {
    return http.put('/owner/update', data)
  },

  // 删除责任人
  delete(ownerId: number) {
    return http.delete(`/owner/delete/${ownerId}`)
  },
}

// 分类标准管理
export interface ClassificationStandard {
  standardId: number
  standardCode: string
  standardName: string
  standardDescription: string
  version: string
  publishDate: string
  publishUnit: string
  scope: string
  status: string
  createTime: string
}

export const classificationStandardApi = {
  // 获取分类标准列表
  getList(params: any) {
    return http.get<{ list: ClassificationStandard[]; total: number }>('/classificationStandard/list', { params })
  },

  // 创建分类标准
  create(data: any) {
    return http.post('/classificationStandard/create', data)
  },

  // 更新分类标准
  update(data: any) {
    return http.put('/classificationStandard/update', data)
  },

  // 删除分类标准
  delete(standardId: number) {
    return http.delete(`/classificationStandard/delete/${standardId}`)
  },

  // 发布分类标准
  publish(standardId: number) {
    return http.post(`/classificationStandard/publish/${standardId}`)
  },
}

// 数据分类管理
export interface DataClassification {
  classificationId: number
  standardId: number
  classificationCode: string
  classificationName: string
  classificationDescription: string
  parentId: number
  level: number
  sortOrder: number
  status: string
  children?: DataClassification[]
}

export const dataClassificationApi = {
  // 获取分类树
  getTree(standardId: number) {
    return http.get<DataClassification[]>(`/classification/tree/${standardId}`)
  },

  // 获取分类列表
  getList(params: any) {
    return http.get<{ list: DataClassification[]; total: number }>('/classification/list', { params })
  },

  // 创建分类
  create(data: any) {
    return http.post('/classification/create', data)
  },

  // 更新分类
  update(data: any) {
    return http.put('/classification/update', data)
  },

  // 删除分类
  delete(classificationId: number) {
    return http.delete(`/classification/delete/${classificationId}`)
  },
}

// 数据资产管理
export interface DataAsset {
  assetId: number
  assetName: string
  assetCode: string
  assetType: string
  systemName: string
  databaseType: string
  databaseHost: string
  databasePort: number
  databaseName: string
  tableName: string
  assetDescription: string
  departmentId: number
  ownerId: number
  classificationId: number
  gradingId: number
  sensitivityScore: number
  dataVolumeLevel: string
  accessFrequency: string
  importanceLevel: string
  status: string
  containsSensitiveData: boolean
  sensitiveDataType: string
  createTime: string
  updateTime: string
}

export const dataAssetApi = {
  // 获取资产列表
  getList(params: any) {
    return http.get<{ list: DataAsset[]; total: number }>('/asset/list', { params })
  },

  // 获取资产详情
  getDetail(assetId: number) {
    return http.get<DataAsset>(`/asset/detail/${assetId}`)
  },

  // 创建资产
  create(data: any) {
    return http.post('/asset/create', data)
  },

  // 更新资产
  update(data: any) {
    return http.put('/asset/update', data)
  },

  // 删除资产
  delete(assetId: number) {
    return http.delete(`/asset/delete/${assetId}`)
  },

  // 批量导入
  import(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return http.upload('/asset/import', formData)
  },

  // 导出资产
  export(params: any) {
    return http.get('/asset/export', { params, responseType: 'blob' })
  },
}

// 权限管理
export interface Permission {
  permissionId: number
  permissionCode: string
  permissionName: string
  permissionType: string
  permissionUrl: string
  permissionMethod: string
  parentId: number
  sortOrder: number
  status: string
  children?: Permission[]
}

export const permissionApi = {
  // 获取权限列表
  getList(params: any) {
    return http.get<{ list: Permission[]; total: number }>('/permission/list', { params })
  },

  // 获取权限树
  getTree() {
    return http.get<Permission[]>('/permission/tree')
  },
}

// 分级标准管理
export interface GradingStandard {
  standardId: number
  standardCode: string
  standardName: string
  standardDescription: string
  version: string
  publishDate: string
  publishUnit: string
  scope: string
  status: string
  createTime: string
}

export const gradingStandardApi = {
  // 获取分级标准列表
  getList(params: any) {
    return http.get<{ list: GradingStandard[]; total: number }>('/gradingStandard/list', { params })
  },

  // 创建分级标准
  create(data: any) {
    return http.post('/gradingStandard/create', data)
  },

  // 更新分级标准
  update(data: any) {
    return http.put('/gradingStandard/update', data)
  },

  // 删除分级标准
  delete(standardId: number) {
    return http.delete(`/gradingStandard/delete/${standardId}`)
  },

  // 发布分级标准
  publish(standardId: number) {
    return http.post(`/gradingStandard/publish/${standardId}`)
  },
}

// 数据分级管理
export interface DataGrading {
  gradingId: number
  standardId: number
  gradingCode: string
  gradingName: string
  gradingDescription: string
  levelValue: number
  securityMeasures: string
  accessControl: string
  retentionPeriod: string
  status: string
  createTime: string
}

export const dataGradingApi = {
  // 获取分级列表
  getList(params: any) {
    return http.get<{ list: DataGrading[]; total: number }>('/grading/list', { params })
  },

  // 创建分级
  create(data: any) {
    return http.post('/grading/create', data)
  },

  // 更新分级
  update(data: any) {
    return http.put('/grading/update', data)
  },

  // 删除分级
  delete(gradingId: number) {
    return http.delete(`/grading/delete/${gradingId}`)
  },
}

// 字段管理
export interface AssetField {
  fieldId: number
  assetId: number
  fieldName: string
  fieldCode: string
  fieldType: string
  fieldLength: number
  isNullable: number
  isPrimaryKey: number
  isRequired: number
  defaultValue: string
  fieldDescription: string
  classificationId: number
  gradingId: number
  sensitiveType: string
  riskLevel: string
  status: string
  createTime: string
}

export const assetFieldApi = {
  // 获取资产字段列表
  getFieldsByAssetId(assetId: number) {
    return http.get<AssetField[]>(`/asset/fields/${assetId}`)
  },

  // 创建字段
  create(data: any) {
    return http.post('/asset/field/create', data)
  },

  // 更新字段
  update(data: any) {
    return http.put('/asset/field/update', data)
  },

  // 删除字段
  delete(fieldId: number) {
    return http.delete(`/asset/field/delete/${fieldId}`)
  },

  // 批量更新字段
  batchUpdate(data: any) {
    return http.post('/asset/field/batchUpdate', data)
  },
}

// 统计分析
export interface Statistics {
  totalAssets: number
  byDepartment: Array<{ name: string; value: number }>
  byClassification: Array<{ name: string; value: number }>
  byGrading: Array<{ name: string; value: number }>
  byStatus: Array<{ name: string; value: number }>
}

export interface TrendData {
  dates: string[]
  assetGrowth: number[]
  classificationGrowth: number[]
  gradingGrowth: number[]
}

export const statisticsApi = {
  // 获取资产统计
  getAssetStatistics() {
    return http.get<Statistics>('/statistics/asset')
  },

  // 获取趋势数据
  getTrendData(params: any) {
    return http.get<TrendData>('/statistics/trend', { params })
  },
}
