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
