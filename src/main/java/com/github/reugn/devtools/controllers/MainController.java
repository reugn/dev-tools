package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.utils.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable, Logger {

    @FXML
    private MenuBar menuBar;

    @FXML
    private TabPane tabPane;

    @FXML
    private void handleKeyInput(final InputEvent event) {
        if (event instanceof KeyEvent) {
            final KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.A) {
                debug("handleKeyInput");
            }
        }
    }

    @FXML
    private void handleExitAction(final ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handleAboutAction(final ActionEvent event) {
        debug("handleAboutAction");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
