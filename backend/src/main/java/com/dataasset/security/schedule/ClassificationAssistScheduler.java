package com.dataasset.security.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataasset.security.entity.ClassificationAssistTask;
import com.dataasset.security.service.ClassificationAssistTaskService;
import com.dataasset.security.service.TaskExecutionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务调度器
 */
@Component
public class ClassificationAssistScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ClassificationAssistScheduler.class);

    @Autowired
    private ClassificationAssistTaskService taskService;

    @Autowired
    private TaskExecutionEngine executionEngine;

    /**
     * 每5分钟检查一次定时任务
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkScheduledTasks() {
        logger.debug("检查定时任务...");

        // 查询需要执行的定时任务
        LambdaQueryWrapper<ClassificationAssistTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassificationAssistTask::getTaskType, "SCHEDULED")
               .eq(ClassificationAssistTask::getStatus, "PENDING")
               .eq(ClassificationAssistTask::getDeleted, 0)
               .isNotNull(ClassificationAssistTask::getNextExecuteTime)
               .le(ClassificationAssistTask::getNextExecuteTime, LocalDateTime.now());

        List<ClassificationAssistTask> tasks = taskService.list(wrapper);

        for (ClassificationAssistTask task : tasks) {
            logger.info("执行定时任务: {}", task.getTaskName());
            executionEngine.executeTask(task.getTaskId());
        }
    }

    /**
     * 每天凌晨2点清理过期数据
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldData() {
        logger.info("开始清理过期数据...");
        // TODO: 实现数据清理逻辑
        // 1. 清理30天前的执行结果
        // 2. 清理已取消的任务
    }
}
