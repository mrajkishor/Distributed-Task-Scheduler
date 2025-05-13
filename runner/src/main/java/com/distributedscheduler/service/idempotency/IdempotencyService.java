package com.distributedscheduler.service.idempotency;

import com.distributedscheduler.dto.TaskRequest;

import java.time.Duration;
import java.util.Optional;

public interface IdempotencyService {

    /**
     * Builds a Redis key for idempotency check based on tenant ID and request or provided key.
     *
     * @param tenantId        the tenant identifier
     * @param idempotencyKey  the optional key from client
     * @param request         the task request
     * @return formatted Redis key string
     */
    String buildKey(String tenantId, String idempotencyKey, TaskRequest request);

    /**
     * Retrieves the task ID associated with the idempotency key if it exists in Redis.
     *
     * @param key the Redis key
     * @return Optional containing task ID if found
     */
    Optional<String> getTaskIdForKey(String key);

    /**
     * Stores the mapping between an idempotency key and task ID with a TTL.
     *
     * @param key     the Redis key
     * @param taskId  the associated task ID
     * @param ttl     time-to-live duration
     */
    void storeKeyToTaskIdMapping(String key, String taskId, Duration ttl);
}
