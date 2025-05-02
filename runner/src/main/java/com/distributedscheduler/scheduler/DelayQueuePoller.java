package com.distributedscheduler.scheduler;

import com.distributedscheduler.redis.RedisDelayQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DelayQueuePoller {

    private final RedisDelayQueueService delayQueueService;

    public DelayQueuePoller(RedisDelayQueueService delayQueueService) {
        this.delayQueueService = delayQueueService;
    }

    @Scheduled(fixedRate = 1000) // every second
    public void poll() {
        long now = System.currentTimeMillis();
        Set<String> readyTaskIds = delayQueueService.getReadyTasks(now);

        for (String taskId : readyTaskIds) {
            System.out.println("⏰ Task ready: " + taskId);
            delayQueueService.removeFromQueue(taskId);
            // Process task (e.g., update status, enqueue for execution)
        }
    }
}
