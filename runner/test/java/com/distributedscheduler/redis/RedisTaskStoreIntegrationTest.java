
package com.distributedscheduler.redis;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisTaskStoreIntegrationTest {

    @Autowired
    private RedisTaskStore redisTaskStore;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Task sampleTask;

    @BeforeEach
    void setup() {
        sampleTask = new Task();
        sampleTask.setId(UUID.randomUUID().toString());
        sampleTask.setName("integration-test-task");
        sampleTask.setTenantId("testTenant");
        sampleTask.setPayload(Map.of("key", "value"));
        sampleTask.setStatus(TaskStatus.PENDING);
        sampleTask.setMaxRetries(5);
        sampleTask.setDelaySeconds(15);
        sampleTask.setPriority(3);
    }

    @Test
    void testSaveAndFetchTask() {
        redisTaskStore.save(sampleTask);

        String redisKey = "task:" + sampleTask.getTenantId() + ":" + sampleTask.getId();
        Task fetched = (Task) redisTemplate.opsForValue().get(redisKey);

        assertNotNull(fetched);
        assertEquals(sampleTask.getId(), fetched.getId());
        assertEquals("integration-test-task", fetched.getName());
        assertEquals("testTenant", fetched.getTenantId());
        assertEquals(TaskStatus.PENDING, fetched.getStatus());
        assertEquals(15, fetched.getDelaySeconds());
        assertEquals(3, fetched.getPriority());
        assertEquals(5, fetched.getMaxRetries());
        assertEquals(Map.of("key", "value"), fetched.getPayload());
    }
}
