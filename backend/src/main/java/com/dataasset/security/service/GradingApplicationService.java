package com.dataasset.security.service;

import com.dataasset.security.entity.ClassificationAssistResult;
import com.dataasset.security.entity.DataField;
import com.dataasset.security.mapper.DataFieldMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分级应用服务
 */
@Service
public class GradingApplicationService {

    @Autowired
    private ClassificationAssistResultService resultService;

    @Autowired
    private DataFieldMapper fieldMapper;

    /**
     * 应用分级到字段
     */
    public boolean applyGrading(Long resultId) {
        ClassificationAssistResult result = resultService.getById(resultId);
        if (result == null || !"APPROVED".equals(result.getStatus())) {
            return false;
        }

        // 更新字段分级
        DataField field = fieldMapper.selectById(result.getFieldId());
        if (field != null) {
            field.setGradingId(result.getSuggestGradingId());
            field.setUpdatedTime(LocalDateTime.now());
            fieldMapper.updateById(field);

            return true;
        }
        return false;
    }

    /**
     * 批量应用分级
     */
    public int batchApplyGrading(List<Long> resultIds) {
        int count = 0;
        for (Long resultId : resultIds) {
            if (applyGrading(resultId)) {
                count++;
            }
        }
        return count;
    }
}
