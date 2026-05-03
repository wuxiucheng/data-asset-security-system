package com.dataasset.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.entity.DataField;
import com.dataasset.security.entity.DataAsset;
import com.dataasset.security.entity.DataSourceConfig;
import com.dataasset.security.mapper.DataFieldMapper;
import com.dataasset.security.mapper.DataAssetMapper;
import com.dataasset.security.mapper.DataSourceConfigMapper;
import com.dataasset.security.service.DynamicDatabaseQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 数据字段控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@RestController
@RequestMapping("/asset/field")
@RequiredArgsConstructor
@Tag(name = "字段管理", description = "数据字段管理")
public class DataFieldController {

    private final DataFieldMapper dataFieldMapper;
    private final DataAssetMapper dataAssetMapper;
    private final DataSourceConfigMapper dataSourceConfigMapper;
    private final DynamicDatabaseQueryService dynamicDatabaseQueryService;

    /**
     * 根据资产ID查询字段列表
     */
    @GetMapping("/{assetId}")
    @Operation(summary = "查询字段列表", description = "根据资产ID查询字段列表")
    public Result<List<DataField>> getFieldsByAssetId(@PathVariable Long assetId) {
        List<DataField> fields = dataFieldMapper.selectList(new LambdaQueryWrapper<DataField>()
            .eq(DataField::getAssetId, assetId)
            .eq(DataField::getDeleted, 0));
        return Result.success(fields);
    }

    /**
     * 下载字段导入模板
     */
    @GetMapping("/import-template")
    @Operation(summary = "下载字段导入模板", description = "下载字段批量导入模板")
    public Result<Map<String, Object>> getImportTemplate() {
        Map<String, Object> template = new HashMap<>();
        template.put("templateName", "字段批量导入模板.csv");
        template.put("columns", List.of(
            Map.of("name", "所属资产ID", "field", "assetId", "required", true, "example", "1"),
            Map.of("name", "字段名称", "field", "fieldName", "required", true, "example", "客户ID"),
            Map.of("name", "字段编码", "field", "fieldCode", "required", true, "example", "customer_id"),
            Map.of("name", "字段类型", "field", "fieldType", "required", true, "example", "BIGINT"),
            Map.of("name", "是否主键", "field", "isPrimaryKey", "required", false, "example", "1"),
            Map.of("name", "是否可空", "field", "isNullable", "required", false, "example", "0"),
            Map.of("name", "是否敏感", "field", "isSensitive", "required", false, "example", "1"),
            Map.of("name", "分类ID", "field", "classificationId", "required", false, "example", "1"),
            Map.of("name", "分级ID", "field", "gradingId", "required", false, "example", "1"),
            Map.of("name", "描述", "field", "description", "required", false, "example", "客户唯一标识")
        ));
        return Result.success(template);
    }

