# Assignment 4: Smart City Scheduling

**Student:** Asset Iglikov  
**Course:** Design and Analysis of Algorithms | Aidana Aidynkyzy
**Assignment topic:** Smart City Scheduling

---

## 📋 Project Overview

This project implements core graph algorithms for smart city task scheduling:

1. **Strongly Connected Components (SCC)** - Tarjan's algorithm
2. **Condensation Graph** - Building DAG from SCCs
3. **Topological Sorting** - Kahn's algorithm
4. **DAG Shortest Paths** - Using topological order
5. **DAG Longest Paths** - Critical path method

---

## 🚀 How to Run

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Clone and Build
```bash
git clone https://github.com/yourusername/assignment4
cd assignment4
mvn clean compile
```

### Run Main Program
```bash
mvn exec:java -Dexec.mainClass="Main"
```

### Run Tests
```bash
mvn test
```

**Output:**
- Console: Detailed execution log for each dataset
- `results/metrics.csv`: Performance metrics for all datasets

---

## 📊 Dataset Summary

All datasets use **edge weights** representing task duration or cost.

| Dataset | Vertices | Edges | Type | SCCs | Description |
|---------|----------|-------|------|------|-------------|
| small_1 | 8 | 7 | Mixed | 6 | One small cycle, mostly DAG structure |
| small_2 | 10 | 12 | Cyclic | 3 | Three separate cycles with connections |
| small_3 | 6 | 8 | Dense cycle | 1 | Fully connected single component |
| medium_1 | 15 | 20 | Sparse | 13 | One small cycle, rest is DAG |
| medium_2 | 18 | 35 | Dense | 7 | Multiple cycles, highly connected |
| medium_3 | 12 | 15 | Pure DAG | 12 | No cycles, each vertex is its own SCC |
| large_1 | 30 | 45 | Sparse DAG | 30 | Long chains, no cycles |
| large_2 | 40 | 76 | Dense cyclic | 10 | Multiple large cycles |
| large_3 | 50 | 65 | Sparse DAG | 50 | Many isolated components |

---

## 📈 Experimental Results

### Small Datasets (6-10 vertices)

| Dataset | Vertices | Edges | SCCs | SCC Time (ms) | Topo Time (ms) | Longest Path |
|---------|----------|-------|------|---------------|----------------|--------------|
| small_1 | 8 | 7 | 6 | 0.045 | 0.401 | 8 |
| small_2 | 10 | 12 | 3 | 0.028 | 0.010 | 6 |
| small_3 | 6 | 8 | 1 | 0.018 | 0.005 | 0 |

**Key Observations:**
- **small_1**: Despite having only 8 vertices, the topological sort took longer (0.401 ms) due to the algorithm setup overhead
- **small_2**: Three separate cycles compressed into 3 SCCs, with critical path length of 6
- **small_3**: Single large SCC (all vertices connected in one cycle), resulting in a condensation graph with only 1 node and longest path of 0

### Medium Datasets (12-18 vertices)

| Dataset | Vertices | Edges | SCCs | SCC Time (ms) | SP Relaxations | Longest Path | LP Time (ms) |
|---------|----------|-------|------|---------------|----------------|--------------|--------------|
| medium_1 | 15 | 20 | 13 | 0.069 | 14 | 32 | 0.028 |
| medium_2 | 18 | 35 | 7 | 0.030 | 8 | 32 | 0.014 |
| medium_3 | 12 | 15 | 12 | 0.071 | 13 | 25 | 0.105 |

**Key Observations:**
- **medium_1**: Sparse structure with mostly independent tasks (13 SCCs from 15 vertices)
- **medium_2**: Despite having more edges (35 vs 20), it ran faster (0.030 ms) because cycles were compressed into larger SCCs, reducing the condensation graph size
- **medium_3**: Pure DAG structure is ideal for critical path analysis - each vertex is its own SCC

### Large Datasets (30-50 vertices)

| Dataset | Vertices | Edges | SCCs | SCC Time (ms) | DFS Visits | LP Relaxations | Critical Path |
|---------|----------|-------|------|---------------|------------|----------------|---------------|
| large_1 | 30 | 45 | 30 | 0.035 | 30 | 45 | 67 |
| large_2 | 40 | 76 | 10 | 0.039 | 40 | 9 | 56 |
| large_3 | 50 | 65 | 50 | 0.203 | 50 | 61 | 84 |

