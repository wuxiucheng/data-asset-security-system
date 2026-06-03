package com.dataasset.security.controller;

import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.LineageRelation;
import com.dataasset.security.entity.ImpactAnalysis;
import com.dataasset.security.entity.MetadataVersion;
import com.dataasset.security.service.LineageRelationService;
import com.dataasset.security.service.ImpactAnalysisService;
import com.dataasset.security.service.MetadataVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据血缘与影响分析Controller
 */
@Slf4j
@RestController
@RequestMapping("/lineage")
@Tag(name = "数据血缘与影响分析", description = "数据血缘关系和影响分析管理")
public class LineageController {

    @Autowired
    private LineageRelationService lineageRelationService;

    @Autowired
    private ImpactAnalysisService impactAnalysisService;

    @Autowired
    private MetadataVersionService metadataVersionService;

    // ==================== 血缘关系管理 ====================

    @GetMapping("/relation/list")
    @Operation(summary = "获取所有血缘关系")
    public Result<List<LineageRelation>> listRelations() {
        return Result.success(lineageRelationService.list());
    }

    @PostMapping("/relation")
    @Operation(summary = "创建血缘关系")
    public Result<LineageRelation> createRelation(@RequestBody LineageRelation relation) {
        lineageRelationService.save(relation);
        return Result.success(relation);
    }

    @DeleteMapping("/relation/{lineageId}")
    @Operation(summary = "删除血缘关系")
    public Result<Void> deleteRelation(@PathVariable Long lineageId) {
        lineageRelationService.removeById(lineageId);
        return Result.success();
    }

    // ==================== 影响分析管理 ====================

    @GetMapping("/analysis/list")
    @Operation(summary = "获取所有影响分析")
    public Result<List<ImpactAnalysis>> listAnalyses() {
        return Result.success(impactAnalysisService.list());
    }

    @PostMapping("/analysis")
    @Operation(summary = "创建影响分析")
    public Result<ImpactAnalysis> createAnalysis(@RequestBody ImpactAnalysis analysis) {
        impactAnalysisService.save(analysis);
        return Result.success(analysis);
    }

    // ==================== 元数据版本管理 ====================

    @GetMapping("/version/list")
    @Operation(summary = "获取所有元数据版本")
    public Result<List<MetadataVersion>> listVersions() {
        return Result.success(metadataVersionService.list());
    }

    @PostMapping("/version")
    @Operation(summary = "创建元数据版本")
    public Result<MetadataVersion> createVersion(@RequestBody MetadataVersion version) {
        metadataVersionService.save(version);
        return Result.success(version);
    }
}
