package com.example.vkumaps.models;

import java.util.List;

public class ShortestPathResult {
    private List<Vertex> path;
    private int distance;

    public ShortestPathResult(List<Vertex> path, int distance) {
        this.path = path;
        this.distance = distance;
    }

    public List<Vertex> getPath() {
        return path;
    }

    public void setPath(List<Vertex> path) {
        this.path = path;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}