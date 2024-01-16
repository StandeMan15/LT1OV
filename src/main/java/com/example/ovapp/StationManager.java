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
                new StationInfo("Den Haag", 18, LocalTime.of(0, 15), ""),
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

    public List<DepartureInfo> getDepartureTimesForStation(String departureStation, String line, LocalTime selectedTime) {
        List<StationInfo> stations = stationRoutes.get(line);
        List<DepartureInfo> departureInfos = new ArrayList<>();
        boolean foundDeparture = false;

        LocalTime currentTime = getNextDepartureTime(line, selectedTime); // Use the next departure time

        for (StationInfo station : stations) {
            if (foundDeparture) {
                // Calculate the travel time from the previous station to the current station
                LocalTime travelTime = station.getTravelTime();

                // Calculate the departure time for the current station
                LocalTime nextDepartureTime = calculateDepartureTime(station, currentTime);

                departureInfos.add(new DepartureInfo(station.getName(), nextDepartureTime));
                currentTime = currentTime.plusHours(travelTime.getHour()).plusMinutes(travelTime.getMinute());

                if (station.getName().equals(departureStation)) {
                    break;
                }
            } else if (station.getName().equals(departureStation)) {
                foundDeparture = true;
            }
        }

        return departureInfos;
    }

    private LocalTime calculateDepartureTime(StationInfo station, LocalTime previousDepartureTime) {
        // Calculate departure time based on the previous departure time and travel time
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

    private void initializeLineDepartureTimes() {
        lineDepartureTimes.put("Intercity Line 1", Arrays.asList(LocalTime.of(11, 0), LocalTime.of(14, 0), LocalTime.of(20, 0)));
        lineDepartureTimes.put("Intercity Line 2", Arrays.asList(LocalTime.of(10, 30), LocalTime.of(15, 30), LocalTime.of(21, 30)));
        lineDepartureTimes.put("Bus Line 1", Arrays.asList(LocalTime.of(8, 0), LocalTime.of(12, 0), LocalTime.of(16, 0)));
        lineDepartureTimes.put("Bus Line 2", Arrays.asList(LocalTime.of(9, 30), LocalTime.of(13, 30), LocalTime.of(17, 30)));
    }


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
