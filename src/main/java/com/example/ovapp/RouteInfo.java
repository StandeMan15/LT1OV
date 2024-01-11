package com.example.ovapp;

import java.time.LocalTime;

public class RouteInfo {
    private int totalDistance;
    private LocalTime totalTravelTime;

    public RouteInfo(int totalDistance, LocalTime totalTravelTime) {
        this.totalDistance = totalDistance;
        this.totalTravelTime = totalTravelTime;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public LocalTime getTotalTravelTime() {
        return totalTravelTime;
    }

    public void setTotalDistance(int distance) {
        totalDistance = distance;
    }

    public void setTotalTravelTime(LocalTime travelTime) {
         totalTravelTime = travelTime;
    }
}
