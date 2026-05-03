# 资产与字段刷新数据条数功能 - 技术设计文档

## 1. 设计概述

### 1.1 设计目标
为"资产清单"、"字段管理"和"批量刷新"三个页面提供数据条数查询能力，包括：
- 资产级：查询数据表记录总条数（`SELECT COUNT(*) FROM tableName`）
- 字段级：查询字段非空值条数（`SELECT COUNT(fieldCode) FROM tableName`）
- 批量级：按数据库维度批量选择资产，异步逐条查询数据条数并更新，支持进度查询和结果汇总

### 1.2 设计原则
- **类型安全**：前后端均使用强类型定义，TypeScript禁止`any`，Java使用明确类型
- **连接安全**：动态数据库连接使用短连接，查询完毕立即关闭，使用只读账号
- **关注点分离**：数据库连接查询逻辑抽取为独立Service，Controller仅做参数校验和结果封装
- **渐进式实现**：Mock后端先行，后端真实实现后续跟进

## 2. 系统架构

### 2.1 整体架构图

```
┌──────────────────────────────────────────────────────────────────────────┐
│                           前端 (Vue3 + TS)                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐      │
│  │ 资产清单页面  │  │ 字段管理页面  │  │ 批量刷新页面（新增）      │      │
│  │ - 数据条数列  │  │ - 数据条数列  │  │ - 数据库筛选 + 资产勾选  │      │
│  │ - 刷新按钮    │  │ - 刷新按钮    │  │ - 刷新范围选择           │      │
│  │ - 批量刷新按钮│  │              │  │ - 开始刷新 + 进度展示    │      │
│  └───────┬──────┘  └───────┬──────┘  │ - 结果汇总               │      │
│          │                 │          └────────────┬─────────────┘      │
│  ┌───────▼─────────────────▼───────────────────────▼──────────────┐    │
│  │               API 层 (frontend/src/api/index.ts)               │    │
│  │  dataAssetApi.refreshRowCount(assetId)                        │    │
│  │  dataAssetApi.batchRefreshRowCount(assetIds, refreshScope)    │    │
│  │  dataAssetApi.getBatchRefreshProgress(taskId)                 │    │
│  │  assetFieldApi.refreshRowCount(fieldId)                       │    │
│  └───────┬─────────────────┬───────────────────────┬─────────────┘    │
└──────────┼─────────────────┼───────────────────────┼──────────────────┘
           │                 │                       │
           ▼                 ▼                       ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                     后端 (Spring Boot 3.2.3)                              │
│  ┌──────────────────┐  ┌──────────────────┐                             │
│  │DataAssetController│  │DataFieldController│                            │
│  │POST /asset/       │  │POST /asset/field/│                            │
│  │refresh-row-count  │  │refresh-row-count │                            │
│  │POST /asset/       │  └────────┬─────────┘                            │
│  │batch-refresh-row- │           │                                      │
│  │count              │           │                                      │
│  │GET /asset/        │           │                                      │
│  │batch-refresh-     │           │                                      │
│  │progress/{taskId}  │           │                                      │
│  └────────┬─────────┘           │                                      │
│           │                     │                                      │
│  ┌────────▼─────────┐  ┌───────▼─────────┐                            │
│  │ DataAssetService  │  │ DataFieldService │                            │
│  │ refreshRowCount() │  │ refreshRowCount()│                            │
│  │ batchRefresh()    │  │                 │                            │
│  └────────┬─────────┘  └───────┬─────────┘                            │
│           │                     │                                      │
│  ┌────────▼─────────────────────▼─────────┐                           │
│  │       DynamicDatabaseQueryService       │                           │
│  │  - 建立动态数据库连接                    │                           │
│  │  - 执行COUNT查询                        │                           │
│  │  - 关闭连接                             │                           │
│  └─────────────────────────────────────────┘                           │
└──────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Mock后端架构

```
┌─────────────────────────────────────────────────┐
│           Mock后端 (Node.js Express)             │
│                                                  │
│  POST /api/asset/refresh-row-count/:assetId     │
│    → 模拟返回 rowCount (1000~100000)            │
│    → 更新 mockAssets[assetId].rowCount           │
│                                                  │
│  POST /api/asset/field/refresh-row-count/:fieldId│
│    → 模拟返回 rowCount (500~80000)              │
│    → 更新 mockFields[fieldId].rowCount           │
│                                                  │
│  mockAssets / mockFields 补充 rowCount 字段      │
└─────────────────────────────────────────────────┘
```

## 3. 数据模型设计

### 3.1 数据库表变更

#### data_asset 表新增字段
```sql
ALTER TABLE data_asset ADD COLUMN row_count BIGINT DEFAULT NULL COMMENT '数据条数，记录该资产对应数据表的记录总行数';
```

#### data_field 表新增字段
```sql
ALTER TABLE data_field ADD COLUMN row_count BIGINT DEFAULT NULL COMMENT '数据条数，记录该字段在对应数据表中非空值的记录条数';
```

### 3.2 后端实体类变更

#### DataAsset.java 新增属性
```java
/**
 * 数据条数（表记录总行数）
 */
