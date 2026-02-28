package com.prasad_v.tests.crud;

import com.prasad_v.constants.APIConstants;
import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.pojos.BookingResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import io.restassured.RestAssured;
import org.testng.annotations.Test;

/**
 * This class automates the creation of a booking using the REST API.
 * It sends a POST request to the booking endpoint and verifies that a valid booking is created.
 */
public class TestCreateBooking extends BaseTest {

    /**
     * Test Case ID: TC#INT1
     * This test case validates the creation of a new booking.
     * It checks that a booking is successfully created and a booking ID is returned.
     */
    @Test(groups = "reg", priority = 1) // Marks test as part of regression, with high priority.
    @TmsLink("https://bugz.atlassian.net/browse/TS-1") // Links test to a test management system.
    @Owner("Prasad") // Specifies the test owner.
    @Description("TC#INT1 - Step 1. Verify that the Booking can be Created") // Brief test description.
    public void testCreateBookingPOST() {

        // Set the base path for the booking creation API
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);

        // Send a POST request with booking details in the request body
        response = RestAssured.given(requestSpecification)
                .when()
                .body(payloadManager.createPayloadBookingAsString()) // Set booking details
                .post(); // Execute the POST request

        // Log the response and store it in a validatable format
        validatableResponse = response.then().log().all();

        // Validate that the response status code is 200 (Success)
        validatableResponse.statusCode(200);

        // Convert the response JSON into a Java object (POJO)
        BookingResponse bookingResponse = payloadManager.bookingResponseJava(response.asString());

        // Validate that the response contains the expected first name
        assertActions.verifyStringKey(bookingResponse.getBooking().getFirstname(), "Prasad");

        // Validate that the response contains a non-null booking ID
        assertActions.verifyStringKeyNotNull(bookingResponse.getBookingid());
    }
}
