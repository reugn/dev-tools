package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.utils.ResourceLoader;
import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.jar.Manifest;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unused")
public class MainController extends ResourceLoader implements Initializable {

    private static final Logger log = LogManager.getLogger(MainController.class);

    private final String[] lightTheme = new String[]{
            "/css/main.css",
            "/css/json-highlighting.css"
    };
    private final String[] darkTheme = new String[]{
            "/css/main-dark.css",
            "/css/json-highlighting-dark.css"
    };
    private final Map<String, Node> nodeCache = new HashMap<>();

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu toolsMenu;
    @FXML
    private BorderPane mainPane;

    private Alert about;
    private Scene scene;

    @Inject
    public MainController(Provider<FXMLLoader> fxmlLoaderProvider) {
        super(fxmlLoaderProvider);
    }

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

        // initialize the main pane
        setMainPane("/views/json_editor.fxml");
    }

    @FXML
    private void handleMenuAction(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String fxmlFile = menuItem.getUserData().toString();
        String resourcePath = String.format("/views/%s", fxmlFile);
        Platform.runLater(() -> updateToolsMenuItems(menuItem.getText()));
        setMainPane(resourcePath);
    }

    private void updateToolsMenuItems(String text) {
        for (MenuItem item : toolsMenu.getItems()) {
            item.setDisable(item.getText().equals(text));
        }
    }

    private void setMainPane(String resourcePath) {
        Node node = nodeCache.computeIfAbsent(resourcePath, this::loadFXML);
        mainPane.setCenter(node);
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
