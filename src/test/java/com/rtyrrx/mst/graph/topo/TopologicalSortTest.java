package com.rtyrrx.mst.graph.topo;

import com.rtyrrx.mst.common.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TopologicalSortTest {

    @Test
    void testSimpleDAG() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);

        TopologicalSort topoSort = new TopologicalSort(graph);
        List<Integer> order = topoSort.sort();

        assertNotNull(order, "Should return valid topological order");
        assertEquals(3, order.size());

        int pos0 = order.indexOf(0);
        int pos1 = order.indexOf(1);
        int pos2 = order.indexOf(2);

        assertTrue(pos0 < pos1, "0 should come before 1");
        assertTrue(pos1 < pos2, "1 should come before 2");
    }

    @Test
    void testCycleDetection() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);

        TopologicalSort topoSort = new TopologicalSort(graph);
        List<Integer> order = topoSort.sort();

        assertNull(order, "Should return null for graph with cycle");
    }

    @Test
    void testDiamondDAG() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);

        TopologicalSort topoSort = new TopologicalSort(graph);
        List<Integer> order = topoSort.sort();

        assertNotNull(order);
        assertEquals(4, order.size());

        assertEquals(0, order.get(0));
        assertEquals(3, order.get(3));
    }

    @Test
    void testSingleVertex() {
        Graph graph = new Graph(1);

        TopologicalSort topoSort = new TopologicalSort(graph);
        List<Integer> order = topoSort.sort();

        assertNotNull(order);
        assertEquals(1, order.size());
        assertEquals(0, order.getFirst());
    }

    @Test
    void testDFSVariant() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);

        TopologicalSort topoSort = new TopologicalSort(graph);
        List<Integer> order = topoSort.sortDFS();

        assertNotNull(order);
        assertEquals(3, order.size());

        int pos0 = order.indexOf(0);
        int pos1 = order.indexOf(1);
        int pos2 = order.indexOf(2);

        assertTrue(pos0 < pos1);
        assertTrue(pos1 < pos2);
    }

    @Test
    void testDFSCycleDetection() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);

        TopologicalSort topoSort = new TopologicalSort(graph);
        List<Integer> order = topoSort.sortDFS();

        assertNull(order, "DFS variant should also detect cycles");
    }

    @Test
    void testMetrics() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);

        TopologicalSort topoSort = new TopologicalSort(graph);
        topoSort.sort();

        assertTrue(topoSort.getMetrics().getCounter("queue_pushes") > 0);
        assertTrue(topoSort.getMetrics().getCounter("queue_pops") > 0);
        assertTrue(topoSort.getMetrics().getElapsedTimeNanos() > 0);
    }
}
