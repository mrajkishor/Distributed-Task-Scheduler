package com.distributedscheduler.dlq;

import com.distributedscheduler.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterQueueService {

    private static final Logger logger = LoggerFactory.getLogger(DeadLetterQueueService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public DeadLetterQueueService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void pushToDLQ(String tenantId, Task failedTask) {
        try {
            String key = "dlq:" + tenantId;
            String json = objectMapper.writeValueAsString(failedTask);
            redisTemplate.opsForList().leftPush(key, json);

            logger.warn("Task [{}] moved to DLQ for tenant [{}]", failedTask.getId(), tenantId);
        } catch (Exception e) {
            logger.error("Failed to push task to DLQ: {}", e.getMessage());
        }
    }
}
