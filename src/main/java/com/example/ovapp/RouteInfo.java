package com.example.ovapp;

public class RouteInfo {
    private final double totalDistance;
    private final int totalTravelTime;

    public RouteInfo(double totalDistance, int totalTravelTime) {
        this.totalDistance = totalDistance;
        this.totalTravelTime = totalTravelTime;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public int getTotalTravelTime() {
        return totalTravelTime;
    }
}
