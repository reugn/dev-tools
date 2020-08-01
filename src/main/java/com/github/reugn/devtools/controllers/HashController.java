package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.services.HashService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class HashController implements Initializable {

    @FXML
    private TextArea hashInput;

    @FXML
    private TextArea hashOutput;

    @FXML
    private ComboBox<String> hashAlgoSelector;

    @FXML
    private Button hashCalculateButton;

    @FXML
    public Button hashMoveUpButton;

    @FXML
    private Button hashClearButton;

    @FXML
    private Label hashMessage;

    @FXML
    private void handleClear(final ActionEvent event) {
        hashInput.setText("");
        hashOutput.setText("");
        hashMessage.setText("");
    }

    @FXML
    private void handleCalculate(final ActionEvent event) {
        hashMessage.setText("");
        try {
            String enc;
            String data = hashInput.getText();
            switch (hashAlgoSelector.getSelectionModel().getSelectedItem()) {
                case "md5":
                    enc = HashService.calculateHash(data, "MD5");
                    break;
                case "sha1":
                    enc = HashService.calculateHash(data, "SHA-1");
                    break;
                case "sha256":
                    enc = HashService.calculateHash(data, "SHA-256");
                    break;
                case "murmur3_128":
                    enc = HashService.murmur3_128(data);
                    break;
                case "Url encode":
                    enc = HashService.urlEncode(data);
                    break;
                case "Url decode":
                    enc = HashService.urlDecode(data);
                    break;
                case "Base64 encode":
                    enc = HashService.base64Encode(data);
                    break;
                case "Base64 decode":
                    enc = HashService.base64Decode(data);
                    break;
                default:
                    enc = "NA";
                    break;
            }
            hashOutput.setText(enc);
        } catch (Exception e) {
            hashMessage.setText(e.getMessage());
        }
    }

    @FXML
    private void handleMoveUp(final ActionEvent event) {
        if (!hashOutput.getText().isEmpty()) {
            hashInput.setText(hashOutput.getText());
            hashOutput.setText("");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hashAlgoSelector.getItems().setAll("md5", "sha1", "sha256", "murmur3_128",
                "Url encode", "Url decode",
                "Base64 encode", "Base64 decode");
        hashMessage.setPadding(new Insets(5));
        hashMessage.setTextFill(Color.RED);

        HBox.setMargin(hashAlgoSelector, new Insets(10, 5, 10, 0));
        HBox.setMargin(hashCalculateButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(hashMoveUpButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(hashClearButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(hashMessage, new Insets(10, 5, 10, 0));
    }
}
