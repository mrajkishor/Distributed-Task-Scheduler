
package com.distributedscheduler.model;

import com.distributedscheduler.model.RetryPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RetryPolicyTest {

    @Test
    public void testRetryPolicySettersAndGetters() {
        RetryPolicy policy = new RetryPolicy();
        policy.setMaxRetries(4);
        policy.setDelaySeconds(120);

        assertEquals(4, policy.getMaxRetries());
        assertEquals(120, policy.getDelaySeconds());
    }

    @Test
    public void testRetryPolicyConstructor() {
        RetryPolicy policy = new RetryPolicy(2, 30);
        assertEquals(2, policy.getMaxRetries());
        assertEquals(30, policy.getDelaySeconds());
    }
}
