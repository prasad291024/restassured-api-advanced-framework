package com.prasad_v.requestbuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.constants.APIConstants;
import com.prasad_v.enums.RequestType;
import com.prasad_v.exceptions.APIException;
import com.prasad_v.interceptors.RequestResponseInterceptor;
import com.prasad_v.logging.CustomLogger;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * RequestBuilder provides a fluent interface to build and execute API requests.
 * It integrates with HeaderManager and AuthenticationManager for request preparation.
 */
public class RequestBuilder {

    private static final CustomLogger logger = new CustomLogger(RequestBuilder.class);

    private RequestSpecification requestSpec;
    private String baseUrl;
    private String path;
    private Map<String, String> queryParams;
    private Map<String, String> formParams;
    private Map<String, Object> pathParams;
    private Object requestBody;
    private HeaderManager headerManager;
    private AuthenticationManager authManager;
    private boolean logRequest = true;
    private boolean logResponse = true;
    private RequestType requestType;

    /**
     * Constructor initializes the builder with default values
     */
    public RequestBuilder() {
        // Initialize components
        headerManager = new HeaderManager();
        authManager = new AuthenticationManager();

        // Initialize collections
        queryParams = new HashMap<>();
        formParams = new HashMap<>();
        pathParams = new HashMap<>();

        // Get base URL from environment
        ConfigurationManager configManager = ConfigurationManager.getInstance();
        baseUrl = configManager.getConfigProperty("api.base.url", "");
        if (baseUrl.isBlank()) {
            baseUrl = configManager.getConfigProperty("api.baseUrl", APIConstants.BASE_URL);
        }

        // Default request type
        requestType = RequestType.GET;

        // Initialize RestAssured spec with config
        initializeRequestSpec();
    }

    /**
     * Initialize the RestAssured RequestSpecification with default configuration
     */
    private void initializeRequestSpec() {
        EncoderConfig encoderConfig = new EncoderConfig()
                .appendDefaultContentCharsetToContentTypeIfUndefined(false)
                .defaultContentCharset("UTF-8");

        LogConfig logConfig = new LogConfig()
                .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);

        RestAssuredConfig config = RestAssured.config()
                .encoderConfig(encoderConfig)
                .logConfig(logConfig);

        requestSpec = RestAssured.given().config(config);

