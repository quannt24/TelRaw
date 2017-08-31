package com.hexa.telraw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelRaw extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            VBox root = (VBox) FXMLLoader.load(getClass().getResource("TelRaw.fxml"));
            Scene scene = new Scene(root, 640, 480);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
