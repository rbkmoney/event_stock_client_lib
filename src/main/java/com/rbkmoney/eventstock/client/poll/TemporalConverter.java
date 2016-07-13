package com.rbkmoney.eventstock.client.poll;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

/**
 * Created by vpankrashkin on 12.07.16.
 */
class TemporalConverter {
    //TODO find the way to work with ISO and java time api universally
    private static final DateTimeFormatter FORMATTER = ISO_DATE_TIME;

    public static TemporalAccessor stringToTemporal(String dateTimeStr) throws IllegalArgumentException {
        try {
            return FORMATTER.parse(dateTimeStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse: "+dateTimeStr, e);
        }
    }

    public static String temporalToString(TemporalAccessor temporalAccessor) throws IllegalArgumentException {
        try {
            return FORMATTER.format(temporalAccessor);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to format:"+temporalAccessor, e);
        }
    }
}
