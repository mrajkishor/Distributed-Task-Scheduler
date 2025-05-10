package com.distributedscheduler.model;

/**
 * Enum representing the current execution status of an individual task
 * within a distributed job.
 */
public enum TaskStatus {

    /**
     * The task is defined but has not started execution yet.
     */
    PENDING,

    /**
     * The task is currently being executed.
     */
    RUNNING,

    /**
     * The task has completed successfully.
     */
    COMPLETED,

    /**
     * The task failed during execution.
     */
    FAILED,

    /**
     * The task was intentionally skipped (e.g., due to unmet dependencies).
     */
    SKIPPED,


    /**
     * The task is currently being retried due to a previous failure.
     */
    RETRYING,

    /**
     * The task has exceeded retry attempts and moved to DLQ.
     */
    DLQ
}
