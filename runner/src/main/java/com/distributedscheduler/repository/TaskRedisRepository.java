package com.distributedscheduler.repository;

import com.distributedscheduler.model.Task;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TaskRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public TaskRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String buildKey(String tenantId, String taskId) {
        return "task:" + tenantId + ":" + taskId;
    }

    public Task save(Task task) {
        String key = buildKey(task.getTenantId(), task.getId());
        redisTemplate.opsForHash().put(key, "data", task); // serialize as a whole
        return task;
    }

    public Task findById(String tenantId, String taskId) {
        String key = buildKey(tenantId, taskId);
        Object value = redisTemplate.opsForHash().get(key, "data");
        return value instanceof Task ? (Task) value : null;
    }

    public void delete(String tenantId, String taskId) {
        redisTemplate.delete(buildKey(tenantId, taskId));
    }
}
