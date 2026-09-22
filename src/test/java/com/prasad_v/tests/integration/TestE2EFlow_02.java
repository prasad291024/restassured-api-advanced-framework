package com.prasad_v.tests.integration;

/**
 * This class automates an end-to-end test flow for:
 * 1. Creating a booking.
 * 2. Verifying that the booking exists.
 * 3. Deleting the booking.
 * 4. Verifying that the booking has been successfully deleted.
 */

import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.constants.APIConstants;
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.BookingResponse;
import com.prasad_v.utils.RestUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestE2EFlow_02 extends BaseTest {

    private Integer bookingId;
    private String token;

    @Test(priority = 1)
    @Owner("Prasad")
    @Description("TC#E2E2 - Step 1: Create a booking and store booking ID")
    public void createBooking(ITestContext context) {
        requestSpecification = createRequestSpec();
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);

        response = RestUtils.post(requestSpecification, payloadManager.createPayloadBookingAsString());
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        BookingResponse bookingResponse = payloadManager.bookingResponseJava(response.asString());
        assertActions.verifyStringKey(bookingResponse.getBooking().getFirstname(), "Prasad");
        assertActions.verifyStringKeyNotNull(bookingResponse.getBookingid());

        this.bookingId = bookingResponse.getBookingid();
        context.setAttribute("flow2_bookingid", this.bookingId);
    }

    @Test(priority = 2, dependsOnMethods = "createBooking")
    @Owner("Prasad")
    @Description("TC#E2E2 - Step 2: Verify booking by ID exists after creation")
    public void verifyBooking(ITestContext context) {
        if (bookingId == null) {
            bookingId = (Integer) context.getAttribute("flow2_bookingid");
        }
        assertThat(bookingId).isNotNull();

        requestSpecification = createRequestSpec();
        String getPath = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;
        requestSpecification.basePath(getPath);

        response = RestUtils.get(requestSpecification);
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        Booking booking = payloadManager.getResponseFromJSON(response.asString());
        assertThat(booking.getFirstname()).isNotBlank();
    }

    @Test(priority = 3, dependsOnMethods = "createBooking")
    @Owner("Prasad")
    @Description("TC#E2E2 - Step 3: Delete the booking using token")
    public void deleteBooking(ITestContext context) {
        if (bookingId == null) {
            bookingId = (Integer) context.getAttribute("flow2_bookingid");
        }
        assertThat(bookingId).isNotNull();

        if (token == null) {
            token = getToken();
        }
        context.setAttribute("flow2_token", token);

        requestSpecification = createRequestSpec();
        String deletePath = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;
        requestSpecification.basePath(deletePath);

        response = RestUtils.delete(requestSpecification, token);
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(201);
    }

    @Test(priority = 4, dependsOnMethods = "deleteBooking")
    @Owner("Prasad")
    @Description("TC#E2E2 - Step 4: Verify booking no longer exists")
    public void verifyBookingDeleted(ITestContext context) {
        if (bookingId == null) {
            bookingId = (Integer) context.getAttribute("flow2_bookingid");
        }
        assertThat(bookingId).isNotNull();

        requestSpecification = createRequestSpec();
        String getPath = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;
        requestSpecification.basePath(getPath);

        response = RestUtils.get(requestSpecification);
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(404);
    }
}
