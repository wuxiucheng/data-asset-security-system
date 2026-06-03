package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.SysUser;
import com.dataasset.security.mapper.SysUserMapper;
import com.dataasset.security.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * 系统用户 Service 实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
