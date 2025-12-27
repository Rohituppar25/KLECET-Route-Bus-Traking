package com.example.bustracking;

public class BusModel {
    private String driverName;
    private String busNumber;
    private String routeName;
    private String driverId;
    private double latitude;
    private double longitude;

    public BusModel() {
        // Required empty constructor for Firebase
    }

    public BusModel(String driverName, String busNumber, String routeName,
                    String driverId, double latitude, double longitude) {
        this.driverName = driverName;
        this.busNumber = busNumber;
        this.routeName = routeName;
        this.driverId = driverId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDriverName() { return driverName; }
    public String getBusNumber() { return busNumber; }
    public String getRouteName() { return routeName; }
    public String getDriverId() { return driverId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
