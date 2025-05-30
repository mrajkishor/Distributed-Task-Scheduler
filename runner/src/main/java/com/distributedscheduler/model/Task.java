package com.distributedscheduler.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * Represents an individual task within a Job's Directed Acyclic Graph (DAG).
 * Each task can have dependencies on other tasks and contains its execution status and logs.
 */


public class Task {

    @Id
    private String id;                              // Unique identifier for the task
    private String name;                            // Human-readable task name
    private TaskStatus status;                      // Task state: PENDING, RUNNING, etc.
    private String log;                             // Single log line (e.g., result or error)

    private int retryCount = 0;                     // Retry attempts made
    private int maxRetries = 3;                     // Retry limit
    private List<String> executionLogs = new ArrayList<>();  // Full execution log history

    private Map<String, Object> payload;            // Dynamic input for the task
    private int priority = 0;                       // Task priority (higher = more urgent)
    private int delaySeconds = 0;                   // Optional delay before task execution


    private String notificationUrl;
    private String notificationEmail;

    private String tenantId = "default"; // For now, hardcoded

    //Field for Delay Calculation
    private long createdAt = System.currentTimeMillis() / 1000L;


    public Task() {
    }

    public Task(String id, String name, TaskStatus status, String log) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.log = log;
        this.createdAt = System.currentTimeMillis() / 1000L;
    }

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


    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }
}
