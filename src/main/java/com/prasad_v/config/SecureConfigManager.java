package com.prasad_v.config;

import java.util.Base64;

/**
 * Secure configuration manager for handling sensitive credentials
 * Uses environment variables instead of hardcoded values
 */
public class SecureConfigManager {
    private static SecureConfigManager instance;
    private final ConfigurationManager configManager;

    private SecureConfigManager() {
        this.configManager = ConfigurationManager.getInstance();
    }

    public static synchronized SecureConfigManager getInstance() {
        if (instance == null) {
            instance = new SecureConfigManager();
        }
        return instance;
    }

    public String getSecureProperty(String key) {
        if (key == null) {
            return null;
        }
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp;
        }
        String envKey = key.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);
        return envValue != null && !envValue.isBlank() ? envValue : configManager.getProperty(key);
    }

    /**
     * Get decoded credential (if prefixed with base64:)
     */
    public String getDecodedProperty(String key) {
        String value = getSecureProperty(key);
        if (value == null || value.isBlank()) {
            return value;
        }
        if (value.startsWith("base64:")) {
            try {
                return new String(Base64.getDecoder().decode(value.substring(7).trim()));
            } catch (IllegalArgumentException ignored) {
                return value;
            }
        }
        return value;
    }

    public String getUsername() {
        return getSecureProperty("auth.username");
    }

    public String getPassword() {
        return getDecodedProperty("auth.password");
    }

    public String getClientId() {
        return getSecureProperty("auth.client.id");
    }

    public String getClientSecret() {
        return getDecodedProperty("auth.client.secret");
    }
}