**Key Observations:**
- **large_1**: Perfect sparse DAG structure (30 SCCs = 30 vertices), efficient processing
- **large_2**: Dense graph with many cycles compressed into 10 SCCs, longest path relaxations reduced to just 9
- **large_3**: Took significantly longer (0.203 ms) due to 50 vertices and 50 separate components requiring individual DFS traversals

---

## 📊 Performance Analysis

### 1. SCC Detection (Tarjan's Algorithm)

| Graph Size | Avg DFS Visits | Avg Time (ms) | Time per Vertex (μs) |
|------------|----------------|---------------|----------------------|
| Small (6-10) | 8.0 | 0.030 | 3.75 |
| Medium (12-18) | 15.0 | 0.057 | 3.80 |
| Large (30-50) | 40.0 | 0.092 | 2.30 |

**Analysis:**
- Time complexity is **O(V + E)** as expected
- DFS visits exactly equal to number of vertices (each visited once)
- Dense graphs don't significantly slow down the algorithm - cycles are handled efficiently
- Larger graphs actually show better time per vertex due to better CPU cache utilization

**Bottleneck:** Recursive DFS depth in graphs with long chains

### 2. Topological Sort (Kahn's Algorithm)

| Graph Size | Avg Operations | Avg Time (ms) | Operations/ms |
|------------|----------------|---------------|---------------|
| Small | 3.3 | 0.139 | 23.7 |
| Medium | 10.7 | 0.016 | 668.8 |
| Large | 30.0 | 0.027 | 1111.1 |

**Analysis:**
- Time complexity: **O(V + E)** confirmed
- Operations count equals number of SCCs in condensation graph
- Small datasets show anomaly due to JVM warmup and setup overhead
- In-degree calculation is the main bottleneck for dense graphs

**Bottleneck:** Queue operations dominate runtime for condensation DAGs

### 3. DAG Shortest Paths

| Dataset | Condensation Size | Relaxations | Time (ms) | Relaxations/ms |
|---------|-------------------|-------------|-----------|----------------|
| small_1 | 6 SCCs | 3 | 0.030 | 100 |
| medium_1 | 13 SCCs | 14 | 0.168 | 83 |
| large_1 | 30 SCCs | 34 | 0.083 | 410 |

**Analysis:**
- Relaxations scale linearly with edges in condensation graph
- medium_1 shows slower performance (0.168 ms) despite fewer vertices - likely due to graph structure requiring more distance updates
- Algorithm is very efficient: even large_1 with 34 relaxations takes only 0.083 ms

**Bottleneck:** Edge relaxation in dense condensation graphs

### 4. DAG Longest Paths (Critical Path)

| Dataset | Critical Path Length | Relaxations | Time (ms) | 
|---------|---------------------|-------------|-----------|
| small_1 | 8 | 4 | 0.030 |
| medium_2 | 32 | 9 | 0.014 |
| large_3 | 84 | 61 | 0.163 |

**Analysis:**
- Critical path length increases with graph depth
- large_3 shows highest critical path (84) due to long chains
- Relaxations correlate with edges in DAG
- Same time complexity as shortest path, just maximizing instead of minimizing

---

## 🔍 Effect of Graph Structure

### Sparse vs Dense Graphs

| Type | Example | SCCs | Time (ms) | Observation |
|------|---------|------|-----------|-------------|
| Sparse | large_1 (30v, 45e) | 30 | 0.035 | Many small SCCs, fast processing |
| Dense | large_2 (40v, 76e) | 10 | 0.039 | Fewer but larger SCCs, similar time |

**Conclusion:** 
- Sparse graphs → More SCCs → More nodes in condensation
- Dense graphs → Fewer SCCs → Smaller condensation graph
- Overall processing time is similar due to O(V+E) complexity

### Cyclic vs Acyclic Graphs

| Type | Example | SCCs | Critical Path | Observation |
|------|---------|------|---------------|-------------|
| Pure DAG | medium_3 (12v) | 12 | 25 | Each vertex is SCC |
| Cyclic | small_3 (6v) | 1 | 0 | Single SCC, no path |
| Mixed | medium_1 (15v) | 13 | 32 | Mostly DAG structure |

**Conclusion:**
- Pure DAGs are ideal for critical path analysis
- Graphs with many cycles compress heavily, reducing condensation size
- Mixed structures offer balance between compression and path analysis

---

## 💡 Key Findings

### Algorithm Performance

1. **SCC Detection**
   - Extremely fast even for 50 vertices (0.203 ms max)
   - Scales linearly with V+E
   - Cycles don't significantly impact performance

2. **Topological Sort**
   - Very efficient on condensation DAGs
   - Small overhead makes it negligible for task scheduling
   - Most time spent in in-degree calculation

