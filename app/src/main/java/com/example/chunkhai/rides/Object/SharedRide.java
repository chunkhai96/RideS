package com.example.chunkhai.rides.Object;

public class SharedRide {
    String rs_id;
    String rs_providedRideId;
    String rs_carpoolerUid;
    String rs_note;
    long rs_createdTimestamp;
    int rs_status;

    public SharedRide() {
    }

    public SharedRide(String rs_providedRideId, String rs_carpoolerUid, String rs_note, long rs_createdTimestamp, int rs_status) {
        this.rs_providedRideId = rs_providedRideId;
        this.rs_carpoolerUid = rs_carpoolerUid;
        this.rs_note = rs_note;
        this.rs_createdTimestamp = rs_createdTimestamp;
        this.rs_status = rs_status;
    }

    public SharedRide(String rs_id, String rs_providedRideId, String rs_carpoolerUid, String rs_note, long rs_createdTimestamp, int rs_status) {
        this.rs_id = rs_id;
        this.rs_providedRideId = rs_providedRideId;
        this.rs_carpoolerUid = rs_carpoolerUid;
        this.rs_note = rs_note;
        this.rs_createdTimestamp = rs_createdTimestamp;
        this.rs_status = rs_status;
    }

    public String getRs_id() {
        return rs_id;
    }

    public void setRs_id(String rs_id) {
        this.rs_id = rs_id;
    }

    public String getRs_providedRideId() {
        return rs_providedRideId;
    }

    public void setRs_providedRideId(String rs_providedRideId) {
        this.rs_providedRideId = rs_providedRideId;
    }

    public String getRs_carpoolerUid() {
        return rs_carpoolerUid;
    }

    public void setRs_carpoolerUid(String rs_carpoolerUid) {
        this.rs_carpoolerUid = rs_carpoolerUid;
    }

    public long getRs_createdTimestamp() {
        return rs_createdTimestamp;
    }

    public void setRs_createdTimestamp(long rs_createdTimestamp) {
        this.rs_createdTimestamp = rs_createdTimestamp;
    }

    public int getRs_status() {
        return rs_status;
    }

    public void setRs_status(int rs_status) {
        this.rs_status = rs_status;
    }

    public String getRs_note() {
        return rs_note;
    }

    public void setRs_note(String rs_note) {
        this.rs_note = rs_note;
    }
}
