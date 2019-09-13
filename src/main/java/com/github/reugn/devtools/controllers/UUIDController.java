package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.utils.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class UUIDController implements Initializable, Logger {

    @FXML
    public ComboBox<Integer> uuidAmount;

    @FXML
    public Label uuidAmountLabel;

    @FXML
    private CheckBox uuidUpperCase;

    @FXML
    private CheckBox uuidHyphens;

    @FXML
    private TextArea uuidResult;

    @FXML
    private void handleGenerateAction(final ActionEvent event) {
        StringBuilder buff = new StringBuilder();
        Integer amount = uuidAmount.getValue();
        for (int i = 0; i < amount; i++) {
            String uuid = UUID.randomUUID().toString();
            if (uuidUpperCase.isSelected()) {
                uuid = uuid.toUpperCase();
            }
            if (!uuidHyphens.isSelected()) {
                uuid = uuid.replace("-", "");
            }
            buff.append(uuid).append("\n");
        }
        uuidResult.setText(buff.toString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uuidAmount.getItems().setAll(1, 2, 3, 5, 10, 20, 50, 100);
        uuidAmount.setValue(1);
        uuidAmountLabel.setPadding(new Insets(5));
    }
}
