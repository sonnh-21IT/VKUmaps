package com.example.vkumaps.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class ShortestPathFinder {
    private Graph graph;

    public ShortestPathFinder(Graph graph) {
        this.graph = graph;
    }

    public ShortestPathResult findShortestPath(Vertex startVertex, Vertex targetVertex) {
        // Khởi tạo khoảng cách từ đỉnh bắt đầu đến tất cả các đỉnh khác là vô cùng
        Map<Vertex, Integer> distances = new HashMap<>();
        for (Vertex vertex : graph.getVertices()) {
            distances.put(vertex, Integer.MAX_VALUE);
        }

        // Thiết lập khoảng cách từ đỉnh bắt đầu đến chính nó là 0
        distances.put(startVertex, 0);

        // Thiết lập các đỉnh đã xét
        Set<Vertex> visitedVertices = new HashSet<>();

        // Thiết lập các đỉnh trước đỉnh hiện tại trong đường đi ngắn nhất
        Map<Vertex, Vertex> previousVertices = new HashMap<>();

        // Xây dựng hàng đợi ưu tiên để lựa chọn đỉnh có khoảng cách ngắn nhất
        PriorityQueue<Vertex> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        priorityQueue.add(startVertex);

        while (!priorityQueue.isEmpty()) {
            Vertex currentVertex = priorityQueue.poll();

            if (currentVertex.equals(targetVertex)) {
                // Đã tìm thấy đường đi ngắn nhất đến đỉnh đích, thoát khỏi vòng lặp
                break;
            }

            visitedVertices.add(currentVertex);

            for (Edge edge : graph.getEdgesFromVertex(currentVertex)) {
                Vertex neighborVertex = edge.getTargetVertex();
                int edgeWeight = edge.getWeight();
                int newDistance = distances.get(currentVertex) + edgeWeight;

                if (newDistance < distances.get(neighborVertex)) {
                    distances.put(neighborVertex, newDistance);
                    previousVertices.put(neighborVertex, currentVertex);

                    if (!visitedVertices.contains(neighborVertex)) {
                        priorityQueue.add(neighborVertex);
                    }
                }
            }
        }

        // Tạo đường đi từ đỉnh đích về đỉnh bắt đầu
        List<Vertex> shortestPath = new ArrayList<>();
        Vertex currentVertex = targetVertex;
        while (currentVertex != null) {
            shortestPath.add(0, currentVertex);
            currentVertex = previousVertices.get(currentVertex);
        }

        // Trả về kết quả đường đi ngắn nhất và khoảng cách cuối cùng
        int shortestDistance = distances.get(targetVertex);
        return new ShortestPathResult(shortestPath, shortestDistance);
    }
}
