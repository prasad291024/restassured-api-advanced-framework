package com.prasad_v.tests.api;

import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.validation.ResponseTimeValidator;
import com.prasad_v.validation.ResponseValidator;
import com.prasad_v.validation.SchemaValidator;
import com.prasad_v.testdata.JsonDataProvider;
import com.prasad_v.requestbuilder.RequestBuilder;
import com.prasad_v.constants.APIConstants;
import com.prasad_v.enums.RequestType;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for User API endpoints
 */
public class UserAPITests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(UserAPITests.class);
    private static final String USER_SCHEMA = "user_schema.json";
    private static final String USER_DATA_FILE = "users.json";
    private static final long RESPONSE_TIME_THRESHOLD = 2000; // 2 seconds

    private RequestBuilder requestBuilder;
    private JsonDataProvider dataProvider;

    @BeforeClass
    public void setup() {
        requestBuilder = new RequestBuilder();
        dataProvider = new JsonDataProvider();
        logger.info("UserAPITests setup completed");
    }

    @Test(description = "Verify get all users API returns success status code")
    public void testGetAllUsers() {
        logger.info("Starting test: testGetAllUsers");

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.USERS_ENDPOINT)
                .setRequestType(RequestType.GET)
                .build()
                .execute();

        // Validate response status code
        ResponseValidator.assertStatusCode(response, 200);

        // Validate response time
        ResponseTimeValidator.assertResponseTime(response, RESPONSE_TIME_THRESHOLD);

        // Validate content type
        Assert.assertTrue(ResponseValidator.validateContentType(response, "application/json"),
                "Content type validation failed");

        // Log response details
        logger.info("Response body: " + response.getBody().asString());
        logger.info("Response time: " + response.getTime() + " ms");
        logger.info("Test completed: testGetAllUsers");
    }

    @Test(description = "Verify get user by ID returns correct user data")
    public void testGetUserById() {
        logger.info("Starting test: testGetUserById");

        // Use ID 1 for test
        int userId = 1;

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.USERS_ENDPOINT + "/" + userId)
                .setRequestType(RequestType.GET)
                .build()
                .execute();

        // Validate response status code
        ResponseValidator.assertStatusCode(response, 200);

        // Validate response schema
        SchemaValidator.assertSchema(response, USER_SCHEMA);

        // Validate required fields
        List<String> requiredFields = Arrays.asList("id", "name", "email", "phone");
        Assert.assertTrue(ResponseValidator.validateRequiredFields(response, requiredFields),
                "Required fields validation failed");

        // Validate specific field value
        Assert.assertTrue(ResponseValidator.validateField(response, "id", userId),
                "User ID validation failed");

        logger.info("Test completed: testGetUserById");
    }

    @Test(description = "Verify create user API works correctly",
            dataProvider = "createUserData")
    public void testCreateUser(Map<String, Object> userData) {
        logger.info("Starting test: testCreateUser with data: " + userData);

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.USERS_ENDPOINT)
                .setRequestType(RequestType.POST)
                .setBody(userData)
                .build()
                .execute();

        // Validate response status code
        ResponseValidator.assertStatusCode(response, 201);

        // Validate response schema
        SchemaValidator.assertSchema(response, USER_SCHEMA);

        // Validate that user was created with correct data
        Assert.assertTrue(ResponseValidator.validateField(response, "name", userData.get("name")),
                "User name validation failed");
        Assert.assertTrue(ResponseValidator.validateField(response, "email", userData.get("email")),
                "User email validation failed");

        logger.info("Test completed: testCreateUser");
    }

    @Test(description = "Verify update user API works correctly")
    public void testUpdateUser() {
        logger.info("Starting test: testUpdateUser");

        // Use ID 2 for test
        int userId = 2;

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", "Updated User Name");
        updateData.put("email", "updated.email@example.com");

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.USERS_ENDPOINT + "/" + userId)
                .setRequestType(RequestType.PUT)
                .setBody(updateData)
                .build()
                .execute();

        // Validate response status code
        ResponseValidator.assertStatusCode(response, 200);

        // Validate response schema
        SchemaValidator.assertSchema(response, USER_SCHEMA);

        // Validate that user was updated with correct data
        Assert.assertTrue(ResponseValidator.validateField(response, "name", updateData.get("name")),
                "Updated user name validation failed");
        Assert.assertTrue(ResponseValidator.validateField(response, "email", updateData.get("email")),
                "Updated user email validation failed");

        logger.info("Test completed: testUpdateUser");
    }

    @Test(description = "Verify delete user API works correctly")
    public void testDeleteUser() {
        logger.info("Starting test: testDeleteUser");

        // Use ID 3 for test
        int userId = 3;

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.USERS_ENDPOINT + "/" + userId)
                .setRequestType(RequestType.DELETE)
                .build()
                .execute();

        // Validate response status code
        ResponseValidator.assertStatusCode(response, 204);

        // Verify user no longer exists by trying to get it
        Response getResponse = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.USERS_ENDPOINT + "/" + userId)
                .setRequestType(RequestType.GET)
                .build()
                .execute();

        // Validate get response status code (should be 404)
        ResponseValidator.assertStatusCode(getResponse, 404);

        logger.info("Test completed: testDeleteUser");
    }

    @Test(description = "Verify invalid user request returns appropriate error")
    public void testInvalidUserRequest() {
        logger.info("Starting test: testInvalidUserRequest");

        // Use a very large ID that should not exist
        int invalidUserId = 9999;

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.USERS_ENDPOINT + "/" + invalidUserId)
                .setRequestType(RequestType.GET)
                .build()
                .execute();

        // Validate response status code
        ResponseValidator.assertStatusCode(response, 404);

        // Validate error message if available
        if (response.getBody().asString().contains("message")) {
            Assert.assertTrue(ResponseValidator.validateField(response, "message", "User not found"),
                    "Error message validation failed");
        }

        logger.info("Test completed: testInvalidUserRequest");
    }

    @DataProvider(name = "createUserData")
    public Object[][] createUserData() {
        return dataProvider.getTestData(USER_DATA_FILE, "newUsers");
    }

    private String getBaseUrl() {
        return config.getProperty("api.base.url") + config.getProperty("api.version");
    }
}
