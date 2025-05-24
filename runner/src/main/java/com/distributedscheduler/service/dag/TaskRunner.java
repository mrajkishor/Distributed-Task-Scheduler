package com.distributedscheduler.service.dag;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.repository.TaskRedisRepository;
import com.distributedscheduler.lock.RedisLockService;
import org.springframework.stereotype.Service;

@Service
public class TaskRunner {

    private final TaskRedisRepository taskRepository;
    private final RedisLockService lockService;

    public TaskRunner(TaskRedisRepository taskRepository, RedisLockService lockService) {
        this.taskRepository = taskRepository;
        this.lockService = lockService;
    }

    public void run(Task task) {
        String taskId = task.getId();
        String tenantId = task.getTenantId();

        if (!lockService.acquireLock(taskId, 60000)) {
            System.out.println("🔒 Task already locked or in progress: " + taskId);
            return;
        }

        try {
            if (task.getRetryCount() >= task.getMaxRetries()) {
                System.out.println("🚫 Max retries reached. Moving to DLQ: " + taskId);
                task.setStatus(TaskStatus.DLQ);
                taskRepository.updateTaskStatus(tenantId, taskId, TaskStatus.DLQ);
                return;
            }

            System.out.println("🚀 Executing task: " + taskId);
            task.setStatus(TaskStatus.RETRYING);
            taskRepository.updateTaskStatus(tenantId, taskId, TaskStatus.RETRYING);

            Thread.sleep(300); // simulate processing time

            task.setStatus(TaskStatus.COMPLETED);
            taskRepository.updateTaskStatus(tenantId, taskId, TaskStatus.COMPLETED);
            System.out.println("✅ Task succeeded: " + taskId);

        } catch (Exception e) {
            System.out.println("❌ Task execution failed: " + taskId);
            task.setRetryCount(task.getRetryCount() + 1);
            task.setStatus(TaskStatus.FAILED);
            taskRepository.updateTaskStatus(tenantId, taskId, TaskStatus.FAILED);
        } finally {
            lockService.releaseLock(taskId);
        }
    }
}
