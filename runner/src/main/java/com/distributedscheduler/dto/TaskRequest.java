package com.distributedscheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.util.List;
import java.util.Map;

/**
 * Represents a request payload to submit a task as part of a job in the Distributed Task Scheduler.
 * Used in the POST /tasks API to receive task creation data from clients.
 */
public class TaskRequest {

    /**
     * Unique identifier for the task (optional for client).
     */
    private String id;

    /**
     * Human-readable name for the task.
     */
    @NotBlank(message = "Task name must not be blank")
    @Size(max = 100, message = "Task name must not exceed 100 characters")
    private String name;

    /**
     * Payload containing task-specific data. Can be any JSON-like structure.
     */
    @NotNull(message = "Payload must not be null")
    private Map<String, Object> payload;

    /**
     * Task execution priority (e.g., 1 to 10).
     */
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 10, message = "Priority must not exceed 10")
    private int priority = 5;

    /**
     * Delay in seconds before the task becomes eligible to run.
     */
    @Min(value = 0, message = "Delay seconds must be non-negative")
    private int delaySeconds = 0;

    /**
     * List of task IDs that this task depends on (for DAG execution).
     */
    private List<@NotBlank(message = "Dependency ID must not be blank") String> dependencies;

    /**
     * Maximum number of retry attempts allowed for this task.
     */
    @Min(value = 0, message = "Max retries must be non-negative")
    private int maxRetries = 3;

    /** Default constructor. */
    public TaskRequest() {}

    /**
     * Constructs a fully-initialized TaskRequest.
     *
     * @param id           Unique task ID
     * @param name         Task name
     * @param payload      Task execution payload
     * @param priority     Execution priority
     * @param delaySeconds Delay before execution
     * @param dependencies Dependent task IDs
     * @param maxRetries   Maximum retry attempts
     */
    public TaskRequest(String id, String name, Map<String, Object> payload, int priority,
                       int delaySeconds, List<String> dependencies, int maxRetries) {
        this.id = id;
        this.name = name;
        this.payload = payload;
        this.priority = priority;
        this.delaySeconds = delaySeconds;
        this.dependencies = dependencies;
        this.maxRetries = maxRetries;
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

    /** @return Task payload */
    public Map<String, Object> getPayload() {
        return payload;
    }

    /** @param payload Task-specific data */
    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    /** @return Task priority */
    public int getPriority() {
        return priority;
    }

    /** @param priority Task priority */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /** @return Delay in seconds before execution */
    public int getDelaySeconds() {
        return delaySeconds;
    }

    /** @param delaySeconds Delay before task is eligible for execution */
    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    /** @return List of dependent task IDs */
    public List<String> getDependencies() {
        return dependencies;
    }

    /** @param dependencies List of task IDs this task depends on */
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    /** @return Max retry attempts */
    public int getMaxRetries() {
        return maxRetries;
    }

    /** @param maxRetries Max retry attempts */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
}
