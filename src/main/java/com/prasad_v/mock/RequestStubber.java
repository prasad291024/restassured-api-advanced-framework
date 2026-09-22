package com.prasad_v.mock;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.concurrent.TimeUnit;

import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.model.Cookie;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.mockserver.model.Parameter;
import org.mockserver.model.StringBody;
import org.mockserver.model.XmlBody;

import com.prasad_v.logging.CustomLogger;
import com.prasad_v.logging.LogManager;

/**
 * Provides a fluent API for creating mock API endpoints with MockServer.
 * This class allows easy stubbing of requests and configuring responses.
 */
public class RequestStubber {
    private static final CustomLogger logger = LogManager.getLogger(RequestStubber.class);

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private Times times;
    private TimeToLive timeToLive;

    /**
     * Create a new request stubber instance
     */
    public RequestStubber() {
        reset();
    }

    /**
     * Reset the stub configuration
     */
    public void reset() {
        httpRequest = request();
        httpResponse = response();
        times = Times.unlimited();
        timeToLive = TimeToLive.unlimited();
    }

    /**
     * Specify the HTTP method for the request
     *
     * @param method The HTTP method (GET, POST, PUT, DELETE, etc.)
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withMethod(String method) {
        httpRequest.withMethod(method);
        return this;
    }

    /**
     * Specify the path for the request
     *
     * @param path The request path
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withPath(String path) {
        httpRequest.withPath(path);
        return this;
    }

    /**
     * Add a query parameter to the request
     *
     * @param name Parameter name
     * @param value Parameter value
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withQueryParam(String name, String value) {
        httpRequest.withQueryStringParameter(new Parameter(name, value));
        return this;
    }

    /**
     * Add a header to the request
     *
     * @param name Header name
     * @param value Header value
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withRequestHeader(String name, String value) {
        httpRequest.withHeader(new Header(name, value));
        return this;
    }

    /**
     * Set the request body as JSON
     *
     * @param json JSON string
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withJsonBody(String json) {
        httpRequest.withBody(JsonBody.json(json));
        return this;
    }

    /**
     * Set the request body as XML
     *
     * @param xml XML string
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withXmlBody(String xml) {
        httpRequest.withBody(XmlBody.xml(xml));
        return this;
    }

    /**
     * Set the request body as plain text
     *
     * @param body Plain text body
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withTextBody(String body) {
        httpRequest.withBody(new StringBody(body));
        return this;
    }

    /**
     * Set the request body with custom media type
     *
     * @param body Body content
     * @param mediaType Media type
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withBody(String body, String mediaType) {
        httpRequest.withBody(new StringBody(body, MediaType.parse(mediaType)));
        return this;
    }

    /**
     * Add a cookie to the request
     *
     * @param name Cookie name
     * @param value Cookie value
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withRequestCookie(String name, String value) {
        httpRequest.withCookie(new Cookie(name, value));
        return this;
    }

    /**
     * Set the response status code
     *
     * @param statusCode The HTTP status code for the response
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber willRespondWithStatusCode(int statusCode) {
        httpResponse.withStatusCode(statusCode);
        return this;
    }

    /**
     * Add a header to the response
     *
     * @param name Header name
     * @param value Header value
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withResponseHeader(String name, String value) {
        httpResponse.withHeader(new Header(name, value));
        return this;
    }

    /**
     * Set the response body as JSON
     *
     * @param json JSON string
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber willRespondWithJsonBody(String json) {
        httpResponse.withBody(JsonBody.json(json));
        return this;
    }

    /**
     * Set the response body as XML
     *
     * @param xml XML string
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber willRespondWithXmlBody(String xml) {
        httpResponse.withBody(XmlBody.xml(xml));
        return this;
    }

    /**
     * Set the response body as plain text
     *
     * @param body Plain text body
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber willRespondWithTextBody(String body) {
        httpResponse.withBody(new StringBody(body));
        return this;
    }

    /**
     * Set the response body with custom media type
     *
     * @param body Body content
     * @param mediaType Media type
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber willRespondWithBody(String body, String mediaType) {
        httpResponse.withBody(new StringBody(body, MediaType.parse(mediaType)));
        return this;
    }

    /**
     * Add a cookie to the response
     *
     * @param name Cookie name
     * @param value Cookie value
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withResponseCookie(String name, String value) {
        httpResponse.withCookie(name, value);
        return this;
    }

    /**
     * Set a delay for the response
     *
     * @param delay The delay time
     * @param timeUnit The time unit
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withDelay(long delay, TimeUnit timeUnit) {
        httpResponse.withDelay(new Delay(timeUnit, delay));
        return this;
    }

    /**
     * Set the number of times this stub should be active
     *
     * @param count Number of times
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withTimes(int count) {
        times = Times.exactly(count);
        return this;
    }

    /**
     * Set the stub to respond unlimited times
     *
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withUnlimitedTimes() {
        times = Times.unlimited();
        return this;
    }

    /**
     * Set time to live for this stub
     *
     * @param ttl Time to live value
     * @param timeUnit Time unit
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withTimeToLive(long ttl, TimeUnit timeUnit) {
        timeToLive = TimeToLive.exactly(timeUnit, ttl);
        return this;
    }

    /**
     * Set the stub to live indefinitely
     *
     * @return The RequestStubber instance for method chaining
     */
    public RequestStubber withUnlimitedTimeToLive() {
        timeToLive = TimeToLive.unlimited();
        return this;
    }

