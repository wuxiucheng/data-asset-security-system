package com.dataasset.security.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("compliance_clause")
public class ComplianceClause extends BaseEntity {
    @TableId
    private Long clauseId;

    @TableField("clause_content")
    private String clauseName;

    private String clauseCode;

    @TableField("regulation_source")
    private String regulation;

    @TableField("eval_method")
    private String description;

    @TableField("compliance_status")
    private String status;
}
