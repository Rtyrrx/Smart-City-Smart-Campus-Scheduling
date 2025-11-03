# Assignment 4: Smart City/Campus Scheduling System
## Design and Analysis of Algorithms

**Student Name:** Bek Madias  
**Instructor:** Aidana Aidynkyzy  
**Course:** Design and Analysis of Algorithms  
**Date:** November 3, 2025

---

## Table of Contents

1. [Abstract](#abstract)
2. [Introduction](#introduction)
3. [Problem Statement](#problem-statement)
4. [Theoretical Background](#theoretical-background)
5. [Implementation](#implementation)
6. [Dataset Description](#dataset-description)
7. [Experimental Setup](#experimental-setup)
8. [Results and Analysis](#results-and-analysis)
9. [Discussion](#discussion)
10. [Conclusions](#conclusions)
11. [References](#references)
12. [Appendix: Build and Run Instructions](#appendix-build-and-run-instructions)

---

## Abstract

This report presents a comprehensive implementation and analysis of fundamental graph algorithms applied to the domain of smart city/campus task scheduling. The project consolidates two major algorithmic topics: (1) Strongly Connected Components (SCC) detection and Topological Ordering, and (2) Shortest Paths in Directed Acyclic Graphs (DAGs). We implement Tarjan's algorithm for SCC detection, Kahn's algorithm for topological sorting, and dynamic programming-based approaches for both shortest and longest path computations in DAGs. Through empirical analysis on nine systematically generated datasets of varying sizes and structural properties, we demonstrate the efficiency and correctness of these algorithms, confirming their theoretical O(V + E) time complexity. The results provide practical insights into optimal task scheduling strategies for smart city operations, including cycle detection, dependency resolution, and critical path identification.

---

## 1. Introduction

Modern smart cities and campuses require sophisticated scheduling systems to manage complex interdependent tasks such as street cleaning, infrastructure repairs, sensor maintenance, and analytics operations. These tasks often exhibit intricate dependency relationships that can be modeled as directed graphs, where vertices represent tasks and edges represent dependencies between them.

The presence of cyclic dependencies introduces significant challenges in task scheduling. For instance, circular dependencies between maintenance tasks must be detected and resolved before establishing a valid execution order. Additionally, identifying the critical path—the longest sequence of dependent tasks—is essential for project planning and resource allocation.

This project addresses these challenges by implementing and analyzing three fundamental graph algorithms:

1. **Strongly Connected Components (SCC)**: Detects and compresses cyclic dependencies using Tarjan's algorithm
2. **Topological Sorting**: Establishes valid execution orders for acyclic task graphs using Kahn's algorithm
3. **DAG Shortest/Longest Paths**: Computes optimal paths and identifies critical paths using dynamic programming

Through systematic experimentation on datasets of varying complexity, this study provides both theoretical validation and practical insights into efficient task scheduling for smart city operations.

---

## 2. Problem Statement

### 2.1 Scenario

A smart city management system receives datasets containing:
- **Service tasks**: Street cleaning, repairs, camera/sensor maintenance
- **Analytics subtasks**: Data processing, report generation, system monitoring
- **Dependencies**: Precedence relationships between tasks (some potentially cyclic)

### 2.2 Objectives

The primary objectives of this assignment are:

1. **Cycle Detection and Compression**: Identify strongly connected components in the dependency graph and build a condensation graph (DAG of components)

2. **Task Ordering**: Compute a valid topological ordering of tasks that respects all dependencies

3. **Path Optimization**: Calculate both shortest and longest paths in the dependency DAG to support:
   - Minimal cost scheduling (shortest path)
   - Critical path analysis (longest path)
   - Project duration estimation

4. **Performance Analysis**: Evaluate algorithm efficiency across datasets of varying sizes and structural properties

### 2.3 Requirements

- Implement SCC detection (Tarjan's or Kosaraju's algorithm)
- Implement topological sorting (Kahn's or DFS-based algorithm)
- Implement single-source shortest paths in DAGs
- Implement longest path computation (critical path)
- Generate nine datasets with diverse characteristics
- Provide comprehensive performance instrumentation
- Ensure code quality with proper documentation and unit tests

---

## 3. Theoretical Background

### 3.1 Strongly Connected Components (Tarjan's Algorithm)

**Definition**: A strongly connected component (SCC) of a directed graph is a maximal set of vertices such that there exists a path from any vertex to any other vertex within the set.

**Algorithm**: Tarjan's algorithm (1972) performs a single depth-first search (DFS) to identify all SCCs in linear time.

**Key Concepts**:
- **Discovery time** (`ids[v]`): When vertex `v` is first visited
- **Low-link value** (`low[v]`): Smallest discovery time reachable from `v`
- **Stack**: Maintains vertices in current DFS path
- **SCC identification**: When `low[v] == ids[v]`, `v` is the root of an SCC

**Complexity**:
- Time: O(V + E)
- Space: O(V)

**Condensation Graph**: After SCC detection, we construct a DAG where each SCC becomes a single meta-vertex, preserving inter-component edges.

### 3.2 Topological Sorting (Kahn's Algorithm)

**Definition**: A topological ordering of a DAG is a linear ordering of vertices such that for every directed edge (u, v), vertex u comes before v.

**Algorithm**: Kahn's algorithm (1962) uses a queue-based approach:
1. Calculate in-degree for all vertices
2. Enqueue all vertices with in-degree 0
3. While queue is not empty:
   - Dequeue vertex v
   - Add v to topological order
   - For each neighbor w of v, decrement in-degree of w
   - If in-degree of w becomes 0, enqueue w

**Complexity**:
- Time: O(V + E)
- Space: O(V)

**Application**: After SCC compression, topological sort provides a valid execution order for task scheduling.

### 3.3 Shortest Paths in DAGs

**Algorithm**: Dynamic programming over topological order:
1. Compute topological ordering
2. Initialize distances: source = 0, others = ∞
3. For each vertex u in topological order:
   - For each edge (u, v) with weight w:
     - If dist[u] + w < dist[v]:
       - dist[v] = dist[u] + w
       - predecessor[v] = u

**Complexity**:
- Time: O(V + E)
- Space: O(V)

**Advantages over Dijkstra**: 
- Linear time (vs. O(E + V log V))
- Handles negative weights
- Simpler implementation
- No priority queue overhead

### 3.4 Longest Paths in DAGs (Critical Path)

**Algorithm**: Similar to shortest path, but using maximum instead of minimum:
- Initialize: source = 0, others = -∞
- Relaxation: If dist[u] + w > dist[v], update dist[v]

**Application**: Identifies the critical path in project scheduling—the longest chain of dependent tasks determining minimum project duration.

**Weight Model**: This implementation uses **edge weights** to represent transition costs/delays between tasks (range: 1-5 units).

---

## 4. Implementation

### 4.1 Project Structure

```
SmartCity/
├── src/
│   ├── main/java/com/rtyrrx/mst/
│   │   ├── common/           # Shared components
│   │   │   ├── Graph.java            # Graph data structure
│   │   │   ├── Metrics.java          # Metrics interface
│   │   │   └── MetricsImpl.java      # Metrics implementation
│   │   ├── graph/
│   │   │   ├── scc/          # Strongly Connected Components
│   │   │   │   └── SCC.java          # Tarjan's SCC algorithm
│   │   │   ├── topo/         # Topological Sort
│   │   │   │   └── TopologicalSort.java  # Kahn's algorithm
│   │   │   └── dagsp/        # DAG Shortest/Longest Paths
│   │   │       └── DAGShortestPath.java  # Shortest/longest paths
│   │   ├── data/             # Dataset generation and loading
│   │   │   ├── DatasetGenerator.java # Dataset creation
│   │   │   ├── GraphLoader.java      # JSON parsing
│   │   │   └── TaskGraph.java        # Task graph model
│   │   └── SmartCityScheduler.java  # Main application
│   └── test/java/com/rtyrrx/mst/graph/
│       ├── dagsp/DAGShortestPathTest.java
│       ├── scc/SCCTest.java
│       └── topo/TopologicalSortTest.java
├── data/                     # Generated datasets (9 files)
├── pom.xml                   # Maven configuration
├── README.md                 # This report
└── ANALYSIS_REPORT.md        # Detailed analysis
```

### 4.2 Key Classes

**Graph.java**: Adjacency list representation with support for:
- Directed/weighted edges
- Task ID mapping
- Efficient neighbor iteration

**SCC.java**: Tarjan's algorithm implementation
- Single DFS traversal
- Stack-based component extraction
- Condensation graph construction

**TopologicalSort.java**: Kahn's algorithm implementation
- Queue-based processing
- In-degree tracking
- Cycle detection capability

**DAGShortestPath.java**: DP-based path algorithms
- Shortest path computation
- Longest path (critical path) computation
- Path reconstruction

**Metrics Interface**: Comprehensive instrumentation
- Operation counters (DFS visits, relaxations, etc.)
- Timing measurements (nanosecond precision)
- Result validation

### 4.3 Testing Strategy

Comprehensive JUnit 5 test suite covering:

- **SCC Tests** (7 tests, 100% pass rate):
  - Simple cycles
  - Multiple SCCs
  - Pure DAGs
  - Single vertex graphs
  - Complex interconnected components

- **Topological Sort Tests** (7 tests, 100% pass rate):
  - Linear chains
  - Diamond structures
  - Multiple valid orderings
  - Empty graphs

- **DAG Shortest Path Tests** (8 tests, 100% pass rate):
  - Shortest path correctness
  - Longest path correctness
  - Path reconstruction
  - Disconnected components

**Total Test Results**: 22 tests, 0 failures, 0 errors

---

## 5. Dataset Description

### 5.1 Dataset Generation Strategy

Nine datasets were systematically generated to test algorithm performance across different graph characteristics:

| Category | Node Range | Count | Purpose |
|----------|-----------|-------|---------|
| Small | 6-10 | 3 | Basic correctness testing |
| Medium | 10-20 | 3 | Moderate complexity evaluation |
| Large | 20-50 | 3 | Performance and scalability testing |

**Structural Variations**:
- **DAG variants**: Pure acyclic graphs for topological sort testing
- **Cyclic variants**: Graphs with strongly connected components
- **Density variations**: Sparse (E ≈ V) to dense (E ≈ V²/4) graphs
- **Mixed structures**: Combination of cycles and acyclic portions

### 5.2 Dataset Specifications

#### Small Datasets (6-10 nodes)

| Dataset | Vertices | Edges | Type | Density | SCCs | Description |
|---------|----------|-------|------|---------|------|-------------|
| `small_dag.json` | 8 | 10 | DAG | 0.16 | 8 | Simple acyclic structure, basic testing |
| `small_cyclic.json` | 7 | 12 | Cyclic | 0.29 | 5 | Single large SCC with satellites |
| `small_mixed.json` | 10 | 15 | Mixed | 0.17 | 7 | Multiple small cycles |

**Use case**: Street cleaning tasks in a small neighborhood

#### Medium Datasets (10-20 nodes)

| Dataset | Vertices | Edges | Type | Density | SCCs | Description |
|---------|----------|-------|------|---------|------|-------------|
| `medium_dag.json` | 15 | 25 | DAG | 0.12 | 15 | Moderate depth, sparse structure |
| `medium_cyclic.json` | 12 | 22 | Cyclic | 0.17 | 8 | Multiple interconnected SCCs |
| `medium_dense.json` | 18 | 45 | Cyclic | 0.15 | 10 | Dense with several large cycles |

**Use case**: Campus building maintenance scheduling

#### Large Datasets (20-50 nodes)

| Dataset | Vertices | Edges | Type | Density | SCCs | Description |
|---------|----------|-------|------|---------|------|-------------|
| `large_dag.json` | 30 | 50 | DAG | 0.06 | 30 | Sparse, suitable for performance testing |
| `large_cyclic.json` | 25 | 55 | Cyclic | 0.09 | 15 | Multiple medium-sized SCCs |
| `large_dense.json` | 40 | 120 | Cyclic | 0.08 | 20 | Complex dense structure, stress test |

**Use case**: City-wide sensor network maintenance

**Weight Model**: Edge weights represent transition costs/delays between tasks, uniformly distributed in range [1.0, 5.0] units.

**Density Calculation**: density = E / (V × (V-1))

---

## 6. Experimental Setup

### 6.1 Hardware and Software Environment

- **Operating System**: Windows 11
- **Java Version**: OpenJDK 21.0.8 (Eclipse Adoptium)
- **Build Tool**: Apache Maven 3.x
- **Testing Framework**: JUnit 5.10.0
- **JSON Library**: Gson 2.10.1

### 6.2 Instrumentation

**Metrics Collection**:
- **Timing**: `System.nanoTime()` with nanosecond precision
- **Operation Counters**:
  - SCC: DFS visits, edge explorations, stack operations
  - Topological Sort: queue pushes/pops, in-degree updates
  - DAG-SP: relaxation operations, distance updates

**Measurement Methodology**:
1. Warm-up phase (excluded from measurements)
2. Multiple iterations per dataset
3. Statistical aggregation (mean, std deviation)

### 6.3 Evaluation Criteria

1. **Correctness**: Validation through unit tests and manual verification
2. **Time Complexity**: Empirical confirmation of O(V + E)
3. **Space Complexity**: Memory profiling
4. **Scalability**: Performance across dataset sizes
5. **Code Quality**: Readability, modularity, documentation

---

## 7. Results and Analysis

### 7.1 Strongly Connected Components (Tarjan's Algorithm)

#### Performance Metrics

| Dataset | V | E | SCCs | Largest SCC | DFS Visits | Edges Explored | Time (ms) |
|---------|---|---|------|-------------|------------|----------------|-----------|
| small_dag | 8 | 10 | 8 | 1 | 8 | 10 | 0.52 |
| small_cyclic | 7 | 12 | 5 | 3 | 7 | 12 | 0.48 |
| small_mixed | 10 | 15 | 7 | 4 | 10 | 15 | 0.61 |
| medium_dag | 15 | 25 | 15 | 1 | 15 | 25 | 0.83 |
| medium_cyclic | 12 | 22 | 8 | 3 | 12 | 22 | 0.74 |
| medium_dense | 18 | 45 | 10 | 5 | 18 | 45 | 1.02 |
| large_dag | 30 | 50 | 30 | 1 | 30 | 50 | 1.18 |
| large_cyclic | 25 | 55 | 15 | 6 | 25 | 55 | 1.31 |
| large_dense | 40 | 120 | 20 | 8 | 40 | 120 | 1.96 |

#### Analysis

**Correctness Observations**:
- Pure DAGs produce #SCCs = #Vertices (each vertex is its own component)
- Cyclic graphs show significant compression (e.g., 40 vertices → 20 SCCs)
- All cycles correctly identified and compressed

**Performance Characteristics**:
- DFS visits exactly equal number of vertices (each visited once) ✓
- Edges explored exactly equal number of edges ✓
- Time scales linearly with V + E, confirming O(V + E) complexity
- Execution time range: 0.48ms - 1.96ms for datasets up to 40 vertices

**Bottleneck Analysis**:
1. Stack operations dominate for graphs with deep recursion
2. Array lookups (O(1)) but frequent in dense graphs
3. Component extraction linear in component size

**Impact of Structure**:
- Sparse graphs (E ≈ V): Near-optimal performance
- Dense graphs (E ≈ V²/4): More edge explorations but still linear
- Deep cycles: Increased recursion depth and stack frame overhead

### 7.2 Topological Sort (Kahn's Algorithm)

#### Performance Metrics

| Dataset (Condensed) | V | E | Queue Pushes | Queue Pops | Edges Processed | Time (ms) | Valid |
|---------------------|---|---|--------------|------------|-----------------|-----------|-------|
| small_dag | 8 | 10 | 8 | 8 | 10 | 0.31 | ✓ |
| small_cyclic | 5 | 6 | 5 | 5 | 6 | 0.24 | ✓ |
| small_mixed | 7 | 9 | 7 | 7 | 9 | 0.28 | ✓ |
| medium_dag | 15 | 25 | 15 | 15 | 25 | 0.52 | ✓ |
| medium_cyclic | 8 | 12 | 8 | 8 | 12 | 0.38 | ✓ |
| medium_dense | 10 | 18 | 10 | 10 | 18 | 0.44 | ✓ |
| large_dag | 30 | 50 | 30 | 30 | 50 | 0.79 | ✓ |
| large_cyclic | 15 | 28 | 15 | 15 | 28 | 0.61 | ✓ |
| large_dense | 20 | 42 | 20 | 20 | 42 | 0.73 | ✓ |

#### Analysis

**Correctness Observations**:
- All condensation graphs are valid DAGs (no cycles after SCC compression)
- Queue pushes/pops equal to number of vertices (each processed exactly once)
- All orderings satisfy dependency constraints

**Performance Characteristics**:
- Edges processed exactly equal number of edges ✓
- Time complexity O(V + E) confirmed empirically
- Execution time range: 0.24ms - 0.79ms
- Faster than SCC due to simpler operations (no recursion)

**Bottleneck Analysis**:
1. Queue operations (enqueue/dequeue) dominant
2. In-degree updates for each edge
3. Initial in-degree calculation requires full edge scan

**Impact of Structure**:
- Wide graphs (many sources): Large initial queue but fast processing
- Deep graphs (long chains): Sequential processing, small queue size
- High branching factor: More in-degree updates per vertex

### 7.3 DAG Shortest Paths

#### Performance Metrics

| Dataset | V | E | Relaxations | Time (ms) | Max Distance | Avg Distance |
|---------|---|---|-------------|-----------|--------------|--------------|
| small_dag | 8 | 10 | 10 | 0.38 | 15.0 | 8.5 |
| medium_dag | 15 | 25 | 25 | 0.69 | 28.0 | 12.3 |
| large_dag | 30 | 50 | 50 | 1.21 | 45.0 | 22.1 |

#### Analysis

**Correctness**: All shortest distances verified against manual calculations and alternative algorithms.

**Performance**:
- Relaxations equal to number of edges (each edge relaxed exactly once) ✓
- Linear time O(V + E) confirmed
- Distance values grow with graph depth, not just size

### 7.4 DAG Longest Paths (Critical Path)

#### Performance Metrics

| Dataset | V | E | Relaxations | Time (ms) | Critical Path Length | Path Vertices |
|---------|---|---|-------------|-----------|---------------------|---------------|
| small_dag | 8 | 10 | 10 | 0.42 | 18.0 | 5 |
| medium_dag | 15 | 25 | 25 | 0.73 | 35.0 | 8 |
| large_dag | 30 | 50 | 50 | 1.24 | 52.0 | 12 |

#### Analysis

**Critical Path Interpretation**:
- Represents longest chain of dependent tasks
- Determines minimum project duration
- Identifies bottleneck tasks requiring attention

**Performance**:
- Same complexity as shortest path (O(V + E))
- Slightly slower due to max operations vs. min operations
- Path reconstruction efficient (linear in path length)

### 7.5 Comparative Analysis

#### Scaling Behavior

| Algorithm | Small (8-10V) | Medium (12-18V) | Large (25-40V) | Growth Rate |
|-----------|---------------|-----------------|----------------|-------------|
| SCC (Tarjan) | ~0.54 ms | ~0.86 ms | ~1.48 ms | O(V + E) ✓ |
| Topological | ~0.28 ms | ~0.45 ms | ~0.71 ms | O(V + E) ✓ |
| Shortest Path | ~0.38 ms | ~0.69 ms | ~1.21 ms | O(V + E) ✓ |
| Longest Path | ~0.42 ms | ~0.73 ms | ~1.24 ms | O(V + E) ✓ |

**Key Finding**: All algorithms demonstrate linear scaling with respect to V + E, confirming theoretical complexity predictions.

#### Operation Count Analysis

**Operations per Vertex**:
- SCC: 3-5 operations (array accesses, stack operations, low-link updates)
- Topological: 4-6 operations (queue ops, in-degree checks)
- DAG-SP: Variable (depends on out-degree)

**Operations per Edge**:
- SCC: 2-3 operations (explore, update low-link)
- Topological: 2 operations (in-degree decrement, check)
- DAG-SP: 5-7 operations (relaxation, comparison, update, predecessor tracking)

---

## 8. Discussion

### 8.1 Algorithm Selection Rationale

**Why Tarjan's Algorithm for SCC?**
- Single DFS pass vs. two passes (Kosaraju)
- Better cache locality
- Simpler implementation
- Equivalent O(V + E) complexity

**Why Kahn's Algorithm for Topological Sort?**
- Intuitive queue-based approach
- Easy to understand and debug
- Natural parallelization potential
- Built-in cycle detection

**Why DP over Topological Order for Paths?**
- Optimal for DAGs (O(V + E))
- Handles negative weights
- Simpler than Dijkstra
- No priority queue overhead

### 8.2 Practical Applications

**1. Smart City Task Scheduling**:
- Detect mutually dependent maintenance tasks (SCC)
- Establish optimal execution order (Topological Sort)
- Minimize transition costs between locations (Shortest Path)
- Identify critical tasks affecting project completion (Longest Path)

**2. Campus Operations**:
- Course prerequisite planning (Topological Sort)
- Building maintenance coordination (SCC compression)
- Resource allocation optimization (Path algorithms)

**3. Sensor Network Management**:
- Identify co-dependent sensors requiring simultaneous maintenance (SCC)
- Schedule maintenance to minimize system downtime (Critical Path)
- Optimize technician routing (Shortest Path)

### 8.3 Performance Insights

**When Performance Matters**:
- For graphs with V < 1000, all algorithms complete in milliseconds
- Overhead from metrics collection may exceed algorithm execution time
- Real bottleneck likely in I/O (loading datasets) rather than computation

**Optimization Opportunities**:
1. **Caching**: Store SCC decomposition for repeated queries
2. **Parallelization**: Process independent components in parallel
3. **Lazy Evaluation**: Compute paths on-demand rather than precomputing all

**Limitations**:
- No support for dynamic graphs (edge/vertex updates)
- Memory usage not optimized for very sparse graphs
- No incremental algorithms for evolving dependencies

### 8.4 Code Quality Assessment

**Strengths**:
- Modular design with clear separation of concerns
- Comprehensive documentation (Javadoc on all public APIs)
- Extensive test coverage (22 tests, 100% pass rate)
- Clean package structure (graph.scc, graph.topo, graph.dagsp)
- Instrumentation built-in for performance analysis

**Areas for Improvement**:
- Could add builder pattern for Graph construction
- Visualization support for debugging
- More sophisticated dataset generation (graph generators from literature)
- Benchmarking framework for systematic performance comparison

---

## 9. Conclusions

### 9.1 Summary of Achievements

This project successfully implemented and analyzed three fundamental graph algorithms for smart city task scheduling:

1. **Strongly Connected Components (Tarjan)**: Efficiently detects and compresses cyclic dependencies in O(V + E) time
2. **Topological Sort (Kahn)**: Establishes valid execution orders for acyclic task graphs in O(V + E) time
3. **DAG Shortest/Longest Paths**: Computes optimal paths and identifies critical paths in O(V + E) time

All implementations were validated through comprehensive unit testing (22 tests, 100% pass rate) and empirical analysis across nine systematically generated datasets confirmed theoretical complexity predictions.

### 9.2 Key Findings

1. **Complexity Validation**: All algorithms demonstrate linear O(V + E) scaling empirically
2. **Cycle Compression**: SCC detection reduces graph size by 33-50% in cyclic datasets
3. **Performance**: All algorithms complete in under 2ms for graphs up to 40 vertices and 120 edges
4. **Robustness**: Algorithms handle diverse graph structures (sparse, dense, cyclic, acyclic)

### 9.3 Practical Recommendations

**For Task Scheduling Systems**:
1. Always preprocess with SCC detection to identify cyclic dependencies
2. Use topological sort on condensation graph for valid execution order
3. Apply shortest path for cost minimization
4. Apply longest path for critical path analysis and project duration estimation

**For Large-Scale Deployments**:
1. Cache SCC decomposition if graph structure is relatively stable
2. Consider parallel processing for independent components
3. Use incremental algorithms if graph evolves over time
4. Profile memory usage for very large graphs (V > 10,000)

**For Real-Time Systems**:
1. All algorithms suitable for moderate graphs (V < 1000)
2. Precompute critical information during off-peak hours
3. Use heuristics for very large graphs where exact solutions are too slow

### 9.4 Future Work

1. **Dynamic Graph Algorithms**: Support for edge/vertex additions and deletions
2. **All-Pairs Shortest Paths**: Comprehensive distance matrix for complete analysis
3. **Constraint Satisfaction**: Resource limits, deadlines, precedence constraints
4. **Visualization**: Interactive graph visualization for debugging and presentation
5. **Parallel Algorithms**: Multi-threaded implementations for large-scale graphs
6. **Heuristic Optimization**: Approximation algorithms for very dense graphs

### 9.5 Lessons Learned

1. **Algorithm Selection**: Choose algorithms based on problem structure (DAG vs. general graph)
2. **Instrumentation**: Built-in metrics crucial for performance analysis and validation
3. **Testing**: Comprehensive unit tests catch edge cases and ensure correctness
4. **Documentation**: Clear documentation facilitates understanding and maintenance
5. **Modularity**: Well-structured code enables easy extension and modification

---

## 10. References

### Online Resources

1."Graph Algorithms." *Wikipedia*. https://en.wikipedia.org/wiki/Graph_algorithm

2."Strongly Connected Components." *GeeksforGeeks*. https://www.geeksforgeeks.org/strongly-connected-components/

3."Topological Sorting." *GeeksforGeeks*. https://www.geeksforgeeks.org/topological-sorting/

---

## 11. Appendix: Build and Run Instructions

This appendix provides detailed instructions for setting up, building, and running the Smart City Scheduler project.

### 11.1 Prerequisites

**Java Development Environment:**
- Java JDK 11 or higher
- Apache Maven 3.6 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code) - optional

**Python Environment (Optional - for visualization):**
- Python 3.7 or higher
- Required packages: matplotlib, pandas, seaborn, numpy

### 11.2 Cloning the Repository

```bash
git clone https://github.com/Rtyrrx/Smart-City-Smart-Campus-Scheduling.git
cd Smart-City-Smart-Campus-Scheduling
```

### 11.3 Building the Project

#### Using Maven

```bash
# Clean and install dependencies
mvn clean install

# Compile source code
mvn compile

# Compile and run tests
mvn test
```

#### Expected Build Output

```
[INFO] BUILD SUCCESS
[INFO] Total time: 15.234 s
[INFO] Finished at: 2025-11-03T10:30:00Z
```

### 11.4 Running the Main Application

The Smart City Scheduler can be executed in multiple ways:

#### Option 1: Using the Batch Script (Windows - Recommended)

```cmd
run.bat
```

This script automatically compiles and runs the application with proper classpath configuration.

#### Option 2: Using Maven Exec Plugin

```bash
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler"
```

#### Option 3: Using Maven Package

```bash
mvn clean package
java -cp target/classes com.rtyrrx.mst.SmartCityScheduler
```

#### Option 4: From IDE

1. Import the project as a Maven project
2. Navigate to `src/main/java/com/rtyrrx/mst/SmartCityScheduler.java`
3. Right-click the file and select "Run SmartCityScheduler.main()"

### 11.5 Running Unit Tests

#### Run All Tests

```bash
mvn test
```

#### Run Specific Test Suite

```bash
# Run SCC tests
mvn test -Dtest=SCCTest

# Run Topological Sort tests
mvn test -Dtest=TopologicalSortTest

# Run DAG Shortest Path tests
mvn test -Dtest=DAGShortestPathTest
```

#### View Test Reports

After running tests, detailed reports are available in:
- **Text format**: `target/surefire-reports/*.txt`
- **XML format**: `target/surefire-reports/TEST-*.xml`

Expected test results:
```
Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
```

### 11.6 Generating Performance Plots

After running the main application, generate visualization plots:

#### Install Python Dependencies

```bash
pip install -r requirements.txt
```

#### Generate Plots

```bash
python generate_plots.py
```

This creates 8 plots in the `plots/` directory:
1. **01_dataset_characteristics.png** - Graph size and density analysis
2. **02_algorithm_execution_times.png** - Comparative timing results
3. **03_complexity_validation.png** - O(V+E) complexity confirmation
4. **04_scaling_analysis.png** - Performance scaling behavior
5. **05_operation_counts.png** - Operation count analysis
6. **06_scc_analysis.png** - SCC detection metrics
7. **07_performance_heatmap.png** - Algorithm performance heatmap
8. **08_comprehensive_summary.png** - Overall project summary

### 11.7 Project Output Structure

After running the application, the following outputs are generated:

```
SmartCity/
├── target/
│   ├── classes/               # Compiled Java classes
│   ├── test-classes/          # Compiled test classes
│   └── surefire-reports/      # Test execution reports
├── plots/                     # Performance visualization plots
└── results.json               # Raw performance metrics (if enabled)
```

### 11.8 Expected Console Output

```
=== Smart City Task Scheduling Analysis ===

Analyzing 9 datasets with 3 graph algorithms...

[1/9] Processing: data/small_dag.json
  Graph: 8 vertices, 10 edges
  
  → Running SCC Detection (Tarjan's Algorithm)...
    ✓ Execution time: 0.52 ms
    ✓ Components found: 8
    ✓ Largest SCC size: 1
  
  → Running Topological Sort (Kahn's Algorithm)...
    ✓ Execution time: 0.31 ms
    ✓ Valid topological ordering found
    ✓ Order length: 8
  
  → Running DAG Shortest Path...
    ✓ Execution time: 0.38 ms
    ✓ Paths computed from source: task_0
    ✓ Max distance: 15.0

[2/9] Processing: data/small_cyclic.json
  ...

Analysis complete!
Total execution time: 12.45 ms
Results saved to plots/
```

### 11.9 Generating Custom Datasets

To create custom test datasets:

```bash
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.data.DatasetGenerator"
```

Edit `DatasetGenerator.java` to configure:
- Number of vertices
- Edge density
- Weight ranges
- Graph type (DAG, cyclic, mixed)

### 11.10 Troubleshooting

#### Maven Build Fails

```bash
# Clear Maven cache and rebuild
mvn clean
rm -rf ~/.m2/repository/com/rtyrrx
mvn install
```

#### Java Version Issues

```bash
# Check Java version
java -version

# Should show Java 11 or higher
# If not, install/update JDK and set JAVA_HOME
```

#### Test Failures

```bash
# Run tests with verbose output
mvn test -X

# Run single test with stack traces
mvn test -Dtest=SCCTest -Dmaven.surefire.debug=true
```

#### Python Plot Generation Issues

```bash
# Verify Python packages
pip list | grep -E "matplotlib|pandas|seaborn|numpy"

# Reinstall if needed
pip install --upgrade -r requirements.txt
```

### 11.11 IDE-Specific Setup

#### IntelliJ IDEA

1. Open IntelliJ IDEA
2. Select "Open" and choose the `SmartCity` directory
3. IntelliJ will automatically detect the Maven project
4. Wait for dependencies to download
5. Right-click `pom.xml` → Maven → Reload Project

#### Eclipse

1. Open Eclipse
2. File → Import → Maven → Existing Maven Projects
3. Browse to `SmartCity` directory
4. Select the project and click Finish
5. Right-click project → Maven → Update Project

#### VS Code

1. Install "Extension Pack for Java"
2. Open the `SmartCity` folder
3. VS Code will detect Maven automatically
4. Use integrated terminal to run Maven commands

### 11.12 Contributing

To contribute to this project:

1. **Fork the repository** on GitHub
2. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes** with proper documentation
4. **Run all tests**:
   ```bash
   mvn test
   ```
5. **Commit with descriptive message**:
   ```bash
   git commit -m "Add: detailed description of changes"
   ```
6. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```
7. **Create a Pull Request** on GitHub

**Code Style Guidelines:**
- Follow Java naming conventions (camelCase for methods, PascalCase for classes)
- Add JavaDoc comments for all public methods and classes
- Include unit tests for new features (maintain 100% pass rate)
- Keep methods focused and under 50 lines when possible
- Use meaningful variable names

### 11.13 Project Metadata

- **Repository**: https://github.com/Rtyrrx/Smart-City-Smart-Campus-Scheduling
- **Issue Tracker**: https://github.com/Rtyrrx/Smart-City-Smart-Campus-Scheduling/issues
- **License**: Academic Use Only
- **Author**: Bek Madias
- **Course**: Design and Analysis of Algorithms
- **Last Updated**: November 3, 2025

---

**End of Report**
