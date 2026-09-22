package com.prasad_v.validation;

import com.prasad_v.exceptions.APIException;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for validating API response times against defined thresholds.
 * Provides methods to verify if responses meet performance requirements.
 */
public class ResponseTimeValidator {

    private static final Logger logger = LogManager.getLogger(ResponseTimeValidator.class);

    /**
     * Validates if the response time is within the specified threshold.
     *
     * @param response The API response to validate
     * @param thresholdInMillis The maximum acceptable response time in milliseconds
     * @return true if the response time is within the threshold, false otherwise
     */
    public static boolean validateResponseTime(Response response, long thresholdInMillis) {
        long responseTime = response.getTime();
        boolean isValid = responseTime <= thresholdInMillis;

        if (isValid) {
            logger.info("Response time validation passed: {} ms (threshold: {} ms)",
                    responseTime, thresholdInMillis);
        } else {
            logger.warn("Response time validation failed: {} ms exceeds threshold of {} ms",
                    responseTime, thresholdInMillis);
        }

        return isValid;
    }

    /**
     * Validates if the response time is within the specified threshold and throws an exception if not.
     *
     * @param response The API response to validate
     * @param thresholdInMillis The maximum acceptable response time in milliseconds
     * @throws APIException if the response time exceeds the threshold
     */
    public static void assertResponseTime(Response response, long thresholdInMillis) {
        long responseTime = response.getTime();

        if (responseTime > thresholdInMillis) {
            String errorMessage = String.format("Response time (%d ms) exceeds the threshold of %d ms",
                    responseTime, thresholdInMillis);
            logger.error(errorMessage);
            throw new APIException(errorMessage);
        }

        logger.info("Response time assertion passed: {} ms (threshold: {} ms)",
                responseTime, thresholdInMillis);
    }

    /**
     * Categorizes the response time based on predefined performance tiers.
     *
     * @param response The API response to analyze
     * @return A string describing the performance category ("Excellent", "Good", "Fair", "Poor")
     */
    public static String categorizeResponseTime(Response response) {
        long responseTime = response.getTime();
        String category;

        if (responseTime <= 100) {
            category = "Excellent";
        } else if (responseTime <= 500) {
            category = "Good";
        } else if (responseTime <= 1000) {
            category = "Fair";
        } else {
            category = "Poor";
        }

        logger.info("Response time category: {} ({} ms)", category, responseTime);
        return category;
    }

    /**
     * Calculates the difference between the actual response time and the expected threshold.
     *
     * @param response The API response to analyze
     * @param thresholdInMillis The expected threshold in milliseconds
     * @return The difference between actual and expected response times
     */
    public static long getResponseTimeDifference(Response response, long thresholdInMillis) {
        long responseTime = response.getTime();
        long difference = responseTime - thresholdInMillis;

        logger.debug("Response time difference: {} ms (actual: {} ms, threshold: {} ms)",
                difference, responseTime, thresholdInMillis);

        return difference;
    }
}