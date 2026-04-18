package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建数据分类请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "创建数据分类请求")
public class DataClassificationCreateDTO {

    @Schema(description = "标准ID", required = true)
    @NotNull(message = "标准ID不能为空")
    private Long standardId;

    @Schema(description = "分类编码", required = true)
    @NotBlank(message = "分类编码不能为空")
    @Size(max = 64, message = "分类编码长度不能超过64")
    private String classificationCode;

    @Schema(description = "分类名称", required = true)
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 128, message = "分类名称长度不能超过128")
    private String classificationName;

    @Schema(description = "分类描述")
    @Size(max = 500, message = "分类描述长度不能超过500")
    private String classificationDescription;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "排序")
    private Integer sortOrder;
}
