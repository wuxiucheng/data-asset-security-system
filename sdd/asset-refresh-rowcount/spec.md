# 资产与字段刷新数据条数功能 - 需求规格文档

## 1. 概述

### 1.1 功能名称
资产与字段刷新数据条数

### 1.2 功能描述
在数据资产安全及分类分级管理系统中，为"资产清单"、"字段管理"和"批量刷新"三个页面新增数据条数查询能力：

1. **资产清单页面**：在表格中新增"数据条数"列，在操作列新增"刷新"按钮。点击"刷新"按钮后，根据该资产对应的数据库连接信息（databaseHost/databasePort/databaseName/tableName）实时查询该资产对应数据表的记录总条数，并更新展示。
2. **字段管理页面**：在字段表格中新增"数据条数"列，在操作列新增"刷新"按钮。点击"刷新"按钮后，根据该字段所属资产的数据库连接信息，查询该字段在对应数据表中非空值的记录条数（即 `SELECT COUNT(fieldName) FROM tableName`），并更新展示。
3. **批量刷新页面**（新增独立页面）：提供按数据库维度批量选择资产，一键批量查询数据条数并更新的功能。用户可选择某个数据库连接下的一组资产，系统在后台逐条查询各表的记录条数和字段非空值条数，查询完成后统一更新到资产和字段记录中，并展示刷新结果汇总。

### 1.3 系统范围
- **包含**：
  - 资产清单页面：单条刷新按钮、数据条数字段展示、跳转批量刷新页面入口
  - 字段管理页面：单条刷新按钮、数据条数（字段非空值条数）字段展示
  - 批量刷新页面（新增）：按数据库选择资产、批量执行数据条数查询、进度展示、结果汇总
  - 后端数据条数查询接口（资产级 + 字段级 + 批量级）
  - Mock后端接口适配
- **不包含**：自动定时刷新（后续可扩展）、数据条数变化告警、数据条数历史趋势记录

### 1.4 利益相关者
| 角色 | 关注点 |
|------|--------|
| 数据管理员 | 需要快速了解各资产数据表的实时数据量，以及各字段的非空值填充情况，辅助数据治理决策 |
| 系统管理员 | 需要监控资产数据量变化，评估存储和性能影响 |
| 审计人员 | 需要查看资产数据量及字段填充率信息作为审计参考 |

## 2. 需求领域

### 2.1 资产清单 - 前端交互需求

#### REQ-FE-001: 资产清单表格新增"数据条数"列
**描述**：在资产清单页面的数据表格中，新增"数据条数"列，用于展示该资产对应数据表的记录总条数。

**前置条件**：用户已登录系统并进入资产清单页面。

**验收标准**：
- While 资产清单页面已加载, the system shall display 一个"数据条数"列在表格中, 位于"表名"列与"状态"列之间
- When 资产的rowCount字段有值, the system shall display 该数值在"数据条数"列中
- When 资产的rowCount字段无值（null或未查询过）, the system shall display "-"在"数据条数"列中
- When 资产类型为非数据库类型（FILE/API/OTHER）且无databaseHost/databaseName/tableName信息, the system shall display "不适用"在"数据条数"列中

#### REQ-FE-002: 资产清单操作列新增"刷新"按钮
**描述**：在资产清单表格每行的操作列中，新增"刷新"按钮，用于触发该资产数据条数的实时查询。

**前置条件**：用户已登录系统并进入资产清单页面。

**验收标准**：
- While 资产清单页面已加载, the system shall display 一个"刷新"文字按钮在每行操作列中, 位于"查看"按钮之前
- When 资产类型为非数据库类型（FILE/API/OTHER）, the system shall disable 该行的"刷新"按钮并显示为灰色不可点击状态
- When 资产类型为数据库类型（DATABASE）但缺少必要的数据库连接信息（databaseHost/databasePort/databaseName/tableName任一为空）, the system shall disable 该行的"刷新"按钮并显示为灰色不可点击状态

#### REQ-FE-003: 点击资产"刷新"按钮触发数据条数查询
**描述**：用户点击某行的"刷新"按钮后，系统根据该资产的数据库连接信息查询对应数据表的记录总条数，并更新表格中该行的"数据条数"字段。

**前置条件**：用户已登录系统，资产清单页面已加载，该行资产具备完整的数据库连接信息。

