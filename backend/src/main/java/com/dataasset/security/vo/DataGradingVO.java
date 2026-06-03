package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据分级信息VO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "数据分级信息")
public class DataGradingVO {

    @Schema(description = "分级ID")
    private Long gradingId;

    @Schema(description = "标准ID")
    private Long standardId;

    @Schema(description = "标准名称")
    private String standardName;

    @Schema(description = "分级编码")
    private String gradingCode;

    @Schema(description = "分级名称")
    private String gradingName;

    @Schema(description = "分级描述")
    private String gradingDescription;

    @Schema(description = "等级值")
    private Integer levelValue;

    @Schema(description = "颜色标识")
    private String colorCode;

    @Schema(description = "安全要求")
    private String securityRequirements;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    private String status;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "更新人ID")
    private Long updaterId;

    @Schema(description = "更新人姓名")
    private String updaterName;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
}
