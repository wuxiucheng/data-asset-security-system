package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.dto.SensitiveIdentRuleCreateDTO;
import com.dataasset.security.dto.SensitiveIdentRuleQueryDTO;
import com.dataasset.security.dto.SensitiveIdentRuleUpdateDTO;
import com.dataasset.security.entity.SensitiveIdentRule;
import com.dataasset.security.vo.SensitiveIdentRuleVO;

import java.util.List;

/**
 * 敏感识别规则Service接口
 */
public interface SensitiveIdentRuleService extends IService<SensitiveIdentRule> {

    /**
     * 分页查询规则
     */
    Page<SensitiveIdentRuleVO> queryPage(SensitiveIdentRuleQueryDTO queryDTO);

    /**
     * 获取所有启用的规则
     */
    List<SensitiveIdentRule> listEnabled();

    /**
     * 创建规则
     */
    SensitiveIdentRule create(SensitiveIdentRuleCreateDTO createDTO);

    /**
     * 更新规则
     */
    SensitiveIdentRule update(SensitiveIdentRuleUpdateDTO updateDTO);

    /**
     * 删除规则
     */
    void delete(Long ruleId);

    /**
     * 启用/禁用规则
     */
    void updateStatus(Long ruleId, String status);

    /**
     * 初始化内置规则
     */
    void initBuiltinRules();
}
