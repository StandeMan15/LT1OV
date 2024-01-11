package com.example.ovapp;

import java.time.LocalTime;

public class StationInfo {
    private String name;
    private String line;
    private int distance;
    private LocalTime travelTime;

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