private Long rowCount;
```

#### DataAssetVO.java 新增属性
```java
@Schema(description = "数据条数（表记录总行数）")
private Long rowCount;
```

#### DataField.java 新增属性
```java
/**
 * 数据条数（字段非空值条数）
 */
private Long rowCount;
```

#### DataFieldVO.java 新增属性
```java
@Schema(description = "数据条数（字段非空值条数）")
private Long rowCount;
```

### 3.3 前端接口变更

#### DataAsset 接口 (frontend/src/api/index.ts)
```typescript
export interface DataAsset {
  // ... 现有字段
  rowCount: number | null  // 数据条数（表记录总行数）
}
```

#### AssetField 接口 (frontend/src/api/index.ts)
```typescript
export interface AssetField {
  // ... 现有字段
  rowCount: number | null  // 数据条数（字段非空值条数）
}
```

## 4. 后端详细设计

### 4.1 新增 DynamicDatabaseQueryService

**包路径**：`com.dataasset.security.service.DynamicDatabaseQueryService`

**职责**：根据数据库连接信息动态建立连接，执行COUNT查询，返回结果后立即关闭连接。

**接口定义**：
```java
public interface DynamicDatabaseQueryService {

    /**
     * 查询数据表记录总条数
     *
     * @param databaseType 数据库类型 (MYSQL/ORACLE/POSTGRESQL/SQLSERVER)
     * @param host         数据库地址
     * @param port         数据库端口
     * @param databaseName 数据库名称
     * @param tableName    表名
     * @return 记录总条数
     */
    long queryTableRowCount(String databaseType, String host, Integer port,
                            String databaseName, String tableName);

    /**
     * 查询字段非空值记录条数
     *
     * @param databaseType 数据库类型
     * @param host         数据库地址
     * @param port         数据库端口
     * @param databaseName 数据库名称
     * @param tableName    表名
     * @param fieldCode    字段编码（列名）
     * @return 字段非空值条数
     */
    long queryFieldRowCount(String databaseType, String host, Integer port,
                            String databaseName, String tableName, String fieldCode);
}
```

**实现要点**：
- 使用JDBC `DriverManager`建立短连接，查询完毕后在`finally`块中关闭Connection
- 根据`databaseType`选择对应的JDBC驱动和URL模板：
  - MYSQL: `jdbc:mysql://{host}:{port}/{databaseName}?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true`
  - ORACLE: `jdbc:oracle:thin:@//{host}:{port}/{databaseName}`
  - POSTGRESQL: `jdbc:postgresql://{host}:{port}/{databaseName}`
  - SQLSERVER: `jdbc:sqlserver://{host}:{port};databaseName={databaseName}`
- 数据库连接凭证从配置或数据源配置表中获取（只读账号）
- 设置查询超时时间为30秒
- 对表名和字段名进行SQL注入校验（仅允许字母、数字、下划线）

### 4.2 DataAssetService 扩展

**新增方法**：
```java
/**
 * 刷新资产数据条数
 *
 * @param assetId 资产ID
 * @return 刷新结果（包含assetId和rowCount）
 */
RowCountRefreshResult refreshAssetRowCount(Long assetId);
```

**RowCountRefreshResult VO**：
```java
@Data
public class RowCountRefreshResult {
    private Long id;       // 资产ID或字段ID
    private Long rowCount; // 查询到的记录条数
}
```

**实现逻辑**：
1. 根据assetId查询DataAsset记录
2. 校验资产存在性，不存在则抛出`ResourceNotFoundException`
3. 校验数据库连接信息完整性，不完整则抛出`ValidationException`
4. 调用`DynamicDatabaseQueryService.queryTableRowCount()`执行查询
5. 更新DataAsset的rowCount字段和updatedTime
6. 返回`RowCountRefreshResult`

**批量刷新方法**：
```java
/**
 * 提交批量刷新任务（异步）
 *
 * @param assetIds     资产ID列表
 * @param refreshScope 刷新范围：ASSET_ONLY / ASSET_AND_FIELD
 * @return 任务ID
 */
String submitBatchRefreshTask(List<Long> assetIds, String refreshScope);

/**
 * 查询批量刷新进度
 *
 * @param taskId 任务ID
 * @return 进度信息
 */
BatchRefreshProgressVO getBatchRefreshProgress(String taskId);
```

