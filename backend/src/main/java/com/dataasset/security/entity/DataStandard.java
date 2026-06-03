package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 数据标准实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_standard")
public class DataStandard extends BaseEntity {

    @TableId
    private Long standardId;

    private String standardName;

    private String standardType;

    @TableField("standard_content")
    private String description;

    private String status;
}
