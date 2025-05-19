package com.distributedscheduler.util;

import com.distributedscheduler.model.Task;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DagUtilsTest {

    private Task task(String id, String... deps) {
        Task t = new Task();
        t.setId(id);
        t.setDependencies(Arrays.asList(deps));
        return t;
    }

    @Test
    public void testTopologicalSortLinearChain() {
        List<Task> tasks = Arrays.asList(
                task("A"),
                task("B", "A"),
                task("C", "B")
        );

        List<Task> sorted = DagUtils.topologicalSort(tasks);
        assertEquals(Arrays.asList("A", "B", "C"),
                sorted.stream().map(Task::getId).toList());
    }

    @Test
    public void testTopologicalSortMultipleRoots() {
        List<Task> tasks = Arrays.asList(
                task("A"),
                task("B"),
                task("C", "A", "B")
        );

        List<Task> sorted = DagUtils.topologicalSort(tasks);
        assertTrue(sorted.indexOf(taskWithId(sorted, "A")) < sorted.indexOf(taskWithId(sorted, "C")));
        assertTrue(sorted.indexOf(taskWithId(sorted, "B")) < sorted.indexOf(taskWithId(sorted, "C")));
    }

    @Test
    public void testTopologicalSortDisconnectedComponents() {
        List<Task> tasks = Arrays.asList(
                task("A"),
                task("B", "A"),
                task("C"),
                task("D", "C")
        );

        List<Task> sorted = DagUtils.topologicalSort(tasks);
        assertTrue(sorted.indexOf(taskWithId(sorted, "A")) < sorted.indexOf(taskWithId(sorted, "B")));
        assertTrue(sorted.indexOf(taskWithId(sorted, "C")) < sorted.indexOf(taskWithId(sorted, "D")));
    }

    @Test
    public void testCycleDetection() {
        List<Task> tasks = Arrays.asList(
                task("A", "B"),
                task("B", "C"),
                task("C", "A")
        );

        assertThrows(IllegalStateException.class, () -> DagUtils.topologicalSort(tasks));
        assertTrue(DagUtils.hasCycle(tasks));
    }

    @Test
    public void testEmptyTaskList() {
        List<Task> tasks = Collections.emptyList();
        List<Task> sorted = DagUtils.topologicalSort(tasks);
        assertTrue(sorted.isEmpty());
        assertFalse(DagUtils.hasCycle(tasks));
    }

    private Task taskWithId(List<Task> tasks, String id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst().orElseThrow();
    }
}