package com.example.vkumaps.models;

import com.google.android.gms.maps.model.LatLng;

public class Vertex {
    private String label;
    private LatLng position;

    public Vertex(String label,LatLng position) {
        this.label = label;
        this.position=position;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getLabel() {
        return label;
    }
}
