package com.example.ovapp;

import java.time.LocalTime;

/**
 * Represents information about a travel route, including total distance and travel time.
 */
public class RouteInfo {
    private int totalDistance;
    private LocalTime totalTravelTime;

    /**
     * Constructs a RouteInfo object with the specified total distance and travel time.
     *
     * @param totalDistance   The total distance of the travel route.
     * @param totalTravelTime The total travel time for the travel route.
     */
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
