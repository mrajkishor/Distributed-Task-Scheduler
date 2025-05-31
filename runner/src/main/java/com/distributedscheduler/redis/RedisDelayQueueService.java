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

/**
 * About this Component:
 *
 * The `RedisDelayQueueService` manages a **delayed task queue** using Redis **ZSET** (sorted set), where tasks are scheduled based on a future timestamp (UNIX epoch seconds).
 *
 * ### 🔍 Key Methods:
 *
 * 1. **`addTaskToDelayQueue(taskId, tenantId, delaySeconds)`**
 *
 *    * Adds a task to a Redis ZSET with a **future score = now + delaySeconds**.
 *    * This score acts as the execution timestamp.
 *
 * 2. **`fetchDueTasks(tenantId)`**
 *
 *    * Fetches tasks whose score (scheduled time) is **less than or equal to current time**.
 *    * These tasks are **ready for execution**.
 *
 * 3. **`removeTask(taskId, tenantId)`**
 *
 *    * Removes a task from the Redis ZSET once it's executed or canceled.
 *
 * ### 📦 Use Case:
 *
 * This service is used to **schedule retries, delays, or initial task executions** based on `delaySeconds`.
 * It powers task leasing/polling in the background scheduler logic.
 *
 *
 * */
