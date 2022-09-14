package com.github.reugn.devtools.controllers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.util.ResourceBundle;

public class JsonController extends TabPaneController {

    @FXML
    private static JsonController self;

    @Inject
    public JsonController(Provider<FXMLLoader> fxmlLoaderProvider) {
        super(fxmlLoaderProvider);
    }

    static JsonController instance() {
        return self;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        self = this;
    }

    @Override
    protected String getInnerResource() {
        return "/views/json_tab.fxml";
    }
}
