package com.distributedscheduler.service;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.dto.TaskRequest;

import java.util.List;

/**
 * Service interface for managing tasks within the Distributed Task Scheduler.
 * Defines operations for task creation and future extensions like status updates.
 */
public interface TaskService {

    /**
     * Creates a new task based on the given request.
     *
     * @param request The TaskRequest object containing task details.
     * @return The created Task object with generated ID and initial status.
     */
    Task createTask(TaskRequest request);


    Task getTaskById(String tenantId, String taskId);

    void addDependenciesByName(String tenantId, String taskName, List<String> dependsOn);



}
