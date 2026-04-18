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

import java.util.List;

/**
 * 数据资产管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/data-assets")
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
}
