package com.github.reugn.devtools.controllers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.util.ResourceBundle;

public class JsonEditorController extends TabPaneController {

    @FXML
    private static JsonEditorController self;

    @Inject
    public JsonEditorController(Provider<FXMLLoader> fxmlLoaderProvider) {
        super(fxmlLoaderProvider);
    }

    static JsonEditorController instance() {
        return self;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        self = this;
    }

    @Override
    protected String getInnerResource() {
        return "/views/json_editor_tab.fxml";
    }
}
