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
    private final Map<String, List<LocalTime>> lineDepartureTimes = new HashMap<>();
    private List<String> trainStations;
    private List<String> busStations;

    public StationManager() {
        initializeStationRoutes();
        initializeBusStations();
        initializeTrainStations();
        initializeLineDepartureTimes();
    }

    /**
     * Initializes station from all routes with hardcoded data.
     */
    private void initializeStationRoutes() {
        List<StationInfo> intercityLine1 = Arrays.asList(
                new StationInfo("Den Haag Centraal", 29, LocalTime.of(0, 18), ""),
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
                new StationInfo("Maastricht", 24, LocalTime.of(0, 15), ""),
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
                new StationInfo("Eemsplein", 1, LocalTime.of(0, 5), ""),
                new StationInfo("Stadhuis", 1, LocalTime.of(0, 5), ""),
                new StationInfo("Centrum", 1, LocalTime.of(0, 3), ""),
                new StationInfo("t'Kip", 4, LocalTime.of(0, 10), "")
        );

        List<StationInfo> busLine2 = Arrays.asList(
                new StationInfo("Politie bureau", 3, LocalTime.of(0, 4), ""),
                new StationInfo("Wolweg", 3, LocalTime.of(0, 4), ""),
                new StationInfo("Prinselaan", 5, LocalTime.of(0, 9), ""),
                new StationInfo("Markt", 5, LocalTime.of(0, 7), "")
        );

        stationRoutes.put("Intercity Line 1", intercityLine1);
        stationRoutes.put("Intercity Line 2", intercityLine2);
        stationRoutes.put("Bus Line 1", busLine1);
        stationRoutes.put("Bus Line 2", busLine2);
    }

    private void initializeLineDepartureTimes() {
        lineDepartureTimes.put("Intercity Line 1", Arrays.asList(LocalTime.of(11, 0), LocalTime.of(14, 0), LocalTime.of(20, 0)));
        lineDepartureTimes.put("Intercity Line 2", Arrays.asList(LocalTime.of(10, 30), LocalTime.of(15, 30), LocalTime.of(21, 30)));
        lineDepartureTimes.put("Bus Line 1", Arrays.asList(LocalTime.of(8, 0), LocalTime.of(12, 0), LocalTime.of(16, 0)));
        lineDepartureTimes.put("Bus Line 2", Arrays.asList(LocalTime.of(9, 30), LocalTime.of(13, 30), LocalTime.of(17, 30)));
    }

    /**
     * Retrieves departure times for a specific station along a given line, considering selected time and arrival station.
     *
     * @param departureStation The name of the departure station.
     * @param line             The line on which the departure station is located.
     * @param selectedTime     The selected time for finding departure times.
     * @param arrivalStation   The name of the arrival station.
     * @return A list of DepartureInfo objects representing departure times and stations along the route.
     */
    public List<DepartureInfo> getDepartureTimesForStation(String departureStation, String line, LocalTime selectedTime, String arrivalStation) {
        List<StationInfo> stations = stationRoutes.get(line);
        List<DepartureInfo> departureInfos = new ArrayList<>();

        int startIndex = -1;
        int endIndex = -1;

        for (int i = 0; i < stations.size(); i++) {

            if (stations.get(i).getName().equals(departureStation)) {
                startIndex = i;
            }
            if (stations.get(i).getName().equals(arrivalStation)) {
                endIndex = i;
            }
        }

        if (startIndex != -1 && endIndex != -1) {
            LocalTime currentTime = getNextDepartureTime(line, selectedTime);

            if (startIndex <= endIndex) {
                // Traverse from top to bottom
                for (int i = startIndex; i <= endIndex; i++) {
                    StationInfo station = stations.get(i);
                    LocalTime travelTime = station.getTravelTime();
                    LocalTime nextDepartureTime = calculateDepartureTime(station, currentTime);

                    departureInfos.add(new DepartureInfo(station.getName(), nextDepartureTime));

                    currentTime = currentTime.plusHours(travelTime.getHour()).plusMinutes(travelTime.getMinute());
                }
            } else {
                // Traverse from bottom to top
                for (int i = startIndex; i >= endIndex; i--) {
                    StationInfo station = stations.get(i);
                    LocalTime travelTime = station.getTravelTime();
                    LocalTime nextDepartureTime = calculateDepartureTime(station, currentTime);

                    departureInfos.add(new DepartureInfo(station.getName(), nextDepartureTime));

                    currentTime = currentTime.plusHours(travelTime.getHour()).plusMinutes(travelTime.getMinute());
                }
            }
        } else {
            System.out.println("Invalid start or end index for stations.");
        }

        return departureInfos;
    }


    /**
     * Calculates the next departure time based on the travel time of the current station.
     *
     * @param station              The current station.
     * @param previousDepartureTime The time of the previous departure.
     * @return The calculated next departure time.
     */
    private LocalTime calculateDepartureTime(StationInfo station, LocalTime previousDepartureTime) {
        return previousDepartureTime.plusHours(station.getTravelTime().getHour())
                .plusMinutes(station.getTravelTime().getMinute());
    }

    /**
     * Initializes bus stations based on the available bus lines.
     */
    private void initializeBusStations() {
        busStations = getStationsForLine("Bus Line 1");
        busStations.addAll(getStationsForLine("Bus Line 2"));
    }

    /**
     * Initializes bus stations based on the available bus lines
     */
    private void initializeTrainStations() {
        trainStations = getStationsForLine("Intercity Line 1");
        trainStations.addAll(getStationsForLine("Intercity Line 2"));
    }

    private List<String> getStationsForLine(String line) {
        return stationRoutes.getOrDefault(line, Collections.emptyList())
                .stream()
                .map(StationInfo::getName)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the next departure time after the selected time for a specific line.
     *
     * @param line       The line for which departure times are considered.
     * @param SelectTime The selected time for finding the next departure time.
     * @return The next departure time after the selected time, or the first departure time of the next day if none is found.
     */
    private LocalTime getNextDepartureTime(String line, LocalTime SelectTime) {
        List<LocalTime> departureTimes = lineDepartureTimes.getOrDefault(line, Collections.emptyList());

        // Zoek de eerstvolgende vertrektijd na het huidige tijdstip
        for (LocalTime departureTime : departureTimes) {
            if (SelectTime.isBefore(departureTime)) {
                return departureTime;
            }
        }

        // Als er geen vertrektijd na het huidige tijdstip is, neem dan de eerste vertrektijd van morgen
        return departureTimes.isEmpty() ? null : departureTimes.get(0);
    }

    /**
     * Retrieves the line associated with a given departure station.
     *
     * @param departureStation The name of the departure station.
     * @return The line associated with the departure station, or "No Line found" if not found.
     */
    String getLineForStation(String departureStation) {
        for (Map.Entry<String, List<StationInfo>> entry : stationRoutes.entrySet()) {
            List<String> stationNames = entry.getValue().stream()
                    .map(StationInfo::getName)
                    .collect(Collectors.toList());

            if (stationNames.contains(departureStation)) {
                return entry.getKey();
            }
        }
        return "No Line found";
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
