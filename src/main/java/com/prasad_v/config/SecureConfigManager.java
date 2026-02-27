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

    /**
     * Get credential from environment variable first, fallback to config file
     */
    public String getSecureProperty(String key) {
        String envKey = key.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);
        return envValue != null ? envValue : configManager.getProperty(key);
    }

    /**
     * Get decoded credential (if base64 encoded in env)
     */
    public String getDecodedProperty(String key) {
        String value = getSecureProperty(key);
        if (value != null && isBase64Encoded(value)) {
            return new String(Base64.getDecoder().decode(value));
        }
        return value;
    }

    private boolean isBase64Encoded(String value) {
        try {
            Base64.getDecoder().decode(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
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
