package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 导入重复检测结果VO
 */
@Data
@Schema(description = "导入重复检测结果")
public class ImportDuplicateCheckVO {

    @Schema(description = "是否存在重复")
    private boolean hasDuplicate;

    @Schema(description = "重复的表信息列表")
    private List<DuplicateItem> duplicates;

    @Schema(description = "不重复的表数量")
    private int newCount;

    @Data
    @Schema(description = "重复项信息")
    public static class DuplicateItem {
        @Schema(description = "表名")
        private String tableName;

        @Schema(description = "表注释")
        private String tableComment;

        @Schema(description = "已存在的资产ID")
        private Long existingAssetId;

        @Schema(description = "已存在的资产名称")
        private String existingAssetName;

        @Schema(description = "已存在的资产状态")
        private String existingAssetStatus;
    }
}
