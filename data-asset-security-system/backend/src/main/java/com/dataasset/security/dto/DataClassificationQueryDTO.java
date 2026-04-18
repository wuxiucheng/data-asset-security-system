package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据分类查询DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "数据分类查询条件")
public class DataClassificationQueryDTO {

    @Schema(description = "标准ID")
    private Long standardId;

    @Schema(description = "分类编码")
    private String classificationCode;

    @Schema(description = "分类名称")
    private String classificationName;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "状态：ACTIVE, INACTIVE")
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
