package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

/**
 * 影响分析实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("impact_analysis")
public class ImpactAnalysis extends BaseEntity {

    /**
     * 分析ID
     */
    @TableId
    private Long analysisId;

    /**
     * 变更资产ID
     */
    @TableField("source_asset_id")
    private Long changeAssetId;

    /**
     * 受影响资产ID
     */
    @TableField("affected_asset_id")
    private Long changeFieldId;

    /**
     * 影响级别
     */
    private String impactLevel;

    /**
     * 影响路径
     */
    @TableField("impact_path")
    private String affectedAssets;
}
