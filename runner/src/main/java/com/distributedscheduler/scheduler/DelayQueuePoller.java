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
import com.distributedscheduler.lock.RedisLockService;

@Component
public class DelayQueuePoller {
    private static final Logger logger = LoggerFactory.getLogger(DelayQueuePoller.class);

    private final TaskRedisRepository taskRepo;
    private final RetryHandler retryHandler;

    private final RedisLockService lockService;

    private final RedisDelayQueueService delayQueueService;
    // Example tenant list; in production, dynamically fetch this from DB or config
    private final List<String> tenants = List.of("default", "clientA", "clientB");
    public DelayQueuePoller(RedisDelayQueueService delayQueueService,
                            TaskRedisRepository taskRepo,
                            RetryHandler retryHandler, RedisLockService lockService
    ) {
        this.delayQueueService = delayQueueService;
        this.taskRepo = taskRepo;
        this.retryHandler = retryHandler;
        this.lockService = lockService;
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
//                task.setStatus(TaskStatus.RUNNING);
//                taskRepo.save(task);

//                try {
//                    // Simulate execution logic
//                    if (task.getPayload() == null || task.getPayload().isEmpty()) {
//                        throw new RuntimeException("Missing payload");
//                    }
//
//                    task.setStatus(TaskStatus.COMPLETED);
//                    taskRepo.save(task);
//                    logger.info(" Task {} completed", taskId);
//
//                } catch (Exception e) {
//                    logger.error(" Task {} failed: {}", taskId, e.getMessage());
//                    retryHandler.handleRetry(task, tenantId);
//                }
                if (!lockService.acquireLock(taskId, 30000)) {
                    logger.info("⏳ Skipping task {}: already locked", taskId);
                    continue;
                }
                try {
                    retryHandler.handle(task); // 🔄 Internally calls taskExecutor.processTask(task)
                } catch (Exception e) {
                    logger.error(" Unexpected error while processing task {}: {}", taskId, e.getMessage());
                }

            }
        }
    }
}



/**
 * About this component
 *
 *
 * Here’s a complete example showing how the `DelayQueuePoller` works step by step in a real-world scenario:
 *
 * ---
 *
 * ### 🛒 **Use Case: Send Order Confirmation Email After Delay**
 *
 * ---
 *
 * ### 🔸 Step 1: Task Submitted
 *
 * ```json
 * POST /tasks
 * {
 *   "id": "task123",
 *   "tenantId": "default",
 *   "payload": { "email": "user@example.com", "message": "Your order is confirmed!" },
 *   "delaySeconds": 10,
 *   "maxRetries": 3
 * }
 * ```
 *
 * ➡️ This task is added to Redis ZSET with score = `now + 10`.
 *
 * ---
 *
 * ### 🔸 Step 2: `DelayQueuePoller` Runs Every Second
 *
 * It checks for all tenants:
 *
 * ```java
 * Set<String> readyTaskIds = delayQueueService.fetchDueTasks("default");
 * ```
 *
 * At 10 seconds later, it finds:
 *
 * ```
 * task123 is ready to run
 * ```
 *
 * ---
 *
 * ### 🔸 Step 3: Task Locked
 *
 * ```java
 * if (!lockService.acquireLock("task123", 30000)) return;
 * ```
 *
 * If lock is acquired, it proceeds.
 *
 * ---
 *
 * ### 🔸 Step 4: Task Loaded
 *
 * ```java
 * Task task = taskRepo.findById("default", "task123");
 * ```
 *
 * ---
 *
 * ### 🔸 Step 5: Task Executed via RetryHandler
 *
 * ```java
 * retryHandler.handle(task); // Internally calls taskExecutor.processTask(task)
 * ```
 *
 * If successful:
 *
 * * `status = COMPLETED`
 * * Notifications are sent (webhook/email)
 *
 * If failed:
 *
 * * `status = RETRYING`
 * * Re-added to ZSET with exponential delay (e.g., 2s, 4s, 8s…)
 *
 * ---
 *
 * ### 🔸 Step 6: After 3 Failures → Moved to DLQ
 *
 * If `retryCount >= maxRetries`, task is:
 *
 * * `status = DLQ`
 * * Pushed to Dead Letter Queue for inspection
 *
 * ---
 *
 * ### ✅ Summary
 *
 * The poller:
 *
 * * Detects ready tasks
 * * Locks and executes them
 * * Retries failed ones
 * * Moves unfixable ones to DLQ
 *
 * All while ensuring **distributed safety and resilience**.
 *
 *
 * **/