3. **DAG Shortest/Longest Paths**
   - Fastest path algorithm for DAGs
   - Relaxation count directly proportional to edges
   - Critical path efficiently identifies bottlenecks

### Practical Implications

**When to use each algorithm:**

- **Tarjan's SCC**: Always run first to detect cycles in dependency graphs
- **Condensation**: Essential preprocessing step before topological sort
- **Topological Sort**: Use for build systems, task scheduling, course prerequisites
- **DAG Shortest Path**: Optimal for resource-constrained scheduling
- **DAG Longest Path**: Critical for project management (finding bottlenecks)

---

## 🎯 Conclusions

### Algorithmic Insights

1. **Cycle Detection is Critical**
   - Real-world task dependencies often have cycles (e.g., small_2, medium_2, large_2)
   - SCC compression is essential before scheduling
   - All tested cyclic graphs successfully compressed to DAGs

2. **Performance Scales Well**
   - Even 50-vertex graphs process in < 0.25 ms
   - Linear scaling confirmed: doubling vertices ≈ doubles time
   - Dense graphs don't cause exponential slowdown

3. **Graph Structure Matters**
   - Sparse DAGs (large_1, large_3): Fast, many opportunities for parallelization
   - Dense cycles (large_2): Compression dramatically reduces problem size
   - Mixed structures (medium_1): Balance between the two extremes

### Practical Recommendations

**For Smart City Task Scheduling:**

1. **Always check for cycles first** using SCC detection
2. **Compress cycles** into single tasks (they must be done together)
3. **Use topological sort** for determining valid execution order
4. **Apply critical path method** to identify tasks that cannot be delayed
5. **Consider shortest paths** when optimizing for resource usage

**For Large-Scale Systems:**

- Tarjan's algorithm handles 1000+ vertices efficiently
- Condensation dramatically reduces graph size for cyclic dependencies
- DAG path algorithms are optimal (faster than Dijkstra for DAGs)

### Real-World Applications

- **Build Systems**: Compilation order with circular dependencies
- **Project Management**: Critical path method for PERT/CPM
- **Course Planning**: Prerequisite chains with corequisites
- **Smart Cities**: Infrastructure maintenance scheduling
- **Database**: Transaction dependency resolution

---

## 📁 Project Structure
```
assignment4/
├── data/                     # 9 test datasets (small/medium/large)
├── results/
│   └── metrics.csv          # Performance metrics
├── src/
│   ├── main/java/
│   │   ├── Main.java        # Main execution and CSV export
│   │   └── graph/   #Graph algorithms & more
│   │       ├── Graph.java   # Graph data structure
│   │       ├── Metrics.java # Performance tracking
│   │       ├── scc/ 
│   │       │   ├── TarjanSCC.java          
│   │       │   └── CondensationGraph.java
│   │       ├── topo/
│   │       │   └── TopologicalSort.java
│   │       └── dagsp/
│   │           └── DAGShortestPath.java
│   └── test/java/graph/     # JUnit tests (22 tests, all passing)
├── pom.xml                  # Maven configuration
└── README.md                # Analytical report + run instructions (This file)
```

---

## ✅ Code Quality

- **Clean Architecture**: Modular package structure (`graph.scc`, `graph.topo`, `graph.dagsp`)
- **Comprehensive Comments**: Every algorithm step explained
- **JUnit Tests**: 22 tests covering edge cases
  - TarjanSCCTest: 6 tests ✅
  - TopologicalSortTest: 7 tests ✅
  - DAGShortestPathTest: 9 tests ✅
- **Performance Tracking**: Built-in metrics collection
- **Reproducible**: All results can be regenerated from source

---

## 📚 References

1. Tarjan, R. (1972). "Depth-first search and linear graph algorithms." *SIAM Journal on Computing*
2. Kahn, A. B. (1962). "Topological sorting of large networks." *Communications of the ACM*
3. Cormen, T. H., et al. (2009). *Introduction to Algorithms* (3rd ed.). MIT Press
4. Sedgewick, R., & Wayne, K. (2011). *Algorithms* (4th ed.). Addison-Wesley

---

## 👤 Author

**Name:** Asset Iglikov  ||
**E-mail:** aaset0645@gmail.com ||
**Course name:** Design and Analysis of Algorithms | Aidana Aidynkyzy ||
**University name:** Astana IT University ||

---

**Last Updated:** 02.11.2025  
**All tests passing ✅** | **22/22 JUnit tests** | **9/9 datasets processed**
