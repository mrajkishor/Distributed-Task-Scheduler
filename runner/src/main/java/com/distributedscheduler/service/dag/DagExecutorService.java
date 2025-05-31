
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


/***
 *
 * The `DagExecutorService` component is responsible for **executing tasks in a Directed Acyclic Graph (DAG) order** while respecting dependencies, delays, and priorities.
 *
 * ---
 *
 * ### ✅ **Primary Role:**
 *
 * To **traverse and execute tasks** submitted by a tenant in a correct topological (dependency-respecting) order.
 *
 * ---
 *
 * ### 🔧 **How it works:**
 *
 * 1. **Fetch all tasks** for a given `tenantId` from Redis.
 * 2. **Create a task map** (taskId → Task object) for quick access.
 * 3. **Get dependency map** (taskId → List of dependent taskIds).
 * 4. **Topologically sort tasks** using `DagUtils.topologicalSort(...)`.
 * 5. **Sort tasks by priority** (higher priority first).
 * 6. **Iterate over each task**:
 *
 *    * Skip if already completed.
 *    * Skip if dependencies are not completed.
 *    * Skip if `delaySeconds` hasn't passed.
 *    * Otherwise, **execute the task using `taskRunner.run(task)`**.
 *
 * ---
 *
 * ### 🧠 **Why it’s important:**
 *
 * It ensures that:
 *
 * * **No task runs before its dependencies** are completed.
 * * **Delay-based scheduling** is respected.
 * * **Priority-based task execution** is honored.
 *
 * ---
 *
 * ### 🧪 Example:
 *
 * If you submit tasks like:
 *
 * ```
 * A → B → C (dependencies)
 * A, B, C = Tasks with increasing delay or priority
 * ```
 *
 * It will:
 *
 * * Run A first,
 * * Wait for A to complete,
 * * Then run B,
 * * Then C, all in **correct dependency order**.
 *
 * ---
 *
 * ### 🔁 Related:
 *
 * * Depends on: `TaskRedisRepository`, `TaskRunner`, and `DagUtils`
 * * Can be triggered periodically or on new DAG submission.
 *
 * ---
 *
 * ### Summary:
 *
 * 🔗 `DagExecutorService` is the **DAG-aware scheduler** that executes ready tasks based on their **dependencies**, **delay**, and **priority**, ensuring correctness in complex task orchestration.
 *
 *
 *
 * ***/
