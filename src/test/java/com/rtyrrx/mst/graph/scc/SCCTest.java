package com.rtyrrx.mst.graph.scc;

import com.rtyrrx.mst.common.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SCCTest {

    @Test
    void testSimpleDAG() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);

        SCC scc = new SCC(graph);
        scc.findSCCs();

        assertEquals(3, scc.getSCCCount(), "DAG should have 3 SCCs (each vertex is its own SCC)");
    }

    @Test
    void testSimpleCycle() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);

        SCC scc = new SCC(graph);
        List<List<Integer>> components = scc.findSCCs();

        assertEquals(1, scc.getSCCCount(), "Simple cycle should have 1 SCC");
        assertEquals(3, components.getFirst().size(), "SCC should contain all 3 vertices");
    }

    @Test
    void testMultipleSCCs() {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(3, 4);
        graph.addEdge(4, 3);
        graph.addEdge(2, 3);

        SCC scc = new SCC(graph);
        scc.findSCCs();

        assertEquals(2, scc.getSCCCount(), "Should have 2 SCCs");
    }

    @Test
    void testSingleVertex() {
        Graph graph = new Graph(1);

        SCC scc = new SCC(graph);
        List<List<Integer>> components = scc.findSCCs();

        assertEquals(1, scc.getSCCCount());
        assertEquals(1, components.getFirst().size());
    }

    @Test
    void testDisconnectedGraph() {
        Graph graph = new Graph(2);

        SCC scc = new SCC(graph);
        scc.findSCCs();

        assertEquals(2, scc.getSCCCount());
    }

    @Test
    void testCondensationGraph() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1);
        graph.addEdge(1, 0);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 2);

        SCC scc = new SCC(graph);
        scc.findSCCs();

        Graph condensation = scc.buildCondensationGraph();

        assertEquals(2, condensation.getVertices(), "Condensation should have 2 vertices");
        assertTrue(condensation.countEdges() >= 1, "Condensation should have at least 1 edge");
    }

    @Test
    void testMetricsTracking() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);

        SCC scc = new SCC(graph);
        scc.findSCCs();

        assertTrue(scc.getMetrics().getCounter("dfs_visits") > 0, "Should track DFS visits");
        assertTrue(scc.getMetrics().getElapsedTimeNanos() > 0, "Should track execution time");
    }
}
