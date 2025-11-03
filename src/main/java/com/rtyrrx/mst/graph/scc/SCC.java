package com.rtyrrx.mst.graph.scc;

import com.rtyrrx.mst.common.Graph;
import com.rtyrrx.mst.common.Metrics;
import com.rtyrrx.mst.common.MetricsImpl;

import java.util.*;

public class SCC {
    private final Graph graph;
    private final Metrics metrics;
    private int[] ids;
    private int[] low;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private int id;
    private int sccCount;
    private List<List<Integer>> components;

    public SCC(Graph graph) {
        this.graph = graph;
        this.metrics = new MetricsImpl();
    }

    public List<List<Integer>> findSCCs() {
        int n = graph.getVertices();
        ids = new int[n];
        low = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        components = new ArrayList<>();

        Arrays.fill(ids, -1);
        id = 0;
        sccCount = 0;

        metrics.reset();
        metrics.startTiming();

        for (int v = 0; v < n; v++) {
            if (ids[v] == -1) {
                dfs(v);
            }
        }

        metrics.stopTiming();
        return components;
    }

    private void dfs(int at) {
        metrics.incrementCounter("dfs_visits");

        ids[at] = id;
        low[at] = id;
        id++;
        stack.push(at);
        onStack[at] = true;

        for (int to : graph.getAdjacentVertices(at)) {
            metrics.incrementCounter("edges_explored");

            if (ids[to] == -1) {
                dfs(to);
            }
            if (onStack[to]) {
                low[at] = Math.min(low[at], low[to]);
            }
        }

        if (ids[at] == low[at]) {
            List<Integer> component = new ArrayList<>();
            while (true) {
                int node = stack.pop();
                onStack[node] = false;
                component.add(node);
                low[node] = ids[at];
                if (node == at) break;
            }
            components.add(component);
            sccCount++;
        }
    }

    public Graph buildCondensationGraph() {
        if (components == null) {
            findSCCs();
        }

        Graph condensation = new Graph(sccCount);

        int[] vertexToComponent = new int[graph.getVertices()];
        for (int i = 0; i < components.size(); i++) {
            for (int vertex : components.get(i)) {
                vertexToComponent[vertex] = i;
            }
        }

        Set<String> addedEdges = new HashSet<>();
        for (int v = 0; v < graph.getVertices(); v++) {
            int fromComp = vertexToComponent[v];
            for (Graph.Edge edge : graph.getAdjacentEdges(v)) {
                int toComp = vertexToComponent[edge.destination];
                if (fromComp != toComp) {
                    String edgeKey = fromComp + "->" + toComp;
                    if (!addedEdges.contains(edgeKey)) {
                        condensation.addEdge(fromComp, toComp, edge.weight);
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }

        return condensation;
    }

    public int getSCCCount() {
        return sccCount;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}
