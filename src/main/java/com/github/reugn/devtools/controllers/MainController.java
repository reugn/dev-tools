package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.utils.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.jar.Manifest;

public class MainController implements Initializable, Logger {

    @FXML
    private MenuBar menuBar;

    @FXML
    private TabPane tabPane;

    private Alert about;

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
        about.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About");
        about.setHeaderText("Development tools");
        VBox vbox = new VBox();
        Label l = new Label(" version: " + getVersion());
        Hyperlink link = new Hyperlink("https://github.com/reugn/dev-tools");
        vbox.getChildren().addAll(l, link);
        about.getDialogPane().setContent(vbox);
    }

    private String getVersion() {
        String version;
        try {
            Class clazz = this.getClass();
            String classPath = clazz.getResource(clazz.getSimpleName() + ".class").toString();
            String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
                    "/META-INF/MANIFEST.MF";
            Manifest manifest = new Manifest(new URL(manifestPath).openStream());
            version = manifest.getMainAttributes().getValue("Implementation-Version");
        } catch (Exception e) {
            return "NA";
        }
        return version;
    }
}
