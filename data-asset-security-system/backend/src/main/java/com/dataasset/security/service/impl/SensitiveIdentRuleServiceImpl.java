package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.dto.SensitiveIdentRuleCreateDTO;
import com.dataasset.security.dto.SensitiveIdentRuleQueryDTO;
import com.dataasset.security.dto.SensitiveIdentRuleUpdateDTO;
import com.dataasset.security.entity.SensitiveIdentRule;
import com.dataasset.security.mapper.SensitiveIdentRuleMapper;
import com.dataasset.security.service.SensitiveIdentRuleService;
import com.dataasset.security.vo.SensitiveIdentRuleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 敏感识别规则Service实现
 */
@Slf4j
@Service
public class SensitiveIdentRuleServiceImpl extends ServiceImpl<SensitiveIdentRuleMapper, SensitiveIdentRule> implements SensitiveIdentRuleService {

    @Override
    public Page<SensitiveIdentRuleVO> queryPage(SensitiveIdentRuleQueryDTO queryDTO) {
        LambdaQueryWrapper<SensitiveIdentRule> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getRuleName())) {
            wrapper.like(SensitiveIdentRule::getRuleName, queryDTO.getRuleName());
        }
        if (StringUtils.hasText(queryDTO.getSensitiveType())) {
            wrapper.eq(SensitiveIdentRule::getSensitiveType, queryDTO.getSensitiveType());
        }
        if (StringUtils.hasText(queryDTO.getMatchMode())) {
            wrapper.eq(SensitiveIdentRule::getMatchMode, queryDTO.getMatchMode());
        }
        if (queryDTO.getIsBuiltin() != null) {
            wrapper.eq(SensitiveIdentRule::getIsBuiltin, queryDTO.getIsBuiltin());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(SensitiveIdentRule::getStatus, queryDTO.getStatus());
        }
        
        wrapper.orderByAsc(SensitiveIdentRule::getPriority);
        
        Page<SensitiveIdentRule> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<SensitiveIdentRule> result = this.page(page, wrapper);
        
        Page<SensitiveIdentRuleVO> voPage = new Page<>();
        BeanUtils.copyProperties(result, voPage, "records");
        voPage.setRecords(result.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        
        return voPage;
    }

    @Override
    public List<SensitiveIdentRule> listEnabled() {
        return this.list(new LambdaQueryWrapper<SensitiveIdentRule>()
                .eq(SensitiveIdentRule::getStatus, "ENABLED")
                .orderByAsc(SensitiveIdentRule::getPriority));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SensitiveIdentRule create(SensitiveIdentRuleCreateDTO createDTO) {
        SensitiveIdentRule rule = new SensitiveIdentRule();
        BeanUtils.copyProperties(createDTO, rule);
        
        if (rule.getConfidenceWeight() == null) {
            rule.setConfidenceWeight(new BigDecimal("0.80"));
        }
        if (rule.getPriority() == null) {
            rule.setPriority(100);
        }
        if (rule.getStatus() == null) {
            rule.setStatus("ENABLED");
        }
        rule.setIsBuiltin(false);
        
        this.save(rule);
        return rule;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SensitiveIdentRule update(SensitiveIdentRuleUpdateDTO updateDTO) {
        SensitiveIdentRule rule = this.getById(updateDTO.getRuleId());
        if (rule == null) {
            throw new RuntimeException("规则不存在");
        }
        
        if (rule.getIsBuiltin()) {
            throw new RuntimeException("内置规则不允许修改");
        }
        
        BeanUtils.copyProperties(updateDTO, rule, "ruleId");
        this.updateById(rule);
        return rule;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long ruleId) {
        SensitiveIdentRule rule = this.getById(ruleId);
        if (rule == null) {
            throw new RuntimeException("规则不存在");
        }
        
        if (rule.getIsBuiltin()) {
            throw new RuntimeException("内置规则不允许删除");
        }
        
        this.removeById(ruleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long ruleId, String status) {
        SensitiveIdentRule rule = this.getById(ruleId);
        if (rule == null) {
            throw new RuntimeException("规则不存在");
        }
        
        rule.setStatus(status);
        this.updateById(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initBuiltinRules() {
        // 检查是否已初始化
        long count = this.count(new LambdaQueryWrapper<SensitiveIdentRule>()
                .eq(SensitiveIdentRule::getIsBuiltin, true));
        if (count > 0) {
            log.info("内置规则已存在，跳过初始化");
            return;
        }
        
        // 手机号规则
        createBuiltinRule("手机号", "PHONE", "REGEX", 
                "1[3-9]\\d{9}", new BigDecimal("0.95"), 1);
        
        // 身份证号规则
        createBuiltinRule("身份证号", "ID_CARD", "REGEX", 
                "[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]", 
                new BigDecimal("0.95"), 2);
        
        // 邮箱规则
        createBuiltinRule("邮箱地址", "EMAIL", "REGEX", 
                "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", 
                new BigDecimal("0.90"), 3);
        
        // 银行卡号规则
        createBuiltinRule("银行卡号", "BANK_CARD", "REGEX", 
                "[1-9]\\d{15,18}", new BigDecimal("0.85"), 4);
        
        // 姓名规则(字段名匹配)
        createBuiltinRule("姓名", "NAME", "FIELD_NAME", 
                "name,username,real_name,realname,user_name,姓名,真实姓名", 
                new BigDecimal("0.80"), 5);
        
        // 手机号字段名规则
        createBuiltinRule("手机号字段", "PHONE", "FIELD_NAME", 
                "phone,mobile,telephone,tel,手机,手机号,电话", 
                new BigDecimal("0.85"), 6);
        
        // 邮箱字段名规则
        createBuiltinRule("邮箱字段", "EMAIL", "FIELD_NAME", 
                "email,mail,邮箱,电子邮箱", 
                new BigDecimal("0.85"), 7);
        
        // 身份证字段名规则
        createBuiltinRule("身份证字段", "ID_CARD", "FIELD_NAME", 
                "id_card,idcard,id_number,idnumber,身份证,身份证号,证件号", 
                new BigDecimal("0.85"), 8);
        
        // 地址规则
        createBuiltinRule("地址", "ADDRESS", "FIELD_NAME", 
                "address,addr,地址,详细地址", 
                new BigDecimal("0.75"), 9);
        
        // 密码字段规则
        createBuiltinRule("密码", "PASSWORD", "FIELD_NAME", 
                "password,pwd,passwd,密码,口令", 
                new BigDecimal("0.90"), 10);
        
        log.info("内置规则初始化完成");
    }
    
    private void createBuiltinRule(String ruleName, String sensitiveType, String matchMode, 
                                   String matchExpression, BigDecimal confidenceWeight, Integer priority) {
        SensitiveIdentRule rule = new SensitiveIdentRule();
        rule.setRuleName(ruleName);
        rule.setSensitiveType(sensitiveType);
        rule.setMatchMode(matchMode);
        rule.setMatchExpression(matchExpression);
        rule.setConfidenceWeight(confidenceWeight);
        rule.setPriority(priority);
        rule.setIsBuiltin(true);
        rule.setStatus("ENABLED");
        this.save(rule);
    }
    
    private SensitiveIdentRuleVO toVO(SensitiveIdentRule rule) {
        SensitiveIdentRuleVO vo = new SensitiveIdentRuleVO();
        BeanUtils.copyProperties(rule, vo);
        return vo;
    }
}
