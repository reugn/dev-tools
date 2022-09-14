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

public class Main {

    public static void main(String[] args) {
        App.main(args);
    }

    public static class App extends Application {

        public static void main(String[] args) {
            launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            Injector injector = Guice.createInjector(new GuiceModule());
            FXMLLoader fxmlLoader = injector.getInstance(FXMLLoader.class);
            fxmlLoader.setLocation(getClass().getResource("/views/main.fxml"));
            Parent root = fxmlLoader.load();
            MainController mainController = fxmlLoader.getController();
            primaryStage.setTitle("Development tools");
            primaryStage.getIcons().add(new Image("/images/icons8-toolbox-64.png"));
            Scene scene = new Scene(root, 900, 500);
            scene.getStylesheets().addAll("/css/main-dark.css", "/css/json-highlighting-dark.css");
            scene.getRoot().setStyle("-fx-font-family: 'Arial'");
            primaryStage.setScene(scene);
            mainController.setScene(scene);
            primaryStage.show();
        }
    }
}
