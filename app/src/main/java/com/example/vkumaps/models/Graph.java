package com.example.vkumaps.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private List<Vertex> vertices;
    private Map<Vertex, List<Edge>> adjacencyMap;

    public Graph() {
        vertices = new ArrayList<>();
        adjacencyMap = new HashMap<>();
    }

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    public void addEdge(Vertex sourceVertex, Vertex targetVertex, int weight) {
        Edge forwardEdge = new Edge(sourceVertex, targetVertex, weight);
        Edge backwardEdge = new Edge(targetVertex, sourceVertex, weight);

        adjacencyMap.computeIfAbsent(sourceVertex, k -> new ArrayList<>()).add(forwardEdge);
        adjacencyMap.computeIfAbsent(targetVertex, k -> new ArrayList<>()).add(backwardEdge);
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdgesFromVertex(Vertex vertex) {
        return adjacencyMap.getOrDefault(vertex, new ArrayList<>());
    }
}
