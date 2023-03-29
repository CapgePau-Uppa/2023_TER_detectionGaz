package com.arangarcia.gazdetector;

import com.google.gson.annotations.SerializedName;

public class sendResult {

    private String latitude;
    @SerializedName("longitude")
    private String longitude;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

}
