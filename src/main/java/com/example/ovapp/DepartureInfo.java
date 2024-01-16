package com.example.ovapp;

import java.time.LocalTime;

public class DepartureInfo {
    private String station;
    private LocalTime departureTime;

    public DepartureInfo(String station, LocalTime departureTime) {
        this.station = station;
        this.departureTime = departureTime;
    }

    public String getStation() {
        return station;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }
}
