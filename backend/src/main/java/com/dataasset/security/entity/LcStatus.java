package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

/**
 * 生命周期状态实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lc_status")
public class LcStatus extends BaseEntity {

    @TableId(type = IdType.AUTO, value = "lifecycle_id")
    private Long statusId;

    private Long policyId;

    private Long assetId;

    private String lifecycleStage;

    @TableField("expire_date")
    private LocalDateTime stageEndTime;

    @TableField("dispose_status")
    private String status;
}
