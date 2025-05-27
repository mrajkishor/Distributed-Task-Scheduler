package com.distributedscheduler.service.idempotency;

import com.distributedscheduler.dto.TaskRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RedisIdempotencyRepositoryTest {

    private RedisIdempotencyRepository repository;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        repository = new RedisIdempotencyRepository(redisTemplate);
    }

    @Test
    void shouldReturnEmptyIfKeyNotFound() {
        String key = "idempotency:tenant-1:abc";
        when(valueOperations.get(key)).thenReturn(null);

        Optional<String> result = repository.getTaskIdForKey(key);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnValueIfKeyExists() {
        String key = "idempotency:tenant-1:abc";
        when(valueOperations.get(key)).thenReturn("task-123");

        Optional<String> result = repository.getTaskIdForKey(key);

        assertTrue(result.isPresent());
        assertEquals("task-123", result.get());
    }

    @Test
    void shouldStoreKeyWithTTL() {
        String key = "idempotency:tenant-1:abc";
        String taskId = "task-123";
        Duration ttl = Duration.ofMinutes(10);

        repository.storeKeyToTaskIdMapping(key, taskId, ttl);

        verify(valueOperations).set(key, taskId, ttl);
    }
    @Test
    void shouldRemoveKeyAfterTTL() throws InterruptedException {
        String key = "idempotency:test:expirekey";
        String value = "task-999";

        // Act: store key with short TTL (5 seconds)
        repository.storeKeyToTaskIdMapping(key, value, Duration.ofSeconds(5));

        // Wait for TTL to expire
        Thread.sleep(6000);

        // Assert: key should no longer be in Redis
        Optional<String> result = repository.getTaskIdForKey(key);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldBuildKeyUsingClientKey() {
        TaskRequest request = new TaskRequest("tenant-1", "name", Map.of("k", "v"), 1, 0, null, 3);
        String key = repository.buildKey("tenant-1", "uuid-123", request);

        assertEquals("idempotency:tenant-1:uuid-123", key);
    }

    @Test
    void shouldBuildKeyUsingHashWhenNoKeyProvided() {
        TaskRequest request = new TaskRequest("tenant-1", "taskName", Map.of("k", "v"), 1, 0, null, 3);
        String key = repository.buildKey("tenant-1", null, request);

        assertTrue(key.startsWith("idempotency:tenant-1:"));
        assertTrue(key.length() > "idempotency:tenant-1:".length());
    }
}