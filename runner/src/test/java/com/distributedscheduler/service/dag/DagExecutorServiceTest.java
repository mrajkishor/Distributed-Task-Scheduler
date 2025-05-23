package com.distributedscheduler.service.dag;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.repository.TaskRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.*;

public class DagExecutorServiceTest {

    private DagExecutorService dagExecutorService;
    private TaskRedisRepository taskRepository;
    private TaskRunner taskRunner;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRedisRepository.class);
        taskRunner = mock(TaskRunner.class);
        dagExecutorService = new DagExecutorService(taskRepository, taskRunner);
    }

    @Test
    void testLinearDagExecution() {
        // Define tasks
        Task taskA = new Task();
        taskA.setId("A");
        taskA.setName("Task A");
        taskA.setStatus(TaskStatus.COMPLETED);
        taskA.setPriority(1);
        taskA.setCreatedAt(System.currentTimeMillis() / 1000);

        Task taskB = new Task();
        taskB.setId("B");
        taskB.setName("Task B");
        taskB.setStatus(TaskStatus.PENDING);
        taskB.setPriority(1);
        taskB.setCreatedAt(System.currentTimeMillis() / 1000);

        Task taskC = new Task();
        taskC.setId("C");
        taskC.setName("Task C");
        taskC.setStatus(TaskStatus.PENDING);
        taskC.setPriority(1);
        taskC.setCreatedAt(System.currentTimeMillis() / 1000);

        List<Task> allTasks = List.of(taskA, taskB, taskC);

        // Define dependencies
        Map<String, List<String>> deps = new HashMap<>();
        deps.put("B", List.of("A"));
        deps.put("C", List.of("B"));

        when(taskRepository.findAllByTenantId("demo")).thenReturn(allTasks);
        when(taskRepository.getAllDependenciesMap("demo")).thenReturn(deps);

        // Execute
        dagExecutorService.executeDag("demo");

        // Verify
        verify(taskRunner).run(taskB);
        verify(taskRunner).run(taskC);
        verify(taskRunner, never()).run(taskA); // Already completed
    }
}
