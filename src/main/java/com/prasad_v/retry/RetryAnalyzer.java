package com.prasad_v.retry;

import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.logging.CustomLogger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * RetryAnalyzer retries failed tests based on framework configuration.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final CustomLogger logger = new CustomLogger(RetryAnalyzer.class);
    private int currentRetryCount = 0;
    private int maxRetryCount;

    public RetryAnalyzer() {
        String configuredCount = ConfigurationManager.getInstance()
                .getConfigProperty("request.retry.count",
                        ConfigurationManager.getInstance().getConfigProperty("retry.count", "2"));
        try {
            maxRetryCount = Integer.parseInt(configuredCount);
        } catch (NumberFormatException e) {
            maxRetryCount = 2;
            logger.warn("Invalid retry count configuration. Falling back to " + maxRetryCount);
        }
    }

    @Override
    public boolean retry(ITestResult result) {
        if (currentRetryCount < maxRetryCount) {
            currentRetryCount++;
            logger.warn("Retrying test " + result.getName() + ". Attempt " + currentRetryCount + "/" + maxRetryCount);
            return true;
        }
        return false;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = Math.max(0, maxRetryCount);
    }
}