        // Add request/response interceptor for logging and monitoring
        requestSpec.filter(new RequestResponseInterceptor());
    }

    /**
     * Set the API endpoint path
     *
     * @param path API endpoint path
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder setPath(String path) {
        this.path = path;
        logger.debug("Set path: " + path);
        return this;
    }

    /**
     * Backward-compatible alias for setPath.
     */
    public RequestBuilder setBasePath(String path) {
        return setPath(path);
    }

    /**
     * Override the base URL
     *
     * @param baseUrl Base URL to use
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        logger.debug("Set base URL: " + baseUrl);
        return this;
    }

    /**
     * Backward-compatible alias for setBaseUrl.
     */
    public RequestBuilder setBaseURI(String baseUrl) {
        return setBaseUrl(baseUrl);
    }

    /**
     * Set the request type (GET, POST, PUT, DELETE, etc.)
     *
     * @param requestType Request type from RequestType enum
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder setRequestType(RequestType requestType) {
        this.requestType = requestType;
        logger.debug("Set request type: " + requestType);
        return this;
    }

    /**
     * Add a query parameter
     *
     * @param key Query parameter key
     * @param value Query parameter value
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addQueryParam(String key, String value) {
        queryParams.put(key, value);
        logger.debug("Added query parameter: " + key + " = " + value);
        return this;
    }

    /**
     * Add multiple query parameters
     *
     * @param params Map of query parameters
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addQueryParams(Map<String, String> params) {
        if (params != null) {
            queryParams.putAll(params);
            logger.debug("Added multiple query parameters: " + params.keySet());
        }
        return this;
    }

    /**
     * Backward-compatible alias accepting non-string query parameter values.
     */
    public RequestBuilder setQueryParams(Map<String, Object> params) {
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                queryParams.put(entry.getKey(), entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
            }
            logger.debug("Set query parameters: " + params.keySet());
        }
        return this;
    }

    /**
     * Add a form parameter
     *
     * @param key Form parameter key
     * @param value Form parameter value
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addFormParam(String key, String value) {
        formParams.put(key, value);
        logger.debug("Added form parameter: " + key + " = " + value);
        return this;
    }

    /**
     * Add multiple form parameters
     *
     * @param params Map of form parameters
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addFormParams(Map<String, String> params) {
        if (params != null) {
            formParams.putAll(params);
            logger.debug("Added multiple form parameters: " + params.keySet());
        }
        return this;
    }

    /**
     * Add a path parameter
     *
     * @param key Path parameter key
     * @param value Path parameter value
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addPathParam(String key, Object value) {
        pathParams.put(key, value);
        logger.debug("Added path parameter: " + key + " = " + value);
        return this;
    }

    /**
     * Add multiple path parameters
     *
     * @param params Map of path parameters
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addPathParams(Map<String, Object> params) {
        if (params != null) {
            pathParams.putAll(params);
            logger.debug("Added multiple path parameters: " + params.keySet());
        }
        return this;
    }

    /**
     * Set JSON request body from String
     *
     * @param body JSON request body string
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder setBody(String body) {
        this.requestBody = body;
        headerManager.addContentTypeJson();
        logger.debug("Set JSON request body from string");
        return this;
    }

    /**
     * Backward-compatible generic body setter (Map/POJO/JSON string).
     */
    public RequestBuilder setBody(Object body) {
        if (body instanceof String) {
            return setBody((String) body);
        }
        return setBodyAsPojo(body);
    }

    /**
     * Set JSON request body from JSONObject
     *
     * @param jsonObject JSON request body object
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder setBody(JSONObject jsonObject) {
        this.requestBody = jsonObject.toString();
        headerManager.addContentTypeJson();
        logger.debug("Set JSON request body from JSONObject");
        return this;
    }

    /**
     * Set XML request body
     *
     * @param xmlBody XML request body string
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder setXmlBody(String xmlBody) {
        this.requestBody = xmlBody;
        headerManager.addContentTypeXml();
        logger.debug("Set XML request body");
        return this;
    }

    /**
     * Set request body as POJO (will be serialized to JSON)
     *
     * @param pojo POJO to be serialized
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder setBodyAsPojo(Object pojo) {
        this.requestBody = pojo;
        headerManager.addContentTypeJson();
        logger.debug("Set request body as POJO");
        return this;
    }

    /**
     * Add a file to the request for multipart/form-data
     *
     * @param controlName Form control name
     * @param file File to upload
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addFile(String controlName, File file) {
        requestSpec.multiPart(controlName, file);
        logger.debug("Added file: " + file.getName() + " as " + controlName);
        return this;
    }

    /**
     * Add a header
     *
     * @param key Header key
     * @param value Header value
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addHeader(String key, String value) {
        headerManager.addHeader(key, value);
        return this;
    }

    /**
     * Add multiple headers
     *
     * @param headers Map of headers
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addHeaders(Map<String, String> headers) {
        headerManager.addHeaders(headers);
        return this;
    }

    /**
     * Add Content-Type: application/json header
     *
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addContentTypeJson() {
        headerManager.addContentTypeJson();
        return this;
    }

    /**
     * Add Accept: application/json header
     *
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addAcceptJson() {
        headerManager.addAcceptJson();
        return this;
    }

    /**
     * Add common headers from configuration
     *
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addCommonHeaders() {
        headerManager.addCommonHeaders();
        return this;
    }

    /**
     * Add OAuth authentication
     *
     * @param tokenKey Token key from configuration
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addOAuthAuthentication(String tokenKey) {
        String authHeader = authManager.getOAuthHeader(tokenKey);
        headerManager.addAuthorizationHeader(authHeader);
        logger.debug("Added OAuth authentication with token key: " + tokenKey);
        return this;
    }

    /**
     * Add Basic authentication
     *
     * @param username Username
     * @param password Password
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addBasicAuthentication(String username, String password) {
        String authHeader = authManager.getBasicAuthHeader(username, password);
        headerManager.addAuthorizationHeader(authHeader);
        logger.debug("Added Basic authentication for user: " + username);
        return this;
    }

    /**
     * Add Basic authentication from configuration
     *
     * @param authKey Auth key from configuration
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder addBasicAuthenticationFromConfig(String authKey) {
        String authHeader = authManager.getBasicAuthHeaderFromConfig(authKey);
        headerManager.addAuthorizationHeader(authHeader);
        logger.debug("Added Basic authentication from config for key: " + authKey);
        return this;
    }

    /**
     * Enable/disable request logging
     *
     * @param logRequest Whether to log request details
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder logRequest(boolean logRequest) {
        this.logRequest = logRequest;
        return this;
    }

    /**
     * Enable/disable response logging
     *
     * @param logResponse Whether to log response details
     * @return Current RequestBuilder instance for method chaining
     */
    public RequestBuilder logResponse(boolean logResponse) {
        this.logResponse = logResponse;
        return this;
    }

    /**
     * Backward-compatible no-op terminal method.
     */
    public RequestBuilder build() {
        return this;
    }

    /**
     * Build and execute the API request
     *
     * @return RestAssured Response object
     * @throws APIException If there's an error during request execution
     */
    public Response execute() throws APIException {
        try {
            // Add headers
            requestSpec.headers(headerManager.getHeaders());

            // Add query parameters
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                requestSpec.queryParam(entry.getKey(), entry.getValue());
            }

            // Add form parameters
            for (Map.Entry<String, String> entry : formParams.entrySet()) {
                requestSpec.formParam(entry.getKey(), entry.getValue());
            }

            // Add path parameters
            for (Map.Entry<String, Object> entry : pathParams.entrySet()) {
                requestSpec.pathParam(entry.getKey(), entry.getValue());
            }

            // Add request body if present
            if (requestBody != null) {
                requestSpec.body(requestBody);
            }

            // Add logging if enabled
            if (logRequest) {
                requestSpec.log().all();
            }

            // Build full URL
            if (baseUrl == null || baseUrl.isBlank()) {
                throw new APIException("Base URL is not set for the request");
            }
            String url = baseUrl;
            if (path != null && !path.isEmpty()) {
                if (!url.endsWith("/") && !path.startsWith("/")) {
                    url += "/";
                }
                url += path;
            }

            // Execute request based on type
            Response response;
            switch (requestType) {
                case GET:
                    response = requestSpec.get(url);
                    break;
                case POST:
                    response = requestSpec.post(url);
                    break;
                case PUT:
                    response = requestSpec.put(url);
                    break;
                case DELETE:
                    response = requestSpec.delete(url);
                    break;
                case PATCH:
                    response = requestSpec.patch(url);
                    break;
                case HEAD:
                    response = requestSpec.head(url);
                    break;
                case OPTIONS:
                    response = requestSpec.options(url);
                    break;
                default:
                    throw new APIException("Unsupported request type: " + requestType);
            }

            // Log response if enabled
            if (logResponse) {
                response.then().log().all();
            }

            return response;

        } catch (Exception e) {
            logger.error("Error executing API request: " + e.getMessage(), e);
            throw new APIException("Failed to execute API request: " + e.getMessage(), e);
        }
    }
}
