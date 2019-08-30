package com.github.reugn.devtools;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
        primaryStage.setTitle("Dev-tools");
        primaryStage.getIcons().add(new Image("/images/icons8-toolbox-64.png"));
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add("/css/main.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
