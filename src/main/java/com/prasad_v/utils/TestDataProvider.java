package com.prasad_v.utils;

import com.prasad_v.builders.BookingBuilder;
import com.prasad_v.pojos.Booking;
import org.testng.annotations.DataProvider;

public class TestDataProvider {

    @DataProvider(name = "bookingData")
    public static Object[][] getBookingData() {
        return new Object[][] {
            {"John", "Doe", 150, true, "2024-12-01", "2024-12-05", "Breakfast"},
            {"Jane", "Smith", 200, false, "2024-12-10", "2024-12-15", "Lunch"}
        };
    }

    @DataProvider(name = "invalidBookingData")
    public static Object[][] getInvalidBookingData() {
        return new Object[][] {
            {"", "Doe", 150, true, "2024-12-01", "2024-12-05", "Breakfast"},
            {"John", "", 150, true, "2024-12-01", "2024-12-05", "Breakfast"},
            {"John", "Doe", -100, true, "2024-12-01", "2024-12-05", "Breakfast"}
        };
    }

    public static Booking getDefaultBooking() {
        return new BookingBuilder().build();
    }

    public static Booking getCustomBooking(String firstname, String lastname) {
        return new BookingBuilder()
                .withFirstname(firstname)
                .withLastname(lastname)
                .build();
    }
}
