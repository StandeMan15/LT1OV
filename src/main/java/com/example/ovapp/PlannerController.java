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


/**
 * Controller class for the OV Planner application.
 * Handles user interactions and updates the UI.
 */
public class PlannerController {
    Translator translator = new Translator();
    RouteInfo routeInfo = new RouteInfo(0,LocalTime.of(0, 0));
    private final StationManager stationManager = new StationManager();
    private final Map<String, List<StationInfo>> stationRoutes = stationManager.getStationRoutes();

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
    private Label routeOutText1;

    /**
     * Searches for a route based on user inputs and displays the result.
     */
    @FXML
    protected void SearchRoute() {
        String Departure = departureComboBox.getValue();
        String Arrival = arrivalComboBox.getValue();
        String Vehicle = vehicleSelectionComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        Integer hourTime = hourSpinner.getValue();
        Integer minuteTime = minuteSpinner.getValue();

        LocalTime selectedTime = LocalTime.of(hourTime, minuteTime);
        LocalTime DepartureTime= null;

        String selectedLine = stationManager.getLineForStation(Departure);
        List<DepartureInfo> departureInfos = stationManager.getDepartureTimesForStation(Departure, selectedLine, selectedTime);

        System.out.println("========================================");
        for (DepartureInfo departureInfo : departureInfos) {
            System.out.println("Station: " + departureInfo.getStation());
            System.out.println("Vertrektijd: " + departureInfo.getDepartureTime());
        }

        for (DepartureInfo departureInfo : departureInfos) {
            if (departureInfo.getStation().equals(Departure)) {
                DepartureTime = departureInfo.getDepartureTime();
                break;
            }
        }

        calculateRouteInfo(Departure, Arrival);
        LocalTime travelTime = routeInfo.getTotalTravelTime();
        Integer travelDistance = routeInfo.getTotalDistance();

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(translator.translate("date_format"));

            routeOutText.setText(String.format(translator.translate("route_message"),
                    Vehicle, Departure, Arrival, selectedDate.format(dateFormatter),
                    DepartureTime,
                    travelTime, travelDistance));
        } catch (NullPointerException e) {
            routeOutText.setText(translator.translate("empty_field"));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private List<String> getStopsAlongRoute(String departure, String arrival, List<StationInfo> stations) {
        List<String> stops = new ArrayList<>();

        boolean foundDeparture = false;

        for (StationInfo station : stations) {
            if (foundDeparture) {
                stops.add(station.getName());
                if (station.getName().equals(arrival)) {
                    break;
                }
            } else if (station.getName().equals(departure)) {
                foundDeparture = true;
                stops.add(station.getName());
            }
        }

        return stops;
    }


    /**
     * Initializes the controller when the FXML file is loaded.
     */
    @FXML
    protected void initialize() {
        System.out.println("Controller initialized.");
        translator.setLanguage("nl");
        Locale.setDefault(new Locale("nl"));

        initializeBusStations();
        initializeComboBoxes();
        initializeTimePicker();
        initializeDatePicker();
        initializeLanguageButtons();
        Timenow();
        updateUI();
    }

    /**
     * Changes the language of the application.
     *
     * @param language The desired language code (e.g., "nl" or "en").
     */
    public void changeLanguage(String language) {
        translator.setLanguage(language);
        Locale.setDefault(new Locale(language));
        updateUI();
    }

    /**
     * Updates the UI elements with translated text based on the current language.
     */
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

    /**
     * Calculates route information based on the selected departure and arrival stations.
     *
     * @param departure The selected departure station.
     * @param arrival   The selected arrival station.
     */

    private void calculateRouteInfo(String departure, String arrival) {
        for (List<StationInfo> stations : stationRoutes.values()) {
            int totalDistanceTopToBottom = 0;
            LocalTime totalTravelTimeTopToBottom = LocalTime.of(0, 0);

            int totalDistanceBottomToTop = 0;
            LocalTime totalTravelTimeBottomToTop = LocalTime.of(0, 0);

            boolean foundDepartureTopToBottom = false;
            boolean foundDepartureBottomToTop = false;

            // Top to Bottom
            for (StationInfo station : stations) {
                if (foundDepartureTopToBottom) {
                    totalDistanceTopToBottom += station.getDistance();
                    totalTravelTimeTopToBottom = totalTravelTimeTopToBottom.plusHours(station.getTravelTime().getHour())
                            .plusMinutes(station.getTravelTime().getMinute());
                    if (station.getName().equals(arrival)) {
                        routeInfo.setTotalDistance(totalDistanceTopToBottom);
                        routeInfo.setTotalTravelTime(totalTravelTimeTopToBottom);
                        return;
                    }
                } else if (station.getName().equals(departure)) {
                    foundDepartureTopToBottom = true;
                }
            }

            // Bottom to Top
            ListIterator<StationInfo> iterator = stations.listIterator(stations.size());
            while (iterator.hasPrevious()) {
                StationInfo station = iterator.previous();

                if (foundDepartureBottomToTop) {
                    totalDistanceBottomToTop += station.getDistance();
                    totalTravelTimeBottomToTop = totalTravelTimeBottomToTop.plusHours(station.getTravelTime().getHour())
                            .plusMinutes(station.getTravelTime().getMinute());
                    if (station.getName().equals(arrival)) {
                        routeInfo.setTotalDistance(totalDistanceBottomToTop);
                        routeInfo.setTotalTravelTime(totalTravelTimeBottomToTop);
                        return;
                    }
                } else if (station.getName().equals(departure)) {
                    foundDepartureBottomToTop = true;
                }
            }
        }
    }

    /**
     * Initializes language change buttons.
     */
    private void initializeLanguageButtons(){
        languageNLButton.setOnAction(event -> changeLanguage("nl"));
        languageENButton.setOnAction(event -> changeLanguage("en"));
    }

    /**
     * Initializes ComboBoxes for vehicle selection and station options.
     */
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

    /**
     * Initializes ComboBoxes with bus stations.
     */
    private void initializeBusStations() {
        List<String> busStations = stationManager.getBusStations();

        departureComboBox.setItems(FXCollections.observableArrayList(busStations));
        arrivalComboBox.setItems(FXCollections.observableArrayList(busStations));

        // Set default values
        departureComboBox.setValue(busStations.get(0));
        arrivalComboBox.setValue(busStations.get(1));
    }

    /**
     * Initializes ComboBoxes with train stations.
     */
    private void initializeTrainStations() {
        List<String> trainStations = stationManager.getTrainStations();

        departureComboBox.setItems(FXCollections.observableArrayList(trainStations));
        arrivalComboBox.setItems(FXCollections.observableArrayList(trainStations));

        departureComboBox.setValue(trainStations.get(0));
        arrivalComboBox.setValue(trainStations.get(1));
    }

    /**
     * Translates the items in the given list.
     *
     * @param list The list to be translated.
     * @return Translated list.
     */
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

    /**
     * Initializes the time picker with the current time.
     */
    private void initializeDatePicker(){
        datePicker.setValue(LocalDate.now());
    }

    /**
     * Creates a StringConverter for the DatePicker to handle date formatting.
     *
     * @return A StringConverter for LocalDate.
     */
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

    /**
     * Updates the time label in real-time.
     */
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

    /**
     * Displays an informational alert about keyboard shortcuts.
     */
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