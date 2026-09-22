package com.prasad_v.tests.integration;
/**
 * This class automates the End-to-End (E2E) flow for booking operations using RestAssured and TestNG.
 * The following steps are executed:
 * 1. Create a Booking → Generate a booking ID.
 * 2. Create an authentication Token.
 * 3. Verify the Booking details via GET request.
 * 4. Update the Booking details via PUT request.
 * 5. Delete the Booking using the booking ID.
 */

import com.prasad_v.constants.APIConstants;
import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.BookingResponse;
import com.prasad_v.utils.RestUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestE2EFlow_01 extends BaseTest {

    private Integer bookingId;
    private String token;

    @Test(groups = "qa", priority = 1)
    @Owner("Prasad")
    @Description("TC#INT1 - Step 1. Verify that the Booking can be Created")
    public void testCreateBooking(ITestContext iTestContext) {
        requestSpecification = createRequestSpec();
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);

        response = RestUtils.post(requestSpecification, payloadManager.createPayloadBookingAsString());
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        BookingResponse bookingResponse = payloadManager.bookingResponseJava(response.asString());
        assertActions.verifyStringKey(bookingResponse.getBooking().getFirstname(), "Prasad");
        assertActions.verifyStringKeyNotNull(bookingResponse.getBookingid());

        this.bookingId = bookingResponse.getBookingid();
        iTestContext.setAttribute("flow1_bookingid", this.bookingId);
    }

    @Test(groups = "qa", priority = 2, dependsOnMethods = "testCreateBooking")
    @Owner("Prasad")
    @Description("TC#INT1 - Step 2. Verify the Booking By ID")
    public void testVerifyBookingId(ITestContext iTestContext) {
        if (bookingId == null) {
            bookingId = (Integer) iTestContext.getAttribute("flow1_bookingid");
        }
        assertThat(bookingId).isNotNull();

        requestSpecification = createRequestSpec();
        String basePathGET = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;
        requestSpecification.basePath(basePathGET);

        response = RestUtils.get(requestSpecification);
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        Booking booking = payloadManager.getResponseFromJSON(response.asString());
        assertThat(booking.getFirstname()).isNotNull().isNotBlank();
        assertThat(booking.getFirstname()).isEqualTo("Prasad");
    }

    @Test(groups = "qa", priority = 3, dependsOnMethods = "testCreateBooking")
    @Owner("Prasad")
    @Description("TC#INT1 - Step 3. Verify Updated Booking by ID")
    public void testUpdateBookingByID(ITestContext iTestContext) {
        if (bookingId == null) {
            bookingId = (Integer) iTestContext.getAttribute("flow1_bookingid");
        }
        assertThat(bookingId).isNotNull();

        if (token == null) {
            token = getToken();
        }
        iTestContext.setAttribute("flow1_token", token);

        requestSpecification = createRequestSpec();
        String basePathPUTPATCH = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;
        requestSpecification.basePath(basePathPUTPATCH);

        response = RestUtils.put(requestSpecification, payloadManager.fullUpdatePayloadAsString(), token);
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        Booking booking = payloadManager.getResponseFromJSON(response.asString());
        assertThat(booking.getFirstname()).isNotNull().isNotBlank();
        assertThat(booking.getFirstname()).isEqualTo("Lucky");
        assertThat(booking.getLastname()).isEqualTo("Charming");
    }

    @Test(groups = "qa", priority = 4, dependsOnMethods = "testCreateBooking")
    @Owner("Prasad")
    @Description("TC#INT1 - Step 4. Delete the Booking by ID")
    public void testDeleteBookingById(ITestContext iTestContext) {
        if (bookingId == null) {
            bookingId = (Integer) iTestContext.getAttribute("flow1_bookingid");
        }
        assertThat(bookingId).isNotNull();

        if (token == null) {
            token = (String) iTestContext.getAttribute("flow1_token");
            if (token == null) {
                token = getToken();
            }
        }

        requestSpecification = createRequestSpec();
        String basePathDELETE = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;
        requestSpecification.basePath(basePathDELETE);

        response = RestUtils.delete(requestSpecification, token);
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(201);
    }
}
