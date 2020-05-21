package com.example.chunkhai.rides.Object;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

public class ParcelUser implements Parcelable{
    String user_uid;
    String user_profileName;
    String user_profilePicSrc;

    public ParcelUser() {
    }

    // Constructor
    public ParcelUser(String user_uid, String user_profileName, String user_profilePicSrc){
        this.user_uid = user_uid;
        this.user_profileName = user_profileName;
        this.user_profilePicSrc = user_profilePicSrc;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getUser_profileName() {
        return user_profileName;
    }

    public void setUser_profileName(String user_profileName) {
        this.user_profileName = user_profileName;
    }

    public String getUser_profilePicSrc() {
        return user_profilePicSrc;
    }

    public void setUser_profilePicSrc(String user_profilePicSrc) {
        this.user_profilePicSrc = user_profilePicSrc;
    }

    // Parcelling part
    public ParcelUser(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.user_uid = data[0];
        this.user_profileName = data[1];
        this.user_profilePicSrc = data[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.user_uid, this.user_profileName,
                this.user_profilePicSrc});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParcelUser createFromParcel(Parcel in) {
            return new ParcelUser(in);
        }

        public ParcelUser[] newArray(int size) {
            return new ParcelUser[size];
        }
    };
}

