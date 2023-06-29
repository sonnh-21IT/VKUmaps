package com.example.vkumaps.models;

public class Edge {
    private Vertex sourceVertex;
    private Vertex targetVertex;
    private int weight;

    public Edge(Vertex sourceVertex, Vertex targetVertex, int weight) {
        this.sourceVertex = sourceVertex;
        this.targetVertex = targetVertex;
        this.weight = weight;
    }

    public Vertex getSourceVertex() {
        return sourceVertex;
    }

    public Vertex getTargetVertex() {
        return targetVertex;
    }

    public int getWeight() {
        return weight;
    }
}