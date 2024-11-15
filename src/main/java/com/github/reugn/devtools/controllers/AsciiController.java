package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.services.AsciiService;
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
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class AsciiController implements Initializable {

    private final AsciiService asciiService;
    @FXML
    private Label asciiLabel;
    @FXML
    private TextField asciiField;
    @FXML
    private Button asciiCalculateButton;
    @FXML
    private Button asciiClearButton;
    @FXML
    private Label asciiMessage;
    @FXML
    private TextArea asciiResult;
    @FXML
    private Label fontSizeLabel;
    @FXML
    private Label fontNameLabel;
    @FXML
    private TextField asciiCharField;
    @FXML
    private TextField fontSizeField;
    @FXML
    private ComboBox<String> fontNameComboBox;
    @FXML
    private ComboBox<String> fontStyleComboBox;
    @FXML
    private Label asciiCharLabel;
    @FXML
    private Label fontStyleLabel;

    @Inject
    public AsciiController(AsciiService asciiService) {
        this.asciiService = asciiService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HBox.setMargin(fontSizeLabel, new Insets(15, 5, 10, 0));
        HBox.setMargin(fontNameLabel, new Insets(15, 5, 10, 0));
        HBox.setMargin(asciiCharField, new Insets(10, 5, 10, 0));
        HBox.setMargin(fontSizeField, new Insets(10, 5, 10, 0));

        HBox.setMargin(fontNameComboBox, new Insets(10, 5, 10, 0));
        HBox.setMargin(fontStyleComboBox, new Insets(10, 5, 10, 0));
        HBox.setMargin(asciiCharLabel, new Insets(15, 5, 10, 0));
        HBox.setMargin(fontStyleLabel, new Insets(15, 5, 10, 0));

        HBox.setMargin(asciiLabel, new Insets(15, 5, 10, 0));
        HBox.setMargin(asciiField, new Insets(10, 5, 10, 0));
        HBox.setMargin(asciiCalculateButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(asciiClearButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(asciiMessage, new Insets(10, 5, 10, 0));

        asciiResult.setPrefRowCount(128);
        asciiResult.setFont(Font.font(java.awt.Font.MONOSPACED, 12));

        asciiCharField.setText("*");
        fontSizeField.setText("14");
        fontNameComboBox.getItems().setAll(javafx.scene.text.Font.getFamilies());
        fontNameComboBox.setValue("Arial");
        fontStyleComboBox.getItems().setAll("PLAIN", "BOLD", "ITALIC");
        fontStyleComboBox.setValue("PLAIN");
    }

    @FXML
    private void handleKeyMatch(@SuppressWarnings("unused") final KeyEvent keyEvent) {
        doConvert();
    }

    @FXML
    private void handleConvert(@SuppressWarnings("unused") final ActionEvent actionEvent) {
        doConvert();
    }

    private void doConvert() {
        resetBorders();
        asciiResult.setText("");
        if (validate()) {
            int height = Integer.parseInt(fontSizeField.getText());
            int style = asciiService.getStyleByName(fontStyleComboBox.getSelectionModel().getSelectedItem());
            String converted = asciiService.convert(asciiField.getText(), asciiCharField.getText(),
                    height, style, fontNameComboBox.getSelectionModel().getSelectedItem());
            asciiResult.setText(converted);
        }
    }

    @FXML
    private void handleClear(@SuppressWarnings("unused") final ActionEvent actionEvent) {
        asciiField.setText("");
        asciiResult.setText("");
    }

    private boolean validate() {
        if (asciiCharField.getText().length() != 1) {
            asciiCharField.setBorder(Elements.alertBorder);
            return false;
        } else if (!StringUtils.isNumeric(fontSizeField.getText())
                || Integer.parseInt(fontSizeField.getText()) > 128) {
            fontSizeField.setBorder(Elements.alertBorder);
            return false;
        }
        return !asciiField.getText().isEmpty();
    }

    private void resetBorders() {
        asciiCharField.setBorder(Border.EMPTY);
        fontSizeField.setBorder(Border.EMPTY);
    }
}
