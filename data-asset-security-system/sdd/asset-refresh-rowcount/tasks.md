# 资产与字段刷新数据条数功能 - 编码任务规划

## 1. 任务总览

| 主任务 | 描述 | 子任务数 | 覆盖需求 |
|--------|------|---------|---------|
| 1 | 数据库模型与后端实体变更 | 2 | REQ-BE-003 |
| 2 | Mock后端接口实现 | 5 | REQ-MOCK-001~004 |
| 3 | 前端API层与接口类型扩展 | 2 | REQ-FE-001~010 |
| 4 | 资产清单页面改造 | 4 | REQ-FE-001~004 |
| 5 | 字段管理页面改造 | 4 | REQ-FE-005~007 |
| 6 | 批量刷新页面开发 | 4 | REQ-FE-008~010 |
| 7 | 后端Service与Controller实现 | 5 | REQ-BE-001~002, REQ-BE-004~006 |
| 8 | 集成验证与收尾 | 2 | REQ-NFR-001~003 |

---

## 2. 任务详细规划

### 任务 1: 数据库模型与后端实体变更

**描述**：为data_asset和data_field表新增row_count字段，并同步更新后端实体类和VO类。

**输入**：spec.md 3.1~3.2节数据模型变更定义
**输出**：数据库迁移SQL执行完成，Java实体类和VO类包含rowCount属性

#### 1.1 执行数据库迁移SQL
- **描述**：在data_asset和data_field表各新增row_count BIGINT DEFAULT NULL字段
- **操作**：编写并执行ALTER TABLE语句，更新init.sql文件
- **验收标准**：data_asset和data_field表均包含row_count字段，类型BIGINT，允许NULL，默认NULL
- **涉及文件**：`backend/src/main/resources/db/init.sql`

#### 1.2 更新后端实体类和VO类
- **描述**：在DataAsset、DataAssetVO、DataField、DataFieldVO四个类中新增rowCount属性（类型Long）
- **操作**：为每个类添加`private Long rowCount;`属性及对应Schema注解
- **验收标准**：四个类均包含rowCount属性，类型为Long，Swagger文档正确展示
- **涉及文件**：
  - `backend/src/main/java/com/dataasset/security/entity/DataAsset.java`
  - `backend/src/main/java/com/dataasset/security/vo/DataAssetVO.java`
  - `backend/src/main/java/com/dataasset/security/entity/DataField.java`
  - `backend/src/main/java/com/dataasset/security/vo/DataFieldVO.java`

---

### 任务 2: Mock后端接口实现

**描述**：在Mock后端中实现所有数据条数查询相关接口，包括单条刷新、批量刷新、进度查询，以及补充mock数据rowCount字段。

**输入**：design.md 第6节Mock后端详细设计
**输出**：Mock后端可响应所有新增接口请求

#### 2.1 补充mockAssets和mockFields的rowCount字段
- **描述**：在mockAssets和mockFields数据中为每个对象添加rowCount字段初始值
- **操作**：数据库类型资产赋予模拟数值，非数据库类型赋予null；字段赋予模拟数值
- **验收标准**：mockAssets和mockFields每个对象均包含rowCount字段
- **涉及文件**：`simple-backend/server.js`

#### 2.2 实现资产数据条数查询接口
- **描述**：新增POST /api/asset/refresh-row-count/:assetId接口，模拟返回rowCount并更新mockAssets
- **操作**：按design.md 6.3节实现，包含资产不存在、缺少连接信息等错误处理，记录审计日志
- **验收标准**：接口返回模拟rowCount值（1000~100000），更新mockAssets对应资产，记录审计日志
- **涉及文件**：`simple-backend/server.js`

#### 2.3 实现字段数据条数查询接口
- **描述**：新增POST /api/asset/field/refresh-row-count/:fieldId接口，模拟返回rowCount并更新mockFields
- **操作**：按design.md 6.4节实现，包含字段不存在、所属资产不存在等错误处理，记录审计日志
- **验收标准**：接口返回模拟rowCount值（500~80000且<=资产rowCount），更新mockFields对应字段，记录审计日志
- **涉及文件**：`simple-backend/server.js`

#### 2.4 实现批量刷新接口和进度查询接口
- **描述**：新增POST /api/asset/batch-refresh-row-count和GET /api/asset/batch-refresh-progress/:taskId接口
- **操作**：按design.md 6.7节实现，使用setTimeout模拟逐条处理，batchRefreshTasks存储任务进度
- **验收标准**：批量刷新接口返回taskId，进度接口返回实时进度，任务完成后状态为COMPLETED
- **涉及文件**：`simple-backend/server.js`

