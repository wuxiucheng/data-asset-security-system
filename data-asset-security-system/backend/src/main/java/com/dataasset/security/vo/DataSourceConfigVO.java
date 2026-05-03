package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据源配置信息VO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "数据源配置信息")
public class DataSourceConfigVO {

    @Schema(description = "数据源ID")
    private Long dataSourceId;

    @Schema(description = "数据源名称")
    private String dataSourceName;

    @Schema(description = "数据库类型")
    private String databaseType;

    @Schema(description = "数据库地址")
    private String host;

    @Schema(description = "数据库端口")
    private Integer port;

    @Schema(description = "数据库名称")
    private String databaseName;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "额外连接参数")
    private String connectionParams;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "最后测试连接时间")
    private LocalDateTime lastTestTime;

    @Schema(description = "最后测试连接结果")
    private String lastTestResult;

    @Schema(description = "备注")
    private String remarks;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
}
