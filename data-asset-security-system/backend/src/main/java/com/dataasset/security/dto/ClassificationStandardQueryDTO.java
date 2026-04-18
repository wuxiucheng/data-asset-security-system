package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据分类标准查询DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "数据分类标准查询条件")
public class ClassificationStandardQueryDTO {

    @Schema(description = "标准编码")
    private String standardCode;

    @Schema(description = "标准名称")
    private String standardName;

    @Schema(description = "状态：DRAFT, PUBLISHED, ARCHIVED")
    private String status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "当前页码")
    private Long current = 1L;

    @Schema(description = "每页大小")
    private Long size = 10L;
}
