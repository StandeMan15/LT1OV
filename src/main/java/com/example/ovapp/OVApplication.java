package com.example.ovapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

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
        Locale.setDefault(new Locale("en")); // Nederlands

        Scene scene = new Scene(fxmlLoader.load(), 1000, 800);

        primaryStage.setTitle("OV Application");

        primaryStage.setScene(scene);

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