**BatchRefreshProgressVO**：
```java
@Data
public class BatchRefreshProgressVO {
    private String taskId;
    private String status;          // RUNNING / COMPLETED / FAILED
    private int completedCount;
    private int totalCount;
    private String currentAssetName;
    private List<BatchRefreshResultItemVO> results;
}

@Data
public class BatchRefreshResultItemVO {
    private Long assetId;
    private String assetName;
    private String status;          // SUCCESS / FAILED
    private Long rowCount;
    private String errorMessage;
}
```

**批量刷新实现逻辑**：
1. 生成唯一taskId（UUID）
2. 创建`BatchRefreshProgressVO`对象，存入`ConcurrentHashMap<String, BatchRefreshProgressVO>`
3. 使用`@Async`或`CompletableFuture.runAsync()`在后台线程中逐条执行：
   - 遍历assetIds，对每个assetId调用`refreshAssetRowCount()`
   - 若refreshScope为ASSET_AND_FIELD，则查询该资产下所有字段，逐条调用`refreshFieldRowCount()`
   - 每完成一条，更新progress对象的completedCount和currentAssetName
   - 查询失败时记录失败原因，不中断后续执行
4. 全部完成后，将progress状态更新为COMPLETED
5. `getBatchRefreshProgress()`方法直接从ConcurrentHashMap中读取并返回进度

### 4.3 DataFieldService 扩展

**新增方法**：
```java
/**
 * 刷新字段数据条数
 *
 * @param fieldId 字段ID
 * @return 刷新结果（包含fieldId和rowCount）
 */
RowCountRefreshResult refreshFieldRowCount(Long fieldId);
```

**实现逻辑**：
1. 根据fieldId查询DataField记录，获取fieldCode和assetId
2. 校验字段存在性，不存在则抛出`ResourceNotFoundException`
3. 根据assetId查询所属DataAsset记录
4. 校验所属资产存在性，不存在则抛出`ResourceNotFoundException`
5. 校验所属资产数据库连接信息完整性，不完整则抛出`ValidationException`
6. 调用`DynamicDatabaseQueryService.queryFieldRowCount()`执行查询
7. 更新DataField的rowCount字段和updatedTime
8. 返回`RowCountRefreshResult`

### 4.4 Controller 层扩展

#### DataAssetController 新增接口

```java
/**
 * 刷新资产数据条数
 */
@PostMapping("/refresh-row-count/{assetId}")
@Operation(summary = "刷新资产数据条数", description = "根据资产ID查询对应数据表的记录总条数")
@AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "刷新数据条数")
public Result<RowCountRefreshResult> refreshAssetRowCount(@PathVariable Long assetId) {
    RowCountRefreshResult result = dataAssetService.refreshAssetRowCount(assetId);
    return Result.success("数据条数刷新成功", result);
}

/**
 * 批量刷新数据条数（异步）
 */
@PostMapping("/batch-refresh-row-count")
@Operation(summary = "批量刷新数据条数", description = "批量查询数据资产的数据条数，异步执行")
@AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "批量刷新数据条数")
public Result<Map<String, String>> batchRefreshRowCount(@RequestBody Map<String, Object> params) {
    @SuppressWarnings("unchecked")
    List<Number> assetIds = (List<Number>) params.get("assetIds");
    String refreshScope = (String) params.getOrDefault("refreshScope", "ASSET_AND_FIELD");
    String taskId = dataAssetService.submitBatchRefreshTask(
        assetIds.stream().map(Number::longValue).collect(Collectors.toList()),
        refreshScope
    );
    Map<String, String> data = new HashMap<>();
    data.put("taskId", taskId);
    return Result.success("批量刷新任务已提交", data);
}

/**
 * 查询批量刷新进度
 */
@GetMapping("/batch-refresh-progress/{taskId}")
@Operation(summary = "查询批量刷新进度", description = "查询批量刷新任务的执行进度")
public Result<BatchRefreshProgressVO> getBatchRefreshProgress(@PathVariable String taskId) {
    BatchRefreshProgressVO progress = dataAssetService.getBatchRefreshProgress(taskId);
    return Result.success(progress);
}
```

#### DataFieldController 新增接口

```java
/**
 * 刷新字段数据条数
 */
@PostMapping("/refresh-row-count/{fieldId}")
@Operation(summary = "刷新字段数据条数", description = "根据字段ID查询该字段在对应数据表中非空值的记录条数")
@AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_FIELD, description = "刷新字段数据条数")
public Result<RowCountRefreshResult> refreshFieldRowCount(@PathVariable Long fieldId) {
    RowCountRefreshResult result = dataFieldService.refreshFieldRowCount(fieldId);
    return Result.success("字段数据条数刷新成功", result);
}
```

