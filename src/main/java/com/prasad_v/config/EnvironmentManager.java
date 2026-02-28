package com.prasad_v.config;

/**
 * Environment manager to handle environment-specific configurations
 * This class loads the appropriate properties file based on the selected environment
 */
public class EnvironmentManager {
    private static final String CONFIG_PATH = "config/";
    private static final String DEFAULT_ENV = "dev";

    private static EnvironmentManager instance;
    private String currentEnvironment;
    private ConfigurationManager configManager;

    private EnvironmentManager() {
        configManager = ConfigurationManager.getInstance();
        currentEnvironment = System.getProperty("env", DEFAULT_ENV);
    }

    /**
     * Get the singleton instance of EnvironmentManager
     *
     * @return EnvironmentManager instance
     */
    public static synchronized EnvironmentManager getInstance() {
        if (instance == null) {
            instance = new EnvironmentManager();
        }
        return instance;
    }

    /**
     * Initialize the environment configuration
     * Loads the appropriate properties file based on the current environment
     *
     * @throws IOException If the properties file cannot be loaded
     */
    public void initializeEnvironment() {
        String configFile = CONFIG_PATH + currentEnvironment + ".properties";
        configManager.loadConfigFromResource(configFile);
        EnvironmentConfigValidator.validate(currentEnvironment, configManager);
    }

    /**
     * Set the environment and load its configuration
     *
     * @param environment Environment name (e.g., "dev", "qa", "prod")
     * @throws IOException If the properties file cannot be loaded
     */
    public void setEnvironment(String environment) {
        this.currentEnvironment = environment;
        initializeEnvironment();
    }

    /**
     * Get the current environment name
     *
     * @return Current environment name
     */
    public String getCurrentEnvironment() {
        return currentEnvironment;
    }

    /**
     * Get the base URL for API calls from the current environment configuration
     *
     * @return Base URL string
     */
    public String getBaseUrl() {
        String baseUrl = configManager.getProperty("api.base.url");
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = configManager.getProperty("api.baseUrl");
        }
        return baseUrl;
    }

    /**
     * Get connection timeout value in milliseconds
     *
     * @return Connection timeout in milliseconds
     */
    public int getConnectionTimeout() {
        return configManager.getIntProperty("api.connection.timeout", 30000);
    }

    /**
     * Get socket timeout value in milliseconds
     *
     * @return Socket timeout in milliseconds
     */
    public int getSocketTimeout() {
        return configManager.getIntProperty("api.socket.timeout", 30000);
    }

    /**
     * Check if request/response logging is enabled
     *
     * @return true if logging is enabled, false otherwise
     */
    public boolean isLoggingEnabled() {
        return configManager.getBooleanProperty("api.logging.enabled", true);
    }

    /**
     * Get a specific property from the current environment configuration
     *
     * @param key Property key
     * @return Property value or null if not found
     */
    public String getProperty(String key) {
        return configManager.getProperty(key);
    }

    /**
     * Get a specific property from the current environment configuration with a default value
     *
     * @param key Property key
     * @param defaultValue Default value to return if property is not found
     * @return Property value or default value if not found
     */
    public String getProperty(String key, String defaultValue) {
        return configManager.getProperty(key, defaultValue);
    }
}
