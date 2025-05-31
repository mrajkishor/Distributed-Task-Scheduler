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


/**
 * About this component
 *
 * This `QueueHealthIndicator` is a **custom health check** for Spring Boot Actuator. Here's what it does:
 *
 * ---
 *
 * ### 🔍 **Purpose**
 *
 * It checks the health of the Redis **`task_queue`** (a ZSET used for delayed tasks) and reports status to Spring Boot's health endpoint (`/actuator/health`).
 *
 * ---
 *
 * ### ⚙️ **How It Works**
 *
 * * It uses `RedisTemplate` to get the size of `task_queue`.
 * * If the size is **`> 1000`** or `null`, it reports **DOWN**.
 * * Otherwise, it reports **UP** and includes the current queue size as a detail.
 *
 * ---
 *
 * ### ✅ **Why Useful**
 *
 * This helps **monitor system backlog**:
 *
 * * Too many tasks = potential overload or stuck consumers.
 * * Integrated with Prometheus/Grafana alerts via `/actuator/health`.
 *
 * ---
 *
 * ### 📌 Example Output
 *
 * ```json
 * {
 *   "status": "UP",
 *   "details": {
 *     "QueueSize": 12
 *   }
 * }
 * ```
 *
 * Or, if overloaded:
 *
 * ```json
 * {
 *   "status": "DOWN",
 *   "details": {
 *     "QueueSize": 1289
 *   }
 * }
 * ```
 *
 * You can customize the threshold `1000` based on your system’s expected load.
 *
 *
 * **/