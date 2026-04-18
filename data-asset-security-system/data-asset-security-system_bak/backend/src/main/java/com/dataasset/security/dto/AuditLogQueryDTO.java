package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志查询DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "审计日志查询条件")
public class AuditLogQueryDTO {

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作人ID")
    private Long operatorId;

    @Schema(description = "操作对象类型")
    private String objectType;

    @Schema(description = "操作对象ID")
    private Long objectId;

    @Schema(description = "操作结果")
    private String operationResult;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "当前页码")
    private Long current = 1L;

    @Schema(description = "每页大小")
    private Long size = 10L;
}
