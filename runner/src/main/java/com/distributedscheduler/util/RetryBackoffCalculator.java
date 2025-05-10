package com.distributedscheduler.util;

public class RetryBackoffCalculator {
    public static int getBackoffDelaySeconds(int retryAttempt) {
        return (int) Math.pow(2, retryAttempt); // Exponential backoff: 2^n
    }
}
