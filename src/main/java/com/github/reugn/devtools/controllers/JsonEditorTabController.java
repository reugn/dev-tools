package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.services.JsonService;
import com.github.reugn.devtools.utils.JsonSearchState;
import com.google.inject.Inject;
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

@SuppressWarnings("unused")
public class JsonEditorTabController implements Initializable {

    private static final int TAB_TITLE_LENGTH = 12;
    private static final Pattern JSON_REGEX = Pattern.compile("(?<JSONCURLY>\\{|\\})|" +
            "(?<JSONPROPERTY>\\\".*\\\")\\s*:\\s*|" +
            "(?<JSONVALUE>\\\".*\\\")|" +
            "\\[(?<JSONARRAY>.*)\\]|" +
            "(?<JSONNUMBER>\\d+.?\\d*)|" +
            "(?<JSONBOOL>true|false)|" +
            "(?<JSONNULL>null)" +
            "(?<TEXT>.*)");

    private final JsonService jsonService;

    @FXML
    private Button clearSpacesButton;
    @FXML
    private Button formatButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button closeSearchButton;
    @FXML
    private ToolBar searchToolBar;
    @FXML
    private CustomTextField searchField;
    @FXML
    private Button searchUpButton;
    @FXML
    private Button searchDownButton;
    @FXML
    private Label matchesLabel;
    @FXML
    private Label jsonMessageLabel;
    @FXML
    private CodeArea jsonArea;

    private JsonSearchState searchState;

    @Inject
    public JsonEditorTabController(JsonService jsonService) {
        this.jsonService = jsonService;
    }

    @FXML
    private void handlePrettyPrint(final ActionEvent event) {
        String data = jsonArea.getText();
        try {
            String pretty = jsonService.format(data);
            if (!pretty.equals(jsonArea.getText())) {
                JsonEditorController.instance().innerTabPane.getSelectionModel().getSelectedItem()
                        .setText(tabTitle(jsonService.clearSpaces(data)));
                jsonArea.replaceText(pretty);
            }
            jsonMessageLabel.setText("");
        } catch (Exception e) {
            jsonMessageLabel.setText("Invalid JSON");
        }
    }

    @FXML
    private void handleClearSpaces(final ActionEvent event) {
        String data = jsonArea.getText();
        try {
            String cleared = jsonService.clearSpaces(data);
            JsonEditorController.instance().innerTabPane.getSelectionModel().getSelectedItem()
                    .setText(tabTitle(cleared));
            jsonArea.replaceText(cleared);
            jsonMessageLabel.setText("");
        } catch (Exception e) {
            jsonMessageLabel.setText("Invalid JSON");
        }
    }

    private String tabTitle(String json) {
        if (json.isEmpty()) return "New";
        return json.length() > TAB_TITLE_LENGTH ? json.substring(0, TAB_TITLE_LENGTH) : json;
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
                    : matcher.group("TEXT") != null ? "text"
                    : null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    @FXML
    private void handleClear(final ActionEvent event) {
        jsonMessageLabel.setText("");
        jsonArea.replaceText("");
        JsonEditorController.instance().innerTabPane.getSelectionModel().getSelectedItem().setText("New");
        handleCloseSearchAction(event);
    }

    @FXML
    private void handleSearchBarEvent(final KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.F) {
            searchToolBar.setVisible(true);
            searchToolBar.setManaged(true);
            searchField.requestFocus();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            searchToolBar.setVisible(false);
            searchToolBar.setManaged(false);
        }
    }

    @FXML
    private void handleCloseSearchAction(final ActionEvent event) {
        jsonArea.deselect();
        searchField.setText("");
        matchesLabel.setText("");
        searchToolBar.setVisible(false);
        searchToolBar.setManaged(false);
    }

    @FXML
    private void handleSearchBarAction(final KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleSearchDownAction(new ActionEvent());
        }
    }

    @FXML
    private void handleSearchUpAction(final ActionEvent event) {
        doSearch(getSearchState().prev());
    }

    @FXML
    private void handleSearchDownAction(final ActionEvent event) {
        doSearch(getSearchState().next());
    }

    private void doSearch(Optional<JsonSearchState.SearchSpan> span) {
        jsonArea.deselect();
        span.ifPresent(searchSpan -> {
            jsonArea.moveTo(searchSpan.getFrom());
            jsonArea.requestFollowCaret();
            jsonArea.selectRange(searchSpan.getFrom(), searchSpan.getTo());
        });
        matchesLabel.setText(getSearchState().toString());
    }

    private JsonSearchState getSearchState() {
        if (searchState == null || !searchState.isValid(searchField.getText(), jsonArea.getText())) {
            searchState = new JsonSearchState(searchField.getText(), jsonArea.getText());
        }
        return searchState;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jsonArea.setPrefHeight(1024);
        jsonArea.setWrapText(true);
        jsonArea.textProperty().addListener((obs, oldText, newText) ->
                jsonArea.setStyleSpans(0, computeHighlighting(newText)));
        jsonMessageLabel.setPadding(new Insets(5));
        jsonMessageLabel.setTextFill(Color.RED);

        HBox.setMargin(clearSpacesButton, new Insets(0, 5, 10, 0));
        HBox.setMargin(formatButton, new Insets(0, 5, 10, 0));
        HBox.setMargin(clearButton, new Insets(0, 5, 10, 0));
        HBox.setMargin(jsonMessageLabel, new Insets(0, 5, 10, 0));

        searchToolBar.setVisible(false);
        searchToolBar.setManaged(false);
    }
}
