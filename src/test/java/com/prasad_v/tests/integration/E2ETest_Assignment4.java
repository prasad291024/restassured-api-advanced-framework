package com.prasad_v.tests.integration;

/*
    Delete a Booking → Try to Update it
    Steps:
        ✔ Create a new booking and get bookingid.
        ✔ Delete that booking.
        ✔ Try to update the deleted booking.
        ✔ Validate update fails with 405 (Method Not Allowed) or 404 (Not Found).
*/

import com.prasad_v.builders.BookingBuilder;
import com.prasad_v.pojos.Booking;
import com.prasad_v.tests.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class E2ETest_Assignment4 extends BaseTest {

    public int createBooking() {
        Booking booking = new BookingBuilder()
                .withFirstname("James")
                .withLastname("Bond")
                .withTotalprice(250)
                .withDepositpaid(true)
                .withCheckin("2025-05-01")
                .withCheckout("2025-05-05")
                .withAdditionalneeds("Martini")
                .build();

        Response createResponse = bookingService.createBooking(payloadManager.createPayloadBookingAsString(booking));
        Assert.assertEquals(createResponse.statusCode(), 200, "Create booking failed");

        return createResponse.jsonPath().getInt("bookingid");
    }

    @Test(groups = {"reg", "e2e"})
    @Owner("Prasad")
    @Description("Assignment 4: Create Booking -> Delete it -> Try to Update deleted booking -> Expect 405 or 404")
    public void testDeleteThenTryToUpdateBooking() {
        int bookingId = createBooking();
        String token = getToken();
        Assert.assertNotNull(token, "Authentication token should not be null");

        // Delete the booking
        Response deleteResponse = bookingService.deleteBooking(bookingId, token);
        Assert.assertEquals(deleteResponse.statusCode(), 201, "Delete booking failed");

        // Try to update the deleted booking
        Booking updateBooking = new BookingBuilder()
                .withFirstname("James")
                .withLastname("Bond")
                .withTotalprice(300)
                .withDepositpaid(true)
                .withCheckin("2025-05-01")
                .withCheckout("2025-05-05")
                .withAdditionalneeds("Shaken not stirred")
                .build();

        Response updateResponse = bookingService.updateBooking(bookingId, payloadManager.createPayloadBookingAsString(updateBooking), token);

        // Restful-booker returns 405 Method Not Allowed when updating a non-existent/deleted resource
        int statusCode = updateResponse.statusCode();
        Assert.assertTrue(statusCode == 405 || statusCode == 404,
                "Expected status code 405 or 404 on deleted booking update, but got: " + statusCode);
    }
}
