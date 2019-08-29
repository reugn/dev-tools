package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.utils.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class UUIDController implements Initializable, Logger {

    @FXML
    private void handleGenerateAction(final ActionEvent event) {
        debug("handleGenerateAction");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
