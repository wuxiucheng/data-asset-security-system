package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.ClassificationStandardCreateDTO;
import com.dataasset.security.dto.ClassificationStandardQueryDTO;
import com.dataasset.security.dto.ClassificationStandardUpdateDTO;
import com.dataasset.security.service.ClassificationStandardService;
import com.dataasset.security.vo.ClassificationStandardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据分类标准管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/classificationStandard")
@RequiredArgsConstructor
@Tag(name = "数据分类标准管理", description = "数据分类标准CRUD相关接口")
public class ClassificationStandardController {

    private final ClassificationStandardService classificationStandardService;

    /**
     * 创建数据分类标准
     */
    @PostMapping
    @Operation(summary = "创建数据分类标准", description = "创建新的数据分类标准")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "创建数据分类标准")
    public Result<Long> createClassificationStandard(@Valid @RequestBody ClassificationStandardCreateDTO createDTO) {
        Long standardId = classificationStandardService.createClassificationStandard(createDTO);
        return Result.success("数据分类标准创建成功", standardId);
    }

    /**
     * 更新数据分类标准
     */
    @PutMapping
    @Operation(summary = "更新数据分类标准", description = "更新数据分类标准信息")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "更新数据分类标准信息")
    public Result<Void> updateClassificationStandard(@Valid @RequestBody ClassificationStandardUpdateDTO updateDTO) {
        classificationStandardService.updateClassificationStandard(updateDTO);
        return Result.success("数据分类标准更新成功");
    }

    /**
     * 删除数据分类标准
     */
    @DeleteMapping("/{standardId}")
    @Operation(summary = "删除数据分类标准", description = "删除指定的数据分类标准")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "删除数据分类标准")
    public Result<Void> deleteClassificationStandard(@PathVariable Long standardId) {
        classificationStandardService.deleteClassificationStandard(standardId);
        return Result.success("数据分类标准删除成功");
    }

    /**
     * 分页查询数据分类标准
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询数据分类标准", description = "根据条件分页查询数据分类标准")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "查询数据分类标准列表")
    public Result<Page<ClassificationStandardVO>> queryClassificationStandards(@Valid @RequestBody ClassificationStandardQueryDTO queryDTO) {
        Page<ClassificationStandardVO> page = classificationStandardService.queryClassificationStandards(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取数据分类标准详情
     */
    @GetMapping("/{standardId}")
    @Operation(summary = "获取数据分类标准详情", description = "根据标准ID获取数据分类标准详细信息")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "查询数据分类标准详情")
    public Result<ClassificationStandardVO> getClassificationStandardDetail(@PathVariable Long standardId) {
        ClassificationStandardVO standardVO = classificationStandardService.getClassificationStandardDetail(standardId);
        return Result.success(standardVO);
    }

    /**
     * 发布数据分类标准
     */
    @PutMapping("/{standardId}/publish")
    @Operation(summary = "发布数据分类标准", description = "发布指定的数据分类标准")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "发布数据分类标准")
    public Result<Void> publishClassificationStandard(@PathVariable Long standardId) {
        classificationStandardService.publishClassificationStandard(standardId);
        return Result.success("数据分类标准发布成功");
    }

    /**
     * 归档数据分类标准
     */
    @PutMapping("/{standardId}/archive")
    @Operation(summary = "归档数据分类标准", description = "归档指定的数据分类标准")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "归档数据分类标准")
    public Result<Void> archiveClassificationStandard(@PathVariable Long standardId) {
        classificationStandardService.archiveClassificationStandard(standardId);
        return Result.success("数据分类标准归档成功");
    }

    /**
     * 获取所有发布的数据分类标准
     */
    @GetMapping("/published")
    @Operation(summary = "获取所有发布的数据分类标准", description = "获取所有已发布的数据分类标准")
    public Result<List<ClassificationStandardVO>> getPublishedClassificationStandards() {
        List<ClassificationStandardVO> standards = classificationStandardService.getPublishedClassificationStandards();
        return Result.success(standards);
    }

    // ========== 兼容前端路径的接口 ==========

    @PostMapping("/create")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "创建数据分类标准")
    public Result<Long> createClassificationStandardAlias(@Valid @RequestBody ClassificationStandardCreateDTO createDTO) {
        return createClassificationStandard(createDTO);
    }

    @PutMapping("/update")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "更新数据分类标准")
    public Result<Void> updateClassificationStandardAlias(@Valid @RequestBody ClassificationStandardUpdateDTO updateDTO) {
        return updateClassificationStandard(updateDTO);
    }

    @DeleteMapping("/delete/{id}")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.CLASSIFICATION_STANDARD, description = "删除数据分类标准")
    public Result<Void> deleteAlias(@PathVariable Long id) {
        return deleteClassificationStandard(id);
    }
}