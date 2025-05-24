package com.distributedscheduler.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RedisLockService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Tries to acquire a lock for a task using SETNX.
     * @param taskId The unique task ID
     * @param ttlMillis Time-to-live in milliseconds
     * @return true if lock was acquired, false otherwise
     */
    public boolean acquireLock(String taskId, long ttlMillis) {
        String key = "lock:task:" + taskId;
        String lockValue = UUID.randomUUID().toString(); // For future ownership validation

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, lockValue, Duration.ofMillis(ttlMillis));

        return Boolean.TRUE.equals(success);
    }

    /**
     * Releases the lock for the given task.
     * You can extend this to only release if `lockValue` matches (ownership).
     */
    public void releaseLock(String taskId) {
        String key = "lock:task:" + taskId;
        redisTemplate.delete(key);
    }
}
