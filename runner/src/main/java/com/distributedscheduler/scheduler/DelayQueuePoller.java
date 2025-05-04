package com.distributedscheduler.scheduler;

import com.distributedscheduler.redis.RedisDelayQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.List;

import java.time.Instant;
@Component
public class DelayQueuePoller {
    private static final Logger logger = LoggerFactory.getLogger(DelayQueuePoller.class);

    private final RedisDelayQueueService delayQueueService;
    // Example tenant list; in production, dynamically fetch this from DB or config
    private final List<String> tenants = List.of("default", "clientA", "clientB");
    public DelayQueuePoller(RedisDelayQueueService delayQueueService) {
        this.delayQueueService = delayQueueService;
    }

    @Scheduled(fixedRate = 1000) // every second
    public void poll() {
        long now = Instant.now().getEpochSecond();
        for (String tenantId : tenants) {
            Set<String> readyTaskIds = delayQueueService.fetchDueTasks(tenantId);

            for (String taskId : readyTaskIds) {
                logger.info("⏰ Task ready to run: {} (tenant: {})", taskId, tenantId);
                delayQueueService.removeTask(taskId, tenantId);

                // TODO: Enqueue for execution or update task status in Redis
                // e.g., taskExecutorService.runTask(taskId);
            }
        }
    }
}
