package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.GradingStandardCreateDTO;
import com.dataasset.security.dto.GradingStandardQueryDTO;
import com.dataasset.security.dto.GradingStandardUpdateDTO;
import com.dataasset.security.service.GradingStandardService;
import com.dataasset.security.vo.GradingStandardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据分级标准管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/grading-standards")
@RequiredArgsConstructor
@Tag(name = "数据分级标准管理", description = "数据分级标准CRUD相关接口")
public class GradingStandardController {

    private final GradingStandardService gradingStandardService;

    /**
     * 创建数据分级标准
     */
    @PostMapping
    @Operation(summary = "创建数据分级标准", description = "创建新的数据分级标准")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.GRADING_STANDARD, description = "创建数据分级标准")
    public Result<Long> createGradingStandard(@Valid @RequestBody GradingStandardCreateDTO createDTO) {
        Long standardId = gradingStandardService.createGradingStandard(createDTO);
        return Result.success("数据分级标准创建成功", standardId);
    }

    /**
     * 更新数据分级标准
     */
    @PutMapping
    @Operation(summary = "更新数据分级标准", description = "更新数据分级标准信息")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.GRADING_STANDARD, description = "更新数据分级标准信息")
    public Result<Void> updateGradingStandard(@Valid @RequestBody GradingStandardUpdateDTO updateDTO) {
        gradingStandardService.updateGradingStandard(updateDTO);
        return Result.success("数据分级标准更新成功");
    }

    /**
     * 删除数据分级标准
     */
    @DeleteMapping("/{standardId}")
    @Operation(summary = "删除数据分级标准", description = "删除指定的数据分级标准")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.GRADING_STANDARD, description = "删除数据分级标准")
    public Result<Void> deleteGradingStandard(@PathVariable Long standardId) {
        gradingStandardService.deleteGradingStandard(standardId);
        return Result.success("数据分级标准删除成功");
    }

    /**
     * 分页查询数据分级标准
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询数据分级标准", description = "根据条件分页查询数据分级标准")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.GRADING_STANDARD, description = "查询数据分级标准列表")
    public Result<Page<GradingStandardVO>> queryGradingStandards(@Valid @RequestBody GradingStandardQueryDTO queryDTO) {
        Page<GradingStandardVO> page = gradingStandardService.queryGradingStandards(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取数据分级标准详情
     */
    @GetMapping("/{standardId}")
    @Operation(summary = "获取数据分级标准详情", description = "根据标准ID获取数据分级标准详细信息")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.GRADING_STANDARD, description = "查询数据分级标准详情")
    public Result<GradingStandardVO> getGradingStandardDetail(@PathVariable Long standardId) {
        GradingStandardVO standardVO = gradingStandardService.getGradingStandardDetail(standardId);
        return Result.success(standardVO);
    }

    /**
     * 发布数据分级标准
     */
    @PutMapping("/{standardId}/publish")
    @Operation(summary = "发布数据分级标准", description = "发布指定的数据分级标准")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.GRADING_STANDARD, description = "发布数据分级标准")
    public Result<Void> publishGradingStandard(@PathVariable Long standardId) {
        gradingStandardService.publishGradingStandard(standardId);
        return Result.success("数据分级标准发布成功");
    }

    /**
     * 归档数据分级标准
     */
    @PutMapping("/{standardId}/archive")
    @Operation(summary = "归档数据分级标准", description = "归档指定的数据分级标准")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.GRADING_STANDARD, description = "归档数据分级标准")
    public Result<Void> archiveGradingStandard(@PathVariable Long standardId) {
        gradingStandardService.archiveGradingStandard(standardId);
        return Result.success("数据分级标准归档成功");
    }

    /**
     * 获取所有发布的数据分级标准
     */
    @GetMapping("/published")
    @Operation(summary = "获取所有发布的数据分级标准", description = "获取所有已发布的数据分级标准")
    public Result<List<GradingStandardVO>> getPublishedGradingStandards() {
        List<GradingStandardVO> standards = gradingStandardService.getPublishedGradingStandards();
        return Result.success(standards);
    }
}
