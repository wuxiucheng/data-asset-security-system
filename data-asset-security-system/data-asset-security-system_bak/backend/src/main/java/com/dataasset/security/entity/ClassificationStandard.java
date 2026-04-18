package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 数据分类标准实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("classification_standard")
public class ClassificationStandard extends BaseEntity implements Serializable {

    @TableId(value = "standard_id", type = IdType.AUTO)
    private Long standardId;

    /**
     * 标准编码
     */
    private String standardCode;

    /**
     * 标准名称
     */
    private String standardName;

    /**
     * 标准描述
     */
    private String standardDescription;

    /**
     * 版本号
     */
    private String version;

    /**
     * 发布日期
     */
    private String publishDate;

    /**
     * 发布单位
     */
    private String publishUnit;

    /**
     * 适用范围
     */
    private String scope;

    /**
     * 状态：DRAFT, PUBLISHED, ARCHIVED
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
