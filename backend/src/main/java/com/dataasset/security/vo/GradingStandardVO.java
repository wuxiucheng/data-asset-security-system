package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据分级标准信息VO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "数据分级标准信息")
public class GradingStandardVO {

    @Schema(description = "标准ID")
    private Long standardId;

    @Schema(description = "标准编码")
    private String standardCode;

    @Schema(description = "标准名称")
    private String standardName;

    @Schema(description = "标准描述")
    private String standardDescription;

    @Schema(description = "版本号")
    private String version;

    @Schema(description = "发布日期")
    private String publishDate;

    @Schema(description = "发布单位")
    private String publishUnit;

    @Schema(description = "适用范围")
    private String scope;

    @Schema(description = "状态：DRAFT, PUBLISHED, ARCHIVED")
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
