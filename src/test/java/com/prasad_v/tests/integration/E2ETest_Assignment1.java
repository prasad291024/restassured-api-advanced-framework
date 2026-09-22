package com.prasad_v.tests.integration;

/*
    Create Booking → Delete it → Verify it's deleted
    Steps:
        ✔ Create a booking and get bookingid.
        ✔ Delete that booking using the DELETE request.
        ✔ Verify that the booking is no longer accessible (GET /booking/{id} should return 404).
*/

import com.prasad_v.builders.BookingBuilder;
import com.prasad_v.constants.APIConstants;
import com.prasad_v.pojos.Booking;
import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.utils.RestUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class E2ETest_Assignment1 extends BaseTest {

    public int createBooking() {
        Booking booking = new BookingBuilder()
                .withFirstname("John")
                .withLastname("Doe")
                .withTotalprice(150)
                .withDepositpaid(true)
                .withCheckin("2025-03-25")
                .withCheckout("2025-03-30")
                .withAdditionalneeds("Breakfast")
                .build();

        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
        Response createResponse = RestUtils.post(requestSpecification, payloadManager.createPayloadBookingAsString(booking));
        Assert.assertEquals(createResponse.statusCode(), 200, "Create booking failed");

        return createResponse.jsonPath().getInt("bookingid");
    }

    public void deleteBooking(int bookingId, String token) {
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId);
        Response deleteResponse = RestUtils.delete(requestSpecification, token);
        Assert.assertEquals(deleteResponse.statusCode(), 201, "Delete booking failed");
    }

    @Test(groups = {"reg", "e2e"})
    @Owner("Prasad")
    @Description("Assignment 1: Create Booking -> Delete it -> Verify it's deleted")
    public void testCreateDeleteVerifyBooking() {
        int bookingId = createBooking();
        String token = getToken();
        Assert.assertNotNull(token, "Authentication token should not be null");

        deleteBooking(bookingId, token);

        // Verify the booking is deleted (should return 404)
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId);
        Response getResponse = RestUtils.get(requestSpecification);
        Assert.assertEquals(getResponse.statusCode(), 404, "Deleted booking should return 404");
    }
}
