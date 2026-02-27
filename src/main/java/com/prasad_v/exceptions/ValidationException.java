package com.prasad_v.exceptions;

/**
 * Exception thrown when validation fails (schema, contract, response)
 */
public class ValidationException extends APIException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
