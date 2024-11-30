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
    private ComboBox<Integer> uuidAmountComboBox;
    @FXML
    private Label uuidAmountLabel;
    @FXML
    private CheckBox uuidUpperCaseCheckBox;
    @FXML
    private CheckBox uuidHyphensCheckBox;
    @FXML
    private TextArea generatorResultTextArea;
    @FXML
    private CheckBox pwdLowCharsCheckBox;
    @FXML
    private CheckBox pwdDigitsCheckBox;
    @FXML
    private CheckBox pwdUpperCharsCheckBox;
    @FXML
    private CheckBox pwdSymbolsCheckBox;
    @FXML
    private TextField pwdLengthField;
    @FXML
    private Label pwdLengthLabel;
    @FXML
    private Button clearButton;

    @FXML
    private void handleGenerateUUIDAction(@SuppressWarnings("unused") final ActionEvent event) {
        StringBuilder builder = new StringBuilder();
        Integer amount = uuidAmountComboBox.getValue();
        for (int i = 0; i < amount; i++) {
            String uuid = UUID.randomUUID().toString();
            if (uuidUpperCaseCheckBox.isSelected()) {
                uuid = uuid.toUpperCase();
            }
            if (!uuidHyphensCheckBox.isSelected()) {
                uuid = uuid.replace("-", "");
            }
            builder.append(uuid).append("\n");
        }
        generatorResultTextArea.setText(builder.toString());
    }

    @FXML
    private void handleGeneratePasswordAction(@SuppressWarnings("unused") final ActionEvent actionEvent) {
        int length;
        try {
            pwdLengthField.setBorder(Border.EMPTY);
            length = validatePasswordLength();
        } catch (Exception e) {
            pwdLengthField.setBorder(Elements.alertBorder);
            return;
        }
        PasswordGenerator generator = new PasswordGenerator.PasswordGeneratorBuilder()
                .withLowerChars(pwdLowCharsCheckBox.isSelected())
                .withDigits(pwdDigitsCheckBox.isSelected())
                .withUpperChars(pwdUpperCharsCheckBox.isSelected())
                .withSymbols(pwdSymbolsCheckBox.isSelected())
                .build();
        generatorResultTextArea.setText(generator.generate(length));
    }

    private int validatePasswordLength() {
        int length = Integer.parseInt(pwdLengthField.getText());
        checkState(length > 0, "Invalid password length");
        return length;
    }

    @FXML
    private void handleClearResult(@SuppressWarnings("unused") final ActionEvent actionEvent) {
        generatorResultTextArea.setText("");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uuidAmountComboBox.getItems().setAll(1, 2, 3, 5, 10, 20, 50, 100);
        uuidAmountComboBox.setValue(1);
        uuidAmountLabel.setPadding(new Insets(5));
        pwdLengthLabel.setPadding(new Insets(5));
        VBox.setMargin(clearButton, new Insets(5, 0, 0, 0));
        pwdLengthField.setPrefWidth(64);
        pwdLengthField.setText("16");

        pwdLowCharsCheckBox.setSelected(true);
        pwdDigitsCheckBox.setSelected(true);
    }
}
