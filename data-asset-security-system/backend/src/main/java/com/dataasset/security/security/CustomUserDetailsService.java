package com.dataasset.security.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataasset.security.common.exception.ResourceNotFoundException;
import com.dataasset.security.common.result.ResultCode;
import com.dataasset.security.entity.SysRole;
import com.dataasset.security.entity.SysUser;
import com.dataasset.security.mapper.SysRoleMapper;
import com.dataasset.security.mapper.SysUserMapper;
import com.dataasset.security.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserService sysUserService;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        SysUser user = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, username)
                .one();

        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_EXIST);
        }

        // 检查用户状态
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new ResourceNotFoundException("用户已被禁用或锁定");
        }

        // 查询用户角色
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(user.getUserId());

        // 转换为权限列表
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType()))
                .collect(Collectors.toList());

        // 返回用户详情
        return new CustomUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getEmail(),
                user.getPhone(),
                authorities
        );
    }
}
