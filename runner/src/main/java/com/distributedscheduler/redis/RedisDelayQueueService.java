package com.distributedscheduler.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
public class RedisDelayQueueService {

    private static final String DELAYED_TASKS_KEY = "delayed_tasks";
    private final StringRedisTemplate redisTemplate;

    public RedisDelayQueueService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addTaskToDelayQueue(String taskId, long delaySeconds) {
        long score = Instant.now().getEpochSecond() + delaySeconds;
        redisTemplate.opsForZSet().add(DELAYED_TASKS_KEY, taskId, score);
    }

    public Set<String> fetchDueTasks() {
        long now = Instant.now().getEpochSecond();
        return redisTemplate.opsForZSet().rangeByScore(DELAYED_TASKS_KEY, 0, now);
    }

    public void removeTask(String taskId) {
        redisTemplate.opsForZSet().remove(DELAYED_TASKS_KEY, taskId);
    }
}
