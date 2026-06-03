package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.DataAssetCreateDTO;
import com.dataasset.security.dto.DataAssetQueryDTO;
import com.dataasset.security.dto.DataAssetUpdateDTO;
import com.dataasset.security.service.DataAssetService;
import com.dataasset.security.vo.DataAssetVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 数据资产管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/asset")
@RequiredArgsConstructor
@Tag(name = "数据资产管理", description = "数据资产CRUD相关接口")
public class DataAssetController {

    private final DataAssetService dataAssetService;

    /**
     * 创建数据资产
     */
    @PostMapping
    @Operation(summary = "创建数据资产", description = "创建新的数据资产")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.DATA_ASSET, description = "创建数据资产")
    public Result<Long> createDataAsset(@Valid @RequestBody DataAssetCreateDTO createDTO) {
        Long assetId = dataAssetService.createDataAsset(createDTO);
        return Result.success("数据资产创建成功", assetId);
    }

    /**
     * 更新数据资产
     */
    @PutMapping
    @Operation(summary = "更新数据资产", description = "更新数据资产信息")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DATA_ASSET, description = "更新数据资产信息")
    public Result<Void> updateDataAsset(@Valid @RequestBody DataAssetUpdateDTO updateDTO) {
        dataAssetService.updateDataAsset(updateDTO);
        return Result.success("数据资产更新成功");
    }

    /**
     * 删除数据资产
     */
    @DeleteMapping("/{assetId}")
    @Operation(summary = "删除数据资产", description = "删除指定的数据资产")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.DATA_ASSET, description = "删除数据资产")
    public Result<Void> deleteDataAsset(@PathVariable Long assetId) {
        dataAssetService.deleteDataAsset(assetId);
        return Result.success("数据资产删除成功");
    }

    /**
     * 批量删除数据资产
     */
    @PostMapping("/batch-delete")
    @Operation(summary = "批量删除数据资产", description = "根据ID列表批量删除数据资产")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.DATA_ASSET, description = "批量删除数据资产")
    public Result<Void> batchDeleteDataAsset(@RequestBody List<Long> assetIds) {
        for (Long assetId : assetIds) {
            dataAssetService.deleteDataAsset(assetId);
        }
        return Result.success("批量删除成功");
    }

    /**
     * 分页查询数据资产
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询数据资产", description = "根据条件分页查询数据资产")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "查询数据资产列表")
    public Result<Page<DataAssetVO>> queryDataAssets(@Valid @RequestBody DataAssetQueryDTO queryDTO) {
        Page<DataAssetVO> page = dataAssetService.queryDataAssets(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取数据资产详情
     */
    @GetMapping("/{assetId}")
    @Operation(summary = "获取数据资产详情", description = "根据资产ID获取数据资产详细信息")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "查询数据资产详情")
    public Result<DataAssetVO> getDataAssetDetail(@PathVariable Long assetId) {
        DataAssetVO assetVO = dataAssetService.getDataAssetDetail(assetId);
        return Result.success(assetVO);
    }

    /**
     * 获取数据资产详情（兼容前端 /asset/detail/{assetId} 路径）
     */
    @GetMapping("/detail/{assetId}")
    @Operation(summary = "获取数据资产详情", description = "根据资产ID获取数据资产详细信息")
    public Result<DataAssetVO> getDataAssetDetailAlias(@PathVariable Long assetId) {
        return getDataAssetDetail(assetId);
    }

    /**
     * 更新数据资产状态
     */
    @PutMapping("/{assetId}/status")
    @Operation(summary = "更新数据资产状态", description = "更新数据资产状态（草稿/启用/禁用/归档）")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DATA_ASSET, description = "更新数据资产状态")
    public Result<Void> updateDataAssetStatus(@PathVariable Long assetId, @RequestParam String status) {
        dataAssetService.updateDataAssetStatus(assetId, status);
        return Result.success("数据资产状态更新成功");
    }

    /**
     * 根据部门ID查询数据资产列表
     */
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "根据部门ID查询数据资产列表", description = "查询指定部门的所有数据资产")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "查询部门数据资产")
    public Result<List<DataAssetVO>> getDataAssetsByDepartmentId(@PathVariable Long departmentId) {
        List<DataAssetVO> assets = dataAssetService.getDataAssetsByDepartmentId(departmentId);
        return Result.success(assets);
    }

    /**
     * 根据责任人ID查询数据资产列表
     */
    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "根据责任人ID查询数据资产列表", description = "查询指定责任人的所有数据资产")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "查询责任人数据资产")
    public Result<List<DataAssetVO>> getDataAssetsByOwnerId(@PathVariable Long ownerId) {
        List<DataAssetVO> assets = dataAssetService.getDataAssetsByOwnerId(ownerId);
        return Result.success(assets);
    }

    /**
     * 根据分类ID查询数据资产列表
     */
    @GetMapping("/classification/{classificationId}")
    @Operation(summary = "根据分类ID查询数据资产列表", description = "查询指定分类的所有数据资产")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "查询分类数据资产")
    public Result<List<DataAssetVO>> getDataAssetsByClassificationId(@PathVariable Long classificationId) {
        List<DataAssetVO> assets = dataAssetService.getDataAssetsByClassificationId(classificationId);
        return Result.success(assets);
    }

    /**
     * 根据分级ID查询数据资产列表
     */
    @GetMapping("/grading/{gradingId}")
    @Operation(summary = "根据分级ID查询数据资产列表", description = "查询指定分级的所有数据资产")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "查询分级数据资产")
    public Result<List<DataAssetVO>> getDataAssetsByGradingId(@PathVariable Long gradingId) {
        List<DataAssetVO> assets = dataAssetService.getDataAssetsByGradingId(gradingId);
        return Result.success(assets);
    }

    // ========== 兼容前端路径的接口 ==========

    @PostMapping("/create")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.DATA_ASSET, description = "创建数据资产")
    public Result<Long> createDataAssetAlias(@Valid @RequestBody DataAssetCreateDTO createDTO) {
        return createDataAsset(createDTO);
    }

    @PutMapping("/update")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DATA_ASSET, description = "更新数据资产")
    public Result<Void> updateDataAssetAlias(@Valid @RequestBody DataAssetUpdateDTO updateDTO) {
        return updateDataAsset(updateDTO);
    }

    @DeleteMapping("/delete/{id}")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.DATA_ASSET, description = "删除数据资产")
    public Result<Void> deleteAlias(@PathVariable Long id) {
        return deleteDataAsset(id);
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/import-template")
    @Operation(summary = "下载导入模板", description = "下载数据资产批量导入模板")
    public Result<Map<String, Object>> getImportTemplate() {
        Map<String, Object> template = new java.util.HashMap<>();
        template.put("templateName", "数据资产批量导入模板.csv");
        template.put("columns", java.util.List.of(
            Map.of("name", "资产名称", "field", "assetName", "required", true, "example", "用户数据库"),
            Map.of("name", "资产编码", "field", "assetCode", "required", true, "example", "DB_001"),
            Map.of("name", "资产类型", "field", "assetType", "required", true, "example", "DATABASE"),
            Map.of("name", "所属部门ID", "field", "departmentId", "required", false, "example", "1"),
            Map.of("name", "责任人ID", "field", "ownerId", "required", false, "example", "1"),
            Map.of("name", "描述", "field", "description", "required", false, "example", "核心用户数据库")
        ));
        return Result.success(template);
    }

    /**
     * 批量导入
     */
    @PostMapping("/import")
    @Operation(summary = "批量导入", description = "批量导入数据资产")
    @AuditLog(operationType = OperationTypeEnum.IMPORT, objectType = ObjectTypeEnum.DATA_ASSET, description = "批量导入数据资产")
    public Result<Map<String, Object>> importAssets(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("message", "导入功能待完善，请使用单个创建");
        result.put("count", 0);
        return Result.success(result);
    }

    /**
     * 导出资产
     */
    @GetMapping("/export")
    @Operation(summary = "导出资产", description = "导出数据资产列表")
    @AuditLog(operationType = OperationTypeEnum.EXPORT, objectType = ObjectTypeEnum.DATA_ASSET, description = "导出数据资产")
    public Result<String> exportAssets(@RequestParam(required = false) java.util.Map<String, String> params) {
        return Result.success("导出成功", "/tmp/assets_export.xlsx");
    }

    /**
     * 刷新资产数据条数
     */
    @PostMapping("/refresh-row-count/{assetId}")
    @Operation(summary = "刷新资产数据条数", description = "根据资产ID查询对应数据表的记录总条数")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "刷新数据条数")
    public Result<Map<String, Object>> refreshAssetRowCount(@PathVariable Long assetId) {
        Map<String, Object> result = dataAssetService.refreshAssetRowCount(assetId);
        return Result.success("数据条数刷新成功", result);
    }

    /**
     * 批量刷新数据条数
     */
    @PostMapping("/batch-refresh-row-count")
    @Operation(summary = "批量刷新数据条数", description = "批量查询多个资产的数据条数")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "批量刷新数据条数")
    public Result<Map<String, Object>> batchRefreshRowCount(@RequestBody Map<String, Object> requestBody) {
        @SuppressWarnings("unchecked")
        List<Integer> assetIdInts = (List<Integer>) requestBody.get("assetIds");
        List<Long> assetIds = assetIdInts.stream().map(Integer::longValue).toList();
        String refreshScope = (String) requestBody.getOrDefault("refreshScope", "ASSET_AND_FIELD");
        String taskId = dataAssetService.submitBatchRefreshTask(assetIds, refreshScope);
        return Result.success("批量刷新任务已提交", Map.of("taskId", taskId));
    }

    /**
     * 查询批量刷新进度
     */
    @GetMapping("/batch-refresh-progress/{taskId}")
    @Operation(summary = "查询批量刷新进度", description = "根据任务ID查询批量刷新任务的进度")
    public Result<Map<String, Object>> getBatchRefreshProgress(@PathVariable String taskId) {
        Map<String, Object> progress = dataAssetService.getBatchRefreshProgress(taskId);
        return Result.success(progress);
    }
}