package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据源配置查询DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "数据源配置查询条件")
public class DataSourceConfigQueryDTO {

    @Schema(description = "数据源名称")
    private String dataSourceName;

    @Schema(description = "数据库类型")
    private String databaseType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "当前页码")
    private Long current = 1L;

    @Schema(description = "每页大小")
    private Long size = 10L;
}
