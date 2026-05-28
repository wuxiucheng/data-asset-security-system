package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.MaskStrategy;
import com.dataasset.security.mapper.MaskStrategyMapper;
import com.dataasset.security.service.MaskStrategyService;
import com.dataasset.security.utils.MaskAlgorithmUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 脱敏策略Service实现
 */
@Slf4j
@Service
public class MaskStrategyServiceImpl extends ServiceImpl<MaskStrategyMapper, MaskStrategy> implements MaskStrategyService {

    @Override
    public MaskStrategy getBySensitiveType(String sensitiveType) {
        return this.getOne(new LambdaQueryWrapper<MaskStrategy>()
                .eq(MaskStrategy::getSensitiveType, sensitiveType)
                .eq(MaskStrategy::getStatus, "ENABLED"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initDefaultStrategies() {
        // 检查是否已初始化
        long count = this.count();
        if (count > 0) {
            log.info("脱敏策略已存在，跳过初始化");
            return;
        }

        // 手机号脱敏策略
        createStrategy("PHONE", "MASK", 
                "{\"start\":3,\"end\":7,\"maskChar\":\"*\"}", 
                "手机号掩码脱敏(保留前3后4)");

        // 身份证号脱敏策略
        createStrategy("ID_CARD", "MASK", 
                "{\"start\":6,\"end\":14,\"maskChar\":\"*\"}", 
                "身份证号掩码脱敏(保留前6后4)");

        // 邮箱脱敏策略
        createStrategy("EMAIL", "MASK", 
                "{\"start\":3,\"end\":\"@\",\"maskChar\":\"*\"}", 
                "邮箱掩码脱敏(保留前3和域名)");

        // 银行卡号脱敏策略
        createStrategy("BANK_CARD", "MASK", 
                "{\"start\":4,\"end\":-4,\"maskChar\":\"*\"}", 
                "银行卡号掩码脱敏(保留前4后4)");

        // 姓名脱敏策略
        createStrategy("NAME", "MASK", 
                "{\"start\":1,\"end\":\"end\",\"maskChar\":\"*\"}", 
                "姓名掩码脱敏(保留姓)");

        // 地址脱敏策略
        createStrategy("ADDRESS", "TRUNCATE", 
                "{\"length\":10}", 
                "地址截断脱敏");

        // 密码脱敏策略
        createStrategy("PASSWORD", "REPLACE", 
                "{\"replacement\":\"******\"}", 
                "密码替换脱敏");

        log.info("默认脱敏策略初始化完成");
    }

    @Override
    public String applyMask(String value, String sensitiveType, String algorithm, String params) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        try {
            switch (algorithm) {
                case "MASK":
                    return MaskAlgorithmUtil.maskPhone(value);
                case "REPLACE":
                    return MaskAlgorithmUtil.replace(value, "******");
                case "HASH":
                    return MaskAlgorithmUtil.hashMD5(value);
                case "TRUNCATE":
                    return MaskAlgorithmUtil.truncate(value, 10);
                case "SHUFFLE":
                    return MaskAlgorithmUtil.shuffle(value);
                default:
                    return value;
            }
        } catch (Exception e) {
            log.error("脱敏失败", e);
            return value;
        }
    }

    private void createStrategy(String sensitiveType, String algorithm, 
                               String params, String description) {
        MaskStrategy strategy = new MaskStrategy();
        strategy.setSensitiveType(sensitiveType);
        strategy.setAlgorithm(algorithm);
        strategy.setAlgorithmParams(params);
        strategy.setDescription(description);
        strategy.setStatus("ENABLED");
        this.save(strategy);
    }
}
