package com.dataasset.security.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("compliance_eval_result")
public class ComplianceEvalResult extends BaseEntity {
    @TableId
    private Long evalId;
    private Long clauseId;
    private Long assetId;
    private Long fieldId;
    private String evalResult;
    private String evalDetails;
    private String status;
}
