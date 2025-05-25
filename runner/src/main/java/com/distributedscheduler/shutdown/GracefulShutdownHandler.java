package com.distributedscheduler.shutdown;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;

@Component
public class GracefulShutdownHandler {
    private static final Logger log = LoggerFactory.getLogger(GracefulShutdownHandler.class);

    private static final String LOCK_KEY_PREFIX = "lock:";

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @EventListener
    public void onShutdown(ContextClosedEvent event) {
        log.info("🛑 Initiating graceful shutdown...");

        try {
            // 1. Stop accepting new tasks and wait for active tasks to complete
            log.info("🧵 Shutting down executor...");
            taskExecutor.shutdown();

            // 2. Release all Redis locks (optional: filter using known keys)
            log.info("🔓 Releasing Redis locks...");
            Set<String> lockedKeys = redisTemplate.keys(LOCK_KEY_PREFIX + "*");
            if (lockedKeys != null) {
                for (String lockKey : lockedKeys) {
                    redisTemplate.delete(lockKey);
                    log.info("✔ Released lock: " + lockKey);
                }
            }

            // 3. Optional: Move any tracked in-progress tasks to DLQ or requeue
            // e.g., redisTemplate.opsForList().leftPush("dlq", taskId);

            log.info("✅ Graceful shutdown complete.");
        } catch (Exception e) {
            log.error("❌ Error during graceful shutdown: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