### 4.5 并发控制设计

使用`ConcurrentHashMap<Long, Long>`记录正在执行刷新操作的资产/字段ID及其开始时间戳：

```java
// DataAssetServiceImpl 中
private final ConcurrentHashMap<Long, Long> refreshingAssets = new ConcurrentHashMap<>();

public RowCountRefreshResult refreshAssetRowCount(Long assetId) {
    // 尝试放入时间戳，若已存在则说明正在刷新
    long now = System.currentTimeMillis();
    Long existing = refreshingAssets.putIfAbsent(assetId, now);
    if (existing != null) {
        throw new BusinessException("该资产正在刷新中，请稍后");
    }
    try {
        // 执行查询逻辑...
    } finally {
        refreshingAssets.remove(assetId);
    }
}
```

DataFieldServiceImpl同理使用`ConcurrentHashMap<Long, Long> refreshingFields`。

## 5. 前端详细设计

### 5.1 API 层扩展 (frontend/src/api/index.ts)

#### dataAssetApi 新增方法
```typescript
export interface BatchRefreshResultItem {
  assetId: number
  assetName: string
  status: 'SUCCESS' | 'FAILED'
  rowCount: number | null
  errorMessage: string | null
}

export interface BatchRefreshProgress {
  taskId: string
  status: 'RUNNING' | 'COMPLETED' | 'FAILED'
  completedCount: number
  totalCount: number
  currentAssetName: string
  results: BatchRefreshResultItem[]
}

export const dataAssetApi = {
  // ... 现有方法

  // 刷新资产数据条数
  refreshRowCount(assetId: number) {
    return http.post<{ assetId: number; rowCount: number }>(`/asset/refresh-row-count/${assetId}`)
  },

  // 批量刷新数据条数
  batchRefreshRowCount(assetIds: number[], refreshScope: 'ASSET_ONLY' | 'ASSET_AND_FIELD') {
    return http.post<{ taskId: string }>('/asset/batch-refresh-row-count', { assetIds, refreshScope })
  },

  // 查询批量刷新进度
  getBatchRefreshProgress(taskId: string) {
    return http.get<BatchRefreshProgress>(`/asset/batch-refresh-progress/${taskId}`)
  },
}
```

#### assetFieldApi 新增方法
```typescript
export const assetFieldApi = {
  // ... 现有方法

  // 刷新字段数据条数
  refreshRowCount(fieldId: number) {
    return http.post<{ fieldId: number; rowCount: number }>(`/asset/field/refresh-row-count/${fieldId}`)
  },
}
```

### 5.2 资产清单页面改造 (frontend/src/views/asset/index.vue)

#### 模板变更

1. **顶部操作区域**：在"批量导出"按钮后新增"批量刷新"按钮
```html
<el-button type="info" @click="handleBatchRefresh">
  <el-icon><RefreshRight /></el-icon>
  批量刷新
</el-button>
```

2. **表格新增"数据条数"列**：在"表名"列与"状态"列之间插入
```html
<el-table-column prop="rowCount" label="数据条数" width="120">
  <template #default="{ row }">
    <span v-if="refreshingAssetId === row.assetId">
      <el-icon class="is-loading"><Loading /></el-icon>
    </span>
    <span v-else-if="row.assetType !== 'DATABASE' && !row.databaseHost">不适用</span>
    <span v-else-if="row.rowCount != null">{{ row.rowCount.toLocaleString() }}</span>
    <span v-else>-</span>
  </template>
</el-table-column>
```

3. **操作列新增"刷新"按钮**：在"查看"按钮前插入
```html
<el-button
  link
  type="primary"
  @click="handleRefreshRowCount(row)"
  :loading="refreshingAssetId === row.assetId"
  :disabled="!canRefreshAsset(row)"
>
  刷新
</el-button>
```

#### 脚本变更

```typescript
import { RefreshRight, Loading } from '@element-plus/icons-vue'

// 正在刷新的资产ID
const refreshingAssetId = ref<number | null>(null)

// 判断资产是否可刷新
const canRefreshAsset = (row: DataAsset): boolean => {
  if (row.assetType !== 'DATABASE') return false
  if (!row.databaseHost || !row.databasePort || !row.databaseName || !row.tableName) return false
  return true
}

// 刷新数据条数
const handleRefreshRowCount = async (row: DataAsset) => {
  refreshingAssetId.value = row.assetId
  try {
    const res = await dataAssetApi.refreshRowCount(row.assetId)
    row.rowCount = res.data.rowCount
    ElMessage.success('数据条数刷新成功')
  } catch (error: any) {
    ElMessage.error(error.message || '数据条数刷新失败')
  } finally {
    refreshingAssetId.value = null
  }
}

// 批量刷新 - 跳转到批量刷新页面
const handleBatchRefresh = () => {
  router.push('/batch-refresh')
}
```

