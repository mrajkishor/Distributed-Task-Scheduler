package com.distributedscheduler.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueueHealthIndicator implements HealthIndicator {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Health health() {
        Long queueSize = redisTemplate.opsForZSet().size("task_queue");
        if (queueSize == null || queueSize > 1000) {
            return Health.down().withDetail("QueueSize", queueSize).build();
        }
        return Health.up().withDetail("QueueSize", queueSize).build();
    }
}
