package com.distributedscheduler.util;

import com.distributedscheduler.model.Task;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DagUtilsTest {

    private Task task(String id) {
        Task t = new Task();
        t.setId(id);
        return t;
    }

    @Test
    public void testTopologicalSortLinearChain() {
        List<Task> tasks = Arrays.asList(task("A"), task("B"), task("C"));

        Map<String, List<String>> deps = new HashMap<>();
        deps.put("B", List.of("A"));
        deps.put("C", List.of("B"));

        List<Task> sorted = DagUtils.topologicalSort(tasks, deps);
        assertEquals(List.of("A", "B", "C"),
                sorted.stream().map(Task::getId).toList());
    }

    @Test
    public void testTopologicalSortMultipleRoots() {
        List<Task> tasks = Arrays.asList(task("A"), task("B"), task("C"));

        Map<String, List<String>> deps = new HashMap<>();
        deps.put("C", List.of("A", "B"));

        List<Task> sorted = DagUtils.topologicalSort(tasks, deps);
        assertTrue(sorted.indexOf(taskWithId(sorted, "A")) < sorted.indexOf(taskWithId(sorted, "C")));
        assertTrue(sorted.indexOf(taskWithId(sorted, "B")) < sorted.indexOf(taskWithId(sorted, "C")));
    }

    @Test
    public void testTopologicalSortDisconnectedComponents() {
        List<Task> tasks = Arrays.asList(task("A"), task("B"), task("C"), task("D"));

        Map<String, List<String>> deps = new HashMap<>();
        deps.put("B", List.of("A"));
        deps.put("D", List.of("C"));

        List<Task> sorted = DagUtils.topologicalSort(tasks, deps);
        assertTrue(sorted.indexOf(taskWithId(sorted, "A")) < sorted.indexOf(taskWithId(sorted, "B")));
        assertTrue(sorted.indexOf(taskWithId(sorted, "C")) < sorted.indexOf(taskWithId(sorted, "D")));
    }

    @Test
    public void testCycleDetection() {
        List<Task> tasks = Arrays.asList(task("A"), task("B"), task("C"));

        Map<String, List<String>> deps = new HashMap<>();
        deps.put("A", List.of("C"));
        deps.put("B", List.of("A"));
        deps.put("C", List.of("B"));  // cycle A -> C -> B -> A

        assertThrows(IllegalStateException.class, () -> DagUtils.topologicalSort(tasks, deps));
        assertTrue(DagUtils.hasCycle(tasks, deps));
    }

    @Test
    public void testEmptyTaskList() {
        List<Task> tasks = Collections.emptyList();
        Map<String, List<String>> deps = new HashMap<>();

        List<Task> sorted = DagUtils.topologicalSort(tasks, deps);
        assertTrue(sorted.isEmpty());
        assertFalse(DagUtils.hasCycle(tasks, deps));
    }

    private Task taskWithId(List<Task> tasks, String id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst().orElseThrow();
    }
}
