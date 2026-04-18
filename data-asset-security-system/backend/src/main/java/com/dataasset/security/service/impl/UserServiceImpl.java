package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.common.exception.ResourceNotFoundException;
import com.dataasset.security.common.result.ResultCode;
import com.dataasset.security.dto.PasswordChangeDTO;
import com.dataasset.security.dto.UserCreateDTO;
import com.dataasset.security.dto.UserQueryDTO;
import com.dataasset.security.dto.UserUpdateDTO;
import com.dataasset.security.entity.SysRole;
import com.dataasset.security.entity.SysUser;
import com.dataasset.security.entity.SysUserRole;
import com.dataasset.security.mapper.SysRoleMapper;
import com.dataasset.security.mapper.SysUserRoleMapper;
import com.dataasset.security.mapper.SysUserMapper;
import com.dataasset.security.security.CustomUserDetails;
import com.dataasset.security.security.SeparationOfDutiesService;
import com.dataasset.security.service.UserService;
import com.dataasset.security.vo.RoleVO;
import com.dataasset.security.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户管理Service实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SeparationOfDutiesService separationOfDutiesService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateDTO createDTO) {
        // 检查用户名是否已存在
        SysUser existUser = this.lambdaQuery()
                .eq(SysUser::getUsername, createDTO.getUsername())
                .one();
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXIST);
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(createDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setRealName(createDTO.getRealName());
        user.setEmail(createDTO.getEmail());
        user.setPhone(createDTO.getPhone());
        user.setStatus("ACTIVE");
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        this.save(user);
        log.info("用户创建成功：{}", user.getUsername());

        return user.getUserId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateDTO updateDTO) {
        // 检查用户是否存在
        SysUser user = this.getById(updateDTO.getUserId());
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_EXIST);
        }

        // 更新用户信息
        if (StringUtils.hasText(updateDTO.getRealName())) {
            user.setRealName(updateDTO.getRealName());
        }
        if (StringUtils.hasText(updateDTO.getEmail())) {
            user.setEmail(updateDTO.getEmail());
        }
        if (StringUtils.hasText(updateDTO.getPhone())) {
            user.setPhone(updateDTO.getPhone());
        }
        if (StringUtils.hasText(updateDTO.getStatus())) {
            user.setStatus(updateDTO.getStatus());
        }

        user.setUpdatedTime(LocalDateTime.now());
        this.updateById(user);
        log.info("用户更新成功：{}", user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        // 检查用户是否存在
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_EXIST);
        }

        // 不能删除系统管理员
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能删除系统管理员");
        }

        // 删除用户角色关联
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));

        // 删除用户（逻辑删除）
        this.removeById(userId);
        log.info("用户删除成功：{}", user.getUsername());
    }

    @Override
    public Page<UserVO> queryUsers(UserQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like(SysUser::getUsername, queryDTO.getUsername());
        }
        if (StringUtils.hasText(queryDTO.getRealName())) {
            wrapper.like(SysUser::getRealName, queryDTO.getRealName());
        }
        if (StringUtils.hasText(queryDTO.getEmail())) {
            wrapper.like(SysUser::getEmail, queryDTO.getEmail());
        }
        if (StringUtils.hasText(queryDTO.getPhone())) {
            wrapper.like(SysUser::getPhone, queryDTO.getPhone());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(SysUser::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(SysUser::getCreatedTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(SysUser::getCreatedTime, queryDTO.getEndTime());
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(SysUser::getCreatedTime);

        // 分页查询
        Page<SysUser> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<SysUser> userPage = this.page(page, wrapper);

        // 转换为VO
        Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    public UserVO getUserDetail(Long userId) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_EXIST);
        }

        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(PasswordChangeDTO passwordChangeDTO) {
        // 检查新密码和确认密码是否一致
        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword())) {
            throw new BusinessException("新密码和确认密码不一致");
        }

        // 检查用户是否存在
        SysUser user = this.getById(passwordChangeDTO.getUserId());
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_EXIST);
        }

        // 验证旧密码
        if (!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        user.setUpdatedTime(LocalDateTime.now());
        this.updateById(user);
        log.info("用户密码修改成功：{}", user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, String status) {
        // 检查用户是否存在
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_EXIST);
        }

        // 不能禁用系统管理员
        if ("admin".equals(user.getUsername()) && !"ACTIVE".equals(status)) {
            throw new BusinessException("不能禁用系统管理员");
        }

        // 更新用户状态
        user.setStatus(status);
        user.setUpdatedTime(LocalDateTime.now());
        this.updateById(user);
        log.info("用户状态更新成功：{} -> {}", user.getUsername(), status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, String newPassword) {
        // 检查用户是否存在
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_EXIST);
        }

        // 重置密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedTime(LocalDateTime.now());
        this.updateById(user);
        log.info("用户密码重置成功：{}", user.getUsername());
    }

    /**
     * 转换为VO
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setStatus(user.getStatus());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setLastLoginIp(user.getLastLoginIp());
        vo.setCreatedTime(user.getCreatedTime());
        vo.setUpdatedTime(user.getUpdatedTime());

        // 查询用户角色
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(user.getUserId());
        List<RoleVO> roleVOs = roles.stream()
                .map(this::convertRoleToVO)
                .collect(Collectors.toList());
        vo.setRoles(roleVOs);

        return vo;
    }

    /**
     * 转换角色为VO
     */
    private RoleVO convertRoleToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        vo.setRoleId(role.getRoleId());
        vo.setRoleCode(role.getRoleCode());
        vo.setRoleName(role.getRoleName());
        vo.setRoleDescription(role.getRoleDescription());
        vo.setRoleType(role.getRoleType());
        vo.setStatus(role.getStatus());
        return vo;
    }
}
