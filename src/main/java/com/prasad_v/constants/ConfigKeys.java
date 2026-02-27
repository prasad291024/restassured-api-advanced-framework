package com.prasad_v.constants;

/**
 * Configuration property keys used across the framework
 */
public final class ConfigKeys {
    
    // API Configuration
    public static final String API_BASE_URL = "api.base.url";
    public static final String API_VERSION = "api.version";
    
    // Authentication
    public static final String AUTH_CLIENT_ID = "auth.client.id";
    public static final String AUTH_CLIENT_SECRET = "auth.client.secret";
    public static final String AUTH_USERNAME = "auth.username";
    public static final String AUTH_PASSWORD = "auth.password";
    public static final String AUTH_TOKEN_URL = "auth.token.url";
    public static final String AUTH_TOKEN_REFRESH_URL = "auth.token.refresh.url";
    
    // Request Configuration
    public static final String REQUEST_TIMEOUT = "request.timeout";
    public static final String REQUEST_RETRY_COUNT = "request.retry.count";
    public static final String REQUEST_RETRY_DELAY = "request.retry.delay";
    
    // Proxy
    public static final String PROXY_ENABLED = "proxy.enabled";
    public static final String PROXY_HOST = "proxy.host";
    public static final String PROXY_PORT = "proxy.port";
    public static final String PROXY_USERNAME = "proxy.username";
    public static final String PROXY_PASSWORD = "proxy.password";
    
    // Logging
    public static final String LOGGING_REQUEST_ENABLE = "logging.request.enable";
    public static final String LOGGING_RESPONSE_ENABLE = "logging.response.enable";
    public static final String LOGGING_REQUEST_HEADERS = "logging.request.headers";
    public static final String LOGGING_RESPONSE_HEADERS = "logging.response.headers";
    public static final String LOGGING_REQUEST_BODY = "logging.request.body";
    public static final String LOGGING_RESPONSE_BODY = "logging.response.body";
    
    // Mock Server
    public static final String MOCK_SERVER_ENABLED = "mock.server.enabled";
    public static final String MOCK_SERVER_PORT = "mock.server.port";
    public static final String MOCK_SERVER_HOST = "mock.server.host";
    
    // Test Data
    public static final String TEST_DATA_PATH = "test.data.path";
    
    // Performance Thresholds
    public static final String PERF_THRESHOLD_DEFAULT = "perf.threshold.default";
    public static final String PERF_THRESHOLD_GET = "perf.threshold.get";
    public static final String PERF_THRESHOLD_POST = "perf.threshold.post";
    public static final String PERF_THRESHOLD_PUT = "perf.threshold.put";
    public static final String PERF_THRESHOLD_DELETE = "perf.threshold.delete";
    
    // SSL
    public static final String SSL_VERIFY = "ssl.verify";
    public static final String SSL_KEYSTORE_PATH = "ssl.keystore.path";
    public static final String SSL_KEYSTORE_PASSWORD = "ssl.keystore.password";
    
    private ConfigKeys() {
        throw new IllegalStateException("Constants class");
    }
}
