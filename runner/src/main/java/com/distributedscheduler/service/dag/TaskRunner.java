package com.distributedscheduler.service.dag;


import com.distributedscheduler.model.Task;
import com.distributedscheduler.repository.TaskRedisRepository;
import org.springframework.stereotype.Service;
import com.distributedscheduler.model.TaskStatus;

@Service
public class TaskRunner {

    private final TaskRedisRepository taskRepository;

    public TaskRunner(TaskRedisRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void run(Task task) {

        if (task.getRetryCount() >= task.getMaxRetries()) {
            System.out.println("🚫 Max retries reached. Moving to DLQ: " + task.getId());
            task.setStatus(TaskStatus.DLQ);
            taskRepository.updateTaskStatus(task.getTenantId(), task.getId(), TaskStatus.DLQ);
            return;
        }

        try {
            System.out.println("🚀 Executing task: " + task.getId());
            task.setStatus(TaskStatus.RETRYING);
            taskRepository.updateTaskStatus(task.getTenantId(), task.getId(), TaskStatus.RETRYING);

            // Simulated execution logic
            Thread.sleep(300); // simulate processing time

            task.setStatus(TaskStatus.COMPLETED);
            taskRepository.updateTaskStatus(task.getTenantId(), task.getId(), TaskStatus.COMPLETED);
            System.out.println("✅ Task succeeded: " + task.getId());

        } catch (Exception e) {
            System.out.println("❌ Task execution failed: " + task.getId());
            task.setRetryCount(task.getRetryCount() + 1);
            task.setStatus(TaskStatus.FAILED);
            taskRepository.updateTaskStatus(task.getTenantId(), task.getId(), TaskStatus.FAILED);
        }
    }
}
