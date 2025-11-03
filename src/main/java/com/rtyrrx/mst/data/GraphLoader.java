package com.rtyrrx.mst.data;

import com.google.gson.Gson;
import com.rtyrrx.mst.common.Graph;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GraphLoader {
    private static final Gson gson = new Gson();

    public static Graph loadFromJson(String filename) throws IOException {
        try (FileReader reader = new FileReader(filename)) {
            TaskGraph taskGraph = gson.fromJson(reader, TaskGraph.class);
            return convertToGraph(taskGraph);
        }
    }

    private static Graph convertToGraph(TaskGraph taskGraph) {
        int numVertices = taskGraph.getTasks().size();
        Graph graph = new Graph(numVertices);

        Map<String, Integer> taskIdMap = new HashMap<>();
        for (int i = 0; i < taskGraph.getTasks().size(); i++) {
            TaskGraph.Task task = taskGraph.getTasks().get(i);
            taskIdMap.put(task.getId(), i);
            graph.mapTaskToVertex(task.getId(), i);
        }

        for (TaskGraph.Dependency dep : taskGraph.getDependencies()) {
            int from = taskIdMap.get(dep.getFrom());
            int to = taskIdMap.get(dep.getTo());
            graph.addEdge(from, to, dep.getWeight());
        }

        return graph;
    }

    public static TaskGraph loadTaskGraph(String filename) throws IOException {
        try (FileReader reader = new FileReader(filename)) {
            return gson.fromJson(reader, TaskGraph.class);
        }
    }
}
