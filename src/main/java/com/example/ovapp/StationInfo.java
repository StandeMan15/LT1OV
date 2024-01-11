package com.example.ovapp;

public class StationInfo {
    private final String name;
    private final int distance;
    private final int travelTime;

    public StationInfo(String name, int distance, int travelTime) {
        this.name = name;
        this.distance = distance;
        this.travelTime = travelTime;
    }

    public String getName() {
        return name;
    }

    public int getDistance() {
        return distance;
    }

    public int getTravelTime() {
        return travelTime;
    }
}
