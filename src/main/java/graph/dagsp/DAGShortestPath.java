package graph.dagsp;

import graph.Graph;
import graph.Metrics;
import graph.topo.TopologicalSort;
import java.util.*;

/**
 * Shortest and longest paths in a DAG
 */
public class DAGShortestPath {
    private static final int INF = Integer.MAX_VALUE / 2;

    /**
     * Find the shortest paths from source in a DAG
     */
    public int[] shortestPaths(Graph g, int source, Metrics m) {
        int n = g.getN();
        int[] dist = new int[n];
        int[] parent = new int[n];

        // Initialize distances
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        m.start();

        // Get topological order
        TopologicalSort topo = new TopologicalSort();
        Metrics topoMetrics = new Metrics();
        List<Integer> order = topo.sort(g, topoMetrics);

        if (order == null) {
            m.stop();
            return null; // Graph has cycle
        }

        // Relax edges in topological order
        for (int u : order) {
            if (dist[u] != INF) {
                for (Graph.Edge edge : g.getNeighbors(u)) {
                    int v = edge.to;
                    int w = edge.weight;

                    if (dist[u] + w < dist[v]) {
                        dist[v] = dist[u] + w;
                        parent[v] = u;
                        m.incrementRelaxations();
                    }
                }
            }
        }

        m.stop();

        return dist;
    }

    /**
     * Find the longest path in a DAG (critical path)
     * Returns the longest path starting from any source vertex (vertices with in-degree 0)
     */
    public PathResult longestPath(Graph g, Metrics m) {
        int n = g.getN();
        int[] dist = new int[n];
        int[] parent = new int[n];

        // Initialize distances to negative infinity
        Arrays.fill(dist, Integer.MIN_VALUE / 2);
        Arrays.fill(parent, -1);

        m.start();

        // Get topological order
        TopologicalSort topo = new TopologicalSort();
        Metrics topoMetrics = new Metrics();
        List<Integer> order = topo.sort(g, topoMetrics);

        if (order == null) {
            m.stop();
            return null;
        }

        // Calculate in-degree for each vertex
        int[] inDegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : g.getNeighbors(u)) {
                inDegree[edge.to]++;
            }
        }

        // Initialize source vertices (in-degree == 0) to 0
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        // Relax edges for longest path in topological order
        for (int u : order) {
            if (dist[u] != Integer.MIN_VALUE / 2) {
                for (Graph.Edge edge : g.getNeighbors(u)) {
                    int v = edge.to;
                    int w = edge.weight;

                    if (dist[u] + w > dist[v]) {
                        dist[v] = dist[u] + w;
                        parent[v] = u;
                        m.incrementRelaxations();
                    }
                }
            }
        }

        m.stop();

        // Find maximum distance and its endpoint
        int maxDist = Integer.MIN_VALUE;
        int endVertex = -1;
        for (int i = 0; i < n; i++) {
            if (dist[i] != Integer.MIN_VALUE / 2 && dist[i] > maxDist) {
                maxDist = dist[i];
                endVertex = i;
            }
        }

        // Handle case where no path exists
        if (endVertex == -1) {
            return new PathResult(new ArrayList<>(), 0);
        }

        // Reconstruct path
        List<Integer> path = reconstructPath(parent, endVertex);

        return new PathResult(path, maxDist);
    }

    /**
     * Reconstruct path from parent array
     */
    private List<Integer> reconstructPath(int[] parent, int end) {
        List<Integer> path = new ArrayList<>();
        int current = end;

        while (current != -1) {
            path.add(current);
            current = parent[current];
        }

        Collections.reverse(path);
        return path;
    }

    /**
     * Reconstruct the shortest path from source to target
     */
    public List<Integer> reconstructShortestPath(int[] parent, int target) {
        return reconstructPath(parent, target);
    }

    /**
     * Path result class
     */
    public static class PathResult {
        public List<Integer> path;
        public int length;

        public PathResult(List<Integer> path, int length) {
            this.path = path;
            this.length = length;
        }
    }
}
