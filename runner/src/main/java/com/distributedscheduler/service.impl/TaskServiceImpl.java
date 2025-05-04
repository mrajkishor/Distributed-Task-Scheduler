package com.distributedscheduler.service.impl;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.dto.TaskRequest;
import com.distributedscheduler.redis.RedisDelayQueueService;
import com.distributedscheduler.redis.RedisTaskStore;
import com.distributedscheduler.service.TaskService;
import com.distributedscheduler.repository.TaskRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

/**
 * Implementation of TaskService that stores tasks in Redis and manages delayed execution using ZSET.
 */
@Service
public class TaskServiceImpl implements TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private RedisTaskStore redisTaskStore;

    private final RedisDelayQueueService redisDelayQueueService;

    @Autowired
    public TaskServiceImpl(RedisDelayQueueService redisDelayQueueService) {
        this.redisDelayQueueService = redisDelayQueueService;
    }


    /**
     * Creates and stores a new Task. If delay is set, the task is added to a Redis ZSET.
     *
     * @param request The request object containing task metadata.
     * @return The created Task with generated ID and metadata.
     */
    @Override
    public Task createTask(TaskRequest request) {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setName(request.getName());
        task.setPayload(request.getPayload());
        task.setPriority(request.getPriority());
        task.setDelaySeconds(request.getDelaySeconds());
        task.setDependencies(request.getDependencies());
        task.setStatus(TaskStatus.PENDING);
        task.setMaxRetries(request.getMaxRetries());
        task.setTenantId(request.getTenantId() != null ? request.getTenantId() : "default");


        // Store in Redis ZSET if delay > 0
        if (request.getDelaySeconds() > 0) {
            redisDelayQueueService.addTaskToDelayQueue(task.getId(), task.getTenantId(), request.getDelaySeconds());
        }

        // ✅ Save using custom key format
        redisTaskStore.save(task);
        logger.info("✅ Task created with ID: {} for tenant: {}", task.getId(), task.getTenantId());

        return task;
    }
}
