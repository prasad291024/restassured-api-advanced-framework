package com.prasad_v.tests.integration;

/*
    Get a Booking from Get All → Try to Delete it
    Steps:
        ✔ Fetch all bookings (GET /booking) and get one bookingid.
        ✔ Try deleting that booking without authentication.
        ✔ Validate that deletion fails with 403 (Forbidden).
*/

import com.prasad_v.constants.APIConstants;
import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.utils.RestUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class E2ETest_Assignment2 extends BaseTest {

    @Test(groups = {"reg", "e2e"})
    @Owner("Prasad")
    @Description("Assignment 2: Get a Booking from Get All -> Try to Delete it without auth -> Verify 403")
    public void testGetAndTryToDeleteBooking() {
        // Fetch bookings
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
        Response getAllResponse = RestUtils.get(requestSpecification);
        Assert.assertEquals(getAllResponse.statusCode(), 200, "Get all bookings failed");

        List<Map<String, Object>> bookingList = getAllResponse.jsonPath().getList("$");
        int bookingId;
        if (bookingList != null && !bookingList.isEmpty()) {
            bookingId = getAllResponse.jsonPath().getInt("[0].bookingid");
        } else {
            // Fallback: create one booking
            Response createResponse = RestUtils.post(requestSpecification, payloadManager.createPayloadBookingAsString());
            bookingId = createResponse.jsonPath().getInt("bookingid");
        }

        // Try to delete the booking WITHOUT authentication (token = null/empty)
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId);
        Response deleteResponse = RestUtils.delete(requestSpecification, "");
        Assert.assertEquals(deleteResponse.statusCode(), 403, "Unauthenticated delete should return 403 Forbidden");
    }
}
