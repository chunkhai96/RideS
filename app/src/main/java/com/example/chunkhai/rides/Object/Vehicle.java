package com.example.chunkhai.rides.Object;

public class Vehicle {

    String vehicle_plateNo;
    String vehicle_uid;
    String vehicle_model;
    String vehicle_color;
    String vehicle_imageSrc;
    int vehicle_capacity;

    public Vehicle() {
    }

    public Vehicle(String vehicle_uid, String vehicle_model, String vehicle_color, String vehicle_imageSrc, int vehicle_capacity) {
        this.vehicle_uid = vehicle_uid;
        this.vehicle_model = vehicle_model;
        this.vehicle_color = vehicle_color;
        this.vehicle_imageSrc = vehicle_imageSrc;
        this.vehicle_capacity = vehicle_capacity;
    }

    public Vehicle(String vehicle_plateNo, String vehicle_uid, String vehicle_model, String vehicle_color, String vehicle_imageSrc, int vehicle_capacity) {
        this.vehicle_plateNo = vehicle_plateNo;
        this.vehicle_uid = vehicle_uid;
        this.vehicle_model = vehicle_model;
        this.vehicle_color = vehicle_color;
        this.vehicle_imageSrc = vehicle_imageSrc;
        this.vehicle_capacity = vehicle_capacity;
    }

    public String getVehicle_uid() {
        return vehicle_uid;
    }

    public void setVehicle_uid(String vehicle_uid) {
        this.vehicle_uid = vehicle_uid;
    }

    public String getVehicle_plateNo() {
        return vehicle_plateNo;
    }

    public void setVehicle_plateNo(String vehicle_plateNo) {
        this.vehicle_plateNo = vehicle_plateNo;
    }

    public String getVehicle_model() {
        return vehicle_model;
    }

    public void setVehicle_model(String vehicle_model) {
        this.vehicle_model = vehicle_model;
    }

    public String getVehicle_color() {
        return vehicle_color;
    }

    public void setVehicle_color(String vehicle_color) {
        this.vehicle_color = vehicle_color;
    }

    public String getVehicle_imageSrc() {
        return vehicle_imageSrc;
    }

    public void setVehicle_imageSrc(String vehicle_imageSrc) {
        this.vehicle_imageSrc = vehicle_imageSrc;
    }

    public int getVehicle_capacity() {
        return vehicle_capacity;
    }

    public void setVehicle_capacity(int vehicle_capacity) {
        this.vehicle_capacity = vehicle_capacity;
    }
}
