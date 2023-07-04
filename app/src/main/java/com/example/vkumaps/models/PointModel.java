package com.example.vkumaps.models;

import com.google.firebase.firestore.GeoPoint;

public class PointModel {
    GeoPoint geo;

    public PointModel() {
    }

    public PointModel(GeoPoint geo) {
        this.geo = geo;
    }

    public GeoPoint getGeo() {
        return geo;
    }

    public void setGeo(GeoPoint geo) {
        this.geo = geo;
    }
}
