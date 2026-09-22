package com.prasad_v.tests.security;

import com.prasad_v.builders.BookingBuilder;
import com.prasad_v.constants.APIConstants;
import com.prasad_v.logging.LogSanitizer;
import com.prasad_v.pojos.Auth;
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.BookingResponse;
import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.utils.RestUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Security and authentication defensive verification test suite.
 * Validates endpoint authorization boundaries, credential rejection, and data masking.
 */
@Feature("Security & Authentication Boundaries")
public class SecurityTests extends BaseTest {

    private Integer bookingId;

    private int getOrCreateBookingId() {
        if (bookingId == null) {
            requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
            Booking booking = new BookingBuilder().build();
            Response response = RestUtils.post(requestSpecification, payloadManager.createPayloadBookingAsString(booking));
            Assert.assertEquals(response.getStatusCode(), 200, "Setup: Booking creation should succeed");
            BookingResponse bookingResponse = payloadManager.bookingResponseJava(response.asString());
            bookingId = bookingResponse.getBookingid();
            Assert.assertNotNull(bookingId, "Setup: Booking ID must not be null");
        }
        return bookingId;
    }

    @Test(groups = {"reg", "security"}, description = "Verify PUT on protected endpoint without token returns HTTP 403")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ensures unauthorized callers without credentials cannot modify bookings")
    public void testProtectedUpdateWithoutToken() {
        int id = getOrCreateBookingId();
        Booking updatedBooking = new BookingBuilder().build();
        String payload = payloadManager.createPayloadBookingAsString(updatedBooking);

        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + id);

        // Intentionally omitting auth token
        Response response = RestUtils.put(requestSpecification, payload, null);

        Assert.assertEquals(response.getStatusCode(), 403,
                "Access without token on protected PUT endpoint must be rejected with 403");
    }

    @Test(groups = {"reg", "security"}, description = "Verify PUT on protected endpoint with invalid token returns HTTP 403")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ensures callers with fraudulent or invalid tokens cannot modify bookings")
    public void testProtectedUpdateWithInvalidToken() {
        int id = getOrCreateBookingId();
        Booking updatedBooking = new BookingBuilder().build();
        String payload = payloadManager.createPayloadBookingAsString(updatedBooking);

        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + id);

        Response response = RestUtils.put(requestSpecification, payload, "invalid_token_999999");

        Assert.assertEquals(response.getStatusCode(), 403,
                "Access with invalid token on protected PUT endpoint must be rejected with 403");
    }

    @Test(groups = {"reg", "security"}, description = "Verify DELETE on protected endpoint without token returns HTTP 403")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ensures unauthorized callers cannot delete resources")
    public void testProtectedDeleteWithoutToken() {
        int id = getOrCreateBookingId();
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + id);

        // Intentionally omitting auth token
        Response response = RestUtils.delete(requestSpecification, null);

        Assert.assertEquals(response.getStatusCode(), 403,
                "Access without token on protected DELETE endpoint must be rejected with 403");
    }

    @Test(groups = {"reg", "security"}, description = "Verify DELETE on protected endpoint with invalid token returns HTTP 403")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ensures callers with invalid token cannot delete resources")
    public void testProtectedDeleteWithInvalidToken() {
        int id = getOrCreateBookingId();
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + id);

        Response response = RestUtils.delete(requestSpecification, "forged_token_abc123");

        Assert.assertEquals(response.getStatusCode(), 403,
                "Access with invalid token on protected DELETE endpoint must be rejected with 403");
    }

    @Test(groups = {"reg", "security"}, description = "Verify authentication endpoint rejects invalid credentials")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that incorrect password does not generate an active session token")
    public void testAuthenticationWithInvalidCredentials() {
        Auth badAuth = new Auth();
        badAuth.setUsername("admin");
        badAuth.setPassword("definitelyWrongPassword!99");

        Response response = RestAssured.given()
                .baseUri(APIConstants.BASE_URL)
                .basePath(APIConstants.AUTH_URL)
                .contentType(ContentType.JSON)
                .body(payloadManager.setAuthPayload(badAuth))
                .when()
                .post();

        // restful-booker returns 200 with {"reason":"Bad credentials"}
        String token = payloadManager.getTokenFromJSON(response.asString());
        Assert.assertNull(token, "Authentication must not return a valid token for invalid credentials");
        Assert.assertTrue(response.asString().contains("Bad credentials"),
                "Response body should communicate invalid credentials");
    }

    @Test(groups = {"reg", "security"}, description = "Verify authentication endpoint rejects empty credentials")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that blank username/password are rejected")
    public void testAuthenticationWithEmptyCredentials() {
        Auth emptyAuth = new Auth();
        emptyAuth.setUsername("");
        emptyAuth.setPassword("");

        Response response = RestAssured.given()
                .baseUri(APIConstants.BASE_URL)
                .basePath(APIConstants.AUTH_URL)
                .contentType(ContentType.JSON)
                .body(payloadManager.setAuthPayload(emptyAuth))
                .when()
                .post();

        String token = payloadManager.getTokenFromJSON(response.asString());
        Assert.assertNull(token, "Authentication must not issue token for blank credentials");
    }

    @Test(groups = {"reg", "security"}, description = "Verify unsupported Media Type is rejected")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that non-JSON content types are handled properly")
    public void testUnsupportedContentType() {
        Response response = RestAssured.given()
                .baseUri(APIConstants.BASE_URL)
                .basePath(APIConstants.CREATE_UPDATE_BOOKING_URL)
                .contentType(ContentType.TEXT)
                .body("Plain text body not JSON")
                .when()
                .post();

        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 415 || statusCode == 400 || statusCode == 500,
                "Unsupported Content-Type should not return 200 OK. Returned: " + statusCode);
    }

    @Test(groups = {"reg", "security"}, description = "Verify LogSanitizer comprehensive secret and PII masking")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates that passwords, tokens, cookies, auth headers, and PII are redacted")
    public void testLogSanitizerRedaction() {
        String sensitiveJson = "{\"username\":\"admin\",\"password\":\"superSecretPassword123\",\"token\":\"tok_987654321\"}";
        String sanitizedJson = LogSanitizer.sanitize(sensitiveJson);

        Assert.assertFalse(sanitizedJson.contains("superSecretPassword123"), "Password must be redacted");
        Assert.assertFalse(sanitizedJson.contains("tok_987654321"), "Token must be redacted");
        Assert.assertTrue(sanitizedJson.contains("***REDACTED***"), "Redaction marker must be present");

        String sensitiveHeaders = "Cookie: token=abcdef0123456789; Path=/; HttpOnly\nAuthorization: Bearer mySecretJwtToken";
        String sanitizedHeaders = LogSanitizer.sanitize(sensitiveHeaders);

        Assert.assertFalse(sanitizedHeaders.contains("abcdef0123456789"), "Cookie token must be redacted");
        Assert.assertFalse(sanitizedHeaders.contains("mySecretJwtToken"), "Bearer token must be redacted");

        String piiData = "User SSN is 123-45-6789 and Card is 4111-2222-3333-4444";
        String sanitizedPii = LogSanitizer.sanitize(piiData);

        Assert.assertFalse(sanitizedPii.contains("123-45-6789"), "SSN must be redacted");
        Assert.assertFalse(sanitizedPii.contains("4111-2222-3333-4444"), "Credit card must be redacted");
    }
}
