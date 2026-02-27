package com.prasad_v.tests.api;

import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.validation.ResponseTimeValidator;
import com.prasad_v.validation.ResponseValidator;
import com.prasad_v.validation.SchemaValidator;
import com.prasad_v.testdata.JsonDataProvider;
import com.prasad_v.requestbuilder.RequestBuilder;
import com.prasad_v.constants.APIConstants;
import com.prasad_v.enums.RequestType;
import com.prasad_v.auth.AuthenticationFactory;
import com.prasad_v.auth.OAuthHandler;

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
 * Test class for Product API endpoints
 */
public class ProductAPITests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(ProductAPITests.class);
    private static final String PRODUCT_DATA_FILE = "products.json";
    private static final long RESPONSE_TIME_THRESHOLD = 3000; // 3 seconds

    private RequestBuilder requestBuilder;
    private JsonDataProvider dataProvider;
    private OAuthHandler authHandler;

    @BeforeClass
    public void setup() {
        requestBuilder = new RequestBuilder();
        dataProvider = new JsonDataProvider();
        authHandler = (OAuthHandler) AuthenticationFactory.getAuthHandler("oauth");
        logger.info("ProductAPITests setup completed");
    }

    @Test(description = "Verify get all products API returns success status code")
    public void testGetAllProducts() {
        logger.info("Starting test: testGetAllProducts");

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.PRODUCTS_ENDPOINT)
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

        // Log performance category
        String performanceCategory = ResponseTimeValidator.categorizeResponseTime(response);
        logger.info("API Performance Category: " + performanceCategory);

        logger.info("Test completed: testGetAllProducts");
    }

    @Test(description = "Verify get product by ID returns correct product data")
    public void testGetProductById() {
        logger.info("Starting test: testGetProductById");

        // Use ID 1 for test
        int productId = 1;

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.PRODUCTS_ENDPOINT + "/" + productId)
                .setRequestType(RequestType.GET)
                .build()
                .execute();

        // Validate response status code
        ResponseValidator.assertStatusCode(response, 200);

        // Validate required fields
        List<String> requiredFields = Arrays.asList("id", "name", "price", "category", "description");
        Assert.assertTrue(ResponseValidator.validateRequiredFields(response, requiredFields),
                "Required fields validation failed");

        // Validate specific field value
        Assert.assertTrue(ResponseValidator.validateField(response, "id", productId),
                "Product ID validation failed");

        // Validate response time
        Assert.assertTrue(ResponseTimeValidator.validateResponseTime(response, RESPONSE_TIME_THRESHOLD),
                "Response time validation failed");

        logger.info("Test completed: testGetProductById");
    }

    @Test(description = "Verify create product API works correctly",
            dataProvider = "createProductData")
    public void testCreateProduct(Map<String, Object> productData) {
        logger.info("Starting test: testCreateProduct with data: " + productData);

        // This endpoint requires authentication
        String token = authHandler.getToken();

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.PRODUCTS_ENDPOINT)
                .setRequestType(RequestType.POST)
                .setBody(productData)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute();

        // Validate response status code
        ResponseValidator.assertStatusCode(response, 201);

        // Validate that product was created with correct data
        Assert.assertTrue(ResponseValidator.validateField(response, "name", productData.get("name")),
                "Product name validation failed");
        Assert.assertTrue(ResponseValidator.validateField(response, "price", productData.get("price")),
                "Product price validation failed");
        Assert.assertTrue(ResponseValidator.validateField(response, "category", productData.get("category")),
                "Product category validation failed");

        logger.info("Test completed: testCreateProduct");
    }

    @Test(description = "Verify update product API works correctly")
    public void testUpdateProduct() {
        logger.info("Starting test: testUpdateProduct");

        // Use ID 2 for test
        int productId = 2;

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", "Updated Product Name");
        updateData.put("price", 149.99);
        updateData.put("description", "Updated product description");

        // This endpoint requires authentication
        String token = authHandler.getToken();

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.PRODUCTS_ENDPOINT + "/" + productId)
                .setRequestType(RequestType.PUT)
                .setBody(updateData)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute();

        // Validate response status code
        ResponseValidator.assertStatusCode(response, 200);

        // Validate that product was updated with correct data
        Assert.assertTrue(ResponseValidator.validateField(response, "name", updateData.get("name")),
                "Updated product name validation failed");
        Assert.assertTrue(ResponseValidator.validateField(response, "price", updateData.get("price")),
                "Updated product price validation failed");
        Assert.assertTrue(ResponseValidator.validateField(response, "description", updateData.get("description")),
                "Updated product description validation failed");

        logger.info("Test completed: testUpdateProduct");
    }

    @Test(description = "Verify delete product API works correctly")
    public void testDeleteProduct() {
        logger.info("Starting test: testDeleteProduct");

        // Use ID 3 for test
        int productId = 3;

        // This endpoint requires authentication
        String token = authHandler.getToken();

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.PRODUCTS_ENDPOINT + "/" + productId)
                .setRequestType(RequestType.DELETE)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute();

        // Validate response status code
        ResponseValidator.assertStatusCode(response, 204);

        // Verify product no longer exists by trying to get it
        Response getResponse = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.PRODUCTS_ENDPOINT + "/" + productId)
                .setRequestType(RequestType.GET)
                .build()
                .execute();

        // Validate get response status code (should be 404)
        ResponseValidator.assertStatusCode(getResponse, 404);

        logger.info("Test completed: testDeleteProduct");
    }

    @Test(description = "Verify product search API works correctly")
    public void testSearchProducts() {
        logger.info("Starting test: testSearchProducts");

        // Search parameters
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("category", "electronics");
        queryParams.put("minPrice", 100);
        queryParams.put("maxPrice", 500);

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.PRODUCTS_ENDPOINT + "/search")
                .setRequestType(RequestType.GET)
                .setQueryParams(queryParams)
                .build()
                .execute();

        // Validate response status code
        ResponseValidator.assertStatusCode(response, 200);

        // Validate response contains items
        Assert.assertTrue(response.jsonPath().getList("items").size() > 0,
                "Search results should not be empty");

        // Validate all returned products match criteria
        List<Map<String, Object>> items = response.jsonPath().getList("items");
        for (Map<String, Object> item : items) {
            String category = (String) item.get("category");
            double price = Double.parseDouble(item.get("price").toString());

            Assert.assertEquals(category, "electronics", "Product category should match search criteria");
            Assert.assertTrue(price >= 100 && price <= 500, "Product price should be within search range");
        }

        logger.info("Test completed: testSearchProducts");
    }

    @Test(description = "Verify product API error handling")
    public void testProductAPIErrorHandling() {
        logger.info("Starting test: testProductAPIErrorHandling");

        // Test with invalid data
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("name", "Test Product");
        // Missing required fields: price, category

        // This endpoint requires authentication
        String token = authHandler.getToken();

        Response response = requestBuilder.setBaseURI(getBaseUrl())
                .setBasePath(APIConstants.PRODUCTS_ENDPOINT)
                .setRequestType(RequestType.POST)
                .setBody(invalidData)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute();

        // Validate response status code for validation error
        ResponseValidator.assertStatusCode(response, 400);

        // Validate error message
        Assert.assertTrue(response.getBody().asString().contains("error"),
                "Response should contain error information");

        // Validate specific error details
        Assert.assertTrue(ResponseValidator.validateField(response, "error.code", "VALIDATION_ERROR"),
                "Error code validation failed");

        logger.info("Test completed: testProductAPIErrorHandling");
    }

    @DataProvider(name = "createProductData")
    public Object[][] createProductData() {
        return dataProvider.getTestData(PRODUCT_DATA_FILE, "newProducts");
    }

    private String getBaseUrl() {
        return config.getProperty("api.base.url") + config.getProperty("api.version");
    }
}
