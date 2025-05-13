package com.distributedscheduler.service.impl;

import com.distributedscheduler.dto.TaskRequest;
import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.redis.RedisDelayQueueService;
import com.distributedscheduler.redis.RedisTaskStore;
import com.distributedscheduler.repository.TaskRedisRepository;
import com.distributedscheduler.service.idempotency.IdempotencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    private TaskRedisRepository taskRedisRepository;
    private RedisDelayQueueService redisDelayQueueService;
    private TaskServiceImpl taskService;
    @Mock
    private RedisTaskStore redisTaskStore;

    // Add these new fields
    private IdempotencyService idempotencyService;
    @BeforeEach
    void setUp() {



        taskRedisRepository = mock(TaskRedisRepository.class);
        redisDelayQueueService = mock(RedisDelayQueueService.class);
        redisTaskStore = mock(RedisTaskStore.class);
        idempotencyService = mock(IdempotencyService.class);

        taskService = new TaskServiceImpl(redisDelayQueueService, redisTaskStore, taskRedisRepository);


        try {
            Field field = TaskServiceImpl.class.getDeclaredField("idempotencyService");
            field.setAccessible(true);
            field.set(taskService, idempotencyService);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Reflection injection failed: " + e.getMessage(), e);
        }


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


    @Test
    void createTask_shouldReturnExistingTask_whenDuplicateKeyFound() {
        TaskRequest request = new TaskRequest(
                "tenant-1",
                "generate-report",
                Map.of("type", "pdf"),
                5,
                0,
                Collections.emptyList(),
                3
        );

        String key = "idempotency:tenant-1:hash123";
        String existingTaskId = "task-123";

        Task existingTask = new Task();
        existingTask.setId(existingTaskId);
        existingTask.setName("generate-report");

        when(idempotencyService.buildKey(any(), any(), any())).thenReturn(key);
        when(idempotencyService.getTaskIdForKey(key)).thenReturn(Optional.of(existingTaskId));
        when(taskRedisRepository.findById("tenant-1", existingTaskId)).thenReturn(existingTask);

        Task result = taskService.createTask(request);

        assertEquals(existingTaskId, result.getId());
        verify(taskRedisRepository, never()).save(any());
        verify(idempotencyService, never()).storeKeyToTaskIdMapping(any(), any(), any());
    }

    @Test
    void createTask_shouldCreateAndStoreNewKey_whenNotDuplicate() {
        TaskRequest request = new TaskRequest(
                "tenant-1",
                "send-email",
                Map.of("to", "test@example.com"),
                1,
                0,
                Collections.emptyList(),
                2
        );

        String key = "idempotency:tenant-1:newkey123";

        when(idempotencyService.buildKey(any(), any(), any())).thenReturn(key);
        when(idempotencyService.getTaskIdForKey(key)).thenReturn(Optional.empty());
        when(taskRedisRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Task task = taskService.createTask(request);

        assertNotNull(task.getId());
        assertEquals("send-email", task.getName());
        verify(idempotencyService).storeKeyToTaskIdMapping(eq(key), eq(task.getId()), any());
    }
}
