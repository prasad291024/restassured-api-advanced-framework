package com.prasad_v.endpoints;

/**
 * @deprecated Use com.prasad_v.constants.APIConstants instead
 * This class is kept for backward compatibility only
 */
@Deprecated
public class APIConstants {
    public static final String BASE_URL = "https://restful-booker.herokuapp.com";
    public static final String CREATE_UPDATE_BOOKING_URL = "/booking";
    public static final String AUTH_URL = "/auth";
    public static final String PING_URL = "/ping";
}

/*
This Java class, APIConstants, defines a set of constants (static string variables) that represent API endpoints for a RESTful web service.
The intention of this program is to centralize and store API endpoint URLs in one place so that they can be easily accessed and maintained across the project.

This class stores API endpoint constants for easy access across the project.
The BASE_URL represents the main API URL.
The other constants define specific API paths used for:
Creating/Updating Bookings (/booking).
User Authentication (/auth).
Checking Server Health (/ping).
These constants help maintain clean and reusable code instead of hardcoding URLs in multiple places.
They also make it easier to update the API endpoints if needed, as they are defined in one central location.
 */