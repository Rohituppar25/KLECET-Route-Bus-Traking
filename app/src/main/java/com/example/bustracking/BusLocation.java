package com.example.bustracking;


public class BusLocation {
    public double latitude;
    public double longitude;

    public BusLocation() {} // required for Firebase

    public BusLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
