package com.distributedscheduler.dto;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a task response returned by the API after submission or retrieval.
 */
public class TaskResponse {

    private String id;
    private String name;
    private Map<String, Object> payload;
    private int priority;
    private int delaySeconds;
    private List<String> dependencies;
    private TaskStatus status;
    private String log;
    private int retryCount;
    private int maxRetries;
    private List<String> executionLogs;
    private String timestamp;

    /** Default constructor. */
    public TaskResponse() {}

    /**
     * Constructs a response from a Task object.
     *
     * @param task The domain model Task
     */
    public TaskResponse(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.payload = task.getPayload();
        this.priority = task.getPriority();
        this.delaySeconds = task.getDelaySeconds();
        this.status = task.getStatus();
        this.log = task.getLog();
        this.retryCount = task.getRetryCount();
        this.maxRetries = task.getMaxRetries();
        this.executionLogs = task.getExecutionLogs();
        this.timestamp = Instant.now().toString();
    }



    // Getters and Setters for all fields...

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }


    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getLog() {
        return log;
    }

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
