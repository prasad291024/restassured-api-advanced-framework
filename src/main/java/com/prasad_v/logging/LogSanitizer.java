package com.prasad_v.logging;

import java.util.regex.Pattern;

public class LogSanitizer {
    private static final String MASK = "***REDACTED***";

    // JSON body patterns
    private static final Pattern JSON_PASSWORD_PATTERN = Pattern.compile(
            "(\"(?:password|passwd|pwd)\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern JSON_TOKEN_PATTERN = Pattern.compile(
            "(\"(?:token|access_?token|refresh_?token|id_?token)\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern JSON_SECRET_PATTERN = Pattern.compile(
            "(\"(?:client_?secret|secret|private_?key)\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern JSON_API_KEY_PATTERN = Pattern.compile(
            "(\"(?:api[_-]?key|x[_-]?api[_-]?key)\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);

    // Headers & Auth tokens
    private static final Pattern AUTH_HEADER_PATTERN = Pattern.compile(
            "(Authorization:\\s*)(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern BEARER_PATTERN = Pattern.compile(
            "(Bearer\\s+)([A-Za-z0-9\\-._~+/]+=*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern BASIC_AUTH_PATTERN = Pattern.compile(
            "(Basic\\s+)([A-Za-z0-9+/=]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern COOKIE_TOKEN_PATTERN = Pattern.compile(
            "(token=)([^;\\s,]+)", Pattern.CASE_INSENSITIVE);

    // Query parameters and form data
    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile(
            "((?:password|token|access_token|client_secret|api_key)=)([^&\\s]+)", Pattern.CASE_INSENSITIVE);

    // PII and Financial
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile(
            "\\b(?:\\d{4}[ -]?){3}\\d{4}\\b");
    private static final Pattern SSN_PATTERN = Pattern.compile(
            "\\b\\d{3}-\\d{2}-\\d{4}\\b");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");

    public static String sanitize(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        String sanitized = content;
        sanitized = JSON_PASSWORD_PATTERN.matcher(sanitized).replaceAll("$1" + MASK + "$3");
        sanitized = JSON_TOKEN_PATTERN.matcher(sanitized).replaceAll("$1" + MASK + "$3");
        sanitized = JSON_SECRET_PATTERN.matcher(sanitized).replaceAll("$1" + MASK + "$3");
        sanitized = JSON_API_KEY_PATTERN.matcher(sanitized).replaceAll("$1" + MASK + "$3");
        sanitized = AUTH_HEADER_PATTERN.matcher(sanitized).replaceAll("$1" + MASK);
        sanitized = BEARER_PATTERN.matcher(sanitized).replaceAll("$1" + MASK);
        sanitized = BASIC_AUTH_PATTERN.matcher(sanitized).replaceAll("$1" + MASK);
        sanitized = COOKIE_TOKEN_PATTERN.matcher(sanitized).replaceAll("$1" + MASK);
        sanitized = QUERY_PARAM_PATTERN.matcher(sanitized).replaceAll("$1" + MASK);
        sanitized = CREDIT_CARD_PATTERN.matcher(sanitized).replaceAll(MASK);
        sanitized = SSN_PATTERN.matcher(sanitized).replaceAll(MASK);
        sanitized = EMAIL_PATTERN.matcher(sanitized).replaceAll("$1@" + MASK);

        return sanitized;
    }

    public static String sanitizeHeaders(String headers) {
        return sanitize(headers);
    }

    public static String sanitizeBody(String body) {
        return sanitize(body);
    }

    public static String sanitizeUri(String uri) {
        return sanitize(uri);
    }
}
