package com.distributedscheduler.service.impl;

import com.distributedscheduler.exception.TaskNotFoundException;
import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.dto.TaskRequest;
import com.distributedscheduler.redis.RedisDelayQueueService;
import com.distributedscheduler.redis.RedisTaskStore;
import com.distributedscheduler.service.TaskService;
import com.distributedscheduler.repository.TaskRedisRepository;
import com.distributedscheduler.service.idempotency.IdempotencyService;
import com.distributedscheduler.util.DagUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;
import java.util.List;

/**
 * Implementation of TaskService that stores tasks in Redis and manages delayed execution using ZSET.
 */
@Service
public class TaskServiceImpl implements TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private RedisTaskStore redisTaskStore;

    @Autowired
    private IdempotencyService idempotencyService;

    private final RedisDelayQueueService redisDelayQueueService;

    private final TaskRedisRepository taskRedisRepository;


    @Autowired
    public TaskServiceImpl(RedisDelayQueueService redisDelayQueueService,  RedisTaskStore redisTaskStore,
                           TaskRedisRepository taskRedisRepository) {
        this.redisDelayQueueService = redisDelayQueueService;
        this.redisTaskStore = redisTaskStore;
        this.taskRedisRepository = taskRedisRepository;
    }


    /**
     * Creates and stores a new Task. If delay is set, the task is added to a Redis ZSET.
     *
     * @param request The request object containing task metadata.
     * @return The created Task with generated ID and metadata.
     */
    @Override
    public Task createTask(TaskRequest request) {

        String tenantId = request.getTenantId();
        String key = idempotencyService.buildKey(tenantId, request.getIdempotencyKey(), request);


        // Check if a task already exists for the idempotency key
        return idempotencyService.getTaskIdForKey(key)
                .map(existingTaskId -> {
                    logger.info("🔁 Duplicate task detected. Returning existing task ID: {}", existingTaskId);
                    return taskRedisRepository.findById(tenantId, existingTaskId);
                })
                    .orElseGet(() -> {
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

                        // DAG validation before saving
                        List<Task> allTasks = taskRedisRepository.findAllByTenantId(task.getTenantId());
                        allTasks.add(task);

                        if (DagUtils.hasCycle(allTasks)) {
                            throw new IllegalStateException("🚫 Cycle detected in task dependencies. Cannot schedule this task.");
                        }


                        // Store in Redis ZSET if delay > 0
                        if (request.getDelaySeconds() > 0) {
                            redisDelayQueueService.addTaskToDelayQueue(task.getId(), task.getTenantId(), request.getDelaySeconds());
                        }

                        // ✅ Save using custom key format
                        taskRedisRepository.save(task); // ensures it is saved as a JSON string
                        logger.info("✅ Task created with ID: {} for tenant: {}", task.getId(), task.getTenantId());
                        // 💾 Store idempotency mapping in Redis
                        idempotencyService.storeKeyToTaskIdMapping(key, task.getId(), java.time.Duration.ofMinutes(10));

                        return task;
            });

    }

    @Override
    public Task getTaskById(String tenantId, String taskId) {
        Task task = taskRedisRepository.findById(tenantId, taskId);
        if (task == null) {
            throw new TaskNotFoundException(taskId);
        }
        return task;
    }
}
