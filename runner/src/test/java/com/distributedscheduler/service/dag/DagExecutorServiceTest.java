
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
        Task taskA = new Task("A", "Task A", List.of(), TaskStatus.COMPLETED, null);
        Task taskB = new Task("B", "Task B", List.of("A"), TaskStatus.PENDING, null);
        Task taskC = new Task("C", "Task C", List.of("B"), TaskStatus.PENDING, null);
        List<Task> allTasks = List.of(taskA, taskB, taskC);

        when(taskRepository.findAllByTenantId("demo")).thenReturn(allTasks);
        dagExecutorService.executeDag("demo");

        verify(taskRunner).run(taskB);
        verify(taskRunner).run(taskC);
    }
}
