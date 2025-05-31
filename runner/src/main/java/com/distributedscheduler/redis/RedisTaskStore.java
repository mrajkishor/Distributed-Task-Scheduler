package com.distributedscheduler.redis;

import com.distributedscheduler.model.Task;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisTaskStore {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTaskStore(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(Task task) {
        String key = "task:" + task.getTenantId() + ":" + task.getId();
        redisTemplate.opsForValue().set(key, task);
    }

    public Task findById(String tenantId, String taskId) {
        String key = "task:" + tenantId + ":" + taskId;
        return (Task) redisTemplate.opsForValue().get(key);
    }

    public void delete(String tenantId, String taskId) {
        String key = "task:" + tenantId + ":" + taskId;
        redisTemplate.delete(key);
    }
}


/**
 * About this component
 *
 *
 * The `RedisTaskStore` is a simple **Redis-based DAO** for storing and retrieving `Task` objects.
 *
 * ### 🔍 Method Explanation:
 *
 * 1. **`save(Task task)`**
 *
 *    * Stores the task object in Redis with key format:
 *
 *      ```
 *      task:<tenantId>:<taskId>
 *      ```
 *    * Uses `opsForValue().set()` for key-value storage.
 *
 * 2. **`findById(tenantId, taskId)`**
 *
 *    * Retrieves the task from Redis using the same key format.
 *
 * 3. **`delete(tenantId, taskId)`**
 *
 *    * Deletes the task from Redis.
 *
 * ### ✅ Purpose:
 *
 * This is the **persistent store for tasks** during their lifecycle (PENDING, RUNNING, etc.) in your distributed task scheduler.
 *
 * **/
