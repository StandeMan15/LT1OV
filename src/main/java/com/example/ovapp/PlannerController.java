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
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PlannerController {
    Translator translator = new Translator();
    RouteInfo routeInfo = new RouteInfo(0,LocalTime.of(0, 0));
    private final StationManager stationManager = new StationManager();
    private final Map<String, List<StationInfo>> stationRoutes = new HashMap<>();

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

        calculateRouteInfo(Departure,Arrival);
        LocalTime travelTime = routeInfo.getTotalTravelTime();
        Integer travelDistance = routeInfo.getTotalDistance();

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
        initializeBusStations();
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
                new StationInfo("Den Haag Centraal", 0, LocalTime.of(0, 0),""),
                new StationInfo("Gouda", 29, LocalTime.of(0, 18),""),
                new StationInfo("Utrecht Centraal", 36, LocalTime.of(0, 18),""),
                new StationInfo("Amersfoort Centraal", 24, LocalTime.of(0, 13),""),
                new StationInfo("Apeldoorn", 45, LocalTime.of(0, 24),""),
                new StationInfo("Deventer", 16, LocalTime.of(0, 10),""),
                new StationInfo("Almelo", 40, LocalTime.of(0, 23),""),
                new StationInfo("Hengelo", 16, LocalTime.of(0, 11),""),
                new StationInfo("Enschede", 9, LocalTime.of(0, 7),"")
        );

        List<StationInfo> intercityLine2 = Arrays.asList(
                new StationInfo("Maastricht", 0, LocalTime.of(0, 0),""),
                new StationInfo("Sittard", 24, LocalTime.of(0, 15),""),
                new StationInfo("Roermond", 26, LocalTime.of(0, 14),""),
                new StationInfo("Weert", 25, LocalTime.of(0, 14),""),
                new StationInfo("Eindhoven Centraal", 30, LocalTime.of(0, 16),""),
                new StationInfo("'S-Hertogenbosch", 33, LocalTime.of(0, 19),""),
                new StationInfo("Utrecht Centraal", 51, LocalTime.of(0, 29),""),
                new StationInfo("Amsterdam Amstel", 35, LocalTime.of(0, 18),""),
                new StationInfo("Amsterdam Centraal", 6, LocalTime.of(0, 8),"")
        );
        List<StationInfo> busLine1 = Arrays.asList(
                new StationInfo("Rotterdam", 0, LocalTime.of(0, 0),""),
                new StationInfo("Delft", 15, LocalTime.of(0, 12),""),
                new StationInfo("The Hague", 18, LocalTime.of(0, 15),""),
                new StationInfo("Leiden", 22, LocalTime.of(0, 18),"")
        );

        List<StationInfo> busLine2 = Arrays.asList(
                new StationInfo("Utrecht", 0, LocalTime.of(0, 0),""),
                new StationInfo("Zeist", 12, LocalTime.of(0, 10),""),
                new StationInfo("Amersfoort", 25, LocalTime.of(0, 20),""),
                new StationInfo("Hilversum", 30, LocalTime.of(0, 25),"")
        );

        stationRoutes.put("Intercity Line 1", intercityLine1);
        stationRoutes.put("Intercity Line 2", intercityLine2);
        stationRoutes.put("Bus Line 1", busLine1);
        stationRoutes.put("Bus Line 2", busLine2);
    }

    private void calculateRouteInfo(String departure, String arrival) {
        for (List<StationInfo> stations : stationRoutes.values()) {
            int totalDistance = 0;
            LocalTime totalTravelTime = LocalTime.of(0, 0);

            boolean foundDeparture = false;

            for (StationInfo station : stations) {
                if (foundDeparture) {
                    totalDistance += station.getDistance();
                    totalTravelTime = totalTravelTime.plusHours(station.getTravelTime().getHour())
                            .plusMinutes(station.getTravelTime().getMinute());
                    if (station.getName().equals(arrival)) {
                        // Update RouteInfo directly
                        routeInfo.setTotalDistance(totalDistance);
                        routeInfo.setTotalTravelTime(totalTravelTime);
                        return;  // Onderbreek de loop wanneer het aankomststation is bereikt
                    }
                } else if (station.getName().equals(departure)) {
                    foundDeparture = true;
                }
            }
        }
    }

    private void initializeLanguageButtons(){
        languageNLButton.setOnAction(event -> changeLanguage("nl"));
        languageENButton.setOnAction(event -> changeLanguage("en"));
    }

    private void initializeComboBoxes() {

        vehicleSelectionComboBox.setItems(vehicles);
        vehicleSelectionComboBox.setValue(vehicles.get(0));

        departureComboBox.setVisibleRowCount(4);
        arrivalComboBox.setVisibleRowCount(4);
        vehicleSelectionComboBox.setVisibleRowCount(3);

        //departureComboBox.setOnAction(event -> updateArrivalOptions());

        vehicleSelectionComboBox.setOnAction(event -> {
            int selectedVehicleIndex = vehicleSelectionComboBox.getSelectionModel().getSelectedIndex();

            if (selectedVehicleIndex == 0) {  // Index 0 is "bus"
                initializeBusStations();
            } else if (selectedVehicleIndex == 1) {  // Index 1 is "train"
                initializeTrainStations();
            }
        });
    }

    private void initializeBusStations() {
        List<String> busStations = stationManager.getBusStations();

        departureComboBox.setItems(FXCollections.observableArrayList(busStations));
        arrivalComboBox.setItems(FXCollections.observableArrayList(busStations));

        // Set default values
        departureComboBox.setValue(busStations.get(0));
        arrivalComboBox.setValue(busStations.get(1));
    }

    private void initializeTrainStations() {
        List<String> trainStations = stationManager.getTrainStations();

        departureComboBox.setItems(FXCollections.observableArrayList(trainStations));
        arrivalComboBox.setItems(FXCollections.observableArrayList(trainStations));

        departureComboBox.setValue(trainStations.get(0));
        arrivalComboBox.setValue(trainStations.get(1));
    }

    private ObservableList<String> translateList(ObservableList<String> list) {
        ObservableList<String> translatedList = FXCollections.observableArrayList();
        for (String item : list) {
            translatedList.add(translator.translate(item));
        }
        return translatedList;
    }

//    private void updateArrivalOptions() {
//        String selectedOption = departureComboBox.getValue();
//
//        FilteredList<String> filteredArrivalOptions = new FilteredList<>(FXCollections.observableArrayList(trainStations));
//
//        filteredArrivalOptions.setPredicate(option -> !option.equals(selectedOption));
//        arrivalComboBox.setItems(filteredArrivalOptions);
//
//        arrivalComboBox.setValue(filteredArrivalOptions.isEmpty() ? null : filteredArrivalOptions.get(0));
//    }

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