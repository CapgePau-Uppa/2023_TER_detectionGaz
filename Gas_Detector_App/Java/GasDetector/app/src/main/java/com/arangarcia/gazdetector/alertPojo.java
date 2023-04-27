package com.arangarcia.gazdetector;

public class alertPojo {
   private String _id;
   private String longitude;
   private String latitude;
   private String danger;

    public alertPojo(String _id, String longitude, String latitude, String danger) {
        this._id = _id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.danger = danger;
    }

    public String get_id() {
        return _id;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getDanger() {
        return danger;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setDanger(String danger) {
        this.danger = danger;
    }
}
