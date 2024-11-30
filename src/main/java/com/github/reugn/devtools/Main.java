package com.github.reugn.devtools;

import com.github.reugn.devtools.controllers.MainController;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;

import static java.util.Objects.requireNonNull;

public class Main {

    public static void main(String[] args) {
        App.main(args);
    }

    public static class App extends Application {

        private static final String TITLE = "Development Tools";
        private static final int SCENE_WIDTH = 900;
        private static final int SCENE_HEIGHT = 500;

        public static void main(String[] args) {
            launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            setTaskbarIcon();
            Injector injector = Guice.createInjector(new GuiceModule());
            FXMLLoader fxmlLoader = injector.getInstance(FXMLLoader.class);
            fxmlLoader.setLocation(getClass().getResource("/views/main.fxml"));
            Parent root = fxmlLoader.load();
            MainController mainController = fxmlLoader.getController();
            primaryStage.setTitle(TITLE);
            primaryStage.setMinWidth(SCENE_WIDTH);
            primaryStage.setMinHeight(SCENE_HEIGHT);
            primaryStage.getIcons().add(new Image(requireNonNull(
                    getClass().getResourceAsStream("/images/icons8-toolbox-64.png"))));
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
            scene.getStylesheets().addAll("/css/main-dark.css", "/css/json-highlighting-dark.css");
            scene.getRoot().setStyle("-fx-font-family: 'Arial'");
            primaryStage.setScene(scene);
            mainController.setScene(scene);
            primaryStage.show();
        }

        private void setTaskbarIcon() {
            try {
                URL imageResource = getClass().getResource("/images/icons8-toolbox-64.png");
                java.awt.Image image = Toolkit.getDefaultToolkit().getImage(imageResource);
                Taskbar taskbar = Taskbar.getTaskbar();
                taskbar.setIconImage(image);
            } catch (Exception ignore) {
            }
        }
    }
}
