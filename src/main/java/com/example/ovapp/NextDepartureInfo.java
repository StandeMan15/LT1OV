package com.example.ovapp;

import java.time.LocalTime;

public class NextDepartureInfo {
    private String availableLine;
    private LocalTime nextDepartureTime;

    public NextDepartureInfo(String availableLine, LocalTime nextDepartureTime) {
        this.availableLine = availableLine;
        this.nextDepartureTime = nextDepartureTime;
    }

    public String getAvailableLine() {
        return availableLine;
    }

    public LocalTime getNextDepartureTime() {
        return nextDepartureTime;
    }

    @Override
    public String toString() {
        return "NextDepartureInfo{" +
                "availableLine='" + availableLine + '\'' +
                ", nextDepartureTime=" + nextDepartureTime +
                '}';
    }
}

