package com.dataasset.security.controller;

import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.AssetDiscoveryImportDTO;
import com.dataasset.security.dto.DatabaseConnectionDTO;
import com.dataasset.security.service.AssetDiscoveryService;
import com.dataasset.security.vo.DiscoveredTableVO;
import com.dataasset.security.vo.ImportDuplicateCheckVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产发现控制器
 */
@Slf4j
@RestController
@RequestMapping("/asset-discovery")
@RequiredArgsConstructor
@Tag(name = "资产发现", description = "数据库资产发现和导入相关接口")
public class AssetDiscoveryController {

    private final AssetDiscoveryService assetDiscoveryService;

    /**
     * 测试数据库连接
     */
    @PostMapping("/test-connection")
    @Operation(summary = "测试数据库连接", description = "测试数据库连接是否可用")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "测试数据库连接")
    public Result<Boolean> testConnection(@Valid @RequestBody DatabaseConnectionDTO connectionDTO) {
        try {
            boolean connected = assetDiscoveryService.testConnection(connectionDTO);
            if (connected) {
                return Result.success("连接成功", true);
            } else {
                return Result.success("连接失败", false);
            }
        } catch (Exception e) {
            log.error("测试数据库连接异常: {}", e.getMessage(), e);
            return Result.error(500, "连接测试失败: " + e.getMessage());
        }
    }

    /**
     * 扫描数据库表
     */
    @PostMapping("/scan-tables")
    @Operation(summary = "扫描数据库表", description = "扫描数据库中所有表信息")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "扫描数据库表")
    public Result<List<DiscoveredTableVO>> scanTables(@Valid @RequestBody DatabaseConnectionDTO connectionDTO) {
        try {
            List<DiscoveredTableVO> tables = assetDiscoveryService.scanTables(connectionDTO);
            return Result.success(tables);
        } catch (Exception e) {
            log.error("扫描数据库表异常: {}", e.getMessage(), e);
            return Result.error(500, "扫描失败: " + e.getMessage());
        }
    }

    /**
     * 扫描表字段
     */
    @PostMapping("/scan-fields")
    @Operation(summary = "扫描表字段", description = "扫描指定表的所有字段信息")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "扫描表字段")
    public Result<DiscoveredTableVO> scanTableFields(@Valid @RequestBody DatabaseConnectionDTO connectionDTO,
                                                      @RequestParam String tableName) {
        try {
            DiscoveredTableVO tableInfo = assetDiscoveryService.scanTableFields(connectionDTO, tableName);
            return Result.success(tableInfo);
        } catch (Exception e) {
            log.error("扫描表字段异常: {}", e.getMessage(), e);
            return Result.error(500, "扫描字段失败: " + e.getMessage());
        }
    }

    /**
     * 检测导入时的重复资产
     */
    @PostMapping("/check-duplicates")
    @Operation(summary = "检测导入重复", description = "检测要导入的表是否已存在资产列表中")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_ASSET, description = "检测导入重复资产")
    public Result<ImportDuplicateCheckVO> checkImportDuplicates(@Valid @RequestBody AssetDiscoveryImportDTO importDTO) {
        try {
            ImportDuplicateCheckVO result = assetDiscoveryService.checkImportDuplicates(importDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("检测重复资产异常: {}", e.getMessage(), e);
            return Result.error(500, "检测重复失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入发现的表为数据资产
     */
    @PostMapping("/import")
    @Operation(summary = "导入发现的资产", description = "将发现的表批量导入为数据资产")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.DATA_ASSET, description = "导入发现的资产")
    public Result<List<Long>> importDiscoveredAssets(@Valid @RequestBody AssetDiscoveryImportDTO importDTO) {
        try {
            List<Long> assetIds = assetDiscoveryService.importDiscoveredAssets(importDTO);
            return Result.success("导入成功，共导入" + assetIds.size() + "个资产", assetIds);
        } catch (Exception e) {
            log.error("导入资产异常: {}", e.getMessage(), e);
            return Result.error(500, "导入失败: " + e.getMessage());
        }
    }
}
