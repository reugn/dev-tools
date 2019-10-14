package com.github.reugn.devtools.controllers;

import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public class RestAPIController extends TabPaneController {

    @FXML
    private static RestAPIController self;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        self = this;
    }

    static RestAPIController instance() {
        return self;
    }

    @Override
    protected String getInnerResource() {
        return "/views/rest_api_tab.fxml";
    }
}
