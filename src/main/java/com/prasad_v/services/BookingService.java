package com.prasad_v.services;

import com.prasad_v.constants.APIConstants;
import com.prasad_v.enums.RequestType;
import io.restassured.response.Response;

import java.util.Map;

public class BookingService extends BaseApiService {

    public Response createBooking(Object payload) {
        return execute(RequestType.POST, APIConstants.CREATE_UPDATE_BOOKING_URL, null, payload);
    }

    public Response getBookingById(int bookingId) {
        return execute(RequestType.GET, APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId, null, null);
    }

    public Response updateBooking(int bookingId, Object payload, String token) {
        Map<String, String> headers = Map.of("Cookie", "token=" + token);
        return execute(RequestType.PUT, APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId, headers, payload);
    }

    public Response deleteBooking(int bookingId, String token) {
        Map<String, String> headers = Map.of("Cookie", "token=" + token);
        return execute(RequestType.DELETE, APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId, headers, null);
    }
}
