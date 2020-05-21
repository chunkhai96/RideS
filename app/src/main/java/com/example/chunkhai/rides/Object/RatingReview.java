package com.example.chunkhai.rides.Object;

import android.support.annotation.NonNull;

public class RatingReview implements Comparable<RatingReview>{

    private String rr_id;
    private String rr_raterUid;
    private String rr_receiverUid;
    private float rr_rating;
    private String rr_review;
    private long rr_createdTimeStamp;

    @Override
    public int compareTo(@NonNull RatingReview ratingReview) {
        if (rr_createdTimeStamp > ratingReview.rr_createdTimeStamp) {
            return 1;
        }
        else if (rr_createdTimeStamp <  ratingReview.rr_createdTimeStamp) {
            return -1;
        }
        else {
            return 0;
        }
    }

    public RatingReview() {
    }

    public RatingReview(String rr_raterUid, String rr_receiverUid, float rr_rating, String rr_review, long rr_createdTimeStamp) {
        this.rr_raterUid = rr_raterUid;
        this.rr_receiverUid = rr_receiverUid;
        this.rr_rating = rr_rating;
        this.rr_review = rr_review;
        this.rr_createdTimeStamp = rr_createdTimeStamp;
    }

    public RatingReview(String rr_id, String rr_raterUid, String rr_receiverUid, float rr_rating, String rr_review, long rr_createdTimeStamp) {
        this.rr_id = rr_id;
        this.rr_raterUid = rr_raterUid;
        this.rr_receiverUid = rr_receiverUid;
        this.rr_rating = rr_rating;
        this.rr_review = rr_review;
        this.rr_createdTimeStamp = rr_createdTimeStamp;
    }

    public String getRr_id() {
        return rr_id;
    }

    public void setRr_id(String rr_id) {
        this.rr_id = rr_id;
    }

    public String getRr_raterUid() {
        return rr_raterUid;
    }

    public void setRr_raterUid(String rr_raterUid) {
        this.rr_raterUid = rr_raterUid;
    }

    public String getRr_receiverUid() {
        return rr_receiverUid;
    }

    public void setRr_receiverUid(String rr_receiverUid) {
        this.rr_receiverUid = rr_receiverUid;
    }

    public float getRr_rating() {
        return rr_rating;
    }

    public void setRr_rating(float rr_rating) {
        this.rr_rating = rr_rating;
    }

    public String getRr_review() {
        return rr_review;
    }

    public void setRr_review(String rr_review) {
        this.rr_review = rr_review;
    }

    public long getRr_createdTimeStamp() {
        return rr_createdTimeStamp;
    }

    public void setRr_createdTimeStamp(long rr_createdTimeStamp) {
        this.rr_createdTimeStamp = rr_createdTimeStamp;
    }
}
