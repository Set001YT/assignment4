package graph;

import java.util.*;

/**
 * Basic graph class for directed graphs with weighted edges
 */
public class Graph {
    private int n; // number of vertices
    private List<List<Edge>> adj; // adjacency list
    private boolean directed;

    /**
     * Edge class to store destination and weight
     */
    public static class Edge {
        public int to;
        public int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    /**
     * Create a graph with n vertices
     */
    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    /**
     * Add an edge from u to v with weight w
     */
    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
    }

    /**
     * Get all neighbors of vertex v
     */
    public List<Edge> getNeighbors(int v) {
        return adj.get(v);
    }

    /**
     * Get number of vertices
     */
    public int getN() {
        return n;
    }

    /**
     * Check if graph is directed
     */
    public boolean isDirected() {
        return directed;
    }
}
