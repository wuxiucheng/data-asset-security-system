package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志VO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "审计日志信息")
public class AuditLogVO {

    @Schema(description = "日志ID")
    private Long logId;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作人ID")
    private Long operatorId;

    @Schema(description = "操作人姓名")
    private String operatorName;

    @Schema(description = "操作时间")
    private LocalDateTime operationTime;

    @Schema(description = "操作对象类型")
    private String objectType;

    @Schema(description = "操作对象ID")
    private Long objectId;

    @Schema(description = "操作对象名称")
    private String objectName;

    @Schema(description = "操作内容")
    private String operationContent;

    @Schema(description = "操作结果")
    private String operationResult;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "用户代理")
    private String userAgent;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "请求URL")
    private String requestUrl;
}
