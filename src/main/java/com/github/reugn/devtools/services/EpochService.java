package com.github.reugn.devtools.services;

import com.github.reugn.devtools.utils.Elements;
import javafx.scene.control.TextField;

import java.security.InvalidParameterException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class EpochService {

    private EpochService() {
    }

    public static LocalDateTime tsToLocalDateTime(String ts) {
        if (ts.length() == 10) {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(ts)), ZoneId.systemDefault());
        } else if (ts.length() == 13) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(ts)), ZoneId.systemDefault());
        }
        throw new InvalidParameterException("Invalid timestamp format: " + ts);
    }

    public static String toHumanEpoch(LocalDateTime dt) {
        String formatted = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
                .format(dt.atZone(ZoneId.systemDefault()));
        String formattedUTC = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
                .format(dt.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC));
        return "Local Time:\n" + formatted + "\nGMT:\n" + formattedUTC;
    }

    public static String toTsEpoch(int year, int month, int dayOfMonth, int hour, int minute, int second,
                                   String timeZone) {
        StringBuilder buff = new StringBuilder();
        ZoneOffset offset = OffsetDateTime.now(ZoneId.of(timeZone)).getOffset();
        LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        buff.append("Epoch timestamp: ");
        buff.append(dt.toInstant(offset).getEpochSecond()).append("\n");
        buff.append("Timestamp in milliseconds: ");
        buff.append(dt.toInstant(offset).toEpochMilli());
        buff.append("\n");
        buff.append("Time Zone: ").append(displayTimeZone(TimeZone.getTimeZone(timeZone)));
        return buff.toString();
    }

    private static String displayTimeZone(TimeZone tz) {
        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = Math.abs(TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours));
        return hours > 0 ? String.format("(GMT+%d:%02d) %s", hours, minutes, tz.getID())
                : String.format("(GMT%d:%02d) %s", hours, minutes, tz.getID());
    }

    public static int validate(TextField f, int min, int max) {
        int intVal;
        try {
            intVal = Integer.parseInt(f.getText());
            if (intVal < min || intVal > max) {
                throw new InvalidParameterException("Invalid input: " + f.getText());
            }
        } catch (Exception e) {
            f.setBorder(Elements.alertBorder);
            throw e;
        }
        return intVal;
    }
}
