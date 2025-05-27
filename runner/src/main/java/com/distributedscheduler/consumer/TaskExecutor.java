package com.distributedscheduler.consumer;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Component
public class TaskExecutor {

    private final NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(TaskExecutor.class);


    public TaskExecutor(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // private static final Logger logger =
    // LoggerFactory.getLogger(TaskExecutor.class);

    public void processTask(Task task) {
        try {
            task.setStatus(TaskStatus.RUNNING);
            log(task, "Task started at " + LocalDateTime.now());

            // 🔧 Simulate task logic (replace this with your real logic)
            // simulateTaskExecution(task);

            task.setStatus(TaskStatus.COMPLETED);
            log(task, "Task completed successfully at " + LocalDateTime.now());
            notificationService.notify(task); // ✅ Notify on completion

        } catch (Exception e) {
            task.setRetryCount(task.getRetryCount() + 1);
            log(task, "Failed at " + LocalDateTime.now() + ": " + e.getMessage());

            if (task.getRetryCount() >= task.getMaxRetries()) {
                task.setStatus(TaskStatus.FAILED);
                log(task, "Max retries reached. Task marked as FAILED.");
                notificationService.notify(task); // ✅ Notify on failure
            } else {
                task.setStatus(TaskStatus.PENDING); // Set to PENDING for retry queue
                log(task, "Retrying task. Retry count: " + task.getRetryCount());

                // Requeue logic (to Kafka/RabbitMQ)
                requeueTask(task);
            }
        }

        // 💾 Persist updated task in Redis or DB
        updateTaskInRedis(task);
    }

    private void simulateTaskExecution(Task task) throws Exception {
        // Simulate success or failure randomly
        if (Math.random() < 0.5) {
            throw new RuntimeException("Simulated failure.");
        }
    }

    private void requeueTask(Task task) {
        // TODO: Implement Kafka/RabbitMQ producer logic
        // logger.info("Requeued task for retry: {}", task.getId());
    }

    private void updateTaskInRedis(Task task) {
        // TODO: Implement Redis logic using Jedis or Redisson
        // logger.info("Updated task in Redis: {}", task.getId());
    }

    private void log(Task task, String message) {
        task.getExecutionLogs().add(message);
        logger.info("[{}] {}", task.getId(), message);
    }
}
