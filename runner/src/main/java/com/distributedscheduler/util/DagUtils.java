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
