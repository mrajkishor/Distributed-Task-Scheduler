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


/**
 * About this component
 *
 *
 * The `DAGValidationService` is a **safety mechanism** that ensures your Directed Acyclic Graph (DAG) of tasks **does not contain cycles** before storing or executing tasks.
 *
 * ---
 *
 * ### ✅ **Purpose:**
 * To **validate task dependencies** using **Kahn's Algorithm** and **prevent cycles**, which would otherwise cause infinite loops or invalid execution order.
 *
 * ---
 *
 * ### 🔧 **How it works:**
 * - It receives:
 *   - `List<Task> allTasks` – all current tasks in the system.
 *   - `Map<String, List<String>> dependencyMap` – a map showing dependencies for each task.
 * - Calls `DagUtils.hasCycle(...)` to check for any **cyclic dependency**.
 * - If a **cycle is detected**, it throws an `IllegalStateException`.
 *
 * ---
 *
 * ### 🧪 Example:
 * Imagine you try to submit a task with this dependency:
 *
 * ```
 * A → B → C → A ❌ (cycle)
 * ```
 *
 * Then `validateNoCycle(...)` will:
 * - Detect the loop,
 * - Throw an exception,
 * - **Prevent the update**, ensuring your DAG stays valid.
 *
 * ---
 *
 * ### Summary:
 * 🛡️ `DAGValidationService` **protects the integrity** of your task graph by **blocking circular dependencies**, ensuring your system stays **stable, deterministic, and executable**.
 *
 *
 * **/