package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.AuditLogQueryDTO;
import com.dataasset.security.security.CustomUserDetails;
import com.dataasset.security.service.AuditLogService;
import com.dataasset.security.vo.AuditLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 审计日志控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
@Tag(name = "审计日志管理", description = "审计日志查询相关接口")
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * 分页查询审计日志
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询审计日志", description = "根据条件分页查询审计日志")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.OTHER, description = "查询审计日志")
    public Result<Page<AuditLogVO>> queryAuditLogs(@Valid @RequestBody AuditLogQueryDTO queryDTO) {
        Page<AuditLogVO> page = auditLogService.queryAuditLogs(queryDTO);
        return Result.success(page);
    }

    /**
     * 查询当前用户的审计日志
     */
    @PostMapping("/my")
    @Operation(summary = "查询当前用户的审计日志", description = "查询当前登录用户的审计日志")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.OTHER, description = "查询我的审计日志")
    public Result<Page<AuditLogVO>> queryMyAuditLogs(@Valid @RequestBody AuditLogQueryDTO queryDTO) {
        // 获取当前用户ID
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        queryDTO.setOperatorId(userDetails.getUserId());

        Page<AuditLogVO> page = auditLogService.queryAuditLogs(queryDTO);
        return Result.success(page);
    }
}
