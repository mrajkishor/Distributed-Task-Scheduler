package com.distributedscheduler.repository;

import com.distributedscheduler.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TaskRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public TaskRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    private String buildKey(String tenantId, String taskId) {
        return "task:" + tenantId + ":" + taskId;
    }

    public Task save(Task task) {
        String key = buildKey(task.getTenantId(), task.getId());
        try {
            String json = objectMapper.writeValueAsString(task);
            redisTemplate.opsForValue().set(key, json); // Store as raw JSON string
            return task;
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize task", e);
        }
    }

    public Task findById(String tenantId, String taskId) {
        String key = buildKey(tenantId, taskId);
        String json  = (String) redisTemplate.opsForValue().get(key);  // Redis stores it as a JSON string

        if (json  == null) return null;
        try {
            return objectMapper.readValue(json, Task.class);  // Deserializing string to Task
        } catch (Exception e) {
            throw new RuntimeException(" Failed to deserialize task JSON", e);
        }
    }

    public void delete(String tenantId, String taskId) {
        redisTemplate.delete(buildKey(tenantId, taskId));
    }
}