#### 2.5 确保资产分页和字段列表接口返回rowCount
- **描述**：验证现有POST /api/asset/page和GET /api/asset/fields/:assetId接口返回数据包含rowCount字段
- **操作**：由于mockAssets和mockFields已补充rowCount字段，确认接口返回数据自然包含该字段即可
- **验收标准**：资产分页接口和字段列表接口返回数据均包含rowCount字段
- **涉及文件**：`simple-backend/server.js`

---

### 任务 3: 前端API层与接口类型扩展

**描述**：在前端API层新增数据条数查询相关方法，扩展TypeScript接口类型定义。

**输入**：design.md 第5.1节API层扩展
**输出**：前端可调用所有新增API方法

#### 3.1 扩展DataAsset和AssetField接口类型
- **描述**：在DataAsset接口中新增rowCount: number | null，在AssetField接口中新增rowCount: number | null
- **操作**：修改frontend/src/api/index.ts中的接口定义
- **验收标准**：TypeScript编译无错误，接口类型包含rowCount属性
- **涉及文件**：`frontend/src/api/index.ts`

#### 3.2 新增API方法
- **描述**：在dataAssetApi中新增refreshRowCount、batchRefreshRowCount、getBatchRefreshProgress方法；在assetFieldApi中新增refreshRowCount方法；新增BatchRefreshResultItem和BatchRefreshProgress接口类型
- **操作**：按design.md 5.1节实现，包含完整的TypeScript类型定义
- **验收标准**：所有API方法可正常调用，TypeScript类型完整无any
- **涉及文件**：`frontend/src/api/index.ts`

---

### 任务 4: 资产清单页面改造

**描述**：在资产清单页面新增"数据条数"列、"刷新"按钮和"批量刷新"按钮。

**输入**：design.md 第5.2节资产清单页面改造
**输出**：资产清单页面支持单条刷新和跳转批量刷新

#### 4.1 表格新增"数据条数"列
- **描述**：在"表名"列与"状态"列之间插入"数据条数"列，根据rowCount值和资产类型显示不同内容
- **操作**：添加el-table-column，实现三种显示逻辑：loading指示器、"不适用"、数值、"-"，数值使用toLocaleString()格式化
- **验收标准**：REQ-FE-001所有验收标准满足
- **涉及文件**：`frontend/src/views/asset/index.vue`

#### 4.2 操作列新增"刷新"按钮
- **描述**：在操作列"查看"按钮前插入"刷新"按钮，根据资产类型和连接信息完整性控制disabled状态
- **操作**：添加el-button，实现canRefreshAsset判断函数，绑定loading状态
- **验收标准**：REQ-FE-002所有验收标准满足
- **涉及文件**：`frontend/src/views/asset/index.vue`

#### 4.3 实现刷新按钮点击逻辑
- **描述**：点击"刷新"按钮调用dataAssetApi.refreshRowCount，更新行数据，处理成功/失败/超时
- **操作**：实现handleRefreshRowCount函数，包含refreshingAssetId状态管理、API调用、结果更新、错误提示
- **验收标准**：REQ-FE-003所有验收标准满足
- **涉及文件**：`frontend/src/views/asset/index.vue`

#### 4.4 新增"批量刷新"按钮并实现跳转
- **描述**：在顶部操作区域"批量导出"按钮后新增"批量刷新"按钮，点击跳转到/batch-refresh
- **操作**：添加el-button（type="info"），引入useRouter，实现handleBatchRefresh跳转逻辑
- **验收标准**：REQ-FE-004所有验收标准满足
- **涉及文件**：`frontend/src/views/asset/index.vue`

---

### 任务 5: 字段管理页面改造

**描述**：在字段管理页面新增"数据条数"列和"刷新"按钮。

**输入**：design.md 第5.3节字段管理页面改造
**输出**：字段管理页面支持单条刷新

#### 5.1 表格新增"数据条数"列
- **描述**：在"字段编码"列与"字段类型"列之间插入"数据条数"列，根据rowCount值和所属资产类型显示不同内容
- **操作**：添加el-table-column，实现三种显示逻辑：loading指示器、"不适用"、数值、"-"，需计算currentAssetCanRefresh
- **验收标准**：REQ-FE-005所有验收标准满足
- **涉及文件**：`frontend/src/views/asset-field/index.vue`

#### 5.2 操作列新增"刷新"按钮
- **描述**：在操作列"编辑"按钮前插入"刷新"按钮，根据所属资产类型和连接信息控制disabled状态
- **操作**：添加el-button，绑定loading状态和disabled条件，需获取当前选中资产的详情信息
- **验收标准**：REQ-FE-006所有验收标准满足
- **涉及文件**：`frontend/src/views/asset-field/index.vue`

#### 5.3 实现刷新按钮点击逻辑
- **描述**：点击"刷新"按钮调用assetFieldApi.refreshRowCount，更新行数据，处理成功/失败/超时
- **操作**：实现handleRefreshFieldRowCount函数，包含refreshingFieldId状态管理、API调用、结果更新、错误提示
- **验收标准**：REQ-FE-007所有验收标准满足
- **涉及文件**：`frontend/src/views/asset-field/index.vue`

