package com.prasad_v.validation;

import com.prasad_v.exceptions.APIException;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Utility class for validating API response contents.
 * Provides methods to verify response status codes, headers, and body content.
 */
public class ResponseValidator {

    private static final Logger logger = LogManager.getLogger(ResponseValidator.class);

    /**
     * Validates if the response status code matches the expected value.
     *
     * @param response The API response to validate
     * @param expectedStatusCode The expected HTTP status code
     * @return true if the status code matches, false otherwise
     */
    public static boolean validateStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        boolean isValid = actualStatusCode == expectedStatusCode;

        if (isValid) {
            logger.info("Status code validation passed: {}", actualStatusCode);
        } else {
            logger.warn("Status code validation failed: Expected {}, but got {}",
                    expectedStatusCode, actualStatusCode);
        }

        return isValid;
    }

    /**
     * Validates if the response status code matches the expected value and throws an exception if not.
     *
     * @param response The API response to validate
     * @param expectedStatusCode The expected HTTP status code
     * @throws APIException if the status code does not match the expected value
     */
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();

        if (actualStatusCode != expectedStatusCode) {
            String errorMessage = String.format("Expected status code %d but got %d",
                    expectedStatusCode, actualStatusCode);
            logger.error(errorMessage);
            throw new APIException(errorMessage);
        }

        logger.info("Status code assertion passed: {}", actualStatusCode);
    }

    /**
     * Validates if the response contains the expected header with the specified value.
     *
     * @param response The API response to validate
     * @param headerName The name of the header to check
     * @param expectedValue The expected value of the header
     * @return true if the header exists with the expected value, false otherwise
     */
    public static boolean validateHeader(Response response, String headerName, String expectedValue) {
        String actualValue = response.getHeader(headerName);
        boolean isValid = expectedValue.equals(actualValue);

        if (isValid) {
            logger.info("Header validation passed for {}: {}", headerName, actualValue);
        } else {
            logger.warn("Header validation failed for {}: Expected {}, but got {}",
                    headerName, expectedValue, actualValue);
        }

        return isValid;
    }

    /**
     * Validates if the response body contains the expected field with the specified value.
     *
     * @param response The API response to validate
     * @param fieldPath The JsonPath to the field
     * @param expectedValue The expected value of the field
     * @return true if the field exists with the expected value, false otherwise
     */
    public static boolean validateField(Response response, String fieldPath, Object expectedValue) {
        try {
            Object actualValue = response.path(fieldPath);
            boolean isValid = expectedValue.equals(actualValue);

            if (isValid) {
                logger.info("Field validation passed for {}: {}", fieldPath, actualValue);
            } else {
                logger.warn("Field validation failed for {}: Expected {}, but got {}",
                        fieldPath, expectedValue, actualValue);
            }

            return isValid;
        } catch (Exception e) {
            logger.error("Error validating field {}: {}", fieldPath, e.getMessage());
            return false;
        }
    }

    /**
     * Validates if the response body contains non-null values for all the specified fields.
     *
     * @param response The API response to validate
     * @param fieldPaths List of JsonPaths to the fields to check
     * @return true if all fields exist and are non-null, false otherwise
     */
    public static boolean validateRequiredFields(Response response, List<String> fieldPaths) {
        boolean allValid = true;

        for (String fieldPath : fieldPaths) {
            try {
                Object value = response.path(fieldPath);
                boolean isValid = value != null;

                if (isValid) {
                    logger.info("Required field validation passed for {}: {}", fieldPath, value);
                } else {
                    logger.warn("Required field validation failed for {}: Value is null", fieldPath);
                    allValid = false;
                }
            } catch (Exception e) {
                logger.error("Error validating required field {}: {}", fieldPath, e.getMessage());
                allValid = false;
            }
        }

        return allValid;
    }

    /**
     * Validates if the response contains the expected content type.
     *
     * @param response The API response to validate
     * @param expectedContentType The expected content type (e.g., "application/json")
     * @return true if the content type matches, false otherwise
     */
    public static boolean validateContentType(Response response, String expectedContentType) {
        String actualContentType = response.getContentType();
        boolean isValid = actualContentType != null && actualContentType.contains(expectedContentType);

        if (isValid) {
            logger.info("Content type validation passed: {}", actualContentType);
        } else {
            logger.warn("Content type validation failed: Expected {}, but got {}",
                    expectedContentType, actualContentType);
        }

        return isValid;
    }

    /**
     * Performs a comprehensive validation of the response against multiple criteria.
     *
     * @param response The API response to validate
     * @param expectedStatusCode The expected HTTP status code
     * @param expectedContentType The expected content type
     * @param requiredFields List of JsonPaths to required fields
     * @param expectedValues Map of JsonPaths to their expected values
     * @return true if all validations pass, false if any fail
     */
    public static boolean validateResponse(Response response, int expectedStatusCode,
                                           String expectedContentType, List<String> requiredFields,
                                           Map<String, Object> expectedValues) {

        boolean statusValid = validateStatusCode(response, expectedStatusCode);
        boolean contentTypeValid = validateContentType(response, expectedContentType);
        boolean requiredFieldsValid = validateRequiredFields(response, requiredFields);

        boolean fieldValuesValid = true;
        for (Map.Entry<String, Object> entry : expectedValues.entrySet()) {
            boolean fieldValid = validateField(response, entry.getKey(), entry.getValue());
            if (!fieldValid) {
                fieldValuesValid = false;
            }
        }

        boolean allValid = statusValid && contentTypeValid && requiredFieldsValid && fieldValuesValid;

        if (allValid) {
            logger.info("All response validations passed");
        } else {
            logger.warn("Some response validations failed");
        }

        return allValid;
    }
}