package com.rtyrrx.mst.common;

public interface Metrics {
    void startTiming();
    void stopTiming();
    long getElapsedTimeNanos();
    double getElapsedTimeMillis();
    long getCounter(String name);
    void incrementCounter(String name);
    void incrementCounter(String name, long value);
    void reset();
}
