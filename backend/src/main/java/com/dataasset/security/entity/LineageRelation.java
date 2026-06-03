package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 血缘关系实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lineage_relation")
public class LineageRelation extends BaseEntity {

    /**
     * 血缘ID
     */
    @TableId
    private Long lineageId;

    /**
     * 源资产ID
     */
    private Long sourceAssetId;

    /**
     * 源字段ID
     */
    private Long sourceFieldId;

    /**
     * 目标资产ID
     */
    private Long targetAssetId;

    /**
     * 目标字段ID
     */
    private Long targetFieldId;

    /**
     * 关系类型：DIRECT/INDIRECT/DERIVED
     */
    private String relationType;

    /**
     * 转换逻辑描述
     */
    @TableField("transform_desc")
    private String transformation;

    /**
     * 血缘层级
     */
    private Integer level;

    /**
     * 路径描述
     */
    private String path;

    /**
     * 置信度：0-100
     */
    private Integer confidence;

    /**
     * 来源：MANUAL/AUTO/DISCOVERED
     */
    @TableField("source_type")
    private String source;

    /**
     * 状态：ACTIVE/INACTIVE
     */
    private String status;
}
