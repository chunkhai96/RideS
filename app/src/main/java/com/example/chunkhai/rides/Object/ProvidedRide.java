package com.example.chunkhai.rides.Object;

import android.support.annotation.NonNull;

public class ProvidedRide implements Comparable<ProvidedRide> {
    private String pr_id;
    private String pr_providerUid;
    private long pr_createdTimestamp;
    private double pr_fromLatitude;
    private double pr_fromLongitude;
    private String pr_fromPlace;
    private String pr_fromAddress;
    private double pr_toLatitude;
    private double pr_toLongitude;
    private String pr_toPlace;
    private String pr_toAddress;
    private long pr_departTimestamp;
    private String pr_vehicleId;
    private double pr_sharedFare;
    private int pr_status;

    @Override
    public int compareTo(@NonNull ProvidedRide providedRide) {
        if (pr_departTimestamp > providedRide.pr_departTimestamp) {
            return 1;
        }
        else if (pr_departTimestamp <  providedRide.pr_departTimestamp) {
            return -1;
        }
        else {
            return 0;
        }
    }

    public ProvidedRide() {
    }

    public ProvidedRide(String pr_providerUid, long pr_createdTimestamp, double pr_fromLatitude, double pr_fromLongitude, String pr_fromPlace, String pr_fromAddress, double pr_toLatitude, double pr_toLongitude, String pr_toPlace, String pr_toAddress, long pr_departTimestamp, String pr_vehicleId, double pr_sharedFare, int pr_status) {
        this.pr_providerUid = pr_providerUid;
        this.pr_createdTimestamp = pr_createdTimestamp;
        this.pr_fromLatitude = pr_fromLatitude;
        this.pr_fromLongitude = pr_fromLongitude;
        this.pr_fromPlace = pr_fromPlace;
        this.pr_fromAddress = pr_fromAddress;
        this.pr_toLatitude = pr_toLatitude;
        this.pr_toLongitude = pr_toLongitude;
        this.pr_toPlace = pr_toPlace;
        this.pr_toAddress = pr_toAddress;
        this.pr_departTimestamp = pr_departTimestamp;
        this.pr_vehicleId = pr_vehicleId;
        this.pr_sharedFare = pr_sharedFare;
        this.pr_status = pr_status;
    }

    public ProvidedRide(String pr_id, String pr_providerUid, long pr_createdTimestamp, double pr_fromLatitude, double pr_fromLongitude, String pr_fromPlace, String pr_fromAddress, double pr_toLatitude, double pr_toLongitude, String pr_toPlace, String pr_toAddress, long pr_departTimestamp, String pr_vehicleId, double pr_sharedFare, int pr_status) {
        this.pr_id = pr_id;
        this.pr_providerUid = pr_providerUid;
        this.pr_createdTimestamp = pr_createdTimestamp;
        this.pr_fromLatitude = pr_fromLatitude;
        this.pr_fromLongitude = pr_fromLongitude;
        this.pr_fromPlace = pr_fromPlace;
        this.pr_fromAddress = pr_fromAddress;
        this.pr_toLatitude = pr_toLatitude;
        this.pr_toLongitude = pr_toLongitude;
        this.pr_toPlace = pr_toPlace;
        this.pr_toAddress = pr_toAddress;
        this.pr_departTimestamp = pr_departTimestamp;
        this.pr_vehicleId = pr_vehicleId;
        this.pr_sharedFare = pr_sharedFare;
        this.pr_status = pr_status;
    }

    public String getPr_id() {
        return pr_id;
    }

    public void setPr_id(String pr_id) {
        this.pr_id = pr_id;
    }

    public String getPr_providerUid() {
        return pr_providerUid;
    }

    public void setPr_providerUid(String pr_providerUid) {
        this.pr_providerUid = pr_providerUid;
    }

    public long getPr_createdTimestamp() {
        return pr_createdTimestamp;
    }

    public void setPr_createdTimestamp(long pr_createdTimestamp) {
        this.pr_createdTimestamp = pr_createdTimestamp;
    }

    public double getPr_fromLatitude() {
        return pr_fromLatitude;
    }

    public void setPr_fromLatitude(double pr_fromLatitude) {
        this.pr_fromLatitude = pr_fromLatitude;
    }

    public double getPr_fromLongitude() {
        return pr_fromLongitude;
    }

    public void setPr_fromLongitude(double pr_fromLongitude) {
        this.pr_fromLongitude = pr_fromLongitude;
    }

    public String getPr_fromPlace() {
        return pr_fromPlace;
    }

    public void setPr_fromPlace(String pr_fromPlace) {
        this.pr_fromPlace = pr_fromPlace;
    }

    public String getPr_fromAddress() {
        return pr_fromAddress;
    }

    public void setPr_fromAddress(String pr_fromAddress) {
        this.pr_fromAddress = pr_fromAddress;
    }

    public double getPr_toLatitude() {
        return pr_toLatitude;
    }

    public void setPr_toLatitude(double pr_toLatitude) {
        this.pr_toLatitude = pr_toLatitude;
    }

    public double getPr_toLongitude() {
        return pr_toLongitude;
    }

    public void setPr_toLongitude(double pr_toLongitude) {
        this.pr_toLongitude = pr_toLongitude;
    }

    public String getPr_toPlace() {
        return pr_toPlace;
    }

    public void setPr_toPlace(String pr_toPlace) {
        this.pr_toPlace = pr_toPlace;
    }

    public String getPr_toAddress() {
        return pr_toAddress;
    }

    public void setPr_toAddress(String pr_toAddress) {
        this.pr_toAddress = pr_toAddress;
    }

    public long getPr_departTimestamp() {
        return pr_departTimestamp;
    }

    public void setPr_departTimestamp(long pr_departTimestamp) {
        this.pr_departTimestamp = pr_departTimestamp;
    }

    public String getPr_vehicleId() {
        return pr_vehicleId;
    }

    public void setPr_vehicleId(String pr_vehicleId) {
        this.pr_vehicleId = pr_vehicleId;
    }

    public double getPr_sharedFare() {
        return pr_sharedFare;
    }

    public void setPr_sharedFare(double pr_sharedFare) {
        this.pr_sharedFare = pr_sharedFare;
    }

    public int getPr_status() {
        return pr_status;
    }

    public void setPr_status(int pr_status) {
        this.pr_status = pr_status;
    }

}
