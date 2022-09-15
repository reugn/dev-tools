package com.github.reugn.devtools.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.jar.Manifest;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unused")
public class MainController implements Initializable {

    private static final Logger log = LogManager.getLogger(MainController.class);

    private final String[] lightTheme = new String[]{
            "/css/main.css",
            "/css/json-highlighting.css"
    };
    private final String[] darkTheme = new String[]{
            "/css/main-dark.css",
            "/css/json-highlighting-dark.css"
    };

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
        setTheme(scene.getStylesheets(), darkTheme);
        setTheme(about.getDialogPane().getStylesheets(), darkTheme);
    }

    @FXML
    private void handleLightThemeAction(final ActionEvent event) {
        setTheme(scene.getStylesheets(), lightTheme);
        setTheme(about.getDialogPane().getStylesheets(), lightTheme);
    }

    private void setTheme(final ObservableList<String> styleSheets, final String[] theme) {
        styleSheets.clear();
        styleSheets.addAll(theme);
    }

    @FXML
    private void handleAboutAction(final ActionEvent event) {
        about.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About");
        about.setHeaderText("Development Tools");
        about.getDialogPane().setStyle("-fx-font-family: 'Arial'");
        setTheme(about.getDialogPane().getStylesheets(), darkTheme);
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
            String classPath = requireNonNull(clazz.getResource(clazz.getSimpleName() + ".class")).toString();
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
