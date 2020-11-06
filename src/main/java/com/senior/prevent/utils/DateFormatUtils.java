package com.senior.prevent.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatUtils {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";


    public static String format() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return localDateTime.format(dateTimeFormatter);
    }

    public static LocalDateTime format(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return LocalDateTime.parse(date, dateTimeFormatter);
    }

    public static String formatDateTime(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return LocalDateTime.parse(date).format(dateTimeFormatter);
    }

    public static LocalDateTime now() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        String localDateTime = LocalDateTime.now().format(dateTimeFormatter);
        return LocalDateTime.parse(localDateTime,dateTimeFormatter);

    }
}
