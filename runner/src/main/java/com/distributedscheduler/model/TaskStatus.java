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



/*
* About this component
*
*
* This `TaskStatus` enum defines **all possible lifecycle stages** for a task in your distributed scheduler:

### ✅ Meaning of Each Status:

| Status      | Description                                                                 |
| ----------- | --------------------------------------------------------------------------- |
| `PENDING`   | Task is created but not yet started                                         |
| `RUNNING`   | Task is currently executing                                                 |
| `COMPLETED` | Task finished successfully                                                  |
| `FAILED`    | Task execution failed                                                       |
| `SKIPPED`   | Task was not run (e.g., due to dependency failure or user action)           |
| `RETRYING`  | Task is being retried due to a previous failure                             |
| `DLQ`       | Task failed too many times and is now pushed to the Dead Letter Queue (DLQ) |

This enum helps track task progress and allows the scheduler to decide what to do next based on its current state.

*
*
*
* */