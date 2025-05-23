package com.distributedscheduler.integration;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.redis.RedisTaskStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisPersistenceIntegrationTest {

    @Autowired
    private RedisTaskStore redisTaskStore;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testTaskPersistenceInRedis() {
        // Given
        String tenantId = "integrationTenant";
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setName("integration-test-task");
        task.setTenantId(tenantId);
        task.setPriority(2);
        task.setStatus(TaskStatus.PENDING);
        task.setMaxRetries(2);
        task.setRetryCount(1);
        task.setDelaySeconds(5);
        task.setPayload(Map.of("format", "csv", "source", "db"));

        // When
        redisTaskStore.save(task);

        // Then
        Task fetched = redisTaskStore.findById(task.getTenantId(), task.getId());
        assertNotNull(fetched);
        assertEquals(task.getId(), fetched.getId());
        assertEquals(task.getName(), fetched.getName());
        assertEquals(task.getTenantId(), fetched.getTenantId());
        assertEquals(task.getPriority(), fetched.getPriority());
        assertEquals(task.getStatus(), fetched.getStatus());
        assertEquals(task.getMaxRetries(), fetched.getMaxRetries());
        assertEquals(task.getRetryCount(), fetched.getRetryCount());
        assertEquals(task.getDelaySeconds(), fetched.getDelaySeconds());
        assertEquals(task.getPayload().get("format"), "csv");
    }
}