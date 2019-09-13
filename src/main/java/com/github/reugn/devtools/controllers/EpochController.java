package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.services.EpochService;
import com.github.reugn.devtools.utils.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class EpochController implements Initializable, Logger {

    public Label currentEpochLabel;

    public TextField currentEpoch;

    public Button currentEpochRefreshButton;

    public TextField tsToHumanField;

    public Button tsToHumanButton;

    public TextArea tsToHumanResult;

    public Button humanToTsButton;

    public TextArea humanToTsResult;

    public TextField epochYear;
    public TextField epochMonth;
    public TextField epochDay;
    public TextField epochHour;
    public TextField epochMinute;
    public TextField epochSecond;

    @FXML
    private void handleRefreshEpoch(final ActionEvent event) {
        currentEpoch.setText(Long.toString(System.currentTimeMillis()));
    }

    @FXML
    private void handleTsToHumanEpoch(final ActionEvent event) {
        tsToHumanField.setBorder(Border.EMPTY);
        try {
            LocalDateTime dt = EpochService.tsToLocalDateTime(tsToHumanField.getText());
            String result = EpochService.toHumanEpoch(dt);
            tsToHumanResult.setText(result);
        } catch (Exception e) {
            tsToHumanField.setBorder(new Border(new BorderStroke(Color.RED,
                    BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
            tsToHumanResult.setText("");
        }
    }

    @FXML
    private void handleHumanToTsEpoch(final ActionEvent event) {
        resetBorders();
        try {
            int year = EpochService.validate(epochYear, 1970, Integer.MAX_VALUE);
            int month = EpochService.validate(epochMonth, 1, 12);
            int day = EpochService.validate(epochDay, 1, 31);
            int hour = EpochService.validate(epochHour, 0, 24);
            int minute = EpochService.validate(epochMinute, 0, 59);
            int second = EpochService.validate(epochSecond, 0, 59);
            String result = EpochService.toTsEpoch(year, month, day, hour, minute, second);
            humanToTsResult.setText(result);
        } catch (Exception e) {
            humanToTsResult.setText("");
        }
    }

    private void resetBorders() {
        epochYear.setBorder(Border.EMPTY);
        epochMonth.setBorder(Border.EMPTY);
        epochDay.setBorder(Border.EMPTY);
        epochHour.setBorder(Border.EMPTY);
        epochMinute.setBorder(Border.EMPTY);
        epochSecond.setBorder(Border.EMPTY);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HBox.setMargin(currentEpochLabel, new Insets(20, 5, 15, 0));
        HBox.setMargin(currentEpoch, new Insets(15, 5, 15, 0));
        HBox.setMargin(currentEpochRefreshButton, new Insets(15, 5, 15, 0));
        HBox.setMargin(tsToHumanField, new Insets(15, 5, 15, 0));
        HBox.setMargin(tsToHumanButton, new Insets(15, 5, 15, 0));

        GridPane.setMargin(epochYear, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochMonth, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochDay, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochHour, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochMinute, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochSecond, new Insets(10, 5, 0, 0));
        GridPane.setMargin(humanToTsButton, new Insets(10, 5, 0, 0));

        long now = System.currentTimeMillis();
        currentEpoch.setText(Long.toString(now));
        tsToHumanField.setText(Long.toString(now));

        LocalDateTime date = LocalDateTime.now();
        epochYear.setText(String.valueOf(date.getYear()));
        epochMonth.setText(String.valueOf(date.getMonthValue()));
        epochDay.setText(String.valueOf(date.getDayOfMonth()));
        epochHour.setText(String.valueOf(date.getHour()));
        epochMinute.setText(String.valueOf(date.getMinute()));
        epochSecond.setText(String.valueOf(date.getSecond()));
    }
}
