package com.distributedscheduler.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an individual task within a Job's Directed Acyclic Graph (DAG).
 * Each task can have dependencies on other tasks and contains its execution
 * status and logs.
 */
public class Task {

    /**
     * Unique identifier for the task.
     */
    private String id;

    /**
     * Human-readable name for the task.
     */
    private String name;

    /**
     * List of task IDs that this task depends on.
     */
    private List<String> dependencies;

    /**
     * Current status of the task (PENDING, RUNNING, COMPLETED, etc.).
     */
    private TaskStatus status;

    /**
     * Execution log or output of the task.
     */
    private String log;

    /**
     * Default constructor.
     */
    public Task() {
    }


    private int retryCount = 0;         // Number of attempts made
    private int maxRetries = 3;         // Configurable per task
    private List<String> executionLogs = new ArrayList<>();  // Full log history



    /**
     * Constructs a fully-initialized Task.
     *
     * @param id           Unique task ID
     * @param name         Task name
     * @param dependencies List of task IDs this task depends on
     * @param status       Current status of the task
     * @param log          Task execution log or output
     */
    public Task(String id, String name, List<String> dependencies, TaskStatus status, String log) {
        this.id = id;
        this.name = name;
        this.dependencies = dependencies;
        this.status = status;
        this.log = log;
    }

    /** @return Unique task ID */
    public String getId() {
        return id;
    }

    /** @param id Unique task ID */
    public void setId(String id) {
        this.id = id;
    }

    /** @return Task name */
    public String getName() {
        return name;
    }

    /** @param name Task name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return List of task IDs this task depends on */
    public List<String> getDependencies() {
        return dependencies;
    }

    /** @param dependencies List of dependent task IDs */
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    /** @return Current task status */
    public TaskStatus getStatus() {
        return status;
    }

    /** @param status Status of the task */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /** @return Execution log for this task */
    public String getLog() {
        return log;
    }

    /** @param log Task execution log */
    public void setLog(String log) {
        this.log = log;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public List<String> getExecutionLogs() {
        return executionLogs;
    }

    public void setExecutionLogs(List<String> executionLogs) {
        this.executionLogs = executionLogs;
    }

}
