package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.models.RegexResult;
import com.github.reugn.devtools.services.RegexService;
import com.github.reugn.devtools.utils.Elements;
import com.google.inject.Inject;
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

    private final RegexService regexService;

    @FXML
    private TextField regexExpressionField;
    @FXML
    private Button regexCalculateButton;
    @FXML
    private Button regexClearButton;
    @FXML
    private Label regexMessageLabel;
    @FXML
    private CodeArea regexTargetCodeArea;
    @FXML
    private TextArea regexResultTextArea;
    @FXML
    private Label regexLabel;
    @FXML
    private CheckComboBox<String> regexFlagsComboBox;

    @Inject
    public RegexController(RegexService regexService) {
        this.regexService = regexService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        regexExpressionField.setPrefWidth(512);
        regexTargetCodeArea.setPrefHeight(256);
        HBox.setMargin(regexExpressionField, new Insets(10, 5, 10, 0));
        HBox.setMargin(regexCalculateButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(regexClearButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(regexFlagsComboBox, new Insets(10, 5, 10, 0));

        regexLabel.setPadding(new Insets(15, 5, 10, 0));
        regexMessageLabel.setPadding(new Insets(15, 5, 10, 0));
        regexMessageLabel.setTextFill(Color.RED);

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
    public void handleMatch(@SuppressWarnings("unused") ActionEvent actionEvent) {
        doMatch();
    }

    private void doMatch() {
        regexMessageLabel.setText("");
        if (validateInput()) {
            try {
                RegexResult result = regexService.match(regexExpressionField.getText(), regexTargetCodeArea.getText(),
                        regexFlagsComboBox.getCheckModel().getCheckedItems());
                regexResultTextArea.setText(result.getMatchSummary());
                List<Pair<Integer, Integer>> l = result.getFullMatchIndexes();
                if (!l.isEmpty()) {
                    regexTargetCodeArea.deselect();
                    regexTargetCodeArea.moveTo(l.get(0).getKey());
                    regexTargetCodeArea.requestFollowCaret();
                    regexTargetCodeArea.selectRange(l.get(0).getKey(), l.get(0).getValue());
                }
            } catch (Exception e) {
                regexMessageLabel.setText("Invalid regex");
            }
        }
    }

    private boolean validateInput() {
        resetBorders();
        boolean isValid = true;
        if (regexExpressionField.getText().isEmpty()) {
            regexExpressionField.setBorder(Elements.alertBorder);
            isValid = false;
        }
        if (regexTargetCodeArea.getText().isEmpty()) {
            regexTargetCodeArea.setBorder(Elements.alertBorder);
            isValid = false;
        }
        return isValid;
    }

    private void resetBorders() {
        regexExpressionField.setBorder(Border.EMPTY);
        regexTargetCodeArea.setBorder(Border.EMPTY);
    }

    @FXML
    public void handleClear(@SuppressWarnings("unused") ActionEvent actionEvent) {
        resetBorders();
        regexExpressionField.setText("");
        regexTargetCodeArea.replaceText("");
        regexResultTextArea.setText("");
    }
}
