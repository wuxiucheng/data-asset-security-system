package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.dto.PasswordChangeDTO;
import com.dataasset.security.dto.UserCreateDTO;
import com.dataasset.security.dto.UserQueryDTO;
import com.dataasset.security.dto.UserUpdateDTO;
import com.dataasset.security.entity.SysUser;
import com.dataasset.security.vo.UserVO;

/**
 * 用户管理Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface UserService extends IService<SysUser> {

    /**
     * 创建用户
     *
     * @param createDTO 创建用户请求
     * @return 用户ID
     */
    Long createUser(UserCreateDTO createDTO);

    /**
     * 更新用户
     *
     * @param updateDTO 更新用户请求
     */
    void updateUser(UserUpdateDTO updateDTO);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 分页查询用户
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<UserVO> queryUsers(UserQueryDTO queryDTO);

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    UserVO getUserDetail(Long userId);

    /**
     * 修改密码
     *
     * @param passwordChangeDTO 修改密码请求
     */
    void changePassword(PasswordChangeDTO passwordChangeDTO);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     */
    void updateUserStatus(Long userId, String status);

    /**
     * 重置密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long userId, String newPassword);
}
