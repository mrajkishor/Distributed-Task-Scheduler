
package com.distributedscheduler.service.dag;

import com.distributedscheduler.lock.RedisLockService;
import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.repository.TaskRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class TaskRunnerTest {

    private TaskRedisRepository taskRepository;
    private TaskRunner taskRunner;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRedisRepository.class);
        RedisLockService redisLockService = mock(RedisLockService.class);
        taskRunner = new TaskRunner(taskRepository, redisLockService);
    }

    @Test
    void testMaxRetryReachedMovesToDLQ() {
        Task task = new Task();
        task.setId("T1");
        task.setTenantId("demo");
        task.setRetryCount(3);
        task.setMaxRetries(3);

        taskRunner.run(task);

        verify(taskRepository).updateTaskStatus("demo", "T1", TaskStatus.DLQ);
    }
}
