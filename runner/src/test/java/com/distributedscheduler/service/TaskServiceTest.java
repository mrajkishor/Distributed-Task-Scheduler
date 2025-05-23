package com.distributedscheduler.service;

import com.distributedscheduler.dto.TaskRequest;
import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.redis.RedisDelayQueueService;
import com.distributedscheduler.repository.TaskRedisRepository;
import com.distributedscheduler.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRedisRepository taskRedisRepository;

    @Mock
    private RedisDelayQueueService redisDelayQueueService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTask_withoutDelay() {
        // Arrange
        TaskRequest request = new TaskRequest();
        request.setName("generate-report");
        request.setPayload(Map.of("type", "pdf", "data", "sample"));
        request.setPriority(3);
        request.setDelaySeconds(0);
        request.setDependencies(List.of());
        request.setMaxRetries(2);

        Task savedTask = new Task();
        savedTask.setId(UUID.randomUUID().toString());
        savedTask.setName(request.getName());
        savedTask.setPayload(request.getPayload());
        savedTask.setPriority(request.getPriority());
        savedTask.setDelaySeconds(0);
        savedTask.setMaxRetries(request.getMaxRetries());
        savedTask.setStatus(TaskStatus.PENDING);

        when(taskRedisRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task result = taskService.createTask(request);

        // Assert
        assertNotNull(result.getId());
        assertEquals("generate-report", result.getName());
        assertEquals(TaskStatus.PENDING, result.getStatus());
        verify(taskRedisRepository, times(1)).save(any(Task.class));
        verify(redisDelayQueueService, never()).addTaskToDelayQueue(anyString(), anyString(), anyInt());
    }

    @Test
    public void testCreateTask_withDelay() {
        // Arrange
        TaskRequest request = new TaskRequest();
        request.setName("delayed-task");
        request.setPayload(Map.of("type", "doc", "data", "test"));
        request.setPriority(1);
        request.setDelaySeconds(30);  // delay
        request.setDependencies(List.of("t1", "t2"));
        request.setMaxRetries(1);

        Task savedTask = new Task();
        savedTask.setId(UUID.randomUUID().toString());
        savedTask.setName(request.getName());
        savedTask.setPayload(request.getPayload());
        savedTask.setPriority(request.getPriority());
        savedTask.setDelaySeconds(30);
        savedTask.setMaxRetries(request.getMaxRetries());
        savedTask.setStatus(TaskStatus.PENDING);

        when(taskRedisRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task result = taskService.createTask(request);

        // Assert
        assertEquals("delayed-task", result.getName());
        assertEquals(30, result.getDelaySeconds());
        verify(redisDelayQueueService, times(1)).addTaskToDelayQueue(result.getId(),result.getTenantId(), 30);
        verify(taskRedisRepository, times(1)).save(any(Task.class));
    }
}
