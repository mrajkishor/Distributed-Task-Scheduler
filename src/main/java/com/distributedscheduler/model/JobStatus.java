package com.distributedscheduler.model;

/**
 * Enum representing the current lifecycle status of a Job in the Distributed Task Scheduler.
 */
public enum JobStatus {

    /**
     * The job is created but has not started execution yet.
     */
    PENDING,

    /**
     * The job is currently running.
     */
    RUNNING,

    /**
     * The job has completed all its tasks successfully.
     */
    COMPLETED,

    /**
     * The job has failed due to an error in one or more tasks.
     */
    FAILED
}
