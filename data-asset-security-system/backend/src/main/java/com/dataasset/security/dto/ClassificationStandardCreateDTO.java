package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建数据分类标准请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "创建数据分类标准请求")
public class ClassificationStandardCreateDTO {

    @Schema(description = "标准编码", required = true)
    @NotBlank(message = "标准编码不能为空")
    @Size(max = 64, message = "标准编码长度不能超过64")
    private String standardCode;

    @Schema(description = "标准名称", required = true)
    @NotBlank(message = "标准名称不能为空")
    @Size(max = 128, message = "标准名称长度不能超过128")
    private String standardName;

    @Schema(description = "标准描述")
    @Size(max = 500, message = "标准描述长度不能超过500")
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
}
