package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 元数据版本实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("metadata_version")
public class MetadataVersion extends BaseEntity {

    /**
     * 版本ID
     */
    @TableId
    private Long versionId;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 字段ID
     */
    private Long fieldId;

    /**
     * 版本号
     */
    private String versionNumber;

    /**
     * 元数据快照(JSON)
     */
    private String snapshot;

    /**
     * 变更类型：CREATE/UPDATE/DELETE
     */
    private String changeType;

    /**
     * 变更描述
     */
    private String changeDescription;

    /**
     * 变更详情(JSON)
     */
    private String changeDetails;

    /**
     * 是否当前版本
     */
    private Boolean isCurrent;

    /**
     * 备注
     */
    private String remark;
}
