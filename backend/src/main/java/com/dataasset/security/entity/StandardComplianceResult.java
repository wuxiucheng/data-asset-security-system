package com.dataasset.security.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("standard_compliance_result")
public class StandardComplianceResult extends BaseEntity {
    @TableId
    private Long resultId;
    private Long standardId;
    private Long assetId;
    private Long fieldId;
    private String complianceResult;
    private String details;
    private String status;
}
