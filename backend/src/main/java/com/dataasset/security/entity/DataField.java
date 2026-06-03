package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据字段实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_field")
public class DataField extends BaseEntity implements Serializable {

    @TableId(value = "field_id", type = IdType.AUTO)
    private Long fieldId;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段编码
     */
    private String fieldCode;

    /**
     * 字段类型：STRING, INTEGER, DECIMAL, DATE, BOOLEAN, OTHER
     */
    private String fieldType;

    /**
     * 字段长度
     */
    private Integer fieldLength;

    /**
     * 是否为空
     */
    private Boolean nullable;

    /**
     * 是否为主键
     */
    private Boolean isPrimaryKey;

    /**
     * 字段描述
     */
    private String fieldDescription;

    /**
     * 分类ID
     */
    private Long classificationId;

    /**
     * 分级ID
     */
    private Long gradingId;

    /**
     * 是否包含敏感数据
     */
    private Boolean containsSensitiveData;

    /**
     * 敏感数据类型
     */
    private String sensitiveDataType;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 是否必填
     */
    private Boolean isRequired;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：ACTIVE, INACTIVE
     */
    private String status;

    /**
     * 数据条数，记录该字段在对应数据表中非空值的记录条数
     */
    private Long rowCount;
}
