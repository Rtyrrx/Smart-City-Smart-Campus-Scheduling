package com.rtyrrx.mst.common;

import java.util.HashMap;
import java.util.Map;

public class MetricsImpl implements Metrics {
    private long startTime;
    private long endTime;
    private final Map<String, Long> counters;

    public MetricsImpl() {
        this.counters = new HashMap<>();
    }

    @Override
    public void startTiming() {
        startTime = System.nanoTime();
    }

    @Override
    public void stopTiming() {
        endTime = System.nanoTime();
    }

    @Override
    public long getElapsedTimeNanos() {
        return endTime - startTime;
    }

    @Override
    public double getElapsedTimeMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    @Override
    public long getCounter(String name) {
        return counters.getOrDefault(name, 0L);
    }

    @Override
    public void incrementCounter(String name) {
        incrementCounter(name, 1);
    }

    @Override
    public void incrementCounter(String name, long value) {
        counters.put(name, counters.getOrDefault(name, 0L) + value);
    }

    @Override
    public void reset() {
        startTime = 0;
        endTime = 0;
        counters.clear();
    }
}
