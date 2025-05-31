package com.distributedscheduler.util;

import com.distributedscheduler.model.Task;

import java.util.*;
import java.util.stream.Collectors;

public class DagUtils {

    public static List<Task> topologicalSort(List<Task> tasks, Map<String, List<String>> dependencyMap) {
        Map<String, Task> taskMap = tasks.stream().collect(Collectors.toMap(Task::getId, t -> t));
        Map<String, List<String>> adjList = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        // Initialize
        for (Task task : tasks) {
            adjList.putIfAbsent(task.getId(), new ArrayList<>());
            inDegree.putIfAbsent(task.getId(), 0);
        }

        // Build graph using external dependency map
        for (Task task : tasks) {
            List<String> deps = dependencyMap.getOrDefault(task.getId(), Collections.emptyList());
            for (String dep : deps) {
                if (!taskMap.containsKey(dep)) {
                    throw new IllegalArgumentException("Dependency '" + dep + "' not found in task list.");
                }
                adjList.get(dep).add(task.getId());
                inDegree.put(task.getId(), inDegree.getOrDefault(task.getId(), 0) + 1);
            }
        }

        // Topological sort (Kahn's algorithm)
        Queue<String> queue = new LinkedList<>();
        for (String taskId : inDegree.keySet()) {
            if (inDegree.get(taskId) == 0) {
                queue.add(taskId);
            }
        }

        List<Task> sorted = new ArrayList<>();
        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            sorted.add(taskMap.get(currentId));

            for (String neighbor : adjList.getOrDefault(currentId, new ArrayList<>())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (sorted.size() != tasks.size()) {
            throw new IllegalStateException("Cycle detected in task dependencies!");
        }

        return sorted;
    }


    public static boolean hasCycle(List<Task> tasks, Map<String, List<String>> dependencyMap) {
        try {
            System.out.println("🧠 Checking cycle in tasks: " + tasks.stream().map(Task::getId).toList());
            topologicalSort(tasks, dependencyMap);
            return false;
        } catch (IllegalStateException e) {
            return true;
        }
    }

}


/**
 * About this component
 *
 *The `DagUtils` class provides **utilities for working with Directed Acyclic Graphs (DAGs)**, specifically to:
 *
 * ---
 *
 * ### 🔁 1. **Topologically Sort Tasks**
 *
 * ```java
 * public static List<Task> topologicalSort(...)
 * ```
 *
 * * Implements **Kahn’s Algorithm** to sort tasks in a way that respects their dependencies.
 * * Uses:
 *
 *   * `adjList`: adjacency list of task dependencies.
 *   * `inDegree`: number of incoming edges (dependencies) per task.
 *   * Starts with tasks having no dependencies (`inDegree == 0`) and removes them one by one.
 *
 * ✅ **Purpose**: Ensures tasks are executed **in correct dependency order**.
 *
 * ---
 *
 * ### 🧠 2. **Cycle Detection**
 *
 * ```java
 * public static boolean hasCycle(...)
 * ```
 *
 * * Calls `topologicalSort()`.
 * * If sorting fails (not all tasks processed), a **cycle** exists.
 * * Returns `true` if the DAG is **invalid**.
 *
 * ✅ **Purpose**: Prevents **cyclic task scheduling**, which would lead to deadlocks or infinite loops.
 *
 * ---
 *
 * ### 📌 Why This Is Important
 *
 * * In distributed task scheduling, dependencies must form a valid DAG.
 * * This utility guarantees:
 *
 *   * ✅ No circular dependencies.
 *   * ✅ Correct task execution order.
 *
 * ---
 *
 * ### 💡 Example
 *
 * Imagine:
 *
 * * A depends on B,
 * * B depends on C.
 *
 * `DagUtils.topologicalSort()` will return:
 * `[C, B, A]` — valid execution order.
 * But if A → B → C → A, it throws an exception (cycle detected).
 *
 *
 * **/



/***
 * Example of topological order
 *
 *
 * Here’s a simple example of **topological sort** using tasks with dependencies:
 *
 * ---
 *
 * ### 🧩 Given Tasks:
 *
 * Let’s say you have 4 tasks:
 *
 * | Task ID | Name      | Depends On |
 * | ------- | --------- | ---------- |
 * | T1      | Inventory | —          |
 * | T2      | Payment   | T1         |
 * | T3      | Packaging | T2         |
 * | T4      | Shipping  | T3         |
 *
 * ---
 *
 * ### 🧠 Input to `DagUtils.topologicalSort`:
 *
 * ```java
 * List<Task> tasks = List.of(T1, T2, T3, T4);
 *
 * Map<String, List<String>> dependencyMap = Map.of(
 *     "T2", List.of("T1"),
 *     "T3", List.of("T2"),
 *     "T4", List.of("T3")
 * );
 * ```
 *
 * ---
 *
 * ### ✅ Output of `topologicalSort(tasks, dependencyMap)`:
 *
 * ```java
 * [T1, T2, T3, T4]
 * ```
 *
 * > Tasks will be returned in this **valid execution order** because each task only starts after its dependencies are completed.
 *
 * ---
 *
 * If you change the dependency map to introduce a cycle like:
 *
 * ```java
 * "T1" -> ["T4"]
 * ```
 *
 * It will throw:
 *
 * ```
 * IllegalStateException: Cycle detected in task dependencies!
 * ```
 *
 *
 * */