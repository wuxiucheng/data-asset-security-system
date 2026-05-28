import { http } from '@/utils/request'

// 用户登录
export interface LoginRequest {
  username: string
  password: string
  captcha?: string
  captchaKey?: string
  mfaCode?: string
  rememberMe?: boolean
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
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
  permissions: string[]
  mfaEnabled: boolean
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

  // 获取当前用户信息
  getCurrentUser() {
    return http.get<UserInfo>('/auth/current-user')
  },

  // 刷新Token
  refreshToken(refreshToken: string) {
    return http.post<LoginResponse>('/auth/refresh-token', { refreshToken })
  },

  // 验证Token
  validateToken(token: string) {
    return http.post<{ valid: boolean }>('/auth/validate-token', null, {
      headers: { Authorization: `Bearer ${token}` }
    })
  },

  // 撤销Token
  revokeToken(token: string) {
    return http.post('/auth/revoke-token', null, {
      headers: { Authorization: `Bearer ${token}` }
    })
  },
}

// 会话管理API
export interface SessionInfo {
  sessionId: number
  userId: number
  username: string
  loginTime: string
  lastAccessTime: string
  expireTime: string
  loginIp: string
  deviceInfo: string
  browserInfo: string
  status: string
}

export const sessionApi = {
  // 获取当前会话信息
  getCurrentSession() {
    return http.get<SessionInfo>('/session/current')
  },

  // 获取用户活跃会话列表
  getUserActiveSessions(userId: number) {
    return http.get<SessionInfo[]>(`/session/user/${userId}`)
  },

  // 获取当前用户的活跃会话列表
  getMyActiveSessions() {
    return http.get<SessionInfo[]>('/session/my-sessions')
  },

  // 强制下线指定会话
  forceLogoutSession(sessionId: number) {
    return http.post(`/session/${sessionId}/force-logout`)
  },

  // 强制下线当前用户的所有会话
  forceLogoutAllMySessions() {
    return http.post('/session/force-logout-all')
  },

  // 清理过期会话
  cleanExpiredSessions() {
    return http.post('/session/clean-expired')
  },
}

// MFA相关API
export interface MfaSetupInfo {
  userId: number
  username: string
  mfaType: string
  secret: string
  qrCodeUrl: string
  qrCodeImage: string
  enabled: boolean
  backupCodes?: string[]
}

export interface MfaEnableRequest {
  mfaType: string
  secret: string
  verificationCode: string
}

export interface MfaVerifyRequest {
  userId?: number
  code: string
  useBackupCode?: boolean
}

export const mfaApi = {
  // 生成MFA设置信息
  generateMfaSetup() {
    return http.get<MfaSetupInfo>('/mfa/setup')
  },

  // 启用MFA
  enableMfa(data: MfaEnableRequest) {
    return http.post('/mfa/enable', data)
  },

  // 验证MFA验证码
  verifyMfaCode(data: MfaVerifyRequest) {
    return http.post<{ valid: boolean }>('/mfa/verify', data)
  },

  // 禁用MFA
  disableMfa(password: string) {
    return http.post('/mfa/disable', null, {
      params: { password }
    })
  },

  // 检查MFA状态
  checkMfaStatus() {
    return http.get<{ enabled: boolean }>('/mfa/status')
  },

  // 获取MFA配置
  getMfaConfig() {
    return http.get<MfaSetupInfo>('/mfa/config')
  },

  // 生成备用码
  generateBackupCodes() {
    return http.post<string[]>('/mfa/backup-codes')
  },
}

// 审计日志API
export interface AuditLogQuery {
  operationType?: string
  moduleName?: string
  objectType?: string
  operatorId?: number
  operatorUsername?: string
  operationResult?: string
  startTime?: string
  endTime?: string
  keyword?: string
  pageNum?: number
  pageSize?: number
}

export interface AuditLogInfo {
  logId: number
  operationType: string
  module: string
  objectType: string
  objectId?: number
  objectName?: string
  operationDescription?: string
  operationContent?: string
  operationResult: string
  errorMessage?: string
  operatorId?: number
  operatorName?: string
  operatorUsername?: string
  operationTime: string
  operationIp?: string
  operationLocation?: string
  userAgent?: string
  requestUrl?: string
  requestMethod?: string
  executionTime?: number
}

