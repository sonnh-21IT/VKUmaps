package com.example.vkumaps.models;

import com.google.firebase.firestore.GeoPoint;

public class MarkerModel {
    String iconURL;
    GeoPoint geopoint;

    public MarkerModel() {
    }

    public MarkerModel(String iconURL, GeoPoint geopoint) {
        this.iconURL = iconURL;
        this.geopoint = geopoint;
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
}