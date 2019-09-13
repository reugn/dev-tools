package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.services.JsonService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class JsonController implements Initializable {

    public Button clearSpacesButton;
    public Button formatButton;
    public Button clearButton;

    @FXML
    private Label jsonMessage;

    @FXML
    private TextArea jsonArea;

    @FXML
    private void handlePrettyPrint(final ActionEvent event) {
        String data = jsonArea.getText();
        try {
            String pretty = JsonService.format(data);
            jsonArea.setText(pretty);
            jsonMessage.setText("");
        } catch (Exception e) {
            jsonMessage.setText("Invalid JSON");
        }
    }

    @FXML
    private void handleClearSpaces(final ActionEvent event) {
        String data = jsonArea.getText();
        try {
            String cleared = JsonService.clearSpaces(data);
            jsonArea.setText(cleared);
            jsonMessage.setText("");
        } catch (Exception e) {
            jsonMessage.setText("Invalid JSON");
        }
    }

    @FXML
    private void handleClear(final ActionEvent event) {
        jsonMessage.setText("");
        jsonArea.setText("");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jsonArea.setPrefRowCount(1024);
        jsonMessage.setPadding(new Insets(5));
        jsonMessage.setTextFill(Color.RED);

        HBox.setMargin(clearSpacesButton, new Insets(0, 5, 10, 0));
        HBox.setMargin(formatButton, new Insets(0, 5, 10, 0));
        HBox.setMargin(clearButton, new Insets(0, 5, 10, 0));
        HBox.setMargin(jsonMessage, new Insets(0, 5, 10, 0));
    }
}
