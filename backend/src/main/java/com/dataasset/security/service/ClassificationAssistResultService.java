package com.dataasset.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.ClassificationAssistResult;
import com.dataasset.security.mapper.ClassificationAssistResultMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClassificationAssistResultService extends ServiceImpl<ClassificationAssistResultMapper, ClassificationAssistResult> {

    /**
     * 分页查询结果
     */
    public Page<ClassificationAssistResult> page(int current, int size, String status, Long ruleId) {
        Page<ClassificationAssistResult> page = new Page<>(current, size);
        LambdaQueryWrapper<ClassificationAssistResult> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ClassificationAssistResult::getStatus, status);
        }
        if (ruleId != null) {
            wrapper.eq(ClassificationAssistResult::getRuleId, ruleId);
        }
        
        wrapper.orderByDesc(ClassificationAssistResult::getExecuteTime);
        return this.page(page, wrapper);
    }

    /**
     * 获取待审核结果
     */
    public List<ClassificationAssistResult> getPendingResults() {
        LambdaQueryWrapper<ClassificationAssistResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassificationAssistResult::getStatus, "PENDING");
        return this.list(wrapper);
    }

    /**
     * 批准分级建议
     */
    public void approve(Long resultId, Long reviewerId, String comment) {
        ClassificationAssistResult result = this.getById(resultId);
        if (result != null) {
            result.setStatus("APPROVED");
            result.setReviewerId(reviewerId);
            result.setReviewTime(LocalDateTime.now());
            result.setReviewComment(comment);
            this.updateById(result);
        }
    }

    /**
     * 拒绝分级建议
     */
    public void reject(Long resultId, Long reviewerId, String comment) {
        ClassificationAssistResult result = this.getById(resultId);
        if (result != null) {
            result.setStatus("REJECTED");
            result.setReviewerId(reviewerId);
            result.setReviewTime(LocalDateTime.now());
            result.setReviewComment(comment);
            this.updateById(result);
        }
    }

    /**
     * 批量审核
     */
    public void batchReview(List<Long> resultIds, String status, Long reviewerId, String comment) {
        for (Long resultId : resultIds) {
            if ("APPROVED".equals(status)) {
                approve(resultId, reviewerId, comment);
            } else {
                reject(resultId, reviewerId, comment);
            }
        }
    }
}
