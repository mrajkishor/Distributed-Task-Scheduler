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
 * Implementation of the TaskService interface.
 * Handles business logic for task creation and storage using an in-memory map.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRedisRepository taskRedisRepository;

    @Autowired
    public TaskServiceImpl(TaskRedisRepository taskRedisRepository) {
        this.taskRedisRepository = taskRedisRepository;
    }


    /**
     * Creates a new Task and stores it in Redis.
     *
     * @param request The request object containing task metadata.
     * @return The newly created Task with an assigned UUID and default status.
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
        return taskRedisRepository.save(task);
    }
}
