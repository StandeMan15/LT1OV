package com.example.ovapp;

import java.time.LocalTime;

/**
 * Represents information about a station, including its name, distance, travel time, and associated line.
 */
public class StationInfo {
    private String name;
    private String line;
    private int distance;
    private LocalTime travelTime;

    /**
     * Constructs a StationInfo object with the specified name, distance, travel time, and line.
     *
     * @param name       The name of the station.
     * @param distance   The distance of the station from the starting point.
     * @param travelTime The travel time required to reach the station.
     * @param line       The line associated with the station.
     */
    public StationInfo(String name, int distance, LocalTime travelTime, String line) {
        this.name = name;
        this.distance = distance;
        this.travelTime = travelTime;
        this.line = line;
    }

    public String getLine() {
        return line;
    }

    public String getName() {
        return name;
    }

    public int getDistance() {
        return distance;
    }

    public LocalTime getTravelTime() {
        return travelTime;
    }
}