#### 5.4 资产选择时获取资产详情
- **描述**：当用户在字段管理页面选择所属资产时，同步获取该资产的详情（含数据库连接信息），用于判断刷新按钮是否可用
- **操作**：监听searchForm.assetId变化，调用dataAssetApi.getDetail获取资产详情，计算currentAssetCanRefresh
- **验收标准**：选择资产后，刷新按钮的disabled状态正确反映该资产是否可刷新
- **涉及文件**：`frontend/src/views/asset-field/index.vue`

---

### 任务 6: 批量刷新页面开发

**描述**：新建批量刷新页面，实现按数据库筛选资产、批量执行刷新、进度展示和结果汇总。

**输入**：design.md 第5.4节批量刷新页面
**输出**：完整的批量刷新页面，路由可访问

#### 6.1 创建批量刷新页面文件和路由
- **描述**：新建frontend/src/views/batch-refresh/index.vue文件，在router/index.ts中注册路由
- **操作**：创建Vue3 SFC组件骨架，添加路由配置{ path: 'batch-refresh', name: 'BatchRefresh', ... }
- **验收标准**：访问/batch-refresh路径可正常显示页面，页面标题为"批量刷新"
- **涉及文件**：
  - `frontend/src/views/batch-refresh/index.vue`（新建）
  - `frontend/src/router/index.ts`

#### 6.2 实现数据库筛选和资产列表展示
- **描述**：页面顶部实现数据库类型/地址/端口/名称筛选条件，查询后展示匹配的DATABASE类型资产列表，支持勾选
- **操作**：实现filterForm、handleQuery、assetList、selectedAssets，el-table含选择框+资产ID+编码+名称+表名+数据条数+状态列
- **验收标准**：REQ-FE-008所有验收标准满足
- **涉及文件**：`frontend/src/views/batch-refresh/index.vue`

#### 6.3 实现刷新范围选择和开始刷新逻辑
- **描述**：添加刷新范围单选组（ASSET_ONLY/ASSET_AND_FIELD），点击"开始刷新"提交批量任务并启动进度轮询
- **操作**：实现refreshScope、handleStartRefresh、startPolling/stopPolling，每2秒轮询进度接口，更新progress状态
- **验收标准**：REQ-FE-009和REQ-FE-010所有验收标准满足
- **涉及文件**：`frontend/src/views/batch-refresh/index.vue`

#### 6.4 实现进度展示和结果汇总
- **描述**：展示进度条（已完成/总数+当前资产名称）、刷新结果汇总（成功数/失败数/失败列表）、"返回资产列表"按钮
- **操作**：使用el-progress展示进度，el-descriptions展示结果汇总，完成后更新资产列表rowCount值
- **验收标准**：批量刷新过程中实时展示进度，完成后展示结果汇总，点击返回跳转到/asset
- **涉及文件**：`frontend/src/views/batch-refresh/index.vue`

---

### 任务 7: 后端Service与Controller实现

**描述**：实现后端DynamicDatabaseQueryService、扩展DataAssetService和DataFieldService、扩展Controller接口。

**输入**：design.md 第4节后端详细设计
**输出**：后端所有数据条数查询接口可正常工作

#### 7.1 实现DynamicDatabaseQueryService
- **描述**：新建动态数据库查询服务，根据databaseType建立JDBC短连接，执行COUNT查询，查询完毕关闭连接
- **操作**：创建接口和实现类，支持MySQL/Oracle/PostgreSQL/SQLServer四种数据库，实现queryTableRowCount和queryFieldRowCount方法，包含SQL注入校验、查询超时设置、try-finally关闭连接
- **验收标准**：可正确连接目标数据库并返回COUNT查询结果，连接查询后必定关闭
- **涉及文件**：
  - `backend/src/main/java/com/dataasset/security/service/DynamicDatabaseQueryService.java`（新建）
  - `backend/src/main/java/com/dataasset/security/service/impl/DynamicDatabaseQueryServiceImpl.java`（新建）

#### 7.2 扩展DataAssetService实现单条刷新
- **描述**：在DataAssetService和DataAssetServiceImpl中新增refreshAssetRowCount方法，包含资产校验、查询执行、结果更新、并发控制
- **操作**：实现refreshAssetRowCount逻辑，使用ConcurrentHashMap实现并发控制，调用DynamicDatabaseQueryService执行查询，更新rowCount字段
- **验收标准**：REQ-BE-001所有验收标准满足，并发刷新返回"该资产正在刷新中"
- **涉及文件**：
  - `backend/src/main/java/com/dataasset/security/service/DataAssetService.java`
  - `backend/src/main/java/com/dataasset/security/service/impl/DataAssetServiceImpl.java`

