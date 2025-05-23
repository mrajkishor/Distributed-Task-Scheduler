
package com.distributedscheduler.service.dag;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.repository.TaskRedisRepository;
import com.distributedscheduler.util.DagUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

        Map<String, List<String>> dependencyMap = taskRepository.getAllDependenciesMap(tenantId);
        List<Task> sortedTasks = DagUtils.topologicalSort(allTasks, dependencyMap);
        sortedTasks.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));

        for (Task task : sortedTasks) {
            if (task.getStatus() == TaskStatus.COMPLETED) continue;
            if (!areDependenciesMet(task, taskMap, dependencyMap)) continue;
            if ((task.getCreatedAt() + task.getDelaySeconds()) > Instant.now().getEpochSecond()) continue;

            taskRunner.run(task);
        }
    }


    private boolean areDependenciesMet(Task task, Map<String, Task> taskMap, Map<String, List<String>> dependencyMap) {
        for (String depId : dependencyMap.getOrDefault(task.getId(), Collections.emptyList())) {
            Task dep = taskMap.get(depId);
            if (dep == null || dep.getStatus() != TaskStatus.COMPLETED) return false;
        }
        return true;
    }
}
