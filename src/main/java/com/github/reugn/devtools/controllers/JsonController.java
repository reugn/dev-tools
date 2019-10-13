package com.github.reugn.devtools.controllers;

import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public class JsonController extends TabPaneController {

    @FXML
    private static JsonController self;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        self = this;
    }

    static JsonController instance() {
        return self;
    }

    @Override
    protected String getInnerResource() {
        return "/views/json_tab.fxml";
    }
}
