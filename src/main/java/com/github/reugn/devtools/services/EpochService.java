package com.github.reugn.devtools.services;

import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.security.InvalidParameterException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

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
        return formatted + "\n" + formattedUTC;
    }

    public static String toTsEpoch(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        StringBuilder buff = new StringBuilder();
        ZoneOffset offset = OffsetDateTime.now().getOffset();
        LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        buff.append("Epoch timestamp: ");
        buff.append(dt.toInstant(offset).getEpochSecond()).append("\n");
        buff.append("Timestamp in milliseconds: ");
        buff.append(dt.toInstant(offset).toEpochMilli());
        return buff.toString();
    }

    public static int validate(TextField f, int min, int max) {
        int intVal;
        try {
            intVal = Integer.parseInt(f.getText());
            if (intVal < min || intVal > max) {
                throw new InvalidParameterException("Invalid input: " + f.getText());
            }
        } catch (Exception e) {
            f.setBorder(new Border(new BorderStroke(Color.RED,
                    BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
            throw e;
        }
        return intVal;
    }
}
