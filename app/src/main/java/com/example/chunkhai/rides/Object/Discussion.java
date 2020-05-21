package com.example.chunkhai.rides.Object;

public class Discussion {
    private String disc_content;
    private long disc_timestamp;
    private String disc_sender;

    public Discussion() {
    }

    public Discussion(String disc_content, long disc_timestamp, String disc_sender) {
        this.disc_content = disc_content;
        this.disc_timestamp = disc_timestamp;
        this.disc_sender = disc_sender;
    }

    public String getDisc_content() {
        return disc_content;
    }

    public void setDisc_content(String disc_content) {
        this.disc_content = disc_content;
    }

    public long getDisc_timestamp() {
        return disc_timestamp;
    }

    public void setDisc_timestamp(long disc_timestamp) {
        this.disc_timestamp = disc_timestamp;
    }

    public String getDisc_sender() {
        return disc_sender;
    }

    public void setDisc_sender(String disc_sender) {
        this.disc_sender = disc_sender;
    }
}
