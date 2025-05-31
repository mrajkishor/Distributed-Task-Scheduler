package com.distributedscheduler.model;

/**
 * Represents the retry policy for a job in case of task failure.
 * Defines how many times a job should retry and the delay between retries.
 */
public class RetryPolicy {

    /**
     * Maximum number of retry attempts allowed for the job.
     */
    private int maxRetries;

    /**
     * Delay between retries in seconds.
     */
    private int delaySeconds;

    /**
     * Default constructor.
     */
    public RetryPolicy() {
    }

    /**
     * Constructs a RetryPolicy with the specified max retries and delay.
     *
     * @param maxRetries   Maximum retry attempts
     * @param delaySeconds Delay in seconds between retries
     */
    public RetryPolicy(int maxRetries, int delaySeconds) {
        this.maxRetries = maxRetries;
        this.delaySeconds = delaySeconds;
    }

    /** @return Maximum number of retry attempts */
    public int getMaxRetries() {
        return maxRetries;
    }

    /** @param maxRetries Maximum number of retry attempts */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /** @return Delay in seconds between retries */
    public int getDelaySeconds() {
        return delaySeconds;
    }

    /** @param delaySeconds Delay in seconds between retries */
    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }
}

/***
 * About this component
 *
 * This `RetryPolicy` class supports only **fixed delay retries**, not
 * exponential or other backoff strategies.
 *
 * ### 🔁 Meaning of Fields:
 *
 * * **`maxRetries`**: Maximum retry attempts if the task fails (e.g., `3` means
 * it can retry 3 times).
 * * **`delaySeconds`**: Fixed number of seconds to wait between each retry
 * (e.g., `10` means retry every 10 seconds).
 *
 * ### ❗Limitation:
 *
 * There is **no field for `backoff strategy`** like `EXPONENTIAL` in this
 * class.
 * If you want to support multiple strategies (like `EXPONENTIAL`, `LINEAR`,
 * `NONE`), you should:
 *
 * * Add a new field: `private BackoffStrategy backoffStrategy;`
 * * Create an enum `BackoffStrategy { FIXED, EXPONENTIAL, LINEAR }`
 *
 * Future Scope:
 * updating this class to support backoff strategies.
 *
 *
 *
 *
 ***/
