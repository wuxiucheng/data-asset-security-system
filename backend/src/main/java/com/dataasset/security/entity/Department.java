package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 责任部门实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@TableName("department")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @TableId(value = "department_id", type = IdType.ASSIGN_ID)
    private Long departmentId;

    /**
     * 部门编码
     */
    private String departmentCode;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 部门负责人ID
     */
    private Long leaderId;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 部门描述
     */
    private String departmentDescription;

    /**
     * 上级部门ID
     */
    private Long parentId;

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
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人ID
     */
    private Long updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    private Integer deleted;
}
