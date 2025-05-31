package com.distributedscheduler.consumer;

import com.distributedscheduler.metrics.PrometheusMetricsCollector;
import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.notification.NotificationService;
import com.distributedscheduler.repository.TaskRedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TaskExecutor {
    private final TaskRedisRepository taskRedisRepository;

    private final NotificationService notificationService;
    private final PrometheusMetricsCollector metrics;
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutor.class);

    public TaskExecutor(TaskRedisRepository taskRedisRepository, NotificationService notificationService, PrometheusMetricsCollector metrics) {
        this.taskRedisRepository = taskRedisRepository;
        this.notificationService = notificationService;
        this.metrics = metrics;
    }

    // private static final Logger logger =
    // LoggerFactory.getLogger(TaskExecutor.class);

    // public void processTask(Task task) {
    // try {
    // task.setStatus(TaskStatus.RUNNING);
    // log(task, "Task started at " + LocalDateTime.now());
    //
    // // 🔧 Simulate task logic (replace this with your real logic)
    // simulateTaskExecution(task);
    //
    // task.setStatus(TaskStatus.COMPLETED);
    // log(task, "Task completed successfully at " + LocalDateTime.now());
    // notificationService.notify(task); // Notify on completion
    //
    // } catch (Exception e) {
    // task.setRetryCount(task.getRetryCount() + 1);
    // log(task, "Failed at " + LocalDateTime.now() + ": " + e.getMessage());
    //
    // if (task.getRetryCount() >= task.getMaxRetries()) {
    // task.setStatus(TaskStatus.FAILED);
    // log(task, "Max retries reached. Task marked as FAILED.");
    // notificationService.notify(task); // ✅ Notify on failure
    // } else {
    // task.setStatus(TaskStatus.PENDING); // Set to PENDING for retry queue
    // log(task, "Retrying task. Retry count: " + task.getRetryCount());
    //
    // // Requeue logic (to Kafka/RabbitMQ)
    // requeueTask(task);
    // }
    // }
    //
    // // 💾 Persist updated task in Redis or DB
    // updateTaskInRedis(task);
    // }

    public void processTask(Task task) throws Exception { // let retry handler do the retry logic
        long start = System.currentTimeMillis(); // ⏱️ Track start time




        String taskId = task.getId();
        if (taskId == null && task.getName() != null) {
            String resolved = taskRedisRepository.getTaskIdByName(task.getTenantId(), task.getName());
            if (resolved != null) {
                taskId = resolved;
            }
        }
        List<String> deps = (taskId != null)
                ? taskRedisRepository.getDependencies(task.getTenantId(), taskId)
                : List.of();
        logger.info("Task ID: {}, Name: {}, Dependencies: {}", taskId, task.getName(), deps);

        String taskType = deps == null || deps.isEmpty() ? "independent" : "dag";

        try {
            task.setStatus(TaskStatus.RUNNING);
            simulateTaskExecution(task);

            task.setStatus(TaskStatus.COMPLETED);
            metrics.recordSuccess(); // ✅ Prometheus counter

        } catch (Exception e) {
            task.setStatus(TaskStatus.FAILED); // Let RetryHandler decide if DLQ is needed
            metrics.recordFailure(); // ✅ Prometheus counter
            throw e; // Let RetryHandler handle it
        } finally {
            long duration = System.currentTimeMillis() - start;
            metrics.recordExecutionTime(taskType, duration); // ⏱️ tagged with "independent" or "dag"
            notificationService.notify(task); // notify success/failure
        }
    }

    private void simulateTaskExecution(Task task) throws Exception {


        // DLQ test snippet start
        if (task.getPayload().containsKey("fail") && Boolean.TRUE.equals(task.getPayload().get("fail"))) {
            throw new RuntimeException("Forced failure for DLQ test");
        }

//        payload:
//        {
//            "name": "AlwaysFailTask",
//             "payload": { "fail": true },
//            "delaySeconds": 0,
//                "maxRetries": 2,
//                "tenantId": "testTenant",
//                "idempotencyKey": "fail-dlq-test-001"
//        }




        // DLQ test snippet end



        System.out.println("✅ Simulated execution for task: " + task.getId());
        Thread.sleep(1000);
        // Simulate success or failure randomly
        // if (Math.random() < 0.5) {
        // throw new RuntimeException("Simulated failure.");
        // }

        // Replace simulated execution with actual logic
        // log(task, "Executing task payload: " + task.getPayload());
        // Thread.sleep(1000); // Simulate execution time
        // Add more logic here based on task.getPayload()

        // For example: Task calls an external service based on the payload:
        // String endpoint = (String) task.getPayload().get("url");
        // HttpClient client = HttpClient.newHttpClient();
        // HttpRequest request = HttpRequest.newBuilder()
        // .uri(URI.create(endpoint))
        // .GET()
        // .build();
        // HttpResponse<String> response = client.send(request,
        // HttpResponse.BodyHandlers.ofString());
        // log(task, "External call response: " + response.body());

    }

    private void requeueTask(Task task) {
        // TODO: Implement Kafka/RabbitMQ producer logic
        // logger.info("Requeued task for retry: {}", task.getId());

        // about this method
        // requeueTask(task) method is meant to put the failed task back into the queue
        // for another execution attempt later — this supports retry mechanism and fault
        // tolerance.

        // 💡 Real-world use of requeueTask(task):
        // It should:
        // 1. Serialize the task object.
        // 2. Send it back to a queue (like Kafka, RabbitMQ, or Redis) with a delay or
        // exponential backoff.
        // 3. Be picked up again by the task consumer/scheduler for reprocessing.

        // Example (Kafka-based):
        // kafkaTemplate.send("task-retry-topic", task.getId(), task);

        // Example (Redis ZSET with delay):
        // redisTemplate.opsForZSet().add("retryQueue", task, System.currentTimeMillis()
        // + delayMillis);

        // requeueTask ensures that temporary failures don’t cause task loss — it
        // retries later based on retry count and max limit.

        // Post task requeue, what happens?
        // After a task is requeued into the Redis ZSET with a new score (`now +
        // delaySeconds`), it will be picked up and executed by your
        // **TaskWorkerScheduler** (or similar scheduler/consumer), which periodically:
        //
        // 1. **Scans the ZSET** for tasks whose score (timestamp) ≤ current time.
        // 2. **Leases the task** using `SETNX` to avoid duplication.
        // 3. **Processes it** using the `TaskExecutor`.
        //
        // So, the **scheduler loop or thread** is responsible for polling ZSET and
        // triggering eligible task execution.

        // How requed task is run ?
        // The **requeued task** is eventually picked up by your scheduler and passed
        // again to the `processTask()` method in `TaskExecutor`, which handles its
        // execution, retry logic, status update, and notification.
    }

    private void updateTaskInRedis(Task task) {
        // TODO: Implement Redis logic using Jedis or Redisson
        // logger.info("Updated task in Redis: {}", task.getId());

        // About this method

        /*****
         *
         *
         * The method `updateTaskInRedis(task)` is meant to **persist the latest state
         * of the task back to Redis** after execution (whether success, failure, or
         * retry).
         *
         * ---
         *
         * ### ✅ **Purpose:**
         *
         * To **store or update** the task's status, retry count, logs, etc. in Redis,
         * so that:
         *
         * * You can later query task info (via `/tasks/{id}`)
         * * The scheduler or UI knows the latest state
         * * The data survives between retries
         *
         * ---
         *
         * ### 🔧 **What it should ideally do:**
         *
         * ```java
         * redisTemplate.opsForHash().put("tasks", task.getId(), task);
         * ```
         *
         * or
         *
         * ```java
         * redisTemplate.opsForValue().set("task:" + task.getId(), task);
         * ```
         *
         * ---
         *
         * ### 🧠 Summary:
         *
         * `updateTaskInRedis()` ensures **task state is not lost** and is **queryable
         * or retryable** with latest info. Without it, your task status wouldn't be
         * tracked persistently.
         *
         *
         *
         *
         ***/

    }

    private void log(Task task, String message) {
        task.getExecutionLogs().add(message);
        logger.info("[{}] {}", task.getId(), message);
    }

    // Complete flow of retry mechanism

    // You're almost right, but let me correct and clarify the full flow and the
    // **role of `delaySeconds`**:
    //
    // ---
    //
    // ### ✅ **Full Retry Flow (with Delay):**
    //
    // 1. **Initial Attempt:**
    //
    // * You submit a task → goes into Redis ZSET with `delaySeconds`.
    // * Scheduler picks it when time has passed → calls `processTask()`.
    // * Inside `simulateTaskExecution()`, task fails (e.g., DB connection error).
    //
    // 2. **On Failure:**
    //
    // * `retryCount++`
    // * If `retryCount < maxRetries`, set `status = PENDING`
    // * Requeue it again into Redis ZSET **with delay (e.g., 10s)**.
    //
    // 3. **Next Retry:**
    //
    // * After delay passes, scheduler again picks it → runs `processTask()` → fails
    // again → repeat.
    //
    // 4. **Final Retry (retryCount == maxRetries):**
    //
    // * On failure, task is **marked as `FAILED`**.
    // * Notification is sent.
    // * Task is **not queued again**.
    //
    // ---
    //
    // ### 🔁 **Where You’re Slightly Off:**
    //
    // * At final retry, status is set to `FAILED`, **not `PENDING`**.
    // * `PENDING` is only for retry attempts before the final one.
    //
    // ---
    //
    // ### 🕒 **Why `delaySeconds` is Important:**
    //
    // * To **throttle retries** (not retry immediately).
    // * Helps **avoid hammering a failing system** (e.g., DB down).
    // * Allows time to recover between retries.
    //
    // This is especially useful in **transient failures**.
    //
    // ---
    //
    // ### ✅ So, Final Behavior:
    //
    // * Retry 3 times → delay in between → then `FAILED`.
    // * Each time, task re-enters ZSET with the new delay → picked again →
    // processed by `processTask()`.
    //
    // Let me know if you want a visual DAG or sequence chart.

}
