package com.example.ovapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalTime;

/**
 * JavaFX OV application with automatic time refresh in the title.
 */
public class HelloApplication extends Application {

    /**
     * Entry point of the application.
     *
     * @param primaryStage The primary stage for the application.
     * @throws IOException If an error occurs while loading the FXML file.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 900, 800);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    primaryStage.setTitle("OV Application - " + getCurrentTime());
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.play();

        primaryStage.setScene(scene);

        primaryStage.show();

    }

    /**
     * Get the current time as a formatted string.
     *
     * @return A string representing the current time.
     */
    private String getCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        return "Current Time: " + currentTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
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