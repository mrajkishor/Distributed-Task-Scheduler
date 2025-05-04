package com.distributedscheduler.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
public class RedisDelayQueueService {

    private final StringRedisTemplate redisTemplate;

    public RedisDelayQueueService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    private String getTenantKey(String tenantId) {
        return "delayed_tasks:" + tenantId;
    }

    public void addTaskToDelayQueue(String taskId, String tenantId, long delaySeconds) {
        long score = Instant.now().getEpochSecond() + delaySeconds;
        System.out.println("📥 Adding to ZSET: " + taskId + " with score " + score + " for tenant " + tenantId);
        redisTemplate.opsForZSet().add(getTenantKey(tenantId), taskId, score);
    }

    public Set<String> fetchDueTasks(String tenantId) {
        long now = Instant.now().getEpochSecond();
        return redisTemplate.opsForZSet().rangeByScore(getTenantKey(tenantId), 0, now);
    }

    public void removeTask(String taskId, String tenantId) {
        redisTemplate.opsForZSet().remove(getTenantKey(tenantId), taskId);
    }
}
