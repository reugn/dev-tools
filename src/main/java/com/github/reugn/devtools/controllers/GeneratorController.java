package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.utils.Logger;
import com.github.reugn.devtools.utils.PasswordGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ResourceBundle;
import java.util.UUID;

public class GeneratorController implements Initializable, Logger {

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
    private void handleGenerateUUIDAction(final ActionEvent event) {
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
    private void handleGeneratePasswordAction(final ActionEvent actionEvent) {
        int length;
        try {
            pwdLength.setBorder(Border.EMPTY);
            length = validatePasswordLength();
        } catch (Exception e) {
            pwdLength.setBorder(new Border(new BorderStroke(Color.RED,
                    BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
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

    private int validatePasswordLength() throws Exception {
        int length = Integer.parseInt(pwdLength.getText());
        if (length < 1) throw new InvalidParameterException("Invalid password length");
        return length;
    }

    @FXML
    private void handleClearResult(final ActionEvent actionEvent) {
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
