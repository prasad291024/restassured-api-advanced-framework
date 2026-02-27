package com.prasad_v.modules;

import com.google.gson.Gson;
import com.prasad_v.builders.BookingBuilder;
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.Bookingdates;
import com.prasad_v.pojos.BookingResponse;
import com.prasad_v.pojos.Auth;
import com.prasad_v.pojos.TokenResponse;

// This Java class, PayloadManager, is responsible for creating and managing JSON payloads for API requests.
// It provides methods to convert Java objects to JSON strings and vice versa.
// The class uses the Gson library for JSON serialization and deserialization.
// The methods in this class are used to create payloads for different API endpoints, such as creating a booking, updating a booking, and authenticating a user.
// The class also includes methods to convert JSON responses to Java objects for further processing.
// The purpose of this class is to centralize the creation and management of JSON payloads, making it easier to maintain and update the payloads across the project.

public class PayloadManager {
    private final Gson gson = new Gson();

    public String createPayloadBookingAsString() {
        Booking booking = new BookingBuilder()
                .withFirstname("Prasad")
                .withLastname("Valiv")
                .withTotalprice(143)
                .withDepositpaid(true)
                .withCheckin("2024-02-01")
                .withCheckout("2024-02-01")
                .withAdditionalneeds("Dinner")
                .build();
        return gson.toJson(booking);
    }

    public String createPayloadBookingAsString(Booking booking) {
        return gson.toJson(booking);
    }

    public BookingResponse bookingResponseJava(String responseString) {
        return gson.fromJson(responseString, BookingResponse.class);
    }

    public String setAuthPayload() {
        Auth auth = new Auth();
        auth.setUsername("admin");
        auth.setPassword("password123");
        return gson.toJson(auth);
    }

    public String getTokenFromJSON(String tokenResponse) {
        TokenResponse response = gson.fromJson(tokenResponse, TokenResponse.class);
        return response.getToken();
    }

    public Booking getResponseFromJSON(String getResponse) {
        return gson.fromJson(getResponse, Booking.class);
    }

    public String fullUpdatePayloadAsString() {
        Booking booking = new BookingBuilder()
                .withFirstname("Lucky")
                .withLastname("Charming")
                .withTotalprice(156)
                .withDepositpaid(true)
                .withCheckin("2024-02-01")
                .withCheckout("2024-02-05")
                .withAdditionalneeds("Breakfast")
                .build();
        return gson.toJson(booking);
    }
}