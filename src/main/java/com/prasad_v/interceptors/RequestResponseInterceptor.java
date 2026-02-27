package com.prasad_v.interceptors;

import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.logging.CustomLogger;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interceptor for HTTP requests and responses.
 * This class provides functionality to intercept, log, and measure API requests and responses.
 */
public class RequestResponseInterceptor implements Filter {

    private static final CustomLogger logger = new CustomLogger(RequestResponseInterceptor.class);
    private static final Map<String, RequestInfo> requestInfoMap = new ConcurrentHashMap<>();
    private static final String RESPONSE_TIME_PROPERTY = "responseTimeInMs";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    /**
     * Inner class to hold request information
     */
    private static class RequestInfo {
        final Instant startTime;
        final String requestId;
        final String method;
        final String url;
        final String requestBody;

        RequestInfo(Instant startTime, String requestId, String method, String url, String requestBody) {
            this.startTime = startTime;
            this.requestId = requestId;
            this.method = method;
            this.url = url;
            this.requestBody = requestBody;
        }
    }

    /**
     * Creates a new RequestSpecification with this interceptor added
     *
     * @param requestSpecification The original request specification
     * @return A new RequestSpecification with the interceptor added
     */
    public static RequestSpecification addInterceptor(RequestSpecification requestSpecification) {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.addFilter(new RequestResponseInterceptor());

        boolean enableLogging = ConfigurationManager.getInstance().getBooleanProperty("api.logging.enabled", true);
        if (enableLogging) {
            // Use try-with-resources to ensure streams are closed
            try (ByteArrayOutputStream requestLog = new ByteArrayOutputStream();
                 ByteArrayOutputStream responseLog = new ByteArrayOutputStream();
                 PrintStream requestPrintStream = new PrintStream(requestLog);
                 PrintStream responsePrintStream = new PrintStream(responseLog)) {

                builder.addFilter(new RequestLoggingFilter(LogDetail.ALL, requestPrintStream));
                builder.addFilter(new ResponseLoggingFilter(LogDetail.ALL, responsePrintStream));

                // You can access the logged content here if needed:
                // String requestLogContent = requestLog.toString();
                // String responseLogContent = responseLog.toString();

            } catch (Exception e) {
                logger.warn("Failed to setup logging filters: " + e.getMessage());
            }
        }

        return requestSpecification.spec(builder.build());
    }

    /**
     * The filter implementation that intercepts requests and responses
     *
     * @param requestSpec The request specification
     * @param responseSpec The response specification
     * @param filterContext The filter context
     * @return The response after processing
     */
    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext filterContext) {

        String requestId = UUID.randomUUID().toString();
        Instant startTime = Instant.now();

        // Add correlation ID header if not already present
        if (!requestSpec.hasHeader(CORRELATION_ID_HEADER)) {
            requestSpec.header(CORRELATION_ID_HEADER, requestId);
        }

        // Store request info
        String requestBody = requestSpec.getBody() != null ? requestSpec.getBody().toString() : null;
        RequestInfo info = new RequestInfo(startTime, requestId, requestSpec.getMethod(),
                requestSpec.getURI(), requestBody);
        requestInfoMap.put(requestId, info);

        // Log request
        logger.debug("Starting API request [" + requestId + "]: " + info.method + " " + info.url);

        Response response = null;
        try {
            // Execute request and capture response
            response = filterContext.next(requestSpec, responseSpec);

            // Calculate duration
            Instant endTime = Instant.now();
            long durationMs = ChronoUnit.MILLIS.between(startTime, endTime);

            // Log response
            int statusCode = response.getStatusCode();
            logger.debug("API response [" + requestId + "]: Status " + statusCode +
                    " (" + durationMs + "ms): " + info.method + " " + info.url);

            // Add response time as a property to the response object (safer casting)
            setResponseTime(response, durationMs);

            // Log extra information for non-2xx responses
            if (statusCode < 200 || statusCode >= 300) {
                String responseBody = response.getBody() != null ? response.getBody().asString() : "No body";
                responseBody = truncate(responseBody, 1000);
                logger.warn("Non-successful API response [" + requestId + "]: " + statusCode + " - " + responseBody);
            }

            return response;

        } catch (Exception e) {
            logger.error("Exception during API request [" + requestId + "]: " + e.getMessage(), e);
            throw e;
        } finally {
            // Always remove request info from map to avoid memory leaks
            requestInfoMap.remove(requestId);
        }
    }

    /**
     * Safely sets response time property
     */
    private void setResponseTime(Response response, long durationMs) {
        try {
            if (response instanceof RestAssuredResponseImpl) {
                ((RestAssuredResponseImpl) response).setProperty(RESPONSE_TIME_PROPERTY, durationMs);
            }
        } catch (Exception e) {
            logger.warn("Failed to set response time property: " + e.getMessage());
        }
    }

    /**
     * Gets response time for a specific request
     *
     * @param response The response object
     * @return Response time in milliseconds or -1 if not available
     */
    public static long getResponseTime(Response response) {
        try {
            if (response instanceof RestAssuredResponseImpl) {
                Object responseTime = ((RestAssuredResponseImpl) response).getProperty(RESPONSE_TIME_PROPERTY);
                if (responseTime instanceof Long) {
                    return (Long) responseTime;
                }
            }
        } catch (Exception e) {
            logger.debug("Could not retrieve response time: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Clears the interceptor's internal state
     * Should be called periodically to ensure no memory leaks
     */
    public static void clearState() {
        requestInfoMap.clear();
        logger.debug("RequestResponseInterceptor state cleared");
    }

    /**
     * Gets the current number of tracked requests (for monitoring)
     */
    public static int getTrackedRequestCount() {
        return requestInfoMap.size();
    }

    /**
     * Helper method to truncate strings that are too large
     *
     * @param input The input string
     * @param maxLength Maximum length before truncation
     * @return The truncated string
     */
    private static String truncate(String input, int maxLength) {
        if (input == null) {
            return "null";
        }
        return input.length() <= maxLength ? input : input.substring(0, maxLength - 3) + "...";
    }
}