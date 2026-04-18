package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 数据分级实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_grading")
public class DataGrading extends BaseEntity implements Serializable {

    @TableId(value = "grading_id", type = IdType.AUTO)
    private Long gradingId;

    /**
     * 标准ID
     */
    private Long standardId;

    /**
     * 分级编码
     */
    private String gradingCode;

    /**
     * 分级名称
     */
    private String gradingName;

    /**
     * 分级描述
     */
    private String gradingDescription;

    /**
     * 等级值
     */
    private Integer levelValue;

    /**
     * 颜色标识
     */
    private String colorCode;

    /**
     * 安全要求
     */
    private String securityRequirements;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：ACTIVE, INACTIVE
     */
    private String status;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 更新人ID
     */
    private Long updaterId;
}