### 5.4 批量刷新页面（新增）(frontend/src/views/batch-refresh/index.vue)

#### 页面结构

```
┌─────────────────────────────────────────────────────────┐
│  批量刷新数据条数                                        │
├─────────────────────────────────────────────────────────┤
│  数据库筛选：                                           │
│  [数据库类型▼] [数据库地址] [端口] [数据库名称] [查询]   │
│                                                         │
│  刷新范围：○ 仅刷新资产数据条数  ● 同时刷新资产和字段    │
│                                                         │
│  ┌───┬────┬────┬────┬────┬──────┬────┐               │
│  │ ☑ │ID  │编码│名称│表名│数据条数│状态│               │
│  ├───┼────┼────┼────┼────┼──────┼────┤               │
│  │ ☑ │ 1  │... │... │... │52380 │启用│               │
│  │ ☐ │ 2  │... │... │... │128456│启用│               │
│  └───┴────┴────┴────┴────┴──────┴────┘               │
│                                                         │
│  [开始刷新]  已选 1 项                                   │
│                                                         │
│  ── 刷新进度 ──                                         │
│  ████████░░░░  3/5  正在刷新：订单数据表                 │
│                                                         │
│  ── 刷新结果 ──                                         │
│  成功：4  失败：1                                        │
│  失败资产：财务报表文件 - 非数据库类型资产                 │
│                                                         │
│  [返回资产列表]                                          │
└─────────────────────────────────────────────────────────┘
```

#### 核心脚本逻辑

```typescript
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { dataAssetApi } from '@/api'

const router = useRouter()

// 筛选条件
const filterForm = reactive({
  databaseType: '',
  databaseHost: '',
  databasePort: null as number | null,
  databaseName: '',
})

// 刷新范围
const refreshScope = ref<'ASSET_ONLY' | 'ASSET_AND_FIELD'>('ASSET_AND_FIELD')

// 资产列表与选择
const assetList = ref<DataAsset[]>([])
const selectedAssets = ref<DataAsset[]>([])
const loading = ref(false)

// 刷新状态
const isRefreshing = ref(false)
const taskId = ref<string | null>(null)
const progress = reactive({
  status: '' as 'RUNNING' | 'COMPLETED' | 'FAILED' | '',
  completedCount: 0,
  totalCount: 0,
  currentAssetName: '',
  results: [] as BatchRefreshResultItem[],
})

// 轮询定时器
let pollTimer: ReturnType<typeof setInterval> | null = null

// 查询资产列表
const handleQuery = async () => {
  loading.value = true
  try {
    const params: Record<string, string | number> = {
      pageNum: 1, pageSize: 1000, assetType: 'DATABASE'
    }
    if (filterForm.databaseType) params.databaseType = filterForm.databaseType
    if (filterForm.databaseHost) params.databaseHost = filterForm.databaseHost
    if (filterForm.databasePort) params.databasePort = filterForm.databasePort
    if (filterForm.databaseName) params.databaseName = filterForm.databaseName
    const res = await dataAssetApi.getList(params)
    assetList.value = res.data.list
  } finally {
    loading.value = false
  }
}

// 开始批量刷新
const handleStartRefresh = async () => {
  if (selectedAssets.value.length === 0) {
    ElMessage.warning('请至少选择一项资产')
    return
  }
  isRefreshing.value = true
  const assetIds = selectedAssets.value.map(a => a.assetId)
  try {
    const res = await dataAssetApi.batchRefreshRowCount(assetIds, refreshScope.value)
    taskId.value = res.data.taskId
    // 开始轮询进度
    startPolling()
  } catch (error: any) {
    ElMessage.error(error.message || '提交批量刷新任务失败')
    isRefreshing.value = false
  }
}

// 轮询进度
const startPolling = () => {
  pollTimer = setInterval(async () => {
    if (!taskId.value) return
    try {
      const res = await dataAssetApi.getBatchRefreshProgress(taskId.value)
      const data = res.data
      progress.status = data.status
      progress.completedCount = data.completedCount
      progress.totalCount = data.totalCount
      progress.currentAssetName = data.currentAssetName || ''
      progress.results = data.results || []

      if (data.status === 'COMPLETED' || data.status === 'FAILED') {
        stopPolling()
        isRefreshing.value = false
        // 更新资产列表中的rowCount
        updateAssetListRowCount()
      }
    } catch (error) {
      console.error('轮询进度失败:', error)
    }
  }, 2000) // 每2秒轮询一次
}

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

// 更新资产列表中的rowCount值
const updateAssetListRowCount = () => {
  progress.results.forEach(result => {
    const asset = assetList.value.find(a => a.assetId === result.assetId)
    if (asset && result.status === 'SUCCESS') {
      asset.rowCount = result.rowCount
    }
  })
}

// 返回资产列表
const handleBack = () => {
  router.push('/asset')
}

onUnmounted(() => {
  stopPolling()
})
```

