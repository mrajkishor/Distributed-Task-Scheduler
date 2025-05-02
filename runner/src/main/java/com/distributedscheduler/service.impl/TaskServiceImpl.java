package com.distributedscheduler.service.impl;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.dto.TaskRequest;
import com.distributedscheduler.service.TaskService;
import com.distributedscheduler.repository.TaskRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of TaskService that stores tasks in Redis and manages delayed execution using ZSET.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRedisRepository taskRedisRepository;
    private final RedisDelayQueueService redisDelayQueueService;

    @Autowired
    public TaskServiceImpl(TaskRedisRepository taskRedisRepository, RedisDelayQueueService redisDelayQueueService) {
        this.taskRedisRepository = taskRedisRepository;
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

        // Store in Redis ZSET if delay > 0
        if (request.getDelaySeconds() > 0) {
            redisDelayQueueService.addTaskToDelayQueue(task.getId(), request.getDelaySeconds());
        }

        return taskRedisRepository.save(task);
    }
}