    /**
     * 批量导入字段
     */
    @PostMapping("/import")
    @Operation(summary = "批量导入字段", description = "批量导入数据字段")
    @AuditLog(operationType = OperationTypeEnum.IMPORT, objectType = ObjectTypeEnum.DATA_FIELD, description = "批量导入数据字段")
    public Result<Map<String, Object>> importFields(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "导入功能待完善，请使用单个创建");
        result.put("count", 0);
        return Result.success(result);
    }

    /**
     * 创建字段
     */
    @PostMapping
    @Operation(summary = "创建字段", description = "创建数据字段")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.DATA_FIELD, description = "创建数据字段")
    public Result<Long> createField(@RequestBody DataField field) {
        field.setDeleted(0);
        dataFieldMapper.insert(field);
        return Result.success(field.getFieldId());
    }

    /**
     * 更新字段
     */
    @PutMapping
    @Operation(summary = "更新字段", description = "更新数据字段")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DATA_FIELD, description = "更新数据字段")
    public Result<Void> updateField(@RequestBody DataField field) {
        dataFieldMapper.updateById(field);
        return Result.success();
    }

    /**
     * 删除字段
     */
    @DeleteMapping("/{fieldId}")
    @Operation(summary = "删除字段", description = "删除数据字段")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.DATA_FIELD, description = "删除数据字段")
    public Result<Void> deleteField(@PathVariable Long fieldId) {
        DataField field = new DataField();
        field.setFieldId(fieldId);
        field.setDeleted(1);
        dataFieldMapper.updateById(field);
        return Result.success();
    }

    /**
     * 批量更新字段
     */
    @PostMapping("/batchUpdate")
    @Operation(summary = "批量更新字段", description = "批量更新数据字段")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DATA_FIELD, description = "批量更新数据字段")
    public Result<Void> batchUpdateFields(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Number> fieldIds = (List<Number>) params.get("fieldIds");
        if (fieldIds == null || fieldIds.isEmpty()) {
            return Result.error("请选择要更新的字段");
        }
        for (Number fieldId : fieldIds) {
            DataField field = dataFieldMapper.selectById(fieldId.longValue());
            if (field != null) {
                // 更新传入的字段
                if (params.get("sensitiveDataType") != null) {
                    field.setSensitiveDataType((String) params.get("sensitiveDataType"));
                }
                if (params.get("classificationId") != null) {
                    field.setClassificationId(Long.valueOf(params.get("classificationId").toString()));
                }
                if (params.get("gradingId") != null) {
                    field.setGradingId(Long.valueOf(params.get("gradingId").toString()));
                }
                if (params.get("containsSensitiveData") != null) {
                    field.setContainsSensitiveData(Boolean.valueOf(params.get("containsSensitiveData").toString()));
                }
                if (params.get("nullable") != null) {
                    field.setNullable(Boolean.valueOf(params.get("nullable").toString()));
                }
                if (params.get("riskLevel") != null) {
                    field.setRiskLevel((String) params.get("riskLevel"));
                }
                dataFieldMapper.updateById(field);
            }
        }
        return Result.success();
    }

    /**
     * 创建字段（兼容前端/asset/field/create路径）
     */
    @PostMapping("/create")
    @Operation(summary = "创建字段", description = "创建数据字段")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.DATA_FIELD, description = "创建数据字段")
    public Result<Long> createFieldAlias(@RequestBody DataField field) {
        return createField(field);
    }

    /**
     * 更新字段（兼容前端/asset/field/update路径）
     */
    @PutMapping("/update")
    @Operation(summary = "更新字段", description = "更新数据字段")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DATA_FIELD, description = "更新数据字段")
    public Result<Void> updateFieldAlias(@RequestBody DataField field) {
        return updateField(field);
    }

    /**
     * 删除字段（兼容前端/asset/field/delete路径）
     */
    @DeleteMapping("/delete/{fieldId}")
    @Operation(summary = "删除字段", description = "删除数据字段")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.DATA_FIELD, description = "删除数据字段")
    public Result<Void> deleteFieldAlias(@PathVariable Long fieldId) {
        return deleteField(fieldId);
    }

    /**
     * 刷新字段数据条数
     */
    @PostMapping("/refresh-row-count/{fieldId}")
    @Operation(summary = "刷新字段数据条数", description = "根据字段ID查询该字段在对应数据表中非空值的记录条数")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_FIELD, description = "刷新字段数据条数")
    public Result<Map<String, Object>> refreshFieldRowCount(@PathVariable Long fieldId) {
        DataField field = dataFieldMapper.selectById(fieldId);
        if (field == null || field.getDeleted() == 1) {
            return Result.error(404, "字段不存在");
        }

        DataAsset asset = dataAssetMapper.selectById(field.getAssetId());
        if (asset == null || asset.getDeleted() == 1) {
            return Result.error(404, "字段所属资产不存在");
        }

        String dbType = asset.getDatabaseType();
        String host = asset.getDatabaseHost();
        int port = asset.getDatabasePort() != null ? asset.getDatabasePort() : 0;
        String dbName = asset.getDatabaseName();
        String username = null;
        String password = null;

        // 优先从数据源配置获取凭证
        if (asset.getDataSourceId() != null) {
            DataSourceConfig dsConfig = dataSourceConfigMapper.selectById(asset.getDataSourceId());
            if (dsConfig != null && "ACTIVE".equals(dsConfig.getStatus())) {
                dbType = dsConfig.getDatabaseType();
                host = dsConfig.getHost();
                port = dsConfig.getPort();
                dbName = dsConfig.getDatabaseName();
                username = dsConfig.getUsername();
                password = dsConfig.getPassword();
            }
        }

        if (host == null || port == 0 || dbName == null || asset.getTableName() == null) {
            return Result.error(400, "所属资产缺少数据库连接信息，无法查询字段数据条数");
        }

        long rowCount = dynamicDatabaseQueryService.queryFieldRowCount(
                dbType, host, port, dbName, username, password, asset.getTableName(), field.getFieldName()
        );

        // 更新字段的rowCount
        field.setRowCount(rowCount);
        dataFieldMapper.updateById(field);

        return Result.success("字段数据条数刷新成功", Map.of("fieldId", fieldId, "rowCount", rowCount));
    }
}
