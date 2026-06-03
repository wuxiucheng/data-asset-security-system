package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 发现的表信息VO
 */
@Data
@Schema(description = "发现的表信息")
public class DiscoveredTableVO {

    @Schema(description = "表名")
    private String tableName;

    @Schema(description = "表注释/描述")
    private String tableComment;

    @Schema(description = "表类型")
    private String tableType;

    @Schema(description = "字段列表")
    private List<DiscoveredFieldVO> fields;

    @Schema(description = "行数估算")
    private Long rowCount;

    @Data
    @Schema(description = "发现的字段信息")
    public static class DiscoveredFieldVO {

        @Schema(description = "字段名")
        private String fieldName;

        @Schema(description = "字段类型")
        private String fieldType;

        @Schema(description = "字段长度")
        private Integer fieldLength;

        @Schema(description = "是否可空")
        private Boolean nullable;

        @Schema(description = "是否主键")
        private Boolean isPrimaryKey;

        @Schema(description = "字段注释")
        private String fieldComment;

        @Schema(description = "默认值")
        private String defaultValue;
    }
}
