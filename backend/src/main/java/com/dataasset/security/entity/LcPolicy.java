package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 生命周期策略实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lc_policy")
public class LcPolicy extends BaseEntity {

    @TableId
    private Long policyId;

    private String policyName;

    @TableField("grading_level")
    private String policyType;

    @TableField("retention_period")
    private Integer retentionDays;

    @TableField("expire_action")
    private String archiveAction;

    private String description;

    private String status;
}
