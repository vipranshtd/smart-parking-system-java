package com.parking.util;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static long minutesBetween(Timestamp start, Timestamp end) {
        long diff = end.getTime() - start.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(diff);
    }

    public static double toHours(long minutes) {
        return minutes / 60.0;
    }
}