**验收标准**：
- When 用户点击某行的"刷新"按钮, the system shall 调用后端资产数据条数查询接口，传入该资产的assetId
- While 数据条数查询请求进行中, the system shall display 该行"刷新"按钮为loading状态，且"数据条数"列显示加载指示器
- If 后端返回查询成功（包含rowCount数值）, the system shall 更新该行资产的"数据条数"字段为返回的rowCount值，并显示成功提示"数据条数刷新成功"
- If 后端返回查询失败（数据库连接失败、表不存在等）, the system shall 保持该行"数据条数"字段不变，并显示错误提示，提示内容包含失败原因
- If 请求超时（超过30秒未响应）, the system shall 取消该次请求，保持"数据条数"字段不变，并显示提示"查询超时，请稍后重试"

#### REQ-FE-004: 资产清单"批量刷新"按钮跳转
**描述**：在资产清单页面的顶部操作区域，新增"批量刷新"按钮，点击后跳转到批量刷新页面。

**前置条件**：用户已登录系统并进入资产清单页面。

**验收标准**：
- While 资产清单页面已加载, the system shall display 一个"批量刷新"按钮在顶部操作区域，位于"批量导出"按钮之后
- When 用户点击"批量刷新"按钮, the system shall 跳转到批量刷新页面（路由路径：/batch-refresh）
- The "批量刷新"按钮 shall 使用info类型样式，与现有按钮风格保持一致

### 2.3 批量刷新页面 - 前端交互需求

#### REQ-FE-008: 批量刷新页面 - 数据库选择与资产列表展示
**描述**：批量刷新页面提供按数据库维度筛选资产的能力。页面顶部展示数据库选择区域，选择数据库后展示该数据库下所有数据库类型资产列表，用户可勾选需要刷新的资产。

**前置条件**：用户已登录系统并进入批量刷新页面。

**验收标准**：
- While 批量刷新页面已加载, the system shall display 数据库选择区域，包含数据库类型、数据库地址、数据库端口、数据库名称四个筛选条件
- When 用户填写数据库筛选条件并点击"查询"按钮, the system shall 根据筛选条件查询匹配的数据库类型资产列表，展示在下方表格中
- The 资产列表表格 shall 包含以下列：选择框、资产ID、资产编码、资产名称、表名、当前数据条数、状态
- The 资产列表表格 shall 支持全选/取消全选操作
- When 未选择任何数据库筛选条件, the system shall 展示所有数据库类型（DATABASE）的资产列表

#### REQ-FE-009: 批量刷新页面 - 执行批量刷新
**描述**：用户勾选需要刷新的资产后，点击"开始刷新"按钮，系统在后台逐条查询各资产对应数据表的记录条数，以及各资产下字段的非空值条数，查询完成后统一更新并展示结果。

**前置条件**：用户已进入批量刷新页面，已勾选至少一条资产。

**验收标准**：
- When 用户勾选资产后点击"开始刷新"按钮, the system shall 调用后端批量刷新接口，传入选中的assetId列表
- While 批量刷新正在执行中, the system shall display 刷新进度信息，包含：已完成数量/总数量、当前正在刷新的资产名称、进度条
- While 批量刷新正在执行中, the system shall disable "开始刷新"按钮，防止重复提交
- When 批量刷新全部完成, the system shall display 刷新结果汇总，包含：成功数量、失败数量、失败资产列表（含失败原因）
- When 批量刷新全部完成, the system shall 更新资产列表表格中各资产的"当前数据条数"列的值
- When 用户点击"返回资产列表"按钮, the system shall 跳转回资产清单页面

#### REQ-FE-010: 批量刷新页面 - 刷新范围选择
**描述**：用户可选择批量刷新的范围：仅刷新资产数据条数，或同时刷新资产数据条数和字段数据条数。

**前置条件**：用户已进入批量刷新页面。

**验收标准**：
- The 批量刷新页面 shall display 一个"刷新范围"单选组，包含两个选项："仅刷新资产数据条数"和"同时刷新资产和字段数据条数"
- The "刷新范围"默认值 shall 为"同时刷新资产和字段数据条数"
- When 刷新范围选择"仅刷新资产数据条数", the system shall 仅查询并更新各资产表的COUNT(*)值
- When 刷新范围选择"同时刷新资产和字段数据条数", the system shall 查询各资产表的COUNT(*)值，并查询各资产下每个字段的COUNT(fieldCode)值