export interface AuditLogStatistics {
  totalOperations: number
  successCount: number
  failureCount: number
  successRate: number
  operationTypeStats: Record<string, number>
  moduleStats: Record<string, number>
  userStats: Record<string, number>
  dateStats: Record<string, number>
  startTime?: string
  endTime?: string
}

export const auditLogApi = {
  // 分页查询审计日志
  queryAuditLogs(data: AuditLogQuery) {
    return http.post<{
      records: AuditLogInfo[]
      total: number
      size: number
      current: number
      pages: number
    }>('/audit-logs/query', data)
  },

  // 统计审计日志
  statisticsAuditLogs(data: AuditLogQuery) {
    return http.post<AuditLogStatistics>('/audit-logs/statistics', data)
  },

  // 导出审计日志
  exportAuditLogs(data: AuditLogQuery) {
    return http.post<{ filePath: string }>('/audit-logs/export', data)
  },

  // 归档审计日志
  archiveAuditLogs(beforeTime: string) {
    return http.post<{ count: number }>('/audit-logs/archive', null, {
      params: { beforeTime }
    })
  },

  // 清理已归档的审计日志
  cleanArchivedAuditLogs(beforeTime: string) {
    return http.post<{ count: number }>('/audit-logs/clean', null, {
      params: { beforeTime }
    })
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
    return http.post('/user/page', { ...params, current: params.pageNum || params.page || 1, size: params.pageSize || params.size || 10 })
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

  // 修改密码（用户修改自己的密码）
  changePassword(data: any) {
    return http.post('/auth/changePassword', data)
  },

  // 重置密码（管理员重置用户密码）
  resetPassword(userId: number, newPassword: string) {
    return http.put(`/user/${userId}/reset-password?newPassword=${newPassword}`)
  },
}

// 脱敏策略管理
export const maskStrategyApi = {
  // 获取策略列表
  getList(params?: any) {
    return http.get('/mask-strategy/list', { params })
  },

  // 获取策略详情
  getDetail(id: number) {
    return http.get(`/mask-strategy/${id}`)
  },

  // 新增策略
  create(data: any) {
    return http.post('/mask-strategy', data)
  },

  // 更新策略
  update(id: number, data: any) {
    return http.put(`/mask-strategy/${id}`, data)
  },

  // 删除策略
  delete(id: number) {
    return http.delete(`/mask-strategy/${id}`)
  },

  // 测试脱敏
  test(id: number, data: any) {
    return http.post(`/mask-strategy/${id}/test`, data)
  }
}

// 质量规则管理
export const qualityRuleApi = {
  // 获取规则列表
  getList(params?: any) {
    return http.get('/quality-rule/list', { params })
  },

  // 获取规则详情
  getDetail(id: number) {
    return http.get(`/quality-rule/${id}`)
  },

  // 新增规则
  create(data: any) {
    return http.post('/quality-rule', data)
  },

  // 更新规则
  update(id: number, data: any) {
    return http.put(`/quality-rule/${id}`, data)
  },

  // 删除规则
  delete(id: number) {
    return http.delete(`/quality-rule/${id}`)
  },

  // 测试规则
  test(id: number, data: any) {
    return http.post(`/quality-rule/${id}/test`, data)
  }
}

// 质量任务管理
export const qualityTaskApi = {
  // 获取任务列表
  getList(params?: any) {
    return http.get('/quality-task/list', { params })
  },

  // 获取任务详情
  getDetail(id: number) {
    return http.get(`/quality-task/${id}`)
  },

  // 新增任务
  create(data: any) {
    return http.post('/quality-task', data)
  },

  // 更新任务
  update(id: number, data: any) {
    return http.put(`/quality-task/${id}`, data)
  },

  // 删除任务
  delete(id: number) {
    return http.delete(`/quality-task/${id}`)
  },

  // 执行任务
  execute(id: number) {
    return http.post(`/quality-task/${id}/execute`)
  },

  // 停止任务
  stop(id: number) {
    return http.post(`/quality-task/${id}/stop`)
  },

  // 获取任务结果
  getResult(id: number) {
    return http.get(`/quality-task/${id}/result`)
  }
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
    return http.post('/role/page', { ...params, current: params.pageNum || params.page || 1, size: params.pageSize || params.size || 10 })
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
    return http.post('/department/page', { ...params, current: params.pageNum || params.page || 1, size: params.pageSize || params.size || 10 })
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
    return http.post('/owner/page', { ...params, current: params.pageNum || params.page || 1, size: params.pageSize || params.size || 10 })
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
    return http.post('/classificationStandard/page', { ...params, current: params.pageNum || params.page || 1, size: params.pageSize || params.size || 10 })
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
    return http.put(`/classificationStandard/${standardId}/publish`)
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
    return http.post('/classification/page', { ...params, current: params.pageNum || params.page || 1, size: params.pageSize || params.size || 10 })
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
  dataSourceId: number | null
  dataSourceName: string
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
  rowCount: number | null
  createTime: string
  updateTime: string
}

export const dataAssetApi = {
  // 获取资产列表
  getList(params: any) {
    return http.post('/asset/page', { ...params, current: params.pageNum || params.page || 1, size: params.pageSize || params.size || 10 })
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

  // 批量删除资产
  batchDelete(ids: number[]) {
    return http.post('/asset/batch-delete', ids)
  },

  // 批量导入
  import(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return http.post('/asset/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 下载导入模板
  getImportTemplate() {
    return http.get('/asset/import-template')
  },

  // 导出资产
  export(params: any) {
    return http.get('/asset/export', { params, responseType: 'blob' })
  },

  // 刷新资产数据条数
  refreshRowCount(assetId: number) {
    return http.post(`/asset/refresh-row-count/${assetId}`)
  },

  // 批量刷新数据条数
  batchRefreshRowCount(data: { assetIds: number[], refreshScope: string }) {
    return http.post('/asset/batch-refresh-row-count', data)
  },

  // 查询批量刷新进度
  getBatchRefreshProgress(taskId: string) {
    return http.get(`/asset/batch-refresh-progress/${taskId}`)
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
    return http.get<Permission[]>('/permission/all')
  },

  // 获取权限树
  getTree() {
    return http.post<Permission[]>('/permission/tree', {})
  },

  // 更新权限
  update(data: any) {
    return http.put('/permission/update', data)
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
    return http.post('/gradingStandard/page', { ...params, current: params.pageNum || params.page || 1, size: params.pageSize || params.size || 10 })
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
    return http.put(`/gradingStandard/${standardId}/publish`)
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
    return http.post('/grading/page', { ...params, current: params.pageNum || params.page || 1, size: params.pageSize || params.size || 10 })
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
  rowCount: number | null
  createTime: string
}

export const assetFieldApi = {
  // 获取资产字段列表
  getFieldsByAssetId(assetId: number) {
    return http.get<AssetField[]>(`/asset/field/${assetId}`)
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

  // 获取字段导入模板
  getImportTemplate() {
    return http.get('/asset/field/import-template')
  },

  // 批量导入字段
  import(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return http.post('/asset/field/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 刷新字段数据条数
  refreshRowCount(fieldId: number) {
    return http.post(`/asset/field/refresh-row-count/${fieldId}`)
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

// 报告相关API
export const reportApi = {
  // 生成资产清单报告
  generateAssetListReport(params: any) {
    return http.get('/report/asset-list/generate', { params })
  },

  // 导出资产清单报告
  exportAssetListReport(params: any) {
    return http.get('/report/asset-list/export', { params, responseType: 'blob' })
  },

  // 生成分类分级统计报告
  generateClassificationStatsReport(params: any) {
    return http.get('/report/classification-stats/generate', { params })
  },

  // 导出分类分级统计报告
  exportClassificationStatsReport(params: any) {
    return http.get('/report/classification-stats/export', { params, responseType: 'blob' })
  },

  // 获取报告生成历史
  getReportHistory(params: any) {
    return http.get('/report/history', { params })
  },

  // 删除报告
  deleteReport(reportId: number) {
    return http.delete(`/report/${reportId}`)
  },
}

// 资产发现
export interface DatabaseConnection {
  databaseType: string
  host: string
  port: number
  databaseName: string
  username: string
  password: string
}

export interface DiscoveredTable {
  tableName: string
  tableComment: string
  tableType: string
  rowCount: number
  fields?: DiscoveredField[]
}

export interface DiscoveredField {
  fieldName: string
  fieldType: string
  fieldLength: number
  nullable: boolean
  isPrimaryKey: boolean
  fieldComment: string
  defaultValue: string
}

export const assetDiscoveryApi = {
  // 测试数据库连接
  testConnection(data: DatabaseConnection) {
    return http.post<boolean>('/asset-discovery/test-connection', data)
  },

  // 扫描数据库表
  scanTables(data: DatabaseConnection) {
    return http.post<DiscoveredTable[]>('/asset-discovery/scan-tables', data)
  },

  // 扫描表字段
  scanFields(data: DatabaseConnection, tableName: string) {
    return http.post<DiscoveredTable>(`/asset-discovery/scan-fields?tableName=${tableName}`, data)
  },

  // 导入发现的资产
  importAssets(data: any) {
    return http.post('/asset-discovery/import', data)
  },

  // 检测导入重复
  checkDuplicates(data: any) {
    return http.post('/asset-discovery/check-duplicates', data)
  },
}

// ==================== 数据源配置 ====================

export interface DataSourceConfig {
  dataSourceId: number
  dataSourceName: string
  databaseType: string
  host: string
  port: number
  databaseName: string
  username: string
  connectionParams?: string
  status: string
  lastTestTime?: string
  lastTestResult?: string
  remarks?: string
  createdTime?: string
  updatedTime?: string
}

export const dataSourceConfigApi = {
  // 分页查询
  getList(params: any) {
    return http.post('/datasource/page', { ...params, current: params.pageNum || params.page || 1, size: params.pageSize || params.size || 10 })
  },

  // 查询所有活跃数据源（下拉选择用）
  listActive() {
    return http.get('/datasource/list')
  },

  // 获取详情
  getDetail(id: number) {
    return http.get(`/datasource/${id}`)
  },

  // 创建
  create(data: any) {
    return http.post('/datasource', data)
  },

  // 更新
  update(id: number, data: any) {
    return http.put(`/datasource/${id}`, data)
  },

  // 删除
  delete(id: number) {
    return http.delete(`/datasource/${id}`)
  },

  // 测试已保存数据源的连接
  testConnection(id: number) {
    return http.post(`/datasource/test-connection/${id}`)
  },

  // 测试连接（不保存）
  testConnectionWithData(data: any) {
    return http.post('/datasource/test-connection', data)
  },
}

// ==================== 敏感数据识别 ====================

export interface SensitiveIdentRule {
  ruleId: number
  ruleName: string
  sensitiveType: string
  matchMode: string
  matchExpression: string
  confidenceWeight: number
  priority: number
  isBuiltin: boolean
  status: string
  createdTime?: string
  updatedTime?: string
}

export interface SensitiveIdentResult {
  resultId: number
  assetId: number
  assetName?: string
  fieldId: number
  fieldName: string
  ruleId: number
  ruleName: string
  sensitiveType: string
  matchMode: string
  confidenceScore: number
  matchDetail?: string
  identifyTime: string
  confirmStatus: string
  confirmerId?: number
  confirmerName?: string
  confirmTime?: string
  confirmRemark?: string
}

export const sensitiveIdentApi = {
  // 分页查询规则
  getRulePage(params: any) {
    return http.post('/sensitive-rule/page', params)
  },

  // 获取所有启用的规则
  listEnabledRules() {
    return http.get<SensitiveIdentRule[]>('/sensitive-rule/list')
  },

  // 获取规则详情
  getRuleDetail(ruleId: number) {
    return http.get<SensitiveIdentRule>(`/sensitive-rule/${ruleId}`)
  },

  // 创建规则
  createRule(data: any) {
    return http.post<SensitiveIdentRule>('/sensitive-rule', data)
  },

  // 更新规则
  updateRule(data: any) {
    return http.put<SensitiveIdentRule>('/sensitive-rule', data)
  },

  // 删除规则
  deleteRule(ruleId: number) {
    return http.delete(`/sensitive-rule/${ruleId}`)
  },

  // 启用/禁用规则
  updateRuleStatus(ruleId: number, status: string) {
    return http.put(`/sensitive-rule/${ruleId}/status?status=${status}`)
  },

  // 初始化内置规则
  initBuiltinRules() {
    return http.post('/sensitive-rule/init-builtin')
  },
}
