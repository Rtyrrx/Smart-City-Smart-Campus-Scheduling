package com.rtyrrx.mst.graph.topo;

import com.rtyrrx.mst.common.Graph;
import com.rtyrrx.mst.common.Metrics;
import com.rtyrrx.mst.common.MetricsImpl;

import java.util.*;

public class TopologicalSort {
    private final Graph graph;
    private final Metrics metrics;

    public TopologicalSort(Graph graph) {
        this.graph = graph;
        this.metrics = new MetricsImpl();
    }

    public List<Integer> sort() {
        int n = graph.getVertices();
        int[] inDegree = new int[n];

        metrics.reset();
        metrics.startTiming();

        for (int v = 0; v < n; v++) {
            for (int neighbor : graph.getAdjacentVertices(v)) {
                inDegree[neighbor]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int v = 0; v < n; v++) {
            if (inDegree[v] == 0) {
                queue.offer(v);
                metrics.incrementCounter("queue_pushes");
            }
        }

        List<Integer> topoOrder = new ArrayList<>();

        while (!queue.isEmpty()) {
            int v = queue.poll();
            metrics.incrementCounter("queue_pops");
            topoOrder.add(v);

            for (int neighbor : graph.getAdjacentVertices(v)) {
                metrics.incrementCounter("edges_processed");
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                    metrics.incrementCounter("queue_pushes");
                }
            }
        }

        metrics.stopTiming();

        if (topoOrder.size() != n) {
            return null;
        }

        return topoOrder;
    }

    public List<Integer> sortDFS() {
        int n = graph.getVertices();
        boolean[] visited = new boolean[n];
        boolean[] recStack = new boolean[n];
        Stack<Integer> stack = new Stack<>();

        metrics.reset();
        metrics.startTiming();

        for (int v = 0; v < n; v++) {
            if (!visited[v]) {
                if (hasCycle(v, visited, recStack, stack)) {
                    metrics.stopTiming();
                    return null;
                }
            }
        }

        metrics.stopTiming();

        List<Integer> topoOrder = new ArrayList<>();
        while (!stack.isEmpty()) {
            topoOrder.add(stack.pop());
        }

        return topoOrder;
    }

    private boolean hasCycle(int v, boolean[] visited, boolean[] recStack, Stack<Integer> stack) {
        metrics.incrementCounter("dfs_visits");
        visited[v] = true;
        recStack[v] = true;

        for (int neighbor : graph.getAdjacentVertices(v)) {
            metrics.incrementCounter("edges_explored");

            if (!visited[neighbor]) {
                if (hasCycle(neighbor, visited, recStack, stack)) {
                    return true;
                }
            } else if (recStack[neighbor]) {
                return true;
            }
        }

        recStack[v] = false;
        stack.push(v);
        return false;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}
