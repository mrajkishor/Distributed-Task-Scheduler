package com.distributedscheduler.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

import static org.mockito.Mockito.*;

class RedisDelayQueueServiceTest {

    private RedisDelayQueueService redisDelayQueueService;
    private StringRedisTemplate redisTemplate;
    private ZSetOperations<String, String> zSetOperations;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        zSetOperations = mock(ZSetOperations.class);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        redisDelayQueueService = new RedisDelayQueueService(redisTemplate);
    }

    @Test
    void testAddTaskToDelayQueue() {
        String taskId = "task-123";
        String tenantId = "tenantA";
        long delaySeconds = 60;

        redisDelayQueueService.addTaskToDelayQueue(taskId, tenantId, delaySeconds);

        verify(zSetOperations, times(1)).add(startsWith("delayed_tasks:" + tenantId), eq(taskId), anyDouble());
    }

    @Test
    void testFetchDueTasks() {
        String tenantId = "tenantA";
        redisDelayQueueService.fetchDueTasks(tenantId);
        verify(zSetOperations, times(1)).rangeByScore(startsWith("delayed_tasks:" + tenantId), eq(0.0), anyDouble());
    }

    @Test
    void testRemoveTask() {
        String tenantId = "tenantA";
        String taskId = "task-123";
        redisDelayQueueService.removeTask(taskId, tenantId);
        verify(zSetOperations, times(1)).remove(startsWith("delayed_tasks:" + tenantId), eq(taskId));
    }
}