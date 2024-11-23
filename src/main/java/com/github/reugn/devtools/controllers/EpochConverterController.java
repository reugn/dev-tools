package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.services.EpochService;
import com.github.reugn.devtools.utils.Elements;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class EpochConverterController implements Initializable {

    private final EpochService epochService;

    @FXML
    private Label currentEpochLabel;
    @FXML
    private TextField currentEpochField;
    @FXML
    private Button currentEpochRefreshButton;
    @FXML
    private TextField tsToHumanField;
    @FXML
    private Button tsToHumanButton;
    @FXML
    private Button millisToTimeButton;
    @FXML
    private TextArea tsToHumanResultTextArea;
    @FXML
    private Button humanToTsButton;
    @FXML
    private TextArea humanToTsResultTextArea;
    @FXML
    private TextField epochYearField;
    @FXML
    private TextField epochMonthField;
    @FXML
    private TextField epochDayField;
    @FXML
    private TextField epochHourField;
    @FXML
    private TextField epochMinuteField;
    @FXML
    private TextField epochSecondField;
    @FXML
    private ComboBox<String> timeZoneComboBox;

    private int timeZoneComboBoxIndex;

    @Inject
    public EpochConverterController(EpochService epochService) {
        this.epochService = epochService;
    }

    @FXML
    private void handleRefreshEpoch(@SuppressWarnings("unused") final ActionEvent event) {
        currentEpochField.setText(Long.toString(System.currentTimeMillis()));
    }

    @FXML
    private void handleTsToHumanEpoch(@SuppressWarnings("unused") final ActionEvent event) {
        tsToHumanField.setBorder(Border.EMPTY);
        try {
            LocalDateTime localDateTime = epochService.tsToLocalDateTime(tsToHumanField.getText());
            String result = epochService.toHumanEpoch(localDateTime);
            tsToHumanResultTextArea.setText(result);
        } catch (Exception e) {
            tsToHumanField.setBorder(Elements.alertBorder);
            tsToHumanResultTextArea.setText("");
        }
    }

    @FXML
    private void handleMillisToTime(@SuppressWarnings("unused") final ActionEvent actionEvent) {
        tsToHumanField.setBorder(Border.EMPTY);
        try {
            long millis = Long.parseLong(tsToHumanField.getText());
            String result = DurationFormatUtils.formatDurationWords(millis, true, true);
            tsToHumanResultTextArea.setText(result);
        } catch (Exception e) {
            tsToHumanField.setBorder(Elements.alertBorder);
            tsToHumanResultTextArea.setText("");
        }
    }

    @FXML
    private void handleHumanToTsEpoch(@SuppressWarnings("unused") final ActionEvent event) {
        resetBorders();
        try {
            int year = epochService.validate(epochYearField, 1970, Integer.MAX_VALUE);
            int month = epochService.validate(epochMonthField, 1, 12);
            int day = epochService.validate(epochDayField, 1, 31);
            int hour = epochService.validate(epochHourField, 0, 24);
            int minute = epochService.validate(epochMinuteField, 0, 59);
            int second = epochService.validate(epochSecondField, 0, 59);
            String timeZone = timeZoneComboBox.getSelectionModel().getSelectedItem();
            String result = epochService.toTsEpoch(year, month, day, hour, minute, second, timeZone);
            humanToTsResultTextArea.setText(result);
        } catch (Exception e) {
            humanToTsResultTextArea.setText("");
        }
    }

    @FXML
    private void handleTimeZoneSearch(KeyEvent keyEvent) {
        String key = keyEvent.getText();
        if (key.isEmpty()) return;
        int i = 0;
        for (String item : timeZoneComboBox.getItems()) {
            if (item.toLowerCase().startsWith(key) && i > timeZoneComboBoxIndex) {
                timeZoneComboBox.setValue(item);
                timeZoneComboBoxIndex = i;
                return;
            }
            i++;
        }
        timeZoneComboBoxIndex = 0;
    }

    private void resetBorders() {
        epochYearField.setBorder(Border.EMPTY);
        epochMonthField.setBorder(Border.EMPTY);
        epochDayField.setBorder(Border.EMPTY);
        epochHourField.setBorder(Border.EMPTY);
        epochMinuteField.setBorder(Border.EMPTY);
        epochSecondField.setBorder(Border.EMPTY);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HBox.setMargin(currentEpochLabel, new Insets(15, 5, 10, 0));
        HBox.setMargin(currentEpochField, new Insets(10, 5, 10, 0));
        HBox.setMargin(currentEpochRefreshButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(tsToHumanField, new Insets(10, 5, 10, 0));
        HBox.setMargin(tsToHumanButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(millisToTimeButton, new Insets(10, 5, 10, 0));

        GridPane.setMargin(epochYearField, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochMonthField, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochDayField, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochHourField, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochMinuteField, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochSecondField, new Insets(10, 5, 0, 0));
        GridPane.setMargin(humanToTsButton, new Insets(10, 5, 0, 0));
        GridPane.setMargin(timeZoneComboBox, new Insets(10, 5, 0, 0));

        long now = System.currentTimeMillis();
        currentEpochField.setText(Long.toString(now));
        tsToHumanField.setText(Long.toString(now));

        LocalDateTime date = LocalDateTime.now();
        epochYearField.setText(String.valueOf(date.getYear()));
        epochMonthField.setText(String.valueOf(date.getMonthValue()));
        epochDayField.setText(String.valueOf(date.getDayOfMonth()));
        epochHourField.setText(String.valueOf(date.getHour()));
        epochMinuteField.setText(String.valueOf(date.getMinute()));
        epochSecondField.setText(String.valueOf(date.getSecond()));

        timeZoneComboBox.getItems().setAll(TimeZone.getAvailableIDs());
        timeZoneComboBox.setValue("UTC");
        timeZoneComboBoxIndex = 0;
    }
}
