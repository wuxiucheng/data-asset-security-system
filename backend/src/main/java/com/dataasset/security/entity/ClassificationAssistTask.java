package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类分级辅助任务
 */
@Data
@TableName("classification_assist_task")
public class ClassificationAssistTask {

    private Long taskId;
    private String taskName;
    private String taskType;  // MANUAL, SCHEDULED
    
    private String scopeType;  // ALL, DATASOURCE, ASSET
    private String scopeConfig;
    private String ruleIds;
    
    private String status;  // PENDING, RUNNING, COMPLETED, FAILED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    private Integer totalCount;
    private Integer matchedCount;
    private Integer appliedCount;
    
    private String cronExpression;
    private LocalDateTime nextExecuteTime;
    
    private Long creatorId;
    private LocalDateTime createdTime;
    private Long updaterId;
    private LocalDateTime updatedTime;
    private Integer deleted;
}
