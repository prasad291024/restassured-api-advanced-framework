package com.prasad_v.validation;

import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.prasad_v.logging.CustomLogger;
import com.prasad_v.reporting.ExtentTestManager;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * JsonPathValidator provides methods to validate API responses using JsonPath expressions.
 * It supports various assertions on JSON response elements.
 */
public class JsonPathValidator {

    private static final CustomLogger logger = new CustomLogger(JsonPathValidator.class);

    /**
     * Validate that a JSON path exists in the response
     *
     * @param response RestAssured response
     * @param jsonPath JsonPath expression
     */
    public void validatePathExists(Response response, String jsonPath) {
        try {
            JsonPath jsonPathEvaluator = response.jsonPath();
            Object value = jsonPathEvaluator.get(jsonPath);

            Assert.assertNotNull(value, "JSON path '" + jsonPath + "' should exist in response");
            logSuccess("JSON path validation passed: '" + jsonPath + "' exists in response");
        } catch (AssertionError e) {
            logFailure("JSON path validation failed: '" + jsonPath + "' does not exist in response", e);
            throw e;
        } catch (Exception e) {
            logFailure("Error validating JSON path '" + jsonPath + "': " + e.getMessage(), e);
            Assert.fail("Error validating JSON path: " + e.getMessage());
        }
    }

    /**
     * Validate that a JSON path does not exist in the response
     *
     * @param response RestAssured response
     * @param jsonPath JsonPath expression
     */
    public void validatePathNotExists(Response response, String jsonPath) {
        try {
            JsonPath jsonPathEvaluator = response.jsonPath();
            Object value = jsonPathEvaluator.get(jsonPath);

            Assert.assertNull(value, "JSON path '" + jsonPath + "' should not exist in response");
            logSuccess("JSON path validation passed: '" + jsonPath + "' does not exist in response");
        } catch (AssertionError e) {
            logFailure("JSON path validation failed: '" + jsonPath + "' exists in response but should not", e);
            throw e;
        } catch (Exception e) {
            // If path doesn't exist, JsonPath might throw an exception, which is what we want
            logSuccess("JSON path validation passed: '" + jsonPath + "' does not exist in response");
        }
    }

    /**
     * Validate that a JSON path value equals the expected value
     *
     * @param response RestAssured response
     * @param jsonPath JsonPath expression
     * @param expectedValue Expected value
     */
    public void validateEquals(Response response, String jsonPath, Object expectedValue) {
        try {
            JsonPath jsonPathEvaluator = response.jsonPath();
            Object actualValue = jsonPathEvaluator.get(jsonPath);

            Assert.assertEquals(actualValue, expectedValue,
                    "JSON path '" + jsonPath + "' value should equal " + expectedValue);
            logSuccess("JSON path validation passed: '" + jsonPath + "' equals " + expectedValue);
        } catch (AssertionError e) {
            logFailure("JSON path validation failed: '" + jsonPath + "' value does not equal expected value", e);
            throw e;
        } catch (Exception e) {
            logFailure("Error validating JSON path '" + jsonPath + "': " + e.getMessage(), e);
            Assert.fail("Error validating JSON path: " + e.getMessage());
        }
    }

    /**
     * Validate that a JSON path value does not equal the expected value
     *
     * @param response RestAssured response
     * @param jsonPath JsonPath expression
     * @param unexpectedValue Value that should not match
     */
    public void validateNotEquals(Response response, String jsonPath, Object unexpectedValue) {
        try {
            JsonPath jsonPathEvaluator = response.jsonPath();
            Object actualValue = jsonPathEvaluator.get(jsonPath);

            if (actualValue == null) {
                if (unexpectedValue != null) {
                    logSuccess("JSON path validation passed: '" + jsonPath + "' is null and does not equal " + unexpectedValue);
                    return;
                }
                Assert.fail("JSON path '" + jsonPath + "' value is null but expected non-null");
            }

            Assert.assertNotEquals(actualValue, unexpectedValue,
                    "JSON path '" + jsonPath + "' value should not equal " + unexpectedValue);
            logSuccess("JSON path validation passed: '" + jsonPath + "' does not equal " + unexpectedValue);
        } catch (AssertionError e) {
            logFailure("JSON path validation failed: '" + jsonPath + "' value equals unexpected value", e);
            throw e;
        } catch (Exception e) {
            logFailure("Error validating JSON path '" + jsonPath + "': " + e.getMessage(), e);
            Assert.fail("Error validating JSON path: " + e.getMessage());
        }
    }

