package com.example.ovapp;

import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class HelloController {

    private String Departure;
    private String Arrival;


    @FXML
    private ChoiceBox<String> choiceBoxArrival;
    @FXML
    private Label routeOutText;
    @FXML
    private ChoiceBox<String> choiceBoxDeparture;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Spinner<Double> timePicker;

    @FXML
    protected void SearchRoute() {
        Departure = choiceBoxDeparture.getValue();
        Arrival = choiceBoxArrival.getValue();
        LocalDate selectedDate = datePicker.getValue();
        Double selectedTime = timePicker.getValue();

        try{
            routeOutText.setText(String.format("Dit is de route van %s naar %s op %s om %.2f uur",
                    Departure, Arrival, selectedDate.toString(), selectedTime));
        } catch(NullPointerException e){
            routeOutText.setText(" Vul A.U.B. alle velden in.");
        }
    }

    @FXML
    protected void initialize() {
        initializeChoiceBox();
        initializeTimePicker();
    }

    private void initializeChoiceBox() {
        ObservableList<String> stations = FXCollections.observableArrayList("Amsterdam", "Amersfoort", "Enschede");

        choiceBoxDeparture.setItems(stations);
        choiceBoxArrival.setItems(stations);

        // Set a default selection (optional)
        choiceBoxDeparture.setValue(stations.get(0));
        choiceBoxArrival.setValue(stations.get(0));

    }
    private void initializeTimePicker() {
        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 23.55, 12.0, 0.05);

        valueFactory.setConverter(new StringConverter<>() {
            private final DecimalFormat decimalFormat = new DecimalFormat("00.00");

            @Override
            public String toString(Double value) {
                return decimalFormat.format(value);
            }

            @Override
            public Double fromString(String text) {
                return Double.parseDouble(text);
            }
        });

        timePicker.setValueFactory(valueFactory);

        timePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
        });
    }
}