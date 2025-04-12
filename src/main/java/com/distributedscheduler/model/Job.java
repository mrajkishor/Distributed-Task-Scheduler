package com.distributedscheduler.model;

import java.util.List;
import java.util.Map;

/**
 * Represents a distributed job consisting of multiple dependent tasks.
 * This class serves as the core model for job scheduling and execution in the
 * system.
 */
public class Job {

    /**
     * Unique identifier for the job.
     */
    private String id;

    /**
     * Human-readable name for the job.
     */
    private String name;

    /**
     * List of tasks forming a Directed Acyclic Graph (DAG).
     */
    private List<Task> tasks;

    /**
     * Retry policy associated with the job in case of failures.
     */
    private RetryPolicy retryPolicy;

    /**
     * Schedule configuration (e.g., cron expression) for the job.
     */
    private Schedule schedule;

    /**
     * Current status of the job (PENDING, RUNNING, COMPLETED, etc.).
     */
    private JobStatus status;

    /**
     * Additional metadata associated with the job.
     */
    private Map<String, String> metadata;

    /**
     * Default constructor for serialization or object mapping.
     */
    public Job() {
    }

    /**
     * Constructs a fully-initialized Job instance.
     *
     * @param id          Unique job ID
     * @param name        Job name
     * @param tasks       List of DAG-based tasks
     * @param retryPolicy Retry policy for job execution
     * @param schedule    Job schedule (cron, recurring, etc.)
     * @param status      Current job status
     * @param metadata    Additional key-value metadata
     */
    public Job(String id, String name, List<Task> tasks, RetryPolicy retryPolicy,
            Schedule schedule, JobStatus status, Map<String, String> metadata) {
        this.id = id;
        this.name = name;
        this.tasks = tasks;
        this.retryPolicy = retryPolicy;
        this.schedule = schedule;
        this.status = status;
        this.metadata = metadata;
    }

    /** @return Unique job ID */
    public String getId() {
        return id;
    }

    /** @param id Unique job ID */
    public void setId(String id) {
        this.id = id;
    }

    /** @return Job name */
    public String getName() {
        return name;
    }

    /** @param name Job name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return List of tasks in the job */
    public List<Task> getTasks() {
        return tasks;
    }

    /** @param tasks List of tasks forming a DAG */
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    /** @return Retry policy of the job */
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    /** @param retryPolicy Retry policy to apply on job failure */
    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    /** @return Schedule of the job */
    public Schedule getSchedule() {
        return schedule;
    }

    /** @param schedule Scheduling configuration */
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    /** @return Current status of the job */
    public JobStatus getStatus() {
        return status;
    }

    /** @param status Status of job execution */
    public void setStatus(JobStatus status) {
        this.status = status;
    }

    /** @return Metadata associated with the job */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /** @param metadata Arbitrary key-value metadata */
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
