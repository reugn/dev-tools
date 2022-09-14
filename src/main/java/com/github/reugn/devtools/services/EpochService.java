package com.github.reugn.devtools.services;

import javafx.scene.control.TextField;

import java.time.LocalDateTime;

public interface EpochService {

    LocalDateTime tsToLocalDateTime(String ts);

    String toHumanEpoch(LocalDateTime dt);

    String toTsEpoch(int year, int month, int dayOfMonth, int hour, int minute, int second, String timeZone);

    int validate(TextField textField, int min, int max);
}
