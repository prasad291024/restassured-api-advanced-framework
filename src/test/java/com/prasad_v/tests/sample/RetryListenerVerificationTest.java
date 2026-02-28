package com.prasad_v.tests.sample;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Verifies that RetryListener is active by failing once and passing on retry.
 */
public class RetryListenerVerificationTest {

    private static final AtomicInteger ATTEMPT_COUNTER = new AtomicInteger(0);

    @Test
    public void shouldPassOnSecondAttemptWhenRetryListenerIsEnabled() {
        int attempt = ATTEMPT_COUNTER.incrementAndGet();
        Assert.assertTrue(attempt >= 2, "Intentional first-attempt failure to validate retry behavior.");
    }
}
