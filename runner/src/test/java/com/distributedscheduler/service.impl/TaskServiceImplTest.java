package com.distributedscheduler.service.impl;

import com.distributedscheduler.dto.TaskRequest;
import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.redis.RedisDelayQueueService;
import com.distributedscheduler.redis.RedisTaskStore;
import com.distributedscheduler.repository.TaskRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    private TaskRedisRepository taskRedisRepository;
    private RedisDelayQueueService redisDelayQueueService;
    private TaskServiceImpl taskService;
    @Mock
    private RedisTaskStore redisTaskStore;


    @BeforeEach
    void setUp() {
        taskRedisRepository = mock(TaskRedisRepository.class);
        redisDelayQueueService = mock(RedisDelayQueueService.class);
        taskService = new TaskServiceImpl(redisDelayQueueService, redisTaskStore, taskRedisRepository);
    }

    @Test
    void createTask_shouldSaveTaskToRedisAndReturnSavedTask() {
        TaskRequest request = new TaskRequest(
                null,
                "generate-report",
                Map.of("type", "pdf"),
                5,
                0,
                Collections.emptyList(),
                3
        );

        Task expectedTask = new Task();
        expectedTask.setName("generate-report");

        when(taskRedisRepository.save(any(Task.class))).thenReturn(expectedTask);

        Task result = taskService.createTask(request);

        assertNotNull(result);
        assertEquals("generate-report", result.getName());
        assertEquals(TaskStatus.PENDING, result.getStatus());
        verify(taskRedisRepository, times(1)).save(any(Task.class));
        verify(redisDelayQueueService, never()).addTaskToDelayQueue(anyString(),anyString(), anyInt());
    }

    @Test
    void createTask_shouldAddToDelayQueue_whenDelayIsProvided() {
        TaskRequest request = new TaskRequest(
                null,
                "delayed-task",
                Map.of("type", "pdf"),
                5,
                10,
                Collections.emptyList(),
                3
        );

        when(taskRedisRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task task = taskService.createTask(request);

        assertEquals("delayed-task", task.getName());
        assertEquals(10, task.getDelaySeconds());
        verify(redisDelayQueueService).addTaskToDelayQueue(eq(task.getId()),task.getTenantId(), eq(10));
    }
}
