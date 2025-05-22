package com.distributedscheduler.repository;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
@Repository
public class TaskRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public TaskRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    private String taskKey(String tenantId, String taskId) {
        return "task:" + tenantId + ":" + taskId;
    }

    private String nameKey(String tenantId, String taskName) {
        return "task:name:" + tenantId + ":" + taskName;
    }

    public boolean isNameTaken(String tenantId, String name) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(nameKey(tenantId, name)));
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

    public void saveTaskAndIndex(Task task) {
        String key = buildKey(task.getTenantId(), task.getId());
        String nameKey = buildNameKey(task.getTenantId(), task.getName());
        try {
            String json = objectMapper.writeValueAsString(task);
            redisTemplate.opsForValue().set(key, json);
            redisTemplate.opsForValue().set(nameKey, task.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save task", e);
        }
    }

    public String getTaskIdByName(String tenantId, String name) {
        String nameKey = buildNameKey(tenantId, name);
        return (String) redisTemplate.opsForValue().get(nameKey);
    }

    private String buildNameKey(String tenantId, String name) {
        return "task:name:" + tenantId + ":" + name;
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


    public List<Task> findAllByTenantId(String tenantId) {
        String pattern = "task:" + tenantId + ":*";  // matches all task keys for this tenant
        Set<String> keys = redisTemplate.keys(pattern);
        List<Task> result = new ArrayList<>();

        if (keys != null) {
            for (String key : keys) {
                Object raw = redisTemplate.opsForValue().get(key);
                if (raw instanceof String json) {
                    try {
                        result.add(objectMapper.readValue(json, Task.class));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse task from Redis key: " + key, e);
                    }
                }
            }
        }

        return result;
    }

    public void updateTaskStatus(String tenantId, String taskId, TaskStatus status) {
        Task task = findById(tenantId, taskId);
        if (task == null) {
            throw new IllegalStateException("Task not found for ID: " + taskId);
        }
        task.setStatus(status);
        save(task); // Save the updated task
    }


    public void delete(String tenantId, String taskId) {
        redisTemplate.delete(buildKey(tenantId, taskId));
    }

    public void saveDependencies(String tenantId, String taskId, List<String> dependencyIds) {
        String key = buildDepsKey(tenantId, taskId);
        redisTemplate.delete(key); // overwrite old deps
        redisTemplate.opsForSet().add(key, dependencyIds.toArray());
    }
    public List<String> getDependencies(String tenantId, String taskId) {
        String key = buildDepsKey(tenantId, taskId);
        Set<Object> members = redisTemplate.opsForSet().members(key);
        if (members == null) return List.of();
        return members.stream().map(Object::toString).toList();
    }
    private String buildDepsKey(String tenantId, String taskId) {
        return "task:deps:" + tenantId + ":" + taskId;
    }

    public Map<String, List<String>> getAllDependenciesMap(String tenantId) {
        Set<String> keys = redisTemplate.keys("task:deps:" + tenantId + ":*");
        Map<String, List<String>> map = new HashMap<>();

        if (keys != null) {
            for (String key : keys) {
                String taskId = key.substring(key.lastIndexOf(":") + 1);
                Set<Object> deps = redisTemplate.opsForSet().members(key);
                List<String> depIds = deps != null
                        ? deps.stream().map(Object::toString).toList()
                        : Collections.emptyList();
                map.put(taskId, depIds);
            }
        }

        return map;
    }


}