    /**
     * Create the stub with the configured settings
     *
     * @param mockServerClient The MockServerClient instance
     */
    public void stub(MockServerClient mockServerClient) {
        try {
            logger.info("Creating stub for: " + httpRequest.getMethod() + " " + httpRequest.getPath());
            mockServerClient
                    .when(httpRequest, times, timeToLive)
                    .respond(httpResponse);
            logger.info("Stub created successfully");
        } catch (Exception e) {
            logger.error("Failed to create stub", e);
            throw new RuntimeException("Failed to create stub", e);
        }
    }

    /**
     * Create a stub for the specified method and path that returns JSON
     *
     * @param mockServerClient The MockServerClient instance
     * @param method The HTTP method
     * @param path The request path
     * @param statusCode The response status code
     * @param jsonBody The JSON response body
     */
    public void stubJsonResponse(MockServerClient mockServerClient, String method, String path, int statusCode, String jsonBody) {
        reset();
        withMethod(method)
                .withPath(path)
                .willRespondWithStatusCode(statusCode)
                .withResponseHeader("Content-Type", "application/json")
                .willRespondWithJsonBody(jsonBody)
                .stub(mockServerClient);
    }

    /**
     * Create a stub for the specified method and path that returns a failure
     *
     * @param mockServerClient The MockServerClient instance
     * @param method The HTTP method
     * @param path The request path
     * @param statusCode The error status code
     * @param errorMessage The error message
     */
    public void stubErrorResponse(MockServerClient mockServerClient, String method, String path, int statusCode, String errorMessage) {
        reset();
        withMethod(method)
                .withPath(path)
                .willRespondWithStatusCode(statusCode)
                .withResponseHeader("Content-Type", "application/json")
                .willRespondWithJsonBody("{\"error\": \"" + errorMessage + "\"}")
                .stub(mockServerClient);
    }

    /**
     * Create a stub that simulates a slow response
     *
     * @param mockServerClient The MockServerClient instance
     * @param method The HTTP method
     * @param path The request path
     * @param statusCode The response status code
     * @param jsonBody The JSON response body
     * @param delayInMillis The delay in milliseconds
     */
    public void stubSlowResponse(MockServerClient mockServerClient, String method, String path, int statusCode,
                                 String jsonBody, long delayInMillis) {
        reset();
        withMethod(method)
                .withPath(path)
                .willRespondWithStatusCode(statusCode)
                .withResponseHeader("Content-Type", "application/json")
                .willRespondWithJsonBody(jsonBody)
                .withDelay(delayInMillis, TimeUnit.MILLISECONDS)
                .stub(mockServerClient);
    }

    /**
     * Create a stub that requires specific headers
     *
     * @param mockServerClient The MockServerClient instance
     * @param method The HTTP method
     * @param path The request path
     * @param requiredHeaderName The required header name
     * @param requiredHeaderValue The required header value
     * @param statusCode The response status code
     * @param jsonBody The JSON response body
     */
    public void stubWithRequiredHeader(MockServerClient mockServerClient, String method, String path,
                                       String requiredHeaderName, String requiredHeaderValue,
                                       int statusCode, String jsonBody) {
        reset();
        withMethod(method)
                .withPath(path)
                .withRequestHeader(requiredHeaderName, requiredHeaderValue)
                .willRespondWithStatusCode(statusCode)
                .withResponseHeader("Content-Type", "application/json")
                .willRespondWithJsonBody(jsonBody)
                .stub(mockServerClient);
    }
}