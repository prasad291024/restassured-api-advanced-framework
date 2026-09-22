package com.prasad_v.validation;

import com.prasad_v.exceptions.APIException;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for validating API responses against JSON schemas.
 * Provides methods to verify that response bodies conform to predefined schemas.
 */
public class SchemaValidator {

    private static final Logger logger = LogManager.getLogger(SchemaValidator.class);
    private static final String SCHEMA_BASE_PATH = "src/test/resources/schemas/";

    /**
     * Validates if the response body conforms to the specified JSON schema file.
     *
     * @param response The API response to validate
     * @param schemaFileName The name of the schema file in the schemas directory
     * @return true if the response body conforms to the schema, false otherwise
     */
    public static boolean validateSchema(Response response, String schemaFileName) {
        try {
            String schemaPath = SCHEMA_BASE_PATH + schemaFileName;
            File schemaFile = new File(schemaPath);

            if (!schemaFile.exists()) {
                logger.error("Schema file not found: {}", schemaPath);
                return false;
            }

            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schemaFile));
            logger.info("Schema validation passed against schema: {}", schemaFileName);
            return true;
        } catch (Exception e) {
            logger.error("Schema validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates if the response body conforms to the specified JSON schema file and throws an exception if not.
     *
     * @param response The API response to validate
     * @param schemaFileName The name of the schema file in the schemas directory
     * @throws APIException if the response body does not conform to the schema
     */
    public static void assertSchema(Response response, String schemaFileName) {
        try {
            String schemaPath = SCHEMA_BASE_PATH + schemaFileName;
            File schemaFile = new File(schemaPath);

            if (!schemaFile.exists()) {
                String errorMessage = "Schema file not found: " + schemaPath;
                logger.error(errorMessage);
                throw new APIException(errorMessage);
            }

            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schemaFile));
            logger.info("Schema assertion passed against schema: {}", schemaFileName);
        } catch (AssertionError e) {
            String errorMessage = "Schema validation failed: " + e.getMessage();
            logger.error(errorMessage);
            throw new APIException(errorMessage);
        }
    }

    /**
     * Validates if the response body conforms to a JSON schema provided as an InputStream.
     *
     * @param response The API response to validate
     * @param schemaStream The schema as an InputStream
     * @return true if the response body conforms to the schema, false otherwise
     */
    public static boolean validateSchemaFromStream(Response response, InputStream schemaStream) {
        try {
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schemaStream));
            logger.info("Schema validation passed against provided schema stream");
            return true;
        } catch (Exception e) {
            logger.error("Schema validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates if the response body conforms to a JSON schema provided as a string.
     *
     * @param response The API response to validate
     * @param schemaContent The schema content as a string
     * @return true if the response body conforms to the schema, false otherwise
     */
    public static boolean validateSchemaFromString(Response response, String schemaContent) {
        try {
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath(createTempSchemaFile(schemaContent)));
            logger.info("Schema validation passed against provided schema string");
            return true;
        } catch (Exception e) {
            logger.error("Schema validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Creates a temporary file from a schema string for validation purposes.
     *
     * @param schemaContent The schema content as a string
     * @return The path to the temporary file
     * @throws Exception if an error occurs while creating the temporary file
     */
    private static String createTempSchemaFile(String schemaContent) throws Exception {
        Path tempFile = Files.createTempFile("schema-", ".json");
        Files.write(tempFile, schemaContent.getBytes());
        tempFile.toFile().deleteOnExit();
        return tempFile.toString();
    }

    /**
     * Checks if a schema file exists in the schemas directory.
     *
     * @param schemaFileName The name of the schema file to check
     * @return true if the schema file exists, false otherwise
     */
    public static boolean schemaFileExists(String schemaFileName) {
        String schemaPath = SCHEMA_BASE_PATH + schemaFileName;
        File schemaFile = new File(schemaPath);
        boolean exists = schemaFile.exists();

        if (exists) {
            logger.debug("Schema file found: {}", schemaPath);
        } else {
            logger.warn("Schema file not found: {}", schemaPath);
        }

        return exists;
    }
}