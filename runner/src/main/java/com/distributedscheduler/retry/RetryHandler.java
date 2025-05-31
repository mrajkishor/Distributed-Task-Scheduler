package com.distributedscheduler.retry;

import com.distributedscheduler.consumer.TaskExecutor;
import com.distributedscheduler.metrics.PrometheusMetricsCollector;
import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.redis.RedisDelayQueueService;
import com.distributedscheduler.repository.TaskRedisRepository;
import com.distributedscheduler.util.RetryBackoffCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.distributedscheduler.dlq.DeadLetterQueueService;

@Service
public class RetryHandler {
    private static final Logger logger = LoggerFactory.getLogger(RetryHandler.class);

    private final RedisDelayQueueService delayQueue;
    private final TaskRedisRepository taskRepo;

    private final DeadLetterQueueService dlqService;

    private final TaskExecutor taskExecutor;

    private final PrometheusMetricsCollector metrics;

    public RetryHandler(RedisDelayQueueService delayQueue, TaskRedisRepository taskRepo, DeadLetterQueueService dlqService, TaskExecutor taskExecutor, PrometheusMetricsCollector metrics) {
        this.delayQueue = delayQueue;
        this.taskRepo = taskRepo;
        this.dlqService = dlqService;
        this.taskExecutor = taskExecutor;
        this.metrics = metrics;
    }

    public void handleRetry(Task task, String tenantId) {
        int retryCount = task.getRetryCount();
        int maxRetries = task.getMaxRetries();

        if (retryCount < maxRetries) {
            task.setRetryCount(retryCount + 1);
            task.setStatus(TaskStatus.RETRYING);

            metrics.recordRetry();
            metrics.recordRetryAttempt(retryCount + 1);
            logger.warn("🔁 Retrying task {}, workerId=worker-1");

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

    public void handle(Task task) {
        try {
            logger.info("🚀 Executing task {}", task.getId());
            taskExecutor.processTask(task);
            taskRepo.save(task); // ✅ Persist status change after execution
        } catch (Exception e) {
            logger.error("❌ Execution failed for task {}: {}", task.getId(), e.getMessage());
            handleRetry(task, task.getTenantId());
        }
    }
}


/***
 * About this component
 *
 *The `RetryHandler` class handles **automatic retries** for failed tasks in your distributed scheduler. Here's a breakdown:
 *
 * ---
 *
 * ### ✅ **Key Responsibilities**
 *
 * #### 1. `handle(Task task)`
 *
 * * Executes the task using `TaskExecutor`.
 * * On **exception**, triggers retry logic via `handleRetry(...)`.
 *
 * #### 2. `handleRetry(Task task, String tenantId)`
 *
 * * If `retryCount < maxRetries`:
 *
 *   * Increments retry count.
 *   * Sets status to `RETRYING`.
 *   * Adds task to Redis ZSET delay queue using exponential backoff delay.
 *   * Saves updated task.
 * * Else (retry limit exceeded):
 *
 *   * Moves task to **DLQ**.
 *   * Sets status `DLQ`, logs the failure.
 *   * Calls `dlqService.pushToDLQ(...)`.
 *
 * ---
 *
 * ### 🧠 **Used Concepts**
 *
 * * **Exponential backoff**: Calculated via `RetryBackoffCalculator.getBackoffDelaySeconds(retryCount + 1)`.
 * * **Delayed retry**: Implemented using `RedisDelayQueueService`.
 * * **DLQ fallback**: After max retries, task is marked DLQ and stored for post-mortem/debugging.
 *
 * ---
 *
 * ### 🔄 Retry Flow Example:
 *
 * ```text
 * Run → Fail → Retry (1) → Retry (2) → Retry (3) → DLQ
 * ```
 *
 * ---
 *
 * ### 🔧 Dependencies Injected:
 *
 * * `RedisDelayQueueService` → schedules retry delays.
 * * `TaskRedisRepository` → stores/retrieves task state.
 * * `DeadLetterQueueService` → stores failed tasks after max retries.
 * * `TaskExecutor` → executes the actual task logic.
 *
 * ---
 *
 * This class ensures fault tolerance by retrying transient failures and isolating permanently failed tasks.
 *
 *
 * **/