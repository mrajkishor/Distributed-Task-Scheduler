package com.distributedscheduler.service.dag;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.repository.TaskRedisRepository;
import com.distributedscheduler.util.DagUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class DagExecutorService {

    private final TaskRedisRepository taskRepository;
    private final TaskRunner taskRunner;

    public DagExecutorService(TaskRedisRepository taskRepository, TaskRunner taskRunner) {
        this.taskRepository = taskRepository;
        this.taskRunner = taskRunner;
    }

    public void executeDag(String tenantId) {
        List<Task> allTasks = taskRepository.findAllByTenantId(tenantId);
        Map<String, Task> taskMap = new HashMap<>();
        for (Task task : allTasks) {
            taskMap.put(task.getId(), task);
        }

        List<Task> sortedTasks = DagUtils.topologicalSort(allTasks);

        // Sort by priority (descending) among tasks that are ready
        sortedTasks.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));

        for (Task task : sortedTasks) {
            if (task.getStatus() == TaskStatus.COMPLETED) {
                System.out.println("⏩ Task already completed: " + task.getId());
                continue;
            }

            if (!areDependenciesMet(task, taskMap)) {
                System.out.println("⏳ Skipping task (dependencies not met): " + task.getId());
                continue;
            }

            long createdTime = task.getCreatedAt();
            if ((createdTime + task.getDelaySeconds()) > Instant.now().getEpochSecond()) {
                System.out.println("🕒 Delay not passed for task: " + task.getId());
                continue;
            }

            taskRunner.run(task);
        }
    }

    private boolean areDependenciesMet(Task task, Map<String, Task> taskMap) {
        for (String depId : task.getDependencies()) {
            Task dep = taskMap.get(depId);
            if (dep == null || dep.getStatus() != TaskStatus.COMPLETED) {
                return false;
            }
        }
        return true;
    }


}