package com.example.ovapp;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.text.SimpleDateFormat;
import java.text.NumberFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PlannerController {
    Translator translator = new Translator();

    private final Map<String, List<StationInfo>> stationRoutes = new HashMap<>();
    private List<String> allstations;

    //ObservableList<String> stations = FXCollections.observableArrayList("Amsterdam", "Amersfoort", "Breda", "Enschede", "Schiphol", "Utrecht", "Zwolle");
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
    protected void SearchRoute() {
        String Departure = departureComboBox.getValue();
        String Arrival = arrivalComboBox.getValue();
        String Vehicle = vehicleSelectionComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        Integer hourTime = hourSpinner.getValue();
        Integer minuteTime = minuteSpinner.getValue();
        LocalTime travelTime = LocalTime.of(0, 0);
        Integer travelDistance = 0;

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(translator.translate("date_format"));

            routeOutText.setText(String.format(translator.translate("route_message"),
                    Vehicle, Departure, Arrival, selectedDate.format(dateFormatter),
                    String.format("%02d", hourTime), String.format("%02d", minuteTime), travelTime, travelDistance));
        } catch (NullPointerException e) {
            routeOutText.setText(translator.translate("empty_field"));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    @FXML
    protected void initialize() {
        System.out.println("Controller initialized.");
        translator.setLanguage("nl");
        Locale.setDefault(new Locale("nl"));

        initializeStationRoutes();
        initializeAllStations();
        initializeComboBoxes();
        initializeTimePicker();
        initializeDatePicker();
        initializeLanguageButtons();
        Timenow();
        updateUI();


    }

    public void changeLanguage(String language) {
        translator.setLanguage(language);
        Locale.setDefault(new Locale(language));
        updateUI();
    }

    private void updateUI() {
        searchButton.setText(translator.translate("button_searchroute"));
        departLabel.setText(translator.translate("depart_label"));
        arrivalLabel.setText(translator.translate("arrival_label"));
        timeDateLabel.setText(translator.translate("time_date_label"));
        transportLabel.setText(translator.translate("transport_label"));

        int selectedVehicleIndex = vehicleSelectionComboBox.getSelectionModel().getSelectedIndex();

        ObservableList<String> translatedVehicles = translateList(vehicles);
        vehicleSelectionComboBox.setItems(translatedVehicles);

        if (selectedVehicleIndex != -1) {
            vehicleSelectionComboBox.getSelectionModel().select(selectedVehicleIndex);
        }

        if (!routeOutText.getText().isEmpty()) SearchRoute();

        datePicker.setConverter(createDateConverter());
        datePicker.setValue(datePicker.getValue());
    }

    private void initializeStationRoutes() {
        List<StationInfo> intercityLine1 = Arrays.asList(
                new StationInfo("Den Haag Centraal", 0, 0),
                new StationInfo("Gouda", 25, 30),
                new StationInfo("Utrecht Centraal", 40, 50),
                new StationInfo("Amersfoort Centraal", 60, 80),
                new StationInfo("Apeldoorn", 90, 120),
                new StationInfo("Deventer", 120, 150),
                new StationInfo("Almelo", 140, 180),
                new StationInfo("Hengelo", 160, 210),
                new StationInfo("Enschede", 180, 240)
        );

        List<StationInfo> intercityLine2 = Arrays.asList(
                new StationInfo("Maastricht", 0, 0),
                new StationInfo("Sittard", 25, 30),
                new StationInfo("Roermond", 50, 60),
                new StationInfo("Weert", 75, 90),
                new StationInfo("Eindhoven Centraal", 100, 120),
                new StationInfo("'S-Hertogenbosch", 120, 150),
                new StationInfo("Utrecht Centraal", 140, 180),
                new StationInfo("Amsterdam Bijlmer Arena", 160, 210),
                new StationInfo("Amsterdam Amstel", 180, 240),
                new StationInfo("Amsterdam Centraal", 200, 270)
        );
        stationRoutes.put("Intercity Line 1", intercityLine1);
        stationRoutes.put("Intercity Line 2", intercityLine2);
    }

    private RouteInfo calculateRouteInfo(String departure, String arrival, String line) {
        List<StationInfo> stations = stationRoutes.get(line);

        if (stations != null) {
            double totalDistance = 0;
            int totalTravelTime = 0;

            boolean foundDeparture = false;

            for (StationInfo station : stations) {
                if (foundDeparture) {
                    totalDistance += station.getDistance();
                    totalTravelTime += station.getTravelTime();
                    if (station.getName().equals(arrival)) {
                        break;
                    }
                } else if (station.getName().equals(departure)) {
                    foundDeparture = true;
                }
            }

            return new RouteInfo(totalDistance, totalTravelTime);
        }

        return null;
    }

    private void initializeLanguageButtons(){
        languageNLButton.setOnAction(event -> changeLanguage("nl"));
        languageENButton.setOnAction(event -> changeLanguage("en"));
    }

    private void initializeAllStations() {
        List<String> intercityLine1Stations = getStationsForLine("Intercity Line 1");
        List<String> intercityLine2Stations = getStationsForLine("Intercity Line 2");

        // Combine stations of both lines
        allstations = Stream.concat(intercityLine1Stations.stream(), intercityLine2Stations.stream())
                .distinct()
                .collect(Collectors.toList());

        System.out.println("Intercity Line 1 Stations: " + intercityLine1Stations);
        System.out.println("Intercity Line 2 Stations: " + intercityLine2Stations);
        System.out.println("All Stations: " + allstations);
    }

    private void initializeComboBoxes() {

        departureComboBox.setItems(FXCollections.observableArrayList(allstations));
        arrivalComboBox.setItems(FXCollections.observableArrayList(allstations));



        // Set a default selection (optional)
        departureComboBox.setValue(allstations.get(0));
        arrivalComboBox.setValue(allstations.get(1));
        vehicleSelectionComboBox.setValue(vehicles.get(0));

        departureComboBox.setVisibleRowCount(4);
        arrivalComboBox.setVisibleRowCount(4);
        vehicleSelectionComboBox.setVisibleRowCount(3);

        // Add an event listener to departureComboBox to filter arrival options
        departureComboBox.setOnAction(event -> updateArrivalOptions());
    }

    private List<String> getStationsForLine(String line) {
        List<String> stations = new ArrayList<>();
        List<StationInfo> stationInfoList = stationRoutes.get(line);

        System.out.println(stationInfoList);

        if (stationInfoList != null) {
            stations = stationInfoList.stream().map(StationInfo::getName).collect(Collectors.toList());
        }

        return stations;
    }

    private ObservableList<String> translateList(ObservableList<String> list) {
        ObservableList<String> translatedList = FXCollections.observableArrayList();
        for (String item : list) {
            translatedList.add(translator.translate(item));
        }
        return translatedList;
    }

    private void updateArrivalOptions() {
        String selectedOption = departureComboBox.getValue();

        FilteredList<String> filteredArrivalOptions = new FilteredList<>(FXCollections.observableArrayList(allstations));

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
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(translator.translate("date_format"));

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
            while(true){
                try{
                    final String timenow = sdf.format(new Date());
                    Platform.runLater(() -> time.setText(timenow));

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println(e);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        thread.start();
    }

    @FXML

    private void showKeyboardInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(translator.translate("keyboard_alert_title"));
        alert.setHeaderText(null);

        Label contentLabel = new Label(translator.translate("keyboard_alert_content"));
        contentLabel.setStyle("-fx-font-size: 21px;");
        alert.getDialogPane().setContent(contentLabel);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.initModality(Modality.APPLICATION_MODAL);

        alert.showAndWait();
    }
}