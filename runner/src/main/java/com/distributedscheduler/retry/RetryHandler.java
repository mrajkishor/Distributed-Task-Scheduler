package com.distributedscheduler.retry;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.redis.RedisDelayQueueService;
import com.distributedscheduler.repository.TaskRedisRepository;
import com.distributedscheduler.util.RetryBackoffCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.distributedscheduler.dlq.DeadLetterQueueService;

@Service
public class RetryHandler {
    private static final Logger logger = LoggerFactory.getLogger(RetryHandler.class);

    private final RedisDelayQueueService delayQueue;
    private final TaskRedisRepository taskRepo;

    private final DeadLetterQueueService dlqService;


    public RetryHandler(RedisDelayQueueService delayQueue, TaskRedisRepository taskRepo, DeadLetterQueueService dlqService) {
        this.delayQueue = delayQueue;
        this.taskRepo = taskRepo;
        this.dlqService = dlqService;
    }

    public void handleRetry(Task task, String tenantId) {
        int retryCount = task.getRetryCount();
        int maxRetries = task.getMaxRetries();

        if (retryCount < maxRetries) {
            task.setRetryCount(retryCount + 1);
            task.setStatus(TaskStatus.RETRYING);

            int delay = RetryBackoffCalculator.getBackoffDelaySeconds(retryCount + 1);
            delayQueue.addTaskToDelayQueue(task.getId(), tenantId, delay);
            taskRepo.save(task);

            logger.warn("\uD83D\uDD01 Retrying task {} (Attempt {}/{}) after {}s", task.getId(), retryCount + 1, maxRetries, delay);
        } else {
            // task.setStatus(TaskStatus.FAILED);
            task.setStatus(TaskStatus.DLQ);
            task.setLog("Moved to DLQ after max retries");
            taskRepo.save(task);

            dlqService.pushToDLQ(tenantId, task); // Push to DLQ
            logger.error("📦 Task {} moved to DLQ after {} retries", task.getId(), maxRetries);
        }
    }
}
