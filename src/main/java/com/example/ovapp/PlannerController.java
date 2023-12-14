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
import java.text.NumberFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


public class PlannerController {

    ObservableList<String> stations = FXCollections.observableArrayList("Amsterdam", "Amersfoort", "Breda", "Enschede", "Schiphol", "Utrecht", "Zwolle");
    ObservableList<String> vehicles = FXCollections.observableArrayList("bus", "train");

    @FXML
    private ComboBox<String> arrivalComboBox;
    @FXML
    private ComboBox<String> departureComboBox;
    @FXML
    private ComboBox<String> vehicleSelectionComboBox;
    @FXML
    private Button searchButton;
    @FXML
    private Button languageNLButton;
    @FXML
    private Button languageENButton;
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
    private Spinner<Integer> hourSpinner;
    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private volatile boolean stop = false;

    @FXML
    protected void SearchRoute() {
        String Departure = departureComboBox.getValue();
        String Arrival = arrivalComboBox.getValue();
        String Vehicle = vehicleSelectionComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        Integer hourTime = hourSpinner.getValue();
        Integer minuteTime = minuteSpinner.getValue();

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Translator.translate("date_format"));

            routeOutText.setText(String.format(Translator.translate("route_message"),
                    Vehicle, Departure, Arrival, selectedDate.format(dateFormatter),
                    String.format("%02d", hourTime), String.format("%02d", minuteTime)));
        } catch (NullPointerException e) {
            routeOutText.setText(Translator.translate("empty_field"));
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
        Translator.setLanguage("nl");
        Locale.setDefault(new Locale("nl"));
        initializeComboBoxes();
        initializeTimePicker();
        initializeDatePicker();
        initializeLanguageButtons();
        Timenow();
        updateUI();
    }

    public void changeLanguage(String language) {
        Translator.setLanguage(language);
        Locale.setDefault(new Locale(language));
        updateUI();
    }

    private void updateUI() {
        searchButton.setText(Translator.translate("button_searchroute"));
        departLabel.setText(Translator.translate("depart_label"));
        arrivalLabel.setText(Translator.translate("arrival_label"));
        timeDateLabel.setText(Translator.translate("time_date_label"));
        transportLabel.setText(Translator.translate("transport_label"));

        if (!routeOutText.getText().isEmpty()) SearchRoute();

        datePicker.setConverter(createDateConverter());
        datePicker.setValue(datePicker.getValue());

        vehicleSelectionComboBox.setItems(translateList(vehicles));
    }

    private void initializeLanguageButtons(){
        languageNLButton.setOnAction(event -> changeLanguage("nl"));
        languageENButton.setOnAction(event -> changeLanguage("en"));
    }

    private void initializeComboBoxes() {
        departureComboBox.setItems(stations);
        arrivalComboBox.setItems(stations);

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

    private ObservableList<String> translateList(ObservableList<String> list) {
        ObservableList<String> translatedList = FXCollections.observableArrayList();
        for (String item : list) {
            translatedList.add(Translator.translate(item));
        }
        return translatedList;
    }

    private void updateArrivalOptions() {
        String selectedOption = departureComboBox.getValue();

        FilteredList<String> filteredArrivalOptions = new FilteredList<>(stations);

        filteredArrivalOptions.setPredicate(option -> !option.equals(selectedOption));
        arrivalComboBox.setItems(filteredArrivalOptions);

        arrivalComboBox.setValue(filteredArrivalOptions.isEmpty() ? null : filteredArrivalOptions.get(0));
    }

    private void initializeTimePicker() {
        LocalTime currentTime = LocalTime.now();
        NumberFormat twoDigitFormat = NumberFormat.getIntegerInstance();
        twoDigitFormat.setMinimumIntegerDigits(2);

        initializeSpinner(hourSpinner, 23, currentTime.getHour(), twoDigitFormat);
        initializeSpinner(minuteSpinner, 59, currentTime.getMinute(), twoDigitFormat);
    }

    private void initializeSpinner(Spinner<Integer> spinner, int max, int initialValue, NumberFormat format) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, max, initialValue);

        valueFactory.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return format.format(value);
            }

            @Override
            public Integer fromString(String string) {
                return Integer.parseInt(string);
            }
        });

        spinner.setValueFactory(valueFactory);
    }

    private void initializeDatePicker(){
        datePicker.setValue(LocalDate.now());
    }

    private StringConverter<LocalDate> createDateConverter() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Translator.translate("date_format"));

        return new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, dateFormatter) : null;
            }
        };
    }

    @FXML
    private void Timenow(){
        Thread thread = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            while(!stop){
                try{
                    Thread.sleep(1);
                }catch(Exception e){
                    System.out.println(e);
                }
                final String timenow = sdf.format(new Date());
                Platform.runLater(() -> time.setText(timenow));
            }
        });
        thread.start();
    }
}