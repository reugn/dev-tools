package com.github.reugn.devtools.services;

import com.github.reugn.devtools.utils.Elements;
import javafx.scene.control.TextField;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class EpochServiceImpl implements EpochService {

    @Override
    public LocalDateTime tsToLocalDateTime(String ts) {
        if (ts.length() == 10) {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(ts)), ZoneId.systemDefault());
        } else if (ts.length() == 13) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(ts)), ZoneId.systemDefault());
        }
        throw new IllegalArgumentException("Invalid timestamp format: " + ts);
    }

    @Override
    public String toHumanEpoch(LocalDateTime dt) {
        String formatted = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
                .format(dt.atZone(ZoneId.systemDefault()));
        String formattedUTC = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
                .format(dt.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC));
        return "Local Time:\n" + formatted + "\nGMT:\n" + formattedUTC;
    }

    @Override
    public String toTsEpoch(int year, int month, int dayOfMonth, int hour, int minute, int second,
                            String timeZone) {
        StringBuilder buff = new StringBuilder();
        ZoneOffset offset = OffsetDateTime.now(ZoneId.of(timeZone)).getOffset();
        LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        buff.append("Epoch timestamp: ");
        buff.append(dt.toInstant(offset).getEpochSecond()).append("\n");
        buff.append("Timestamp in milliseconds: ");
        buff.append(dt.toInstant(offset).toEpochMilli()).append("\n");
        buff.append("Time Zone: ").append(displayTimeZone(TimeZone.getTimeZone(timeZone)));
        return buff.toString();
    }

    private String displayTimeZone(TimeZone tz) {
        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = Math.abs(TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours));
        return hours > 0 ? String.format("(GMT+%d:%02d) %s", hours, minutes, tz.getID())
                : String.format("(GMT%d:%02d) %s", hours, minutes, tz.getID());
    }

    @Override
    public int validate(TextField textField, int min, int max) {
        int intVal;
        try {
            intVal = Integer.parseInt(textField.getText());
            if (intVal < min || intVal > max) {
                throw new IllegalArgumentException("Invalid input: " + textField.getText());
            }
        } catch (Exception e) {
            textField.setBorder(Elements.alertBorder);
            throw e;
        }
        return intVal;
    }
}