    /**
     * Validate that a JSON path value contains the expected value (for strings)
     *
     * @param response RestAssured response
     * @param jsonPath JsonPath expression
     * @param expectedSubstring Expected substring
     */
    public void validateContains(Response response, String jsonPath, String expectedSubstring) {
        try {
            JsonPath jsonPathEvaluator = response.jsonPath();
            String actualValue = jsonPathEvaluator.getString(jsonPath);

            Assert.assertTrue(actualValue != null && actualValue.contains(expectedSubstring),
                    "JSON path '" + jsonPath + "' value should contain '" + expectedSubstring + "'");
            logSuccess("JSON path validation passed: '" + jsonPath + "' contains '" + expectedSubstring + "'");
        } catch (AssertionError e) {
            logFailure("JSON path validation failed: '" + jsonPath + "' value does not contain expected substring", e);
            throw e;
        } catch (Exception e) {
            logFailure("Error validating JSON path '" + jsonPath + "': " + e.getMessage(), e);
            Assert.fail("Error validating JSON path: " + e.getMessage());
        }
    }

    /**
     * Validate that a JSON path array contains the expected values
     *
     * @param response RestAssured response
     * @param jsonPath JsonPath expression for array
     * @param expectedValues List of expected values
     */
    public void validateArrayContainsAll(Response response, String jsonPath, List<?> expectedValues) {
        try {
            JsonPath jsonPathEvaluator = response.jsonPath();
            List<?> actualList = jsonPathEvaluator.getList(jsonPath);

            Assert.assertNotNull(actualList, "JSON path '" + jsonPath + "' should return an array");

            boolean containsAll = true;
            for (Object expected : expectedValues) {
                if (!actualList.contains(expected)) {
                    containsAll = false;
                    break;
                }
            }

            Assert.assertTrue(containsAll,
                    "JSON path '" + jsonPath + "' array should contain all expected values");
            logSuccess("JSON path validation passed: '" + jsonPath + "' array contains all expected values");
        } catch (AssertionError e) {
            logFailure("JSON path validation failed: '" + jsonPath + "' array does not contain all expected values", e);
            throw e;
        } catch (Exception e) {
            logFailure("Error validating JSON path '" + jsonPath + "': " + e.getMessage(), e);
            Assert.fail("Error validating JSON path: " + e.getMessage());
        }
    }

    /**
     * Validate that a JSON path array has expected size
     *
     * @param response RestAssured response
     * @param jsonPath JsonPath expression for array
     * @param expectedSize Expected array size
     */
    public void validateArraySize(Response response, String jsonPath, int expectedSize) {
        try {
            JsonPath jsonPathEvaluator = response.jsonPath();
            List<?> actualList = jsonPathEvaluator.getList(jsonPath);

            Assert.assertNotNull(actualList, "JSON path '" + jsonPath + "' should return an array");
            Assert.assertEquals(actualList.size(), expectedSize,
                    "JSON path '" + jsonPath + "' array should have size " + expectedSize);
            logSuccess("JSON path validation passed: '" + jsonPath + "' array has expected size of " + expectedSize);
        } catch (AssertionError e) {
            logFailure("JSON path validation failed: '" + jsonPath + "' array does not have expected size", e);
            throw e;
        } catch (Exception e) {
            logFailure("Error validating JSON path '" + jsonPath + "': " + e.getMessage(), e);
            Assert.fail("Error validating JSON path: " + e.getMessage());
        }
    }

    /**
     * Validate that a JSON path matches a condition using a custom assertion
     *
     * @param response RestAssured response
     * @param jsonPath JsonPath expression
     * @param message Assertion message
     * @param condition Boolean condition to check
     */
    public void validateCondition(Response response, String jsonPath, String message, boolean condition) {
        try {
            JsonPath jsonPathEvaluator = response.jsonPath();
            Object value = jsonPathEvaluator.get(jsonPath);

            Assert.assertTrue(condition, message + " for path '" + jsonPath + "' with value: " + value);
            logSuccess("JSON path validation passed: " + message);
        } catch (AssertionError e) {
            logFailure("JSON path validation failed: " + message, e);
            throw e;
        } catch (Exception e) {
            logFailure("Error validating JSON path '" + jsonPath + "': " + e.getMessage(), e);
            Assert.fail("Error validating JSON path: " + e.getMessage());
        }
    }

    /**
     * Validate multiple JSON paths against expected values
     *
     * @param response RestAssured response
     * @param pathValueMap Map with JSON paths as keys and expected values
     */
    public void validateMultiplePaths(Response response, Map<String, Object> pathValueMap) {
        for (Map.Entry<String, Object> entry : pathValueMap.entrySet()) {
            validateEquals(response, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Log validation success
     *
     * @param message Success message
     */
    private void logSuccess(String message) {
        logger.info(message);
        ExtentTestManager.logInfo(message);
    }

    /**
     * Log validation failure
     *
     * @param message Failure message
     * @param e Throwable
     */
    private void logFailure(String message, Throwable e) {
        logger.error(message, e);
        ExtentTestManager.logFail(message);
    }
}
