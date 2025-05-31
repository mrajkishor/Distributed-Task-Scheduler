package com.distributedscheduler.util;

public class RetryBackoffCalculator {
    public static int getBackoffDelaySeconds(int retryAttempt) {
        return (int) Math.pow(2, retryAttempt); // Exponential backoff: 2^n
    }
}

/**
 * About this component
 *
 *
 * The `RetryBackoffCalculator` class implements **exponential backoff logic** to calculate the delay (in seconds) before retrying a failed task.
 *
 * ---
 *
 * ### 🔧 Method:
 *
 * ```java
 * public static int getBackoffDelaySeconds(int retryAttempt) {
 *     return (int) Math.pow(2, retryAttempt); // 2^retryAttempt
 * }
 * ```
 *
 * ---
 *
 * ### 💡 Explanation:
 *
 * It returns a delay in **seconds** that increases exponentially with each retry:
 *
 * | `retryAttempt` | Delay (seconds) | Formula |
 * | -------------- | --------------- | ------- |
 * | 1              | 2               | 2¹      |
 * | 2              | 4               | 2²      |
 * | 3              | 8               | 2³      |
 * | 4              | 16              | 2⁴      |
 * | 5              | 32              | 2⁵      |
 *
 * ---
 *
 * ### ✅ Use Case:
 *
 * To **prevent overwhelming the system** or Redis when a task keeps failing repeatedly.
 * Each retry waits longer than the previous one, giving time for potential system recovery.
 *
 *
 *
 *
 * ***/