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
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.FileReader;
import java.net.URL;
import java.util.ResourceBundle;

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
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            return model.getVersion();
        } catch (Exception e) {
            return "NA";
        }
    }
}
