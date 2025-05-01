package com.distributedscheduler.model;

/**
 * Represents the updatable fields of a Job.
 * Used in PUT /jobs/{id} API for updating job metadata.
 */
public class JobUpdate {

    private String name;
    private RetryPolicy retryPolicy;
    private Schedule schedule;

    public JobUpdate() {
    }

    public JobUpdate(String name, RetryPolicy retryPolicy, Schedule schedule) {
        this.name = name;
        this.retryPolicy = retryPolicy;
        this.schedule = schedule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
