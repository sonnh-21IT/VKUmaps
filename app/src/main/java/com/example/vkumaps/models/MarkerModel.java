package com.example.vkumaps.models;

import com.google.firebase.firestore.GeoPoint;

public class MarkerModel {
    String iconURL;
    GeoPoint geopoint;
    String imgURL;

    public MarkerModel() {
    }

    public MarkerModel(String iconURL, GeoPoint geopoint, String imgURL) {
        this.iconURL = iconURL;
        this.geopoint = geopoint;
        this.imgURL = imgURL;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public GeoPoint getGeopoint() {
        return geopoint;
    }

    public void setGeopoint(GeoPoint geopoint) {
        this.geopoint = geopoint;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
