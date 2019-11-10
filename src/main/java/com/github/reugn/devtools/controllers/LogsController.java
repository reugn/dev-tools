package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.services.LogsService;
import com.github.reugn.devtools.utils.Elements;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class LogsController implements Initializable {

    @FXML
    private Label delayLowerLabel;
    @FXML
    private TextField delayLowerField;
    @FXML
    private Label delayUpperLabel;
    @FXML
    private TextField delayUpperField;
    @FXML
    private Label limitLabel;
    @FXML
    private TextField limitField;
    @FXML
    private Label logTypeLabel;
    @FXML
    private ComboBox<String> logTypeComboBox;
    @FXML
    private Label outputFileLabel;
    @FXML
    private Button browseButton;
    @FXML
    private TextField outputFileField;
    @FXML
    private Button logGenerateButton;
    @FXML
    private Button logStopButton;
    @FXML
    private Button logClearButton;
    @FXML
    private Label logMessage;
    @FXML
    private TextArea logsResult;

    private PrintWriter writer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HBox.setMargin(delayLowerLabel, new Insets(15, 5, 5, 0));
        HBox.setMargin(delayLowerField, new Insets(10, 5, 5, 0));
        HBox.setMargin(delayUpperLabel, new Insets(15, 5, 5, 0));
        HBox.setMargin(delayUpperField, new Insets(10, 5, 5, 0));

        HBox.setMargin(limitLabel, new Insets(15, 5, 5, 0));
        HBox.setMargin(limitField, new Insets(10, 5, 5, 0));
        HBox.setMargin(logTypeLabel, new Insets(15, 5, 5, 0));
        HBox.setMargin(logTypeComboBox, new Insets(10, 5, 5, 0));

        HBox.setMargin(outputFileLabel, new Insets(15, 5, 5, 0));
        HBox.setMargin(browseButton, new Insets(10, 0, 5, 0));
        HBox.setMargin(outputFileField, new Insets(10, 5, 5, 0));
        HBox.setMargin(logGenerateButton, new Insets(10, 5, 5, 0));
        HBox.setMargin(logStopButton, new Insets(10, 5, 5, 0));
        HBox.setMargin(logClearButton, new Insets(10, 5, 5, 0));
        HBox.setMargin(logMessage, new Insets(10, 5, 5, 0));

        logsResult.setPrefRowCount(128);
        logStopButton.setDisable(true);
        logTypeComboBox.getItems().setAll("Apache Common", "Log4j Default", "RFC3164", "RFC5424");
        logTypeComboBox.setValue("Apache Common");
    }

    @FXML
    private void handleGenerate(ActionEvent actionEvent) {
        resetBorders();
        if (!validate()) return;
        logGenerateButton.setDisable(true);
        logStopButton.setDisable(false);
        LogsService.startLog(
                logTypeComboBox.getValue(),
                Integer.parseInt(delayLowerField.getText()),
                Integer.parseInt(delayUpperField.getText()),
                writer,
                Integer.parseInt(limitField.getText()),
                logsResult
        );
    }

    @FXML
    private void handleClear(ActionEvent actionEvent) {
        resetBorders();
        logsResult.setText("");
    }

    @FXML
    private void handleStop(ActionEvent actionEvent) {
        logGenerateButton.setDisable(false);
        logStopButton.setDisable(true);
        LogsService.stopLog();
    }

    @FXML
    private void handleBrowse(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        File f = chooser.showDialog(null);
        if (f != null)
            outputFileField.setText(f.getPath());
    }

    private boolean validate() {
        if (!isNumericPositive(delayLowerField.getText())) {
            delayLowerField.setBorder(Elements.alertBorder);
            return false;
        } else if (!isNumericPositive(delayUpperField.getText())) {
            delayUpperField.setBorder(Elements.alertBorder);
            return false;
        } else if (!isNumericPositive(limitField.getText())) {
            limitField.setBorder(Elements.alertBorder);
            return false;
        } else {
            String fileName = outputFileField.getText();
            if (!fileName.isEmpty()) {
                try {
                    writer = new PrintWriter(new FileWriter(fileName));
                } catch (Exception e) {
                    outputFileField.setBorder(Elements.alertBorder);
                    return false;
                }
            }
        }
        return true;
    }

    private void resetBorders() {
        delayLowerField.setBorder(Border.EMPTY);
        delayUpperField.setBorder(Border.EMPTY);
        limitField.setBorder(Border.EMPTY);
        outputFileField.setBorder(Border.EMPTY);
    }

    private boolean isNumericPositive(String s) {
        return StringUtils.isNumeric(s) && Integer.parseInt(s) >= 0;
    }

}
