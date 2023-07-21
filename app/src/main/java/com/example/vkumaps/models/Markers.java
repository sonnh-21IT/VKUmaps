package com.example.vkumaps.models;

public class Markers {
    private MarkerModel markerModel;
    private String name;
    public Markers(MarkerModel markerModel,String name){
        this.markerModel=markerModel;
        this.name=name;
    }

    public MarkerModel getMarkerModel() {
        return markerModel;
    }

    public void setMarkerModel(MarkerModel markerModel) {
        this.markerModel = markerModel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