#### 路由注册 (frontend/src/router/index.ts)

```typescript
{
  path: 'batch-refresh',
  name: 'BatchRefresh',
  component: () => import('@/views/batch-refresh/index.vue'),
  meta: { title: '批量刷新', requiresAuth: true },
},
```

### 5.3 字段管理页面改造 (frontend/src/views/asset-field/index.vue)

#### 模板变更

1. **表格新增"数据条数"列**：在"字段编码"列与"字段类型"列之间插入
```html
<el-table-column prop="rowCount" label="数据条数" width="120">
  <template #default="{ row }">
    <span v-if="refreshingFieldId === row.fieldId">
      <el-icon class="is-loading"><Loading /></el-icon>
    </span>
    <span v-else-if="!currentAssetCanRefresh">不适用</span>
    <span v-else-if="row.rowCount != null">{{ row.rowCount.toLocaleString() }}</span>
    <span v-else>-</span>
  </template>
</el-table-column>
```

2. **操作列新增"刷新"按钮**：在"编辑"按钮前插入
```html
<el-button
  type="primary"
  link
  size="small"
  @click="handleRefreshFieldRowCount(row)"
  :loading="refreshingFieldId === row.fieldId"
  :disabled="!currentAssetCanRefresh"
>
  <el-icon><Refresh /></el-icon> 刷新
</el-button>
```

#### 脚本变更

```typescript
import { Loading } from '@element-plus/icons-vue'

// 正在刷新的字段ID
const refreshingFieldId = ref<number | null>(null)

// 当前选中资产是否可刷新（需查询资产的数据库连接信息）
const currentAssetInfo = ref<DataAsset | null>(null)
const currentAssetCanRefresh = computed(() => {
  const asset = currentAssetInfo.value
  if (!asset) return false
  if (asset.assetType !== 'DATABASE') return false
  if (!asset.databaseHost || !asset.databasePort || !asset.databaseName || !asset.tableName) return false
  return true
})

// 选择资产时，同步获取资产详情（含数据库连接信息）
const onAssetChange = async (assetId: number) => {
  if (assetId) {
    const res = await dataAssetApi.getDetail(assetId)
    currentAssetInfo.value = res.data
  } else {
    currentAssetInfo.value = null
  }
}

// 刷新字段数据条数
const handleRefreshFieldRowCount = async (row: any) => {
  refreshingFieldId.value = row.fieldId
  try {
    const res = await assetFieldApi.refreshRowCount(row.fieldId)
    row.rowCount = res.data.rowCount
    ElMessage.success('字段数据条数刷新成功')
  } catch (error: any) {
    ElMessage.error(error.message || '字段数据条数刷新失败')
  } finally {
    refreshingFieldId.value = null
  }
}
```

## 6. Mock后端详细设计

### 6.1 mockAssets 数据补充 rowCount 字段

```javascript
const mockAssets = [
  { assetId: 1, ..., rowCount: 52380 },
  { assetId: 2, ..., rowCount: 128456 },
  { assetId: 3, ..., rowCount: null },  // 非数据库类型
  { assetId: 4, ..., rowCount: null },  // 非数据库类型
];
```

### 6.2 mockFields 数据补充 rowCount 字段

```javascript
const mockFields = [
  { fieldId: 1, ..., rowCount: 52380 },
  { fieldId: 2, ..., rowCount: 52100 },
  { fieldId: 3, ..., rowCount: 48750 },
];
```

### 6.3 新增资产数据条数查询接口

```javascript
// 资产数据条数查询
app.post('/api/asset/refresh-row-count/:assetId', (req, res) => {
  const { assetId } = req.params;
  const asset = mockAssets.find(a => a.assetId === parseInt(assetId));

  if (!asset) {
    return res.json({ code: 404, message: '资产不存在' });
  }

  if (!asset.databaseHost || !asset.databaseName || !asset.tableName) {
    return res.json({ code: 400, message: '资产缺少数据库连接信息，无法查询数据条数' });
  }

  // 模拟查询结果
  const rowCount = Math.floor(Math.random() * 99000) + 1000; // 1000~100000
  asset.rowCount = rowCount;

  addAuditLog({
    operationType: 'QUERY',
    module: '数据资产管理',
    objectType: '数据资产',
    objectId: parseInt(assetId),
    objectName: asset.assetName,
    description: `刷新数据条数：${asset.assetName}`,
    result: 'SUCCESS',
    url: `/api/asset/refresh-row-count/${assetId}`,
    method: 'POST'
  });

  res.json({
    code: 0,
    message: '数据条数刷新成功',
    data: { assetId: parseInt(assetId), rowCount }
  });
});
```

