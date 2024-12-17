package com.example.konnect_v2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public interface TimeAgoFormatter {

    static String timeAgo(Date pastDate) {
        Date currentDate = new Date();
        long diffInMillis = currentDate.getTime() - pastDate.getTime();

        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        long diffInMonths = diffInDays / 30;  // Approximation
        long diffInYears = diffInDays / 365;  // Approximation

        if (diffInYears > 0) {
            return diffInYears + "y ago";
        } else if (diffInMonths > 0) {
            return diffInMonths + "mo ago";
        } else if (diffInDays > 0) {
            return diffInDays + "d ago";
        } else if (diffInHours > 0) {
            return diffInHours + "h ago";
        } else if (diffInMinutes > 0) {
            return diffInMinutes + "m ago";
        } else if (diffInSeconds > 0) {
            return diffInSeconds + "s ago";
        } else {
            return "just now";
        }
    }

    static Date parseISODate(String isoDate) throws ParseException {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return isoFormat.parse(isoDate);
    }
}

