package com.distributedscheduler.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Gauge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class PrometheusMetricsCollector {

    private final MeterRegistry meterRegistry;
    private final RedisTemplate<String, Object> redisTemplate;

    private Counter taskSuccessCounter;
    private Counter taskFailureCounter;
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
        this.taskExecutionTimer = meterRegistry.timer("task_execution_duration_sec");

        Gauge.builder("task_queue_size", this, collector ->
                redisTemplate.keys("task:*") != null ? redisTemplate.keys("task:*").size() : 0
        ).register(meterRegistry);
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
}
