package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.utils.Elements;
import com.github.reugn.devtools.utils.PasswordGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;

public class GeneratorController implements Initializable {

    @FXML
    private ComboBox<Integer> uuidAmount;
    @FXML
    private Label uuidAmountLabel;
    @FXML
    private CheckBox uuidUpperCase;
    @FXML
    private CheckBox uuidHyphens;
    @FXML
    private TextArea generatorResult;
    @FXML
    private CheckBox pwdLowChars;
    @FXML
    private CheckBox pwdDigits;
    @FXML
    private CheckBox pwdUpperChars;
    @FXML
    private CheckBox pwdSymbols;
    @FXML
    private TextField pwdLength;
    @FXML
    private Label pwdLengthLabel;
    @FXML
    private Button clearButton;

    @FXML
    private void handleGenerateUUIDAction(@SuppressWarnings("unused") final ActionEvent event) {
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
        generatorResult.setText(buff.toString());
    }

    @FXML
    private void handleGeneratePasswordAction(@SuppressWarnings("unused") final ActionEvent actionEvent) {
        int length;
        try {
            pwdLength.setBorder(Border.EMPTY);
            length = validatePasswordLength();
        } catch (Exception e) {
            pwdLength.setBorder(Elements.alertBorder);
            return;
        }
        PasswordGenerator generator = new PasswordGenerator.PasswordGeneratorBuilder()
                .withLowerChars(pwdLowChars.isSelected())
                .withDigits(pwdDigits.isSelected())
                .withUpperChars(pwdUpperChars.isSelected())
                .withSymbols(pwdSymbols.isSelected())
                .build();
        generatorResult.setText(generator.generate(length));
    }

    private int validatePasswordLength() {
        int length = Integer.parseInt(pwdLength.getText());
        checkState(length > 0, "Invalid password length");
        return length;
    }

    @FXML
    private void handleClearResult(@SuppressWarnings("unused") final ActionEvent actionEvent) {
        generatorResult.setText("");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uuidAmount.getItems().setAll(1, 2, 3, 5, 10, 20, 50, 100);
        uuidAmount.setValue(1);
        uuidAmountLabel.setPadding(new Insets(5));
        pwdLengthLabel.setPadding(new Insets(5));
        VBox.setMargin(clearButton, new Insets(5, 0, 0, 0));
        pwdLength.setPrefWidth(64);
        pwdLength.setText("16");

        pwdLowChars.setSelected(true);
        pwdDigits.setSelected(true);
    }
}
