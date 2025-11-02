package graph.topo;

import graph.Graph;
import graph.Metrics;
import java.util.*;

/**
 * Topological sorting using Kahn's algorithm
 */
public class TopologicalSort {

    /**
     * Perform topological sort on a DAG
     * Returns null if graph has a cycle
     */
    public List<Integer> sort(Graph g, Metrics m) {
        int n = g.getN();
        int[] inDegree = new int[n];

        m.start();

        // Calculate in-degree for each vertex
        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : g.getNeighbors(u)) {
                inDegree[edge.to]++;
            }
        }

        // Queue for vertices with in-degree 0
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> result = new ArrayList<>();

        // Process vertices
        while (!queue.isEmpty()) {
            int u = queue.poll();
            result.add(u);
            m.incrementOperations();

            // Reduce in-degree for neighbors
            for (Graph.Edge edge : g.getNeighbors(u)) {
                int v = edge.to;
                inDegree[v]--;

                if (inDegree[v] == 0) {
                    queue.add(v);
                }
            }
        }

        m.stop();

        // Check if all vertices are included (no cycle)
        if (result.size() != n) {
            return null; // Graph has cycle
        }

        return result;
    }

    /**
     * Get topological order for original vertices based on SCC order
     */
    public List<Integer> expandToOriginal(List<Integer> sccOrder,
                                          List<List<Integer>> sccs) {
        List<Integer> result = new ArrayList<>();

        // For each SCC in topological order, add all its vertices
        for (int sccIdx : sccOrder) {
            result.addAll(sccs.get(sccIdx));
        }

        return result;
    }
}
