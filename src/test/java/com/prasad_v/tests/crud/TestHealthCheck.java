package com.prasad_v.tests.crud;

import com.prasad_v.constants.APIConstants;
import com.prasad_v.tests.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestHealthCheck extends BaseTest {

    @Test(groups = "reg", priority = 1)
    @TmsLink("https://bugz.atlassian.net/browse/TS-1")
    @Owner("Promode")
    @Description("TC#3  - Verify Health Check API is working")
    public void testGETHealthCheck() {
        requestSpecification.basePath(APIConstants.PING_URL);

        response = RestAssured
                .given().spec(requestSpecification)
                .when()
                .get();

        validatableResponse = response.then().log().all();

        // Register custom parser for text/plain responses
        RestAssured.registerParser("text/plain", io.restassured.parsing.Parser.TEXT);

        // ✅ Validate the status code (should match what API expects)
        validatableResponse.statusCode(201);

        // ✅ Extract response body and validate
        String responseBody = response.getBody().asString();
        Assert.assertEquals(responseBody, "Created", "Health check response does not match expected value.");
    }
}
