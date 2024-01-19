package com.example.ovapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

/**
 * JavaFX OV application with automatic time refresh in the title.
 */
public class OVApplication extends Application {

    /**
     * Entry point of the application.
     *
     * @param primaryStage The primary stage for the application.
     * @throws IOException If an error occurs while loading the FXML file.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OVApplication.class.getResource("planner-view.fxml"));
        Parent root = fxmlLoader.load();
        
        Scene scene = new Scene(root, 1000, 800);

        primaryStage.setTitle("OV Application");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        primaryStage.getIcons().add(new Image(OVApplication.class.getResourceAsStream("/com/example/ovapp/images/OVIcon.png")));

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.out.print("Closing Application...");
            System.exit(0);

        });

        primaryStage.show();
    }

    /**
     * Main method to launch the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}