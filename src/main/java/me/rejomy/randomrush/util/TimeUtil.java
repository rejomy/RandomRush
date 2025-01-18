package me.rejomy.randomrush.util;

import java.util.Locale;

public class TimeUtil {
    public static long getTimeInMillis(String time) {
        String[] parts = time.split("[0-9][A-z]+");
        long millis = Integer.parseInt(parts[0]);

        if (parts.length == 1) {
            return millis;
        }

        String timeUnit = parts[1].toLowerCase(Locale.ENGLISH);

        millis *= 1000;

        if (timeUnit.startsWith("s")) return millis;

        millis *= 60;

        if (timeUnit.length() == 1 && timeUnit.startsWith("m") || timeUnit.startsWith("mi")) return millis;

        millis *= 60;

        if (timeUnit.startsWith("h")) return millis;

        millis *= 24;

        if (timeUnit.startsWith("d")) return millis;

        millis *= 30;

        return millis;
    }
}
