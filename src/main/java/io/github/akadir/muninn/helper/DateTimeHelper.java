package io.github.akadir.muninn.helper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author akadir
 * Date: 9.05.2020
 * Time: 00:27
 */
public class DateTimeHelper {
    private DateTimeHelper() {
    }

    public static long getTimeDifferenceInHoursSince(Date since) {
        if (since == null) {
            return Long.MAX_VALUE;
        }

        LocalDateTime now = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        LocalDateTime lastChecked = LocalDateTime.ofInstant(since.toInstant(), ZoneId.systemDefault());

        return ChronoUnit.HOURS.between(lastChecked, now);
    }
}
