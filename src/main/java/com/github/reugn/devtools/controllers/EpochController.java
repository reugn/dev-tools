package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.utils.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
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

        currentEpoch.setText(Long.toString(System.currentTimeMillis()));
    }
}
