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


/**
 * about this component
 *
 * The `JobUpdate` class represents the **fields of a Job that can be modified** via a `PUT /jobs/{id}` API.
 *
 * ---
 *
 * ### 🔧 Purpose:
 *
 * It is a **DTO (Data Transfer Object)** used when updating an existing job.
 *
 * ---
 *
 * ### 🧾 Fields:
 *
 * | Field         | Type          | Description                                     |
 * | ------------- | ------------- | ----------------------------------------------- |
 * | `name`        | `String`      | New name for the job.                           |
 * | `retryPolicy` | `RetryPolicy` | Updated retry policy settings.                  |
 * | `schedule`    | `Schedule`    | New job schedule (like cron, fixed delay, etc). |
 *
 * ---
 *
 * ### ✅ Typical Usage:
 *
 * When a client sends a `PUT` request to update a job:
 *
 * ```json
 * {
 *   "name": "Nightly Data Sync",
 *   "retryPolicy": { "maxRetries": 3, "backoff": "EXPONENTIAL" },
 *   "schedule": { "cron": "0 0 * * *" }
 * }
 * ```
 * > Note : "0 0 * * *"" <== this is a CRON expression meaning: run daily at 12:00 AM (midnight).
 * > Check cron expressions : https://crontab.guru/
 *
 *
 * This class maps the payload for backend processing.
 *
 * Let me know if you also want to support partial updates using `PATCH`.
 *
 *
 * **/

/**
 * More to this :
 *
 *
 *
 * ### 🔁 Explanation:
 *
 * #### 1. **`retryPolicy`:**
 *
 * ```json
 * "retryPolicy": {
 *   "maxRetries": 3,
 *   "backoff": "EXPONENTIAL"
 * }
 * ```
 *
 * * **`maxRetries: 3`** → The system will try executing the task up to 3 times if it fails.
 * * **`backoff: "EXPONENTIAL"`** → Each retry waits longer than the last. For example:
 *
 *   * Retry 1 after 1s
 *   * Retry 2 after 2s
 *   * Retry 3 after 4s
 *
 * > This prevents overwhelming the system and gives time for transient issues to recover.
 *
 * ---
 *
 * #### 2. **`schedule`:**
 *
 * ```json
 * "schedule": {
 *   "cron": "0 0 * * *"
 * }
 * ```
 *
 * * This is a [CRON expression](https://crontab.guru/) meaning: **run daily at 12:00 AM** (midnight).
 *
 * ---
 *
 * ### 🧠 How many `backoff` strategies are possible?
 *
 * You can define multiple types (depending on your implementation):
 *
 * | Strategy      | Description                                        |
 * | ------------- | -------------------------------------------------- |
 * | `EXPONENTIAL` | Wait time doubles after each retry.                |
 * | `FIXED`       | Retry after a constant time interval.              |
 * | `LINEAR`      | Retry intervals increase linearly (e.g., +2s).     |
 * | `NONE`        | Retry immediately without delay (not recommended). |
 *
 * You can define any or all of these in your enum `BackoffStrategy`.
 *
 *
 *
 * ***/