package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新数据分类请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "更新数据分类请求")
public class DataClassificationUpdateDTO {

    @Schema(description = "分类ID", required = true)
    @NotNull(message = "分类ID不能为空")
    private Long classificationId;

    @Schema(description = "分类名称")
    @Size(max = 128, message = "分类名称长度不能超过128")
    private String classificationName;

    @Schema(description = "分类描述")
    @Size(max = 500, message = "分类描述长度不能超过500")
    private String classificationDescription;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    @Size(max = 32, message = "状态长度不能超过32")
    private String status;
}
