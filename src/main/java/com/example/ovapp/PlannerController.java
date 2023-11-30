package com.example.ovapp;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class PlannerController {

    ObservableList<String> stations = FXCollections.observableArrayList("Amsterdam", "Amersfoort", "Breda", "Enschede", "Schiphol", "Utrecht", "Zwolle");
    ObservableList<String> vehicles = FXCollections.observableArrayList("bus", "trein");

    @FXML
    private ComboBox<String> arrivalComboBox;
    @FXML
    private ComboBox<String> departureComboBox;
    @FXML
    private ComboBox<String> vehicleSelectionComboBox;
    @FXML
    private Button searchButton;
    @FXML
    private Label routeOutText;
    @FXML
    private Label departLabel;
    @FXML
    private Label arrivalLabel;
    @FXML
    private Label timeDateLabel;
    @FXML
    private Label transportLabel;
    @FXML
    private Label time;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Spinner<LocalTime> timePicker;
    @FXML
    private volatile boolean stop = false;

    @FXML
    protected void SearchRoute() {
        String Departure = departureComboBox.getValue();
        String Arrival = arrivalComboBox.getValue();
        String Vehicle = vehicleSelectionComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        LocalTime selectedTime = timePicker.getValue();

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            routeOutText.setText(String.format("Dit is de %s route:\nVan: %s\nNaar: %s\nOp: %s\nOm: %s uur",
                    Vehicle, Departure, Arrival, selectedDate.format(dateFormatter), selectedTime.format(timeFormatter)));
        } catch (NullPointerException e) {
            routeOutText.setText(" Vul A.U.B. alle velden in.");
        }
    }

    @FXML
    private void Close_clicked(MouseEvent event){
        stop = true;
        javafx.application.Platform.exit();
    }

    @FXML
    protected void initialize() {
        System.out.println("Controller initialized.");
        Translator.setLanguage("en");
        updateUI();
        initializeComboBoxes();
        initializeTimePicker();
        initializeDatePicker();
        Timenow();
    }

    public void changeLanguage(String language) {
        Translator.setLanguage(language);
        updateUI();
    }

    private void updateUI() {
        searchButton.setText(Translator.translate("button_searchroute"));
        departLabel.setText(Translator.translate("depart_label"));
        arrivalLabel.setText(Translator.translate("arrival_label"));
        timeDateLabel.setText(Translator.translate("time_date_label"));
        transportLabel.setText(Translator.translate("transport_label"));

    }

    private void initializeComboBoxes() {
        departureComboBox.setItems(stations);
        arrivalComboBox.setItems(stations);
        vehicleSelectionComboBox.setItems(vehicles);

        // Set a default selection (optional)
        departureComboBox.setValue(stations.get(0));
        arrivalComboBox.setValue(stations.get(1));
        vehicleSelectionComboBox.setValue(vehicles.get(0));


        departureComboBox.setVisibleRowCount(4);
        arrivalComboBox.setVisibleRowCount(4);
        vehicleSelectionComboBox.setVisibleRowCount(3);

        // Add an event listener to departureComboBox to filter arrival options
        departureComboBox.setOnAction(event -> updateArrivalOptions());
    }

    private void updateArrivalOptions() {
        String selectedOption = departureComboBox.getValue();

        FilteredList<String> filteredArrivalOptions = new FilteredList<>(stations);

        filteredArrivalOptions.setPredicate(option -> !option.equals(selectedOption));
        arrivalComboBox.setItems(filteredArrivalOptions);

        arrivalComboBox.setValue(filteredArrivalOptions.isEmpty() ? null : filteredArrivalOptions.get(0));
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

    @FXML
    private void Timenow(){
        Thread thread = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            while(!stop){
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    System.out.println(e);
                }
                final String timenow = sdf.format(new Date());
                Platform.runLater(() -> {
                    time.setText(timenow);
                });
            }
        });
        thread.start();
    }
}