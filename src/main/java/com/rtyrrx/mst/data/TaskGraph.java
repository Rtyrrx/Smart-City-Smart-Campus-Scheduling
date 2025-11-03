package com.rtyrrx.mst.data;

import java.util.ArrayList;
import java.util.List;

public class TaskGraph {
    private List<Task> tasks;
    private List<Dependency> dependencies;
    private String description;

    public TaskGraph() {
        this.tasks = new ArrayList<>();
        this.dependencies = new ArrayList<>();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void addDependency(Dependency dependency) {
        this.dependencies.add(dependency);
    }

    public static class Task {
        private String id;
        private String name;
        private double duration;

        public Task(String id, String name, double duration) {
            this.id = id;
            this.name = name;
            this.duration = duration;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getDuration() {
            return duration;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }
    }

    public static class Dependency {
        private String from;
        private String to;
        private double weight;

        public Dependency(String from, String to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }
    }
}
