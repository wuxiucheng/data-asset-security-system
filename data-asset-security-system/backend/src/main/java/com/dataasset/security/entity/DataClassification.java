package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 数据分类实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_classification")
public class DataClassification extends BaseEntity implements Serializable {

    @TableId(value = "classification_id", type = IdType.AUTO)
    private Long classificationId;

    /**
     * 标准ID
     */
    private Long standardId;

    /**
     * 分类编码
     */
    private String classificationCode;

    /**
     * 分类名称
     */
    private String classificationName;

    /**
     * 分类描述
     */
    private String classificationDescription;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 层级
     */
    private Integer level;

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
