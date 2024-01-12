package com.example.ovapp;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages station information, including routes, train stations, and bus stations.
 */
public class StationManager {

    private final Map<String, List<StationInfo>> stationRoutes = new HashMap<>();
    private List<String> trainStations;
    private List<String> busStations;

    public StationManager() {
        initializeStationRoutes();
        initializeBusStations();
        initializeTrainStations();
    }

    /**
     * Initializes station from all routes with hardcoded data.
     */
    private void initializeStationRoutes() {
        List<StationInfo> intercityLine1 = Arrays.asList(
                new StationInfo("Den Haag Centraal", 0, LocalTime.of(0, 0), ""),
                new StationInfo("Gouda", 29, LocalTime.of(0, 18), ""),
                new StationInfo("Utrecht Centraal", 36, LocalTime.of(0, 18), ""),
                new StationInfo("Amersfoort Centraal", 24, LocalTime.of(0, 13), ""),
                new StationInfo("Apeldoorn", 45, LocalTime.of(0, 24), ""),
                new StationInfo("Deventer", 16, LocalTime.of(0, 10), ""),
                new StationInfo("Almelo", 40, LocalTime.of(0, 23), ""),
                new StationInfo("Hengelo", 16, LocalTime.of(0, 11), ""),
                new StationInfo("Enschede", 9, LocalTime.of(0, 7), "")
        );

        List<StationInfo> intercityLine2 = Arrays.asList(
                new StationInfo("Maastricht", 0, LocalTime.of(0, 0), ""),
                new StationInfo("Sittard", 24, LocalTime.of(0, 15), ""),
                new StationInfo("Roermond", 26, LocalTime.of(0, 14), ""),
                new StationInfo("Weert", 25, LocalTime.of(0, 14), ""),
                new StationInfo("Eindhoven Centraal", 30, LocalTime.of(0, 16), ""),
                new StationInfo("'S-Hertogenbosch", 33, LocalTime.of(0, 19), ""),
                new StationInfo("Utrecht Centraal", 51, LocalTime.of(0, 29), ""),
                new StationInfo("Amsterdam Amstel", 35, LocalTime.of(0, 18), ""),
                new StationInfo("Amsterdam Centraal", 6, LocalTime.of(0, 8), "")
        );
        List<StationInfo> busLine1 = Arrays.asList(
                new StationInfo("Rotterdam", 0, LocalTime.of(0, 0), ""),
                new StationInfo("Delft", 15, LocalTime.of(0, 12), ""),
                new StationInfo("The Hague", 18, LocalTime.of(0, 15), ""),
                new StationInfo("Leiden", 22, LocalTime.of(0, 18), "")
        );

        List<StationInfo> busLine2 = Arrays.asList(
                new StationInfo("Utrecht", 0, LocalTime.of(0, 0), ""),
                new StationInfo("Zeist", 12, LocalTime.of(0, 10), ""),
                new StationInfo("Amersfoort", 25, LocalTime.of(0, 20), ""),
                new StationInfo("Hilversum", 30, LocalTime.of(0, 25), "")
        );

        stationRoutes.put("Intercity Line 1", intercityLine1);
        stationRoutes.put("Intercity Line 2", intercityLine2);
        stationRoutes.put("Bus Line 1", busLine1);
        stationRoutes.put("Bus Line 2", busLine2);
    }

    /**
     * Initializes bus stations based on the available bus lines.
     */
    private void initializeBusStations() {
        List<String> busLine1Stations = getStationsForLine("Bus Line 1");
        List<String> busLine2Stations = getStationsForLine("Bus Line 2");

        busStations = Stream.concat(busLine1Stations.stream(), busLine2Stations.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Initializes train stations based on the available intercity lines.
     */
    private void initializeTrainStations() {
        List<String> intercityLine1Stations = getStationsForLine("Intercity Line 1");
        List<String> intercityLine2Stations = getStationsForLine("Intercity Line 2");

        trainStations = Stream.concat(intercityLine1Stations.stream(), intercityLine2Stations.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the list of stations for a given line.
     *
     * @param line The line for which stations need to be retrieved.
     * @return List of stations for the specified line.
     */
    public List<String> getStationsForLine(String line) {
        List<String> stations = new ArrayList<>();
        List<StationInfo> stationInfoList = stationRoutes.get(line);

        if (stationInfoList != null) {
            stations = stationInfoList.stream().map(StationInfo::getName).collect(Collectors.toList());
        }

        return stations;
    }

    public List<String> getTrainStations() {
        return trainStations;
    }

    public List<String> getBusStations() {
        return busStations;
    }

    public Map<String, List<StationInfo>> getStationRoutes() {
        return stationRoutes;
    }
}
