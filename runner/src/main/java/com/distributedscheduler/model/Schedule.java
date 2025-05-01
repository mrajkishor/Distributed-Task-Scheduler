package com.distributedscheduler.model;

/**
 * Represents the scheduling configuration for a job.
 * Supports cron-based scheduling and recurring job execution.
 */
public class Schedule {

    /**
     * Cron expression defining when the job should be triggered.
     */
    private String cronExpression;

    /**
     * Indicates whether the job should run on a recurring basis.
     */
    private boolean isRecurring;

    /**
     * Default constructor.
     */
    public Schedule() {
    }

    /**
     * Constructs a Schedule with a given cron expression and recurrence flag.
     *
     * @param cronExpression Cron expression for scheduling the job
     * @param isRecurring    True if the job is recurring, false otherwise
     */
    public Schedule(String cronExpression, boolean isRecurring) {
        this.cronExpression = cronExpression;
        this.isRecurring = isRecurring;
    }

    /** @return Cron expression defining the schedule */
    public String getCronExpression() {
        return cronExpression;
    }

    /** @param cronExpression Cron expression to set */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    /** @return True if the job is recurring, false otherwise */
    public boolean isRecurring() {
        return isRecurring;
    }

    /** @param recurring True to make the job recurring */
    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }
}
