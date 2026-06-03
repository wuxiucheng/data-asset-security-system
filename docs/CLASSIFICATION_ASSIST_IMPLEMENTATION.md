# 分类分级辅助规则功能实现方案

## 已完成

### 1. 数据库表创建 ✅

已创建以下表：
- `classification_assist_rule` - 规则表
- `classification_assist_result` - 执行结果表
- `classification_assist_task` - 执行任务表

已预置6条规则：
- 手机号字段识别 → L3级
- 身份证号字段识别 → L4级
- 姓名字段识别 → L2级
- 邮箱字段识别 → L2级
- 地址字段识别 → L3级
- 银行卡号字段识别 → L4级

## 待实现

### 2. 后端实现

#### 2.1 实体类
- [x] ClassificationAssistRule.java
- [ ] ClassificationAssistResult.java
- [ ] ClassificationAssistTask.java

#### 2.2 Mapper
- [ ] ClassificationAssistRuleMapper.java
- [ ] ClassificationAssistResultMapper.java
- [ ] ClassificationAssistTaskMapper.java

#### 2.3 Service
- [ ] ClassificationAssistRuleService.java - 规则管理
- [ ] ClassificationAssistResultService.java - 结果管理
- [ ] ClassificationAssistTaskService.java - 任务管理和执行

#### 2.4 Controller
- [ ] ClassificationAssistRuleController.java - 规则CRUD
- [ ] ClassificationAssistResultController.java - 结果查询和审核
- [ ] ClassificationAssistTaskController.java - 任务创建和执行

### 3. 前端实现

#### 3.1 API接口
```typescript
// 规则管理
export const classificationAssistRuleApi = {
  getList: (params) => http.post('/classificationAssistRule/page', params),
  create: (data) => http.post('/classificationAssistRule', data),
  update: (data) => http.put('/classificationAssistRule', data),
  delete: (id) => http.delete(`/classificationAssistRule/${id}`),
  enable: (id) => http.put(`/classificationAssistRule/${id}/enable`),
  disable: (id) => http.put(`/classificationAssistRule/${id}/disable`),
}

// 任务管理
export const classificationAssistTaskApi = {
  getList: (params) => http.post('/classificationAssistTask/page', params),
  create: (data) => http.post('/classificationAssistTask', data),
  execute: (id) => http.post(`/classificationAssistTask/${id}/execute`),
  getStatus: (id) => http.get(`/classificationAssistTask/${id}/status`),
}

// 结果管理
export const classificationAssistResultApi = {
  getList: (params) => http.post('/classificationAssistResult/page', params),
  approve: (id, comment) => http.put(`/classificationAssistResult/${id}/approve`, { comment }),
  reject: (id, comment) => http.put(`/classificationAssistResult/${id}/reject`, { comment }),
  batchApprove: (ids) => http.put('/classificationAssistResult/batch-approve', { ids }),
}
```

#### 3.2 页面组件
- [ ] `views/classification-assist/rule/index.vue` - 规则管理页面
- [ ] `views/classification-assist/task/index.vue` - 任务管理页面
- [ ] `views/classification-assist/result/index.vue` - 结果审核页面

### 4. 核心功能实现

#### 4.1 规则匹配逻辑

```java
public boolean matchRule(ClassificationAssistRule rule, DataField field) {
    switch (rule.getRuleType()) {
        case "FIELD_NAME":
            // 字段名匹配（支持通配符）
            return matchFieldName(rule.getFieldNamePattern(), field.getFieldName());
        case "FIELD_PATTERN":
            // 字段值正则匹配
            return matchFieldPattern(rule.getFieldValuePattern(), field);
        case "FIELD_SAMPLE":
            // 样本匹配
            return matchSample(rule.getSampleMatchRule(), field);
        default:
            return false;
    }
}

private boolean matchFieldName(String pattern, String fieldName) {
    // 将通配符模式转换为正则表达式
    // *phone* -> .*phone.*
    String regex = pattern.replace("*", ".*");
    return Pattern.matches(regex, fieldName.toLowerCase());
}
```

#### 4.2 任务执行流程

```java
public void executeTask(Long taskId) {
    // 1. 获取任务和规则
    ClassificationAssistTask task = taskMapper.selectById(taskId);
    List<ClassificationAssistRule> rules = getRules(task.getRuleIds());

    // 2. 获取待扫描的资产
    List<DataAsset> assets = getAssets(task.getScopeType(), task.getScopeConfig());

    // 3. 遍历资产和字段
    for (DataAsset asset : assets) {
        List<DataField> fields = fieldMapper.selectByAssetId(asset.getAssetId());

        for (DataField field : fields) {
            for (ClassificationAssistRule rule : rules) {
                // 4. 匹配规则
                if (matchRule(rule, field)) {
                    // 5. 创建结果记录
                    createResult(rule, asset, field);
                }
            }
        }
    }

    // 6. 更新任务状态
    task.setStatus("COMPLETED");
    task.setEndTime(LocalDateTime.now());
    taskMapper.updateById(task);
}
```

### 5. 菜单配置

在系统菜单中添加：
```
数据资产管理
  └─ 分类分级辅助
      ├─ 规则管理
      ├─ 执行任务
      └─ 结果审核
```

## 使用流程

### 1. 配置规则
- 进入"规则管理"页面
- 查看预置规则或创建新规则
- 配置匹配条件和分级建议

### 2. 创建执行任务
- 进入"执行任务"页面
- 创建新任务
- 选择执行范围（全部资产/指定数据源/指定资产）
- 选择要执行的规则
- 执行任务

### 3. 审核结果
- 进入"结果审核"页面
- 查看匹配结果
- 审核分级建议（批准/拒绝）
- 批量审核

### 4. 应用分级
- 审核通过后，自动或手动应用分级建议
- 更新资产/字段的分级信息

## 定时任务配置

支持Cron表达式：
- 每天凌晨2点：`0 0 2 * * ?`
- 每周一凌晨2点：`0 0 2 ? * MON`
- 每月1日凌晨2点：`0 0 2 1 * ?`

## 扩展功能

### 1. 规则导入导出
- 支持JSON格式导入导出规则
- 方便规则共享和迁移

### 2. 规则测试
- 提供规则测试功能
- 输入字段名/值，测试是否匹配

### 3. 统计分析
- 规则匹配统计
- 分级分布统计
- 执行效率分析

### 4. 智能推荐
- 基于历史数据推荐规则
- 自动优化规则优先级
