package com.example.chunkhai.rides.Object;

public class LocationLatLng {
    private String location_id;
    private double latitude;
    private double longitude;

    public LocationLatLng() {
    }

    public LocationLatLng(String location_id, double latitude, double longitude) {
        this.location_id = location_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
