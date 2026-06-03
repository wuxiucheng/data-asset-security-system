package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类分级辅助结果
 */
@Data
@TableName("classification_assist_result")
public class ClassificationAssistResult {

    private Long resultId;
    private Long ruleId;
    private Long assetId;
    private Long fieldId;
    
    private String matchType;
    private String matchValue;
    
    private Long originalGradingId;
    private Long suggestGradingId;
    private Integer gradingChanged;
    
    private String status;  // PENDING, APPROVED, REJECTED
    private Long reviewerId;
    private LocalDateTime reviewTime;
    private String reviewComment;
    
    private LocalDateTime executeTime;
    private String executeBatch;
    
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
