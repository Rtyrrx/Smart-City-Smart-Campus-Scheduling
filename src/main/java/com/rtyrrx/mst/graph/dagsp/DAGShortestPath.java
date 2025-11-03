package com.rtyrrx.mst.graph.dagsp;

import com.rtyrrx.mst.common.Graph;
import com.rtyrrx.mst.common.Metrics;
import com.rtyrrx.mst.common.MetricsImpl;
import com.rtyrrx.mst.graph.topo.TopologicalSort;

import java.util.*;

public class DAGShortestPath {
    private final Graph graph;
    private final Metrics metrics;

    public DAGShortestPath(Graph graph) {
        this.graph = graph;
        this.metrics = new MetricsImpl();
    }

    public ShortestPathResult findShortestPaths(int source) {
        PathResult result = computePaths(source, true);
        if (result == null) return null;
        return new ShortestPathResult(result.distances, result.predecessors, source);
    }

    public LongestPathResult findLongestPath(int source) {
        PathResult result = computePaths(source, false);
        if (result == null) return null;

        int maxVertex = source;
        double maxDist = result.distances[source];
        for (int v = 0; v < graph.getVertices(); v++) {
            if (result.distances[v] != Double.NEGATIVE_INFINITY && result.distances[v] > maxDist) {
                maxDist = result.distances[v];
                maxVertex = v;
            }
        }

        List<Integer> criticalPath = reconstructPath(result.predecessors, source, maxVertex);
        return new LongestPathResult(result.distances, result.predecessors, source, criticalPath, maxDist);
    }

    private PathResult computePaths(int source, boolean findShortest) {
        int n = graph.getVertices();
        double[] dist = new double[n];
        int[] pred = new int[n];

        double initialValue = findShortest ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        Arrays.fill(dist, initialValue);
        Arrays.fill(pred, -1);
        dist[source] = 0;

        metrics.reset();
        metrics.startTiming();

        TopologicalSort topoSort = new TopologicalSort(graph);
        List<Integer> topoOrder = topoSort.sort();

        if (topoOrder == null) {
            metrics.stopTiming();
            return null;
        }

        for (int u : topoOrder) {
            if (dist[u] != initialValue) {
                for (Graph.Edge edge : graph.getAdjacentEdges(u)) {
                    metrics.incrementCounter("relaxations");
                    int v = edge.destination;
                    double newDist = dist[u] + edge.weight;

                    boolean shouldUpdate = findShortest ? (newDist < dist[v]) : (newDist > dist[v]);
                    if (shouldUpdate) {
                        dist[v] = newDist;
                        pred[v] = u;
                    }
                }
            }
        }

        metrics.stopTiming();
        return new PathResult(dist, pred);
    }

    private static List<Integer> reconstructPath(int[] pred, int source, int dest) {
        if (pred[dest] == -1 && dest != source) {
            return Collections.emptyList();
        }

        List<Integer> path = new ArrayList<>();
        int current = dest;

        while (current != -1) {
            path.add(current);
            if (current == source) break;
            current = pred[current];
        }

        Collections.reverse(path);
        return path;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    private record PathResult(double[] distances, int[] predecessors) {
    }

    public record ShortestPathResult(double[] distances, int[] predecessors, int source) {
        public List<Integer> getPathTo(int dest) {
            return reconstructPath(predecessors, source, dest);
        }
    }

    public record LongestPathResult(double[] distances, int[] predecessors, int source,
                                     List<Integer> criticalPath, double criticalPathLength) {
    }
}
