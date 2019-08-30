package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.utils.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class UUIDController implements Initializable, Logger {

    @FXML
    private CheckBox uuidUpperCase;

    @FXML
    private CheckBox uuidHyphens;

    @FXML
    private TextField uuidResult;

    @FXML
    private void handleGenerateAction(final ActionEvent event) {
        String uuid = UUID.randomUUID().toString();
        if (uuidUpperCase.isSelected()) {
            uuid = uuid.toUpperCase();
        }
        if (!uuidHyphens.isSelected()) {
            uuid = uuid.replace("-", "");
        }
        uuidResult.setText(uuid);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
