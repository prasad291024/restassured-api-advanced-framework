package com.prasad_v.logging;

import java.util.regex.Pattern;

public class LogSanitizer {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(\"password\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOKEN_PATTERN = Pattern.compile("(\"token\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern AUTH_HEADER_PATTERN = Pattern.compile("(Authorization:\\s*)(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern API_KEY_PATTERN = Pattern.compile("(\"api[_-]?key\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile("\\b\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");

    private static final String MASK = "***REDACTED***";

    public static String sanitize(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        String sanitized = content;
        sanitized = PASSWORD_PATTERN.matcher(sanitized).replaceAll("$1" + MASK + "$3");
        sanitized = TOKEN_PATTERN.matcher(sanitized).replaceAll("$1" + MASK + "$3");
        sanitized = AUTH_HEADER_PATTERN.matcher(sanitized).replaceAll("$1" + MASK);
        sanitized = API_KEY_PATTERN.matcher(sanitized).replaceAll("$1" + MASK + "$3");
        sanitized = CREDIT_CARD_PATTERN.matcher(sanitized).replaceAll(MASK);
        sanitized = EMAIL_PATTERN.matcher(sanitized).replaceAll("$1@" + MASK);

        return sanitized;
    }

    public static String sanitizeHeaders(String headers) {
        return sanitize(headers);
    }

    public static String sanitizeBody(String body) {
        return sanitize(body);
    }
}
