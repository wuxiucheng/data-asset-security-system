package com.dataasset.security.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

@Data
@TableName("governance_kpi")
public class GovernanceKpi implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long kpiId;
    private String kpiName;
    private String kpiCode;

    @TableField("kpi_value")
    private Double actualValue;

    @TableField("kpi_target")
    private Double targetValue;

    @TableField("record_date")
    private String period;

    private Integer deleted;
}
