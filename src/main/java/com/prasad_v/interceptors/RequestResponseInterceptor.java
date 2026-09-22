package com.prasad_v.interceptors;

import com.prasad_v.logging.CustomLogger;
import com.prasad_v.logging.LogSanitizer;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.qameta.allure.Allure;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class RequestResponseInterceptor implements Filter {
    private static final CustomLogger logger = new CustomLogger(RequestResponseInterceptor.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext filterContext) {
        
        String correlationId = UUID.randomUUID().toString();
        Instant startTime = Instant.now();
        
        if (requestSpec.getHeaders() == null || !requestSpec.getHeaders().hasHeaderWithName(CORRELATION_ID_HEADER)) {
            requestSpec.header(CORRELATION_ID_HEADER, correlationId);
        }
        
        String method = requestSpec.getMethod();
        String uri = requestSpec.getURI();
        String requestBody = requestSpec.getBody() != null ? requestSpec.getBody().toString() : "";
        String requestHeaders = requestSpec.getHeaders().toString();
        
        logger.logRequest(uri, method, requestHeaders, requestBody);
        
        // Attach sanitized request to Allure
        Allure.addAttachment("Request", "text/plain", 
            String.format("%s %s\nHeaders:\n%s\nBody:\n%s", 
                method, 
                LogSanitizer.sanitizeUri(uri), 
                LogSanitizer.sanitizeHeaders(requestHeaders), 
                LogSanitizer.sanitizeBody(requestBody)));
        
        Response response = filterContext.next(requestSpec, responseSpec);
        
        long responseTime = ChronoUnit.MILLIS.between(startTime, Instant.now());
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();
        String responseHeaders = response.getHeaders().toString();
        
        logger.logResponse(statusCode, responseTime, responseHeaders, responseBody);
        
        // Attach sanitized response to Allure
        Allure.addAttachment("Response", "text/plain",
            String.format("Status: %d\nTime: %dms\nHeaders:\n%s\nBody:\n%s", 
                statusCode, 
                responseTime, 
                LogSanitizer.sanitizeHeaders(responseHeaders), 
                LogSanitizer.sanitizeBody(responseBody)));
        
        return response;
    }
}
