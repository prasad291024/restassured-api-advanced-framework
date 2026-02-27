package com.prasad_v.tests.base;

import com.prasad_v.constants.APIConstants;
import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.asserts.AssertActions;
import com.prasad_v.modules.PayloadManager;
import com.prasad_v.interceptors.RequestResponseInterceptor;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeMethod;

/**
 * BaseTest class provides a foundation for all API tests.
 * It sets up common configurations such as base URL, headers, and authentication token retrieval.
 */
public class BaseTest {
    protected ConfigurationManager config;
    public RequestSpecification requestSpecification;
    public AssertActions assertActions;
    public PayloadManager payloadManager;
    public JsonPath jsonPath;
    public Response response;
    public ValidatableResponse validatableResponse;

    @BeforeMethod
    public void setUp() {
        config = ConfigurationManager.getInstance();
        payloadManager = new PayloadManager();
        assertActions = new AssertActions();
        String baseUrl = config.getProperty("api.base.url", APIConstants.BASE_URL);
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .addHeader("Content-Type", "application/json")
                .addFilter(new RequestResponseInterceptor())
                .build();
    }

    public String getToken() {
        String baseUrl = config.getProperty("api.base.url", APIConstants.BASE_URL);
        requestSpecification = RestAssured.given()
                .baseUri(baseUrl)
                .basePath(APIConstants.AUTH_URL);
        
        response = requestSpecification
                .contentType(ContentType.JSON)
                .body(payloadManager.setAuthPayload())
                .when().post();
        
        return payloadManager.getTokenFromJSON(response.asString());
    }
}
