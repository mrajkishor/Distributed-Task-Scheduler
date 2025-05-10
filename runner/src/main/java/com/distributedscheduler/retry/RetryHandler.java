package com.distributedscheduler.retry;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.redis.RedisDelayQueueService;
import com.distributedscheduler.repository.TaskRedisRepository;
import com.distributedscheduler.util.RetryBackoffCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RetryHandler {
    private static final Logger logger = LoggerFactory.getLogger(RetryHandler.class);

    private final RedisDelayQueueService delayQueue;
    private final TaskRedisRepository taskRepo;

    public RetryHandler(RedisDelayQueueService delayQueue, TaskRedisRepository taskRepo) {
        this.delayQueue = delayQueue;
        this.taskRepo = taskRepo;
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

            logger.warn("Retrying task {} (Attempt {}/{}) after {}s", task.getId(), retryCount + 1, maxRetries, delay);
        } else {
            task.setStatus(TaskStatus.FAILED); // DLQ optional
            taskRepo.save(task);
            logger.error("Task {} permanently failed after {} attempts", task.getId(), maxRetries);
        }
    }
}
