package com.distributedscheduler.util;

import com.distributedscheduler.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DagUtilsTest {

    @Test
    public void testTopologicalSort_NoCycle() {
        Task a = new Task();
        a.setId("A");
        a.setDependencies(List.of());

        Task b = new Task();
        b.setId("B");
        b.setDependencies(List.of("A"));

        Task c = new Task();
        c.setId("C");
        c.setDependencies(List.of("B"));

        List<Task> sorted = DagUtils.topologicalSort(List.of(a, b, c));
        List<String> sortedIds = sorted.stream().map(Task::getId).toList();

        Assertions.assertEquals(List.of("A", "B", "C"), sortedIds);
    }

    @Test
    public void testHasCycle() {
        Task a = new Task();
        a.setId("A");
        a.setDependencies(List.of("C"));

        Task b = new Task();
        b.setId("B");
        b.setDependencies(List.of("A"));

        Task c = new Task();
        c.setId("C");
        c.setDependencies(List.of("B")); // Cycle: C → B → A → C

        Assertions.assertTrue(DagUtils.hasCycle(List.of(a, b, c)));
    }

    @Test
    public void testTopologicalSort_MultipleRoots() {
        Task a = new Task();
        a.setId("A");
        a.setDependencies(List.of());

        Task b = new Task();
        b.setId("B");
        b.setDependencies(List.of());

        Task c = new Task();
        c.setId("C");
        c.setDependencies(List.of("A", "B"));

        List<Task> sorted = DagUtils.topologicalSort(List.of(a, b, c));
        List<String> sortedIds = sorted.stream().map(Task::getId).toList();

        // A and B can come in any order, C must be last
        Assertions.assertTrue(
                sortedIds.indexOf("C") > sortedIds.indexOf("A") &&
                        sortedIds.indexOf("C") > sortedIds.indexOf("B")
        );
    }

}
