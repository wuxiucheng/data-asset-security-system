package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据资产实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_asset")
public class DataAsset extends BaseEntity implements Serializable {

    @TableId(value = "asset_id", type = IdType.AUTO)
    private Long assetId;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 资产编码
     */
    private String assetCode;

    /**
     * 资产类型：DATABASE, TABLE, FILE, API, OTHER
     */
    private String assetType;

    /**
     * 所属系统
     */
    private String systemName;

    /**
     * 数据库类型：MYSQL, ORACLE, POSTGRESQL, SQLSERVER, OTHER
     */
    private String databaseType;

    /**
     * 数据库地址
     */
    private String databaseHost;

    /**
     * 数据库端口
     */
    private Integer databasePort;

    /**
     * 数据库名称
     */
    private String databaseName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 资产描述
     */
    private String assetDescription;

    /**
     * 责任部门ID
     */
    private Long departmentId;

    /**
     * 责任人ID
     */
    private Long ownerId;

    /**
     * 分类ID
     */
    private Long classificationId;

    /**
     * 分级ID
     */
    private Long gradingId;

    /**
     * 敏感度评分
     */
    private Integer sensitivityScore;

    /**
     * 数据量级别：SMALL, MEDIUM, LARGE, HUGE
     */
    private String dataVolumeLevel;

    /**
     * 访问频率：LOW, MEDIUM, HIGH
     */
    private String accessFrequency;

    /**
     * 数据重要性：LOW, MEDIUM, HIGH, CRITICAL
     */
    private String importanceLevel;

    /**
     * 状态：DRAFT, ACTIVE, INACTIVE, ARCHIVED
     */
    private String status;

    /**
     * 是否包含敏感数据
     */
    private Boolean containsSensitiveData;

    /**
     * 敏感数据类型
     */
    private String sensitiveDataType;

    /**
     * 最后扫描时间
     */
    private LocalDateTime lastScanTime;

    /**
     * 最后扫描结果
     */
    private String lastScanResult;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 备注
     */
    private String remarks;
}
