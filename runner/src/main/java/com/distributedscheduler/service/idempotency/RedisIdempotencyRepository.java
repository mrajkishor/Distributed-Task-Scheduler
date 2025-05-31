package com.distributedscheduler.service.idempotency;

import com.distributedscheduler.dto.TaskRequest;
import com.distributedscheduler.redis.RedisTaskStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Service
public class RedisIdempotencyRepository implements IdempotencyService {

    private static final Duration IDEMPOTENCY_TTL = Duration.ofMinutes(10);

    @Autowired
    private RedisTaskStore redisTaskStore;
    private static final String PREFIX = "idempotency";

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisIdempotencyRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String buildKey(String tenantId, String idempotencyKey, TaskRequest request) {
        String keyPart = (idempotencyKey != null && !idempotencyKey.isBlank())
                ? idempotencyKey.trim()
                : DigestUtils.md5DigestAsHex((request.getName() + ":" + request.getPayload()).getBytes(StandardCharsets.UTF_8));
        return String.format("%s:%s:%s", PREFIX, tenantId, keyPart);
    }

    @Override
    public Optional<String> getTaskIdForKey(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    @Override
    public void storeKeyToTaskIdMapping(String key, String taskId, Duration ttl) {
        redisTemplate.opsForValue().set(key, taskId, ttl);
    }
}


/**
 *
 * about this component
 *
 *
 * This component, `RedisIdempotencyRepository`, is a Redis-backed implementation of the `IdempotencyService` interface. It ensures **idempotent task creation**, meaning repeated task creation requests (with same data) won’t create duplicate tasks.
 *
 * ---
 *
 * ### ✅ **Key Responsibilities:**
 *
 * #### 1. `buildKey(...)`
 *
 * Creates a unique Redis key for each task request:
 *
 * * Uses `idempotencyKey` (if client provides it).
 * * Else, hashes `request.name + request.payload` (via MD5).
 * * Prepends with `idempotency:<tenantId>:...`.
 *
 * **➡️ Purpose**: Generate consistent Redis keys across retries.
 *
 * ---
 *
 * #### 2. `getTaskIdForKey(...)`
 *
 * Checks Redis to see if the task for this key was **already created**.
 *
 * * If found, returns the previously generated `taskId`.
 *
 * ---
 *
 * #### 3. `storeKeyToTaskIdMapping(...)`
 *
 * Stores a mapping from the generated key to the created `taskId` in Redis with a TTL (10 minutes).
 *
 * ---
 *
 * ### 🔁 **Example Use-Case**
 *
 * 1. User sends a task creation request.
 * 2. This class builds the Redis key from payload or `idempotencyKey`.
 * 3. It checks if task already exists using `getTaskIdForKey`.
 * 4. If not, creates task, stores the mapping using `storeKeyToTaskIdMapping`.
 *
 * ---
 *
 * ### 💡 Why It Matters:
 *
 * * **Prevents duplicate tasks**
 * * Enables **safe retries**
 * * Works even if the client doesn’t send an idempotency key (fallback to MD5)
 *
 * Let me know if you want a flow diagram or sample request/response.
 *
 * **/