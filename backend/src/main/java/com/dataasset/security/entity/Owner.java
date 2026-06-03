package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 责任人实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@TableName("owner")
public class Owner implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 责任人ID
     */
    @TableId(value = "owner_id", type = IdType.ASSIGN_ID)
    private Long ownerId;

    /**
     * 工号
     */
    private String employeeNo;

    /**
     * 姓名
     */
    private String name;

    /**
     * 所属部门ID
     */
    private Long departmentId;

    /**
     * 职务
     */
    private String position;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户账号
     */
    private String userAccount;

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
