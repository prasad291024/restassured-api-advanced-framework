package com.prasad_v.tests.integration;

/*
    Create Booking → Update it → Try to Delete it
    Steps:
        ✔ Create a new booking and store bookingid.
        ✔ Update the booking using PUT.
        ✔ Delete the updated booking using DELETE.
        ✔ Verify deletion was successful (GET /booking/{id} should return 404).
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

public class E2ETest_Assignment3 extends BaseTest {

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

    @Test(groups = {"reg", "e2e"})
    @Owner("Prasad")
    @Description("Assignment 3: Create Booking -> Update it -> Delete it -> Verify 404")
    public void testCreateUpdateDeleteBooking() {
        int bookingId = createBooking();
        String token = getToken();
        Assert.assertNotNull(token, "Authentication token should not be null");

        // Update booking
        Booking updatedBooking = new BookingBuilder()
                .withFirstname("Michael")
                .withLastname("Scott")
                .withTotalprice(200)
                .withDepositpaid(false)
                .withCheckin("2025-04-01")
                .withCheckout("2025-04-05")
                .withAdditionalneeds("Lunch")
                .build();

        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId);
        Response updateResponse = RestUtils.put(requestSpecification, payloadManager.createPayloadBookingAsString(updatedBooking), token);
        Assert.assertEquals(updateResponse.statusCode(), 200, "Update booking failed");

        // Delete booking
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId);
        Response deleteResponse = RestUtils.delete(requestSpecification, token);
        Assert.assertEquals(deleteResponse.statusCode(), 201, "Delete booking failed");

        // Verify deletion
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId);
        Response getResponse = RestUtils.get(requestSpecification);
        Assert.assertEquals(getResponse.statusCode(), 404, "Deleted booking should return 404");
    }
}
