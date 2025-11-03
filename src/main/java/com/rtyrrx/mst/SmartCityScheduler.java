package com.rtyrrx.mst;

import com.rtyrrx.mst.common.Graph;
import com.rtyrrx.mst.data.GraphLoader;
import com.rtyrrx.mst.data.TaskGraph;
import com.rtyrrx.mst.graph.dagsp.DAGShortestPath;
import com.rtyrrx.mst.graph.scc.SCC;
import com.rtyrrx.mst.graph.topo.TopologicalSort;

import java.io.IOException;
import java.util.List;

public class SmartCityScheduler {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java SmartCityScheduler <dataset.json>");
            System.out.println("Example: java SmartCityScheduler data/small_cyclic.json");
            return;
        }

        String filename = args[0];

        try {
            System.out.println("=================================================");
            System.out.println("Smart City/Campus Scheduling System");
            System.out.println("=================================================\n");

            System.out.println("Loading dataset: " + filename);
            Graph graph = GraphLoader.loadFromJson(filename);
            TaskGraph taskGraph = GraphLoader.loadTaskGraph(filename);

            System.out.println("Description: " + taskGraph.getDescription());
            System.out.println("Vertices: " + graph.getVertices());
            System.out.println("Edges: " + graph.countEdges());
            System.out.println();

            System.out.println("----- Step 1: SCC Detection (Tarjan) -----");
            SCC scc = new SCC(graph);
            List<List<Integer>> components = scc.findSCCs();

            System.out.println("Number of SCCs: " + scc.getSCCCount());
            System.out.println("\nComponents:");
            for (int i = 0; i < components.size(); i++) {
                System.out.print("SCC " + i + " (size " + components.get(i).size() + "): ");
                List<Integer> component = components.get(i);
                for (int j = 0; j < Math.min(5, component.size()); j++) {
                    int vertex = component.get(j);
                    String taskId = graph.getTaskId(vertex);
                    System.out.print(taskId != null ? taskId : ("V" + vertex));
                    if (j < Math.min(5, component.size()) - 1) System.out.print(", ");
                }
                if (component.size() > 5) System.out.print("...");
                System.out.println();
            }

            System.out.println("\nMetrics:");
            printMetrics(scc.getMetrics());

            System.out.println("----- Step 2: Condensation Graph (DAG of SCCs) -----");
            Graph condensation = scc.buildCondensationGraph();
            System.out.println("Condensation vertices: " + condensation.getVertices());
            System.out.println("Condensation edges: " + condensation.countEdges());
            System.out.println();

            System.out.println("----- Step 3: Topological Sort (Kahn) -----");
            TopologicalSort topoSort = new TopologicalSort(condensation);
            List<Integer> topoOrder = topoSort.sort();

            if (topoOrder != null) {
                System.out.println("Topological order of SCCs: " + topoOrder);
                System.out.println("\nMetrics:");
                printMetrics(topoSort.getMetrics());

                System.out.println("----- Step 4: DAG Shortest/Longest Paths -----");

                if (condensation.getVertices() > 0) {
                    DAGShortestPath dagSP = new DAGShortestPath(condensation);
                    int source = topoOrder.getFirst();

                    System.out.println("\nShortest paths from SCC " + source + ":");
                    DAGShortestPath.ShortestPathResult spResult = dagSP.findShortestPaths(source);
                    if (spResult != null) {
                        for (int i = 0; i < condensation.getVertices(); i++) {
                            if (spResult.distances()[i] != Double.POSITIVE_INFINITY) {
                                System.out.printf("  To SCC %d: %.2f%n", i, spResult.distances()[i]);
                            }
                        }
                        System.out.println("\nShortest Path Metrics:");
                        printMetrics(dagSP.getMetrics());
                    }

                    System.out.println("\nLongest path (Critical Path) from SCC " + source + ":");
                    DAGShortestPath.LongestPathResult lpResult = dagSP.findLongestPath(source);
                    if (lpResult != null) {
                        System.out.println("Critical path: " + lpResult.criticalPath());
                        System.out.printf("Critical path length: %.2f%n", lpResult.criticalPathLength());
                        System.out.println("\nLongest Path Metrics:");
                        printMetrics(dagSP.getMetrics());
                    }
                }
            } else {
                System.out.println("ERROR: Condensation graph contains a cycle (should not happen!)");
            }

            System.out.println("=================================================");
            System.out.println("Analysis Complete");
            System.out.println("=================================================");

        } catch (IOException e) {
            System.err.println("Error loading dataset: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during analysis: " + e.getMessage());
        }
    }

    private static void printMetrics(com.rtyrrx.mst.common.Metrics metrics) {
        System.out.printf("Time: %.3f ms%n", metrics.getElapsedTimeMillis());
        String[] counterNames = {"dfs_visits", "edges_explored", "queue_pushes", "queue_pops", "edges_processed", "relaxations"};
        for (String name : counterNames) {
            long count = metrics.getCounter(name);
            if (count > 0) {
                System.out.println(name + ": " + count);
            }
        }
    }
}
