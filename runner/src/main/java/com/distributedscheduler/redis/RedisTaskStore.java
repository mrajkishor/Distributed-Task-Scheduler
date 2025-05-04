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
