package com.prasad_v.constants;

/**
 * Constants used across the API automation framework
 * This class contains static final values for various API-related constants
 */
public class APIConstants {

    // Restful Booker API Endpoints
    public static final String BASE_URL = "https://restful-booker.herokuapp.com";
    public static final String CREATE_UPDATE_BOOKING_URL = "/booking";
    public static final String AUTH_URL = "/auth";
    public static final String PING_URL = "/ping";

    // HTTP Methods
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_DELETE = "DELETE";
    public static final String HTTP_PATCH = "PATCH";
    public static final String HTTP_HEAD = "HEAD";
    public static final String HTTP_OPTIONS = "OPTIONS";

    // HTTP Status Codes
    public static final int SC_OK = 200;
    public static final int SC_CREATED = 201;
    public static final int SC_ACCEPTED = 202;
    public static final int SC_NO_CONTENT = 204;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_UNAUTHORIZED = 401;
    public static final int SC_FORBIDDEN = 403;
    public static final int SC_NOT_FOUND = 404;
    public static final int SC_CONFLICT = 409;
    public static final int SC_PRECONDITION_FAILED = 412;
    public static final int SC_UNPROCESSABLE_ENTITY = 422;
    public static final int SC_TOO_MANY_REQUESTS = 429;
    public static final int SC_INTERNAL_SERVER_ERROR = 500;
    public static final int SC_BAD_GATEWAY = 502;
    public static final int SC_SERVICE_UNAVAILABLE = 503;

    // Common Header Keys
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_X_API_KEY = "X-API-Key";
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";

    // Common Content Types
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    public static final String CONTENT_TYPE_TEXT = "text/plain";

    // Response Format Keys
    public static final String JSON_STATUS_KEY = "status";
    public static final String JSON_MESSAGE_KEY = "message";
    public static final String JSON_DATA_KEY = "data";
    public static final String JSON_ERROR_KEY = "error";
    public static final String JSON_ERRORS_KEY = "errors";

    // Authentication Constants
    public static final String AUTH_TYPE_BASIC = "Basic";
    public static final String AUTH_TYPE_BEARER = "Bearer";
    public static final String AUTH_TYPE_OAUTH = "OAuth";

    // Timeout Constants (in milliseconds)
    public static final int DEFAULT_CONNECTION_TIMEOUT = 30000;  // 30 seconds
    public static final int DEFAULT_SOCKET_TIMEOUT = 60000;      // 60 seconds
    public static final int DEFAULT_READ_TIMEOUT = 30000;        // 30 seconds

    // Retry Constants
    public static final int DEFAULT_MAX_RETRIES = 3;
    public static final int DEFAULT_RETRY_DELAY_MS = 1000;       // 1 second

    // Rate Limiting
    public static final int DEFAULT_RATE_LIMIT = 10;             // requests per second
    public static final long DEFAULT_RATE_LIMIT_PERIOD = 1000;   // 1 second in milliseconds

    // Response Time Thresholds (in milliseconds)
    public static final long RESPONSE_TIME_THRESHOLD_FAST = 500;
    public static final long RESPONSE_TIME_THRESHOLD_MEDIUM = 2000;
    public static final long RESPONSE_TIME_THRESHOLD_SLOW = 5000;

    // Environment Names
    public static final String ENV_DEV = "dev";
    public static final String ENV_QA = "qa";
    public static final String ENV_STAGE = "stage";
    public static final String ENV_PROD = "prod";

    // Configuration Keys
    public static final String CONFIG_BASE_URL = "api.baseUrl";
    public static final String CONFIG_API_VERSION = "api.version";
    public static final String CONFIG_AUTH_TYPE = "api.auth.type";
    public static final String CONFIG_USERNAME = "api.auth.username";
    public static final String CONFIG_PASSWORD = "api.auth.password";
    public static final String CONFIG_API_KEY = "api.auth.apiKey";
    public static final String CONFIG_CLIENT_ID = "api.auth.oauth.clientId";
    public static final String CONFIG_CLIENT_SECRET = "api.auth.oauth.clientSecret";
    public static final String CONFIG_TOKEN_URL = "api.auth.oauth.tokenUrl";

    // Test Tags
    public static final String TAG_REGRESSION = "regression";
    public static final String TAG_SMOKE = "smoke";
    public static final String TAG_SANITY = "sanity";
    public static final String TAG_POSITIVE = "positive";
    public static final String TAG_NEGATIVE = "negative";
    public static final String TAG_PERFORMANCE = "performance";
    public static final String TAG_SECURITY = "security";

    // File Paths
    public static final String PATH_TEST_DATA = "src/test/resources/testdata/";
    public static final String PATH_SCHEMAS = "src/test/resources/schemas/";
    public static final String PATH_CONTRACTS = "src/test/resources/contracts/";
    public static final String PATH_CONFIG = "src/test/resources/config/";
    public static final String PATH_LOGS = "target/logs/";
    public static final String PATH_REPORTS = "target/reports/";

    // Report Constants
    public static final String REPORT_TITLE = "API Automation Test Report";
    public static final String REPORT_NAME = "API Tests";

    // Private constructor to prevent instantiation
    private APIConstants() {
        throw new IllegalStateException("Constants class should not be instantiated");
    }
}




/*
This APIConstants.java file provides a comprehensive set of constants that will be useful throughout your API automation framework.

It includes:
HTTP methods and status codes
Common header keys and content types
Response format keys for JSON parsing
Authentication types and constants
Timeout and retry settings
Environment names
Configuration keys for your properties files
Test tags for categorizing tests
File paths for resources
Report constants

The class is designed with a private constructor to prevent instantiation since it only contains static constants.
This follows the best practice for utility/constants classes.
 */