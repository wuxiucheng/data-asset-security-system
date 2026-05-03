package com.dataasset.security.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新数据分类标准请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "更新数据分类标准请求")
public class ClassificationStandardUpdateDTO {

    @Schema(description = "标准ID", required = true)
    @NotNull(message = "标准ID不能为空")
    private Long standardId;

    @Schema(description = "标准名称")
    @Size(max = 128, message = "标准名称长度不能超过128")
    private String standardName;

    @Schema(description = "标准描述")
    @Size(max = 500, message = "标准描述长度不能超过500")
    @JsonAlias("description")
    private String standardDescription;

    @Schema(description = "版本号")
    @Size(max = 32, message = "版本号长度不能超过32")
    private String version;

    @Schema(description = "发布日期")
    @Size(max = 32, message = "发布日期长度不能超过32")
    private String publishDate;

    @Schema(description = "发布单位")
    @Size(max = 128, message = "发布单位长度不能超过128")
    private String publishUnit;

    @Schema(description = "适用范围")
    @Size(max = 500, message = "适用范围长度不能超过500")
    private String scope;

    @Schema(description = "状态：DRAFT, PUBLISHED, ARCHIVED")
    @Size(max = 32, message = "状态长度不能超过32")
    private String status;
}
