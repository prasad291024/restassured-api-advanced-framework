package com.prasad_v.config;

import com.prasad_v.exceptions.ConfigurationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates environment configuration and fails fast for invalid qa/prod setups.
 */
public final class EnvironmentConfigValidator {

    private EnvironmentConfigValidator() {
    }

    public static void validate(String environment, ConfigurationManager configManager) {
        if (environment == null) {
            return;
        }
        String env = environment.trim().toLowerCase();
        if (!"qa".equals(env) && !"prod".equals(env)) {
            return;
        }

        List<String> errors = new ArrayList<>();
        String baseUrl = firstNonBlank(
                configManager.getProperty("api.base.url"),
                configManager.getProperty("api.baseUrl")
        );
        String tokenUrl = configManager.getProperty("auth.token.url", "");

        validateUrl("api.base.url", baseUrl, errors);
        validateUrl("auth.token.url", tokenUrl, errors);

        requireNonBlank("auth.client.id", configManager.getProperty("auth.client.id"), errors);
        requireNonBlank("auth.client.secret", configManager.getProperty("auth.client.secret"), errors);
        requireNonBlank("auth.username", configManager.getProperty("auth.username"), errors);
        requireNonBlank("auth.password", configManager.getProperty("auth.password"), errors);

        if (!errors.isEmpty()) {
            throw new ConfigurationException("Invalid " + env + " configuration: " + String.join("; ", errors));
        }
    }

    private static void validateUrl(String key, String value, List<String> errors) {
        if (value == null || value.isBlank()) {
            errors.add(key + " is missing");
            return;
        }
        String normalized = value.trim().toLowerCase();
        if (!(normalized.startsWith("http://") || normalized.startsWith("https://"))) {
            errors.add(key + " must start with http:// or https://");
        }
        if (normalized.contains("example.com")) {
            errors.add(key + " still uses placeholder domain");
        }
    }

    private static void requireNonBlank(String key, String value, List<String> errors) {
        if (value == null || value.isBlank()) {
            errors.add(key + " is missing");
        }
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