#### 7.3 扩展DataFieldService实现单条刷新
- **描述**：在DataFieldService和DataFieldServiceImpl中新增refreshFieldRowCount方法，包含字段校验、所属资产校验、查询执行、结果更新、并发控制
- **操作**：实现refreshFieldRowCount逻辑，使用ConcurrentHashMap实现并发控制，调用DynamicDatabaseQueryService执行查询，更新rowCount字段
- **验收标准**：REQ-BE-002所有验收标准满足
- **涉及文件**：
  - `backend/src/main/java/com/dataasset/security/service/DataFieldService.java`
  - `backend/src/main/java/com/dataasset/security/service/impl/DataFieldServiceImpl.java`

#### 7.4 实现批量刷新Service方法
- **描述**：在DataAssetService中新增submitBatchRefreshTask和getBatchRefreshProgress方法，实现异步批量刷新和进度查询
- **操作**：创建BatchRefreshProgressVO和BatchRefreshResultItemVO，使用ConcurrentHashMap存储任务进度，CompletableFuture.runAsync异步执行逐条刷新，支持ASSET_ONLY和ASSET_AND_FIELD两种范围
- **验收标准**：REQ-BE-004和REQ-BE-005所有验收标准满足
- **涉及文件**：
  - `backend/src/main/java/com/dataasset/security/service/DataAssetService.java`
  - `backend/src/main/java/com/dataasset/security/service/impl/DataAssetServiceImpl.java`
  - `backend/src/main/java/com/dataasset/security/vo/BatchRefreshProgressVO.java`（新建）
  - `backend/src/main/java/com/dataasset/security/vo/BatchRefreshResultItemVO.java`（新建）

#### 7.5 扩展Controller接口
- **描述**：在DataAssetController中新增refreshAssetRowCount、batchRefreshRowCount、getBatchRefreshProgress接口；在DataFieldController中新增refreshFieldRowCount接口
- **操作**：按design.md 4.4节实现，添加@AuditLog注解，添加Swagger @Operation注解
- **验收标准**：所有接口可通过HTTP访问，审计日志正确记录
- **涉及文件**：
  - `backend/src/main/java/com/dataasset/security/controller/DataAssetController.java`
  - `backend/src/main/java/com/dataasset/security/controller/DataFieldController.java`

---

### 任务 8: 集成验证与收尾

**描述**：端到端验证所有功能，确保前后端联调正常，处理边界情况。

**输入**：spec.md全部验收标准
**输出**：功能完整可用，通过验收

#### 8.1 Mock后端端到端验证
- **描述**：启动Mock后端，验证所有新增接口的请求/响应正确性，验证批量刷新的进度轮询机制
- **操作**：使用curl或Postman测试所有新增接口，验证正常流程和异常流程（资产不存在、缺少连接信息等）
- **验收标准**：所有Mock接口返回符合接口契约定义，批量刷新进度轮询正常工作
- **涉及文件**：`simple-backend/server.js`

#### 8.2 前端页面联调验证
- **描述**：启动前端+Mock后端，验证资产清单页面、字段管理页面、批量刷新页面的完整交互流程
- **操作**：验证数据条数列展示、刷新按钮交互、批量刷新页面筛选/勾选/执行/进度/结果全流程
- **验收标准**：spec.md中REQ-FE-001~010所有验收标准在前端页面上均满足
- **涉及文件**：所有前端页面文件

---

## 3. 任务依赖关系

```
任务1 (数据库+实体变更)
  ↓
任务2 (Mock后端) ← 可与任务3并行
任务3 (前端API层) ← 可与任务2并行
  ↓
任务4 (资产清单页面) ← 依赖任务3
任务5 (字段管理页面) ← 依赖任务3
任务6 (批量刷新页面) ← 依赖任务3
  ↓
任务7 (后端Service+Controller) ← 依赖任务1
  ↓
任务8 (集成验证) ← 依赖任务2~7全部完成
```

## 4. 优先级与建议执行顺序

| 执行顺序 | 任务 | 优先级 | 理由 |
|---------|------|--------|------|
| 1 | 任务1 | P0 | 基础数据模型变更，所有后续任务依赖 |
| 2 | 任务2 | P0 | Mock后端先行，前端开发即可联调 |
| 3 | 任务3 | P0 | 前端API层是页面开发的前提 |
| 4 | 任务4 | P1 | 资产清单是核心页面，优先实现 |
| 5 | 任务5 | P1 | 字段管理页面改造 |
| 6 | 任务6 | P1 | 批量刷新页面开发 |
| 7 | 任务7 | P2 | 后端真实实现，Mock可用后可后续跟进 |
| 8 | 任务8 | P0 | 集成验证确保质量 |

**建议**：任务2和任务3可并行执行；任务4、5、6可并行执行；任务7可在前端页面开发完成后实施。