### 2.2 字段管理 - 前端交互需求

#### REQ-FE-005: 字段管理表格新增"数据条数"列
**描述**：在字段管理页面的数据表格中，新增"数据条数"列，用于展示该字段在对应数据表中非空值的记录条数。

**前置条件**：用户已登录系统并进入字段管理页面，且已选择所属资产。

**验收标准**：
- While 字段管理页面已加载且已选择所属资产, the system shall display 一个"数据条数"列在表格中, 位于"字段编码"列与"字段类型"列之间
- When 字段的rowCount字段有值, the system shall display 该数值在"数据条数"列中
- When 字段的rowCount字段无值（null或未查询过）, the system shall display "-"在"数据条数"列中
- When 所属资产为非数据库类型（FILE/API/OTHER）, the system shall display "不适用"在所有字段的"数据条数"列中

#### REQ-FE-006: 字段管理操作列新增"刷新"按钮
**描述**：在字段管理表格每行的操作列中，新增"刷新"按钮，用于触发该字段数据条数的实时查询。

**前置条件**：用户已登录系统并进入字段管理页面，且已选择所属资产。

**验收标准**：
- While 字段管理页面已加载且已选择所属资产, the system shall display 一个"刷新"文字按钮在每行操作列中, 位于"编辑"按钮之前
- When 所属资产为非数据库类型（FILE/API/OTHER）, the system shall disable 该行及所有行的"刷新"按钮并显示为灰色不可点击状态
- When 所属资产为数据库类型但缺少必要的数据库连接信息（databaseHost/databasePort/databaseName/tableName任一为空）, the system shall disable 该行的"刷新"按钮并显示为灰色不可点击状态

#### REQ-FE-007: 点击字段"刷新"按钮触发数据条数查询
**描述**：用户点击某字段的"刷新"按钮后，系统根据该字段所属资产的数据库连接信息，查询该字段在对应数据表中非空值的记录条数（即 `COUNT(fieldCode)`），并更新表格中该行的"数据条数"字段。

**前置条件**：用户已登录系统，字段管理页面已加载，已选择所属资产且该资产具备完整的数据库连接信息。

**验收标准**：
- When 用户点击某字段的"刷新"按钮, the system shall 调用后端字段数据条数查询接口，传入该字段的fieldId
- While 字段数据条数查询请求进行中, the system shall display 该行"刷新"按钮为loading状态，且"数据条数"列显示加载指示器
- If 后端返回查询成功（包含rowCount数值）, the system shall 更新该行字段的"数据条数"字段为返回的rowCount值，并显示成功提示"字段数据条数刷新成功"
- If 后端返回查询失败（数据库连接失败、表不存在、字段不存在等）, the system shall 保持该行"数据条数"字段不变，并显示错误提示，提示内容包含失败原因
- If 请求超时（超过30秒未响应）, the system shall 取消该次请求，保持"数据条数"字段不变，并显示提示"查询超时，请稍后重试"

### 2.3 后端接口需求

#### REQ-BE-001: 资产数据条数查询接口
**描述**：后端提供根据资产ID查询对应数据表记录总条数的接口，通过资产的数据库连接信息动态连接目标数据库并执行COUNT查询。

**前置条件**：资产记录存在且包含完整的数据库连接信息。

**验收标准**：
- When 系统接收到资产数据条数查询请求（包含assetId）, the system shall 根据assetId查询资产记录，获取databaseType、databaseHost、databasePort、databaseName、tableName信息
- If 资产记录不存在, the system shall 返回错误响应，错误码为404，错误信息为"资产不存在"
- If 资产缺少必要的数据库连接信息（databaseHost、databasePort、databaseName、tableName任一为空）, the system shall 返回错误响应，错误码为400，错误信息为"资产缺少数据库连接信息，无法查询数据条数"
- When 数据库连接信息完整, the system shall 根据databaseType建立到目标数据库的连接，并执行"SELECT COUNT(*) FROM tableName"查询
- If 目标数据库连接成功且查询执行成功, the system shall 返回成功响应，包含assetId和rowCount（记录总条数）
- If 目标数据库连接失败, the system shall 返回错误响应，错误码为500，错误信息为"数据库连接失败：{具体原因}"
- If 目标数据库中表不存在, the system shall 返回错误响应，错误码为500，错误信息为"数据表不存在：{tableName}"
- When 查询成功后, the system shall 将rowCount值更新到资产记录的row_count字段中，并更新updated_time

