package com.prasad_v.exceptions;

/**
 * Exception thrown when authentication fails
 */
public class AuthenticationException extends APIException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AuthenticationException(String message, int statusCode, String responseBody) {
        super(message, statusCode, responseBody);
    }
}
