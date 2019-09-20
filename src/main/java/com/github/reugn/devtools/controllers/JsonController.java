package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.services.JsonService;
import com.github.reugn.devtools.utils.JsonSearchState;
import com.github.reugn.devtools.utils.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.textfield.CustomTextField;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonController implements Initializable, Logger {

    public Button clearSpacesButton;
    public Button formatButton;
    public Button clearButton;
    public Button buttonCloseSearch;
    public ToolBar barSearch;
    public CustomTextField fieldSearch;
    public Button buttonSearchUp;
    public Button buttonSearchDown;
    public Label labelMatches;

    private JsonSearchState searchState;

    @FXML
    private Label jsonMessage;

    @FXML
    private CodeArea jsonArea;

    private static final Pattern JSON_REGEX = Pattern.compile("(?<JSONCURLY>\\{|\\})|" +
            "(?<JSONPROPERTY>\\\".*\\\")\\s*:\\s*|" +
            "(?<JSONVALUE>\\\".*\\\")|" +
            "\\[(?<JSONARRAY>.*)\\]|" +
            "(?<JSONNUMBER>\\d+.?\\d*)|" +
            "(?<JSONBOOL>true|false)|" +
            "(?<JSONNULL>null)");

    @FXML
    private void handlePrettyPrint(final ActionEvent event) {
        String data = jsonArea.getText();
        try {
            String pretty = JsonService.format(data);
            jsonArea.replaceText(pretty);
            jsonMessage.setText("");
        } catch (Exception e) {
            jsonMessage.setText("Invalid JSON");
        }
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = JSON_REGEX.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass
                    = matcher.group("JSONPROPERTY") != null ? "json_property"
                    : matcher.group("JSONARRAY") != null ? "json_array"
                    : matcher.group("JSONCURLY") != null ? "json_curly"
                    : matcher.group("JSONBOOL") != null ? "json_bool"
                    : matcher.group("JSONNULL") != null ? "json_null"
                    : matcher.group("JSONNUMBER") != null ? "json_number"
                    : matcher.group("JSONVALUE") != null ? "json_value"
                    : null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    @FXML
    private void handleClearSpaces(final ActionEvent event) {
        String data = jsonArea.getText();
        try {
            String cleared = JsonService.clearSpaces(data);
            jsonArea.replaceText(cleared);
            jsonMessage.setText("");
        } catch (Exception e) {
            jsonMessage.setText("Invalid JSON");
        }
    }

    @FXML
    private void handleClear(final ActionEvent event) {
        jsonMessage.setText("");
        jsonArea.replaceText("");
        handleCloseSearchAction(event);
    }

    @FXML
    private void handleSearchBarEvent(final KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.F) {
            barSearch.setVisible(true);
            barSearch.setManaged(true);
            fieldSearch.requestFocus();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            barSearch.setVisible(false);
            barSearch.setManaged(false);
        }
    }

    @FXML
    private void handleCloseSearchAction(final ActionEvent event) {
        jsonArea.deselect();
        fieldSearch.setText("");
        labelMatches.setText("");
        barSearch.setVisible(false);
        barSearch.setManaged(false);
    }

    @FXML
    private void handleSearchBarAction(final KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleSearchDownAction(new ActionEvent());
        }
    }

    @FXML
    private void handleSearchUpAction(final ActionEvent event) {
        Optional<JsonSearchState.SearchSpan> span = getSearchState().prev();
        jsonArea.deselect();
        span.ifPresent(searchSpan -> jsonArea.selectRange(searchSpan.getFrom(), searchSpan.getTo()));
        labelMatches.setText(getSearchState().toString());
    }

    @FXML
    private void handleSearchDownAction(final ActionEvent event) {
        Optional<JsonSearchState.SearchSpan> span = getSearchState().next();
        jsonArea.deselect();
        span.ifPresent(searchSpan -> jsonArea.selectRange(searchSpan.getFrom(), searchSpan.getTo()));
        labelMatches.setText(getSearchState().toString());
    }

    private JsonSearchState getSearchState() {
        if (searchState == null || !searchState.isValid(fieldSearch.getText(), jsonArea.getText())) {
            searchState = new JsonSearchState(fieldSearch.getText(), jsonArea.getText());
        }
        return searchState;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jsonArea.setPrefHeight(1024);
        jsonArea.setWrapText(true);
        jsonArea.textProperty().addListener((obs, oldText, newText) -> {
            jsonArea.setStyleSpans(0, computeHighlighting(newText));
        });
        jsonMessage.setPadding(new Insets(5));
        jsonMessage.setTextFill(Color.RED);

        HBox.setMargin(clearSpacesButton, new Insets(0, 5, 10, 0));
        HBox.setMargin(formatButton, new Insets(0, 5, 10, 0));
        HBox.setMargin(clearButton, new Insets(0, 5, 10, 0));
        HBox.setMargin(jsonMessage, new Insets(0, 5, 10, 0));

        barSearch.setVisible(false);
        barSearch.setManaged(false);
    }
}
