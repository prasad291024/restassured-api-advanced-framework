package com.prasad_v.tests.sample;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Intentionally fails on first attempt and passes on retry to validate RetryListener wiring.
 */
public class RetryListenerVerificationTest {

    private static final AtomicInteger ATTEMPTS = new AtomicInteger(0);

    @Test
    public void shouldPassOnSecondAttemptWhenRetryIsEnabled() {
        int attempt = ATTEMPTS.incrementAndGet();
        Assert.assertTrue(attempt >= 2, "Intentional first-attempt failure for retry verification.");
    }
}
