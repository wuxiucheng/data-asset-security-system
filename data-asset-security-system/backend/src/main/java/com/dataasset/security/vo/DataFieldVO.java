package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据字段信息VO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "数据字段信息")
public class DataFieldVO {

    @Schema(description = "字段ID")
    private Long fieldId;

    @Schema(description = "资产ID")
    private Long assetId;

    @Schema(description = "资产名称")
    private String assetName;

    @Schema(description = "字段名称")
    private String fieldName;

    @Schema(description = "字段编码")
    private String fieldCode;

    @Schema(description = "字段类型")
    private String fieldType;

    @Schema(description = "字段长度")
    private Integer fieldLength;

    @Schema(description = "是否为空")
    private Boolean nullable;

    @Schema(description = "是否为主键")
    private Boolean isPrimaryKey;

    @Schema(description = "字段描述")
    private String fieldDescription;

    @Schema(description = "分类ID")
    private Long classificationId;

    @Schema(description = "分类名称")
    private String classificationName;

    @Schema(description = "分级ID")
    private Long gradingId;

    @Schema(description = "分级名称")
    private String gradingName;

    @Schema(description = "是否包含敏感数据")
    private Boolean containsSensitiveData;

    @Schema(description = "敏感数据类型")
    private String sensitiveDataType;

    @Schema(description = "默认值")
    private String defaultValue;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
}
