package com.distributedscheduler.service.dag;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.util.DagUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DAGValidationService {

    /** ===> validate DAG integrity using Kahn’s Algorithm before storing dependencies.
     * Validates that adding the given task and its dependencies does not introduce a cycle.
     *
     * @param allTasks       All tasks in the system (including the new one).
     * @param dependencyMap  taskId → list of dependency taskIds
     * @throws IllegalStateException if a cycle is detected
     */
    public void validateNoCycle(List<Task> allTasks, Map<String, List<String>> dependencyMap) {
        boolean hasCycle = DagUtils.hasCycle(allTasks, dependencyMap);
        if (hasCycle) {
            throw new IllegalStateException("🚫 Cycle detected in DAG – operation rejected.");
        }
    }
}