### 6.4 新增字段数据条数查询接口

```javascript
// 字段数据条数查询
app.post('/api/asset/field/refresh-row-count/:fieldId', (req, res) => {
  const { fieldId } = req.params;
  const field = mockFields.find(f => f.fieldId === parseInt(fieldId));

  if (!field) {
    return res.json({ code: 404, message: '字段不存在' });
  }

  const asset = mockAssets.find(a => a.assetId === field.assetId);
  if (!asset) {
    return res.json({ code: 404, message: '字段所属资产不存在' });
  }

  if (!asset.databaseHost || !asset.databaseName || !asset.tableName) {
    return res.json({ code: 400, message: '所属资产缺少数据库连接信息，无法查询字段数据条数' });
  }

  // 模拟查询结果（字段非空值条数 <= 资产总条数）
  const maxRowCount = asset.rowCount || 80000;
  const rowCount = Math.floor(Math.random() * Math.min(maxRowCount, 79500)) + 500; // 500~min(maxRowCount,80000)
  field.rowCount = rowCount;

  addAuditLog({
    operationType: 'QUERY',
    module: '字段管理',
    objectType: '数据字段',
    objectId: parseInt(fieldId),
    objectName: field.fieldName,
    description: `刷新字段数据条数：${field.fieldName}`,
    result: 'SUCCESS',
    url: `/api/asset/field/refresh-row-count/${fieldId}`,
    method: 'POST'
  });

  res.json({
    code: 0,
    message: '字段数据条数刷新成功',
    data: { fieldId: parseInt(fieldId), rowCount }
  });
});
```

### 6.5 资产分页接口补充 rowCount

在现有的 `POST /api/asset/page` 接口中，确保返回的资产对象包含 `rowCount` 字段（mockAssets数据已补充该字段，无需额外处理）。

### 6.6 字段列表接口补充 rowCount

在现有的 `GET /api/asset/fields/:assetId` 接口中，确保返回的字段对象包含 `rowCount` 字段（mockFields数据已补充该字段，无需额外处理）。

### 6.7 新增批量刷新接口

```javascript
// 批量刷新任务存储
const batchRefreshTasks = {};

// 批量刷新数据条数
app.post('/api/asset/batch-refresh-row-count', (req, res) => {
  const { assetIds, refreshScope = 'ASSET_AND_FIELD' } = req.body;

  if (!assetIds || assetIds.length === 0) {
    return res.json({ code: 400, message: '请选择至少一项资产' });
  }

  const taskId = 'task_' + Date.now();

  // 初始化任务进度
  batchRefreshTasks[taskId] = {
    taskId,
    status: 'RUNNING',
    completedCount: 0,
    totalCount: assetIds.length,
    currentAssetName: '',
    results: []
  };

  // 模拟异步逐条处理
  let index = 0;
  const processNext = () => {
    if (index >= assetIds.length) {
      batchRefreshTasks[taskId].status = 'COMPLETED';
      batchRefreshTasks[taskId].currentAssetName = '';
      return;
    }

    const assetId = assetIds[index];
    const asset = mockAssets.find(a => a.assetId === assetId);

    if (asset) {
      batchRefreshTasks[taskId].currentAssetName = asset.assetName;
      const rowCount = Math.floor(Math.random() * 99000) + 1000;
      asset.rowCount = rowCount;

      // 如果刷新范围包含字段，也模拟更新字段
      if (refreshScope === 'ASSET_AND_FIELD') {
        const fields = mockFields.filter(f => f.assetId === assetId);
        fields.forEach(f => {
          f.rowCount = Math.floor(Math.random() * Math.min(rowCount, 79500)) + 500;
        });
      }

      batchRefreshTasks[taskId].results.push({
        assetId,
        assetName: asset.assetName,
        status: 'SUCCESS',
        rowCount,
        errorMessage: null
      });
    } else {
      batchRefreshTasks[taskId].results.push({
        assetId,
        assetName: `资产${assetId}`,
        status: 'FAILED',
        rowCount: null,
        errorMessage: '资产不存在'
      });
    }

    batchRefreshTasks[taskId].completedCount = ++index;
    setTimeout(processNext, 200); // 每条间隔200ms
  };

  setTimeout(processNext, 100);

  addAuditLog({
    operationType: 'QUERY',
    module: '数据资产管理',
    objectType: '数据资产',
    description: `批量刷新数据条数（${assetIds.length}项）`,
    result: 'SUCCESS',
    url: '/api/asset/batch-refresh-row-count',
    method: 'POST'
  });

  res.json({
    code: 0,
    message: '批量刷新任务已提交',
    data: { taskId }
  });
});

// 查询批量刷新进度
app.get('/api/asset/batch-refresh-progress/:taskId', (req, res) => {
  const { taskId } = req.params;
  const task = batchRefreshTasks[taskId];

  if (!task) {
    return res.json({ code: 404, message: '任务不存在' });
  }

  res.json({
    code: 0,
    data: task
  });
});
```

