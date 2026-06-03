package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据分类信息VO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "数据分类信息")
public class DataClassificationVO {

    @Schema(description = "分类ID")
    private Long classificationId;

    @Schema(description = "标准ID")
    private Long standardId;

    @Schema(description = "标准名称")
    private String standardName;

    @Schema(description = "分类编码")
    private String classificationCode;

    @Schema(description = "分类名称")
    private String classificationName;

    @Schema(description = "分类描述")
    private String classificationDescription;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "父分类名称")
    private String parentName;

    @Schema(description = "层级")
    private Integer level;

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

    @Schema(description = "子分类列表")
    private List<DataClassificationVO> children;
}
