package com.prasad_v.tests.crud;

import com.prasad_v.constants.APIConstants;
import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.utils.RestUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

/**
 * This class is responsible for testing the authentication API
 * by sending a POST request to create a token.
 */
public class TestCreateToken extends BaseTest {

    /**
     * Test Case ID: TC#2
     * This test validates the successful creation of an authentication token.
     * It sends a POST request with authentication credentials and verifies the response.
     */
    @Test(groups = "reg", priority = 1) // Marks test as part of regression, with high priority.
    @TmsLink("https://bugz.atlassian.net/browse/TS-1") // Links test to a Test Management System.
    @Owner("Promode") // Specifies the test owner.
    @Description("TC#2 - Create Token and Verify") // Provides a brief test description.
    public void testTokenPOST() {

        // Set the base path for the authentication endpoint
        requestSpecification.basePath(APIConstants.AUTH_URL);

        // Send a POST request with authentication payload
        response = RestUtils.post(requestSpecification, payloadManager.setAuthPayload());

        // Log the response and store it in a validatable format
        validatableResponse = response.then().log().all();

        // Validate that the response status code is 200 (Success)
        validatableResponse.statusCode(200);

        // Extract the authentication token from the response body
        String token = payloadManager.getTokenFromJSON(response.asString());

        // Verify that the extracted token is not null (ensuring successful authentication)
        assertActions.verifyStringKeyNotNull(token);
    }
}
