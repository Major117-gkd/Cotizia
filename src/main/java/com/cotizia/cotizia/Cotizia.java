package com.cotizia.cotizia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App
 */
public class Cotizia extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("login"), 800, 600);

        // Load CSS with null check
        java.net.URL cssURL = Cotizia.class.getResource("/com/cotizia/cotizia/ui/style.css");
        if (cssURL != null) {
            scene.getStylesheets().add(cssURL.toExternalForm());
        } else {
            System.err.println("Warning: style.css not found in classpath!");
        }

        stage.setScene(scene);
        stage.setTitle("Cotizia - Gestion de Cotisation");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        System.out.println("Cotizia.setRoot: Changing root to " + fxml);
        scene.setRoot(loadFXML(fxml));
        System.out.println("Cotizia.setRoot: Root changed successfully to " + fxml);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Cotizia.class.getResource("/com/cotizia/cotizia/ui/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
