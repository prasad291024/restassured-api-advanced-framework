package com.prasad_v.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String getTodayDate() {
        return LocalDate.now().format(FORMATTER);
    }

    public static String getFutureDate(int daysToAdd) {
        return LocalDate.now().plusDays(daysToAdd).format(FORMATTER);
    }

    public static String getPastDate(int daysToSubtract) {
        return LocalDate.now().minusDays(daysToSubtract).format(FORMATTER);
    }

    public static String formatDate(LocalDate date) {
        return date.format(FORMATTER);
    }
}
