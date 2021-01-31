package com.github.reugn.devtools.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.jar.Manifest;

public class MainController implements Initializable {

    private static final Logger log = Logger.getLogger(MainController.class);

    @FXML
    private MenuBar menuBar;

    @FXML
    private TabPane tabPane;

    private Alert about;

    private Scene scene;

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @FXML
    private void handleExitAction(final ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handleDarkThemeAction(final ActionEvent event) {
        scene.getStylesheets().clear();
        scene.getStylesheets().addAll("/css/main-dark.css", "/css/json-highlighting-dark.css");
    }

    @FXML
    private void handleLightThemeAction(final ActionEvent event) {
        scene.getStylesheets().clear();
        scene.getStylesheets().addAll("/css/main.css", "/css/json-highlighting.css");
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
        Label versionLabel = new Label(" version: " + getVersion());
        Hyperlink link = new Hyperlink("https://github.com/reugn/dev-tools");
        vbox.getChildren().addAll(versionLabel, link);
        about.getDialogPane().setContent(vbox);
    }

    private String getVersion() {
        String version;
        try {
            Class<?> clazz = this.getClass();
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
