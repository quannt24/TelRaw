package hexa.telraw;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        VBox root;
        try {
            root = (VBox) FXMLLoader.load(getClass().getResource("TelRaw.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}