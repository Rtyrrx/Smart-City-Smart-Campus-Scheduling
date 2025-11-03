package com.rtyrrx.mst.graph.dagsp;

import com.rtyrrx.mst.common.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DAGShortestPathTest {

    @Test
    void testShortestPathSimpleDAG() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 2.0);
        graph.addEdge(1, 2, 3.0);

        DAGShortestPath dagSP = new DAGShortestPath(graph);
        DAGShortestPath.ShortestPathResult result = dagSP.findShortestPaths(0);

        assertNotNull(result);
        assertEquals(0.0, result.distances()[0], 0.001);
        assertEquals(2.0, result.distances()[1], 0.001);
        assertEquals(5.0, result.distances()[2], 0.001);
    }

    @Test
    void testShortestPathMultiplePaths() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 5.0);
        graph.addEdge(0, 2, 2.0);
        graph.addEdge(1, 3, 1.0);
        graph.addEdge(2, 3, 3.0);

        DAGShortestPath dagSP = new DAGShortestPath(graph);
        DAGShortestPath.ShortestPathResult result = dagSP.findShortestPaths(0);

        assertNotNull(result);
        assertEquals(5.0, result.distances()[3], 0.001);

        List<Integer> path = result.getPathTo(3);
        assertEquals(3, path.size());
        assertEquals(0, path.get(0));
        assertEquals(2, path.get(1));
        assertEquals(3, path.get(2));
    }

    @Test
    void testLongestPathSimpleDAG() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 2.0);
        graph.addEdge(1, 2, 3.0);

        DAGShortestPath dagSP = new DAGShortestPath(graph);
        DAGShortestPath.LongestPathResult result = dagSP.findLongestPath(0);

        assertNotNull(result);
        assertEquals(5.0, result.criticalPathLength(), 0.001);
        assertEquals(3, result.criticalPath().size());
    }

    @Test
    void testLongestPathMultiplePaths() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 5.0);
        graph.addEdge(0, 2, 2.0);
        graph.addEdge(1, 3, 1.0);
        graph.addEdge(2, 3, 3.0);

        DAGShortestPath dagSP = new DAGShortestPath(graph);
        DAGShortestPath.LongestPathResult result = dagSP.findLongestPath(0);

        assertNotNull(result);
        assertEquals(6.0, result.criticalPathLength(), 0.001);

        List<Integer> criticalPath = result.criticalPath();
        assertEquals(0, criticalPath.getFirst());
        assertEquals(3, criticalPath.getLast());
    }

    @Test
    void testSingleVertex() {
        Graph graph = new Graph(1);

        DAGShortestPath dagSP = new DAGShortestPath(graph);
        DAGShortestPath.ShortestPathResult result = dagSP.findShortestPaths(0);

        assertNotNull(result);
        assertEquals(0.0, result.distances()[0], 0.001);
    }

    @Test
    void testDisconnectedVertices() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);

        DAGShortestPath dagSP = new DAGShortestPath(graph);
        DAGShortestPath.ShortestPathResult result = dagSP.findShortestPaths(0);

        assertNotNull(result);
        assertEquals(0.0, result.distances()[0], 0.001);
        assertEquals(1.0, result.distances()[1], 0.001);
        assertEquals(Double.POSITIVE_INFINITY, result.distances()[2]);
    }

    @Test
    void testMetrics() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);

        DAGShortestPath dagSP = new DAGShortestPath(graph);
        dagSP.findShortestPaths(0);

        assertTrue(dagSP.getMetrics().getCounter("relaxations") > 0);
        assertTrue(dagSP.getMetrics().getElapsedTimeNanos() > 0);
    }

    @Test
    void testPathReconstruction() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 3, 1.0);

        DAGShortestPath dagSP = new DAGShortestPath(graph);
        DAGShortestPath.ShortestPathResult result = dagSP.findShortestPaths(0);

        List<Integer> path = result.getPathTo(3);
        assertEquals(4, path.size());
        assertEquals(0, path.get(0));
        assertEquals(1, path.get(1));
        assertEquals(2, path.get(2));
        assertEquals(3, path.get(3));
    }
}
