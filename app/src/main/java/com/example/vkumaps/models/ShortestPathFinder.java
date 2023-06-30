package com.example.vkumaps.models;

import java.util.*;

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
    public static void main(String[] args) {
        // Tạo đồ thị
        Graph graph = new Graph();

        Vertex A = new Vertex("A");
        Vertex B = new Vertex("B");
        Vertex C = new Vertex("C");
        Vertex D = new Vertex("D");
        Vertex E = new Vertex("E");

        graph.addVertex(A);
        graph.addVertex(B);
        graph.addVertex(C);
        graph.addVertex(D);
        graph.addVertex(E);

        graph.addEdge(A, B, 5);
        graph.addEdge(A, C, 3);
        graph.addEdge(B, C, 1);
        graph.addEdge(B, D, 2);
        graph.addEdge(C, D, 4);
        graph.addEdge(C, E, 6);
        graph.addEdge(D, E, 7);

        // Tạo đối tượng DijkstraShortestPath
        ShortestPathFinder dijkstra = new ShortestPathFinder(graph);

        // Tìm đường đi ngắn nhất từ đỉnh bắt đầu đến đỉnh đích
        Vertex startVertex = B;
        Vertex targetVertex = E;
        ShortestPathResult result= dijkstra.findShortestPath(startVertex, targetVertex);

// Lấy đường đi ngắn nhất và khoảng cách cuối cùng
        List<Vertex> shortestPath = result.getPath();
        int shortestDistance = result.getDistance();

// In đường đi ngắn nhất và khoảng cách cuối cùng
        if (shortestPath != null) {
            System.out.print("Đường đi ngắn nhất từ " + startVertex.getLabel() + " đến " + targetVertex.getLabel() + ": ");
            for (Vertex vertex : shortestPath) {
                System.out.print(vertex.getLabel() + " ");
            }
            System.out.println();
            System.out.println("Khoảng cách ngắn nhất từ " + startVertex.getLabel() + " đến " + targetVertex.getLabel() + ": " + shortestDistance);
        } else {
            System.out.println("Không tìm thấy đường đi từ " + startVertex.getLabel() + " đến " + targetVertex.getLabel());
        }
    }
}
