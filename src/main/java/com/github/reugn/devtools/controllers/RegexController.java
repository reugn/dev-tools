package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.models.RegexResult;
import com.github.reugn.devtools.services.RegexService;
import com.github.reugn.devtools.utils.Elements;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.controlsfx.control.CheckComboBox;
import org.fxmisc.richtext.CodeArea;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RegexController implements Initializable {

    @FXML
    private TextField regexExpression;
    @FXML
    private Button regexCalculateButton;
    @FXML
    private Button regexClearButton;
    @FXML
    private Label regexMessage;
    @FXML
    private CodeArea regexTarget;
    @FXML
    private TextArea regexResult;
    @FXML
    private Label regexLabel;
    @FXML
    private CheckComboBox<String> regexFlagsComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        regexExpression.setPrefWidth(512);
        regexTarget.setPrefHeight(256);
        HBox.setMargin(regexExpression, new Insets(10, 5, 10, 0));
        HBox.setMargin(regexCalculateButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(regexClearButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(regexFlagsComboBox, new Insets(10, 5, 10, 0));

        regexLabel.setPadding(new Insets(15, 5, 10, 0));
        regexMessage.setPadding(new Insets(15, 5, 10, 0));
        regexMessage.setTextFill(Color.RED);

        regexFlagsComboBox.getItems().addAll("global", "multiline", "insensitive", "unicode");
        regexFlagsComboBox.setTitle("Flags");
        regexFlagsComboBox.getCheckModel().checkIndices(0);
    }

    @FXML
    public void handleKeyMatch(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            doMatch();
        }
    }

    @FXML
    public void handleMatch(ActionEvent actionEvent) {
        doMatch();
    }

    private void doMatch() {
        regexMessage.setText("");
        if (validateInput()) {
            try {
                RegexResult result = RegexService.match(regexExpression.getText(), regexTarget.getText(),
                        regexFlagsComboBox.getCheckModel().getCheckedItems());
                regexResult.setText(result.getMatchSummary());
                List<Pair<Integer, Integer>> l = result.getFullMatchIndexes();
                if (!l.isEmpty()) {
                    regexTarget.deselect();
                    regexTarget.moveTo(l.get(0).getKey());
                    regexTarget.requestFollowCaret();
                    regexTarget.selectRange(l.get(0).getKey(), l.get(0).getValue());
                }
            } catch (Exception e) {
                regexMessage.setText("Invalid regex");
            }
        }
    }

    private boolean validateInput() {
        resetBorders();
        boolean isValid = true;
        if (regexExpression.getText().isEmpty()) {
            regexExpression.setBorder(Elements.alertBorder);
            isValid = false;
        }
        if (regexTarget.getText().isEmpty()) {
            regexTarget.setBorder(Elements.alertBorder);
            isValid = false;
        }
        return isValid;
    }

    private void resetBorders() {
        regexExpression.setBorder(Border.EMPTY);
        regexTarget.setBorder(Border.EMPTY);
    }

    @FXML
    public void handleClear(ActionEvent actionEvent) {
        resetBorders();
        regexExpression.setText("");
        regexTarget.replaceText("");
        regexResult.setText("");
    }
}