#### REQ-BE-002: 字段数据条数查询接口
**描述**：后端提供根据字段ID查询该字段在对应数据表中非空值记录条数的接口。通过字段所属资产的数据库连接信息，动态连接目标数据库并执行 `SELECT COUNT(fieldCode) FROM tableName` 查询。

**前置条件**：字段记录存在，且其所属资产包含完整的数据库连接信息。

**验收标准**：
- When 系统接收到字段数据条数查询请求（包含fieldId）, the system shall 根据fieldId查询字段记录，获取fieldCode和assetId，再根据assetId查询所属资产的databaseType、databaseHost、databasePort、databaseName、tableName信息
- If 字段记录不存在, the system shall 返回错误响应，错误码为404，错误信息为"字段不存在"
- If 字段所属资产不存在, the system shall 返回错误响应，错误码为404，错误信息为"字段所属资产不存在"
- If 所属资产缺少必要的数据库连接信息, the system shall 返回错误响应，错误码为400，错误信息为"所属资产缺少数据库连接信息，无法查询字段数据条数"
- When 数据库连接信息完整, the system shall 根据databaseType建立到目标数据库的连接，并执行"SELECT COUNT(fieldCode) FROM tableName"查询（COUNT计数字段编码，排除NULL值）
- If 目标数据库连接成功且查询执行成功, the system shall 返回成功响应，包含fieldId和rowCount（字段非空值条数）
- If 目标数据库连接失败, the system shall 返回错误响应，错误码为500，错误信息为"数据库连接失败：{具体原因}"
- If 目标数据库中表或字段不存在, the system shall 返回错误响应，错误码为500，错误信息为"数据表或字段不存在"
- When 查询成功后, the system shall 将rowCount值更新到字段记录的row_count字段中，并更新updated_time

#### REQ-BE-003: 数据条数字段持久化
**描述**：在数据资产表（data_asset）和数据字段表（data_field）中分别新增row_count字段，用于持久化存储查询到的数据条数。

**前置条件**：数据库表data_asset和data_field已存在。

**验收标准**：
- The data_asset表 shall 新增一个row_count字段，类型为BIGINT，允许NULL，默认值为NULL
- The data_field表 shall 新增一个row_count字段，类型为BIGINT，允许NULL，默认值为NULL
- The data_asset.row_count字段 shall 在资产数据条数查询成功后被更新为查询到的记录总条数
- The data_field.row_count字段 shall 在字段数据条数查询成功后被更新为查询到的字段非空值条数
- The row_count字段 shall 在资产/字段创建时默认为NULL（未查询过）
- The DataAsset实体类 shall 新增rowCount属性，类型为Long
- The DataAssetVO类 shall 新增rowCount属性，类型为Long
- The DataField实体类 shall 新增rowCount属性，类型为Long
- The DataFieldVO类 shall 新增rowCount属性，类型为Long
- The 前端DataAsset接口 shall 新增rowCount属性，类型为number | null
- The 前端AssetField接口 shall 新增rowCount属性，类型为number | null

#### REQ-BE-004: 批量刷新数据条数接口
**描述**：后端提供批量刷新数据条数的接口，接收一组assetId列表，逐条查询各资产的数据条数（以及可选的字段数据条数），查询完成后统一更新并返回结果汇总。

**前置条件**：传入的assetId列表非空，且对应资产均包含完整的数据库连接信息。

**验收标准**：
- When 系统接收到批量刷新请求（包含assetIds数组和refreshScope枚举）, the system shall 逐条遍历assetIds，对每个assetId执行数据条数查询
- When refreshScope为"ASSET_ONLY", the system shall 仅查询各资产的表记录总条数（COUNT(*)）
- When refreshScope为"ASSET_AND_FIELD", the system shall 查询各资产的表记录总条数，并查询该资产下每个字段的非空值条数（COUNT(fieldCode)）
- While 某条资产查询失败, the system shall 记录该条失败原因，继续执行下一条，不中断整个批量任务
- When 全部查询完成后, the system shall 返回结果汇总，包含：成功数量、失败数量、每条资产的结果详情（assetId、资产名称、状态、rowCount、失败原因）
- The 批量刷新接口 shall 使用异步方式执行，避免长时间阻塞HTTP连接，接口立即返回一个任务ID，客户端通过轮询获取进度