## 7. 数据库迁移脚本

### 7.1 迁移SQL

```sql
-- 资产与字段刷新数据条数功能 - 数据库迁移脚本

-- data_asset 表新增 row_count 字段
ALTER TABLE data_asset ADD COLUMN row_count BIGINT DEFAULT NULL COMMENT '数据条数，记录该资产对应数据表的记录总行数';

-- data_field 表新增 row_count 字段
ALTER TABLE data_field ADD COLUMN row_count BIGINT DEFAULT NULL COMMENT '数据条数，记录该字段在对应数据表中非空值的记录条数';
```

## 8. 接口契约汇总

### 8.1 资产数据条数查询

| 项目 | 内容 |
|------|------|
| 路径 | `POST /api/asset/refresh-row-count/{assetId}` |
| 路径参数 | `assetId: Long` |
| 成功响应 | `{ code: 200, message: "数据条数刷新成功", data: { id: Long, rowCount: Long } }` |
| 400响应 | `{ code: 400, message: "资产缺少数据库连接信息，无法查询数据条数" }` |
| 404响应 | `{ code: 404, message: "资产不存在" }` |
| 500响应 | `{ code: 500, message: "数据库连接失败：{原因}" / "数据表不存在：{tableName}" }` |

### 8.2 字段数据条数查询

| 项目 | 内容 |
|------|------|
| 路径 | `POST /api/asset/field/refresh-row-count/{fieldId}` |
| 路径参数 | `fieldId: Long` |
| 成功响应 | `{ code: 200, message: "字段数据条数刷新成功", data: { id: Long, rowCount: Long } }` |
| 400响应 | `{ code: 400, message: "所属资产缺少数据库连接信息，无法查询字段数据条数" }` |
| 404响应 | `{ code: 404, message: "字段不存在" / "字段所属资产不存在" }` |
| 500响应 | `{ code: 500, message: "数据库连接失败：{原因}" / "数据表或字段不存在" }` |

### 8.3 批量刷新

| 项目 | 内容 |
|------|------|
| 路径 | `POST /api/asset/batch-refresh-row-count` |
| 请求体 | `{ assetIds: Long[], refreshScope: "ASSET_ONLY" \| "ASSET_AND_FIELD" }` |
| 成功响应 | `{ code: 200, message: "批量刷新任务已提交", data: { taskId: String } }` |

### 8.4 批量刷新进度查询

| 项目 | 内容 |
|------|------|
| 路径 | `GET /api/asset/batch-refresh-progress/{taskId}` |
| 路径参数 | `taskId: String` |
| 成功响应 | `{ code: 200, data: { taskId, status, completedCount, totalCount, currentAssetName, results } }` |
| 404响应 | `{ code: 404, message: "任务不存在" }` |

## 9. 错误处理设计

| 错误场景 | 错误码 | 错误信息 | 前端处理 |
|---------|--------|---------|---------|
| 资产不存在 | 404 | 资产不存在 | ElMessage.error提示 |
| 字段不存在 | 404 | 字段不存在 | ElMessage.error提示 |
| 数据库连接信息缺失 | 400 | 资产/字段缺少数据库连接信息 | 按钮disabled，不触发请求 |
| 数据库连接失败 | 500 | 数据库连接失败：{原因} | ElMessage.error提示 |
| 表/字段不存在 | 500 | 数据表(或字段)不存在 | ElMessage.error提示 |
| 并发刷新 | 409 | 该资产/字段正在刷新中 | ElMessage.warning提示 |
| 查询超时 | - | 查询超时，请稍后重试 | 前端30s超时取消请求 |

## 10. 安全设计

- **SQL注入防护**：对动态拼接的表名和字段名进行白名单校验（仅允许`[a-zA-Z0-9_]`），不直接拼接用户输入
- **连接凭证**：动态数据库连接使用只读账号，从配置中心或数据源配置表获取，不硬编码在代码中
- **连接生命周期**：使用try-finally确保Connection在查询完成后必定关闭
- **查询超时**：JDBC Statement设置queryTimeout为30秒
- **权限控制**：刷新接口受`@AuditLog`注解管控，记录操作审计日志
