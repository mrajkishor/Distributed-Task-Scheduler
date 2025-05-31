package com.distributedscheduler.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Gauge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class PrometheusMetricsCollector {

    private final MeterRegistry meterRegistry;
    private final RedisTemplate<String, Object> redisTemplate;

    private final Map<Integer, Counter> retryAttemptCounters = new HashMap<>();

    private Counter taskSuccessCounter;
    private Counter taskFailureCounter;

    private Counter taskRetryCounter;
    private Timer taskExecutionTimer;

    @Autowired
    public PrometheusMetricsCollector(MeterRegistry meterRegistry, RedisTemplate<String, Object> redisTemplate) {
        this.meterRegistry = meterRegistry;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void initMetrics() {
        this.taskSuccessCounter = meterRegistry.counter("task_success_count");
        this.taskFailureCounter = meterRegistry.counter("task_failure_count");
        this.taskRetryCounter = meterRegistry.counter("task_retry_count");

        Gauge.builder("task_queue_size", this,
                collector -> redisTemplate.keys("task:*") != null ? redisTemplate.keys("task:*").size() : 0)
                .register(meterRegistry);
    }

    public void recordSuccess() {
        taskSuccessCounter.increment();
    }

    public void recordFailure() {
        taskFailureCounter.increment();
    }

    public void recordExecutionTime(long durationInMillis) {
        taskExecutionTimer.record(durationInMillis, TimeUnit.MILLISECONDS);
    }

    public void recordExecutionTime(String taskType, long durationInMillis) {
        meterRegistry.timer("task_execution_duration_sec", "type", taskType)
                .record(durationInMillis, TimeUnit.MILLISECONDS);
    }

    public void recordRetry() {
        taskRetryCounter.increment();
    }

    public void recordRetryAttempt(int attemptCount) {
        retryAttemptCounters.computeIfAbsent(attemptCount, count ->
                Counter.builder("task_retry_attempts")
                        .description("Retry attempt count")
                        .tag("attempt", String.valueOf(count))
                        .register(meterRegistry)
        ).increment();
    }
}

/***
 * About this component :
 *
 *
 * This `PrometheusMetricsCollector` class collects custom task-related metrics
 * for Prometheus using **Micrometer**. Here's what each part does:
 *
 * ---
 *
 * ### 🎯 **Purpose**
 *
 * Track real-time task execution metrics like:
 *
 * * How many succeeded/failed
 * * How long tasks took to execute
 * * How many tasks are in Redis (task queue size)
 *
 * ---
 *
 * ### 📊 **Metrics Collected**
 *
 * | Metric Name | Type | Description |
 * | ----------------------------- | ------- |
 * ------------------------------------------------- |
 * | `task_success_count` | Counter | Increments when a task completes
 * successfully |
 * | `task_failure_count` | Counter | Increments when a task fails |
 * | `task_execution_duration_sec` | Timer | Measures task execution duration
 * (in ms) |
 * | `task_queue_size` | Gauge | Current number of tasks in Redis (prefix
 * `task:`) |
 *
 * ---
 *
 * ### 🔁 **How It's Used**
 *
 * * `recordSuccess()`: Call when a task succeeds.
 * * `recordFailure()`: Call when a task fails.
 * * `recordExecutionTime(durationInMillis)`: Call after task finishes, passing
 * its duration.
 * * `@PostConstruct`: Initializes all metrics once the bean is ready.
 *
 * ---
 *
 * ### ⚙️ **Note**
 *
 * The gauge (`task_queue_size`) uses Redis to count keys starting with `task:`
 * – make sure this prefix is used consistently.
 *
 * ---
 *
 * ### ✅ **Effect**
 *
 * These metrics are scraped by Prometheus and can be visualized in **Grafana**
 * dashboards to monitor the system health and performance.
 *
 *
 *
 *
 *
 ***/
