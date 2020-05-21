package com.example.chunkhai.rides.Object;

import android.support.annotation.NonNull;

public class Notification implements Comparable<Notification>{
    String notif_id;
    String notif_reciever_uid;
    String notif_message;
    int notif_status;
    int notif_type;
    long notif_createdTimestamp;
    String notif_sender_uid;
    String notif_ref_providedRideId;

    @Override
    public int compareTo(@NonNull Notification notification) {
        if (notif_createdTimestamp > notification.notif_createdTimestamp) {
            return 1;
        }
        else if (notif_createdTimestamp <  notification.notif_createdTimestamp) {
            return -1;
        }
        else {
            return 0;
        }
    }

    public Notification() {
    }

    public Notification(String notif_reciever_uid, String notif_message, int notif_status, int notif_type, long notif_createdTimestamp, String notif_ref_providedRideId) {
        this.notif_reciever_uid = notif_reciever_uid;
        this.notif_message = notif_message;
        this.notif_status = notif_status;
        this.notif_type = notif_type;
        this.notif_createdTimestamp = notif_createdTimestamp;
        this.notif_ref_providedRideId = notif_ref_providedRideId;
    }

    public Notification(String notif_id, String notif_reciever_uid, String notif_message, int notif_status, int notif_type, long notif_createdTimestamp, String notif_ref_providedRideId) {
        this.notif_id = notif_id;
        this.notif_reciever_uid = notif_reciever_uid;
        this.notif_message = notif_message;
        this.notif_status = notif_status;
        this.notif_type = notif_type;
        this.notif_createdTimestamp = notif_createdTimestamp;
        this.notif_ref_providedRideId = notif_ref_providedRideId;
    }

    public String getNotif_id() {
        return notif_id;
    }

    public void setNotif_id(String notif_id) {
        this.notif_id = notif_id;
    }

    public String getNotif_reciever_uid() {
        return notif_reciever_uid;
    }

    public void setNotif_reciever_uid(String notif_reciever_uid) {
        this.notif_reciever_uid = notif_reciever_uid;
    }

    public String getNotif_message() {
        return notif_message;
    }

    public void setNotif_message(String notif_message) {
        this.notif_message = notif_message;
    }

    public int getNotif_status() {
        return notif_status;
    }

    public void setNotif_status(int notif_status) {
        this.notif_status = notif_status;
    }

    public int getNotif_type() {
        return notif_type;
    }

    public void setNotif_type(int notif_type) {
        this.notif_type = notif_type;
    }

    public long getNotif_createdTimestamp() {
        return notif_createdTimestamp;
    }

    public void setNotif_createdTimestamp(long notif_createdTimestamp) {
        this.notif_createdTimestamp = notif_createdTimestamp;
    }

    public String getNotif_sender_uid() {
        return notif_sender_uid;
    }

    public void setNotif_sender_uid(String notif_sender_uid) {
        this.notif_sender_uid = notif_sender_uid;
    }

    public String getNotif_ref_providedRideId() {
        return notif_ref_providedRideId;
    }

    public void setNotif_ref_providedRideId(String notif_ref_providedRideId) {
        this.notif_ref_providedRideId = notif_ref_providedRideId;
    }
}
