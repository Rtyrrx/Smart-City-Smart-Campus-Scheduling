package com.rtyrrx.mst.common;

import java.util.*;

public class Graph {
    private final int vertices;
    private final List<List<Edge>> adjacencyList;
    private final Map<Integer, String> vertexToTaskId;

    public Graph(int vertices) {
        this.vertices = vertices;
        this.adjacencyList = new ArrayList<>(vertices);
        this.vertexToTaskId = new HashMap<>();

        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int source, int dest, double weight) {
        adjacencyList.get(source).add(new Edge(dest, weight));
    }

    public void addEdge(int source, int dest) {
        addEdge(source, dest, 1.0);
    }

    public int getVertices() {
        return vertices;
    }

    public List<Edge> getAdjacentEdges(int vertex) {
        return adjacencyList.get(vertex);
    }

    public List<Integer> getAdjacentVertices(int vertex) {
        List<Integer> adjacent = new ArrayList<>();
        for (Edge edge : adjacencyList.get(vertex)) {
            adjacent.add(edge.destination);
        }
        return adjacent;
    }

    public void mapTaskToVertex(String taskId, int vertex) {
        vertexToTaskId.put(vertex, taskId);
    }

    public String getTaskId(int vertex) {
        return vertexToTaskId.get(vertex);
    }

    public int countEdges() {
        int count = 0;
        for (List<Edge> edges : adjacencyList) {
            count += edges.size();
        }
        return count;
    }

    public static class Edge {
        public final int destination;
        public final double weight;

        public Edge(int destination, double weight) {
            this.destination = destination;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "(" + destination + ", w=" + weight + ")";
        }
    }
}
