package com.example.ovapp;

import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HelloController {

    private String Departure;
    private String Arrival;


    @FXML
    private ChoiceBox<String> choiceBoxArrival;
    @FXML
    private ChoiceBox<String> choiceBoxDeparture;
    @FXML
    private Label routeOutText;

    @FXML
    private DatePicker datePicker;
    @FXML
    private Spinner<LocalTime> timePicker;

    @FXML
    protected void SearchRoute() {
        Departure = choiceBoxDeparture.getValue();
        Arrival = choiceBoxArrival.getValue();
        LocalDate selectedDate = datePicker.getValue();
        LocalTime selectedTime = timePicker.getValue();

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            routeOutText.setText(String.format("Dit is de route:\nVan: %s\nNaar: %s\nOp: %s\nOm: %s uur",
                    Departure, Arrival, selectedDate.format(dateFormatter), selectedTime.format(timeFormatter)));
        } catch (NullPointerException e) {
            routeOutText.setText(" Vul A.U.B. alle velden in.");
        }
    }

    @FXML
    protected void initialize() {
        initializeChoiceBox();
        initializeTimePicker();
        initializeDatePicker();
    }

    private void initializeChoiceBox() {
        ObservableList<String> stations = FXCollections.observableArrayList("Amsterdam", "Amersfoort", "Breda", "Enschede", "Schiphol", "Utrecht", "Zwolle");

        choiceBoxDeparture.setItems(stations);
        choiceBoxArrival.setItems(stations);

        // Set a default selection (optional)
        choiceBoxDeparture.setValue(stations.get(0));
        choiceBoxArrival.setValue(stations.get(0));

    }
    private void initializeTimePicker() {
        SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<>() {
            {
                setConverter(new StringConverter<>() {
                    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                    @Override
                    public String toString(LocalTime value) {
                        return formatter.format(value);
                    }

                    @Override
                    public LocalTime fromString(String text) {
                        return LocalTime.parse(text, formatter);
                    }
                });
            }

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps));
            }
        };

        valueFactory.setValue(LocalTime.now());
        timePicker.setValueFactory(valueFactory);
    }

    private void initializeDatePicker(){
        datePicker.setValue(LocalDate.now());
    }
}