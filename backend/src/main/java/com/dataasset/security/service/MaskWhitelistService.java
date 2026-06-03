package com.dataasset.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.MaskWhitelist;
import com.dataasset.security.mapper.MaskWhitelistMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 脱敏白名单Service
 */
@Slf4j
@Service
public class MaskWhitelistService extends ServiceImpl<MaskWhitelistMapper, MaskWhitelist> {

    /**
     * 分页查询白名单
     */
    public Page<MaskWhitelist> queryPage(Page<MaskWhitelist> page, Long strategyId, String whitelistType, String status) {
        LambdaQueryWrapper<MaskWhitelist> wrapper = new LambdaQueryWrapper<>();
        if (strategyId != null) {
            wrapper.eq(MaskWhitelist::getStrategyId, strategyId);
        }
        if (whitelistType != null && !whitelistType.isEmpty()) {
            wrapper.eq(MaskWhitelist::getWhitelistType, whitelistType);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(MaskWhitelist::getStatus, status);
        }
        wrapper.orderByDesc(MaskWhitelist::getCreatedTime);
        return this.page(page, wrapper);
    }

    /**
     * 根据策略ID获取白名单列表
     */
    public List<MaskWhitelist> getByStrategyId(Long strategyId) {
        LambdaQueryWrapper<MaskWhitelist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaskWhitelist::getStrategyId, strategyId)
               .eq(MaskWhitelist::getStatus, "ACTIVE")
               .orderByDesc(MaskWhitelist::getCreatedTime);
        return this.list(wrapper);
    }

    /**
     * 检查用户是否在白名单中
     */
    public boolean isInWhitelist(Long strategyId, Long userId, List<Long> roleIds) {
        LocalDateTime now = LocalDateTime.now();

        // 检查用户白名单
        LambdaQueryWrapper<MaskWhitelist> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(MaskWhitelist::getStrategyId, strategyId)
                   .eq(MaskWhitelist::getWhitelistType, "USER")
                   .eq(MaskWhitelist::getUserId, userId)
                   .eq(MaskWhitelist::getStatus, "ACTIVE")
                   .and(w -> w.isNull(MaskWhitelist::getEffectiveStart)
                             .or()
                             .le(MaskWhitelist::getEffectiveStart, now))
                   .and(w -> w.isNull(MaskWhitelist::getEffectiveEnd)
                             .or()
                             .ge(MaskWhitelist::getEffectiveEnd, now));

        if (this.count(userWrapper) > 0) {
            return true;
        }

        // 检查角色白名单
        if (roleIds != null && !roleIds.isEmpty()) {
            LambdaQueryWrapper<MaskWhitelist> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.eq(MaskWhitelist::getStrategyId, strategyId)
                       .eq(MaskWhitelist::getWhitelistType, "ROLE")
                       .in(MaskWhitelist::getRoleId, roleIds)
                       .eq(MaskWhitelist::getStatus, "ACTIVE")
                       .and(w -> w.isNull(MaskWhitelist::getEffectiveStart)
                                 .or()
                                 .le(MaskWhitelist::getEffectiveStart, now))
                       .and(w -> w.isNull(MaskWhitelist::getEffectiveEnd)
                                 .or()
                                 .ge(MaskWhitelist::getEffectiveEnd, now));

            return this.count(roleWrapper) > 0;
        }

        return false;
    }

    /**
     * 创建白名单
     */
    @Transactional(rollbackFor = Exception.class)
    public MaskWhitelist create(MaskWhitelist whitelist) {
        // 验证白名单类型
        if (!"USER".equals(whitelist.getWhitelistType()) && !"ROLE".equals(whitelist.getWhitelistType())) {
            throw new IllegalArgumentException("白名单类型必须是USER或ROLE");
        }

        // 验证用户ID或角色ID
        if ("USER".equals(whitelist.getWhitelistType()) && whitelist.getUserId() == null) {
            throw new IllegalArgumentException("用户白名单必须指定用户ID");
        }
        if ("ROLE".equals(whitelist.getWhitelistType()) && whitelist.getRoleId() == null) {
            throw new IllegalArgumentException("角色白名单必须指定角色ID");
        }

        // 检查是否已存在
        LambdaQueryWrapper<MaskWhitelist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaskWhitelist::getStrategyId, whitelist.getStrategyId())
               .eq(MaskWhitelist::getWhitelistType, whitelist.getWhitelistType());
        if ("USER".equals(whitelist.getWhitelistType())) {
            wrapper.eq(MaskWhitelist::getUserId, whitelist.getUserId());
        } else {
            wrapper.eq(MaskWhitelist::getRoleId, whitelist.getRoleId());
        }

        if (this.count(wrapper) > 0) {
            throw new IllegalArgumentException("该白名单已存在");
        }

        // 设置默认状态
        if (whitelist.getStatus() == null) {
            whitelist.setStatus("ACTIVE");
        }

        this.save(whitelist);
        return whitelist;
    }

    /**
     * 更新白名单
     */
    @Transactional(rollbackFor = Exception.class)
    public MaskWhitelist update(MaskWhitelist whitelist) {
        MaskWhitelist existing = this.getById(whitelist.getWhitelistId());
        if (existing == null) {
            throw new IllegalArgumentException("白名单不存在");
        }

        this.updateById(whitelist);
        return whitelist;
    }

    /**
     * 删除白名单
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long whitelistId) {
        this.removeById(whitelistId);
    }

    /**
     * 启用/禁用白名单
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long whitelistId, String status) {
        MaskWhitelist whitelist = this.getById(whitelistId);
        if (whitelist == null) {
            throw new IllegalArgumentException("白名单不存在");
        }
        whitelist.setStatus(status);
        this.updateById(whitelist);
    }
}
