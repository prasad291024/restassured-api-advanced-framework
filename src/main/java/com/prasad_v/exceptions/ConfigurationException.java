package com.prasad_v.exceptions;

/**
 * Exception thrown when configuration is invalid or missing
 */
public class ConfigurationException extends APIException {
    
    public ConfigurationException(String message) {
        super(message);
    }
    
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
