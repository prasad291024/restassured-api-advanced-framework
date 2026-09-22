package com.prasad_v.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomLogger {
    private final Logger logger;

    public CustomLogger(Class<?> clazz) {
        this.logger = LogManager.getLogger(clazz);
    }

    public void info(String message) {
        logger.info(formatMessage(message));
    }

    public void debug(String message) {
        logger.debug(formatMessage(message));
    }

    public void warn(String message) {
        logger.warn(formatMessage(message));
    }

    public void error(String message) {
        logger.error(formatMessage(message));
    }

    public void error(String message, Throwable throwable) {
        logger.error(formatMessage(message), throwable);
    }

    public void logRequest(String endpoint, String method, String headers, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n====== REQUEST ======\n");
        sb.append("Endpoint: ").append(endpoint).append("\n");
        sb.append("Method: ").append(method).append("\n");
        sb.append("Headers: ").append(LogSanitizer.sanitizeHeaders(headers)).append("\n");
        if (body != null && !body.isEmpty()) {
            sb.append("Body: ").append(LogSanitizer.sanitizeBody(body)).append("\n");
        }
        sb.append("=====================\n");
        logger.info(sb.toString());
    }

    public void logResponse(int statusCode, long responseTime, String headers, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n====== RESPONSE ======\n");
        sb.append("Status Code: ").append(statusCode).append("\n");
        sb.append("Response Time: ").append(responseTime).append(" ms\n");
        sb.append("Headers: ").append(LogSanitizer.sanitizeHeaders(headers)).append("\n");
        if (body != null && !body.isEmpty()) {
            sb.append("Body: ").append(LogSanitizer.sanitizeBody(body)).append("\n");
        }
        sb.append("======================\n");
        logger.info(sb.toString());
    }

    private String formatMessage(String message) {
        return LogSanitizer.sanitize(message);
    }
}