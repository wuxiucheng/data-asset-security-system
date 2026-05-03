package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.DataClassificationCreateDTO;
import com.dataasset.security.dto.DataClassificationQueryDTO;
import com.dataasset.security.dto.DataClassificationUpdateDTO;
import com.dataasset.security.service.DataClassificationService;
import com.dataasset.security.vo.DataClassificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据分类管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/classification")
@RequiredArgsConstructor
@Tag(name = "数据分类管理", description = "数据分类CRUD相关接口")
public class DataClassificationController {

    private final DataClassificationService dataClassificationService;

    /**
     * 创建数据分类
     */
    @PostMapping
    @Operation(summary = "创建数据分类", description = "创建新的数据分类")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.DATA_CLASSIFICATION, description = "创建数据分类")
    public Result<Long> createDataClassification(@Valid @RequestBody DataClassificationCreateDTO createDTO) {
        Long classificationId = dataClassificationService.createDataClassification(createDTO);
        return Result.success("数据分类创建成功", classificationId);
    }

    /**
     * 更新数据分类
     */
    @PutMapping
    @Operation(summary = "更新数据分类", description = "更新数据分类信息")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DATA_CLASSIFICATION, description = "更新数据分类信息")
    public Result<Void> updateDataClassification(@Valid @RequestBody DataClassificationUpdateDTO updateDTO) {
        dataClassificationService.updateDataClassification(updateDTO);
        return Result.success("数据分类更新成功");
    }

    /**
     * 删除数据分类
     */
    @DeleteMapping("/{classificationId}")
    @Operation(summary = "删除数据分类", description = "删除指定的数据分类")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.DATA_CLASSIFICATION, description = "删除数据分类")
    public Result<Void> deleteDataClassification(@PathVariable Long classificationId) {
        dataClassificationService.deleteDataClassification(classificationId);
        return Result.success("数据分类删除成功");
    }

    /**
     * 分页查询数据分类
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询数据分类", description = "根据条件分页查询数据分类")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_CLASSIFICATION, description = "查询数据分类列表")
    public Result<Page<DataClassificationVO>> queryDataClassifications(@Valid @RequestBody DataClassificationQueryDTO queryDTO) {
        Page<DataClassificationVO> page = dataClassificationService.queryDataClassifications(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取数据分类详情
     */
    @GetMapping("/{classificationId}")
    @Operation(summary = "获取数据分类详情", description = "根据分类ID获取数据分类详细信息")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_CLASSIFICATION, description = "查询数据分类详情")
    public Result<DataClassificationVO> getDataClassificationDetail(@PathVariable Long classificationId) {
        DataClassificationVO classificationVO = dataClassificationService.getDataClassificationDetail(classificationId);
        return Result.success(classificationVO);
    }

    /**
     * 获取数据分类树
     */
    @GetMapping("/tree/{standardId}")
    @Operation(summary = "获取数据分类树", description = "根据标准ID获取数据分类树结构")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_CLASSIFICATION, description = "查询数据分类树")
    public Result<List<DataClassificationVO>> getDataClassificationTree(@PathVariable Long standardId) {
        List<DataClassificationVO> tree = dataClassificationService.getDataClassificationTree(standardId);
        return Result.success(tree);
    }

    /**
     * 更新数据分类状态
     */
    @PutMapping("/{classificationId}/status")
    @Operation(summary = "更新数据分类状态", description = "更新数据分类状态（启用/禁用）")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DATA_CLASSIFICATION, description = "更新数据分类状态")
    public Result<Void> updateDataClassificationStatus(@PathVariable Long classificationId, @RequestParam String status) {
        dataClassificationService.updateDataClassificationStatus(classificationId, status);
        return Result.success("数据分类状态更新成功");
    }

    /**
     * 根据标准ID查询数据分类列表
     */
    @GetMapping("/standard/{standardId}")
    @Operation(summary = "根据标准ID查询数据分类列表", description = "查询指定标准的所有数据分类")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_CLASSIFICATION, description = "查询标准数据分类")
    public Result<List<DataClassificationVO>> getDataClassificationsByStandardId(@PathVariable Long standardId) {
        List<DataClassificationVO> classifications = dataClassificationService.getDataClassificationsByStandardId(standardId);
        return Result.success(classifications);
    }

    // ========== 兼容前端路径的接口 ==========

    @PostMapping("/create")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "创建数据分类")
    public Result<Long> createDataClassificationAlias(@Valid @RequestBody DataClassificationCreateDTO createDTO) {
        return createDataClassification(createDTO);
    }

    @PutMapping("/update")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "更新数据分类")
    public Result<Void> updateDataClassificationAlias(@Valid @RequestBody DataClassificationUpdateDTO updateDTO) {
        return updateDataClassification(updateDTO);
    }

    @DeleteMapping("/delete/{id}")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "删除数据分类")
    public Result<Void> deleteAlias(@PathVariable Long id) {
        return deleteDataClassification(id);
    }
}