#### REQ-BE-005: 批量刷新进度查询接口
**描述**：后端提供批量刷新任务进度查询接口，客户端通过轮询该接口获取实时进度。

**前置条件**：批量刷新任务已提交，拥有有效的taskId。

**验收标准**：
- When 系统接收到进度查询请求（包含taskId）, the system shall 返回当前任务进度，包含：任务状态（RUNNING/COMPLETED/FAILED）、已完成数量、总数量、当前正在处理的资产名称、已完成资产的结果列表
- When 任务状态为COMPLETED, the system shall 返回完整的结果汇总

#### REQ-BE-006: 数据条数查询审计日志
**描述**：资产和字段的数据条数查询操作均需记录审计日志，确保操作可追溯。

**前置条件**：审计日志功能已启用。

**验收标准**：
- When 资产数据条数查询接口被调用, the system shall 记录一条审计日志，操作类型为QUERY，对象类型为DATA_ASSET，描述包含"刷新数据条数"和资产名称
- When 字段数据条数查询接口被调用, the system shall 记录一条审计日志，操作类型为QUERY，对象类型为DATA_FIELD，描述包含"刷新字段数据条数"和字段名称
- If 查询失败, the system shall 在审计日志中记录失败原因

### 2.4 Mock后端适配需求

#### REQ-MOCK-001: Mock后端资产数据条数查询接口
**描述**：在Mock后端（simple-backend/server.js）中新增资产数据条数查询接口的模拟实现。

**前置条件**：Mock后端服务正在运行。

**验收标准**：
- The Mock后端 shall 提供POST /api/asset/refresh-row-count/:assetId接口
- When 接收到请求, the Mock后端 shall 返回模拟的rowCount值（随机生成1000-100000之间的整数）
- The Mock后端 shall 同时更新mockAssets中对应资产的rowCount字段
- The Mock后端 shall 在资产列表接口返回数据中包含rowCount字段

#### REQ-MOCK-002: Mock后端字段数据条数查询接口
**描述**：在Mock后端（simple-backend/server.js）中新增字段数据条数查询接口的模拟实现。

**前置条件**：Mock后端服务正在运行。

**验收标准**：
- The Mock后端 shall 提供POST /api/asset/field/refresh-row-count/:fieldId接口
- When 接收到请求, the Mock后端 shall 返回模拟的rowCount值（随机生成500-80000之间的整数，模拟字段非空值条数，应小于等于所属资产的rowCount）
- The Mock后端 shall 同时更新mockFields中对应字段的rowCount字段
- The Mock后端 shall 在字段列表接口返回数据中包含rowCount字段

#### REQ-MOCK-003: Mock后端批量刷新接口
**描述**：在Mock后端中新增批量刷新接口和进度查询接口的模拟实现。

**前置条件**：Mock后端服务正在运行。

**验收标准**：
- The Mock后端 shall 提供POST /api/asset/batch-refresh-row-count接口，接收{ assetIds: number[], refreshScope: string }，返回{ taskId: string }
- The Mock后端 shall 提供GET /api/asset/batch-refresh-progress/:taskId接口，返回任务进度
- When 批量刷新任务提交后, the Mock后端 shall 使用setTimeout模拟逐条处理，每条间隔200ms
- When 轮询进度接口, the Mock后端 shall 返回当前模拟进度（已完成数量/总数量、任务状态）

#### REQ-MOCK-004: Mock后端数据补充rowCount字段
**描述**：在Mock后端的模拟资产和字段数据中补充rowCount字段。

**验收标准**：
- The mockAssets数据中每个资产对象 shall 包含rowCount字段，初始值可为null或模拟数值
- The mockFields数据中每个字段对象 shall 包含rowCount字段，初始值可为null或模拟数值
- The 资产列表接口返回数据 shall 包含rowCount字段
- The 字段列表接口返回数据 shall 包含rowCount字段

### 2.5 非功能需求

#### REQ-NFR-001: 查询性能
**描述**：数据条数查询接口的响应时间应满足可接受范围。

**验收标准**：
- When 目标数据库表数据量在100万条以内, the system shall 在5秒内返回查询结果
- When 目标数据库表数据量超过100万条, the system shall 在10秒内返回查询结果

