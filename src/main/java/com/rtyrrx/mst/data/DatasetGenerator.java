package com.rtyrrx.mst.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DatasetGenerator {
    private static final Random random = new Random(42);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void generateAllDatasets(String outputDir) throws IOException {
        generateDataset(outputDir + "/small_dag.json", 8, 10, false, false,
            "Small DAG with 8 tasks, sparse connections");
        generateDataset(outputDir + "/small_cyclic.json", 7, 12, true, false,
            "Small graph with 7 tasks, contains one cycle");
        generateDataset(outputDir + "/small_mixed.json", 10, 15, true, false,
            "Small mixed graph with 10 tasks, 1-2 cycles");

        generateDataset(outputDir + "/medium_dag.json", 15, 25, false, false,
            "Medium DAG with 15 tasks, moderate density");
        generateDataset(outputDir + "/medium_cyclic.json", 12, 22, true, false,
            "Medium graph with 12 tasks, multiple SCCs");
        generateDataset(outputDir + "/medium_dense.json", 18, 45, true, true,
            "Medium dense graph with 18 tasks, several cycles");

        generateDataset(outputDir + "/large_dag.json", 30, 50, false, false,
            "Large DAG with 30 tasks, sparse");
        generateDataset(outputDir + "/large_cyclic.json", 25, 55, true, false,
            "Large graph with 25 tasks, multiple SCCs for performance testing");
        generateDataset(outputDir + "/large_dense.json", 40, 120, true, true,
            "Large dense graph with 40 tasks, complex structure");
    }

    private static void generateDataset(String filename, int numTasks, int numDeps,
                                       boolean allowCycles, boolean dense, String description)
            throws IOException {
        TaskGraph graph = new TaskGraph();
        graph.setDescription(description);

        String[] taskTypes = {"Clean", "Repair", "Inspect", "Maintain", "Analyze", "Monitor"};
        String[] locations = {"StreetA", "StreetB", "ParkC", "SensorD", "CameraE", "Zone"};

        for (int i = 0; i < numTasks; i++) {
            String taskType = taskTypes[random.nextInt(taskTypes.length)];
            String location = locations[random.nextInt(locations.length)];
            double duration = 1 + random.nextInt(10);

            graph.addTask(new TaskGraph.Task("T" + i, taskType + "_" + location + i, duration));
        }

        Set<String> addedEdges = new HashSet<>();
        int edgesAdded = 0;

        if (allowCycles) {
            int numCycles = dense ? 3 : 1;
            for (int c = 0; c < numCycles && edgesAdded < numDeps; c++) {
                int cycleSize = 2 + random.nextInt(Math.min(5, numTasks / 2));
                int start = random.nextInt(numTasks);

                for (int j = 0; j < cycleSize && edgesAdded < numDeps; j++) {
                    int from = (start + j) % numTasks;
                    int to = (start + j + 1) % numTasks;
                    if (j == cycleSize - 1) to = start;

                    String edgeKey = from + "->" + to;
                    if (!addedEdges.contains(edgeKey)) {
                        double weight = 1 + random.nextInt(5);
                        graph.addDependency(new TaskGraph.Dependency("T" + from, "T" + to, weight));
                        addedEdges.add(edgeKey);
                        edgesAdded++;
                    }
                }
            }
        }

        int attempts = 0;
        int maxAttempts = numDeps * 10;

        while (edgesAdded < numDeps && attempts < maxAttempts) {
            attempts++;
            int from = random.nextInt(numTasks);
            int to = random.nextInt(numTasks);

            if (from == to) continue;

            String edgeKey = from + "->" + to;
            if (addedEdges.contains(edgeKey)) continue;

            if (!allowCycles && from > to) {
                int temp = from;
                from = to;
                to = temp;
                edgeKey = from + "->" + to;
                if (addedEdges.contains(edgeKey)) continue;
            }

            double weight = 1 + random.nextInt(5);
            graph.addDependency(new TaskGraph.Dependency("T" + from, "T" + to, weight));
            addedEdges.add(edgeKey);
            edgesAdded++;
        }

        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(graph, writer);
        }

        System.out.println("Generated: " + filename +
                         " (tasks=" + numTasks + ", deps=" + edgesAdded + ")");
    }

    public static void main(String[] args) {
        try {
            String dataDir = args.length > 0 ? args[0] : "data";
            java.io.File dir = new java.io.File(dataDir);
            if (!dir.exists() && !dir.mkdirs()) {
                System.err.println("Failed to create directory: " + dataDir);
                return;
            }
            generateAllDatasets(dataDir);
            System.out.println("All datasets generated successfully!");
        } catch (IOException e) {
            System.err.println("Error generating datasets: " + e.getMessage());
        }
    }
}
