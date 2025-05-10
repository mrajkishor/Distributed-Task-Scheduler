package com.distributedscheduler.scheduler;

import com.distributedscheduler.redis.RedisDelayQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.List;
import java.time.Instant;


import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.repository.TaskRedisRepository;
import com.distributedscheduler.retry.RetryHandler;

@Component
public class DelayQueuePoller {
    private static final Logger logger = LoggerFactory.getLogger(DelayQueuePoller.class);

    private final TaskRedisRepository taskRepo;
    private final RetryHandler retryHandler;

    private final RedisDelayQueueService delayQueueService;
    // Example tenant list; in production, dynamically fetch this from DB or config
    private final List<String> tenants = List.of("default", "clientA", "clientB");
    public DelayQueuePoller(RedisDelayQueueService delayQueueService,
                            TaskRedisRepository taskRepo,
                            RetryHandler retryHandler
                            ) {
        this.delayQueueService = delayQueueService;
        this.taskRepo = taskRepo;
        this.retryHandler = retryHandler;
    }




    @Scheduled(fixedRate = 1000) // every second
    public void poll() {
        long now = Instant.now().getEpochSecond();
        for (String tenantId : tenants) {
            Set<String> readyTaskIds = delayQueueService.fetchDueTasks(tenantId);

            for (String taskId : readyTaskIds) {
                logger.info("Task ready to run: {} (tenant: {})", taskId, tenantId);
                delayQueueService.removeTask(taskId, tenantId);

                Task task = taskRepo.findById(tenantId, taskId);

                if (task == null) {
                    logger.warn(" Task {} not found in Redis.", taskId);
                    continue;
                }

                logger.info(" Executing task {} (tenant: {})", taskId, tenantId);
                task.setStatus(TaskStatus.RUNNING);
                taskRepo.save(task);

                try {
                    // Simulate execution logic
                    if (task.getPayload() == null || task.getPayload().isEmpty()) {
                        throw new RuntimeException("Missing payload");
                    }

                    task.setStatus(TaskStatus.COMPLETED);
                    taskRepo.save(task);
                    logger.info(" Task {} completed", taskId);

                } catch (Exception e) {
                    logger.error(" Task {} failed: {}", taskId, e.getMessage());
                    retryHandler.handleRetry(task, tenantId);
                }

            }
        }
    }
}