#### REQ-NFR-002: 数据库连接安全
**描述**：查询数据条数时建立的目标数据库连接应确保安全，防止连接泄露。

**验收标准**：
- When 数据条数查询完成（无论成功或失败）, the system shall 确保目标数据库连接已正确关闭
- The system shall 使用连接池或短连接方式，避免长期占用数据库连接资源
- The system shall 对目标数据库连接使用只读权限账号，防止通过该功能执行写操作

#### REQ-NFR-003: 并发控制
**描述**：同一资产或字段的刷新操作应进行并发控制，防止重复查询。

**验收标准**：
- While 某资产的刷新请求正在执行中, the system shall 拒绝对该资产的重复刷新请求，返回提示"该资产正在刷新中，请稍后"
- While 某字段的刷新请求正在执行中, the system shall 拒绝对该字段的重复刷新请求，返回提示"该字段正在刷新中，请稍后"
- The 前端 shall 在刷新请求期间禁用该行的"刷新"按钮，防止重复点击

## 3. 数据模型变更

### 3.1 data_asset表变更
| 变更类型 | 字段名 | 类型 | 允许NULL | 默认值 | 说明 |
|---------|--------|------|----------|--------|------|
| 新增 | row_count | BIGINT | YES | NULL | 数据条数，记录该资产对应数据表的记录总行数 |

### 3.2 data_field表变更
| 变更类型 | 字段名 | 类型 | 允许NULL | 默认值 | 说明 |
|---------|--------|------|----------|--------|------|
| 新增 | row_count | BIGINT | YES | NULL | 数据条数，记录该字段在对应数据表中非空值的记录条数 |

### 3.3 前端接口变更
| 所属接口 | 变更类型 | 属性名 | 类型 | 说明 |
|---------|---------|--------|------|------|
| DataAsset | 新增 | rowCount | number \| null | 资产数据条数（表记录总行数） |
| AssetField | 新增 | rowCount | number \| null | 字段数据条数（字段非空值条数） |

## 4. 接口设计概要

### 4.1 资产数据条数查询接口
- **路径**：POST /api/asset/refresh-row-count/{assetId}
- **请求参数**：assetId（路径参数）
- **成功响应**：`{ code: 0, message: "数据条数刷新成功", data: { assetId: number, rowCount: number } }`
- **失败响应**：`{ code: 400/404/500, message: "错误信息" }`

### 4.2 字段数据条数查询接口
- **路径**：POST /api/asset/field/refresh-row-count/{fieldId}
- **请求参数**：fieldId（路径参数）
- **成功响应**：`{ code: 0, message: "字段数据条数刷新成功", data: { fieldId: number, rowCount: number } }`
- **失败响应**：`{ code: 400/404/500, message: "错误信息" }`

### 4.3 批量刷新接口
- **路径**：POST /api/asset/batch-refresh-row-count
- **请求参数**：`{ assetIds: number[], refreshScope: "ASSET_ONLY" | "ASSET_AND_FIELD" }`
- **成功响应**：`{ code: 0, message: "批量刷新任务已提交", data: { taskId: string } }`

### 4.4 批量刷新进度查询接口
- **路径**：GET /api/asset/batch-refresh-progress/{taskId}
- **请求参数**：taskId（路径参数）
- **成功响应**：`{ code: 0, data: { taskId: string, status: "RUNNING"|"COMPLETED"|"FAILED", completedCount: number, totalCount: number, currentAssetName: string, results: [{ assetId: number, assetName: string, status: string, rowCount: number, errorMessage: string }] } }`

## 5. 约束与假设

### 5.1 约束
- 仅支持MySQL、Oracle、PostgreSQL、SQL Server四种数据库类型的数据条数查询
- 查询数据条数需要目标数据库的网络可达性和访问权限
- 非数据库类型资产（FILE/API/OTHER）不支持数据条数查询
- 字段数据条数查询的SQL为 `SELECT COUNT(fieldCode) FROM tableName`，统计的是该字段非NULL值的行数

### 5.2 假设
- 目标数据库的访问凭证已通过其他方式（如数据源配置）预先配置
- 执行COUNT查询的数据库账号具有目标表的SELECT权限
- 目标数据库的表名和字段编码在资产/字段记录中准确无误
- 字段编码（fieldCode）与目标数据库中的实际列名一致
