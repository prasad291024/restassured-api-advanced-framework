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
    //  Test E2E Scenario 1
    //  1. Create a Booking -> bookingID
    // 2. Create Token -> token
    // 3. Verify that the Create Booking is working - GET Request to bookingID
    // 4. Update the booking ( bookingID, Token) - Need to get the token, bookingID from above request
    // 5. Delete the Booking - Need to get the token, bookingID from above request
    // Create A Booking, Create a Token
    // Verify that Get booking -
    // Update the Booking
    // Delete the Booking

    /**
     * Step 1: Create a Booking and Store Booking ID
     * This test sends a POST request to create a new booking and stores the booking ID in TestNG context.
     *
     * @param iTestContext - Used to store booking ID for other tests.
     */
    @Test(groups = "qa", priority = 1)
    @Owner("Prasad")
    @Description("TC#INT1 - Step 1. Verify that the Booking can be Created")
    public void testCreateBooking(ITestContext iTestContext) {

        // Setting the API endpoint for creating a booking
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);

        // Sending a POST request with the booking payload and storing the response
        response = RestUtils.post(requestSpecification, payloadManager.createPayloadBookingAsString());

        // Logging and validating the response
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);  // Expected HTTP status: 200 (Success)

        // Parsing the response to extract booking details
        BookingResponse bookingResponse = payloadManager.bookingResponseJava(response.asString());

        // Validating response data
        assertActions.verifyStringKey(bookingResponse.getBooking().getFirstname(), "Prasad");
        assertActions.verifyStringKeyNotNull(bookingResponse.getBookingid());

        // Storing the generated booking ID in TestNG context for later use
        iTestContext.setAttribute("bookingid", bookingResponse.getBookingid());
    }

    /**
     * Step 2: Verify the Booking by Booking ID
     * This test sends a GET request to retrieve booking details and verifies the correctness.
     *
     * @param iTestContext - Retrieves stored booking ID.
     */
    @Test(groups = "qa", priority = 2)
    @Owner("Prasad")
    @Description("TC#INT1 - Step 2. Verify the Booking By ID")
    public void testVerifyBookingId(ITestContext iTestContext) {

        // Retrieving the booking ID from TestNG context
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");

        // Constructing the GET request path
        String basePathGET = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;
        System.out.println("GET Request Path: " + basePathGET);

        // Sending a GET request to retrieve booking details
        requestSpecification.basePath(basePathGET);
        response = RestUtils.get(requestSpecification);

        // Logging and validating the response
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        // Parsing response into Booking object
        Booking booking = payloadManager.getResponseFromJSON(response.asString());

        // Validating the retrieved booking details
        assertThat(booking.getFirstname()).isNotNull().isNotBlank();
        assertThat(booking.getFirstname()).isEqualTo("Prasad");
    }

    /**
     * Step 3: Update the Booking
     * This test sends a PUT request to update booking details and verifies the updated values.
     *
     * @param iTestContext - Retrieves stored booking ID and sets authentication token.
     */
    @Test(groups = "qa", priority = 3)
    @Owner("Prasad")
    @Description("TC#INT1 - Step 3. Verify Updated Booking by ID")
    public void testUpdateBookingByID(ITestContext iTestContext) {

        // Retrieving the booking ID
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");

        // Generating an authentication token for update operation
        String token = getToken();
        iTestContext.setAttribute("token", token);

        // Constructing the PUT request path
        String basePathPUTPATCH = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;
        System.out.println("PUT Request Path: " + basePathPUTPATCH);

        // Sending a PUT request with new booking data
        requestSpecification.basePath(basePathPUTPATCH);
        response = RestUtils.put(requestSpecification, payloadManager.fullUpdatePayloadAsString(), token);

        // Logging and validating the response
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        // Parsing response into Booking object
        Booking booking = payloadManager.getResponseFromJSON(response.asString());

        // Validating updated booking details
        assertThat(booking.getFirstname()).isNotNull().isNotBlank();
        assertThat(booking.getFirstname()).isEqualTo("Lucky");
        assertThat(booking.getLastname()).isEqualTo("Charming");
    }

    /**
     * Step 4: Delete the Booking
     * This test sends a DELETE request to remove the booking.
     *
     * @param iTestContext - Retrieves stored booking ID and token.
     */
    @Test(groups = "qa", priority = 4)
    @Owner("Prasad")
    @Description("TC#INT1 - Step 4. Delete the Booking by ID")
    public void testDeleteBookingById(ITestContext iTestContext) {

        // Retrieving authentication token and booking ID from TestNG context
        String token = (String) iTestContext.getAttribute("token");
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");

        // Constructing the DELETE request path
        String basePathDELETE = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;

        // Sending a DELETE request with authentication token
        requestSpecification.basePath(basePathDELETE);
        response = RestUtils.delete(requestSpecification, token);
        validatableResponse = response.then().log().all();

        // Validating response - Expected HTTP status: 201 (Deleted Successfully)
        validatableResponse.statusCode(201);
    }
}
