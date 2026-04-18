package com.dataasset.security.controller;

import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.OrganizationComparisonDTO;
import com.dataasset.security.dto.OrganizationNodeDTO;
import com.dataasset.security.service.OrganizationSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 组织架构同步控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/organization")
@RequiredArgsConstructor
@Tag(name = "组织架构同步", description = "组织架构导入、导出、对比、同步相关接口")
public class OrganizationSyncController {

    private final OrganizationSyncService organizationSyncService;

    /**
     * 导入组织架构
     */
    @PostMapping("/import")
    @Operation(summary = "导入组织架构", description = "导入组织架构数据")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.ORGANIZATION, description = "导入组织架构")
    public Result<String> importOrganization(@Valid @RequestBody OrganizationNodeDTO organization) {
        String result = organizationSyncService.importOrganization(organization);
        return Result.success(result);
    }

    /**
     * 对比组织架构
     */
    @PostMapping("/compare")
    @Operation(summary = "对比组织架构", description = "对比组织架构差异")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.ORGANIZATION, description = "对比组织架构")
    public Result<OrganizationComparisonDTO> compareOrganization(@Valid @RequestBody OrganizationNodeDTO organization) {
        OrganizationComparisonDTO comparison = organizationSyncService.compareOrganization(organization);
        return Result.success(comparison);
    }

    /**
     * 同步组织架构
     */
    @PostMapping("/sync")
    @Operation(summary = "同步组织架构", description = "同步组织架构数据")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.ORGANIZATION, description = "同步组织架构")
    public Result<String> syncOrganization(@Valid @RequestBody OrganizationNodeDTO organization) {
        String result = organizationSyncService.syncOrganization(organization);
        return Result.success(result);
    }

    /**
     * 导出组织架构
     */
    @GetMapping("/export")
    @Operation(summary = "导出组织架构", description = "导出组织架构数据")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.ORGANIZATION, description = "导出组织架构")
    public Result<OrganizationNodeDTO> exportOrganization() {
        OrganizationNodeDTO organization = organizationSyncService.exportOrganization();
        return Result.success(organization);
    }
}
